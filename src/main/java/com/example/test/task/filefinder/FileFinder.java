package com.example.test.task.filefinder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * Class for looking for files by mask in separate thread
 */
public class FileFinder {
    private Queue<FileFinderTask> tasks = new ConcurrentLinkedQueue<FileFinderTask>();
    private Boolean isWorking = false;
    protected Thread worker;

    /**
     * Add task to find file
     * @param root - head directory for search
     * @param depth - how deep in subdirectories need to look
     * @param mask - substring that contains in filename
     * @param results - queue for asynchronously storing results
     */
    public void find(File root, int depth, String mask,
                     ConcurrentLinkedQueue<File> results, SimpleAction finishHandler) {
        Objects.requireNonNull(root);
        if(!root.isDirectory()){
            //TODO: Maybe delete this block and use throws NotDirectoryException from newDirectoryStream
            return;
        }

        FileFinderTask task = new FileFinderTask(root, depth, mask, results::add, finishHandler);

        synchronized (isWorking) {
            tasks.add(task);

            if(isWorking){
                return;
            }
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
                System.err.println(e);
                System.exit(1);
            } finally {
                task.finish();
            }
        }
        synchronized (isWorking) {
            isWorking = false;
        }
    }

    private void checkFile(File file, FileFinderTask task){
        if(file.isDirectory() && task.getDepth() > 0){
            tasks.add(task.goDeep(file));
        }

        task.match(file);
    }


}
