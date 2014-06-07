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

package com.aliasi.corpus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * An {@code XValidatingObjectCorpus} holds a list of items
 * which it uses to provide training and testing items using
 * cross-validation.  
 *
 * <h4>Handler Implementation</h4>
 *
 * The method {@link #handle(Object)} is used to add items to the
 * corpus.  The items will be stored in the order in which they
 * are received (though they may be permuted later).
 *
 * <p>When used as a handler, this class simply collects the items and
 * stores them in a list.  This allows an instance of this class to be
 * used like any other object handler.
 *
 * <h4>Cross Validation</h4>
 *
 * Cross-validation divides a corpus up into roughly equal sized
 * parts, called folds, assigning one of the parts as the test section and
 * the other parts as training sections.  A typical number of
 * folds is 10, with 90% of the data being used for training and
 * 10% for testing.  The number of folds is set in the constructor.
 *
 * <p>Initially, the fold will be set to 0, but the fold may be reset
 * later using {@link #setFold(int)}.  Iterating between 0 and the
 * number of folds minus 1 will work through all folds.  The method
 * {@link #size()} returns the size of the corpus and {@link #fold()}
 * is the current fold.
 *
 * <p>For cases where {@code numFolds()} is greater than zero,
 * the start and end of a fold are defined by:
 *
 * <blockquote><pre>
 * start(fold) = (int) (size() * fold() / (double) numFolds())
 * 
 * end(fold) = start(fold+1)</blockquote></pre>
 *
 * If {@code numFolds()} is 0, the start and end for the fold
 * are 0, so that visiting the training part of the corpus visits
 * the entire corpus.  
 *
 *
 * <h3>Permuting the Corpus</h3>
 * 
 * <p>The randomization method {@link #permuteCorpus(Random)} randomizes
 * the list of items.  This can be useful for removing local
 * dependencies.
 *
 * <h4>Use Without Cross Validation</h4>
 *
 * No matter how the folds are set, using {@link
 * #visitCorpus(Handler)} will run the specified handler over all of
 * the data collected in this corpus.  
 *
 * <p>If the number of folds is set to 1, then {@link
 * #visitTest(Handler)} visits the entire corpus.
 *
 * <p>If the number of folds is set to 0, {@link #visitTrain(Handler)}
 * visits the entire corpus.
 * 
 * <h4>Thead Safety</h4>
 *
 * <p>This class must be used with external read/write
 * synchronization.  The write operations include the constructor,
 * set-fold, set number of folds, permute corpus, and handle methods.
 * The read operations include the visit num instances and fold
 * reporting methods.
 *
 * @author Bob Carpenter
 * @version 3.9
 * @since LingPipe3.9
 * @param <E> the type of objects handled.
 */
public class XValidatingObjectCorpus<E> 
    extends Corpus<ObjectHandler<E>> 
    implements ObjectHandler<E> {

    private final List<E> mItemList
        = new ArrayList<E>();
    private int mNumFolds;
    private int mFold;

    /**
     * Construct a cross-validating corpus with the specified
     * number of folds.  The initial fold is set to 0.
     *
     * <p>See the class documentation above for information on
     * how the number of folds is used.
     *
     * @param numFolds Number of folds in the corpus.
     * @throws IllegalArgumentException If the number of folds is
     * negative.
     */
    public XValidatingObjectCorpus(int numFolds) {
        setNumFolds(numFolds);
        mFold = 0;
    }

    /**
     * Return the number of folds for this cross-validating corpus.
     *
     * @return Current number of folds.
     */
    public int numFolds() {
        return mNumFolds;
    }

    /**
     * Sets the number of folds to the specified value.
     *
     * <p>See the class documentation above for information on
     * how the number of folds is used.
     *
     * @param numFolds Number of folds.
     * @throws IllegalArgumentException If the number of folds is
     * negative.
     */
    public void setNumFolds(int numFolds) {
        if (numFolds < 0) {
            String msg = "Number of folds must be non-negative."
                + " Found numFolds=" + numFolds;
            throw new IllegalArgumentException(msg);
        }
        mNumFolds = numFolds;
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
     * Randomly permutes the corpus using the specified randomizer.
     *
     * @param random Randomizer to use for permutation.
     */
    public void permuteCorpus(Random random) {
        Collections.shuffle(mItemList,random);
    }

    /**
     * Set the current fold to the specified value.
     *
     * <p><i>Warning:</i>  If the number of folds is set to zero, this
     * method will throw an exception.
     *
     * @throws IllegalArgumentException If the fold is not greater than
     * or equal to 0 and less than the number of folds.
     */
    public void setFold(int fold) {
        if (mNumFolds == 0) {
            String msg = "Cannot set folds when numFolds() is 0.";
            throw new IllegalArgumentException(msg);
        }
        if (fold < 0 || fold >= mNumFolds) {
            String msg = "Fold must be non-negative and less than numFolds."
                + " Found numFolds=" + mNumFolds
                + " fold=" + fold;
            throw new IllegalArgumentException(msg);
        }
        mFold = fold;
    }

    /**
     * Return the number of items in this corpus.
     *
     * @return Number of items.
     */
    public int size() {
        return mItemList.size();
    }


    /**
     * Add the specified item to the end of the corpus.
     *
     * @param e Item to add to corpus.
     */
    public void handle(E e) {
        mItemList.add(e);
    }


    /**
     * Send all of the training items to the specified
     * handler.  See the class documentation above for
     * a specification of which items are visited based on
     * the value of the number of folds and the current fold.
     *
     * @param handler Handler receiving training items.
     */
    public void visitTrain(ObjectHandler<E> handler) {
        handle(handler,0,startTestFold());
        handle(handler,endTestFold(),size());
    }

    /**
     * Send all of the test items to the specified
     * handler.  See the class documentation above for
     * a specification of which items are visited based on
     * the value of the number of folds and the current fold.
     *
     * @param handler Handler receiving training items.
     */
    public void visitTest(ObjectHandler<E> handler) {
        handle(handler,startTestFold(),endTestFold());
    }
    

    public void visitCorpus(ObjectHandler<E> handler) {
        for (E e : mItemList)
            handler.handle(e);
    }

    public void visitCorpus(ObjectHandler<E> trainHandler,
                            ObjectHandler<E> testHandler) {
        visitTrain(trainHandler);
        visitTest(testHandler);
    }

    private void handle(ObjectHandler<E> handler, int start, int end) {
        for (int i = start; i < end; ++i)
            handler.handle(mItemList.get(i));
    }

    private int startTestFold() {
        if (mNumFolds == 0) return 0;
        return (int) (size() * (mFold / (double) mNumFolds));
    }

    private int endTestFold() {
        if (mNumFolds == 0) return 0;
        if (mFold == (mNumFolds - 1))
            return size(); // make sure cover the last example
        return (int) (size() * ((mFold + 1.0)/ mNumFolds));
    }



}