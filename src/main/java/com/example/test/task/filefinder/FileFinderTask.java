package com.example.test.task.filefinder;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class FileFinderTask {
    private int depth;
    private String mask;
    private File root;
    private Consumer<File> fileHandler;
    SimpleAction finishHandler;

    //Atomic is not required now, but may useful in future, in case subtask creation will be in separate thread
    private AtomicBoolean hasSubtask = new AtomicBoolean();

    public FileFinderTask(File root, int depth, String mask,
                          Consumer<File> fileHandler, SimpleAction finishHandler) {
        this.depth = depth;
        this.mask = mask;
        this.root = root;
        this.fileHandler = fileHandler;
        this.finishHandler = finishHandler;
        hasSubtask.set(false);
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
        hasSubtask.set(true);
        return new FileFinderTask(file, depth-1, mask, fileHandler, finishHandler);
    }

    public void finish(){
        if(!hasSubtask.get()){
            System.out.println("task finished");
            finishHandler.doAction();
        }
    }
}
