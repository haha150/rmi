package org.inlm3.server.controller;

import org.inlm3.common.FileCatalog;
import org.inlm3.common.FileDTO;
import org.inlm3.server.exception.UserAlreadyExistsException;
import org.inlm3.server.integration.FileDAO;
import org.inlm3.server.integration.UserDAO;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class Controller extends UnicastRemoteObject implements FileCatalog {
    
    private final FileDAO fileDAO;
    private final UserDAO userDAO;

    public Controller() throws RemoteException {
        super();
        fileDAO = new FileDAO();
        userDAO = new UserDAO();
    }

    public void register(String username, String password) throws RemoteException {
        try {
            userDAO.registerUser(username,password);
        } catch (UserAlreadyExistsException e) {
            e.printStackTrace();
        }
    }

    public void unregister(String username) throws RemoteException {

    }

    public void login(String username, String password) throws RemoteException {

    }

    public void logout(String username) throws RemoteException {

    }

    public List<? extends FileDTO> listFiles() throws RemoteException {
        return null;
    }
}
