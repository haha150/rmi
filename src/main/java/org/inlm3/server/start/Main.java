package org.inlm3.server.start;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

public class Main {

    public static void main(String[] args) throws RemoteException, MalformedURLException {
        Server s = new Server();
        s.start();
    }
}
