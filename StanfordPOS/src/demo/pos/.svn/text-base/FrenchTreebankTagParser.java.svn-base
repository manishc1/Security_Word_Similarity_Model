package demo.pos;
import com.aliasi.corpus.TagHandler;
import com.aliasi.corpus.XMLParser;

import com.aliasi.xml.DelegateHandler;
import com.aliasi.xml.DelegatingHandler;
import com.aliasi.xml.TextAccumulatorHandler;

import com.aliasi.util.ObjectToCounterMap;

import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;


/**
 * Note that there is no whitespace information available in this
 * corpus, so the whitespace arrays will be <code>null</code> for
 * the handlers.
 *
 * <p>The only difference in functional annotations is some fct
 * elements.
 */
public class FrenchTreebankTagParser extends XMLParser<TagHandler> {

    public FrenchTreebankTagParser() { }

    protected DefaultHandler getXMLHandler() {
        return new FrenchTreebankXmlHandler(getHandler());
    }


    private static final Map<String,String> TAG_CORRECTION_MAP
        = new HashMap<String,String>();
    static {
        // unknown tags to null
        TAG_CORRECTION_MAP.put("",null);
        TAG_CORRECTION_MAP.put("*UNKNOWN*",null);
        TAG_CORRECTION_MAP.put("_unknown_",null);

        // easy corrections
        TAG_CORRECTION_MAP.put("ADVP","ADV"); // just phrasal
        TAG_CORRECTION_MAP.put("Afs","A");  // extra functional
        TAG_CORRECTION_MAP.put("CS","C");  // even though valid, only 2 in corpus!
        TAG_CORRECTION_MAP.put("CC","C");  // CC converted to C in data
        TAG_CORRECTION_MAP.put("V ","V");  // extra space

        // don't know about these examples
        TAG_CORRECTION_MAP.put("S",null);  // V?
        TAG_CORRECTION_MAP.put("PC",null); // C or P?
        TAG_CORRECTION_MAP.put("X",null);  // ?
        TAG_CORRECTION_MAP.put("ND",null);  // N or D?
        TAG_CORRECTION_MAP.put("PRE",null);  // PREF?
        TAG_CORRECTION_MAP.put("W",null);  // ?
        TAG_CORRECTION_MAP.put("pr\u00E8s",null); // ?? V
    }

    static String fixCharset(String x) {
        if (alphaNumPunct(x))
            return x;

        String y = null;
        try {
            y = transcode(x);
            if (alphaNumPunct(y))
                return y;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        return null;
    }

    static String transcode(String x) throws UnsupportedEncodingException {
        byte[] bs = x.getBytes("ISO-8859-1");
        return new String(bs,0,bs.length,
                          "UTF-8");
    }


    static boolean alphaNum(String x) {
        for (int i = 0; i < x.length(); ++i)
            if (!Character.isLetterOrDigit(x.charAt(i)))
                return false;
        return true;
    }

    static boolean alphaNumPunct(String x) {
        for (int i = 0; i < x.length(); ++i)
            if (!Character.isLetterOrDigit(x.charAt(i)) && !legalPunct(x.charAt(i)))
                return false;
        return true;
    }

    static boolean legalPunct(char c) {
        return c == '\''
            || c == '"'

            || c == '-'
            || c == '_'

            || c == '&'
            || c == '/'

            || c == ','
            || c == ';'
            || c == ':'
            || c == '.'
            || c == '?'
            || c == '!'

            || c == '+'
            || c == '='
            || c == '*'
            || c == '%'

            || c == '<'
            || c == '>'
            || c == '('
            || c == ')'
            || c == '['
            || c == ']'

            || c == ' '
            || (c == (char) 0xb2) // 2nd superscript
            || (c == (char) 0xb3) // 3rd superscript
            || (c == (char) 0xb0) // degrees (as in celsius)
            ;
    }


    static void correctAndHandle(String[] tokens, String[] tags, TagHandler handler) {

        // trim
        for (int i = 0; i < tokens.length; ++i)
            tokens[i] = tokens[i].trim();
        for (int i = 0; i < tags.length; ++i)
            if (tags[i] != null)
                tags[i] = tags[i].trim();

        // re-map known cats errors
        for (int i = 0; i < tags.length; ++i)
            if (TAG_CORRECTION_MAP.containsKey(tags[i]))
                tags[i] = TAG_CORRECTION_MAP.get(tags[i]);

        // charset norm tokens
        for (int i = 0; i < tokens.length; ++i)
            tokens[i] = fixCharset(tokens[i]);

        handler.handle(tokens,null,tags);
    }

    static class FrenchTreebankXmlHandler extends DelegatingHandler {
        private final TagHandler mTagHandler;
        private final SentenceHandler mSentenceHandler = new SentenceHandler();
        public FrenchTreebankXmlHandler(TagHandler tagHandler) {
            mTagHandler = tagHandler;
            setDelegate("SENT",mSentenceHandler);
        }
        public void finishDelegate(String qName, DefaultHandler handler) {
            if ("SENT".equals(qName)) {
                String[] tokens = mSentenceHandler.getTokens();
                String[] tags = mSentenceHandler.getTags();
                correctAndHandle(tokens,tags,mTagHandler);
            }
        }
    }



    static class SentenceHandler extends DefaultHandler {
        private final List<String> mTokenList = new ArrayList<String>();
        private final List<String> mTagList = new ArrayList<String>();
        private final StringBuilder mTokenBuf = new StringBuilder();
        private String mTagBuf = null;
        private boolean mInToken = false;

        public void startDocument() {
            mTokenList.clear();
            mTagList.clear();
            mInToken = false;
        }
        public void startElement(String url, String localName,
                                 String qName, Attributes atts) {
            if (qName.equals("w") && !"yes".equals(atts.getValue("compound"))) {
                mInToken = true;
                mTokenBuf.setLength(0);
                String internalCat = atts.getValue("catint");
                mTagBuf = internalCat != null ? internalCat : atts.getValue("cat");
            }
        }
        public void endElement(String url, String localName, String qName) {
            if (mInToken && "w".equals(qName)) {
                mInToken = false;
                if (mTokenBuf.length() > 0) {
                    mTokenList.add(mTokenBuf.toString());
                    mTagList.add(mTagBuf);
                }
            }
        }
        public void characters(char[] cs, int start, int length) {
            if (mInToken)
                mTokenBuf.append(cs,start,length);
        }

        public String[] getTokens() {
            return mTokenList.<String>toArray(new String[mTokenList.size()]);
        }
        public String[] getTags() {
            return mTagList.<String>toArray(new String[mTagList.size()]);
        }
    }


}