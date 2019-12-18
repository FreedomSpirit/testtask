package com.example.test.task.filefinder;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class FileFinderIntegrationTest {

    @Test
    public void testFind() {
        int depth = 1;
        String mask = "aa";
        File rootFile = new File(this.getClass().getClassLoader().getResource("test-folder").getFile());

        ConcurrentLinkedQueue<File> results = new ConcurrentLinkedQueue<File>();
        FileFinderForTest finder = new FileFinderForTest();

        Waiter waiter = new Waiter();
        Thread waiterThread = new Thread(waiter);
        waiterThread.start();
        finder.find(rootFile, depth, mask, results, waiter);

        try {
            waiterThread.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        assertEquals(5, results.size());
        results.forEach(x -> assertTrue(x.getName().contains("aa")));
        assertTrue(results.stream().noneMatch(x -> x.getName().contains("aaa2")));
    }

    @Test
    public void testFindDeeper() {
        int depth = 5;
        String mask = "bb";
        File rootFile = new File(this.getClass().getClassLoader().getResource("test-folder").getFile());

        ConcurrentLinkedQueue<File> results = new ConcurrentLinkedQueue<File>();
        FileFinderForTest finder = new FileFinderForTest();

        Waiter waiter = new Waiter();
        Thread waiterThread = new Thread(waiter);
        waiterThread.start();
        finder.find(rootFile, depth, mask, results, waiter);

        try {
            waiterThread.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        assertEquals(6, results.size());
        results.forEach(x -> assertTrue(x.getName().contains("bb")));
        assertTrue(results.stream().filter(x -> x.getName().contains("bbb2")).count() != 0);
    }
}



class FileFinderForTest extends FileFinder{
    public Thread getWorker(){
        return super.worker;
    }
}

class Waiter implements Runnable, SimpleAction{
    AtomicBoolean finished;

    public Waiter() {
        finished = new AtomicBoolean();
        finished.set(false);
    }

    @Override
    public void run() {
        while(!finished.get()){
            Thread.yield();
        }
    }

    @Override
    public void doAction() {
        finished.set(true);
    }
}