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

package com.aliasi.corpus;

import com.aliasi.util.Streams;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.xml.sax.InputSource;

/**
 * A <code>LineParser</code> provides an abstract adapter for
 * line-based parsing.  For each line of the input, the abstract
 * method {@link #parseLine(String,int)} is called.  The newline marker
 * character(s) are not part of the line sent to the line parser
 * method.  
 *
 * <p>The notion of line is defined by {@link
 * BufferedReader#readLine()}.  As in that method, newlines may be
 * marked by any of the markers in the following table:
 *
 * <table border="1">
 * <tr><th>Marker</th><th>Character Sequence</th></tr>
 * <tr><td>line feed</td><td align="center"><code>\n</code></td></tr>
 * <tr><td>carriage return</td><td align="center"><code>\r</code></td></tr>
 * <tr><td>carriage return, line feed</td><td align="center"><code>\r\n</code></td></tr>
 * </table>
 *
 * <p>The newline marker is not returned as part of the line.
 * Zero-length lines will be included as lines except for the very
 * last line.  The following table shows some example:
 *
 * <table border="1">
 * <tr><th>Input</th><th>Lines</th></tr>
 * <tr><td><code>&quot;&quot;</code></td>
 *     <td><code></code></td></tr>
 * <tr><td><code>&quot;\n&quot;</code></td>
 *     <td><code>&quot;&quot;</code></td></tr>
 * <tr><td><code>&quot;\n\n&quot;</code></td>
 *     <td><code>&quot;&quot;, &quot;&quot;</code></td></tr>
 * <tr><td><code>&quot;abc&quot;</code></td>
 *     <td><code>&quot;abc&quot;</code></td></tr>
 * <tr><td><code>&quot;abc\n&quot;</code></td>
 *     <td><code>&quot;abc&quot;</code></td></tr>
 * <tr><td><code>&quot;abc\rdef&quot;</code></td>
 *     <td><code>&quot;abc&quot;, &quot;def&quot;</code></td></tr>
 * <tr><td><code>&quot;\nabc\rdef\r\n\n&quot;</code></td>
 *     <td><code>&quot;&quot;, &quot;abc&quot;, &quot;def&quot;, &quot;&quot;</code></td></tr>
 * </table>
 *
 * <p>It is up to the line parser method to handle comments, empty
 * lines, and other conditions defined by the particular input format
 * to be parsed.
 *
 * @author  Bob Carpenter
 * @version 3.5
 * @since   LingPipe3.5
 * @param <H> the type of handler to which this parser sends events
 */
public abstract class LineParser<H extends Handler> extends InputSourceParser<H> {

    /**
     * Construct a line parser without a specified handler.
     * The handler may be set after construction.
     */
    public LineParser() { 
        /* no op constructor */
    }

    /**
     * Construct a line parser with the handler set to the
     * specified handler.  The handler may be reset after
     * construction.
     *
     * @param handler Handler to use. 
     */
    public LineParser(H handler) {
	super(handler);
    }

    /**
     * Parse the specified input source by converting to a reader and
     * then using a buffered reader to parse out lines.
     *
     * <p>The reader or input stream from the input source will be
     * closed before this method exits.
     *
     * @param inSrc Input source from which to extract a reader.
     * @throws IOException If there is an underlying I/O error
     * from the input source's input stream or reader.
     */
    @Override
    public void parse(InputSource inSrc) throws IOException {
	InputStream in = null;
	Reader reader = null;
	BufferedReader bufReader = null;
	try {
	    reader = inSrc.getCharacterStream();
	    if (reader == null) {
		in = inSrc.getByteStream();
                if (in == null) {
                    String systemId = inSrc.getSystemId();
                    if (systemId == null) {
                        String msg = "One of character stream, byte stream, or system ID must be non-null.";
                        throw new IllegalArgumentException(msg);
                    }
                    URI uri = null;
                    URL url = null;
                    try {
                        uri = new URI(systemId);
                        url = uri.toURL();
                        in = url.openStream();
                    } catch (URISyntaxException e) {
                        String msg = "URI syntax exception with systemId=" + systemId;
                        throw new IllegalArgumentException(msg,e);
                    } catch (MalformedURLException e) {
                        String msg = "URL must be well fromed. SystemId=" + systemId;
                        throw new IllegalArgumentException(msg,e);
                    }
                }
		String encoding = inSrc.getEncoding();
		reader = (encoding == null)
		    ? new InputStreamReader(in)
		    : new InputStreamReader(in,encoding);
	    }
	    bufReader = new BufferedReader(reader);
	    String line;
	    for (int lineNumber = 0; (line = bufReader.readLine()) != null; ++lineNumber)
		parseLine(line,lineNumber);
	} finally {
	    Streams.closeReader(bufReader);
	    Streams.closeReader(reader);
	    Streams.closeInputStream(in);
	}
    }


    /**
     * Handle a line of input.  The end-of-line characters are not
     * included in the line. 
     *
     * @param line Line to parse.
     * @param lineNumber Line number being parsed.
     */
    protected abstract void parseLine(String line, int lineNumber);

}
