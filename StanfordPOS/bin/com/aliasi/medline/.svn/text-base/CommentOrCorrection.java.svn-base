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

import com.aliasi.xml.TextAccumulatorHandler;
import com.aliasi.xml.DelegatingHandler;
import com.aliasi.xml.DelegateHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A <code>CommentOrCorrection</code> object represents one of
 * the many possible comments or corrections that have been applied
 * to an article.  these include comments, errat, retractions, etc.
 *
 * <P>There are various types of comments or corrections, corresponding
 * to the elements used to code them in the XML version of MEDLINE.
 * The following is a complete list with descriptions:
 *
 * <blockquote>
 * <table border='1' cellpadding='5'>
 * <tr><td><i>Type</i></td><td><i>Description</i></td></tr>
 * <tr><td><code>CommentOn</code></td>
 *     <td>Reference upon which this article comments</td></tr>
 * <tr><td><code>CommentIn</code></td>
 *     <td>Reference containing a comment about this article</td></tr>
 * <tr><td><code>ErratumIn</code></td>
 *     <td>Reference containing published erratum to this article</td></tr>
 * <tr><td><code>ErratumFor</code></td>
 *     <td>The original article for which there is a published
 *         erratum</td></tr>
 * <tr><td><code>RepublishedFrom</code></td>
 *     <td>The original article</td></tr>
 * <tr><td><code>RepublishedIn</code></td>
 *     <td>Reference of the corrected and republished article</td></tr>
 * <tr><td><code>RetractionOf</code></td>
 *     <td>The article being retracted</td></tr>
 * <tr><td><code>RetractionIn</code></td>
 *     <td>The reference containing a retraction of the article</td></tr>
 * <tr><td><code>PartialRetractionOf</code></td>
 *     <td>The article being retracted</td></tr>
 * <tr><td><code>PartialRetractionIn</code></td>
 *     <td>The reference containing a retraction of the article</td></tr>
 * <tr><td><code>UpdateIn</code></td>
 *     <td>Reference containing and update to the article</td></tr>
 * <tr><td><code>UpdateOf</code></td>
 *     <td>The article being updated</td></tr>
 * <tr><td><code>SummaryForPatientsIn</code></td>
 *     <td>Reference containing a patient summary article</td></tr>
 * <tr><td><code>OriginalReportIn</code></td>
 *     <td>Scientific article associated with a patient summary</td></tr>
 * <tr><td><code>ReprintIn</code></td>
 *     <td>2005 addition not yet documented by NLM</td></tr>
 * <tr><td><code>ReprintOf</code></td>
 *     <td>2005 addition not yet documented by NLM</td></tr>
 * </table>
 * </blockquote>
 *
 * <P>More information on these fields is available from:
 *
 * <blockquote>
 * <a href=" http://www.nlm.nih.gov/pubs/factsheets/errata.html"
 *   >NLM Fact Sheet: Errata, Retraction, Duplicate Publication and
 *    Comment Policy for MEDLINE</a>
 * </blockquote>
 *
 *
 * @author  Bob Carpenter
 * @version 3.9
 * @since   LingPipe2.0
 */
public class CommentOrCorrection {

    private final String mType;
    private final String mRefSource;
    private final String mPMID;
    private final String mNote;

    CommentOrCorrection(String type, String refSource,
                        String pmid, String note) {
        mType = type;
        mRefSource = refSource;
        mPMID = pmid;
        mNote = note;
    }

    /**
     * Returns the type of this comment or correction.  See the
     * class documentation for an enumeration of possible values.
     *
     * @return Type of this comment or correction.
     */
    public String type() {
        return mType;
    }

    /**
     * Returns a string representation of the citation for the
     * cross-referenced article.  The relation between the
     * cross-referenced article and this article is determined
     * by type as described in the class documentation above.
     *
     * @return The cross-reference for this comment or correction.
     */
    public String refSource() {
        return mRefSource;
    }

    /**
     * Returns the PubMed identifier of the associated record in
     * PubMed, or the empty (zero length) string if none is available.
     * The relation between the associated record is based on type and
     * described in the class documentation above.
     *
     * @return The PubMed identifier of the cross-reference for this
     * comment or correction.
     */
    public String pmid() {
        return mPMID;
    }

    /**
     * Returns a clarifying note on the data, or the empty (length
     * zero) string if no note was provided.  Notes appear
     * infrequently, but when used, often accompanies an erratum
     * correcting authors name.  Other usages correct dosage errors in
     * original abstracts and clarification of the scope of
     * retractions of and retracted in comments or corrections.
     *
     * @return A clarifying note on the data.
     */
    public String note() {
        return mNote;
    }

    /**
     * Returns a string representation of this comment or correction.
     *
     * @return A string representation of this comment or correction.
     */
    @Override
    public String toString() {
        return "Type=" + type()
            + " RefSource=" + refSource()
            + " PMID=" + pmid()
            + " Note=" + note();
    }

    static class Handler extends DelegateHandler {
        private String mType;
        private final TextAccumulatorHandler mRefSourceHandler
            = new TextAccumulatorHandler();
        private final TextAccumulatorHandler mPMIDHandler
            = new TextAccumulatorHandler();
        private final TextAccumulatorHandler mNoteHandler
            = new TextAccumulatorHandler();
        public Handler(DelegatingHandler delegator) {
            super(delegator);
            setDelegate(MedlineCitationSet.REF_SOURCE_ELT,
                        mRefSourceHandler);
            setDelegate(MedlineCitationSet.PMID_ELT,mPMIDHandler);
            setDelegate(MedlineCitationSet.NOTE_ELT,mNoteHandler);
        }
        @Override
        public void startDocument() throws SAXException {
            mRefSourceHandler.reset();
            mPMIDHandler.reset();
            mNoteHandler.reset();
            mType = null;
            super.startDocument();
        }
        @Override
        public void startElement(String uri, String localName,
                                 String qName, Attributes atts) throws SAXException {
            super.startElement(uri,localName,qName,atts);
            if (MedlineCitationSet.COMMENTS_CORRECTIONS_ELT.equals(qName))
                mType = atts.getValue(MedlineCitationSet.REF_TYPE_ATT);
        }
        public CommentOrCorrection getCommentOrCorrection() {
            return new CommentOrCorrection(mType,
                                           mRefSourceHandler.getText(),
                                           mPMIDHandler.getText(),
                                           mNoteHandler.getText());
        }
    }
}
