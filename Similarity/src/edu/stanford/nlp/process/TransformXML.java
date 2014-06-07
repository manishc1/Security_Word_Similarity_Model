package edu.stanford.nlp.process;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.nlp.util.Function;


/**
 * Reads XML from an input file or stream and writes XML to an output
 * file or stream, while transforming text appearing inside specified
 * XML tags by applying a specified {@link Function
 * <code>Function</code>}.  See TransformXMLApplications for examples.
 *
 * @author Bill MacCartney
 * @author Anna Rafferty (refactoring, making SAXInterface easy to extend elsewhere)
 */
public class TransformXML {

  private SAXParser saxParser;

  private InputStream inStream;

  private SAXInterface saxInterface = new SAXInterface();

  public static class SAXInterface extends DefaultHandler {
    protected List<String> elementsToBeTransformed;
    protected boolean inElementToBeTransformed;
    protected StringBuffer textToBeTransformed;
    protected PrintStream outStream = System.out;
    protected Function function;
    // Called at the beginning of each element.  If the tag is on the
    // designated list, set flag to remember that we're in an element
    // to be transformed.  In either case, echo tag.
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (elementsToBeTransformed.contains(qName)) {
        inElementToBeTransformed = true;
      }
      // echo tag to outStream
      outStream.print("<" + qName);
      for (int i = 0; i < attributes.getLength(); i++) {
        outStream.print(" " + attributes.getQName(i) + "=\"" + attributes.getValue(i) + "\"");
      }
      outStream.println(">");
    }

    // Called at the end of each element.  If the tag is on the
    // designated list, apply the designated {@link Function <code>Function</code>} to the
    // accumulated text and echo the the result.  In either case, echo
    // the closing tag.
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      //System.err.println("function is " + function.getClass());
      //System.err.println("elementsToBeTransformed is " + elementsToBeTransformed);
      //System.err.println("textToBeTransformed is " + textToBeTransformed);
      String text = textToBeTransformed.toString().trim();
      if (elementsToBeTransformed.contains(qName)) {
        Object result = function.apply(text);
        outStream.println(result);
      } else {
        outStream.println(text);
      }
      outStream.println("</" + qName + ">");
      clear();
    }

    // Accumulate characters in buffer of text to be transformed
    // (SAX may call this after each line break)
    @Override
    public void characters(char[] buf, int offset, int len) throws SAXException {
        textToBeTransformed.append(buf, offset, len);
    }

    // Forget where we are in the document
    protected void clear() {
      inElementToBeTransformed = false;
      textToBeTransformed = new StringBuffer();
    }

  } // end SAXInterface

  public TransformXML() {
    try {
      saxParser = SAXParserFactory.newInstance().newSAXParser();
    } catch (Exception e) {
      System.err.println("Error configuring XML parser: " + e);
      e.printStackTrace();
    }
    saxInterface.elementsToBeTransformed = new ArrayList<String>();
    saxInterface.clear();
  }



  // Read XML from input stream and invoke SAX methods on it
  private void read() {
    try {
      saxInterface.clear();
      saxParser.parse(inStream, saxInterface);
    } catch (Exception e) {
      System.err.println("Error in XML parser: " + e);
      e.printStackTrace();
    }
  }

  /**
   * Read XML from the specified file and write XML to stdout,
   * while transforming text appearing inside the specified XML
   * tags by applying the specified {@link Function
   * <code>Function</code>}.  Note that the <code>Function</code>
   * you supply must be prepared to accept <code>String</code>s as
   * input; if your <code>Function</code> doesn't handle
   * <code>String</code>s, you need to write a wrapper for it that
   * does.
   *
   * @param tags an array of <code>String</code>s, each an XML tag
   *             within which the transformation should be applied
   * @param fn   the {@link Function <code>Function</code>} to apply
   * @param in   the <code>File</code> to read from
   */
  public void transformXML(String[] tags, Function fn, File in) {
    InputStream ins = null;
    try {
      ins = new BufferedInputStream(new FileInputStream(in));
    } catch (Exception e) {
      System.err.println("Error reading file " + in + ": " + e);
      e.printStackTrace();
    }
    transformXML(tags, fn, ins, System.out);
  }

  /**
   * Read XML from input stream and write XML to stdout, while
   * transforming text appearing inside the specified XML tags by
   * applying the specified {@link Function <code>Function</code>}.
   * Note that the <code>Function</code> you supply must be
   * prepared to accept <code>String</code>s as input; if your
   * <code>Function</code> doesn't handle <code>String</code>s, you
   * need to write a wrapper for it that does.
   *
   * @param tags an array of <code>String</code>s, each an XML tag
   *             within which the transformation should be applied
   * @param fn   the {@link Function <code>Function</code>} to apply
   * @param in   the <code>InputStream</code> to read from
   */
  public void transformXML(String[] tags, Function fn, InputStream in) {
    transformXML(tags, fn, in, System.out);
  }

  /**
   * Read XML from the specified file and write XML to specified file,
   * while transforming text appearing inside the specified XML tags
   * by applying the specified {@link Function <code>Function</code>}.
   * Note that the <code>Function</code> you supply must be
   * prepared to accept <code>String</code>s as input; if your
   * <code>Function</code> doesn't handle <code>String</code>s, you
   * need to write a wrapper for it that does.
   *
   * @param tags an array of <code>String</code>s, each an XML tag
   *             within which the transformation should be applied
   * @param fn   the {@link Function <code>Function</code>} to apply
   * @param in   the <code>File</code> to read from
   * @param out  the <code>File</code> to write to
   */
  public void transformXML(String[] tags, Function fn, File in, File out) {
    InputStream ins = null;
    OutputStream outs = null;
    try {
      ins = new BufferedInputStream(new FileInputStream(in));
    } catch (Exception e) {
      System.err.println("Error reading file " + in + ": " + e);
      e.printStackTrace();
    }
    try {
      outs = new BufferedOutputStream(new FileOutputStream(out));
    } catch (Exception e) {
      System.err.println("Error writing file " + out + ": " + e);
      e.printStackTrace();
    }
    transformXML(tags, fn, ins, outs);
  }

  /**
   * Read XML from input stream and write XML to output stream,
   * while transforming text appearing inside the specified XML tags
   * by applying the specified {@link Function <code>Function</code>}.
   * Note that the <code>Function</code> you supply must be
   * prepared to accept <code>String</code>s as input; if your
   * <code>Function</code> doesn't handle <code>String</code>s, you
   * need to write a wrapper for it that does.
   *
   * @param tags an array of <code>String</code>s, each an XML tag
   *             within which the transformation should be applied
   * @param fn   the {@link Function <code>Function</code>} to apply
   * @param in   the <code>InputStream</code> to read from
   * @param out  the <code>OutputStream</code> to write to
   */
  public void transformXML(String[] tags, Function fn, InputStream in, OutputStream out) {
    transformXML(tags,fn,in,out,saxInterface);
  }


  /**
   * Read XML from input stream and write XML to output stream,
   * while transforming text appearing inside the specified XML tags
   * by applying the specified {@link Function <code>Function</code>}.
   * Note that the <code>Function</code> you supply must be
   * prepared to accept <code>String</code>s as input; if your
   * <code>Function</code> doesn't handle <code>String</code>s, you
   * need to write a wrapper for it that does.
   *
   * @param tags an array of <code>String</code>s, each an XML tag
   *             within which the transformation should be applied
   * @param fn   the {@link Function <code>Function</code>} to apply
   * @param in   the <code>InputStream</code> to read from
   * @param out  the <code>OutputStream</code> to write to
   * @param handler the sax handler you would like to use (default is SaxInterface, defined in this class, but you may define your own handler)
   */
  public void transformXML(String[] tags, Function fn, InputStream in, OutputStream out, SAXInterface handler) {
    saxInterface = handler;
    inStream = in;
    saxInterface.outStream = new PrintStream(out);
    saxInterface.function = fn;
    saxInterface.elementsToBeTransformed = new ArrayList<String>();
    saxInterface.elementsToBeTransformed.addAll(Arrays.asList(tags));
    read();
  }



} // end class TransformXML
