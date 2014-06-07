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

package com.aliasi.medline;

import com.aliasi.xml.DelegateHandler;
import com.aliasi.xml.DelegatingHandler;

import com.aliasi.util.Iterators;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

/**
 * An <code>AuthorList</code> represents the authors of a particular
 * article.  The authors may be accessed directly with the method
 * {@link #authors()}.  The authors on the resulting list may include
 * both individual and collective authors.  Anonymous articles will
 * have an empty list of authors.
 * 
 * <P>Author lists may not be complete.  Completeness may be checked
 * using the method {@link #complete()}.  Records from 1966--1983 have
 * complete author lists; from 1984--1995, a maximum of 10 authors was
 * included per article; from 1996--1999, a maximum of 25 authors was
 * included; from 2000 onwards all authors are included.
 *
 * @author  Bob Carpenter
 * @version 3.8
 * @since   LingPipe2.0
 */
public class AuthorList implements Iterable<Author> {

    private final boolean mComplete;
    private final Author[] mAuthors;

    AuthorList(boolean complete, Author[] authors) {
        mComplete = complete;
        mAuthors = authors;
    }

    /**
     * Returns an iterator over the authors in this list.  This method
     * allows general for loops to be used with authors.
     *
     * @return An iterator over the authors in this list.
     */
    public Iterator<Author> iterator() {
        return Iterators.<Author>array(mAuthors);
    }
    
    /**
     * Returns <code>true</code> if the author list is complete.  See
     * the class documentation for further information on
     * completeness.
     *
     * @return <code>true</code> if the author list is complete.
     */
    public boolean complete() {
        return mComplete;
    }

    /**
     * Returns the authors of this article.  Whether this list is
     * complete or not can be determined by method {@link
     * #complete()}.  If the list has length zero, the article is
     * anonymous.
     *
     * @return The authors of this article.
     */
    public Author[] authors() {
        return mAuthors;
    }

    /**
     * Returns a string-based representation of this author list.
     *
     * @return A string-based representation of this author list.
     */
    @Override
    public String toString() {
        return Arrays.<Author>asList(mAuthors) + " Complete=" + mComplete;
    }

    static class Handler extends DelegateHandler {
        private boolean mComplete;
        private final List<Author> mAuthorList = new ArrayList<Author>();
        private final Author.Handler mAuthorHandler;
        public Handler(DelegatingHandler delegator) {
            super(delegator);
            mAuthorHandler = new Author.Handler(delegator);
            setDelegate(MedlineCitationSet.AUTHOR_ELT,
                        mAuthorHandler);
        }
        public void reset() {
            mAuthorList.clear();
        }
        @Override
        public void finishDelegate(String qName, DefaultHandler handler) {
            if (qName.equals(MedlineCitationSet.AUTHOR_ELT)) {
                mAuthorList.add(mAuthorHandler.getAuthor());
            }
        }
        public AuthorList getAuthorList() {
            Author[] authors 
                = mAuthorList.toArray(EMPTY_AUTHOR_ARRAY);
            return new AuthorList(mComplete,authors);
        }
        @Override
        public void startElement(String namespaceURI, String localName,
                                 String qName, Attributes atts)
            throws SAXException {
            if (qName.equals(MedlineCitationSet.AUTHOR_LIST_ELT)) {
                mComplete 
                    = MedlineCitationSet
                    .YES_VALUE
                    .equals(atts.getValue(MedlineCitationSet.COMPLETE_YN_ATT));
            }
            super.startElement(namespaceURI,localName,qName,atts);
        }
        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            reset();
        }
    }

    static final Author[] EMPTY_AUTHOR_ARRAY = new Author[0];

}
