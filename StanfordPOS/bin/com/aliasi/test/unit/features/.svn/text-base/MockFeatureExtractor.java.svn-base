package com.aliasi.test.unit.features;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;

import static junit.framework.Assert.assertEquals;

import java.util.Map;

import com.aliasi.util.FeatureExtractor;

public class MockFeatureExtractor
    extends SerializableMockFeatureExtractor {

    private void writeObject(ObjectOutputStream out) throws IOException {
        throw new NotSerializableException();
    }

    public static <E> void assertFeats(FeatureExtractor<E> fe,
                                       E in,
                                       String[] feats,
                                       double[] vals) {
        Map<String,? extends Number> featVec = fe.features(in);
        assertEquals(feats.length,vals.length);
        assertEquals(featVec.size(),feats.length);
        for (int i = 0; i < feats.length; ++i)
            assertEquals(vals[i],featVec.get(feats[i]).doubleValue(),0.0001);
    }

}
    
