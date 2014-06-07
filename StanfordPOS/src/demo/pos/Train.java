package demo.pos;
import com.aliasi.corpus.Parser;
import com.aliasi.corpus.TagHandler;

import com.aliasi.hmm.HmmCharLmEstimator;

import com.aliasi.util.Streams;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.xml.sax.InputSource;

public class Train {

    final int mNGram;
    final int mNumChars;
    final double mLambdaFactor;
    final String mCorpusParserClass;
    final File mCorpusFile;
    final File mModelFile;

    final boolean mSmootheTags;

    public Train(String[] args) {
        mNGram = Integer.valueOf(args[0]);
        mNumChars = Integer.valueOf(args[1]);
        mLambdaFactor = Double.valueOf(args[2]);
        mCorpusParserClass = args[3];
        mCorpusFile = new File(args[4]);
        mModelFile = new File(args[5]);
        mSmootheTags = args.length >= 7 && parseBoolean(args[6]);
        
        System.out.println("n-gram=" + mNGram);
        System.out.println("num chars=" + mNumChars);
        System.out.println("lambda fact=" + mLambdaFactor);
        System.out.println("corpus parser=" + mCorpusParserClass);
        System.out.println("corpus file=" + mCorpusFile);
        System.out.println("model file=" + mModelFile);
    }

    static boolean parseBoolean(String in) {
        if (in == null) return false;
        String lc = in.toLowerCase();
        return "yes".equals(lc) || "true".equals(lc);
    }


    public void run() throws Exception {
        HmmCharLmEstimator estimator
            = new HmmCharLmEstimator(mNGram,mNumChars,mLambdaFactor,
                                     mSmootheTags);

        @SuppressWarnings({"unchecked","rawtypes"}) // OK by construction
        PosCorpus corpus = corpus
            = (PosCorpus) 
            Class
            .forName(mCorpusParserClass)
            .getConstructor(new Class[] { File.class })
            .newInstance(new Object[] { mCorpusFile });

        Parser<TagHandler> parser = corpus.parser();
        parser.setHandler(estimator);

        Iterator<InputSource> it = corpus.sourceIterator();
        while (it.hasNext()) {
            InputSource in = it.next();
            parser.parse(in);
        }

        FileOutputStream fileOut = null;
        BufferedOutputStream bufOut = null;
        ObjectOutputStream objOut = null;
        try {
            fileOut = new FileOutputStream(mModelFile);
            bufOut = new BufferedOutputStream(fileOut);
            objOut = new ObjectOutputStream(bufOut);
            estimator.compileTo(objOut);
        } catch (IOException e) {
            System.out.println("IOException=" + e);
            e.printStackTrace(System.out);
        } finally {
            Streams.closeOutputStream(objOut);
            Streams.closeOutputStream(bufOut);
            Streams.closeOutputStream(fileOut);
        }
    }

    public static void main(String[] args) {
        try {
            new Train(args).run(); 
        } catch (Throwable t) {
            System.out.println("Threw t=" + t);
            t.printStackTrace(System.out);
        }
    }

}
