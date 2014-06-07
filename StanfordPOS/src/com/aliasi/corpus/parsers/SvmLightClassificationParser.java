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

package com.aliasi.corpus.parsers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.aliasi.classify.Classification;
import com.aliasi.corpus.ClassificationHandler;
import com.aliasi.corpus.LineParser;
import com.aliasi.matrix.SparseFloatVector;
import com.aliasi.matrix.Vector;
import com.aliasi.util.ObjectToDoubleMap;

/**
 * The <code>SvmLightClassificationParser</code> class parses (a
 * generalization of) the widely-used SVM<sup><i>light</i></sup>
 * format for vector classification.
 *
 * <h4>The Format</h4>
 *
 * The SVM<sup><i>light</i></sup> format is line-based, with each line
 * representing a classification instance.  A line consists of a category
 * followed by an arbitrary number of feature/value pairs, followed by
 * an optional comment.  
 *
 * <p>The following example is drawn from the
 * SVM<sup><i>light</i></sup> documentation:
 *
 * <blockquote><pre>
 * -1 1:0.43 3:0.12 9284:0.2 # abcdef</pre></blockquote>
 *
 * <p>The category is <code>-1</code>, the feature 1 has value 0.43,
 * the feature 3 has value 0.12 and the feature 9284 has value 0.2,
 * with <code># abcdef</code> being a comment.
 *
 * <p>This class generalizes the format to allow arbitrary string-based categories
 * in addition to the <code>-1</code>, <code>0</code> and <code>1</code>
 * allowed by SVM<sup><i>light</i></sup>. 
 *
 * <p>This class also generalizes the format to allow the features to
 * appear in any order and to treat features occurring more than once
 * as having a value equal to the sum of their specified values.
 *
 * <p>This class does <b>not</b> parse numerical regression data files
 * in which the category is a floating point value, nor does it deal with
 * the ranking mode of SVM<sup><i>light</i></sup>.
 *
 * <p>No spaces are allowed around the colons separating dimensions
 * and their values; all other whitespace in a line may be one or more
 * spaces or tabs.
 *
 * <p>Blank lines are ignored.
 *
 * <h4>Target Vectors and Dimensionality</h4>
 *
 * <p>The vectors produced by this parser will be instances of {@link
 * SparseFloatVector}.  The dimensionality of these vectors must
 * be specified in the constructor for the parser.
 * 
 * 
 
 * <h4>References</h4>
 *
 * <p>The home page for and primary reference for SVM<sup><i>light</i></sup> is:
 *
 * <ul><li><a href="http://svmlight.joachims.org/">SVM<sup><i>light</i></sup> Home Page</a></li>
 *
   <li>Joachims, Thorsten. 1999. 
 * <a href="http://www.joachims.org/publications/joachims_99a.pdf">Making large-Scale SVM Learning Practical</a>. In <i>Advances in Kernel Methods - Support Vector Learning</i>, B. Sch&ouml;lkopf and C. Burges and A. Smola (ed.). MIT Press.
 * </li></ul>
 *
 * <p>Many other packages use (some variant of) the
 * SVM<sup><i>light</i></sup> basic classification format, including:
 *
 * <ul>
 * <li>Bottou, Leon. <a href="http://leon.bottou.org/projects/sgd">SGD</a> (logistic regression and SVMs)</li>
 * <li>Langford, John, Alex Strehl and Lihong Li. <a href="http://hunch.net/~vw/">Vowpal Rabbit</a> (squared loss linear regression)</li>
 * <li>Genkin, Alexander, David D. Lewis, and David Madigan.
 <a href="http://www.stat.rutgers.edu/~madigan/BMR/">BMR: Bayesian Multinomial Regression</a> (regularized logistic regression)
 * </ul>
 *
 * @author  Bob Carpenter
 * @version 3.5
 * @since   LingPipe3.5
 */
public class SvmLightClassificationParser 
    extends LineParser<ClassificationHandler<Vector,Classification>> {

    private final int mDataDimensionality;
    private final boolean mAddIntercept;
    private final Set<String> mCategories;

    private int mMaxDimensionFound = -1;

    /**
     * Construct a classification parser for the
     * SVM<sup><i>light</i></sup> format for data of the specified
     * dimensionality and specified automatic intercept flag.
     *
     * <p>The dimensionality may be chosen to be {@link
     * Integer#MAX_VALUE} without any efficiency problems because the
     * created vectors are sparse.
     *
     * <p>If the intercept flag is set to <code>true</code>, the value
     * of dimension 0 will always be 1.0.  Note that this will override
     * any other value of the parameter 0 found in the line.
     *
     * @param dataDimensionality Number of dimensions in the data.
     * @param addIntercept Flag indicating whether an intercept should be
     * automatically added.
     */
    public SvmLightClassificationParser(boolean addIntercept, int dataDimensionality) {
        this(null,addIntercept,dataDimensionality);
    }

    /**
     * Construct a classification parser for the
     * SVM<sup><i>light</i></sup> format for data of the specified
     * dimensionality, specified automatic intercept flag, and
     * specified classification handler.
     *
     * <p>The dimensionality may be chosen to be {@link
     * Integer#MAX_VALUE} without any efficiency problems because the
     * created vectors are sparse.
     *
     * <p>If the intercept flag is set to <code>true</code>, the value
     * of dimension 0 will always be 1.0.
     *
     * @param handler Classification handler for data.
     * @param dataDimensionality Number of dimensions in the data.
     * @param addIntercept Flag indicating whether an intercept should be
     * automatically added.
     */
    public SvmLightClassificationParser(ClassificationHandler<Vector,Classification> handler,
                                        boolean addIntercept,
                                        int dataDimensionality) {
        super(handler);
        mDataDimensionality = dataDimensionality;
        mAddIntercept = addIntercept;
        mCategories = new HashSet<String>();
    }

    /**
     * Parses a line of the data, pulling out category and
     * dimensiona/value pairs.  
     *
     * @param line Line of data to parse.
     * @param lineNumber Number of line being parsed.
     * @throws NumberFormatException If there is a dimension that is
     * not parsable as an integer or a value that is not parsable as a
     * double.
     * @throws IllegalArgumentException For other ill-formed line
     * exceptions.
     */
    @Override
    protected void parseLine(String line, int lineNumber) {
        line = stripComment(line);
        String[] pairs = line.split("\\s+");
        if (pairs.length == 0) return;
        String category = pairs[0];
        ObjectToDoubleMap<Integer> valMap = new ObjectToDoubleMap<Integer>();
        for (int i = 1; i < pairs.length; ++i) {
            String pair = pairs[i];
            int colonIndex = pair.indexOf(':');
            int dimension = Integer.valueOf(pair.substring(0,colonIndex));
            if (dimension > mMaxDimensionFound)
                mMaxDimensionFound = dimension;
            double value = Double.valueOf(pair.substring(colonIndex+1));
            valMap.increment(dimension,value);
        }
        if (mAddIntercept) valMap.set(0,1.0); // overrides
        Vector v = new SparseFloatVector(valMap,mDataDimensionality);
        Classification c = new Classification(category);
        mCategories.add(category);
        getHandler().handle(v,c);
    }

    /**
     * Returns the maximum dimension index found so far.  If the
     * parser runs over all data of interest, the dimensionality may
     * be set to one plus the returned value.
     *
     * @return The maximum dimension index found in the data so far.
     */
    public int maxDimensionFound() {
        return mMaxDimensionFound;
    }

    /**
     * Returns an immutable set of the categories that have been found so far.
     *
     * @return An immutable set of the categories that have been found so far.
     */
    public Set<String> categoriesFound() {
        return Collections.unmodifiableSet(mCategories);
    }

    static String stripComment(String line) {
        int commentStart = line.indexOf('#');
        return commentStart < 0
            ? line 
            : line.substring(0,commentStart);
    }

}
