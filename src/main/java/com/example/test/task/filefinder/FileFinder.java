package com.example.test.task.filefinder;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Class for looking for files by mask in separate thread
 */
public class FileFinder {
    private Queue<FileFinderTask> tasks = new ConcurrentLinkedQueue<FileFinderTask>();
    protected Thread worker;

    /**
     * Add task to find file
     * @param root - head directory for search
     * @param depth - how deep in subdirectories need to look
     * @param mask - substring that contains in filename
     * @param results - queue for asynchronously storing results
     * @param finishWorkingHandler - action that will be call when all files collected and was added to results
     */
    public void find(File root, int depth, String mask,
                     ConcurrentLinkedQueue<File> results, SimpleAction finishWorkingHandler) {
        Objects.requireNonNull(root);
        if(!root.isDirectory()){
            //TODO: Maybe delete this block and use throws NotDirectoryException from newDirectoryStream
            return;
        }

        AtomicInteger subtaskCounter = new AtomicInteger();
        subtaskCounter.set(0);
        FileFinderTask task = new FileFinderTask(root, depth, mask, results::add, finishWorkingHandler, subtaskCounter);

        synchronized (this) {
            tasks.add(task);
            if(worker == null){
                worker = new Thread(this::doFind);
                worker.start();
            }
        }
    }

    private void doFind(){
        while (true) {
            while (tasks.peek() != null) {
                FileFinderTask task = tasks.poll();
                try {
                    Files.newDirectoryStream(task.getRoot().toPath())
                            .forEach(x -> checkFile(x.toFile(), task));
                } catch (IOException e) {
                    System.err.println(e);
                    System.exit(1);
                } finally {
                    task.finish();
                }
            }
            Thread.currentThread().yield();
        }
    }

    private void checkFile(File file, FileFinderTask task){
        if(file.isDirectory() && task.getDepth() > 0){
            tasks.add(task.goDeep(file));
        }

        task.match(file);
    }


}
