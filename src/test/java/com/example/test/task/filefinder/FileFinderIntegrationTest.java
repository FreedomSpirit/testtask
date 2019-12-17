package com.example.test.task.filefinder;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.*;

class FileFinderIntegrationTest {

    @Test
    public void testFind() {
        int depth = 1;
        String mask = "aa";
        File rootFile = new File(this.getClass().getClassLoader().getResource("test-folder").getFile());

        ConcurrentLinkedQueue<File> results = new ConcurrentLinkedQueue<File>();
        FileFinderForTest finder = new FileFinderForTest();
        finder.find(rootFile, depth, mask, results);
        try {
            finder.getWorker().join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        assertEquals(results.size(), 5);
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
        finder.find(rootFile, depth, mask, results);
        try {
            finder.getWorker().join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        assertEquals(results.size(), 6);
        results.forEach(x -> assertTrue(x.getName().contains("bb")));
        assertTrue(results.stream().filter(x -> x.getName().contains("bbb2")).count() != 0);
    }
}



class FileFinderForTest extends FileFinder{
    public Thread getWorker(){
        return super.worker;
    }
}