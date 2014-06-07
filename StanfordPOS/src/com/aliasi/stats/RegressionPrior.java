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
package com.aliasi.stats;

import com.aliasi.matrix.Vector;

import com.aliasi.util.AbstractExternalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

/**
 * A <code>RegressionPrior</code> instance represents a prior
 * distribution on parameters for linear or logistic regression.
 *
 * <p>Instances of this class are used as parameters in the {@link
 * LogisticRegression} class to control the regularization or lack
 * thereof used by the stochastic gradient descent optimizers.  The
 * priors all assume a zero mean (or position) for each dimension, but
 * allow variances (or scales) to vary by input dimension.
 *
 * <p>The behavior of a prior is determined by its gradient, the
 * partial derivatives with respect to the dimensions of the error
 * function for the prior (negative log likelihood) with respect to
 * a coefficient <code>&beta;<sub>i</sub></code>.
 *
 * <blockquote><pre>
 * gradient(&beta;<sub>i</sub>,i) = - &part; log p(&beta;) / &part; &beta;<sub>i</sub></pre></blockquote>
 *
 * <p>See the class documentation for {@link LogisticRegression}
 * for more information.
 *
 * <p>Priors also implement a log (base 2) probability density for the
 * prior for a given parameter in a given dimension.  The total log
 * prior probability is the sum of the log probabilities for the dimensions.
 *
 * <p>Priors affect gradient descent fitting of regression through
 * their contribution to the gradient of the error function with
 * respect to the parameter vector.  The contribution of the prior to
 * the error function is the negative log probability of the parameter
 * vector(s) with respect to the prior distribution.  The gradient of
 * the error function is the collection of partial derivatives of the
 * error function with respect to the components of the parameter
 * vector.  The regression prior abstract base class is defined in
 * terms of a single method {@link #gradient(double,int)}, which
 * specifies the value of the gradient of the error function for a
 * specified dimension with a specified value in that dimension.
 *
 * <p>This class implements static factory methods to construct
 * non-informative, Gaussian and Laplace priors.  The Gaussian and
 * Laplace priors may specify a different variance for each dimension,
 * but assumes all the prior means are zero.  The priors also assume
 * the dimensions are independent so that the full covariance matrix
 * is assumed to be diagonal (that is, there is zero covariance between
 * different dimensions).
 *
 *
 * <h4>Non-informative Prior &amp; Maximum Likelihood Estimation</h4>
 *
 * <p>Using a non-informative prior for regression results in standard
 * maximum likelihood estimation.
 *
 * <p>The non-informative prior assumes a uniform distribution over
 * parameter vectors:
 *
 * <blockquote><pre>
 * p(&beta;<sub>i</sub>,i) = 1.0</pre></blockquote>
 *
 * and thus contributes nothing to the gradient:
 *
 * <blockquote><pre>
 * gradient(&beta;<sub>i</sub>,i) =  0.0</pre></blockquote>
 *
 * A non-informative prior is constructed using the static method
 * {@link #noninformative()}.
 *
 *
 * <h4>Gaussian Prior, L<sub>2</sub> Regularization &amp; Ridge Regression</h4>
 *
 * <p>The Gaussian prior assumes a Gaussian (also known as normal) density over
 * parameter vectors which results in L<sub>2</sub>-regularized
 * regression, also known as ridge regression.  Specifically, the
 * prior allows a variance to be specified per dimension, but
 * assumes dimensions are independent in that all off-diagonal
 * covariances are zero.
 *
 * <p>The Gaussian density is defined by:
 *
 * <blockquote><pre>
 * p(&beta;<sub>i</sub>,i) = 1.0/sqrt(2 * &pi; &sigma;<sub>i</sub><sup>2</sup>) * exp(-&beta;<sub>i</sub><sup>2</sup>/(2 * &sigma;<sub>i</sub><sup>2</sup>))</pre></blockquote>
 *
 * <p>The Gaussian prior leads to the following contribution to the
 * gradient for a dimension <code>i</code> with parameter
 * <code>beta<sub>i</sub></code> and variance
 * <code>&sigma;<sub>i</sub><sup>2</sup></code>:
 *
 * <blockquote><pre>
 * gradient(&beta;<sub>i</sub>,i) = &beta;<sub>i</sub>/(2 * &sigma;<sub>i</sub><sup>2</sup>)</pre></blockquote>
 *
 * <p>Gaussian priors are constructed using one of the static factory
 * methods, {@link #gaussian(double[])} or {@link
 * #gaussian(double,boolean)}.
 *
 * <h4>Laplace Prior, L<sub>1</sub> Regularization &amp; the Lasso</h4>
 *
 * <p>The Laplace prior assumes a Laplace density over parameter
 * vectors which results in L<sub>1</sub>-regularized regression, also
 * known as the lasso.  The Laplace prior is called a
 * double-exponential distribution because it is looks like an exponential
 * distribution for positive values and the reflection of this exponential
 * distribution around zero (or more generally, around its mean parameter).
 *
 * <p>A Laplace prior allows a variance to be specified per dimension,
 * but like the Gaussian prior, assumes means are zero and that the
 * dimensions are independent in that all off-diagonal covariances are
 * zero.
 *
 * <p>The Laplace density is defined by:
 *
 * <blockquote><pre>
 * p(&beta;<sub>i</sub>,i) = (sqrt(2)/(2 * &sigma;<sub>i</sub>)) * exp(- sqrt(2) * abs(&beta;<sub>i</sub>) / &sigma;<sub>i</sub>)</pre></blockquote>
 *
 * <p>The Laplace prior leads to the following contribution to the
 * gradient for a dimension <code>i</code> with parameter
 * <code>beta<sub>i</sub></code>, mean zero and variance
 * <code>&sigma;<sub>i</sub><sup>2</sup></code>:
 *
 * <blockquote><pre>
 * gradient(&beta;<sub>i</sub>,i) = signum(&beta;<sub>i</sub>)/(2 * &sigma;<sub>i</sub><sup>2</sup>)</pre></blockquote>
 *
 * where the <code>signum</code> function is defined by {@link Math#signum(double)}.
 *
 * <p>Laplace priors are constructed using one of the static factory
 * methods, {@link #laplace(double[])} or {@link
 * #laplace(double,boolean)}.
 *
 *
 * <h4>Cauchy Prior</h4>
 *
 * <p>The Cauchy prior assumes a Cauchy density (also known as a
 * Lorentz density) over priors.  The Cauchy density is a Student-t
 * density with one degree of freedom.  The Cauchy density allows a
 * scale to be specified for each dimension.  The mean and variance
 * are undefined as their integrals diverge.  The Cauchy distribution
 * is symmetric and for regression priors, we assume a mode of zero.
 *
 * <p>The Cauchy density is defined by:
 *
 * <blockquote><pre>
 * p(&beta;<sub>i</sub>,i) = (1 / &pi;) * (&lambda; / (&beta;<sub>i</sub><sup>2</sup> + &lambda;<sup>2</sup>))</pre></blockquote>
 *
 * <p>The Cauchy prior leads to the following contribution to the
 * gradient for dimension <code>i</code> with parameter <code>&beta;<sub>i</sub></code> and scale
 * <code>&lambda;<sub>i</sub><sup>2</sup></code>:
 *
 * <blockquote><pre>
 * gradient(&beta;<sub>i</sub>, i) = 2 &beta;<sub>i</sub> / (&beta;<sub>i</sub><sup>2</sup> + &lambda;<sub>i</sub><sup>2</sup>)</pre></blockquote>
 *
 * <p>Cauchy priors are constructed using one of the static factory
 * methods {@link #cauchy(double[])} or {@link #cauchy(double,boolean)}.

 *
 * <h4>Special Treatment of Intercept</h4>
 *
 * <p>By convention, input dimension zero (<code>0</code>) may be
 * reserved for the intercept and set to value 1.0 in all input
 * vectors.  For regularized regression, the regularization is
 * typically not applied to the intercept term.  To match this
 * convention, the factory methods allow a boolean parameter
 * indicating whether the intercept parameter has a
 * non-informative/uniform prior.  If the intercept flag indicates it
 * is non-informative, then dimension 0 will not have an infinite
 * prior variance or scale, and hence a zero gradient.  The result is
 * that the intercept will be fit by maximum likelihood.
 *
 * <h4>Serialization</h4>
 *
 * <p>All of the regression priors may be serialized.

 * <h4>References</h4>
 *
 * <p>For full details on the Gaussian and Laplace distributions,
 * see:
 *
 * <ul>
 * <li>Wikipedia: <a href="http://en.wikipedia.org/wiki/Normal_distribution">Normal (Gaussian) Distribution</a></li>
 * <li>Wikipedia: <a href="http://en.wikipedia.org/wiki/Laplace_distribution">Laplace (Double Exponential) Distribution</a></li>
 * <li>Wikipedia: <a href="http://en.wikipedia.org/wiki/Cauchy_distribution">Cauchy Distribution</a>
 * </ul>
 *
 * <p>For explanations of how the priors are used with logistic regression,
 * see the following two textbooks:
 *
 * <ul>
 * <li>Hastie, Trevor, Tibshirani, Robert and Jerome Friedman. 2001.
 * <i><a href="http://www-stat.stanford.edu/~tibs/ElemStatLearn/">Elements of Statistical Learning</a></i>.
 * Springer.</li>
 *
 * <li>Bishop, Christopher M. 2006. <a href="http://research.microsoft.com/~cmbishop/PRML/">Pattern Recognition and Machine Learning</a>.
 * Springer.</li>
 * </ul>
 *
 * and two tech reports:
 *
 * <ul>
 * <li>Genkin, Alexander, David D. Lewis, and David Madigan. 2004.
 * <a href="http://www.stat.columbia.edu/~gelman/stuff_for_blog/madigan.pdf">Large-Scale Bayesian Logistic Regression for Text Categorization</a>.
 * Rutgers University Technical Report.
 * (<a href="http://stat.rutgers.edu/~madigan/PAPERS/techno-06-09-18.pdf">alternate download</a>).
 *
 * <li>
 Gelman, Andrew, Aleks Jakulin, Yu-Sung Su, and Maria Grazia Pittau. 2007.
<a href="http://ssrn.com/abstract=1010421">A Default Prior Distribution for Logistic and Other Regression Models</a>.
* </li>
 * </ul>
 *
 * @author  Bob Carpenter
 * @version 3.8.1
 * @since   LingPipe3.5
 */
public abstract class RegressionPrior implements Serializable {

    static final long serialVersionUID = 2955531646832969891L;

    // do not allow instances or subclasses
    private RegressionPrior() { 
    	/* empty constructor */
    }

    /**
     * Returns {@code true} if this prior is the uniform distribution.
     * Uniform priors reduce to maximum likelihood calculations.
     *
     * @return {@code true} if this prior is the uniform distribution.
     */
    public boolean isUniform() {
        return false;
    }

    /**
     * Returns the contribution to the gradient of the error function
     * of the specified parameter value for the specified dimension.
     *
     * @param betaForDimension Parameter value for the specified dimension.
     * @param dimension The dimension.
     * @return The contribution to the gradient of the error function
     * of the parameter value and dimension.
     */
    public abstract double gradient(double betaForDimension, int dimension);

    /**
     * Returns the log (base 2) of the prior density evaluated at the
     * specified coefficient value for the specified dimension.  The
     * overall error function is the sum of the negative log
     * likelihood of the data under the model and the negative log of
     * the prior.
     *
     * @param betaForDimension Parameter value for the specified dimension.
     * @param dimension The dimension.
     * @return The prior probability of the specified parameter value
     * for the specified dimension.
     */
    public abstract double log2Prior(double betaForDimension, int dimension);


    /**
     * Returns the log (base 2) prior density for a specified
     * coefficient vector.
     *
     * @param beta Parameter vector.
     * @return The log (base 2) prior for the specified parameter
     * vector.
     * @throws IllegalArgumentException If the specified parameter
     * vector does not match the dimensionality of the prior (if
     * specified).
     */
    public double log2Prior(Vector beta) {
        int numDimensions = beta.numDimensions();
        verifyNumberOfDimensions(numDimensions);
        double log2Prior = 0.0;
        for (int i = 0; i < numDimensions; ++i)
            log2Prior += log2Prior(beta.value(i),i);
        return log2Prior;
    }

    /**
     * Returns the log (base 2) prior density for the specified
     * array of coefficient vectors.
     *
     * @param betas The parameter vectors.
     * @return The log (base 2) prior density for the specified
     * @throws IllegalArgumentException If any of the specified
     * parameter vectors does not match the dimensionality of the
     * prior (if specified).
     */
    public double log2Prior(Vector[] betas) {
        double log2Prior = 0.0;
        for (Vector beta : betas)
            log2Prior += log2Prior(beta);
        return log2Prior;
    }

    // package local on purpose
    void verifyNumberOfDimensions(int ignoreMeNumDimensions) {
        // do nothing on purpose
    }


    private final static RegressionPrior NONINFORMATIVE_PRIOR
        = new NoninformativeRegressionPrior();


    /**
     * Returns the noninformative or uniform prior to use for
     * maximum likelihood regression fitting.
     *
     * @return The noninformative prior.
     */
    public static RegressionPrior noninformative() {
        return NONINFORMATIVE_PRIOR;
    }


    /**
     * Returns the Gaussian prior with the specified prior variance
     * and indication of whether the intercept is given a
     * noninformative prior.
     *
     * <p>If the noninformative-intercept flag is set to
     * <code>true</code>, the prior variance for dimension zero
     * (<code>0</code>) is set to {@link Double#POSITIVE_INFINITY}.
     *
     * <p>See the class documentation above for more inforamtion on
     * Gaussian priors.
     *
     * @param priorVariance Variance of the Gaussian prior for each
     * dimension.
     * @param noninformativeIntercept Flag indicating if intercept is
     * given a noninformative (uniform) prior.
     * @return The Gaussian prior with the specified parameters.
     * @throws IllegalArgumentException If the prior variance is not
     * a non-negative number.
     */
    public static RegressionPrior gaussian(double priorVariance,
                                           boolean noninformativeIntercept) {
        verifyPriorVariance(priorVariance);
        return new VariableGaussianRegressionPrior(priorVariance,noninformativeIntercept);
    }


    /**
     * Returns the Gaussian prior with the specified priors for
     * each dimension.  The number of dimensions is taken to be
     * the length of the variance array.
     *
     * <p>See the class documentation above for more inforamtion on
     * Gaussian priors.
     *
     * @param priorVariances Array of prior variances for dimensions.
     * @return The Gaussian prior with the specified variances.
     * @throws IllegalArgumentException If any of the variances are not
     * non-negative numbers.
     *
     */
    public static RegressionPrior gaussian(double[] priorVariances) {
        verifyPriorVariances(priorVariances);
        return new GaussianRegressionPrior(priorVariances);
    }



    /**
     * Returns the Laplace prior with the specified prior variance
     * and number of dimensions and indication of whether the
     * intecept dimension is given a noninformative prior.
     *
     * <p>If the noninformative-intercept flag is set to
     * <code>true</code>, the prior variance for dimension zero
     * (<code>0</code>) is set to {@link Double#POSITIVE_INFINITY}.
     *
     * <p>See the class documentation above for more inforamtion on
     * Laplace priors.
     *
     * @param priorVariance Variance of the Laplace prior for each
     * dimension.
     * @param noninformativeIntercept Flag indicating if intercept is
     * given a noninformative (uniform) prior.
     * @return The Laplace prior with the specified parameters.
     * @throws IllegalArgumentException If the variance is not a non-negative
     * number.
     */
    public static RegressionPrior laplace(double priorVariance,
                                          boolean noninformativeIntercept) {
        verifyPriorVariance(priorVariance);
        return new VariableLaplaceRegressionPrior(priorVariance,noninformativeIntercept);
    }


    /**
     * Returns the Laplace prior with the specified prior variances
     * for the dimensions.
     *
     * <p>See the class documentation above for more inforamtion on
     * Laplace priors.
     *
     * @param priorVariances Array of prior variances for dimensions.
     * @return The Laplace prior for the specified variances.
     * @throws IllegalArgumentException If any of the variances is not
     * a non-negative number.
     */
    public static RegressionPrior laplace(double[] priorVariances) {
        verifyPriorVariances(priorVariances);
        return new LaplaceRegressionPrior(priorVariances);
    }


    /**
     * Returns the Cauchy prior with the specified prior squared
     * scales for the dimensions.
     *
     * <p>See the class documentation above for more information
     * on Cauchy priors.
     *
     * @param priorSquaredScale The square of the prior scae parameter.
     * @param noninformativeIntercept Flag indicating if intercept is
     * given a noninformative (uniform) prior.
     * @return The Cauchy prior for the specified squared scale and
     * intercept flag.
     * @throws IllegalArgumentException If the scale is not a non-negative
     * number.
     */
    public static RegressionPrior cauchy(double priorSquaredScale,
                                         boolean noninformativeIntercept) {
        verifyPriorVariance(priorSquaredScale);
        return new VariableCauchyRegressionPrior(priorSquaredScale,noninformativeIntercept);
    }

    /**
     * Returns the Cauchy prior for the specified squared scales.
     *
     * <p>See the class documentation above for more information
     * on Cauchy priors.
     *
     * @param priorSquaredScales Prior squared scale parameters.
     * @return The Cauchy prior for the specified square scales.
     * @throws IllegalArgumentException If any of the prior squared
     * scales is not a non-negative number.
     */
    public static RegressionPrior cauchy(double[] priorSquaredScales) {
        verifyPriorVariances(priorSquaredScales);
        return new CauchyRegressionPrior(priorSquaredScales);
    }


    static void verifyPriorVariance(double priorVariance) {
        if (priorVariance < 0
            || Double.isNaN(priorVariance)
            || priorVariance == Double.NEGATIVE_INFINITY) {

            String msg = "Prior variance must be a non-negative number."
                + " Found priorVariance=" + priorVariance;
            throw new IllegalArgumentException(msg);
        }
    }

    static void verifyPriorVariances(double[] priorVariances) {
        for (int i = 0; i < priorVariances.length; ++i) {

            if (priorVariances[i] < 0
                || Double.isNaN(priorVariances[i])
                || priorVariances[i] == Double.NEGATIVE_INFINITY) {

                String msg = "Prior variances must be non-negative numbers."
                    + " Found priorVariances[" + i + "]=" + priorVariances[i];
                throw new IllegalArgumentException(msg);
            }
        }
    }

    static class NoninformativeRegressionPrior
        extends RegressionPrior
        implements Serializable {

        static final long serialVersionUID = -582012445093979284L;

        @Override
        public double gradient(double beta, int dimension) {
            return 0.0;
        }
        @Override
        public double log2Prior(double beta, int dimension) {
            return 0.0;  // log2(1) = 0
        }
        @Override
        public double log2Prior(Vector beta) {
            return 0.0;
        }
        @Override
        public double log2Prior(Vector[] betas) {
            return 0.0;
        }
        @Override
        public String toString() {
            return "NoninformativeRegressionPrior";
        }
        @Override
        public boolean isUniform() { 
            return true; 
        }

    }

    static abstract class ArrayRegressionPrior extends RegressionPrior {
        static final long serialVersionUID = -1887383164794837169L;
        final double[] mValues;
        ArrayRegressionPrior(double[] values) {
            mValues = values;
        }
        @Override
        void verifyNumberOfDimensions(int numDimensions) {
            if (mValues.length != numDimensions) {
                String msg = "Prior and instances must match in number of dimensions."
                    + " Found prior numDimensions=" + mValues.length
                    + " instance numDimensions=" + numDimensions;
                throw new IllegalArgumentException(msg);
            }
        }
        public String toString(String priorName, String paramName) {
            StringBuilder sb = new StringBuilder();
            sb.append(priorName + "\n");
            sb.append("     dimensionality=" + mValues.length);
            for (int i = 0; i < mValues.length; ++i)
                sb.append("     " + paramName + "[" + i + "]=" + mValues[i] + "\n");
            return sb.toString();
        }
    }

    static class GaussianRegressionPrior
        extends ArrayRegressionPrior
        implements Serializable {

        static final long serialVersionUID = 8257747607648390037L;

        GaussianRegressionPrior(double[] priorVariances) {
            super(priorVariances);
        }
        @Override
        public double gradient(double beta, int dimension) {
            return beta / mValues[dimension];
        }
        @Override
        public double log2Prior(double beta, int dimension) {
            return -log2Sqrt2Pi
                - 0.5 * com.aliasi.util.Math.log2(mValues[dimension])
                - beta * beta / (2.0 * mValues[dimension]);
        }
        @Override
        public String toString() {
            return toString("GaussianRegressionPrior","Variance");
        }
        private Object writeReplace() {
            return new Serializer(this);
        }
        private static class Serializer extends AbstractExternalizable {

            static final long serialVersionUID = -1129377549371296060L;

            final GaussianRegressionPrior mPrior;
            public Serializer(GaussianRegressionPrior prior) {
                mPrior = prior;
            }
            public Serializer() {
                this(null);
            }
            @Override
            public void writeExternal(ObjectOutput out) throws IOException {
                out.writeInt(mPrior.mValues.length);
                for (int i = 0; i < mPrior.mValues.length; ++i)
                    out.writeDouble(mPrior.mValues[i]);
            }
            @Override
            public Object read(ObjectInput in) throws IOException, ClassNotFoundException {
                int numDimensions = in.readInt();
                double[] priorVariances = new double[numDimensions];
                for (int i = 0; i < numDimensions; ++i)
                    priorVariances[i] = in.readDouble();
                return new GaussianRegressionPrior(priorVariances);
            }
        }
    }

    static final double sqrt2 = Math.sqrt(2.0);
    static final double log2Sqrt2Over2 = com.aliasi.util.Math.log2(sqrt2/2.0);
    static final double log2Sqrt2Pi
        = com.aliasi.util.Math.log2(Math.sqrt(2.0 * Math.PI));
    static final double log21OverPi = -com.aliasi.util.Math.log2(Math.PI);

    static class LaplaceRegressionPrior
        extends ArrayRegressionPrior
        implements Serializable {

        static final long serialVersionUID = 9120480132502062861L;

        LaplaceRegressionPrior(double[] priorVariances) {
            super(priorVariances);
        }
        @Override
        public double gradient(double beta, int dimension) {
            if (beta == 0.0) return 0.0;
            if (beta > 0)
                return Math.sqrt(2.0/mValues[dimension]);
            return -Math.sqrt(2.0/mValues[dimension]);
        }
        @Override
        public double log2Prior(double beta, int dimension) {
            return log2Sqrt2Over2
                - 0.5 * com.aliasi.util.Math.log2(mValues[dimension])
                - sqrt2 * Math.abs(beta) / Math.sqrt(mValues[dimension]);
        }
        @Override
        public String toString() {
            return toString("LaplaceRegressionPrior","Variance");
        }
        private Object writeReplace() {
            return new Serializer(this);
        }
        private static class Serializer extends AbstractExternalizable {
            static final long serialVersionUID = 7844951573062416091L;
            final LaplaceRegressionPrior mPrior;
            public Serializer(LaplaceRegressionPrior prior) {
                mPrior = prior;
            }
            public Serializer() {
                this(null);
            }
            @Override
            public void writeExternal(ObjectOutput out) throws IOException {
                out.writeInt(mPrior.mValues.length);
                for (int i = 0; i < mPrior.mValues.length; ++i)
                    out.writeDouble(mPrior.mValues[i]);
            }
            @Override
            public Object read(ObjectInput in)
                throws IOException, ClassNotFoundException {

                int numDimensions = in.readInt();
                double[] priorVariances = new double[numDimensions];
                for (int i = 0; i < numDimensions; ++i)
                    priorVariances[i] = in.readDouble();
                return new LaplaceRegressionPrior(priorVariances);
            }
        }
    }

    static class CauchyRegressionPrior
        extends ArrayRegressionPrior
        implements Serializable {

        static final long serialVersionUID = 2351846943518745614L;

        CauchyRegressionPrior(double[] priorSquaredScales) {
            super(priorSquaredScales);
        }
        @Override
        public double gradient(double beta, int dimension) {
            return 2.0 * beta / (beta * beta + mValues[dimension]);
        }
        @Override
        public double log2Prior(double beta, int dimension) {
            return log21OverPi
                + 0.5 * com.aliasi.util.Math.log2(mValues[dimension])
                - com.aliasi.util.Math.log2(beta * beta + mValues[dimension]
                                            * mValues[dimension]);
        }
        @Override
        public String toString() {
            return toString("CauchyRegressionPrior","Scale");
        }
        private Object writeReplace() {
            return new Serializer(this);
        }
        private static class Serializer extends AbstractExternalizable {
            static final long serialVersionUID = 5202676106810759907L;
            final CauchyRegressionPrior mPrior;
            public Serializer(CauchyRegressionPrior prior) {
                mPrior = prior;
            }
            public Serializer() {
                this(null);
            }
            @Override
            public void writeExternal(ObjectOutput out) throws IOException {
                out.writeInt(mPrior.mValues.length);
                for (int i = 0; i < mPrior.mValues.length; ++i)
                    out.writeDouble(mPrior.mValues[i]);
            }
            @Override
            public Object read(ObjectInput in)
                throws IOException, ClassNotFoundException {

                int numDimensions = in.readInt();
                double[] priorScales = new double[numDimensions];
                for (int i = 0; i < numDimensions; ++i)
                    priorScales[i] = in.readDouble();
                return new CauchyRegressionPrior(priorScales);
            }
        }
    }


    static abstract class VariableRegressionPrior extends RegressionPrior {

        static final long serialVersionUID = -7527207309328127863L;

        final double mPriorVariance;
        final boolean mNoninformativeIntercept;
        VariableRegressionPrior(double priorVariance,
                                boolean noninformativeIntercept) {
            mPriorVariance = priorVariance;
            mNoninformativeIntercept = noninformativeIntercept;
        }
        public String toString(String priorName, String paramName) {
            return priorName + "(" + paramName + "=" + mPriorVariance
                + ", noninformativeIntercept=" + mNoninformativeIntercept + ")";
        }
    }

    static class VariableGaussianRegressionPrior
        extends VariableRegressionPrior
        implements Serializable {
        static final long serialVersionUID = -7527207309328127863L;
        VariableGaussianRegressionPrior(double priorVariance,
                                        boolean noninformativeIntercept) {
            super(priorVariance,noninformativeIntercept);
        }
        @Override
        public double gradient(double beta, int dimension) {
            return (dimension == 0 && mNoninformativeIntercept)
                ? 0.0
                : beta / mPriorVariance;
        }
        @Override
        public double log2Prior(double beta, int dimension) {
            if (dimension == 0 && mNoninformativeIntercept)
                return 0.0;  // log(1)=0.0
            return -log2Sqrt2Pi
                - 0.5 * com.aliasi.util.Math.log2(mPriorVariance)
                - beta * beta / (2.0 * mPriorVariance);

        }
        @Override
        public String toString() {
            return toString("GaussianRegressionPrior","Variance");
        }
        private Object writeReplace() {
            return new Serializer(this);
        }
        private static class Serializer extends AbstractExternalizable {
            static final long serialVersionUID = 5979483825025936160L;
            final VariableGaussianRegressionPrior mPrior;
            public Serializer(VariableGaussianRegressionPrior prior) {
                mPrior = prior;
            }
            public Serializer() {
                this(null);
            }
            @Override
            public void writeExternal(ObjectOutput out) throws IOException {
                out.writeDouble(mPrior.mPriorVariance);
                out.writeBoolean(mPrior.mNoninformativeIntercept);
            }
            @Override
            public Object read(ObjectInput in)
                throws IOException, ClassNotFoundException {

                double priorVariance = in.readDouble();
                boolean noninformativeIntercept = in.readBoolean();
                return new VariableGaussianRegressionPrior(priorVariance,
                                                           noninformativeIntercept);
            }
        }
    }

    static class VariableLaplaceRegressionPrior
        extends VariableRegressionPrior
        implements Serializable {

        static final long serialVersionUID = -4286001162222250623L;
        final double mPositiveGradient;
        final double mNegativeGradient;
        final double mPriorIntercept;
        final double mPriorCoefficient;
        VariableLaplaceRegressionPrior(double priorVariance,
                                       boolean noninformativeIntercept) {
            super(priorVariance,noninformativeIntercept);
            mPositiveGradient = Math.sqrt(2.0/priorVariance);
            mNegativeGradient = -mPositiveGradient;
            mPriorIntercept
                = log2Sqrt2Over2 - 0.5
                * com.aliasi.util.Math.log2(priorVariance);
            mPriorCoefficient = -sqrt2 / Math.sqrt(priorVariance);
        }
        @Override
        public double gradient(double beta, int dimension) {
            return (dimension == 0 && mNoninformativeIntercept || beta == 0.0)
                ? 0.0
                : (beta > 0
                   ? mPositiveGradient
                   : mNegativeGradient );
        }
        @Override
        public double log2Prior(double beta, int dimension) {
            if (dimension == 0 && mNoninformativeIntercept)
                return 0.0;
            return mPriorIntercept + mPriorCoefficient * Math.abs(beta);

            // return log2Sqrt2Over2
            // - 0.5 * com.aliasi.util.Math.log2(mPriorVariance)
            // - sqrt2 * Math.abs(beta) / Math.sqrt(mPriorVariance);
        }
        @Override
        public String toString() {
            return toString("LaplaceRegressionPrior","Variance");
        }
        private Object writeReplace() {
            return new Serializer(this);
        }
        private static class Serializer extends AbstractExternalizable {
            static final long serialVersionUID = 2321796089407881776L;
            final VariableLaplaceRegressionPrior mPrior;
            public Serializer(VariableLaplaceRegressionPrior prior) {
                mPrior = prior;
            }
            public Serializer() {
                this(null);
            }
            @Override
            public void writeExternal(ObjectOutput out) throws IOException {
                out.writeDouble(mPrior.mPriorVariance);
                out.writeBoolean(mPrior.mNoninformativeIntercept);
            }
            @Override
            public Object read(ObjectInput in)
                throws IOException, ClassNotFoundException {

                double priorVariance = in.readDouble();
                boolean noninformativeIntercept = in.readBoolean();
                return new VariableLaplaceRegressionPrior(priorVariance,
                                                          noninformativeIntercept);
            }
        }
    }

    static class VariableCauchyRegressionPrior
        extends VariableRegressionPrior {
        static final long serialVersionUID = 3368658136325392652L;
        VariableCauchyRegressionPrior(double priorVariance,
                                      boolean noninformativeIntercept) {
            super(priorVariance,noninformativeIntercept);
        }
        @Override
        public double gradient(double beta, int dimension) {
            return (dimension == 0 && mNoninformativeIntercept)
                ? 0
                : 2.0 * beta / (beta * beta + mPriorVariance);
        }
        @Override
        public double log2Prior(double beta, int dimension) {
            if (dimension == 0 && mNoninformativeIntercept)
                return 0.0;
            return log21OverPi
                + 0.5 * com.aliasi.util.Math.log2(mPriorVariance)
                - com.aliasi.util.Math.log2(beta * beta + mPriorVariance);
        }
        @Override
        public String toString() {
            return toString("CauchyRegressionPrior","Scale");
        }
        public Object writeReplace() {
            return new Serializer(this);
        }
        private static class Serializer extends AbstractExternalizable {
            static final long serialVersionUID = -7209096281888148303L;
            final VariableCauchyRegressionPrior mPrior;
            public Serializer(VariableCauchyRegressionPrior prior) {
                mPrior = prior;
            }
            public Serializer() {
                this(null);
            }
            @Override
            public void writeExternal(ObjectOutput out) throws IOException {
                out.writeDouble(mPrior.mPriorVariance);
                out.writeBoolean(mPrior.mNoninformativeIntercept);
            }
            @Override
            public Object read(ObjectInput in)
                throws IOException, ClassNotFoundException {

                double priorScale = in.readDouble();
                boolean noninformativeIntercept = in.readBoolean();
                return new VariableCauchyRegressionPrior(priorScale,
                                                         noninformativeIntercept);
            }
        }
    }



}


