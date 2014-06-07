package com.aliasi.test.unit.classify;

import com.aliasi.classify.NaiveBayesClassifier;

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;


public class NaiveBayesClassifierTest  {

    @Test
    public void testOne() {
    NaiveBayesClassifier classifier
        = new NaiveBayesClassifier(new String[] { "a", "b", "c" },
                       new IndoEuropeanTokenizerFactory());
    classifier.train("a","John Smith");
    classifier.train("a","John Smith");
    classifier.train("b","Fred Smith");
    classifier.train("b","Fred Smith");
    classifier.train("c","Fred Jones");
    classifier.train("c","Fred Jones");
    
    assertEquals("a",classifier.classify("John Smith").bestCategory());
    }

}
