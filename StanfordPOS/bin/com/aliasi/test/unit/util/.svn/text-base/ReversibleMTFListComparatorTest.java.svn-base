package com.aliasi.test.unit.util;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;


import com.aliasi.util.ReversibleMTFListComparator;

import java.util.Comparator;

public class ReversibleMTFListComparatorTest  {

    static Integer I3 = Integer.valueOf(3);
    static Integer I33 = Integer.valueOf(33);
    static Integer I8 = Integer.valueOf(8);
    static Integer I9 = Integer.valueOf(9);

    @Test
    public void testOne() {
        ReversibleMTFListComparator rmlc
            = new ReversibleMTFListComparator(new Comparator[] {
                FilterComparatorTest.INTEGER_COMPARATOR,
                AD_HOC_COMPARATOR,
                FilterComparatorTest.STRING_COMPARATOR
            });
        Integer[] ints = new Integer[] { I3, I33, I9, I8 };
        rmlc.sort(ints);
        assertArrayEquals(new Integer[] { I3, I8, I9, I33 }, ints);

        rmlc.reverse(FilterComparatorTest.INTEGER_COMPARATOR);
        rmlc.sort(ints);
        assertArrayEquals(new Integer[] { I33, I9, I8, I3 }, ints);

        rmlc.moveToFront(FilterComparatorTest.STRING_COMPARATOR);
        rmlc.sort(ints);
        assertArrayEquals(new Integer[] { I3, I33, I8, I9}, ints);

        rmlc.reverse(FilterComparatorTest.STRING_COMPARATOR);
        rmlc.sort(ints);
        assertArrayEquals(new Integer[] { I9, I8, I33, I3}, ints);

        rmlc.moveToFront(AD_HOC_COMPARATOR);
        rmlc.sort(ints);
        assertArrayEquals(new Integer[] { I33, I9, I8, I3 }, ints);
    }

    private static final Comparator AD_HOC_COMPARATOR
        = new Comparator() {
                public int compare(Object x1, Object x2) {
                    if (x1.equals(I3)) return 1;
                    if (x2.equals(I3)) return -1;
                    if (x1.equals(I33)) return -1;
                    if (x2.equals(I33)) return 1;
                    return 0;
                }
            };


}
