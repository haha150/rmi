package org.inlm3.common;

import org.inlm3.server.exception.FileAlreadyExistsException;
import org.inlm3.server.exception.UserAlreadyExistsException;
import org.inlm3.server.exception.UserDoesNotExistException;
import org.inlm3.server.exception.WrongCredentialsException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FileCatalog extends Remote {

    public void register(String username, String password) throws RemoteException, UserAlreadyExistsException;

    public void unregister(String username, String password) throws RemoteException, UserDoesNotExistException, WrongCredentialsException;

    public UserDTO login(String username, String password) throws RemoteException, UserDoesNotExistException, WrongCredentialsException;

    public void logout(String username) throws RemoteException;

    public List<? extends FileDTO> listFiles(String username) throws RemoteException;

    public void notifyMe(String name) throws RemoteException;

    public boolean upload(String username, String name, int size, String permission) throws RemoteException, FileAlreadyExistsException;

    public void download() throws RemoteException;
}
