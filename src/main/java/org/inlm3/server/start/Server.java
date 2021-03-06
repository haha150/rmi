package org.inlm3.server.start;

import org.inlm3.common.Constants;
import org.inlm3.common.FileTransfer;
import org.inlm3.server.controller.Controller;
import org.inlm3.server.net.ReceiveFile;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Server {

    public static final String FILE_PATH = "C:\\inlm3\\";

    public Server() {}

    public void start() throws RemoteException, MalformedURLException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException e) {
            LocateRegistry.createRegistry(1099);
        }
        Controller controller = new Controller();
        Naming.rebind("server", controller);

        new Thread(() -> {
            ReceiveFile fileTransfer = new ReceiveFile(Constants.SERVER_PORT);
            fileTransfer.receive(FILE_PATH);
        }).start();
    }
}
