package org.inlm3.server.controller;

import org.inlm3.common.*;
import org.inlm3.server.exception.*;
import org.inlm3.server.integration.FileDAO;
import org.inlm3.server.integration.UserDAO;
import org.inlm3.server.model.File;
import org.inlm3.server.model.Notification;
import org.inlm3.server.model.User;
import org.inlm3.server.start.Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Controller extends UnicastRemoteObject implements FileCatalog {
    
    private final FileDAO fileDAO;
    private final UserDAO userDAO;
    private List<User> users;
    private List<File> files;
    private List<Notification> notifications;

    public Controller() throws RemoteException {
        super();
        fileDAO = new FileDAO();
        userDAO = new UserDAO();
        users = new ArrayList<>();
        files = new ArrayList<>();
        notifications = new ArrayList<>();
    }

    @Override
    public void register(String username, String password) throws RemoteException, UserAlreadyExistsException {
        try {
            userDAO.registerUser(username,password);
        } catch (UserAlreadyExistsException e) {
            throw e;
        }
    }

    @Override
    public void unregister(String username, String password) throws RemoteException, UserDoesNotExistException, WrongCredentialsException {
        userDAO.unegisterUser(username, password);
    }

    @Override
    public User login(String username, String password) throws RemoteException, UserDoesNotExistException, WrongCredentialsException {
        try {
            User u = userDAO.login(username,password);
            users.add(u);
            return u;
        } catch (UserDoesNotExistException e) {
            throw e;
        } catch (WrongCredentialsException e2) {
            throw e2;
        }
    }

    @Override
    public void logout(String username) throws RemoteException {
        users.remove(userDAO.getUserByName(username));
    }

    @Override
    public List<? extends FileDTO> listFiles(String username) throws RemoteException {
        List<File> files = fileDAO.getAllPublicFiles();
        files.addAll(fileDAO.getAllPrivateFiles(username));
        return files;
    }

    @Override
    public void notifyMe(String username, String fileName) throws RemoteException {
        User user = userDAO.getUserByName(username);
        if (user != null) {
            files.add(fileDAO.getFileByName(fileName));
        }
    }

    @Override
    public boolean upload(String username, String name, int size, String permission, boolean read, boolean write) throws RemoteException, FileAlreadyExistsException {
        try {
            User user = userDAO.getUserByName(username);
            fileDAO.addFile(user,name,size,permission,read,write);
            return true;
        } catch (FileAlreadyExistsException e) {
            throw e;
        }
    }

    @Override
    public void download(String username, String name) throws RemoteException, PermissionDeniedException, FileDoesNotExistException {
        File file = fileDAO.getFileByName(name);
        if(file != null) {
            if(file.getFilePermission().equalsIgnoreCase("public") && file.isRead()) {
                new Thread(() -> {
                    FileTransfer fileTransfer = new FileTransfer("localhost", Constants.CLIENT_PORT);
                    try {
                        fileTransfer.send(new java.io.File(Server.FILE_PATH + "\\" + name));
                        synchronized (files) {
                            for (File f : files) {
                                if(f.getFileName().equals(name)) {
                                    synchronized (notifications) {
                                        notifications.add(new Notification("Download " + name, username));
                                    }
                                }
                            }
                        }
                    } catch (PermissionDeniedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                throw new PermissionDeniedException();
            }
        } else {
            throw new FileDoesNotExistException();
        }
    }

    @Override
    public void editFile(String username, String oldName, String newName, String permission, boolean read, boolean write) throws RemoteException, PermissionDeniedException, FileDoesNotExistException, UserDoesNotExistException {
        User user = userDAO.getUserByName(username);
        if (user != null) {
            File retrieved = fileDAO.getFileByName(oldName);
            if(retrieved != null) {
                if(!retrieved.getUsername().equalsIgnoreCase(user.getUsername()) && !retrieved.isWrite()) {
                    throw new PermissionDeniedException();
                }
                fileDAO.editFile(oldName, newName, permission, read, write);
                java.io.File file = new java.io.File(Server.FILE_PATH + oldName);
                if(!oldName.equals(newName)) {
                    file.renameTo(new java.io.File(Server.FILE_PATH + newName));
                }
                synchronized (files) {
                    for (File f : files) {
                        if(f.getFileName().equals(oldName)) {
                            f.setFileName(newName);
                        }
                    }
                    for (File f : files) {
                        if(f.getFileName().equals(newName)) {
                            synchronized (notifications) {
                                notifications.add(new Notification("Edit " + newName, username));
                            }
                        }
                    }
                }
            } else {
                throw new FileDoesNotExistException();
            }
        } else {
            throw new UserDoesNotExistException();
        }
    }

    @Override
    public void deleteFile(String username, String name) throws RemoteException, FileDoesNotExistException, PermissionDeniedException, UserDoesNotExistException {
        User user = userDAO.getUserByName(username);
        if (user != null) {
            File retrieved = fileDAO.getFileByName(name);
            if(retrieved != null) {
                if (retrieved.getFileOwner() != user && !retrieved.isWrite()) {
                    throw new PermissionDeniedException();
                }
                fileDAO.deleteFile(user, name);
                new java.io.File(Server.FILE_PATH + name).delete();
                for (File f : files) {
                    if(f.getFileName().equals(name)) {
                        synchronized (notifications) {
                            notifications.add(new Notification("Delete + " + name, username));
                        }
                    }
                }
            } else {
                throw new FileDoesNotExistException();
            }
        } else {
            throw new UserDoesNotExistException();
        }
    }

    @Override
    public synchronized List<? extends NotificationDTO> pollNotifications(String username) throws RemoteException {
        List<Notification> poll = new ArrayList<>();
        for(Notification n : notifications) {
            if(n.getUser().equals(username)) {
                poll.add(n);
            }
        }
        notifications.removeAll(poll);
        return poll;
    }
}
