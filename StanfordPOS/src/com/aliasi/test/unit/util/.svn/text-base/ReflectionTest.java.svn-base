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

import com.aliasi.util.Reflection;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ReflectionTest  {

    @Test
    public void testSubmittedRecursionBug() {
        Reflection.newInstance("java.lang.String",
                               new Object[] { "foo" },
                               new String[] { "java.lang.String" });
    }

    @Test
    public void testNewInstance() {

        assertEquals("abc",
                     Reflection.newInstance("java.lang.String",
                                            new Object[] { "abc" }));
        Object x = Reflection.newInstance("java.lang.StringBuilder",
                                          new Object[] { });
        assertEquals("",x.toString());


        assertEquals("",
                     Reflection.newInstance("java.lang.StringBuilder").toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExc1() {
        Reflection.newInstance("java.lang.Object",
                   new Object[] { "abc" });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExc2() {
        Reflection.newInstance("java.lang.Integer",
                   new Object[] { "abc" });
    }


    @Test(expected = IllegalArgumentException.class)
    public void testExc3() {
        Reflection.newInstance("java.lang.foobar",
                   new Object[] { "abc" });
    }


}
