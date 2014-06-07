/**
 * Title:        StanfordMaxEnt<p>
 * Description:  A Maximum Entropy Toolkit<p>
 * Copyright:    Copyright (c) Kristina Toutanova<p>
 * Company:      Stanford University<p>
 * @author Kristina Toutanova
 * @version 1.0
 */
package edu.stanford.nlp.tagger.maxent;

public class ExtractorFollowingWClass extends Extractor {

  private static final long serialVersionUID = -1718985062915165188L;

  private Extractor nextXWord;

  @Override
  String extract(History h, PairsHolder pH) {
    // should extract last verbal word and also the current word
    String nWord = nextXWord.extract(h, pH);
    int classId = GlobalHolder.dict.getAmbClass(nWord);
    //System.out.println(nWord);
    //GlobalHolder.ambClasses.get(classId).print();
    return Integer.toString(classId);
  }

  public ExtractorFollowingWClass(int num) {
    nextXWord = new Extractor(num, false);
  }

}
