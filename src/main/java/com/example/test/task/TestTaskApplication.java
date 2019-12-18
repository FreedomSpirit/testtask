package com.example.test.task;

import com.example.test.task.telnet.TelnetServer;
import java.io.*;

public class TestTaskApplication {
    public static void main (String args []) {
        int port = 23; //just to remember default telnet port
        File rootFile;

        if (args.length != 2) {
            System.err.println("Must be 2 arguments: rootPath port");
            System.exit(1);
        }

        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Argument port (" + args[1] + ") must be an integer.");
            System.exit(1);
        }
        rootFile = new File(args[0]);

        Thread server = new Thread(new TelnetServer(rootFile, port));
        server.start();
        try {
            server.join();
        } catch (InterruptedException e) {
            System.out.println(e);
        }


    }
}
