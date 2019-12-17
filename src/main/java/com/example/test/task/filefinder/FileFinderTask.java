package com.example.test.task.filefinder;

import java.io.File;
import java.util.function.Consumer;

public class FileFinderTask {
    private int depth;
    private String mask;
    private File root;
    private Consumer<File> fileHandler;

    public FileFinderTask(File root, int depth, String mask, Consumer<File> fileHandler) {
        this.depth = depth;
        this.mask = mask;
        this.root = root;
        this.fileHandler = fileHandler;
    }

    public File getRoot() {
        return root;
    }

    public int getDepth() {
        return depth;
    }

    public void match(File file){
        if(file.getName().contains(mask)){
            fileHandler.accept(file);
        }
    }

    public FileFinderTask goDeep(File file){
        return new FileFinderTask(file, depth-1, mask, fileHandler);
    }
}
