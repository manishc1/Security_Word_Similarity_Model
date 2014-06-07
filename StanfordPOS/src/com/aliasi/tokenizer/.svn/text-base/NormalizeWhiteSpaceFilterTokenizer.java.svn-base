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
 * A <code>NormalizeWhiteSpaceFilterTokenizer</code> reduces each
 * non-empty whitespace to a single space, leaving empty whitespaces
 * alone.
 *
 * @author Bob Carpenter
 * @version 3.8
 * @since   LingPipe1.0
 * @deprecated Use {@link WhitespaceNormTokenizerFactory} instead.
 */
@Deprecated
public class NormalizeWhiteSpaceFilterTokenizer extends FilterTokenizer {

    /**
     * Construct a filter tokenizer that normalizes whitespace,
     * using the specified contained tokenizer.
     *
     * @param tokenizer Contained tokenizer.
     * @deprecated Use {@link WhitespaceNormTokenizerFactory} or
     * {@link WhitespaceNormTokenizerFactory#modify(Tokenizer)}
     * instead.
     */
    @Deprecated
    public NormalizeWhiteSpaceFilterTokenizer(Tokenizer tokenizer) {
        super(tokenizer);
    }

    /**
     * Returns the next whitespace, which will either be
     * the single space string {@link Strings#SINGLE_SPACE_STRING}
     * or the empty string {@link Strings#EMPTY_STRING}.
     *
     * @return Next whitespace.
     */
    @Override
    public String nextWhitespace() {
        return baseTokenizer().nextWhitespace().length() > 0
            ? Strings.SINGLE_SPACE_STRING
            : Strings.EMPTY_STRING;
    }

}
