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

package com.aliasi.util;

import java.util.Collection;

/**
 * A <code>PriorityQueue</code> is an ordered collection that supports
 * peek and pop methods to inspect and return the greatest element in
 * the queue.  Iterators for priority queues return the elements
 * ordered from largest to smallest.  Priority is typically defined by
 * means of a comparator.
 *
 * <P>The ordering on which a priority queue is based need not be
 * consistent with equals (see {@link java.util.Comparator}).
 * Priority queues behave like sets with respect to natural object
 * equality.  Thus no two elements in the queue will be equal to each
 * other as defined by {@link Object#equals(Object)}.  Priority queues
 * behave like bags or multisets with respect to the priority ordering
 * of objects.  Thus it is acceptable for the priority ordering {@link
 * java.util.Comparator#compare(Object,Object)} to return <code>0</code> for
 * objects which are not naturally equal.
 *
 * @author  Bob Carpenter
 * @version 3.8
 * @since   LingPipe2.0
 * @see java.util.Collection
 * @see java.util.Comparator
 * @param <E> the type of objects stored in the queue
 * @deprecated Use one of the implementations in applications,
 * or {@link java.util.Queue} for the interface.
 */
@Deprecated
public interface PriorityQueue<E> extends Collection<E> {

    /**
     * Remove and return the largest element in the queue or
     * return <code>null</code> if the queue is empty.
     *
     * @return The largest element in the queue.
     */
    public E pop();

    /**
     * Returns the largest element in the queue or <code>null</code>
     * if the queue is empty.
     *
     * @return The largest element in the queue or <code>null</code>
     * if the queue is empty.
     */
    public E peek();

}
