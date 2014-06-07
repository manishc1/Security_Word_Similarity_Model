package com.aliasi.test.unit.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;


import com.aliasi.util.ReversibleComparator;

public class ReversibleComparatorTest  {

    @Test
    public void testOne() {
        ReversibleComparator rc
            = new ReversibleComparator(FilterComparatorTest.INTEGER_COMPARATOR);
        assertEquals(4,rc.compare(Integer.valueOf(7),Integer.valueOf(3)));
        assertEquals(0,rc.compare(Integer.valueOf(7),Integer.valueOf(7)));
        assertEquals(-4,rc.compare(Integer.valueOf(3),Integer.valueOf(7)));

        rc.toggleSortOrder();

        assertEquals(-4,rc.compare(Integer.valueOf(7),Integer.valueOf(3)));
        assertEquals(0,rc.compare(Integer.valueOf(7),Integer.valueOf(7)));
        assertEquals(4,rc.compare(Integer.valueOf(3),Integer.valueOf(7)));

        rc.toggleSortOrder();

        assertEquals(4,rc.compare(Integer.valueOf(7),Integer.valueOf(3)));
        assertEquals(0,rc.compare(Integer.valueOf(7),Integer.valueOf(7)));
        assertEquals(-4,rc.compare(Integer.valueOf(3),Integer.valueOf(7)));

        rc.toggleSortOrder();
        rc.setOriginalSortOrder();

        assertEquals(4,rc.compare(Integer.valueOf(7),Integer.valueOf(3)));
        assertEquals(0,rc.compare(Integer.valueOf(7),Integer.valueOf(7)));
        assertEquals(-4,rc.compare(Integer.valueOf(3),Integer.valueOf(7)));

        rc.setReverseSortOrder();

        assertEquals(-4,rc.compare(Integer.valueOf(7),Integer.valueOf(3)));
        assertEquals(0,rc.compare(Integer.valueOf(7),Integer.valueOf(7)));
        assertEquals(4,rc.compare(Integer.valueOf(3),Integer.valueOf(7)));

    }

}
