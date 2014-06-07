package com.aliasi.test.unit.corpus.parsers;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


import com.aliasi.classify.Classification;


import com.aliasi.corpus.ClassificationHandler;
import com.aliasi.corpus.parsers.SvmLightClassificationParser;

import com.aliasi.matrix.SparseFloatVector;
import com.aliasi.matrix.Vector;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class SvmLightClassificationParserTest  {

    static String LINE0 = "1 1:1 2:2 # foo";
    static String LINE1 = "-1 5:4 0:1 5:2 3:7";
    static String LINE2 = "1 179:1# bar";

    static String INPUT1 
        = LINE0 + "\n" + LINE1 + "\n" + LINE2;

    static Vector V0 = new SparseFloatVector(new int[] { 1, 2 },
                                             new float[] { 1, 2 },
                                             1000);
    static Vector V1 = new SparseFloatVector(new int[] { 0, 3, 5 },
                                             new float[] { 1, 7, 6 },
                                             1000);
    static Vector V2 = new SparseFloatVector(new int[] { 179 },
                                             new float[] { 1 },
                                             1000);
    static Classification C0 = new Classification("1");
    static Classification C1 = new Classification("-1");
    static Classification C2 = new Classification("1");

    static String LINE3 = "foo 0:1 ";

    @Test
    public void testOne() throws IOException {
        SvmLightClassificationParser parser 
            = new SvmLightClassificationParser(false,1000);
        Collector c = new Collector();
        parser.setHandler(c);
        parser.parseString(INPUT1);
        assertEquals(V0,c.mInputList.get(0));
        assertEquals(V1,c.mInputList.get(1));
        assertEquals(V2,c.mInputList.get(2));
        assertEquals(C0.bestCategory(),c.mClassificationList.get(0).bestCategory());
        assertEquals(C1.bestCategory(),c.mClassificationList.get(1).bestCategory());
        assertEquals(C2.bestCategory(),c.mClassificationList.get(2).bestCategory());
        assertEquals(parser.categoriesFound().size(),2);
        assertTrue(parser.categoriesFound().contains("-1"));
        assertTrue(parser.categoriesFound().contains("1"));
    }

    static class Collector implements ClassificationHandler<Vector,Classification> {
        List<Vector> mInputList = new ArrayList<Vector>();
        List<Classification> mClassificationList = new ArrayList<Classification>();
        public void handle(Vector v, Classification c) {
            mInputList.add(v);
            mClassificationList.add(c);
        }
    }

}
