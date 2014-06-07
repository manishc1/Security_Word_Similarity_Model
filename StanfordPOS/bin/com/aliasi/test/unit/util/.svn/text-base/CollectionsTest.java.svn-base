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

package com.aliasi.test.unit.util;

import com.aliasi.util.Collections;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertArrayEquals;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.NoSuchElementException;

public class CollectionsTest  {

    @Test
    public void testIsSingleton() {
        HashSet set = new HashSet();
        assertFalse(Collections.isSingleton(set));
        set.add("a");
        assertTrue(Collections.isSingleton(set));
        set.add("b");
        assertFalse(Collections.isSingleton(set));
    }

    @Test
    public void testGetFirstList() {
        ArrayList list = new ArrayList();
        boolean threw = false;
        try {
            Collections.getFirst(list);
        } catch (IndexOutOfBoundsException e) {
            threw = true;
        }
        assertTrue(threw);
        list.add("a");
        assertEquals("a",Collections.getFirst(list));
        list.add("b");
        assertEquals("a",Collections.getFirst(list));
    }

    @Test
    public void testGetFirstSet() {
        HashSet set = new HashSet();
        boolean threw = false;
        try {
            Collections.getFirst(set);
        } catch (NoSuchElementException e) {
            threw = true;
        }
        assertTrue(threw);
        set.add("a");
        assertEquals("a",Collections.getFirst(set));
        set.add("b");
        assertTrue("a".equals(Collections.getFirst(set))
                   || "b".equals(Collections.getFirst(set)));
    }

    @Test
    public void testNonEmptyIntersection() {
        HashSet set1 = new HashSet();
        HashSet set2 = new HashSet();
        assertFalse(Collections.intersects(set1,set2));
        set1.add("a");
        assertFalse(Collections.intersects(set1,set2));
        set2.add("b");
        assertFalse(Collections.intersects(set1,set2));
        set1.add("c");
        set2.add("c");
        assertTrue(Collections.intersects(set1,set2));
    }

    @Test
    public void testAddAll() {
        HashSet set = new HashSet();
        assertEquals(0,set.size());
        Collections.addAll(set,new Object[] { });
        assertEquals(0,set.size());
        Collections.addAll(set,new Object[] { "a" });
        assertEquals(1,set.size());
        assertTrue(set.contains("a"));
        Collections.addAll(set,new Object[] { "b", "c" });
        assertEquals(3,set.size());
        assertTrue(set.contains("b"));
    }

    @Test
    public void testToStringArray() {
        ArrayList list = new ArrayList();
        String[] zeroArray = new String[0];
        String[] oneArray = new String[1];
        String[] twoArray = new String[2];
        assertArrayEquals(new String[] { },
                          Collections.toStringArray(list));
        Collections.toStringArray(list,zeroArray);
        list.add("a");
        assertArrayEquals(new String[] { "a" },
                          Collections.toStringArray(list));
        Collections.toStringArray(list,oneArray);
        assertArrayEquals(new String[] { "a" },
                          oneArray);
        list.add("b");
        Collections.toStringArray(list,twoArray);
        assertArrayEquals(new String[] { "a", "b" },
                          Collections.toStringArray(list));
        assertArrayEquals(new String[] { "a", "b" },
                          twoArray);
        boolean threw = false;
        try {
            Collections.toStringArray(list,oneArray);
        } catch (IndexOutOfBoundsException e) {
            threw = false;
        }
        assertFalse(threw);
        assertArrayEquals(new String[] { "a" },
                          oneArray);
    }


    @Test
    public void testToIntArray() {
        ArrayList list = new ArrayList();
        assertArrayEquals(Collections.toIntArray(list),
                          new int[] { });
        list.add(Integer.valueOf(1));
        assertArrayEquals(Collections.toIntArray(list),
                          new int[] { 1 });
        list.add(Integer.valueOf(2));
        assertArrayEquals(Collections.toIntArray(list),
                          new int[] { 1, 2 });

    }

    @Test
    public void testIntersections() {
        HashSet s1 = new HashSet();
        HashSet s2 = new HashSet();
        assertFalse(Collections.intersects(s1,s2));
        s1.add("a");
        assertFalse(Collections.intersects(s1,s2));
        s2.add("a");
        assertTrue(Collections.intersects(s1,s2));

        HashSet s3 = new HashSet();
        s3.add(Integer.valueOf(3));
        s3.add(Integer.valueOf(5));
        s3.add(Integer.valueOf(7));
        HashSet s4 = new HashSet();
        s4.add(Integer.valueOf(2));
        s4.add(Integer.valueOf(4));
        s4.add(Integer.valueOf(5));
        s4.add(Integer.valueOf(9));
        assertTrue(Collections.intersects(s3,s4));
    }


    @Test
    public void testCompare() {
        String o1 = "a";
        String o2 = "b";
        assertTrue(Collections.<String>compare(o1,o2,null) < 0);
        String o3 = "a";
        assertEquals(0,Collections.<String>compare(o1,o3,null));

        Comparator<String> longestStringComparator = new Comparator<String>() {
            public int compare(String s1, String s2) {
                return s1.length() - s2.length();
            }
        };
        assertEquals(0,Collections.<String>compare(o1,o3,longestStringComparator));
        assertEquals(2,Collections.<String>compare("abc","d",longestStringComparator));
        assertEquals(-1,Collections.<String>compare("abc","defg",longestStringComparator));
    }

    @Test(expected=ClassCastException.class)
    public void testClassCastExc() {
        Collections.<Object>compare(new Object(), new Object(), null);
    }


}
