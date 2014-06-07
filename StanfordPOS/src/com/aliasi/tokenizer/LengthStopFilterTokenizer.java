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


/**
 * A <code>StopFilterTokenizer</code> removes tokens that exceed a
 * specified length.
 *
 * @author Bob Carpenter
 * @version 3.8
 * @since   LingPipe3.6
 * @deprecated Use {@link TokenLengthTokenizerFactory} or {@link
 * TokenLengthTokenizerFactory#modify(Tokenizer)} instead.
 */
@Deprecated
public class LengthStopFilterTokenizer
    extends StopFilterTokenizer {

    private final int mMaxTokenLength;

    /**
     * Construct a length filtering tokenizer with the specified
     * maximum length.
     *
     * @param maxTokenLength Maximum token length that is accepted by
     * the filter.
     * @deprecated Use {@link
     * TokenLengthTokenizerFactory#modify(Tokenizer)} instead.
     */
    @Deprecated
    public LengthStopFilterTokenizer(Tokenizer tokenizer, int maxTokenLength) {
        super(tokenizer);
        mMaxTokenLength = maxTokenLength;
    }

    /**
     * Returns the maximum token length for this filter.
     *
     * @return The maximum token length for this filter.
     */
    public int maxTokenLength() {
        return mMaxTokenLength;
    }

    /**
     * Returns <code>true</code> if the specified token exceeds
     * the maximum length.
     *
     * @param token Token to filter.
     * @return <code>true</code> if the specified token exceeds
     * the maximum length.
     */
    @Override
    public boolean stop(String token) {
        return token.length() > mMaxTokenLength;
    }

    /**
     * Returns a string representation of this tokenizer including
     * the class and maximum token length.
     *
     * @return A string representation of this tokenizer.
     */
    @Override public String toString() {
    return getClass().getName() + "("
            + "maxTokenLength=" + maxTokenLength()
            + ")";
    }

}
