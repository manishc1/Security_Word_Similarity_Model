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
import com.aliasi.xml.TextAccumulatorHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A <code>NameId</code> is a structured record consisting of a source
 * and an identifier for the name supplied by that source.
 *
 * @author  Bob Carpenter
 * @version 3.9
 * @since   LingPipe3.9
 */
public class NameId {

    private final String mSource;
    private final String mId;

    NameId(String source,
           String id) {
        mSource = source;
        mId = id;
    }

    /**
     * Returns the source of the name identifier.
     *
     * @return Source of name identifier.
     */
    public String source() {
        return mSource;
    }

    /**
     * Returns the identifier for name identifier.
     *
     * @return Source for this name identifier.
     */
    public String id() {
        return mId;
    }

    /**
     * Returns a string-based representation of this name
     * identifier.
     *
     * @return String representation of this identifier.
     */
    @Override
    public String toString() {
        return "NameId(source=" + source() + ",id=" + id()+ ")";
    }
        

    static class Handler extends TextAccumulatorHandler {
        private String mSource = null;
        public Handler() {
        }
        @Override
        public void startDocument() {
            super.startDocument();
            mSource = null;
        }
        @Override
        public void startElement(String uri, String localName,
                                 String qName, Attributes atts) throws SAXException {
            super.startElement(uri,localName,qName,atts);
            if (MedlineCitationSet.NAME_ID_ELT.equals(qName))
                mSource = atts.getValue(MedlineCitationSet.SOURCE_ATT);
        }
        public NameId getNameId() {
            return new NameId(mSource,getText());
        }
    }

}