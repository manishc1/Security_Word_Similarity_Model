/**
 * Title:        StanfordMaxEnt<p>
 * Description:  A Maximum Entropy Toolkit<p>
 * Copyright:    Copyright (c) Kristina Toutanova<p>
 * Company:      Stanford University<p>
 */


package edu.stanford.nlp.tagger.maxent;

import java.io.PrintStream;

import edu.stanford.nlp.io.InDataStreamFile;
import edu.stanford.nlp.io.OutDataStreamFile;


/**
 *
 *  @author Kristina Toutanova
 *  @version 1.0
 */
public class History {
  int start;  // this is the index of the first word of the sentence
  int end;    //this is the index of the last word in the sentence - the dot
  int current; // this is the index of the current word

  public History() {
  }

  public History(int start, int end, int current) {
    this.start = start;
    this.end = end;
    this.current = current;
    // init();
  }

  public void save(OutDataStreamFile rf) {
    try {
      rf.writeInt(start);
      rf.writeInt(end);
      rf.writeInt(current);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void read(InDataStreamFile rf) {
    try {
      start = rf.readInt();
      end = rf.readInt();
      current = rf.readInt();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public String getX(int index) {
    // get the string by the index in x
    return GlobalHolder.extractors.get(index).extract(this);
  }

  public String getX(int index, PairsHolder pH) {
    // get the string by the index in x
    return GlobalHolder.extractors.get(index).extract(this, pH);
  }


  public String[] getX() {
    String[] x = new String[GlobalHolder.extractors.getSize()];
    for (int i = 0; i < x.length; i++) {
      x[i] = getX(i);
    }
    return x;
  }


  void print(PrintStream ps) {
    String[] str = getX();
    for (String aStr : str) {
      ps.print(aStr);
      ps.print('\t');
    }
    ps.println();
  }

  public void printSent() {
    print(System.out);

    for (int i = this.start; i < this.end; i++) {
      System.out.print(GlobalHolder.pairs.get(i, true) + ' ' + GlobalHolder.pairs.get(i, false) + '\t');
    }
    System.out.println();
  }


  public void setTag(int pos, String tag) {
    GlobalHolder.pairs.setTag(pos + start, tag);
  }


  public String[] getX(PairsHolder pH) {
    String[] x = new String[GlobalHolder.extractors.getSize()];
    for (int i = 0; i < x.length; i++) {
      x[i] = getX(i, pH);
    }
    return x;
  }

  public void set(int start, int end, int current) {
    this.start = start;
    this.end = end;
    this.current = current;
    // init();
  }

  // public void init() {
  //   add here how from the history one fills out the x array and fill it
  //   or may be there is no need for an x array, try to do without it
  // }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    String[] str = getX();
    for (String aStr : str) {
      sb.append(aStr).append('\t');
    }
    return sb.toString();
  }

  @Override
  public int hashCode() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < GlobalHolder.extractors.getSize(); i++) {
      sb.append(getX(i));
    }
    return sb.toString().hashCode();
  }


  @Override
  public boolean equals(Object h1) {
    if (!(h1 instanceof History)) {
      return false;
    }
    return GlobalHolder.extractors.equals(this, (History) h1);
  }

}
