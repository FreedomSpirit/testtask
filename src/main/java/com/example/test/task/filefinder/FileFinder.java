package com.example.test.task.filefinder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Class for looking for files by mask in separate thread
 */
public class FileFinder {
    private Queue<FileFinderTask> tasks = new ConcurrentLinkedQueue<FileFinderTask>();
    private ConcurrentLinkedQueue<File> results;
    private Boolean isWorking = false;
    protected Thread worker;

    /**
     * Add task to find file
     * @param root - head directory for search
     * @param depth - how deep in subdirectories need to look
     * @param mask - substring that contains in filename
     * @param results - queue for asynchronously storing results
     */
    public void find(File root, int depth, String mask, ConcurrentLinkedQueue<File> results) {
        Objects.requireNonNull(root);
        if(!root.isDirectory()){
            //TODO: Maybe delete this block and use throws NotDirectoryException from newDirectoryStream
            return;
        }

        FileFinderTask task = new FileFinderTask(root, depth, mask, results::add);
        tasks.add(task);

        if(isWorking){
            return;
        }

        synchronized (isWorking) {
            isWorking = true;
            worker = new Thread(this::doFind);
            worker.start();
        }
    }

    private void doFind(){
        while(tasks.peek() != null){
            FileFinderTask task = tasks.poll();
            try {
                Files.newDirectoryStream(task.getRoot().toPath())
                        .forEach(x -> checkFile(x.toFile(),task));
            } catch (IOException e){
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
        synchronized (isWorking) {
            isWorking = false;
            System.out.println("finished");
        }
    }

    private void checkFile(File file, FileFinderTask task){
        if(file.isDirectory() && task.getDepth() > 0){
            tasks.add(task.goDeep(file));
        }

        task.match(file);
    }


}
