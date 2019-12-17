package com.example.test.task.filefinder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class FileFinder {
    private Queue<FileFinderTask> tasks = new ArrayDeque<FileFinderTask>();
    private List<File> results = new ArrayList<File>();

    public List<File> find(File root, int depth, String mask) {
        Objects.requireNonNull(root);
        if(!root.isDirectory()){
            return null;
        }

        FileFinderTask task = new FileFinderTask(root, depth, mask);
        tasks.add(task);

        return doFind();
    }

    private List<File> doFind(){
        while(tasks.peek() != null){
            FileFinderTask task = tasks.poll();
            try {
                Files.newDirectoryStream(task
                        .getRoot()
                        .toPath())
                        .forEach(x -> checkFile(x.toFile(),task));
            } catch (IOException e){
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
        return results;
    }

    private void checkFile(File file, FileFinderTask task){
        if(file.isDirectory() && task.getDepth() > 0){
            tasks.add(task.goDeep(file));
        }

        if(task.match(file)){
            results.add(file);
        }
    }


}
