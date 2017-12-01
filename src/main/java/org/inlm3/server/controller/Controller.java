package org.inlm3.server.controller;

import org.inlm3.common.FileCatalog;
import org.inlm3.common.FileDTO;
import org.inlm3.server.exception.FileAlreadyExistsException;
import org.inlm3.server.exception.UserAlreadyExistsException;
import org.inlm3.server.exception.UserDoesNotExistException;
import org.inlm3.server.exception.WrongCredentialsException;
import org.inlm3.server.integration.FileDAO;
import org.inlm3.server.integration.UserDAO;
import org.inlm3.server.model.File;
import org.inlm3.server.model.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Controller extends UnicastRemoteObject implements FileCatalog {
    
    private final FileDAO fileDAO;
    private final UserDAO userDAO;
    private List<User> users;

    public Controller() throws RemoteException {
        super();
        fileDAO = new FileDAO();
        userDAO = new UserDAO();
        users = new ArrayList<>();
    }

    @Override
    public void register(String username, String password) throws RemoteException, UserAlreadyExistsException {
        try {
            userDAO.registerUser(username,password);
            users.add(userDAO.getUserByName(username));
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
    public void notifyMe(String name) throws RemoteException {

    }

    @Override
    public boolean upload(String username, String name, int size, String permission) throws RemoteException, FileAlreadyExistsException {
        try {
            User user = userDAO.getUserByName(username);
            fileDAO.addFile(user,name,size,permission);
            return true;
        } catch (FileAlreadyExistsException e) {
            throw e;
        }
    }

    @Override
    public void download() throws RemoteException {

    }
}
