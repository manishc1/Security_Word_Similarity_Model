package edu.stanford.nlp.tagger.maxent;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import edu.stanford.nlp.process.TransformXML;

/**
 *
 * Based on SAXInterface in TransformXML, but discards internal tags in fields to be tagged,
 * tagging all content in the fields.
 * @author Anna Rafferty
 *
 */
public class TaggerSaxInterface extends TransformXML.SAXInterface {


  // Called at the beginning of each element.  If the tag is on the
  // designated list, set flag to remember that we're in an element
  // to be transformed.  In either case, echo tag.
  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if (elementsToBeTransformed.contains(qName)) {
      inElementToBeTransformed = true;
    }
    // echo tag to outStream
    if(!inElementToBeTransformed || elementsToBeTransformed.contains(qName)) {
      outStream.print("<" + qName);
      for (int i = 0; i < attributes.getLength(); i++) {
        outStream.print(" " + attributes.getQName(i) + "=\"" + attributes.getValue(i) + "\"");
      }
      outStream.println(">");
    }

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
      if(!inElementToBeTransformed || elementsToBeTransformed.contains(qName))
        outStream.println(text);
    }
    if(!inElementToBeTransformed || elementsToBeTransformed.contains(qName))
      outStream.println("</" + qName + ">");
    if(!inElementToBeTransformed || elementsToBeTransformed.contains(qName))
      clear();
  }

  // Accumulate characters in buffer of text to be transformed
  // (SAX may call this after each line break)
  @Override
  public void characters(char[] buf, int offset, int len) throws SAXException {
    textToBeTransformed.append(buf, offset, len);
  }


}
