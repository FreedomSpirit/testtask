package com.example.test.task.filefinder;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class FileFinderTask {
    private int depth;
    private String mask;
    private File root;
    private Consumer<File> fileHandler;
    SimpleAction finishHandler;

    //Atomic is not required now, but may useful in future, in case subtask creation will be in separate thread
    private AtomicInteger subtaskCount;

    public FileFinderTask(File root, int depth, String mask,
                          Consumer<File> fileHandler, SimpleAction finishHandler,
                           AtomicInteger subtaskCounter) {
        this.depth = depth;
        this.mask = mask;
        this.root = root;
        this.fileHandler = fileHandler;
        this.finishHandler = finishHandler;
        this.subtaskCount = subtaskCounter;
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
        subtaskCount.getAndIncrement();
        return new FileFinderTask(file, depth-1, mask, fileHandler, finishHandler, subtaskCount);
    }

    public void finish(){
        if(subtaskCount.get() > 0){
            subtaskCount.getAndDecrement();
        } else {
            finishHandler.doAction();
        }
    }
}
