package com.aliasi.test.unit.classify;

import com.aliasi.classify.BernoulliClassifier;
import com.aliasi.classify.Classification;
import com.aliasi.classify.JointClassification;

import com.aliasi.tokenizer.TokenFeatureExtractor;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;

import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.FeatureExtractor;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;

public class BernoulliClassifierTest  {

    static final FeatureExtractor FEATURE_EXTRACTOR
        = new TokenFeatureExtractor(IndoEuropeanTokenizerFactory.FACTORY);

    @Test
    public void testOne() {
        BernoulliClassifier classifier
            = new BernoulliClassifier(FEATURE_EXTRACTOR);

        classifier.handle("a b",new Classification("cat1"));
        classifier.handle("a",new Classification("cat1"));

        classifier.handle("a b",new Classification("cat2"));
        classifier.handle("b",new Classification("cat2"));

        JointClassification c = classifier.classify("a");

        assertEquals("cat1",c.bestCategory());
        assertEquals(0.75,c.conditionalProbability(0),0.0001);
        assertEquals(0.25,c.conditionalProbability(1),0.0001);


        assertEquals("cat2",classifier.classify("b").bestCategory());
        assertEquals(0.75,c.conditionalProbability(0),0.0001);
        assertEquals(0.25,c.conditionalProbability(1),0.0001);


        assertEquals("cat2",classifier.classify("b b").bestCategory());
        assertEquals(0.75,c.conditionalProbability(0),0.0001);
        assertEquals(0.25,c.conditionalProbability(1),0.0001);

        assertEquals("cat2",classifier.classify("b foo").bestCategory());
        assertEquals(0.75,c.conditionalProbability(0),0.0001);
        assertEquals(0.25,c.conditionalProbability(1),0.0001);

        classifier.handle("d",new Classification("cat1"));

        JointClassification c2 = classifier.classify("d d");
        assertEquals("cat1",c2.bestCategory());

        JointClassification c3 = classifier.classify("e");
        assertEquals("cat1",c3.bestCategory());


    }

    @Test
    public void testSer() throws IOException, ClassNotFoundException {
        BernoulliClassifier classifier
            = new BernoulliClassifier(FEATURE_EXTRACTOR);

        classifier.handle("a b",new Classification("cat1"));
        classifier.handle("a",new Classification("cat1"));

        classifier.handle("a b",new Classification("cat2"));
        classifier.handle("b",new Classification("cat2"));

        BernoulliClassifier classifier2
            = (BernoulliClassifier) AbstractExternalizable.serializeDeserialize(classifier);

        JointClassification c = classifier2.classify("a");

        assertEquals("cat1",c.bestCategory());
        assertEquals(0.75,c.conditionalProbability(0),0.0001);
        assertEquals(0.25,c.conditionalProbability(1),0.0001);


        assertEquals("cat2",classifier.classify("b").bestCategory());
        assertEquals(0.75,c.conditionalProbability(0),0.0001);
        assertEquals(0.25,c.conditionalProbability(1),0.0001);


        assertEquals("cat2",classifier.classify("b b").bestCategory());
        assertEquals(0.75,c.conditionalProbability(0),0.0001);
        assertEquals(0.25,c.conditionalProbability(1),0.0001);

        assertEquals("cat2",classifier.classify("b foo").bestCategory());
        assertEquals(0.75,c.conditionalProbability(0),0.0001);
        assertEquals(0.25,c.conditionalProbability(1),0.0001);

    }

}

