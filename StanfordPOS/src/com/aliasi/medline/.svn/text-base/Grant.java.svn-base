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

import org.xml.sax.SAXException;

/**
 * A <code>Grant</code> represents a particular instance of a grant.
 * Four pieces of information are optionally available: the grant
 * identifier, the acronym of the grant type, the acronym for the
 * grant agencies,. and the country of origin for the grant.
 *
 * <P>The full list of grant agencies, grants and their acronyms may
 * be found at:
 *
 * <blockquote>
 * <a href="http://www.ncbi.nlm.nih.gov/entrez/query/static/help/pmhelp.html">PubMed Help: Grant Abbreviations</a>
 * </blockquote>
 *
 * @author  Bob Carpenter
 * @version 3.7
 * @since   LingPipe2.0
 */
public class Grant {

    private final String mGrantID;
    private final String mAcronym;
    private final String mAgency;
    private final String mCountry;

    Grant(String grantID, String acronym, String agency, String country) {
        mGrantID = grantID;
        mAcronym = acronym;
        mAgency = agency;
        mCountry = country;
    }

    /**
     * Return the country for the grant.
     *
     * @return The country for the grant.
     */
    public String country() {
        return mCountry;
    }

    /**
     * Returns the grant identifier for this grant, or the
     * empty (zero length) string if none was provided.
     *
     * @return The grant identifier for this grant.
     */
    public String grantID() {
        return mGrantID;
    }

    /**
     * Returns a two-letter acronym for the type of this grant, or
     * the empty (zero length) string if none was provided.
     * A link to a list of grant abbreviations is cited in the class
     * documentation above.
     *
     * @return The acronym for the type of this grant.
     */
    public String acronym() {
        return mAcronym;
    }

    /**
     * Returns a three-letter acronym for the agency providing this
     * grant, or the empty (zero length) string if none was provided.
     * A link to a list of grant abbreviations is cited in the class
     * documentation above.  As of 2009, granting agencies are presented
     * hierarchically, as <code>NIEHS NIH HHS</code> for the HHS branch
     * of NIH.
     *
     * @return The hierarchical acronym(s) for the agency providing
     * this grant.
     */
    public String agency() {
        return mAgency;
    }

    /**
     * Returns a string-based representation of this grant.
     *
     * @return A string-based representation of this grant.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        if (country() != null && country().length() > 0) {
            sb.append(" Country=" + country());
        }
        if (grantID() != null && grantID().length() > 0) {
            sb.append(" Grant ID=" + grantID());
        }
        if (acronym() != null && acronym().length() > 0) {
            sb.append(" Acronym=" + acronym());
        }
        if (agency() != null && agency().length() > 0) {
            sb.append(" Agency=" + agency());
        }
        sb.append('}');
        return sb.toString();
    }

    static class Handler extends DelegateHandler {
        private final TextAccumulatorHandler mGrantIDHandler
            = new TextAccumulatorHandler();
        private final TextAccumulatorHandler mAcronymHandler
            = new TextAccumulatorHandler();
        private final TextAccumulatorHandler mAgencyHandler
            = new TextAccumulatorHandler();
        private final TextAccumulatorHandler mCountryHandler
            = new TextAccumulatorHandler();
        public Handler(DelegatingHandler delegator) {
            super(delegator);
            setDelegate(MedlineCitationSet.GRANT_ID_ELT,
                        mGrantIDHandler);
            setDelegate(MedlineCitationSet.ACRONYM_ELT,
                        mAcronymHandler);
            setDelegate(MedlineCitationSet.AGENCY_ELT,
                        mAgencyHandler);
            setDelegate(MedlineCitationSet.COUNTRY_ELT,
                        mCountryHandler);
        }
        public void reset() {
            mGrantIDHandler.reset();
            mAcronymHandler.reset();
            mAgencyHandler.reset();
            mCountryHandler.reset();
        }
        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            reset();
        }
        public Grant getGrant() {
            return new Grant(mGrantIDHandler.getText(),
                             mAcronymHandler.getText(),
                             mAgencyHandler.getText(),
                             mCountryHandler.getText());
        }

    }

}
