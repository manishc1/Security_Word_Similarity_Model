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

import com.aliasi.xml.DelegatingHandler;

import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * An <code>ArticleDate</code> represents the date on which the
 * publisher created an electronic version of an article.  The
 * type of publication is fixed to <code>Electronic</code> for
 * article dates, so this information is not provided in this class.
 *
 * <P><i>Note:</i> The <code>ArticleDate</code> element was introduced
 * in 2005 to replace the previous <code>ElectronicPubDate</code>
 * element.  The nlmcommon DTD specifies a fixed
 * <code>Electronic</code> value for the <code>DateType</code>
 * attribute.
 *
 * @author  Bob Carpenter
 * @version 2.0
 * @since   LingPipe2.0
 */
public class ArticleDate {

    private final Date mDate;

    /**
     * Construct an article date with the specified date.
     *
     * @param date Date for article.
     */
    ArticleDate(Date date) {
        mDate = date;
    }

    /**
     * Returns the actual electronic publication date.  Note that
     * this date will be resolved to the year, month and day.
     * level.  Any hour, minute or second information is ignored.
     *
     * @return The electronic publishing date.
     */
    public Date date() {
        return mDate;
    }

    /**
     * Return a string-based representation of this electronic
     * publication date.
     *
     * @return A string-based representation of this electronic date.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append(" Date=");
        sb.append(mDate);
        sb.append('}');
        return sb.toString();
    }

    static class Handler extends MedlineCitation.DateHandler {
        private boolean mVisited;
        public Handler(DelegatingHandler delegator) {
            super(delegator);
        }
        @Override
        public void reset() {
            mVisited = false;
            super.reset();
        }
        @Override
        public void startElement(String namespaceURI, String localName,
                                 String qName, Attributes atts)
            throws SAXException {
            super.startElement(namespaceURI,localName,qName,atts);
            mVisited = true;
        }
        public ArticleDate getArticleDate() {
            if (!mVisited) return null;
            return new ArticleDate(getDate());
        }
    }
}


