/**
 * Title:        StanfordMaxEnt<p>
 * Description:  A Maximum Entropy Toolkit<p>
 * Copyright:    Copyright (c) Kristina Toutanova<p>
 * Company:      Stanford University<p>
 * @author Kristina Toutanova
 * @version 1.0
 */
package edu.stanford.nlp.tagger.maxent;

/**
 * Extractor whether the last verbal takes a that complement.
 */
class ExtractorLastVerbalTC extends Extractor {
  static int commThat = 0;
  private static final String thatWord = "that";
  private static final String inTag = "IN";
  private static final String wdtTag = "WDT";
  private static final String dtTag = "DT";
  static int[][] counts = new int[3][3];

  public ExtractorLastVerbalTC() {
  }

  public static void printCounts() {
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        System.out.print(counts[i][j] + "\t");
      }
      System.out.println();
    }
  }

  @Override
  String extract(History h, PairsHolder pH) {
    // should extract last verbal word and also the current word
    String cword = ExtractorFrames.cWord.extract(h, pH);
    if (!cword.equals(thatWord)) {
      return "0";
    }
    String ctag = pH.get(h, 0, true);
    int start = h.start;
    String lastverb = "NA";
    int current = h.current;
    int index = current - 1;
    String tag = "NA";
    while (index >= start) {
      tag = pH.get(index, true);
      if (tag.startsWith("VB")) {
        lastverb = pH.get(index, false);
        break;
      }
      index--;
    }
    if (lastverb.startsWith("NA")) {
      return "3";
    }

    if (isThatComplTakingVerb(lastverb, tag)) {
      //System.out.println(" part taking");
      return ("1");
    }
    return ("2");
  }


  @Override
  String extract(History h, PairsHolder pH, int bound) {
    // should extract last verbal word and also the current word
    String cword = ExtractorFrames.cWord.extract(h, pH);
    if (!cword.equals(thatWord)) {
      return "0";
    }
    String lastverb = extractLV(h, pH);
    if (lastverb.startsWith("NA")) {
      return "0";
    }

    if (isThatComplTakingVerb(lastverb, bound)) {
      //System.out.println(" part taking");
      return ("1");
    }
    return ("2");
  }


  boolean isThatComplTakingVerb(String verb, String tag) {
    //System.out.println(verb);
    if (GlobalHolder.dict.sum(verb) == 0) {
      int i = (int) (Math.random() * 2);
      if (i == 0) {
        // pretend the verb takes that complements
        GlobalHolder.dict.addVThatTaking(verb);
        return true;
      }
    }
    return (GlobalHolder.dict.getCountThat(verb) > commThat);
  }


  boolean isThatComplTakingVerb(String verb, int bound) {
    //System.out.println(verb);
    return GlobalHolder.dict.getCountThat(verb) > bound;
  }

  private static final long serialVersionUID = -2324821292516788746L;

}
