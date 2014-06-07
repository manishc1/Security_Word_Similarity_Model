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
import com.aliasi.xml.TextAccumulatorHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * An <code>Investigator</code> represents a funded principal
 * investigator for the (United States) National Aeronautics and Space
 * Administration (NASA).  The information provided by an investigator
 * is similar to that provided by an {@link Author}.
 * 
 * @author  Bob Carpenter
 * @version 3.7
 * @since   LingPipe2.0
 */
public class Investigator {

    private final Name mName;
    private final String mAffiliation;
    private final boolean mIsValid;

    Investigator(Name name, String affiliation, boolean isValid) {
        mName = name;
        mAffiliation = affiliation;
        mIsValid = isValid;
    }

    /**
     * Returns <code>true</code> if the investigator's name has been
     * validated and <code>false</code> if it has been changed later.
     *
     * @return <code>true</code> if the investigator has validated.
     */
    public boolean isValid() {
        return mIsValid;
    }

    /**
     * Returns the name of this investigator.
     *
     * @return The name of this investigator.
     */
    public Name name() {
        return mName;
    }

    /**
     * Returns the affiliation of this investigator, or the
     * empty (zero length) string if none was provided.
     *
     * @return The affiliation of this investigator.
     */
    public String affiliation() {
        return mAffiliation;
    }

    /**
     * Returns a string-based representation of this investigator.
     *
     * @return A string-based representation of this investigator.
     */
    @Override
    public String toString() {
        return ("Name=" + name() 
                + " Affiliation=" + affiliation());
    }

    static class Handler extends Name.Handler {
        private final TextAccumulatorHandler mAffiliationHandler
            = new TextAccumulatorHandler();
        private boolean mIsValid; // required so must be set
        public Handler(DelegatingHandler delegator) {
            super(delegator);
            setDelegate(MedlineCitationSet.AFFILIATION_ELT,mAffiliationHandler);
        }
        @Override
        public void startElement(String url, String localName, String qName, Attributes atts) throws SAXException {
            super.startElement(url,localName,qName,atts);
            if (qName.equals(MedlineCitationSet.INVESTIGATOR_ELT)) {
                String isValidString = atts.getValue(MedlineCitationSet.VALID_YN_ATT);
                mIsValid = MedlineCitationSet.YES_VALUE.equals(isValidString);
            }
        }
        public Investigator getInvestigator() {
            return new Investigator(getName(),mAffiliationHandler.getText(),mIsValid);
        }
    }
}
