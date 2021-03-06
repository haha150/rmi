package org.inlm3.server.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiveFile {

    private int port;

    public ReceiveFile(int port) {
        this.port = port;
    }

    public void receive(String filePath) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> {
                    BufferedReader sin = null;
                    InputStream in = null;
                    OutputStream out = null;
                    PrintWriter sout = null;
                    try {
                        sin = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String fileName = sin.readLine();
                        sout = new PrintWriter(clientSocket.getOutputStream(), true);
                        sout.println("OK");
                        in = clientSocket.getInputStream();
                        out = new FileOutputStream(filePath + "\\" + fileName);
                        byte[] buffer = new byte[1024];
                        int bytes;
                        while ((bytes = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytes);
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    } finally {
                        try {
                            out.close();
                            sin.close();
                            in.close();
                            sout.close();
                        } catch (IOException e) {

                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
