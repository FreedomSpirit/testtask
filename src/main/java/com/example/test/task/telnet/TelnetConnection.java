package com.example.test.task.telnet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TelnetConnection implements Runnable{
    private Socket socket;
    private TelnetServer server;
    private BufferedReader in;
    private PrintStream out;
    ConcurrentLinkedQueue<File> results = new ConcurrentLinkedQueue<File>();
    AtomicBoolean inProgress = new AtomicBoolean();

    public TelnetConnection(Socket socket, TelnetServer server){
        this.socket = socket;
        this.server = server;
        inProgress.set(false);
    }

    public void run(){
        System.out.println("New connection");
        try {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintStream(socket.getOutputStream());
                socket.setSoTimeout(300000);

                //TODO: First 4 read lines returns garbage symbols when connect from putty. Need to find a way to fix it.

                while(socket.isConnected()) {
                    try {
                        out.println("Enter depth: ");
                        String depthString = in.readLine();
                        int depth = Integer.parseInt(depthString);

                        out.println("Enter mask: ");
                        String mask = in.readLine();

                        inProgress.set(true);
                        server.find(depth, mask, results, () -> inProgress.set(false));


                        while(inProgress.get() || results.peek() != null){
                            File result = results.poll();
                            if(result != null){
                                out.println(result);
                            } else {
                                Thread.yield();
                            }
                        }

                    } catch (SocketException | NumberFormatException e) {
                        out.println(e);
                        //TODO: add logger
                        e.printStackTrace();
                        continue;
                    } catch (SocketTimeoutException e) {
                        out.println("Connection closed: " + e);
                        break;
                    }
                }
            } finally {
                socket.close();
                in.close();
                out.close();
                System.out.println("Connection closed");
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
