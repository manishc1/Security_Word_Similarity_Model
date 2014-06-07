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

package com.aliasi.test.unit.io;

import com.aliasi.io.FileNameFilter;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;


import com.aliasi.util.Files;

import java.io.File;
import java.io.FileFilter;

public class FileNameFilterTest  {


    @Test
    public void testAll() {
        File fDir = Files.makeCleanDir(Files.TEMP_DIRECTORY,"fnamefilter");
        File fYes = new File(Files.TEMP_DIRECTORY,"foo");
        File fNo = new File(Files.TEMP_DIRECTORY,"foo.txt");
        File fNo2 = new File(Files.TEMP_DIRECTORY,"bar");

        FileFilter filter1 = new FileNameFilter1(); // all dirs, default
        assertTrue(filter1.accept(fDir));
        assertTrue(filter1.accept(fYes));
        assertFalse(filter1.accept(fNo));
        assertFalse(filter1.accept(fNo2));

        FileFilter filter2 = new FileNameFilter2(); // all dirs, specified
        assertTrue(filter2.accept(fDir));
        assertTrue(filter2.accept(fYes));
        assertFalse(filter2.accept(fNo));
        assertFalse(filter2.accept(fNo2));

        FileFilter filter3 = new FileNameFilter3(); // no dirs
        assertFalse(filter3.accept(fDir));
        assertTrue(filter3.accept(fYes));
        assertFalse(filter3.accept(fNo));
        assertFalse(filter3.accept(fNo2));
    }        


    private class FileNameFilter1 extends FileNameFilter {
        public FileNameFilter1() {
            super();
        }
        @Override
        public boolean accept(String name) {
            return "foo".equals(name);
        }
    }

    private class FileNameFilter2 extends FileNameFilter {
        public FileNameFilter2() {
            super(true);
        }
        @Override
        public boolean accept(String name) {
            return "foo".equals(name);
        }
    }

    private class FileNameFilter3 extends FileNameFilter {
        public FileNameFilter3() {
            super(false);
        }
        @Override
        public boolean accept(String name) {
            return "foo".equals(name);
        }
    }

}
