package com.example.test.task.filefinder;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileFinderIntegrationTest {

    @Test
    public void testFind() {
        int depth = 1;
        String mask = "aa";
        File rootFile = new File(this.getClass().getClassLoader().getResource("test-folder").getFile());

        FileFinder finder = new FileFinder();
        List<File> results = finder.find(rootFile, depth, mask);

        assertTrue(results.size() == 5);
        results.stream().forEach(x -> assertTrue(x.getName().contains("aa")));
        assertTrue(results.stream().filter(x -> x.getName().contains("aaa2")).count() == 0);
    }

    @Test
    public void testFindDeeper() {
        int depth = 5;
        String mask = "bb";
        File rootFile = new File(this.getClass().getClassLoader().getResource("test-folder").getFile());

        FileFinder finder = new FileFinder();
        List<File> results = finder.find(rootFile, depth, mask);

        assertTrue(results.size() == 6);
        results.stream().forEach(x -> assertTrue(x.getName().contains("bb")));
        assertTrue(results.stream().filter(x -> x.getName().contains("bbb2")).count() != 0);
    }
}