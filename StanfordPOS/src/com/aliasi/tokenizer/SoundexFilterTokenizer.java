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

package com.aliasi.tokenizer;

/**
 * The <code>SoundexFilterTokenizer</code> replaces each token with
 * its Soundex encoding.  Soundex replaces sequences of characters
 * with a crude four-character approximation of their pronunciation
 * plus initial letter.
 *
 * <p>The process for converting an input to its Soundex
 * representation is fairly straighforward for inputs that are all
 * ASCII letters.  Soundex is case insensitive, but is only defined
 * for strings of ASCII letters.  Thus to begin, all characters
 * that are not Latin1 letters are removed, and all Latin1 characters
 * are stripped of their diacritics.  The algorithm then proceeds
 * according to its standard definition:
 *
 * <ol>
 * <li>Normalize input by removing all characters that are not
 * Latin1 letters, and converting all other characters to uppercase
 * ASCII after first removing any diacritics.
 * <li>If the input is empty, return "0000"
 * </li>
 * <li>Set the first letter of the output to the first letter of the input.
 * </li>
 * <li>While there are less than four letters of output do:
 *   <ol>
 *   <li>If the next letter is a vowel, unset the last letter's code.
 *   <li>If the next letter is <code>A</code>, <code>E</code>, <code>I</code>, <code>O</code>, <code>U</code>, <code>H</code>, <code>W</code>, <code>Y</code>, continue.
 *   <li>If the next letter's code is equal to the previous letter's
 *       code, continue.
 *   <li>Set the next letter of output to the current letter's code.
 *   </ol>
 * </li>
 * <li>If there are fewer than four characters of output, pad
 * the output with zeros (<code>0</code>)
 * <li>Return the output string.
 * </ol>
 *
 * <p>The table of individual character encodings is as follows:
 *
 * <blockquote><table border='1' cellpadding='5'>
 * <tr><th>Characters</th><th>Code</th></tr>
 * <tr><td>B, F, P, V</td><td>1</td></tr>
 * <tr><td>C, G, J, K, Q, S, X, Z</td><td>2</td></tr>
 * <tr><td>D, T</td><td>3</td></tr>
 * <tr><td>L</td><td>4</td></tr>
 * <tr><td>M, N</td><td>5</td></tr>
 * <tr><td>R</td><td>6</td></tr>
 * <table></blockquote>
 *
 * <p>Here are some examples of translations from the unit tests,
 * drawn from the sources cited below.
 *
 * <blockquote><table border='1' cellpadding='5'>
 * <tr><th>Tokens</th><th>Soundex Encoding</th><th>Notes</th></tr>
 * <tr><td>Gutierrez</td><td>G362</td><td> </td></tr>
 * <tr><td>Pfister</td><td>P236</td><td> </td></tr>
 * <tr><td>Jackson</td><td>J250</td><td> </td></tr>
 * <tr><td>Tymczak</td><td>T522</td><td> </td></tr>
 * <tr><td>Ashcraft</td><td>A261</td><td> </td></tr>
 * <tr><td>Robert, Rupert</td><td>R163</td><td> </td></tr>
 * <tr><td>Euler, Ellery</td><td>E460</td><td> </td></tr>
 * <tr><td>Gauss, Ghosh</td><td>G200</td><td> </td></tr>
 * <tr><td>Hilbert, Heilbronn</td><td>H416</td><td> </td></tr>
 * <tr><td>Knuth, Kant</td><td>K530</td><td> </td></tr>
 * <tr><td>Lloyd, Liddy</td><td>L300</td><td> </td></tr>
 * <tr><td>Lukasiewicz, Lissajous</td><td>L222</td><td> </td></tr>
 * <tr><td>Wachs, Waugh</td><td>W200</td><td></td></tr>
 * </table></blockquote>
 *
 * <p>As a tokenizer filter, the <code>SoundexFilterTokenizer</code>
 * simply replaces each token with its Soundex equivalent.  Note that
 * this may produce very many <code>0000</code> outputs if it is fed
 * standard text with punctuation, numbers, etc.
 *
 * <p>Note: In order to produce a deterministic tokenizer filter,
 * names with prefixes are coded with the prefix.  Recall that
 * Soundex considers the following set of words prefixes, and suggests
 * providing both the Soundex computed with the prefix and the
 * Soundex encoding computed without the prefix:
 *
 * <blockquote><pre>
 * Van, Con, De, Di, La, Le</pre></blockquote>
 *
 * <p>These are not accorded any special treatment by this
 * implementation.
 *
 * <h3>References and Historical Notes</h3>
 *
 * Soundex was invented and patented by Robert C. Russell in 1918.
 * The original version involved eight categories, including one for
 * vowels, without the initial character being treated specially as to
 * coding.  The first vowel was retained in the original Soundex.
 * Furthermore, some positional information was added, such as the
 * deletion of final <code>s</code> and <code>z</code>.
 *
 * <p> The version in this class is the one described by Donald Knuth
 * in <i>The Art of Computer Programming</i> and the one described by
 * the United States National Archives and Records Administration
 * version, which has been used for the United States Census.
 *
 * <ul>
 *
 * <li>Knuth, D.  1973.  <i>The Art of Computer Programming Volum 3: Sorting and Searching</i>. Addison-Wesley.  2nd Edition Pages 394-395.</li>
 *
 * <li>Wikipedia. <a href="http://en.wikipedia.org/wiki/Soundex">Soundex</a>.
 *
 * <li>United States National Archives and Records Administration.
 * <a href="http://www.archives.gov/publications/general-info-leaflets/55.html">Using the Census Soundex.</a>
 * General Information Leaflet 55.
 * </li>
 *
 * <li>Robert C. Russell.  1918. <a
 * href="http://www.pat2pdf.org/pat2pdf/foo.pl?number=1261167">United States Patent
 * 1,261,167</a>.  </li>

 * <li>Robert C. Russell.  1922.
 * <a href="http://www.pat2pdf.org/pat2pdf/foo.pl?number=1435663">United States Patent 1,435,663</a>.
 * </li>
 * </ul>
 *
 *
 * @author Bob Carpenter
 * @version 3.8
 * @since   LingPipe3.2
 * @deprecated Use {@link SoundexTokenizerFactory} instead.
 */
@Deprecated
public class SoundexFilterTokenizer extends FilterTokenizer {

    /**
     * Construct a soundex filter for the specified tokenizer.
     *
     * @param tokenizer Tokenizer to filter.
     * @deprecated Use {@link SoundexTokenizerFactory} instead.
     */
    @Deprecated
    public SoundexFilterTokenizer(Tokenizer tokenizer) {
        super(tokenizer);
    }

    /**
     * Returns the Soundex equivalent of the specified token.  This
     * method simply calls the static method {@link
     * #soundexEncoding(String)} on the specified token.
     *
     * @param token Token to be converted to Soundex.
     * @return The Soundex representation of the specified token.
     */
    public String filter(String token) {
        return SoundexTokenizerFactory.soundexEncoding(token);
    }

    /**
     * Returns the Soundex encoding of the specified token.
     *
     * @param token Token to be encoded.
     * @return The Soundex encoding of the specified token.
     * @deprecated Use {@link
     * SoundexTokenizerFactory#soundexEncoding(String)} instead.
     */
    @Deprecated
    public static String soundexEncoding(String token) {
        return SoundexTokenizerFactory.soundexEncoding(token);
    }


}
