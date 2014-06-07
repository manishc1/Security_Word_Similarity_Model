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
 * An <code>EnglishStopListFilterTokenizer</code> filters its input by
 * removing words on the English stop list.  The stoplist is:
 *
 * <blockquote>
 *   a, be, had, it, only, she, was, about, because, has,
 *   its, of, some, we, after, been, have, last, on, such, were, all,
 *   but, he, more, one, than, when, also, by, her, most, or, that,
 *   which, an, can, his, mr, other, the, who, any, co, if, mrs, out,
 *   their, will, and, corp, in, ms, over, there, with, are, could, inc,
 *   mz, s, they, would, as, for, into, no, so, this, up, at, from, is,
 *   not, says, to
 * </blockquote>
 *
 * Note that the stoplist entries are all lowercase.  The input should
 * first be filtered by a {@link LowerCaseFilterTokenizer}.
 *
 * @author  Bob Carpenter
 * @version 3.8
 * @since   LingPipe1.0
 * @deprecated Use {@link EnglishStopTokenizerFactory} instead.
 */
@Deprecated
public class EnglishStopListFilterTokenizer extends StopListFilterTokenizer {

    /**
     * Construct an English stoplist filter tokenizer.
     *
     * @deprecated Use {@link EnglishStopTokenizerFactory} or {@link
     * EnglishStopTokenizerFactory#modify(Tokenizer)} instead.
     */
    @Deprecated
    public EnglishStopListFilterTokenizer(Tokenizer tokenizer) {
        super(tokenizer,EnglishStopTokenizerFactory.STOP_SET);
    }



}
