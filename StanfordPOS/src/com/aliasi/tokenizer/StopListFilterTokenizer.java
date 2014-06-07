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

import java.util.HashSet;
import java.util.Set;

/**
 * A <code>StopListFilterTokenizer</code> is a stop-list-based stop
 * filter tokenizer that removes tokens from a tokenizer stream if
 * they are on a specified list of so-called ``stop'' tokens.
 *
 * @author Bob Carpenter
 * @version 3.8
 * @since   LingPipe1.0
 * @deprecated Use {@link StopTokenizerFactory} instead.
 */
@Deprecated
public class StopListFilterTokenizer extends StopFilterTokenizer {

    /**
     * The set of tokens to filter out.
     */
    private final Set<String> mStopList;

    /**
     * Construct a stop-list-based stop filter tokenizer based
     * on the specified set of stop tokens.
     *
     * <p>Keeps a handle on the set, so that changes to the set will
     * affect this stop list.
     *
     * @param stopList Set of tokens to remove from token streams.
     * @deprecated Use {@link StopTokenizerFactory} instead.
     */
    @Deprecated
    public StopListFilterTokenizer(Tokenizer tokenizer, Set<String> stopList) {
        super(tokenizer);
        mStopList = stopList;
    }

    /**
     * Returns <code>true</code> if the specified token should
     * be ignored.
     *
     * @param token Token to test for removal.
     * @return <code>true</code> if the token should be removed from
     * the stream of tokens.
     */
    @Override
    public boolean stop(String token) {
        return mStopList.contains(token);
    }


}
