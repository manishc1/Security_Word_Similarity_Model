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

package com.aliasi.corpus.parsers;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.chunk.ChunkFactory;
import com.aliasi.chunk.ChunkingImpl;

import com.aliasi.corpus.Handler;
import com.aliasi.corpus.ObjectHandler;
import com.aliasi.corpus.XMLParser;

import com.aliasi.xml.DelegatingHandler;
import com.aliasi.xml.DelegateHandler;

import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

/**
 * A <code>GeniaEntityChunkParser</code> provides an entity parser for
 * the XML-formatted GENIA entity corpus.
 *
 * @author  Bob Carpenter
 * @version 3.9
 * @since   LingPipe2.3
 */
public class GeniaEntityChunkParser extends XMLParser<ObjectHandler<Chunking>> {

    /**
     * Construct a GENIA entity chunk parser with no designated chunk
     * handler.  Chunk handlers may be set later using the method
     * {@link #setHandler(Handler)}.
     *
     * @throws SAXException If there is an error configuring the SAX
     * XML reader required for parsing.
     */
    public GeniaEntityChunkParser() throws SAXException {
        super();
    }

    /**
     * Construct a GENIA entity chunk parser with the specified chunk
     * handler.  Chunk handlers may be reset later using the method
     * {@link #setHandler(Handler)}.
     *
     * @param handler Chunk handler for the parser.
     * @throws SAXException If there is an error configuring the SAX
     * XML reader required for parsing.
     */
    public GeniaEntityChunkParser(ObjectHandler<Chunking> handler) 
        throws SAXException {

        super(handler);
    }

    /**
     * Returns the chunk handler for this parser.  The result
     * will be the same as calling the superclass method {@link
     * #getHandler()}, but the result in this case is cast to type
     * <code>ObjectHandler&lt;Chunking&gt;</code>.
     *
     * @return The chunk handler for this sentence parser.
     * @deprecated Use generic {@link #getHandler()} instead.
     */
    @Deprecated
    public ObjectHandler<Chunking> getChunkHandler() {
        return getHandler();
    }

    /**
     * Returns the embedded XML handler.  This method implements
     * the required method for the abstract superclass {@link XMLParser}.
     *
     * @return The XML handler for this class.
     */
    @Override
    protected DefaultHandler getXMLHandler() {
        return new SetHandler(this,getChunkHandler());
    }

    private static final int SIMPLIFY_TYPE_GROUP = 1;
    private static final Pattern SIMPLIFY_TYPE_PATTERN
        = Pattern.compile("G#([a-zA-Z_]+)");

    /**
     * Returns a simplified type for the specified original genia type.
     * No tags are lost; they're just shortened in form.  Override this method
     * in a subclass to remove type simplification.
     *
     * @param originalGeniaType Original type from Genia.
     * @return Simplified entity type.
     */
    public String simplifyType(String originalGeniaType) {
        Matcher matcher = SIMPLIFY_TYPE_PATTERN.matcher(originalGeniaType);
        matcher.find();
        return matcher.group(SIMPLIFY_TYPE_GROUP);
    }

    /**
     * The tag used for sentence elements in GENIA, namely
     * <code>sentence</code>.
     */
    public static final String GENIA_SENTENCE_ELT = "sentence";

    public static final String GENIA_ENTITY_ELT = "cons";

    public static final String GENIA_ENTITY_TYPE_ATT = "sem";

    private static class SetHandler extends DelegatingHandler {
        final ObjectHandler<Chunking> mChunkHandler;
        final SentenceHandler mSentenceHandler;
        final GeniaEntityChunkParser mGECP;

        SetHandler(GeniaEntityChunkParser gecp,
                   ObjectHandler<Chunking> chunkHandler) {
            mGECP = gecp;
            mChunkHandler = chunkHandler;
            mSentenceHandler = new SentenceHandler(gecp,this);
            setDelegate(GENIA_SENTENCE_ELT,mSentenceHandler);
        }

        @Override
        public void finishDelegate(String qName, DefaultHandler delegate) {
            if (qName.equals(GENIA_SENTENCE_ELT))
                mChunkHandler.handle(mSentenceHandler.getChunking());
        }
    }


    private static class SentenceHandler extends DelegateHandler {
        StringBuilder mBuf;
        List<Chunk> mChunkList;
        int mChunkDepth;
        int mChunkStart;
        String mChunkType;
        final GeniaEntityChunkParser mGECP;
        public SentenceHandler(GeniaEntityChunkParser gecp,
                               DelegatingHandler parent) {
            super(parent);
            mGECP = gecp;
        }
        @Override
        public void startDocument() {
            mBuf = new StringBuilder();
            mChunkList = new ArrayList<Chunk>();
            mChunkDepth = 0;
        }
        @Override
        public void startElement(String namespace, String localName,
                                 String qName, Attributes atts) {
            if (qName.equals(GENIA_ENTITY_ELT)
                && mChunkDepth++ == 0) {
                mChunkStart = mBuf.length();
                String origType = atts.getValue(GENIA_ENTITY_TYPE_ATT);
                mChunkType = mGECP.simplifyType(origType);
            }
        }
        @Override
        public void characters(char[] cs, int start, int length) {
            mBuf.append(cs,start,length);
        }
        @Override
        public void endElement(String namespace, String localName,
                               String qName) {
            if (qName.equals(GENIA_ENTITY_ELT)
                && --mChunkDepth == 0) {
                int chunkEnd = mBuf.length();
                Chunk chunk
                    = ChunkFactory.createChunk(mChunkStart,
                                               chunkEnd,
                                               mChunkType);
                mChunkList.add(chunk);
            }
        }
        Chunking getChunking() {
            ChunkingImpl chunking = new ChunkingImpl(mBuf);
            for (Chunk chunk : mChunkList) {
                chunking.add(chunk);
            }
            return chunking;
        }
    }


}
