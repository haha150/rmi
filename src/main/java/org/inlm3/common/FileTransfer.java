package org.inlm3.common;

import org.inlm3.server.exception.PermissionDeniedException;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class FileTransfer {

    private String ip;
    private int port;

    public FileTransfer(String ip, int port) {
        this.port = port;
        this.ip = ip;
    }

    public void receive(String filePath) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> {
                    BufferedReader sin = null;
                    InputStream in = null;
                    OutputStream output = null;
                    PrintWriter sout = null;
                    try {
                        int bytesRead;
                        sin = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String fileName = sin.readLine();
                        sout = new PrintWriter(clientSocket.getOutputStream(), true);
                        sout.println("OK");
                        in = clientSocket.getInputStream();
                        output = new FileOutputStream(filePath + "\\" + fileName);
                        byte[] buffer = new byte[1024];
                        while ((bytesRead = in.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    } finally {
                        try {
                            output.close();
                            sin.close();
                            in.close();
                        } catch (IOException e) {

                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String filePath) throws PermissionDeniedException {
        try {
            Socket socket = new Socket(ip, port);
            File myFile = new File(filePath);
            PrintWriter sout = new PrintWriter(socket.getOutputStream(), true);
            sout.println(myFile.getName());
            BufferedReader sin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String status = sin.readLine();
            if (!status.equals("OK")) {
                throw new PermissionDeniedException();
            }
            byte[] mybytearray = new byte[(int)myFile.length()];
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(mybytearray, 0, mybytearray.length);

            OutputStream os = socket.getOutputStream();

            os.write(mybytearray, 0, mybytearray.length);

            os.flush();

            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
