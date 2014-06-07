package com.aliasi.test.unit.corpus;

import com.aliasi.corpus.ObjectHandler;
import com.aliasi.corpus.XValidatingObjectCorpus;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;


import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class XValidatingObjectCorpusTest {

    @Test(expected=IllegalArgumentException.class)
    public void testConsEx() {
        new XValidatingObjectCorpus<String>(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetNumFoldsExc() {
        XValidatingObjectCorpus<String> corpus
            = new XValidatingObjectCorpus(12);
        corpus.setNumFolds(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetFoldExc1() {
        XValidatingObjectCorpus<String> corpus
            = new XValidatingObjectCorpus(12);
        corpus.setFold(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetFoldExc2() {
        XValidatingObjectCorpus<String> corpus
            = new XValidatingObjectCorpus(12);
        corpus.setFold(12);
    }

    @Test
    public void testPermute() {
        XValidatingObjectCorpus<String> corpus
            = new XValidatingObjectCorpus<String>(3);
        corpus.handle("a");
        corpus.handle("b");
        corpus.handle("c");
        
        Random random = new Random();
        corpus.permuteCorpus(random);
        Collector collector = new Collector();
        corpus.visitCorpus(collector);
        assertEquals(new HashSet<String>(Arrays.asList("a","b","c")),
                     new HashSet<String>(collector.mItems));
    }

    @Test
    public void testAllZeroFolds() {
        XValidatingObjectCorpus<String> corpus
            = new XValidatingObjectCorpus<String>(0);
        corpus.handle("a");
        corpus.handle("b");
        corpus.handle("c");
        
        Random random = new Random();
        corpus.permuteCorpus(random);
        Collector collector = new Collector();
        corpus.visitTrain(collector);
        assertEquals(new HashSet<String>(Arrays.asList("a","b","c")),
                     new HashSet<String>(collector.mItems));
        Collector collector2 = new Collector();
        corpus.visitTest(collector2);
        assertEquals(0,collector2.mItems.size());
    }

    @Test
    public void testFolds() {
        XValidatingObjectCorpus<String> corpus
            = new XValidatingObjectCorpus<String>(3);
        corpus.handle("a");
        corpus.handle("b");
        corpus.handle("c");
        corpus.handle("d");
        corpus.handle("e");
        corpus.handle("f");

        assertTrainTest(corpus,0,
                        Arrays.asList("c","d","e","f"),
                        Arrays.asList("a","b"));

        assertTrainTest(corpus,1,
                        Arrays.asList("a","b","e","f"),
                        Arrays.asList("c","d"));
        
        assertTrainTest(corpus,2,
                        Arrays.asList("a","b","c","d"),
                        Arrays.asList("e","f"));
    }

    void assertTrainTest(XValidatingObjectCorpus<String> corpus,
                         int fold,
                         List<String> trainCases,
                         List<String> testCases) {
        corpus.setFold(fold);

        Collector collector = new Collector();
        corpus.visitTrain(collector);
        assertEquals(trainCases,collector.mItems);

        Collector collector2 = new Collector();
        corpus.visitTest(collector2);
        assertEquals(testCases,collector2.mItems);
    }

    static class Collector implements ObjectHandler<String> {
        List<String> mItems = new ArrayList<String>();
        public void handle(String s) {
            mItems.add(s);
        }
    }

}


