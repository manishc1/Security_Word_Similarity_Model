package com.aliasi.test.unit.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;


import com.aliasi.util.FilterComparator;

import java.util.Comparator;

public class FilterComparatorTest  {

    @Test
    public void testOne() {
        FilterComparator fc = new FilterComparator(INTEGER_COMPARATOR);
        assertEquals(4,fc.compare(Integer.valueOf(7),Integer.valueOf(3)));
        assertEquals(0,fc.compare(Integer.valueOf(7),Integer.valueOf(7)));
        assertEquals(-4,fc.compare(Integer.valueOf(3),Integer.valueOf(7)));
    }

    public static final Comparator INTEGER_COMPARATOR
        = new Comparator() {
                public int compare(Object x1, Object x2) {
                    return ((Integer)x1).intValue()
                        - ((Integer)x2).intValue();
                }
            };


    public static final Comparator STRING_COMPARATOR
        = new Comparator() {
                public int compare(Object x1, Object x2) {
                    return x1.toString().compareTo(x2.toString());
                }
            };
}
