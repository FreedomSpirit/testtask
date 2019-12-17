package com.example.test.task;

import com.example.test.task.filefinder.FileFinder;
import com.example.test.task.filefinder.ResultsPrinter;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestTaskApplication {
    public static void main (String args []) {
        int depth = 0;
        String mask;
        File rootFile;

        if (args.length != 3) {
            System.err.println("Must be 3 arguments: rootPath depth mask");
            System.exit(1);
        }

        try {
            depth = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Argument depth (" + args[1] + ") must be an integer.");
            System.exit(1);
        }
        rootFile = new File(args[0]);
        mask = args[2];

        ConcurrentLinkedQueue<File> results = new ConcurrentLinkedQueue<File>();
        FileFinder finder = new FileFinder();
        finder.find(rootFile, depth, mask, results);

        Thread printer = new Thread(new ResultsPrinter(results, System.out::println));
        printer.start();
        try {
            printer.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
