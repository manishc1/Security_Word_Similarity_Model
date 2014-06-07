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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

/**
 * A <code>MeshHeading</code> represents a particular heading in NLM's
 * controlled vocabulary of Medical Subject Headings (MeSH).  Each
 * heading is composed of a descriptor and zero or more qualifiers.
 * Each descriptor and qualifier is marked as to whether it is major
 * or minor for the article.
 *
 * <P>For more information about the MeSH vocabulary, see:
 *
 * <blockquote>
 * <a href=" http://www.nlm.nih.gov/mesh/meshhome.html">MeSH Home Page</a>
 * </blockquote>
 * 
 * @author  Bob Carpenter
 * @version 3.8
 * @since   LingPipe2.0
 */
public  class MeshHeading {

    private final Topic[] mTopics;

    MeshHeading(Topic[] topics) {
        mTopics = topics;
    }

    /**
     * Return an array containing the descriptor followed by
     * all qualifiers for this heading.
     *
     * @return All topics for this heading.
     */
    public Topic[] topics() {
        return mTopics;
    }

    /**
     * Return the descriptor for this heading.
     *
     * @return The descriptor for this heading.
     */
    public Topic descriptor() {
        return mTopics[0];
    }

    /**
     * Returns an array of zero or more qualifiers for this
     * heading.
     *
     * @return Zero or more qualifiers for this heading.
     */
    public Topic[] qualifiers() {
        Topic[] result = new Topic[mTopics.length-1];
        System.arraycopy(mTopics,1,result,0,mTopics.length-1);
        return result;
    }

    /**
     * Returns a string-based representation of this heading.
     *
     * @return A string-based representation of this heading.
     */
    @Override
    public String toString() {
        return Arrays.asList(mTopics).toString();
    }

    // anonymously collects the list elements; should use
    // this pattern elsewhere to pull all finishDelegate out
    // of MedlineCitation for lists
    static class ListHandler extends DelegateHandler {
        private final Handler mMeshHeadingHandler;
        private final List<MeshHeading> mMeshHeadingList = new ArrayList<MeshHeading>();
        public ListHandler(DelegatingHandler delegator) {
            super(delegator);
            mMeshHeadingHandler = new Handler(delegator);
            setDelegate(MedlineCitationSet.MESH_HEADING_ELT,
                        mMeshHeadingHandler);
        }
        public void reset() {
            mMeshHeadingList.clear();
        }
        @Override
        public void finishDelegate(String qName, DefaultHandler handler) {
            if (qName.equals(MedlineCitationSet.MESH_HEADING_ELT))
                mMeshHeadingList.add(((Handler)handler).getMeshHeading());
        }
        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            reset();
        }
        public MeshHeading[] getMeshHeadings() {
            return mMeshHeadingList.<MeshHeading>toArray(EMPTY_MESH_HEADING_ARRAY);
        }
    }

    static class Handler extends DelegateHandler {
        private final List<Topic> mTopicList = new ArrayList<Topic>();
        private final Topic.Handler mTopicHandler;
        public Handler(DelegatingHandler delegator) {
            super(delegator);
            mTopicHandler = new Topic.Handler();
            setDelegate(MedlineCitationSet.DESCRIPTOR_NAME_ELT,mTopicHandler);
            setDelegate(MedlineCitationSet.QUALIFIER_NAME_ELT,mTopicHandler);
        }
        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            mTopicList.clear();
        }
        @Override
        public void finishDelegate(String qName, DefaultHandler handler) {
            if (qName.equals(MedlineCitationSet.DESCRIPTOR_NAME_ELT)
                || qName.equals(MedlineCitationSet.QUALIFIER_NAME_ELT)) {
                mTopicList.add(((Topic.Handler)handler).getTopic());
            }
        }
        public MeshHeading getMeshHeading() {
            return new MeshHeading(mTopicList.<Topic>toArray(EMPTY_TOPIC_ARRAY));
        }
    }

    static final Topic[] EMPTY_TOPIC_ARRAY = new Topic[0];

    static final MeshHeading[] EMPTY_MESH_HEADING_ARRAY
        = new MeshHeading[0];


}
