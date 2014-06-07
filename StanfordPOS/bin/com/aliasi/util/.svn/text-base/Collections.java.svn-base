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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Static utility methods for processing collections.
 *
 * @author  Bob Carpenter
 * @version 3.8
 * @since   LingPipe1.0
 */
public class Collections {

    /**
     * Forbid instance construction.
     */
    private Collections() {
        /* no instances */
    }

    /**
     * Returns <code>true</code> if the specified collection contains
     * exactly one member.
     *
     * @param c Collection to test.
     * @return <code>true</code> if the specified collection contains
     * exactly one member.
     * @deprecated Use {@code c.size() == 1} instead.
     */
    @Deprecated
    public static boolean isSingleton(Collection<?> c) {
        return c.size() == 1;
    }

    /**
     * Returns the first member of the specified list.
     *
     * @param l List whose first member is returned.
     * @return First member of the specified list.
     * @throws IndexOutOfBoundsException If the list is empty.
     * @param <E> type of objects in the list
     * @deprecated Use {@code l.get(0)} instead.
     */
    @Deprecated
    public static <E> E getFirst(List<? extends E> l) {
        return l.get(0);
    }

    /**
     * Returns the first member of the specified set.
     *
     * @param s Set whose first member is returned.
     * @return First member of the specified set.
     * @throws NoSuchElementException If the set is empty.
     * @param <E> type of objects in the list
     */
    public static <E> E getFirst(Set<? extends E> s) {
        return s.iterator().next();
    }

    /**
     * Returns <code>true</code> if the specified sets have at least one
     * element in common.
     *
     * @param set1 First set.
     * @param set2 Second set.
     * @return <code>true</code> if the specified sets have at least
     * one element in common.
     * @deprecated Use {@code !java.util.Collections.disjoint(set1,set2)}.
     */
    @Deprecated
    public static boolean intersects(Set<?> set1, Set<?> set2) {
        for (Object x : set1)
            if (set2.contains(x))
                return true;
        return false;
    }

    /**
     * Adds the elements from the specified array to the
     * specified collection.
     *
     * @param c Collection to which objects are added.
     * @param xs Objects to add to the collection.
     * @param <E> type of objects in the collection
     * @deprecated Use {@code java.util.Collections.addAll(Collection,Object[])} instead.
    */
    @Deprecated
    public static <E> void addAll(Collection<? super E> c, E[] xs) {
        for (int i = 0; i < xs.length; ++i)
            c.add(xs[i]);
    }


    /**
     * Returns the elements in the specified collection as an array
     * after converting each to a string.  The strings in the array
     * will be ordered as by the collection's iterator.
     *
     * @param c Collection to convert to an array.
     * @return Elements of specified collection as an array of
     * strings.
     */
    public static String[] toStringArray(Collection<?> c) {
        String[] result = new String[c.size()];
        toStringArray(c,result);
        return result;
    }

    /**
     * Writes the elements in the specified collection into the
     * specified array as strings, beginning with the first position
     * of the array.  The elements in the array will be ordered as by
     * the collection's iterator.  The array may be longer than the
     * collection, in which case nothing is done to the remaining
     * members of the array.
     *
     * @param c Collection to convert to an array.
     * @param members String rray to write collection into.
     * @throws IndexOutOfBoundsException If the size of the collection
     * is greater than the length of the array.
     */
    public static void toStringArray(Collection<?> c, String[] members) {
        Iterator<?> it = c.iterator();
        for (int i = 0; it.hasNext() && i < members.length; ++i) {
            Object obj = it.next();
            members[i] = obj==null ? "null" : obj.toString();
        }
    }

    /**
     * Returns an array of <code>int</code> consisting
     * of the elements of the specified collection converted
     * to int.
     *
     * @param cs Collection of integers to convert to ints.
     * @return Array of ints derived from collection of integers.
     */
    public static int[] toIntArray(Collection<? extends Number> cs) {
        int[] result = new int[cs.size()];
        Iterator<? extends Number> it = cs.iterator();
        for (int i = 0; it.hasNext(); ++i)
            result[i] = it.next().intValue();
        return result;
    }

    /**
     * Returns a string-based representation of the specified set.
     *
     * @param s Set to convert to string.
     * @return String-based representation of the specified set.
     */
    public static String setToString(Set<?> s) {
        StringBuilder sb = new StringBuilder();
        setToStringBuilder(sb,s);
        return sb.toString();
    }

    /**
     * Returns a string-based representation of the specified list.
     *
     * @param ls List to convert to string.
     * @return String-based representation of the specified list.
     * @deprecated Use {@code ls.toString()} instead.
     */
    @Deprecated
    public static String listToString(List<?> ls) {
        StringBuilder sb = new StringBuilder();
        listToStringBuilder(sb,ls);
        return sb.toString();
    }

    /**
     * Appends a string-based representation of the specified list
     * to the specified string buffer.
     *
     * @param sb String buffer to which the representation is appended.
     * @param ls List to append as a string.
     * @deprecated Use {@code sb.append(ls.toString())}.
     */
    @Deprecated
    public static void listToStringBuilder(StringBuilder sb, List<?> ls) {
        sb.append('<');
        elementsToStringBuilder(sb,ls);
        sb.append('>');
    }

    /**
     * Appends a string-based representation of the specified set
     * to the specified string buffer.
     *
     * @param sb String buffer to which the representation is appended.
     * @param c Set to append as a string.
     * @deprecated Use {@code sb.append(c.toString())} instead.
     */
    @Deprecated
    public static void setToStringBuilder(StringBuilder sb, Set<?> c) {
        sb.append('{');
        elementsToStringBuilder(sb,c);
        sb.append('}');
    }

    /**
     * Appends a string-based representation of the specified colleciton
     * to the specified string buffer.
     *
     * @param sb String buffer to which the representation is appended.
     * @param c Collection to append as a string.
     * @deprecated Use {@code sb.append(c.toString())} instead.
     */
    @Deprecated
    public static void elementsToStringBuilder(StringBuilder sb,
                                               Collection<?> c) {
        Iterator<?> it = c.iterator();
        for (int i = 0; it.hasNext(); ++i) {
            if (i > 0) sb.append(',');
            sb.append(it.next());
        }
    }


    /**
     * Compare the first objec to the second using the specified comparator
     * if it is non-null, or by using their natural order.
     *
     * @param e1 First object.
     * @param e2 Second object.
     * @param comparator Comparator for objects, or {@code null}
     * @throws ClassCastException If the comparator is {@code null}
     * and the object {@code e1} is not an instance of {@code
     * Comparable<? super E>}.
     * @param <E> type of objects being compared
     */
    public static <E> int compare(E e1, E e2, Comparator<? super E> comparator) {
        if (comparator != null)
            return comparator.compare(e1,e2);
        @SuppressWarnings("unchecked")
        Comparable<? super E> c1 = (Comparable<? super E>) e1;
        return c1.compareTo(e2);
    }


}
