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

package com.aliasi.corpus;

/**
 * The <code>IntArrayHandler</code> interface supplies a single method
 * taking an array of integers as an argument.
 *
 * @author  Bob Carpenter
 * @version 3.9
 * @since   LingPipe2.2
 * @deprecated Use {@code ObjectHandler<int[]>} instead.
 */
@Deprecated
public interface IntArrayHandler extends ObjectHandler<int[]> {

    /**
     * Handle the specified array of integers.
     *
     * @param xs Array of integers to handle.
     */
    public void handle(int[] xs);

}
