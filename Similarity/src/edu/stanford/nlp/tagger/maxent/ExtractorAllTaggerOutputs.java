
/**
 * Title:        StanfordMaxEnt<p>
 * Description:  A Maximum Entropy Toolkit<p>
 * Copyright:    Copyright (c) Kristina Toutanova<p>
 * Company:      Stanford University<p>
 * @author Kristina Toutanova
 * @version 1.0
 */


package edu.stanford.nlp.tagger.maxent;


public class ExtractorAllTaggerOutputs extends Extractor {

  private static final long serialVersionUID = -3460332717788797188L;

  @Override
  String extract(History h, PairsHolder pH) {
    // should extract last verbal word and also the current word
    StringBuilder res = new StringBuilder();
    for (int num = 0; num < GlobalHolder.collectionTaggers.numTaggers; num++) {
      String tag = GlobalHolder.collectionTaggers.getTag(num, h.current);
      res.append('!').append(tag);
    }//for
    return res.toString();
  }

}

