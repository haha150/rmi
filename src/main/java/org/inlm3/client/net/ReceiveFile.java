package org.inlm3.client.net;

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
            Socket clientSocket = serverSocket.accept();
            BufferedReader sin = null;
            InputStream in = null;
            OutputStream output = null;
            PrintWriter sout = null;
            try {
                sin = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String fileName = sin.readLine();
                sout = new PrintWriter(clientSocket.getOutputStream(), true);
                sout.println("OK");
                in = clientSocket.getInputStream();
                output = new FileOutputStream(filePath + "\\" + fileName);
                byte[] buffer = new byte[1024];
                int bytes;
                while ((bytes = in.read(buffer)) != -1) {
                    output.write(buffer, 0, bytes);
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                try {
                    output.close();
                    sin.close();
                    in.close();
                    sout.close();
                } catch (IOException e) {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
