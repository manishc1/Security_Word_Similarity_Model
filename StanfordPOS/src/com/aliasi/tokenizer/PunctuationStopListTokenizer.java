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
import com.aliasi.util.Strings;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
/**
 * A <code>PunctuationStopListTokenizer</code> removes tokens consisting
 * entirely of punctuation.  Punctuation is defined by the method {@link
 * com.aliasi.util.Strings#allPunctuation(String)}.
 *
 * @author  Bob Carpenter
 * @version 3.8
 * @since   LingPipe3.3
 * @deprecated Use {@link RegExFilteredTokenizerFactory} with a
 * pattern matching the characters specified in {@link
 * Strings#allPunctuation(String)}.
 */
@Deprecated
public class PunctuationStopListTokenizer extends StopFilterTokenizer {


    /**
     * Construct a punctuation stop-list tokenizer.
     *
     * @param tokenizer Tokenizer to filter.
     * @deprecated Use {@link RegExFilteredTokenizerFactory#RegExFilteredTokenizerFactory(TokenizerFactory,java.util.regex.Pattern)} instead.
     */
    @Deprecated
    public PunctuationStopListTokenizer(Tokenizer tokenizer) {
        super(tokenizer);
    }

    /**
     * Returns <code>true</code> if the specified token consists
     * entirely of punctuation.
     *
     * @param token Token to test for removal.
     * @return <code>true</code> if the token is all punctuation.
     */
    @Override 
    public boolean stop(String token) {
        return Strings.allPunctuation(token);
    }

}
