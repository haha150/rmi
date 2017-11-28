package org.inlm3.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FileCatalog extends Remote {

    public void register(String username, String password) throws RemoteException;

    public void unregister(String username) throws RemoteException;

    public void login(String username, String password) throws RemoteException;

    public void logout(String username) throws RemoteException;

    public List<? extends FileDTO> listFiles() throws RemoteException;
}
