/**
 * Title:        StanfordMaxEnt<p>
 * Description:  A Maximum Entropy Toolkit<p>
 * Copyright:    Copyright (c) Kristina Toutanova<p>
 * Company:      Stanford University<p>
 * @author Kristina Toutanova
 * @version 1.0
 */
package edu.stanford.nlp.tagger.maxent;

import java.util.HashMap;
import java.util.Map;

import edu.stanford.nlp.objectbank.ObjectBank;
import edu.stanford.nlp.util.Timing;

/**
 * Extractor for adding distsim information
 * @author rafferty
 *
 */
public class ExtractorDistsim extends Extractor {

  private static final long serialVersionUID = 1L;

  private Map<String,String> lexicon;

  private void initLexicon(String path) {
    Timing.startDoing("Loading distsim lexicon from " + path);
    lexicon = new HashMap<String, String>();
    for (String word : ObjectBank.getLineIteratorObjectBank(path)) {
      String[] bits = word.split("\\s+");
      lexicon.put(bits[0].toLowerCase(), bits[1]);
    }
    Timing.endDoing();
  }

  @Override
  String extract(History h, PairsHolder pH) {
    String curWord = ExtractorFrames.cWord.extract(h, pH);
    String distSim = lexicon.get(curWord.toLowerCase());
    if (distSim == null) distSim = "null";
    return distSim;
  }

  public ExtractorDistsim(String distSimPath) {
    initLexicon(distSimPath);
  }

}
