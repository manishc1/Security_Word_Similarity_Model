package demo.pos;
import com.aliasi.classify.ClassifierEvaluator;
import com.aliasi.classify.Classification;

import com.aliasi.corpus.TagHandler;

import com.aliasi.hmm.HiddenMarkovModel;
import com.aliasi.hmm.HmmCharLmEstimator;
import com.aliasi.hmm.HmmDecoder;

import com.aliasi.tag.Tagging;

import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.ObjectToCounterMap;

import java.io.File;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;


public class TrainFrenchTreebank {


    public static void main(String[] args) throws Exception {
        File dataDir = new File(args[0]);
        File modelFile = new File(args[1]);

        int nGram = 8;
        int numChars = 256;
        double interpolationRatio = 8.0;

        System.out.println("\n\nREADING CORPUS");
        TagCorpus corpus
            = new TagCorpus(new FrenchTreebankTagParser(),
                            dataDir);

        System.out.println("\n\nTOTAL SENTENCE COUNT=" + corpus.numInstances());

        /*
        System.out.println("\n\nTOTAL TOKEN COUNT=" + corpus.totalTokenCount());

        System.out.println("\n\nTAGS");
        System.out.println(corpus.tagCounter());


        System.out.println("\n\nCHARACTERS");
        ObjectToCounterMap<Character> charCounter
            = corpus.charCounter();
        for (Character c : charCounter.keySet()) {
            int count = charCounter.getCount(c);
            System.out.println(c
                               + "\t" + Integer.toHexString(c));
        }

        System.out.println("\n\nDICTIONARY");
        Map<String,ObjectToCounterMap<String>> tokenToTagCounter
            = corpus.tokenToTagCounter();
        TreeSet<String> tokensAlpha = new TreeSet<String>(tokenToTagCounter.keySet());
        for (String token : tokensAlpha) {
            ObjectToCounterMap<String> tagCounter = tokenToTagCounter.get(token);
            System.out.println(token);
            System.out.println("     " + tagCounter.toString().replaceAll("\\n","\n     "));
        }
        */

        System.out.println("\n\nCROSS-VALIDATING");
        int numFolds = 10;
        int maxNBest = 32;
        String[] tags = corpus.nonNullTags(); // does not include null
        System.out.println("NUMBER OF FOLDS=" + numFolds);
        for (int fold = 0; fold < numFolds; ++fold) {
            System.out.println("\nFOLD=" + fold);
            corpus.setFold(fold,numFolds);

            System.out.println("     training");
            HmmCharLmEstimator estimator
                = new HmmCharLmEstimator(nGram,numChars,interpolationRatio);
            corpus.visitTrain(estimator);

            System.out.println("     compiling");
            HiddenMarkovModel hmm = (HiddenMarkovModel) AbstractExternalizable.compile(estimator);

            System.out.println("     testing");
            Evaluator evaluator = new Evaluator(hmm,tags);
            corpus.visitTest(evaluator);
            System.out.println(evaluator);
        }


        System.out.println("\n\nTRAINING");
        HmmCharLmEstimator estimator
            = new HmmCharLmEstimator(nGram,numChars,interpolationRatio);
        corpus.visitCorpus(estimator);

        System.out.println("\n\nDONE");

    }

    static class Evaluator implements TagHandler {
        private final HiddenMarkovModel mHmm;
        private final HmmDecoder mDecoder;
        private final ClassifierEvaluator<CharSequence,Classification> mEvaluator;
        private int mNumCases = 0;
        private int mNullTokenCases = 0;

        Evaluator(HiddenMarkovModel hmm, String[] tags) {
            mHmm = hmm;
            mDecoder = new HmmDecoder(hmm);
            mEvaluator = new ClassifierEvaluator<CharSequence,Classification>(null,tags);
        }
        public void handle(String[] tokens, String[] whitespaces, String[] referenceTags) {
            for (int i = 0; i < tokens.length; ++i) {
                if (tokens[i] == null) {
                    ++mNullTokenCases;
                    return;
                }
            }
            ++mNumCases;
            List<String> tokenList = Arrays.asList(tokens);
            Tagging<String> tagging = mDecoder.tag(tokenList);

            for (int i = 0; i < tokens.length; ++i) {
                if (referenceTags[i] == null)
                    continue;
                mEvaluator.addClassification(referenceTags[i],
                                             new Classification(tagging.tag(i)));
            }

        }
        public String toString() {
            return "NUM CASES=" + mNumCases
                + " NUM NULL TOKEN CASES=" + mNullTokenCases
                + " ACCURACY="
                + mEvaluator.confusionMatrix().totalAccuracy()
                + "+/-" + mEvaluator.confusionMatrix().confidence99();
        }
    }


}