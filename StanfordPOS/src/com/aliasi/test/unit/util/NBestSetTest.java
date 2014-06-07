package com.aliasi.test.unit.util;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;


import com.aliasi.util.NBestSet;

import java.util.Comparator;
import java.util.Iterator;


public class NBestSetTest  {

    @Test
    public void testOne() {
        NBestSet s = new NBestSet(2);
        s.add(Integer.valueOf(1));
        assertEquals(1,s.size());
        s.add(Integer.valueOf(2));
        assertEquals(2,s.size());
        s.add(Integer.valueOf(3));
        assertEquals(2,s.size());
        assertEquals(Integer.valueOf(2),s.first());
        assertEquals(Integer.valueOf(3),s.last());
        Iterator it = s.iterator();
        assertEquals(Integer.valueOf(2),it.next());
        assertEquals(Integer.valueOf(3),it.next());
        assertFalse(it.hasNext());
        s.add(Integer.valueOf(1));
        it = s.iterator();
        assertEquals(Integer.valueOf(2),it.next());
        assertEquals(Integer.valueOf(3),it.next());
        assertFalse(it.hasNext());
        s.add(Integer.valueOf(7));
        it = s.iterator();
        assertEquals(Integer.valueOf(3),it.next());
        assertEquals(Integer.valueOf(7),it.next());
        assertFalse(it.hasNext());
    }


    @Test
    public void testTwo() {
        Comparator comparator = new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((Comparable)o2).compareTo(o1);
                }
            };
        NBestSet s = new NBestSet(3,comparator);
        s.add(Integer.valueOf(1));
        s.add(Integer.valueOf(3));
        s.add(Integer.valueOf(5));
        s.add(Integer.valueOf(100));
        assertEquals(3,s.size());
        assertEquals(Integer.valueOf(5),s.first());
        assertEquals(Integer.valueOf(1),s.last());
    }
}
