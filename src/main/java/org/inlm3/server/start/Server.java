package org.inlm3.server.start;

import org.inlm3.server.controller.Controller;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Server {

    public Server() {}

    public void start() throws RemoteException, MalformedURLException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException e) {
            LocateRegistry.createRegistry(1099);
        }
        Controller controller = new Controller();
        Naming.rebind("server", controller);
        controller.register("abc","abcd");
    }
}
