package com.example.test.task.telnet;

import com.example.test.task.filefinder.FileFinder;
import com.example.test.task.filefinder.FileFinderTask;
import com.example.test.task.filefinder.SimpleAction;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TelnetServer implements Runnable{
    private Socket clientSocket;
    private ServerSocket server;
    private BufferedReader in;
    private PrintStream out;
    private File root;
    private FileFinder fileFinder;

    public TelnetServer(File root, int port){
        this.root = root;
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e);
        }

        fileFinder = new FileFinder();
    }

    public void run(){
        System.out.println("Server started");
        try {
            try {
                while(true) {
                    clientSocket = server.accept();
                    Thread connection = new Thread(new TelnetConnection(clientSocket, this));
                    connection.start();
                }
            } finally {
                server.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void find(int depth, String mask, ConcurrentLinkedQueue<File> results, SimpleAction finishAction){
        fileFinder.find(root, depth, mask, results, finishAction);
    }
}
