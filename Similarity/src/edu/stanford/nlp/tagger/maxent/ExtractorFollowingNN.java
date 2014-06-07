
/**
 * Title:        StanfordMaxEnt<p>
 * Description:  A Maximum Entropy Toolkit<p>
 * Copyright:    Copyright (c) Kristina Toutanova<p>
 * Company:      Stanford University<p>
 * @author Kristina Toutanova
 * @version 1.0
 */


package edu.stanford.nlp.tagger.maxent;

class ExtractorFollowingNN extends Extractor {

  private static final long serialVersionUID = -4668571115564345146L;

  static Extractor nextWord = new Extractor(1, false);
  private static final String thatWord = "that";
  private static final String nnTag = "NN";
  private static final String nnsTag = "NNS";
  // private static final String nnpTag = "NNP";
  // private static final String nnpsTag = "NNPS";

  public ExtractorFollowingNN() {
  }

  @Override
  String extract(History h, PairsHolder pH) {
    // should extract last verbal word and also the current word
    String cword = ExtractorFrames.cWord.extract(h, pH);
    String nWord = nextWord.extract(h, pH);
    if (!cword.equals(thatWord)) {
      return "0";
    }

    if (nWord.startsWith("NA")) {
      return "0";
    }

    if (isNSCount(nWord)) {
      //System.out.println(" part taking");
      return ("1");
    }
    return ("0");
  }

  public static boolean isNSCount(String word) {
    //System.out.println(verb);
    // if(GlobalHolder.dict.getCount(word,nnsTag)>0) return false;
    // if(GlobalHolder.dict.getCount(word,nnpTag)>0) return false;
    //if(GlobalHolder.dict.getCount(word,nnpsTag)>0) return false;
    if (GlobalHolder.dict.getCount(word, nnTag) > 0) {
      String word1 = TestSentence.toNice(word) + "s";
      if (GlobalHolder.dict.getCount(word1, nnsTag) > 0) {
        return true;
      }
    }
    return false;
  }

}


