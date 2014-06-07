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

package com.aliasi.tokenizer;

import com.aliasi.util.AbstractExternalizable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;


/**
 * A <code>PorterStemmerFilterTokenizer</code> returns the stemmed
 * version of each token, as produced by {@link
 * PorterStemmer#stem(String)}.
 *
 * @author Bob Carpenter
 * @version 3.8
 * @since   LingPipe1.0
 * @deprecated Use {@link PorterStemmerTokenizerFactory} instead.
 */
@Deprecated
public class PorterStemmerFilterTokenizer extends TokenFilterTokenizer {

    /**
     * Construct a Porter stemmer filter tokenizer containing
     * the specified tokenizer.
     *
     * @param tokenizer Contained tokenizer.
     */
    @Deprecated
    public PorterStemmerFilterTokenizer(Tokenizer tokenizer) {
        super(tokenizer);
    }

    /**
     * Returns the stemmed form of the specified token.
     *
     * @param token Token to stem.
     * @return Stemmed version of specified token.
     */
    @Override
    public String filter(String token) {
        return PorterStemmerTokenizerFactory.stem(token);
    }


}



