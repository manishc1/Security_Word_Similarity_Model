package demo.pos;
import com.aliasi.corpus.Parser;
import com.aliasi.corpus.TagHandler;

import com.aliasi.hmm.HmmCharLmEstimator;
import com.aliasi.hmm.HmmDecoder;

import com.aliasi.tag.TaggerEvaluator;
import com.aliasi.tag.Tagging;

import com.aliasi.util.FastCache;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.xml.sax.InputSource;

public class CrossValidateBeam {

    final int mSentEvalRate;
    final int mToksBeforeEval;
    final int mMaxNBest;
    final int mNGram;
    final int mNumChars;
    final double mLambdaFactor;
    final PosCorpus mCorpus;
    final boolean mSmootheTags;
    final double mBeam;
    final double mEmissionBeam;

    final Set<String> mTagSet = new HashSet<String>();
    HmmCharLmEstimator mEstimator;
    TaggerEvaluator<String> mTaggerEvaluator;
    int mTrainingSentenceCount = 0;
    int mTrainingTokenCount = 0;

    public CrossValidateBeam(String[] args) throws Exception {
        mSentEvalRate = Integer.valueOf(args[0]);
        mToksBeforeEval = Integer.valueOf(args[1]);
        mMaxNBest = Integer.valueOf(args[2]);
        mNGram = Integer.valueOf(args[3]);
        mNumChars = Integer.valueOf(args[4]);
        mLambdaFactor = Double.valueOf(args[5]);
        String constructorName = args[6];
        File corpusFile = new File(args[7]);
        mSmootheTags = Train.parseBoolean(args[8]);
        mBeam = Double.valueOf(args[9]);
        mEmissionBeam = Double.valueOf(args[10]);

        Object[] consArgs = new Object[] { corpusFile };
        @SuppressWarnings("rawtypes") // needed for array, two steps nec.
        PosCorpus corpus 
            = (PosCorpus) 
            Class.forName(constructorName)
            .getConstructor(new Class[] { File.class })
            .newInstance(consArgs);
        mCorpus = corpus;
    }

    void run() throws IOException {
        System.out.println("\nCOMMAND PARAMETERS:");
        System.out.println("  Sent eval rate=" + mSentEvalRate);
        System.out.println("  Toks before eval=" + mToksBeforeEval);
        System.out.println("  Max n-best eval=" + mMaxNBest);
        System.out.println("  Max n-gram=" + mNGram);
        System.out.println("  Num chars=" + mNumChars);
        System.out.println("  Lambda factor=" + mLambdaFactor);
        System.out.println("  Smoothe tags=" + mSmootheTags);
        System.out.println("  Beam=" + mBeam);
        System.out.println("  Emission beam=" + mEmissionBeam);

        CorpusProfileHandler profileHandler = new CorpusProfileHandler();
        parseCorpus(profileHandler);
        String[] tags = mTagSet.toArray(new String[0]);
        Arrays.sort(tags);
        System.out.println("\nCORPUS PROFILE:");
        System.out.println("  Corpus class=" + mCorpus.getClass().getName());
        System.out.println("  #Sentences=" 
                           + mTrainingSentenceCount);
        System.out.println("  #Tokens=" + mTrainingTokenCount);
        System.out.println("  #Tags=" + tags.length);
        System.out.println("  Tags=" + Arrays.asList(tags));
        
        System.out.println("\nEVALUATION:");
        mEstimator
            = new HmmCharLmEstimator(mNGram,mNumChars,mLambdaFactor,
                                     mSmootheTags);
        for (int i = 0; i < tags.length; ++i)
            mEstimator.addState(tags[i]);
        
        HmmDecoder decoder 
            = new  HmmDecoder(mEstimator,
                              new FastCache<String,double[]>(100000), new FastCache<String,double[]>(100000),
                              mBeam, mEmissionBeam);

        boolean storeTokens = true;
        mTaggerEvaluator= new TaggerEvaluator<String>(decoder,storeTokens);
        
        LearningCurveHandler evaluationHandler
            = new LearningCurveHandler();
        parseCorpus(evaluationHandler);
    }

    void parseCorpus(TagHandler handler) throws IOException {
        Parser<TagHandler> parser = mCorpus.parser();
        parser.setHandler(handler);
        Iterator<InputSource> it = mCorpus.sourceIterator();
        while (it.hasNext()) {
            InputSource in = it.next();
            parser.parse(in);
        }
    }

    class CorpusProfileHandler implements TagHandler {
        public void handle(String[] toks, String[] whitespaces, 
                           String[] tags) {
            ++mTrainingSentenceCount;
            mTrainingTokenCount += toks.length;
            for (int i = 0; i < tags.length; ++i)
                mTagSet.add(tags[i]);
        }
    }

    class LearningCurveHandler implements TagHandler {
        Set<String> mKnownTokenSet = new HashSet<String>();
        public void handle(String[] toks, String[] whites, String[] refTags) {
            if (mEstimator.numTrainingTokens() > mToksBeforeEval
                && mEstimator.numTrainingCases() % mSentEvalRate == 0) {
                
                Tagging<String> tagging
                    = new Tagging<String>(Arrays.asList(toks),
                                          Arrays.asList(refTags));
                System.out.println("\nTest Case " 
                                   + mTaggerEvaluator.numCases());
                // System.out.println(mEvaluator.lastCaseToString());
                System.out.println("Cumulative Evaluation");
                System.out.print("    Estimator:  #Train Cases=" 
                                 + mEstimator.numTrainingCases()); 
                System.out.println(" #Train Toks="
                                   + mEstimator.numTrainingTokens() + "]");
                System.out.print("    Evaluation:  ");
                System.out.println(mTaggerEvaluator.tokenEvaluation());
                System.out.print("    Unknown Token Evaluation: ");
                System.out.println(mTaggerEvaluator.unknownTokenEvaluation(mKnownTokenSet));
            }
            // train
            mEstimator.handle(toks,whites,refTags);
            for (int i = 0; i < toks.length; ++i)
                mKnownTokenSet.add(toks[i]);
        }
    }

    public static void main(String[] args) 
        throws Exception {
        
        new CrossValidateBeam(args).run();
    }        



}
