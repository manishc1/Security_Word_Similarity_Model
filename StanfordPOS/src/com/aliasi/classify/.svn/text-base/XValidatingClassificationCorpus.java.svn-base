/*
 * LingPipe v. 3.9
 * Copyright (C) 2003-2010 Alias-i
 *
 * This program is licensed under the Alias-i Royalty Free License
 * Version 1 WITHOUT ANY WARRANTY, without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Alias-i
 * Royalty Free License Version 1 for more details.
 * 
 * You should have received a copy of the Alias-i Royalty Free License
 * Version 1 along with this program; if not, visit
 * http://alias-i.com/lingpipe/licenses/lingpipe-license-1.txt or contact
 * Alias-i, Inc. at 181 North 11th Street, Suite 401, Brooklyn, NY 11211,
 * +1 (718) 290-9170.
 */

package com.aliasi.classify;

import com.aliasi.corpus.ClassificationHandler;
import com.aliasi.corpus.Corpus;
import com.aliasi.corpus.Parser;

import com.aliasi.stats.Statistics;

import com.aliasi.util.Strings;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * A <code>XValidatingClassificationCorpus</code> holds a set of
 * inputs and classification results to be used as a corpus with
 * built-in cross-validation support.  Instances may be added in the
 * constructors, or through the implementation of a classification
 * handler.
 *
 * <h4>Handler Implementation</h4>
 *
 * When used as a handler, this class simply collects the examples
 * and stores them in internal arrays.  This allows an instance of
 * this class to be used like any other classification handler,
 * the result of which is simply a collection of instances with
 * their classification results.
 *
 * <h4>Cross Validation</h4>
 *
 * Cross-validation divides a corpus up into roughly equal sized
 * parts, called folds, assigning one of the parts as the test section and
 * the other parts as training sections.  A typical number of
 * folds is 10, with 90% of the data being used for training and
 * 10% for testing.
 *
 * <p>Initially, the fold will be set to 0, which takes the initial
 * prefix of the data for testing and the rest for training.  The
 * fold may be reset using {@link #setFold(int)}.  This will reset the
 * fold to be the specified value.  In this way, by iterating from
 * 0 to <code>numFolds()-1</code>, a full cross-validation may be
 * performed.  
 *
 * <p>The randomization method {@link #permuteCorpus(Random)} takes
 * a corpus and permutes its instances.  This may be used to make
 * each fold random.
 *
 * <h4>Use Without Cross Validation</h4>
 *
 * No matter how the folds are set, using {@link #visitCorpus(Handler)}
 * will run the specified handler over all of the data collected in
 * this corpus.
 * 
 * <h4>Concurrency</h4>
 *
 * <p>This class must be used with external read/write synchronization.
 * The write operations include the constructor, set-fold, permute
 * corpus and handle methods.  The read operations include the visit
 * num instances and fold reporting methods.
 *
 * @author  Bob Carpenter
 * @version 3.6
 * @since   LingPipe3.5
 * @param <E> the type of objects being classified
 */
public class XValidatingClassificationCorpus<E>
    extends Corpus<ClassificationHandler<E,Classification>>
    implements ClassificationHandler<E,Classification> {

    private final List<E> mInputList = new ArrayList<E>();
    private final List<Classification> mClassificationList = new ArrayList<Classification>();

    private int mNumFolds;
    private int mFold = 0;


    /**
     * Construct a deep copy of the specified corpus.  The deep copy
     * may be permuted or even added to with the handle method
     * independently of the corpus from which it was copied.  
     *
     * <p>The main use for this method is for cross-validation, where
     * several copies of the same corpus may be used in parallel.
     * Typically, a single corpus is permuted once and then copied
     * with the copies being set to handle different folds
     * concurrently.
     * 
     * <p>The cost of the copy is the pair of parallel lists to hold
     * the inputs and classifications.  The inputs and classifications
     * are not themselves deep-copied.
     *
     * @param corpus Corpus to deep copy.
     */
    public XValidatingClassificationCorpus(XValidatingClassificationCorpus<E> corpus) {
        mInputList.addAll(corpus.mInputList);
        mClassificationList.addAll(corpus.mClassificationList);
        mNumFolds = corpus.mNumFolds;
        mFold = corpus.mFold;
    }

    /**
     * Construct a cross-validating corpus containing the instances
     * specified on the parallel arrays of inputs and classifications,
     * and the specified number of folds.
     *
     * <p>The lists are copied and not used after construction.
     *
     * @param inputList List of inputs to classify.
     * @param classificationList List of classification results for inputs.
     * @param numFolds Number of folds for cross-validation.
     * @throws IllegalArgumentException If the number of folds is not
     * greater than zero or if the parallel lists are not of the same
     * length.
     */
    public XValidatingClassificationCorpus(List<E> inputList,
                                           List<Classification> classificationList,
                                           int numFolds) {
        this(numFolds);
        if (inputList.size() != classificationList.size()) {
            String msg = "Input and classification list must be same length."
                + " inputList.size()=" + inputList.size()
                + " classificationList.size()=" + classificationList.size();
            throw new IllegalArgumentException(msg);
        }
        mInputList.addAll(inputList);
        mClassificationList.addAll(classificationList);
    }

    /**
     * Construct a cross-validating corpus containing the instances
     * parsed out of the specified data files using the specified
     * parser using the specified number of folds.
     *
     * @param parser Classification parser for data files.
     * @param dataFiles List of data files to parse.
     * @param numFolds Number of folds.
     * @throws IllegalArgumentException If the number of folds is less
     * than one.
     * @throws IOException If there is an underlying I/O error reading
     * the file or parsing.
     */
    public XValidatingClassificationCorpus(Parser<ClassificationHandler<E,Classification>> parser,
                                           File[] dataFiles,
                                           int numFolds) throws IOException {
        this(numFolds);
        parser.setHandler(this);
        for (File dataFile : dataFiles)
            parser.parse(dataFile);
        mNumFolds = numFolds;
    }

    /**
     * Construct a cross-validating corpus with the specified number
     * of folds that initially contains no examples. 
     *
     * @param numFolds Number of folds for cross-validation.
     * @throws IllegalArgumentException If the number of folds is
     * less than one.
     */
    public XValidatingClassificationCorpus(int numFolds) {
        verifyNumFolds(numFolds);
        mNumFolds = numFolds;
    }

    /**
     * Returns the categories found in the cases for this
     * corpus sorted into ascending order.
     *
     * @return The categories for this corpus.
     */
    public String[] categories() {
        Set<String> catSet = new TreeSet<String>();
        for (Classification c : mClassificationList)
            catSet.add(c.bestCategory());
        return catSet.<String>toArray(Strings.EMPTY_STRING_ARRAY);
    }

    /**
     * Adds the specified object and corresponding classification
     * to the corpus.
     *
     * @param e Object that is classified.
     * @param c Classification for the object.
     */
    public void handle(E e, Classification c) {
        mInputList.add(e);
        mClassificationList.add(c);
    }


    /**
     * Randomly permutes the corpus using the specified randomizer.
     *
     * @param random Randomizer to use for permutation.
     */
    public void permuteCorpus(Random random) {
        int numInstances = mInputList.size();
        int[] permutation = Statistics.permutation(numInstances,random);
        List<E> permInputList = new ArrayList<E>(numInstances);
        List<Classification> permClassificationList = new ArrayList<Classification>(numInstances);
        for (int i = 0; i < numInstances; ++i) {
            permInputList.add(mInputList.get(permutation[i]));
            permClassificationList.add(mClassificationList.get(permutation[i]));
        }
        for (int i = 0; i < numInstances; ++i) {
            mInputList.set(i,permInputList.get(i));
            mClassificationList.set(i,permClassificationList.get(i));
        }
    }

    /**
     * Returns the number of instances for this corpus.
     *
     * @return The number of instances for this corpus.
     */
    public int numInstances() {
        return mInputList.size();
    }

    /**
     * Returns the number of folds for this corpus.
     *
     * @return The number of folds for this corpus.
     */
    public int numFolds() {
        return mNumFolds;
    }

    /**
     * Returns the current fold.
     *
     * @return The current fold.
     */
    public int fold() {
        return mFold;
    }

    /**
     * Set the current fold to the specified value.
     *
     * @param fold New fold value.
     * @throws IllegalArgumentException If the fold is less than zero or
     * greater than or equal to the number of folds.
     */
    public void setFold(int fold) {
        if (fold >= mNumFolds || fold < 0) {
            String msg = "Fold must be between 0 and numFolds=" + mNumFolds
                + " Found fold=" + fold;
            throw new IllegalArgumentException(msg);
        }
        mFold = fold;
    }


    /**
     * Sends all of the test cases in this corpus for the current
     * fold to the specified handler.
     *
     * @param handler Handler to receive the test cases.
     */
    @Override
    public void visitTest(ClassificationHandler<E,Classification> handler) {
        handle(handler,startTestFold(),endTestFold());
    }


    /**
     * Send all of the training cases in this corpus for the current
     * fold to the specified handler.
     *
     * @param handler Handler to receive the training cases.
     */
    @Override
    public void visitTrain(ClassificationHandler<E,Classification> handler) {
        handle(handler,0,startTestFold());
        handle(handler,endTestFold(),numInstances());
    }

    /**
     * Returns a string representation of the size of this corpus.
     *
     * @return A string representation of the size of this corpus.
     */
    @Override
    public String toString() {
        return "XValidatingClassificationCorpus."
            + " numInstances=" + numInstances()
            + " numFolds=" + numFolds()
            + " fold=" + fold();
    }



    private void handle(ClassificationHandler<E,Classification> handler, int start, int end) {
        for (int i = start; i < end; ++i)
            handler.handle(mInputList.get(i),mClassificationList.get(i));
    }

    private int startTestFold() {
        return (int) (numInstances() * (mFold / (double) mNumFolds));
    }

    private int endTestFold() {
        if (mFold == (mNumFolds - 1))
            return numInstances(); // make sure cover the last example
        return (int) (numInstances() * ((mFold + 1.0)/ mNumFolds));
    }


    private static void verifyNumFolds(int numFolds) {
        if (numFolds < 1) {
            String msg = "Require at least one fold."
                + " Found numFolds=" + numFolds;
            throw new IllegalArgumentException(msg);
        }
    }




}
