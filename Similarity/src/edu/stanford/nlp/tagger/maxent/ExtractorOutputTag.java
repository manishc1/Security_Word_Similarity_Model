/**
 * Title:        StanfordMaxEnt<p>
 * Description:  A Maximum Entropy Toolkit<p>
 * Copyright:    Copyright (c) Kristina Toutanova<p>
 * Company:      Stanford University<p>
 */

package edu.stanford.nlp.tagger.maxent;

/**
 * @author Kristina Toutanova
 * @version 1.0
 */
public class ExtractorOutputTag extends Extractor {

  private static final long serialVersionUID = -14742821811550955L;

  private static final String naTag = "NA";
  private final int num;
  private final int position;

  public ExtractorOutputTag(int num, int position) {
    this.num = num;
    this.position = position;
  }

  @Override
  String extract(History h, PairsHolder pH) {
    // should extract last verbal word and also the current word
    int pos = h.current + position;
    if ((pos < h.start) || (pos > h.end)) {
      return naTag;
    }
    String tag = GlobalHolder.collectionTaggers.getTag(num, pos);
    return tag;
  }

}
