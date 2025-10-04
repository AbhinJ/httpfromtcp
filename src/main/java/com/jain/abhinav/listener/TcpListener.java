package com.jain.abhinav.listener;

import java.net.ServerSocket;
import java.net.Socket;
import com.jain.abhinav.http.RequestParser;
import com.jain.abhinav.http.Request;

public class TcpListener {
    public static void tcpListener() {
        try (ServerSocket socket = new ServerSocket(42069) ) {
            while (true) {
                Socket clientSocket = socket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                Request request = RequestParser.requestReader(clientSocket.getInputStream());
                System.out.println(request);

//                BlockingQueue<String> lines = ReadFile.getLinesFromProducer(clientSocket.getInputStream());
//                while (true) {
//                    String line = lines.take();
//                    if (line.equals(ReadFile.POISON_PILL)) break;
//                    System.out.println("read: " + line);
//                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
