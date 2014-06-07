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

import java.util.Locale;

/**
 * A <code>LowerCaseFilterTokenizer</code> renders all of its
 * tokens in lower case as defined by {@link String#toLowerCase()}.
 * The scheme for lower-casing is determined by a {@link Locale}.
 *
 * @author  Bob Carpenter
 * @version 3.8
 * @since   LingPipe1.0
 * @deprecated Use {@link LowerCaseTokenizerFactory} instead.
 */
@Deprecated
public class LowerCaseFilterTokenizer extends TokenFilterTokenizer {


    private final Locale mLocale;

    /**
     * Construct a new lower case filter tokenizer with a default
     * locale of {@link Locale#ENGLISH}.
     *
     * @param tokenizer Contained tokenizer.
     * @deprecated Use {@link LowerCaseTokenizerFactory} or {@link
     * LowerCaseTokenizerFactory#modify(Tokenizer)} instead.
     */
    @Deprecated
    public LowerCaseFilterTokenizer(Tokenizer tokenizer) {
        this(tokenizer,Locale.ENGLISH);
    }

    /**
     * Construct a lower case filter tokenizer with the specified
     * tokenizer using the locale-specific lower-casing rules.
     *
     * @param tokenizer Contained tokenizer.
     * @param locale Locale to use for lower-casing.
     * @deprecated Use {@link LowerCaseTokenizerFactory} or {@link
     * LowerCaseTokenizerFactory#modify(Tokenizer)} instead.
     */
    @Deprecated
    public LowerCaseFilterTokenizer(Tokenizer tokenizer, Locale locale) {
        super(tokenizer);
        mLocale = locale;
    }

    /**
     * Returns the next token from the contained tokenizer,
     * converted to lower case.
     *
     * @param token Token to convert to lower case.
     * @return Lower-cased version of specified token.
     */
    @Override
    public String filter(String token) {
        return token.toLowerCase(mLocale);
    }


    /**
     * Returns the string representation of this tokenizer, including
     * the programmatic name of its entire locale.
     *
     * @return The string representation of this tokenizer.
     */
    @Override public String toString() {
    return getClass().getName() + "("
            + "locale=" + mLocale
            + ")";
    }

}
