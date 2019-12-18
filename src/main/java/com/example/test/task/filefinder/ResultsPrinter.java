package com.example.test.task.filefinder;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * Printing File finder results in separate thread.
 */
public class ResultsPrinter implements Runnable{
    private ConcurrentLinkedQueue<File> results;
    Consumer<String> printMethod;

    /**
     *
     * @param results - queue where FileFinder results will be added
     * @param printMethod - action that will be called when file added to results
     */
    public ResultsPrinter(ConcurrentLinkedQueue<File> results, Consumer<String> printMethod){
        this.results = results;
        this.printMethod = printMethod;
    }

    public void run(){
        while(true){
            File result = results.poll();
            if(result != null){
                printMethod.accept(result.toString());
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }
        }
    }
}
