/**
 * Title:       Stanford JavaNLP.
 * Description: A Maximum Entropy Toolkit.
 * Copyright:   Copyright (c) 2002. Kristina Toutanova, Stanford University
 * Company:     Stanford University, All Rights Reserved.
 */
package edu.stanford.nlp.maxent;


import edu.stanford.nlp.maxent.iis.LambdaSolve;
import edu.stanford.nlp.optimization.DiffFunction;
import edu.stanford.nlp.optimization.Function;
import edu.stanford.nlp.optimization.Minimizer;
import edu.stanford.nlp.optimization.QNMinimizer;

/**
 * This class will call Conjugate Gradient on a LambdaSolve object to find
 * optimal parameters, including imposing a Gaussian prior on those
 * parameters.
 *
 * @author Kristina Toutanova
 * @author Christopher Manning
 */
public class CGRunner {

  private static final boolean SAVE_LAMBDAS_REGULARLY = false;

  private final LambdaSolve prob;
  private final String filename;
  /**
   * Error tolerance passed to CGMinimizer
   */
  private final double tol;
  private final boolean useGaussianPrior;
  private double priorSigmaS = DEFAULT_SIGMASQUARED;
  private double[] sigmaSquareds;

  private static final double DEFAULT_TOLERANCE = 1e-4;
  private static final double DEFAULT_SIGMASQUARED = 0.5;


  /**
   * Set up a LambdaSolve problem for solution by Conjugate Gradient.
   * Uses a Gaussian prior with a sigma<sup>2</sup> of 0.5.
   *
   * @param prob     The problem to solve
   * @param filename Used (with extension) to save intermediate results.
   */
  public CGRunner(LambdaSolve prob, String filename) {
    this(prob, filename, DEFAULT_TOLERANCE, true, DEFAULT_SIGMASQUARED);
  }

  /**
   * Set up a LambdaSolve problem for solution by Conjugate Gradient,
   * specifying a value for sigma<sup>2</sup>.
   *
   * @param prob             The problem to solve
   * @param filename         Used (with extension) to save intermediate results.
   * @param priorSigmaS      The prior sigma<sup>2</sup>: this doubled will be
   *                         used to divide the lambda<sup>2</sup> values as the
   *                         prior penalty in the likelihood.
   */
  public CGRunner(LambdaSolve prob, String filename, double priorSigmaS) {
    this(prob, filename, DEFAULT_TOLERANCE, true, priorSigmaS);
  }

  /**
   * Set up a LambdaSolve problem for solution by Conjugate Gradient.
   *
   * @param prob             The problem to solve
   * @param filename         Used (with extension) to save intermediate results.
   * @param tol              Tolerance of errors (passed to CG)
   * @param useGaussianPrior True if parameters should be penalized with
   *                         a gaussian prior for smoothing
   * @param priorSigmaS      The prior sigma<sup>2</sup>: this doubled will be
   *                         used to divide the lambda<sup>2</sup> values as the
   *                         prior penalty
   */
  public CGRunner(LambdaSolve prob, String filename, double tol, boolean useGaussianPrior, double priorSigmaS) {
    this.prob = prob;
    this.filename = filename;
    this.tol = tol;
    this.useGaussianPrior = useGaussianPrior;
    this.priorSigmaS = priorSigmaS;
  }


  /**
   * Set up a LambdaSolve problem for solution by Conjugate Gradient.
   *
   * @param prob             The problem to solve
   * @param filename         Used (with extension) to save intermediate results.
   * @param tol              Tolerance of errors (passed to CG)
   * @param useGaussianPrior True if parameters should be penalized with
   *                         a gaussian prior for smoothing
   * @param sigmaSquareds           The prior sigma<sup>2</sup>: this doubled will be
   *                         used to divide the lambda<sup>2</sup> values as the
   *                         prior penalty
   */
  public CGRunner(LambdaSolve prob, String filename, double tol, boolean useGaussianPrior, double[] sigmaSquareds) {
    this.prob = prob;
    this.filename = filename;
    this.tol = tol;
    this.useGaussianPrior = useGaussianPrior;
    this.sigmaSquareds = sigmaSquareds;
  }


  /**
   * Solves the problem using CG.  The solution is stored in the
   * <code>lambda</code> array of <code>prob</code>.
   */
  public void solve() {
    LikelihoodFunction df = new LikelihoodFunction(prob, tol, useGaussianPrior, priorSigmaS);

    if (sigmaSquareds != null) {
      df.setSigmaSquareds(sigmaSquareds);
    }

    MonitorFunction monitor = new MonitorFunction(prob, df, filename);
    Minimizer<DiffFunction> cgm = new QNMinimizer(monitor, 10);

    // all parameters are started at 0.0
    double[] result = cgm.minimize(df, tol, new double[df.domainDimension()]);
    prob.lambda = result;
    monitor.reportMonitoring(df.valueAt(result));
    System.err.println("after optimization value is " + df.valueAt(result));
  }


  /**
   * This class implements the DiffFunction interface for Minimizer
   */
  private static final class LikelihoodFunction implements DiffFunction {

    private final LambdaSolve model;
    private final double tol;
    private final boolean useGaussianPrior;
    private int valueAtCalls;
    private double likelihood;
    private double[] sigmaSquareds;

    // Stores sigma value for Gaussian prior penalty on lambdas
    // don't do this since they're all the same and constant.
    // private double[] sigmaSquareds;

    public LikelihoodFunction(LambdaSolve m, double tol, boolean useGaussianPrior, double sigmaSquared) {
      model = m;
      this.tol = tol;
      this.useGaussianPrior = useGaussianPrior;
      // keep separate prior on each parameter for flexibility
      sigmaSquareds = new double[model.lambda.length];
      for (int i = 0; i < sigmaSquareds.length; i++) {
        sigmaSquareds[i] = sigmaSquared;
      }
    }

    public int domainDimension() {
      return model.lambda.length;
    }

    public double likelihood() {
      return likelihood;
    }

    public int numCalls() {
      return valueAtCalls;
    }

    /**
     * Set the sigmaSquareds externally.
     * @param sigmaSquareds Sigma-squared values in featur indexing order
     */
    public void setSigmaSquareds(double[] sigmaSquareds) {
      this.sigmaSquareds = sigmaSquareds;

    }

    public double valueAt(double[] lambda) {
      valueAtCalls++;
      model.lambda = lambda;
      double lik = model.logLikelihoodScratch();

      if (useGaussianPrior) {
        //double twoSigmaSquared = 2 * sigmaSquared;
        for (int i = 0; i < lambda.length; i++) {
          lik += (lambda[i] * lambda[i]) / (sigmaSquareds[i] + sigmaSquareds[i]);
        }
      }
      // System.err.println(valueAtCalls + " calls to valueAt;" +
      //		       " penalized log likelihood is " + lik);

      likelihood = lik;
      return lik;
    }


    public double[] derivativeAt(double[] lambda) {
      boolean eq = true;
      for (int j = 0; j < lambda.length; j++) {
        if (Math.abs(lambda[j] - model.lambda[j]) > tol) {
          eq = false;
          break;
        }
      }
      if (!eq) {
        System.err.println("derivativeAt: call with different value");
        valueAt(lambda);
      }

      double[] drvs = model.getDerivatives();

      // System.out.println("for lambdas "+lambda[0]+" "+lambda[1] +
      //                   " derivatives "+drvs[0]+" "+drvs[1]);

      if (useGaussianPrior) {
        // prior penalty
        for (int j = 0; j < lambda.length; j++) {
          // double sign=1;
          // if(lambda[j]<=0){sign=-1;}
          drvs[j] += lambda[j] / sigmaSquareds[j];
        }
      }

      //System.out.println("final derivatives "+drvs[0]+" "+drvs[1]);
      return drvs;
    }
  }


  /**
   * This one is used in the monitor
   */
  private static final class MonitorFunction implements Function {

    private final LambdaSolve model;
    private final LikelihoodFunction lf;
    private final String filename;
    private int iterations; // = 0

    public MonitorFunction(LambdaSolve m, LikelihoodFunction lf, String filename) {
      this.model = m;
      this.lf = lf;
      this.filename = filename;
    }

    @SuppressWarnings({"ConstantConditions"})
    public double valueAt(double[] lambda) {
      double likelihood = lf.likelihood();
      // this line is printed in the middle of the normal line of QN minimization, so put println at beginning
      System.err.println();
      System.err.print(reportMonitoring(likelihood));

      if (SAVE_LAMBDAS_REGULARLY  && iterations > 0 && iterations % 5 == 0) {
        model.save_lambdas(filename + '.' + iterations + ".lam");
      }

      if (iterations > 0 && iterations % 30 == 0) {
        model.checkCorrectness();
      }
      iterations++;

      return 42; // never cause premature termination.
    }

    public String reportMonitoring(double likelihood) {
      return "Iter. " + iterations + ": " + "neg. log cond. likelihood = " + likelihood + " [" + lf.numCalls() + " calls to valueAt]";
    }

    public int domainDimension() {
      return lf.domainDimension();
    }

  }

}
