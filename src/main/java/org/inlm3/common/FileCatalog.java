package org.inlm3.common;

import org.inlm3.server.exception.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FileCatalog extends Remote {

    public void register(String username, String password) throws RemoteException, UserAlreadyExistsException;

    public void unregister(String username, String password) throws RemoteException, UserDoesNotExistException, WrongCredentialsException;

    public UserDTO login(String username, String password) throws RemoteException, UserDoesNotExistException, WrongCredentialsException;

    public void logout(String username) throws RemoteException;

    public List<? extends FileDTO> listFiles(String username) throws RemoteException;

    public void notifyMe(String username, String fileName) throws RemoteException;

    public boolean upload(String username, String name, int size, String permission) throws RemoteException, FileAlreadyExistsException;

    public void download(String username, String name) throws RemoteException, PermissionDeniedException;

    public void editFile(String username, String oldName, String newName, String permission) throws RemoteException, PermissionDeniedException;

    public void deleteFile(String username, String name) throws RemoteException, FileDoesNotExistException, PermissionDeniedException;

    public List<? extends NotificationDTO> pollNotifications(String username) throws RemoteException;
}
