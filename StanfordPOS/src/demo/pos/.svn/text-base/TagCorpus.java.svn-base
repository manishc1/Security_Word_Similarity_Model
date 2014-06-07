package demo.pos;
import com.aliasi.corpus.Corpus;
import com.aliasi.corpus.Parser;
import com.aliasi.corpus.TagHandler;

import com.aliasi.util.ObjectToCounterMap;

import java.io.IOException;
import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Unlike disk corpus, this one stores instances
 * in memory for computing cross-validation and
 * folds.  Could do this with more work on disk corpus
 */
public class TagCorpus extends Corpus<TagHandler> {

    private final String[][] mTags;
    private final String[][] mTokens;
    private final String[][] mWhitespaces;

    private int mFold = 0;
    private int mNumFolds = 1;

    public TagCorpus(Parser<TagHandler> parser, File dir)
        throws IOException {

        TagCorpusHandler handler = new TagCorpusHandler();
        parser.setHandler(handler);
        for (File file : dir.listFiles()) {
            // org.xml.sax.InputSource in
            // = new org.xml.sax.InputSource(com.aliasi.util.Files.fileToURLName(file));
            // in.setEncoding("ISO-8859-1");
            parser.parse(file);
        }


        mTokens = handler.getTokens();
        mWhitespaces = handler.getWhitespaces();
        mTags = handler.getTags();
    }

    public ObjectToCounterMap<Character> charCounter() {
        ObjectToCounterMap<Character> charCounter = new ObjectToCounterMap<Character>();
        for (String[] tokens : mTokens)
            for (String token : tokens)
                if (token != null)
                    for (int i = 0; i < token.length(); ++i)
                        charCounter.increment(Character.valueOf(token.charAt(i)));
        return charCounter;
    }

    public int totalTokenCount() {
        int total = 0;
        for (String[] tokens : mTokens)
            total += tokens.length;
        return total;
    }

    public ObjectToCounterMap<String> tokenCounter() {
        return counter(mTokens);
    }

    public ObjectToCounterMap<String> tagCounter() {
        return counter(mTags);
    }

    public String[] nonNullTags() {
        Set<String> tagSet = new HashSet<String>();
        for (String[] tags : mTags)
            for (String tag : tags)
                if (tag != null)
                    tagSet.add(tag);
        return tagSet.<String>toArray(new String[tagSet.size()]);

    }

    public Map<String,ObjectToCounterMap<String>> tokenToTagCounter() {
        Map<String,ObjectToCounterMap<String>> result
            = new HashMap<String,ObjectToCounterMap<String>>();
        for (int i = 0; i < numInstances(); ++i) {
            String[] tokens = mTokens[i];
            String[] tags = mTags[i];
            for (int j = 0; j < tokens.length; ++j) {
                String token = tokens[j];
                String tag = tags[j];
                if (token == null) token = "***NULL***";
                if (tag == null) tag = "***NULL***";
                ObjectToCounterMap<String> counter
                    = result.get(token);
                if (counter == null) {
                    counter = new ObjectToCounterMap<String>();
                    result.put(token,counter);
                }
                counter.increment(tag);
            }
        }
        return result;
    }

    private ObjectToCounterMap<String> counter(String[][] xss) {
        ObjectToCounterMap<String> result = new ObjectToCounterMap<String>();
        for (String[] tokens : xss)
            for (String token : tokens)
                result.increment(token);
        return result;
    }

    private final ObjectToCounterMap<String> mTagCounter
        = new ObjectToCounterMap<String>();
    private int mTokenCount = 0;



    public int numInstances() {
        return mTags.length;
    }

    public void setFold(int fold, int numFolds) {
        if (fold >= numFolds) {
            String msg = "Fold must be less than numFolds."
                + " Found fold=" + fold
                + " numFolds=" + numFolds;
            throw new IllegalArgumentException(msg);
        }
        if (fold < 0) {
            String msg = "Fold must be non-negative."
                + " Found fold=" + fold;
            throw new IllegalArgumentException(msg);
        }
        mFold = fold;
        mNumFolds = numFolds;
    }


    private int startTestFold() {
        return (int) (numInstances() * (mFold / (double) mNumFolds));
    }
    private int endTestFold() {
        if (mFold == (mNumFolds - 1))
            return numInstances(); // make sure cover the last example
        return (int) (numInstances() * ((mFold + 1.0)/ mNumFolds));
    }
    public void visitTrain(TagHandler handler) {
        handle(handler,0,startTestFold());
        handle(handler,endTestFold(),numInstances());
    }
    public void visitTest(TagHandler handler) {
        handle(handler,startTestFold(),endTestFold());
    }


    void handle(TagHandler handler, int start, int end) {
        for (int i = start; i < end; ++i)
            handler.handle(mTokens[i],mWhitespaces[i],mTags[i]);
    }


    static class TagCorpusHandler implements TagHandler {
        private final List<String[]> mTokenList
            = new ArrayList<String[]>();
        private final List<String[]> mWhitespaceList
            = new ArrayList<String[]>();
        private final List<String[]> mTagList
            = new ArrayList<String[]>();
        public void handle(String[] tokens, String[] whitespaces,
                           String[] tags) {
            mTokenList.add(tokens);
            mWhitespaceList.add(whitespaces);
            mTagList.add(tags);
        }
        public String[][] getTokens() {
            return toArray(mTokenList);
        }
        public String[][] getWhitespaces() {
            return toArray(mWhitespaceList);
        }
        public String[][] getTags() {
            return toArray(mTagList);
        }
        static String[][] toArray(List<String[]> xs) {
            String[][] result = new String[xs.size()][];
            xs.toArray(result);
            return result;
            // why won't this compile?
            // return xs.<String[][]>toArray(new String[xs.size()][]);
        }
    }

}

