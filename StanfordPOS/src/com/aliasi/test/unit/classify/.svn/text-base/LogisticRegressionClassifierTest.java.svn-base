package com.aliasi.test.unit.classify;

import com.aliasi.classify.Classification;
import com.aliasi.classify.LogisticRegressionClassifier;
import com.aliasi.classify.XValidatingClassificationCorpus;

import com.aliasi.stats.AnnealingSchedule;
import com.aliasi.stats.RegressionPrior;

import com.aliasi.tokenizer.TokenFeatureExtractor;
import com.aliasi.tokenizer.RegExTokenizerFactory;

import com.aliasi.util.FeatureExtractor;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;


import java.io.IOException;

import java.util.Random;

public class LogisticRegressionClassifierTest  {

    @Test
    public void test1() throws IOException {

        Random random = new Random();

        XValidatingClassificationCorpus<CharSequence> corpus
            = new XValidatingClassificationCorpus<CharSequence>(10);
        // four categories
        for (int j = 0; j < 4; ++j) {
            Classification c = new Classification("cat_" + ((char)('a' + j)));
            // 100 instances each
            for (int i = 0; i < 100; ++i) {
                StringBuilder sb = generateExample(j);
                corpus.handle(sb,c);
            }
        }

        corpus.permuteCorpus(random);

        /* looks OK
        corpus.visitCorpus(new ClassificationHandler<CharSequence,Classification>() {
                               public void handle(CharSequence cs, Classification c) {
                                   System.out.println(c + "|" + cs);
                               }
                           });
        */
        

        FeatureExtractor<CharSequence> featureExtractor
            = new  TokenFeatureExtractor(new RegExTokenizerFactory("\\S+"));
        
        boolean addIntercept = true;
        RegressionPrior prior = RegressionPrior.noninformative();
        double initLearningRate = 0.01;
        double annealingRate = 500;
        double minImprovement = 0.001;
        int minEpochs = 2;
        int maxEpochs = 10000;
        int minFeatureCount = 2;

        LogisticRegressionClassifier<CharSequence> classifier
            = LogisticRegressionClassifier.train(featureExtractor,
                                                 corpus,
                                                 minFeatureCount,
                                                 addIntercept,
                                                 prior,
                                                 AnnealingSchedule.inverse(initLearningRate,annealingRate),
                                                 minImprovement,
                                                 minEpochs,maxEpochs,
                                                 null ); // no writer feedback for test


        for (int j = 0; j < 4; ++j) {
            Classification c = new Classification("cat_" + ((char)('a' + j)));
            // 100 instances each
            for (int i = 0; i < 10; ++i) {
                StringBuilder sb = generateExample(j);
                assertEquals(c.bestCategory(),
                             classifier.classify(sb).bestCategory());
            }
        }

    }

    static StringBuilder generateExample(int j) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < 100; ++k) {
            if (k > 0) sb.append(' ');
            if (random.nextBoolean())
                sb.append(((char)('a' + j)));
            else
                sb.append(((char)('a' + random.nextInt(10))));
        }
        return sb;
    }


}
