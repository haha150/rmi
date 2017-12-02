package org.inlm3.common;

import org.inlm3.server.exception.PermissionDeniedException;

import java.io.*;
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

    public void send(File file) throws PermissionDeniedException {
        PrintWriter sout = null;
        FileInputStream fis = null;
        BufferedReader sin = null;
        BufferedInputStream bis = null;
        try {
            Socket socket = new Socket(ip, port);
            sout = new PrintWriter(socket.getOutputStream(), true);
            sout.println(file.getName());
            sin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String status = sin.readLine();
            if (!status.equals("OK")) {
                throw new PermissionDeniedException();
            }
            byte[] arr = new byte[(int)file.length()];
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            bis.read(arr, 0, arr.length);
            OutputStream os = socket.getOutputStream();
            os.write(arr, 0, arr.length);
            os.flush();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                sout.close();
                sin.close();
                fis.close();
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
