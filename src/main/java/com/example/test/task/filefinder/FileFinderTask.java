package com.example.test.task.filefinder;

import java.io.File;

public class FileFinderTask {
    private int depth;
    private String mask;
    private File root;

    public FileFinderTask(File root, int depth, String mask) {
        this.depth = depth;
        this.mask = mask;
        this.root = root;
    }

    public File getRoot() {
        return root;
    }

    public int getDepth() {
        return depth;
    }

    public boolean match(File file){
        return file.getName().contains(mask);
    }

    public FileFinderTask goDeep(File file){
        return new FileFinderTask(file, depth-1, mask);
    }
}
