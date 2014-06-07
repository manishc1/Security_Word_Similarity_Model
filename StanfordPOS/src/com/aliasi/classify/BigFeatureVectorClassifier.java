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

import com.aliasi.features.Features;

import com.aliasi.matrix.SparseFloatVector;
import com.aliasi.matrix.Vector;

import com.aliasi.symbol.MapSymbolTable;
import com.aliasi.symbol.SymbolTable;

import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.FeatureExtractor;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import java.util.Map;

/**
 *
 * @author  Bob Carpenter
 * @version 3.9
 * @since   LingPipe3.9
 * @param <E> Type of object being classified.
 */
public class BigFeatureVectorClassifier<E>
    implements Classifier<E,ScoredClassification>,
               Serializable {

    static final long serialVersionUID = 5553460448330675312L;

    private final FeatureExtractor<E> mFeatureExtractor;
    private final SymbolTable mFeatureSymbolTable;
    private final boolean mAddIntercept;
    private final BigVectorClassifier mClassifier;

    public BigFeatureVectorClassifier(FeatureExtractor<E> featureExtractor,
                                      SymbolTable featureSymbolTable,
                                      boolean addIntercept,
                                      Vector[] termVectors,
                                      String[] categories,
                                      int maxResults) {
        this(featureExtractor,featureSymbolTable,addIntercept,
             new BigVectorClassifier(termVectors,categories,maxResults));
    }

    BigFeatureVectorClassifier(FeatureExtractor<E> featureExtractor,
                               SymbolTable featureSymbolTable,
                               boolean addIntercept,
                               BigVectorClassifier classifier) {
        mFeatureExtractor = featureExtractor;
        mFeatureSymbolTable = featureSymbolTable;
        mAddIntercept = addIntercept;
        mClassifier = classifier;
    }

    public FeatureExtractor<E> featureExtractor() {
        return mFeatureExtractor;
    }

    public SymbolTable featureSymbolTable() {
        return MapSymbolTable.unmodifiableView(mFeatureSymbolTable);
    }

    public boolean addIntercept() {
        return mAddIntercept;
    }

    public int maxResults() {
        return mClassifier.maxResults();
    }

    public void setMaxResults(int maxResults) {
        mClassifier.setMaxResults(maxResults);
    }

    public ScoredClassification classify(E input) {
        Map<String,? extends Number> featureVector
            = mFeatureExtractor.features(input);
        Vector v = Features.toVector(featureVector,mFeatureSymbolTable,
                                     Integer.MAX_VALUE,mAddIntercept);
        return mClassifier.classify(v);
    }

    Object writeReplace() {
        return new Serializer<E>(this);
    }

    static class Serializer<F> extends AbstractExternalizable {
        static final long serialVersionUID = -1928944057303743192L;
        private final BigFeatureVectorClassifier<F> mClassifier;
        public Serializer() {
            this(null);
        }
        public Serializer(BigFeatureVectorClassifier<F> classifier) {
            mClassifier = classifier;
        }
        @Override
        public void writeExternal(ObjectOutput objOut)
            throws IOException {

            objOut.writeObject(mClassifier.mFeatureExtractor);
            objOut.writeObject(mClassifier.mFeatureSymbolTable);
            objOut.writeBoolean(mClassifier.mAddIntercept);
            objOut.writeObject(mClassifier.mClassifier);
        }
        @Override
        public Object read(ObjectInput objIn)
            throws ClassNotFoundException, IOException {

            @SuppressWarnings("unchecked")
            FeatureExtractor<F> featureExtractor
                = (FeatureExtractor<F>) objIn.readObject();
            SymbolTable symbolTable
                = (SymbolTable) objIn.readObject();
            boolean addIntercept = objIn.readBoolean();
            BigVectorClassifier classifier
                = (BigVectorClassifier) objIn.readObject();
            return new BigFeatureVectorClassifier<F>(featureExtractor,
                                                     symbolTable,
                                                     addIntercept,
                                                     classifier);

        }
    }


}