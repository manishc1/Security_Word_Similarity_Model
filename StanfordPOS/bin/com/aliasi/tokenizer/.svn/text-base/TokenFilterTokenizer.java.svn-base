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
 * A <code>TokenFilterTokenizer</code> allows a sequence of tokens to
 * be filtered a token at a time.  Each token in the input corresponds
 * to one token in the output, with the transform being implemented in
 * a subclass with the method {@link #filter(String)}.
 *
 * @author Bob Carpenter
 * @version 3.8
 * @since   LingPipe1.0
 * @deprecated Use {@link ModifyTokenTokenizerFactory} instead.
 */
@Deprecated
public abstract class TokenFilterTokenizer extends FilterTokenizer {

    /**
     * Construct a token filter tokenizer that filters the
     * specified tokenizer.
     *
     * @param tokenizer Underlying tokenizer to filter.
     */
    public TokenFilterTokenizer(Tokenizer tokenizer) {
        super(tokenizer);
    }

    /**
     * Returns the filtered form of the next token produced by the
     * contained tokenizer.  If the next token returned by the tokenizer
     * is {@code null}, it is not passed through the filter, but
     * returned as-is.
     *
     * @return Output of <code>filter</code> applied to next token
     * from filtered tokenizer.
     */
    @Override public String nextToken() {
        String token = baseTokenizer().nextToken();
        if (token == null) return null;
        return filter(token);
    }

    /**
     * Returns a filtered version of the specified token.
     *
     * @param token Input token.
     * @return Output token after filtering.
     */
    public abstract String filter(String token);


}
