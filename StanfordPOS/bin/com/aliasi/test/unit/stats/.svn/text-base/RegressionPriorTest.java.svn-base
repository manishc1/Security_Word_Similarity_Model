package com.aliasi.test.unit.stats;

import com.aliasi.stats.RegressionPrior;

import com.aliasi.util.AbstractExternalizable;

import static com.aliasi.test.unit.Asserts.succeed;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;


import java.io.IOException;

public class RegressionPriorTest  {

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        double[] priorVariances = new double[] { 1.0, 2.0, 3.0 };
        double priorVariance = 1.0;

        assertSerialization(RegressionPrior.cauchy(priorVariances),3);
        assertSerialization(RegressionPrior.cauchy(priorVariance,true),-1);
        assertSerialization(RegressionPrior.cauchy(priorVariance,false),-1);

        assertSerialization(RegressionPrior.gaussian(priorVariances),3);
        assertSerialization(RegressionPrior.gaussian(priorVariance,true),-1);
        assertSerialization(RegressionPrior.gaussian(priorVariance,false),-1);

        assertSerialization(RegressionPrior.laplace(priorVariances),3);
        assertSerialization(RegressionPrior.laplace(priorVariance,true),-1);
        assertSerialization(RegressionPrior.laplace(priorVariance,false),-1);

        assertSerialization(RegressionPrior.noninformative(),-1);

    }

    void assertSerialization(RegressionPrior prior, int dimensionality)
        throws IOException, ClassNotFoundException {

        RegressionPrior prior2 = (RegressionPrior) AbstractExternalizable.serializeDeserialize(prior);
        for (int i = 0; i < dimensionality || dimensionality == -1 && i < 10; ++i) {
            assertEquals(prior.log2Prior(2.0,i),
                         prior2.log2Prior(2.0,i),
                         0.00001);
            assertEquals(prior.log2Prior(-1.0,i),
                         prior2.log2Prior(-1.0,i),
                         0.00001);
            assertEquals(prior.gradient(5.0,i),
                         prior2.gradient(5.0,i),
                         0.00001);
            assertEquals(prior.gradient(-2.0,i),
                         prior2.gradient(-2.0,i),
                         0.00001);
        }
        if (dimensionality > 0) {
            try {
                prior.gradient(2.0,dimensionality+1);
                fail();
            } catch (ArrayIndexOutOfBoundsException e) {
                succeed();
            }
        }
    }
}
