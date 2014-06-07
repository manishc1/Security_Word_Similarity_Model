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
 * A <code>FilterTokenizer</code> contains a tokenizer to
 * which it delegates the tokenizer methods.
 *
 * @author  Bob Carpenter
 * @version 3.8.1
 * @since   LingPipe1.0
 * @deprecated Use {@link ModifiedTokenizerFactory} instead.
 */
@Deprecated
public class FilterTokenizer extends Tokenizer {

    /**
     * The contained tokenizer.
     * @deprecated Use the method {@link #baseTokenizer()} for reads
     * and create a new instance of {@link FilterTokenizer} itself for
     * different values.
     */
    @Deprecated
    protected Tokenizer mTokenizer;

    /**
     * Construct a filter tokenizer that contains the specified
     * tokenizer.
     *
     * @param tokenizer Contained tokenizer.
     */
    public FilterTokenizer(Tokenizer tokenizer) {
        mTokenizer = tokenizer;
    }

    /**
     * Returns the base tokenizer underlying this filtered tokenizer.
     *
     * @return The base tokenizer.
     */
    public Tokenizer baseTokenizer() {
        return mTokenizer;
    }

    /**
     * Sets the contained tokenizer to the specified tokenizer.
     *
     * @param tokenizer New contained tokenizer.
     * @deprecated Create a new immutable instance of {@link
     * FilterTokenizer} instead.
     */
    @Deprecated
    public void setTokenizer(Tokenizer tokenizer) {
        mTokenizer = tokenizer;
    }

    /**
     * Returns the next token from this tokenizer.  The
     * method is delegated to the contained tokenizer.
     *
     * @return Next token from this tokenizer.
     */
    @Override
    public String nextToken() {
        return mTokenizer.nextToken();
    }

    /**
     * Returns the next white space from this tokenizer.  The method
     * is delegated to the contained tokenizer.
     *
     * @return Next white space from this tokenizer.
     */
    @Override
    public String nextWhitespace() {
        return mTokenizer.nextWhitespace();
    }

    /**
     * Returns the starting index of the last token returned.  The
     * method is delegated to the contained tokenizer.
     *
     * @return Starting index of last token in sequence.
     */
    @Override
    public int lastTokenStartPosition() {
        return mTokenizer.lastTokenStartPosition();
    }

    /**
     * Returns the end position of the last token returned.  The end
     * is one past the last character in the token.  The method is
     * delegated to the contained tokenizer.
     *
     * @return Starting index of last token in sequence.
     */
    @Override
    public int lastTokenEndPosition() {
        return mTokenizer.lastTokenEndPosition();
    }

    /**
     * Returns a string representation of this tokenizer including the
     * class name and representation of the contained tokenizer.  This
     * does not include the characters or current position.
     *
     * @return A string representation of this tokenizer.
     */
    @Override public String toString() {
        return getClass().getName() + "(" +  mTokenizer + ")";
    }


}
