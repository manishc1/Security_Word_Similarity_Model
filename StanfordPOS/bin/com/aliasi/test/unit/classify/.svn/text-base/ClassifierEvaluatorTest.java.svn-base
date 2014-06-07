package com.aliasi.test.unit.classify;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertArrayEquals;
import static com.aliasi.test.unit.Asserts.assertEqualsArray;


import com.aliasi.classify.Classification;
import com.aliasi.classify.ConditionalClassification;
import com.aliasi.classify.Classifier;
import com.aliasi.classify.ClassifierEvaluator;
import com.aliasi.classify.JointClassification;
import com.aliasi.classify.RankedClassification;
import com.aliasi.classify.ScoredClassification;
import com.aliasi.classify.ScoredPrecisionRecallEvaluation;

import com.aliasi.util.Pair;

import java.util.HashMap;
import java.util.List;

public class ClassifierEvaluatorTest  {

    private static final String[] CATS
        = new String[] { "foo", "bar", "baz" };

    private static final String[] CATS2
        = new String[] { "foo", "bar" };

    // @Test
    public void testScoredPrCurveOneVersusAll() {
        java.util.Random random = new java.util.Random(); // fix long seed for replication
        String[] posCats = new String[] { "A", "B" };
        String[] negCats = new String[] { "B", "A" };
        ClassifierEvaluator<String,ScoredClassification> evaluator 
            = new ClassifierEvaluator<String,ScoredClassification>(null,posCats,false);
        for (int i = 0; i < 100; ++i) {
            double high = 0.5 + random.nextDouble()/2.0;
            double low = 1.0 - high;
            double[] scores = new double[] { high, low };

            boolean isPos = random.nextBoolean(); // true if correct answer is A
            String refCategory = isPos ? "A" : "B";

            boolean correct = random.nextDouble() < 0.8;  // true if answer is correct

            // isPos,correct:posCats, isPos,!correct:negCats, 
            // !isPos,correct:negCats, !isPos,!correct:posCats
            String[] cats = (isPos == correct) ? posCats : negCats;
            ScoredClassification classification = new ScoredClassification(cats,scores);
            evaluator.addClassification(refCategory,classification);
        }
        // System.out.println(evaluator);

        System.out.println("\nOne-V-All\n" + evaluator.oneVersusAll("B"));
        
        ScoredPrecisionRecallEvaluation ova
            = evaluator.scoredOneVersusAll("B");
        System.out.println("\nScored One-V-All\n" + ova);

        double[][] prCurve = ova.prCurve(false); // true for interpolated
        System.out.printf("\n%8s %8s\n","REC","PREC");
        for (double[] pr : prCurve) {
            System.out.printf("%8.6f %8.6f %8.6f\n", pr[0], pr[1], 
                              com.aliasi.classify.PrecisionRecallEvaluation.fMeasure(1.0,pr[0],pr[1]));
        }
        
    }

    @Test
    public void testSetClassifier() {
        MockClassifier classifier = new MockClassifier<Object,Classification>();
        ClassifierEvaluator evaluator = new ClassifierEvaluator(classifier,
                                                                CATS2);
        assertEquals(classifier,evaluator.classifier());
        MockClassifier classifier2 = new MockClassifier<Object,Classification>();
        evaluator.setClassifier(classifier2);
        
        assertEquals(classifier2,evaluator.classifier());
    }

    @Test
    public void testAdaptation() {
        MockClassifier classifier = new MockClassifier<Object,Classification>();
        ClassifierEvaluator evaluator = new ClassifierEvaluator(classifier,
                                                                CATS2);
        addJointCase(classifier,evaluator,"test0",
                     "foo",
                     "foo","bar","baz",
                     -1,-2,-3);

        assertEquals(-1.0,evaluator.averageLog2JointProbability("foo","foo"),
                     0.01);
        assertEquals(-1.0,evaluator.averageLog2JointProbabilityReference(),
                     0.01);
        // 0.57 = (1/2)/(1/2 + 1/4 + 1/8)
        assertEquals(0.57,evaluator.averageConditionalProbability("foo","foo"),
                     0.01);
    }

    @Test
    public void testScored() {
        MockClassifier classifier = new MockClassifier();
        ClassifierEvaluator evaluator = new ClassifierEvaluator(classifier,
                                                                CATS2);
        addScoredCase(classifier,evaluator,"obj0",
                      "foo",
                      "foo", "bar",
                      0.7, 0.3);
        addScoredCase(classifier,evaluator,"obj1",
                      "foo",
                      "foo", "bar",
                      0.8, 0.2);
        addScoredCase(classifier,evaluator,"obj2",
                      "foo",
                      "bar", "foo",
                      0.6, 0.4);
        addScoredCase(classifier,evaluator,"obj3",
                      "bar",
                      "bar", "foo",
                      0.7, 0.3);
        addScoredCase(classifier,evaluator,"obj4",
                      "bar",
                      "foo", "bar",
                      0.6, 0.4);

        assertEquals(0.63,
                     evaluator.averageScore("foo","foo"),
                     0.01);
        assertEquals(0.37,
                     evaluator.averageScore("foo","bar"),
                     0.01);

        assertEquals(0.45,
                     evaluator.averageScore("bar","foo"),
                     0.01);
        assertEquals(0.55,
                     evaluator.averageScore("bar","bar"),
                     0.01);
        assertEquals(0.60,
                     evaluator.averageScoreReference(),
                     0.01);

    }

    @Test(expected=IllegalArgumentException.class)
    public void testScoredExc() {
        MockClassifier classifier = new MockClassifier();
        ClassifierEvaluator evaluator = new ClassifierEvaluator(classifier,
                                                                CATS2);
        addScoredCase(classifier,evaluator,"obj0",
                      "foo",
                      "foo", "bar",
                      0.7, 0.3);
        addScoredCase(classifier,evaluator,"obj1",
                      "foo",
                      "foo", "bar",
                      0.8, 0.2);
        evaluator.averageScore("baz","baz");
    }

    @Test
    public void testConditional() {
        MockClassifier classifier = new MockClassifier();
        ClassifierEvaluator evaluator = new ClassifierEvaluator(classifier,
                                                                CATS2);
        addConditionalCase(classifier,evaluator,"obj0",
                           "foo",
                           "foo", "bar",
                           0.7, 0.3);
        addConditionalCase(classifier,evaluator,"obj1",
                           "foo",
                           "foo", "bar",
                           0.8, 0.2);
        addConditionalCase(classifier,evaluator,"obj2",
                           "foo",
                           "bar", "foo",
                           0.6, 0.4);
        addConditionalCase(classifier,evaluator,"obj3",
                           "bar",
                           "bar", "foo",
                           0.7, 0.3);
        addConditionalCase(classifier,evaluator,"obj4",
                           "bar",
                           "foo", "bar",
                           0.6, 0.4);

        assertEquals(0.63,
                     evaluator.averageConditionalProbability("foo","foo"),
                     0.01);
        assertEquals(0.37,
                     evaluator.averageConditionalProbability("foo","bar"),
                     0.01);

        assertEquals(0.45,
                     evaluator.averageConditionalProbability("bar","foo"),
                     0.01);
        assertEquals(0.55,
                     evaluator.averageConditionalProbability("bar","bar"),
                     0.01);

        assertEquals(0.60,
                     evaluator.averageConditionalProbabilityReference(),
                     0.01);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConditionalExc() {
        MockClassifier classifier = new MockClassifier();
        ClassifierEvaluator evaluator = new ClassifierEvaluator(classifier,
                                                                CATS2);
        addConditionalCase(classifier,evaluator,"obj0",
                           "foo",
                           "foo", "bar",
                           0.7, 0.3);
        addConditionalCase(classifier,evaluator,"obj1",
                           "foo",
                           "foo", "bar",
                           0.8, 0.2);
        addConditionalCase(classifier,evaluator,"obj2",
                           "foo",
                           "bar", "foo",
                           0.6, 0.4);
        addConditionalCase(classifier,evaluator,"obj3",
                           "bar",
                           "bar", "foo",
                           0.7, 0.3);
        evaluator.averageConditionalProbability("baz","bar");
    }

    @Test
    public void testJoint() {
        MockClassifier classifier = new MockClassifier();
        ClassifierEvaluator evaluator = new ClassifierEvaluator(classifier,
                                                                CATS);
        addJointCase(classifier,evaluator,"obj0",
                     "foo",
                     "foo", "bar", "baz",
                     -1, -2, -3);
        addJointCase(classifier,evaluator,"obj1",
                     "foo",
                     "foo", "baz", "bar",
                     -2, -3, -5);
        addJointCase(classifier,evaluator,"obj2",
                     "foo",
                     "bar", "foo", "baz",
                     -3, -4, -7);
        addJointCase(classifier,evaluator,"obj3",
                     "bar",
                     "bar", "foo", "baz",
                     -1, -2, -3);
        addJointCase(classifier,evaluator,"obj4",
                     "bar",
                     "foo", "baz", "bar",
                     -1, -2, -3);

        assertEquals(-11.0/5.0,
                     evaluator.averageLog2JointProbabilityReference(),
                     0.01);

        assertEquals(-7.0/3.0,
                     evaluator.averageLog2JointProbability("foo","foo"),
                     0.01);
        assertEquals(-10.0/3.0,
                     evaluator.averageLog2JointProbability("foo","bar"),
                     0.01);
        assertEquals(-13.0/3.0,
                     evaluator.averageLog2JointProbability("foo","baz"),
                     0.01);

        assertEquals(-3.0/2.0,
                     evaluator.averageLog2JointProbability("bar","foo"),
                     0.01);
        assertEquals(-4.0/2.0,
                     evaluator.averageLog2JointProbability("bar","bar"),
                     0.01);
        assertTrue(Double.isNaN(evaluator.
                                averageLog2JointProbability("baz","bar")));


    }


    @Test(expected=IllegalArgumentException.class)
    public void testIllegalArgExc1() {
        MockClassifier classifier = new MockClassifier();
        ClassifierEvaluator evaluator = new ClassifierEvaluator(classifier,
                                                                CATS);

        assertArrayEquals(CATS,evaluator.categories());

        addScoredCase(classifier,evaluator,"obj0",
                "bar",
                "foo","bar","baz",
                -1.21,-1.4,-1.7);
        addScoredCase(classifier,evaluator,"obj1",
                "foo",
                "foo","bar","baz",
                -1.27,-1.5,-1.9);
        addScoredCase(classifier,evaluator,"obj2",
                "bar",
                "bar","foo","baz",
                -1.1,-1.39,-1.5);
        addScoredCase(classifier,evaluator,"obj3",
                "foo",
                "foo","baz","bar",
                -1.47,-1.6,-2.5);
        addScoredCase(classifier,evaluator,"obj4",
                "foo",
                "bar","foo","baz",
                -1.2,-1.6,-2.3);

        evaluator.scoredOneVersusAll("Foo"); // caps don't match
    }



    @Test
    public void testOne() {
        MockClassifier classifier = new MockClassifier();
        ClassifierEvaluator evaluator = new ClassifierEvaluator(classifier,
                                                                CATS);

        assertArrayEquals(CATS,evaluator.categories());

        addScoredCase(classifier,evaluator,"obj0",
                "bar",
                "foo","bar","baz",
                -1.21,-1.4,-1.7);
        addScoredCase(classifier,evaluator,"obj1",
                "foo",
                "foo","bar","baz",
                -1.27,-1.5,-1.9);
        addScoredCase(classifier,evaluator,"obj2",
                "bar",
                "bar","foo","baz",
                -1.1,-1.39,-1.5);
        addScoredCase(classifier,evaluator,"obj3",
                "foo",
                "foo","baz","bar",
                -1.47,-1.6,-2.5);
        addScoredCase(classifier,evaluator,"obj4",
                "foo",
                "bar","foo","baz",
                -1.2,-1.6,-2.3);
        addScoredCase(classifier,evaluator,"obj5",
                "baz",
                "baz","foo","bar",
                -1.1,-1.65,-3.13);
        addScoredCase(classifier,evaluator,"obj6",
                "baz",
                "baz","bar","foo",
                -1.0,-1.1,-1.79);
        addScoredCase(classifier,evaluator,"obj7",
                "bar",
                "bar","baz","foo",
                -1.2,-1.3,-1.8);
        addScoredCase(classifier,evaluator,"obj8",
                "foo",
                "bar","baz","foo",
                -1.5,-1.6,-2.01);
        addScoredCase(classifier,evaluator,"obj9",
                "bar",
                "baz","bar","foo",
                -1.5,-1.6,-3.7);


        assertEquals(2,
                     evaluator.rankCount("foo",0));
        assertEquals(1,
                     evaluator.rankCount("foo",1));
        assertEquals(1,
                     evaluator.rankCount("foo",2));
        assertEquals(2,
                     evaluator.rankCount("bar",0));
        assertEquals(2,
                     evaluator.rankCount("bar",1));
        assertEquals(0,
                     evaluator.rankCount("bar",2));
        assertEquals(2,
                     evaluator.rankCount("baz",0));
        assertEquals(0,
                     evaluator.rankCount("baz",1));
        assertEquals(0,
                     evaluator.rankCount("baz",2));

        double mrr
            = 1.0/2.0
            + 1.0/1.0
            + 1.0/1.0
            + 1.0/1.0
            + 1.0/2.0
            + 1.0/1.0
            + 1.0/1.0
            + 1.0/1.0
            + 1.0/3.0
            + 1.0/2.0;
        assertEquals(mrr/10.0,evaluator.meanReciprocalRank(),0.001);


        assertEquals(10,evaluator.numCases());

        assertEquals(0.5,evaluator.averageRankReference(),0.01);
        assertEquals(0.5,evaluator.averageRank("bar","bar"),0.01);
        assertEquals(0.0,evaluator.averageRank("baz","baz"),0.01);
        assertEquals(0.75,evaluator.averageRank("foo","foo"),0.01);

        ScoredPrecisionRecallEvaluation fooEval
            = evaluator.scoredOneVersusAll("foo");

        double[][] prCurve = fooEval.prCurve(false);
        assertEquals(4,prCurve.length);
        assertEqualsArray(new double[] { 0.25, 0.50 },
                          prCurve[0], 0.01);
        assertEqualsArray(new double[] { 0.50, 0.50 },
                          prCurve[1], 0.01);
        assertEqualsArray(new double[] { 0.75, 0.60 },
                          prCurve[2], 0.01);
        assertEqualsArray(new double[] { 1.00, 0.44 },
                          prCurve[3], 0.01);

        assertEquals(0.51,fooEval.areaUnderPrCurve(false),0.01);

        assertEquals(0.51,fooEval.averagePrecision(),0.01);

        assertEquals(0.67,fooEval.maximumFMeasure(),0.01);

        assertEquals(0.60,fooEval.prBreakevenPoint(),0.01);

        double[][] interpolatedPrCurve = fooEval.prCurve(true);
        assertEquals(2,interpolatedPrCurve.length);
        assertEqualsArray(new double[] { 0.75, 0.60 },
                          interpolatedPrCurve[0], 0.01);
        assertEqualsArray(new double[] { 1.00, 0.44 },
                          interpolatedPrCurve[1], 0.01);

        assertEquals(0.56,fooEval.areaUnderPrCurve(true),0.01);



        double[][] rocCurve = fooEval.rocCurve(false);
        assertEquals(4,rocCurve.length);
        assertEqualsArray(new double[] { 0.25, 0.83 },
                          rocCurve[0], 0.01);
        assertEqualsArray(new double[] { 0.50, 0.67 },
                          rocCurve[1], 0.01);
        assertEqualsArray(new double[] { 0.75, 0.67 },
                          rocCurve[2], 0.01);
        assertEqualsArray(new double[] { 1.00, 0.17 },
                          rocCurve[3], 0.01);

        double[][] interpolatedRocCurve = fooEval.rocCurve(true);
        assertEquals(3,interpolatedRocCurve.length);
        assertEqualsArray(new double[] { 0.25, 0.83 },
                          interpolatedRocCurve[0], 0.01);
        assertEqualsArray(new double[] { 0.75, 0.67 },
                          interpolatedRocCurve[1], 0.01);
        assertEqualsArray(new double[] { 1.00, 0.17 },
                          interpolatedRocCurve[2], 0.01);

        assertEquals(0.51,fooEval.areaUnderPrCurve(false),0.01);
        assertEquals(0.56,fooEval.areaUnderPrCurve(true),0.01);

    }

    @Test
    public void testRanked() {
        MockClassifier<Integer,RankedClassification> classifier
            = new MockClassifier<Integer,RankedClassification>();
        classifier.put(Integer.valueOf(1),
                       new RankedClassification(new String[] { "a", "b", "c" }));
        classifier.put(Integer.valueOf(2),
                       new RankedClassification(new String[] { "c", "b", "a" }));
        classifier.put(Integer.valueOf(3),
                       new RankedClassification(new String[] { "c", "b" }));
        classifier.put(Integer.valueOf(4),
                       new RankedClassification(new String[] { "a" }));
        classifier.put(Integer.valueOf(5),
                       new RankedClassification(new String[] { "b", "a" }));
        ClassifierEvaluator evaluator
            = new ClassifierEvaluator(classifier,
                                      new String[] { "a", "b", "c" });
        evaluator.addCase("a", Integer.valueOf(1));
        evaluator.addCase("c", Integer.valueOf(2));
        evaluator.addCase("a", Integer.valueOf(3));
        evaluator.addCase("b", Integer.valueOf(4));
        evaluator.addCase("a", Integer.valueOf(5));

        assertNotNull(evaluator.confusionMatrix());
        assertEquals(5.0/5.0,evaluator.averageRankReference(),
                     0.001);
        assertEquals((1.0/1.0 + 1.0/1.0 + 1.0/3.0 + 1.0/3.0 + 1.0/2.0)/5.0,
                     evaluator.meanReciprocalRank(),
                     0.001);
        assertEquals(2.0/3.0,evaluator.averageRank("a","b"),
                     0.001);
        assertEquals(evaluator.rankCount("a",0),1);
        assertEquals(evaluator.rankCount("a",1),1);
        assertEquals(evaluator.rankCount("a",2),1);
        assertEquals(evaluator.rankCount("b",0),0);
        assertEquals(evaluator.rankCount("b",1),0);
        assertEquals(evaluator.rankCount("b",2),1);
        assertEquals(evaluator.rankCount("c",0),1);
        assertEquals(evaluator.rankCount("c",1),0);
        assertEquals(evaluator.rankCount("c",2),0);
        assertEquals(3.0/3.0,evaluator.averageRank("a","a"),0.001);
        assertEquals(2.0/3.0,evaluator.averageRank("a","b"),0.001);
        assertEquals(4.0/3.0,evaluator.averageRank("a","c"),0.001);
        assertEquals(0.0,evaluator.averageRank("b","a"),0.001);
        assertEquals(2.0,evaluator.averageRank("b","b"),0.001);
        assertEquals(2.0,evaluator.averageRank("b","c"),0.001);
        assertEquals(2.0,evaluator.averageRank("c","a"),0.001);
        assertEquals(1.0,evaluator.averageRank("c","b"),0.001);
        assertEquals(0.0,evaluator.averageRank("c","c"),0.001);

        assertNotNull(evaluator.toString());
    }

    static <E> void addScoredCase(MockClassifier<E,ScoredClassification> classifier,
                                  ClassifierEvaluator<E,ScoredClassification> evaluator,
                                  E input,
                                  String refCategory,
                                  String cat1, String cat2,
                                  double cond1, double cond2) {
        String[] cats = new String[] { cat1, cat2 };
        double[] conds = new double[] { cond1, cond2 };
        classifier.put(input,
                       new ScoredClassification(cats,conds));
        evaluator.addCase(refCategory,input);
    }

    static <E> void addScoredCase(MockClassifier<E,ScoredClassification> classifier,
                                  ClassifierEvaluator<E,ScoredClassification> evaluator,
                                  E input,
                                  String refCat,
                                  String cat1, String cat2, String cat3,
                                  double score1, double score2, double score3) {
        String[] cats = new String[] { cat1, cat2, cat3 };
        double[] scores = new double[] { score1, score2, score3 };
        classifier.put(input,
                       new ScoredClassification(cats,scores));
        evaluator.addCase(refCat,input);
    }

    static <E> void addConditionalCase(MockClassifier<E,ConditionalClassification> classifier,
                                       ClassifierEvaluator<E,ConditionalClassification> evaluator,
                                       E input,
                                       String refCategory,
                                       String cat1, String cat2,
                                       double cond1, double cond2) {
        String[] cats = new String[] { cat1, cat2 };
        double[] conds = new double[] { cond1, cond2 };
        classifier.put(input,
                       new ConditionalClassification(cats,conds));
        evaluator.addCase(refCategory,input);
    }

    static <E> void addConditionalCase(MockClassifier<E,ConditionalClassification> classifier,
                                       ClassifierEvaluator<E,ConditionalClassification> evaluator,
                                       E input,
                                       String refCategory,
                                       String cat1, String cat2, String cat3,
                                       double cond1, double cond2) {
        String[] cats = new String[] { cat1, cat2, cat3 };
        double cond3 = 1 - cond1 - cond2;
        double[] conds = new double[] { cond1, cond2, cond3 };
        classifier.put(input,
                       new ConditionalClassification(cats,conds));
        evaluator.addCase(refCategory,input);
    }

    static <E> void addJointCase(MockClassifier<E,JointClassification> classifier,
                                 ClassifierEvaluator<E,JointClassification> evaluator,
                                 E input,
                                 String refCategory,
                                 String cat1, String cat2, String cat3,
                                 double joint1, double joint2, double joint3) {
        String[] cats = new String[] { cat1, cat2, cat3 };
        double[] joints = new double[] { joint1, joint2, joint3 };
        classifier.put(input,
                       new JointClassification(cats,joints));
        evaluator.addCase(refCategory,input);
    }



    @Test(expected=UnsupportedOperationException.class)
    public void testScoreCasesException() {
        MockClassifier<Integer,ConditionalClassification> classifier
            = new MockClassifier<Integer,ConditionalClassification>();
        ClassifierEvaluator<Integer,ConditionalClassification> evaluator
            = new ClassifierEvaluator<Integer,ConditionalClassification>(classifier,
                                                                         new String[] { "a", "b", "c" },
                                                                         false);
        evaluator.truePositives("b");
    }

    @Test
    public void testStoreCases() {
        MockClassifier<Integer,ConditionalClassification> classifier
            = new MockClassifier<Integer,ConditionalClassification>();
        ClassifierEvaluator<Integer,ConditionalClassification> evaluator
            = new ClassifierEvaluator<Integer,ConditionalClassification>(classifier,
                                                                        new String[] { "a", "b", "c" },
                                                                        true);
        // TP
        addConditionalCase(classifier,evaluator,
                           1,"a",
                           "a", "b", "c",
                           0.51, 0.31);

        addConditionalCase(classifier,evaluator,
                           2,"a",
                           "a","c","b",
                           0.53,0.33);

        addConditionalCase(classifier,evaluator,
                           3,"a",
                           "a","c","b",
                           0.52,0.32);

        // FN
        addConditionalCase(classifier,evaluator,
                           4,"a",
                           "b","c","a",
                           0.54,0.34);

        // FP
        addConditionalCase(classifier,evaluator,
                           5,"b",
                           "a","b","c",
                           0.55,0.35);
        addConditionalCase(classifier,evaluator,
                           6,"c",
                           "a","c","b",
                           0.56,0.36);

        // TN
        addConditionalCase(classifier,evaluator,
                           7,"c",
                           "b","c","a",
                           0.57,0.37);
        addConditionalCase(classifier,evaluator,
                           8,"b",
                           "c","a","b",
                           0.58,0.38);
        addConditionalCase(classifier,evaluator,
                           9,"c",
                           "b","a","c",
                           0.59,0.39);

        assertEquals(3,evaluator.truePositives("a").size());
        assertEquals(1,evaluator.falseNegatives("a").size());
        assertEquals(2,evaluator.falsePositives("a").size());
        assertEquals(3,evaluator.trueNegatives("a").size());

        assertExpectedItems(new int[] { 2, 3, 1 },
                            evaluator.truePositives("a"));
        assertExpectedItems(new int[] { 4 },
                            evaluator.falseNegatives("a"));
        assertExpectedItems(new int[] { 6, 5 },
                            evaluator.falsePositives("a"));
        assertExpectedItems(new int[] { 9, 8, 7 },
                            evaluator.trueNegatives("a"));
    }

    <E,C extends ScoredClassification> void
                 assertExpectedItems(int[] xs, List<Pair<E,C>> cases) {
        assertEquals(xs.length,cases.size());
        for (int i = 0; i < xs.length; ++i)
            assertEquals(xs[i],cases.get(i).a());
    }



    static class MockClassifier<E,C extends Classification>
        extends HashMap<E,C>
        implements Classifier<E,C> {

        public C classify(E input) {
            return get(input);
        }
    }

}

