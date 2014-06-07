package edu.stanford.nlp.sequences;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.PrevSGMLAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.AfterSGMLAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.BeforeAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.AfterAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CurrentAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PositionAnnotation;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.WordToSentenceProcessor;
import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * @author Jenny Finkel
 */
public class PlainTextDocumentReaderAndWriter implements DocumentReaderAndWriter {

  private static final long serialVersionUID = -2420535144980273136L;

  private SeqClassifierFlags flags = null;
  public void init(SeqClassifierFlags flags) {
    this.flags = flags;
  }

  private static final Pattern sgml = Pattern.compile("<[^>]*>");
  private static final WordToSentenceProcessor wts = new WordToSentenceProcessor();

  public Iterator<List<CoreLabel>> getIterator(Reader r) {
    PTBTokenizer<CoreLabel> ptb = PTBTokenizer.newPTBTokenizer(r, false, true);
    List<CoreLabel> words = new ArrayList<CoreLabel>();
    CoreLabel previous = new CoreLabel();
    String prepend = "";

    boolean wasSGML = false;
    
    while (ptb.hasNext()) {
      CoreLabel w = ptb.next();
      Matcher m = sgml.matcher(w.word());
      if (m.matches()) {
        String sgml = w.word() + w.after();
        prepend += w.before() + w.word();
        previous.appendAfter(sgml);
        wasSGML = true;
      } else {
        w.prependBefore(prepend);
        prepend = "";
        words.add(w);
        previous = w;

        wasSGML = false;
      }
    }

    List<List<CoreLabel>> sentences = wts.process(words);
    String after = "";
    CoreLabel last = null;
    for (List<CoreLabel> sentence : sentences) {
      int pos = 0;
      for (CoreLabel w : sentence) {
        w.set(PositionAnnotation.class, Integer.toString(pos));
        after = w.after();
        w.remove(AfterAnnotation.class);
        last = w;
      }      
    }
    if (last != null) { last.set(AfterAnnotation.class, after); }
    
    return sentences.iterator();
//    return documents.iterator();
  }

  public Iterator<List<CoreLabel>> getIteratorOld(Reader r) {
    PTBTokenizer<CoreLabel> ptb = PTBTokenizer.newPTBTokenizer(r, false, true);
    List firstSplit = new ArrayList();
    List<CoreLabel> d = new ArrayList<CoreLabel>();

    while (ptb.hasNext()) {
      CoreLabel w = ptb.next();
      Matcher m = sgml.matcher(w.word());
      if (m.matches()) {
        if (d.size() > 0) {
          firstSplit.add(d);
          d = new ArrayList<CoreLabel>();
        }
        firstSplit.add(w);
        continue;
      }
      d.add(w);
    }
    if (d.size() > 0) {
      firstSplit.add(d);
    }

    List secondSplit = new ArrayList();
    for (Object o : firstSplit) {
      if (o instanceof List) {
        secondSplit.addAll(wts.process((List) o));
      } else {
        secondSplit.add(o);
      }
    }

    String prevTags = "";
    CoreLabel lastWord = null;

    List<List<CoreLabel>> documents = new ArrayList<List<CoreLabel>>();

    boolean first = true;

    for (Object o : secondSplit) {
      if (o instanceof List) {
        List doc = (List) o;
        List<CoreLabel> document = new ArrayList<CoreLabel>();
        int pos = 0;
        for (Iterator wordIter = doc.iterator(); wordIter.hasNext(); pos++) {
          CoreLabel w = (CoreLabel) wordIter.next();
          w.set(PositionAnnotation.class, Integer.toString(pos));
          if (first && prevTags.length() > 0) {
            w.set(PrevSGMLAnnotation.class, prevTags);
          }
          first = false;
          lastWord = w;
          document.add(w);
        }
        documents.add(document);
      } else {
        //String tag = ((Word) o).word();
        CoreLabel word = (CoreLabel) o;        
        String tag = word.before() + word.current();
        if (first) {
          System.err.println(word);
          prevTags = tag;
        } else {
          String t = lastWord.getString(AfterSGMLAnnotation.class);
          tag = t + tag;
          lastWord.set(AfterSGMLAnnotation.class, tag);
        }
      }
    }

    // this is a hack to deal with the incorrect assumption in the above code that
    // SGML only occurs between sentences and never inside of them.
    List<CoreLabel> allWords = new ArrayList<CoreLabel>();
    for (List<CoreLabel> doc : documents) {
      allWords.addAll(doc);
    }

    List<List<CoreLabel>> documentsFinal = wts.process(allWords);
    System.err.println(documentsFinal.get(0).get(0));
    System.exit(0);
    
    return documentsFinal.iterator();
//    return documents.iterator();
  }

  public static String getAnswers(List<CoreLabel> l) {

    StringBuilder sb = new StringBuilder();
    String after = "";
    for (CoreLabel wi : l) {
      String prev = wi.getString(BeforeAnnotation.class);
      after = wi.getString(AfterAnnotation.class);
      sb.append(prev).append(wi.word()).append('/').append(wi.getString(AnswerAnnotation.class));
    }
    sb.append(after);

    return sb.toString();
  }

  public void printAnswers(List<CoreLabel> l) {
    PrintWriter pw = new PrintWriter(System.out);
    printAnswers(l, pw);
    pw.flush();
    pw.close();
  }

  /** Print the classifications for the document to the given Writer.
   *  This method now checks the <code>outputFormat</code> property,
   *  and can print in text, inlineXML, or standOffXML.
   */
  public void printAnswers(List<CoreLabel> l, PrintWriter out) {
    if (flags != null) {
      String style = flags.outputFormat;
      if ("inlineXML".equalsIgnoreCase(style)) {
        printAnswersInlineXML(l, out);
      } else if ("xml".equalsIgnoreCase(style)) {
        printAnswersXML(l, out);
      } else {
        printAnswersText(l, out);
      }
    } else {
      printAnswersText(l, out);
    }
  }

  public void printAnswersText(List<CoreLabel> l, PrintWriter out) {
    for (CoreLabel wi : l) {
      String prev = wi.getString(PrevSGMLAnnotation.class);
      String after = wi.getString(AfterSGMLAnnotation.class);
//      out.print("["+prev+"]" + "{"+wi.word()+"}" + "/" + "("+wi.getString(AnswerAnnotation.class)+")" + "<"+after+">" + " ");
      out.print(prev + wi.word() + '/' + wi.getString(AnswerAnnotation.class) + after + ' ');
    }
  }

  public static String getAnswersXML(List<CoreLabel> l) {
    StringWriter sw = new StringWriter();
    printAnswersXML(l, new PrintWriter(sw));
    sw.flush();
    return sw.toString();
  }

  public static void printAnswersXML(List<CoreLabel> l) {
    PrintWriter pw = new PrintWriter(System.out);
    printAnswersXML(l, pw);
    pw.flush();
    pw.close();
  }

  public static void printAnswersXML(List<CoreLabel> doc, PrintWriter out) {
    int num = 0;
    for (CoreLabel wi : doc) {
      String prev = wi.getString(PrevSGMLAnnotation.class);
      out.print(prev);
      StringBuilder tag = new StringBuilder();
      tag.append("<wi num=");
      //tag.append(wi.get("position"));
      tag.append(num++);
      tag.append(" entity=");
      tag.append(wi.getString(AnswerAnnotation.class));
      tag.append('>');
      tag.append(wi.word());
      tag.append("</wi>");
      out.print(tag);
      String after = wi.getString(AfterSGMLAnnotation.class);
      out.println(after);
    }
  }

  public static String getAnswersInlineXML(List<CoreLabel> l) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    printAnswersInlineXML(l, pw);
    pw.flush();
    return sw.toString();
  }

  public void printAnswersInlineXML(List<CoreLabel> l) {
    PrintWriter pw = new PrintWriter(System.out);
    printAnswersInlineXML(l, pw);
    pw.flush();
    pw.close();
  }

  // TODO: This hardcodes the background symbol; really need to pass it in to the method from flags
  public static void printAnswersInlineXML(List<CoreLabel> doc, PrintWriter out) {
    String prevTag = "O";
    for (Iterator<CoreLabel> wordIter = doc.iterator(); wordIter.hasNext(); ) {
      CoreLabel wi = wordIter.next();
      String prev = wi.getString(PrevSGMLAnnotation.class);
      out.print(prev);
      if (prev.length() > 0) {
        prevTag = "O";
      }
      String tag = wi.getString(AnswerAnnotation.class);
      if ( ! tag.equals(prevTag)) {
        if ( ! prevTag.equals("O") && ! tag.equals("O")) {
          out.print("</");
          out.print(prevTag);
          out.print('>');
          out.print(wi.getString(BeforeAnnotation.class));
          out.print('<');
          out.print(tag);
          out.print('>');
        } else if ( ! prevTag.equals("O")) {
          out.print("</");
          out.print(prevTag);
          out.print('>');
          out.print(wi.getString(BeforeAnnotation.class));
        } else if ( ! tag.equals("O")) {
          out.print(wi.getString(BeforeAnnotation.class));
          out.print('<');
          out.print(tag);
          out.print('>');
        }
      } else {
        out.print(wi.getString(BeforeAnnotation.class));
      }
      out.print(wi.getString(CurrentAnnotation.class));
      String after = wi.getString(AfterSGMLAnnotation.class);
      if ( ! tag.equals("O") && ( ! wordIter.hasNext() || after.length() > 0)) {
        out.print("</");
        out.print(tag);
        out.print('>');
        prevTag = "O";
      } else {
        prevTag = tag;
      }
      out.print(after);
    }
  }

}
