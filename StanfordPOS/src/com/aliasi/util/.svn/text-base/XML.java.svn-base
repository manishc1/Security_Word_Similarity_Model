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

package com.aliasi.util;

import com.aliasi.io.FileExtensionFilter;

import java.io.CharArrayReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Static utility methods and classes for processing XML.
 *
 * @author  Bob Carpenter
 * @version 3.8
 * @since   LingPipe1.0
 */
public class XML {

    /**
     * Forbid instance construction
     */
    private XML() {
        /* no instances */
    }

    /**
     * Returns an XML-escaped version of the specified string.
     *
     * @param in String to escape.
     * @return Escaped version of the specified string.
     */
    public static String escape(String in) {
        StringBuilder sb = new StringBuilder();
        escape(in,sb);
        return sb.toString();
    }

    /**
     * Write an XML-escaped version of the specified string
     * to the specified string buffer.
     *
     * @param in String to escape.
     * @param sb String buffer to whcih to write escaped version of
     * string.
     */
    public static void escape(String in, StringBuilder sb) {
        for (int i = 0; i < in.length(); ++i) {
            escape(in.charAt(i),sb);
        }
    }

    /**
     * Write an XML-escaped version of the specified character
     * to the specified string buffer.
     *
     * @param c Character to write.
     * @param sb String buffer to which to write escaped version of
     * character.
     */
    public static void escape(char c, StringBuilder sb) {
        switch (c) {
        case '<':  escapeEntity("lt",sb); return;
        case '>':  escapeEntity("gt",sb); return;
        case '&':  escapeEntity("amp",sb); return;
        case '"':  escapeEntity("quot",sb); return;
        default:   sb.append(c);
        }
    }

    /**
     * Write an XML-escaped version of the entity specified by name to
     * the specified string buffer.
     *
     * @param xmlEntity Name of entity with no markup.
     * @param sb String Buffer to which escaped form of entity is
     * written.
     */
    public static void escapeEntity(String xmlEntity, StringBuilder sb) {
        sb.append('&');
        sb.append(xmlEntity);
        sb.append(';');
    }

    /**
     * Handle the document specified as a string with the specified
     * handler.
     *
     * <p><p>An {@link XMLReader} instance will be created using Java's
     * built-in {@link XMLReaderFactory}.

     *
     * @param document String representation of XML document.
     * @param handler Handler for SAX events from document.
     * @throws SAXException If there is a SAX exception handling the
     * document.
     * @throws IOException If there is an I/O exception reading from
     * the document.
     */
    public static void handle(String document,
                              DefaultHandler handler)
        throws IOException, SAXException {

        CharArrayReader reader
            = new CharArrayReader(document.toCharArray());
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setContentHandler(handler);
        InputSource in = new InputSource(reader);
        xmlReader.parse(in);
    }

    /**
     * Parse the specified file encoded in the specified character
     * encoding, sending XML events to the specified handler.  If the
     * character encoding is <code>null</code>, the parser will attempt
     * to infer the encoding.
     *
     * <p>An {@link XMLReader} instance will be created using Java's
     * built-in {@link XMLReaderFactory}.
     *
     * @param file File to parse.
     * @param charEncoding Character encoding for file, or
     * <code>null</code> if it's unknown.
     * @param handler Handler for SAX events from document.
     * @throws SAXException If there is a SAX exception handling the
     * document.
     * @throws IOException If there is an I/O exception reading from
     * the document.
     */
    public static void parse(File file,
                             String charEncoding,
                             DefaultHandler handler)
        throws IOException, SAXException {

        String url = file.toURI().toURL().toString();
        InputSource in = new InputSource(url);
        if (charEncoding != null)
            in.setEncoding(charEncoding);
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setContentHandler(handler);
        xmlReader.parse(in);
    }

    /**
     * Suffix for XML files.
     */
    public static final String XML_SUFFIX = "xml";

    /**
     * Filter for XML files and directories.
     */
    public static final FileFilter XML_FILE_FILTER
        = new FileExtensionFilter(XML_SUFFIX,true);

    /**
     * Filter for XML files only.
     */
    public static final FileFilter XML_FILE_ONLY_FILTER
        = new FileExtensionFilter(XML_SUFFIX,false);

    /**
     * The feature used to set an <code>XMLReader</code> to be
     * validating or not.  To turn on validation, which is off
     * by default, use:

     * <blockquote><code>
     *   xmlReader.setFeature(XML.VALIDATION_FEATURE,true);
     * </code></blockquote>
     *
     * <P>See <a href="http://www.saxproject.org/?selected=get-set">SAX Project Features and Properties</a>.
     */
    public static final String VALIDATION_FEATURE
        = "http://xml.org/sax/features/validation";


    /**
     * The feature used to set an <code>XMLReader</code> to
     * handle namespaces.  This is an expensive feature and
     * turning it off can double parsing speed.  To turn
     * off namespace parsing, which is on by default, use:
     *
     * <blockquote><code>
     *   xmlReader.setFeature(XML.NAMESPACES_FEATURE,true)
     * </code></blockquote>
     *
     * <P>See <a href="http://www.saxproject.org/?selected=get-set">SAX Project Features and Properties</a>.
     */
    public static final String NAMESPACES_FEATURE
    = "http://xml.org/sax/features/namespaces";

    /**
     * The feature used to set the buffer size for the Xerces
     * parser in bytes.  Use
     *
     * <blockquote><code>
     *   XMLReader reader;
     *   <br>
     *   reader.setProperty(XML.XERCES_BUFFER_SIZE_PROPERTY,2048);
     * </code></blockquote>
     *
     * <P>

     * <P>See <a href="http://www.saxproject.org/?selected=get-set">SAX Project Features and Properties</a>.
     * <P> Also see: <a href="http://xml.apache.org/xerces2-j/properties.html">Xerces 2 Properties</a>.
     *
     * @deprecated Configure Xerces on an ad-hoc basis as needed.
     */
    @Deprecated
    public static final String XERCES_BUFFER_SIZE_PROPERTY
        = "http://apache.org/xml/properties/input-buffer-size";


}
