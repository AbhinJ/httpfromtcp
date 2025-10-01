package com.jain.abhinav.listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;

public class UdpSender {
    private static final String HOST = "localhost";
    private static final int PORT = 42069;
    public static void udpSender() {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(HOST, PORT);
            socket.connect(inetSocketAddress);
            System.out.println("UDP Sender is ready. Sending to " + HOST + ":" + PORT);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while (true) {
                System.out.println("> ");
                line = bufferedReader.readLine();
                if(line == null) break;
                byte[] data = line.getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet = new DatagramPacket(data, data.length);

                socket.send(packet);
            }
        } catch (SocketException e) {
            System.err.println("Cannot open udp socket message: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Cannot read the message: " + e.getMessage());
        }
    }
}
