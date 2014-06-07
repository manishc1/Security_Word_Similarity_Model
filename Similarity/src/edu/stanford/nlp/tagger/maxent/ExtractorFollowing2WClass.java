/**
 * Title:        StanfordMaxEnt<p>
 * Description:  A Maximum Entropy Toolkit<p>
 * Copyright:    Copyright (c) Kristina Toutanova<p>
 * Company:      Stanford University<p>
 * @author Kristina Toutanova
 * @version 1.0
 */

package edu.stanford.nlp.tagger.maxent;


public class ExtractorFollowing2WClass extends Extractor {

  private static final long serialVersionUID = 208892521920629670L;

  static Extractor nextWord = new Extractor(1, false);
  static Extractor next2Word = new Extractor(2, false);

  @Override
  String extract(History h, PairsHolder pH) {
    // should extract last verbal word and also the current word

    String nWord = nextWord.extract(h, pH);
    String n2Word = next2Word.extract(h, pH);
    int classId = GlobalHolder.dict.getAmbClass(nWord);
    int class2Id = GlobalHolder.dict.getAmbClass(n2Word);
    //System.out.println(nWord);
    //GlobalHolder.ambClasses.get(classId).print();
    return classId + "|" + class2Id;
  }

  public ExtractorFollowing2WClass() {
  }

}
