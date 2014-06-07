//ExtractorFrames -- StanfordMaxEnt, A Maximum Entropy Toolkit
//Copyright (c) 2002-2008 Leland Stanford Junior University


//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.

//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.

//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

//For more information, bug reports, fixes, contact:
//Christopher Manning
//Dept of Computer Science, Gates 1A
//Stanford CA 94305-9010
//USA
//    Support/Questions: java-nlp-user@lists.stanford.edu
//    Licensing: java-nlp-support@lists.stanford.edu
//http://www-nlp.stanford.edu/software/tagger.shtml


package edu.stanford.nlp.tagger.maxent;

import java.util.*;


/**
 * The static eFrames contains
 * an array of all Extractors that are used to define
 * features. This is an important class for the tagger.
 * If you want to add a new Extractor, you should add it to the
 * array eFrames.
 *
 * @author Kristina Toutanova
 * @version 1.0
 */
public class ExtractorFrames {

  // all features are implicitly conjoined with the current tag
  static final Extractor cWord = new Extractor(0, false);
  static final Extractor prevTag = new Extractor(-1, true);
  static final Extractor prevTwoTag =new Extractor(-2,true);
  static final Extractor prevTwoTags = new ExtractorPrevTwoTags();
  static final Extractor prevThreeTags = new ExtractorPrevThreeTags();
  static final Extractor nextTag = new Extractor(1, true);
  static final Extractor nextTwoTag = new Extractor(2,true);
  static final Extractor nextTwoTags = new ExtractorNextTwoTags();
  static final Extractor nextThreeTags = new ExtractorNextThreeTags();
  static final Extractor prevNextTag = new ExtractorPrevTagNextTag();
  static final Extractor prevNextTagWord = new ExtractorPrevTagNextTagWord();
  static final Extractor prevTagWord = new ExtractorPrevTagWord();
  static final Extractor nextTagWord = new ExtractorNextTagWord();
  static final Extractor nextWord = new Extractor(1, false);
  static final Extractor prevWord = new Extractor(-1, false);
  static final Extractor prevWord2 = new Extractor(-2,false);
  static final Extractor nextWord2 = new Extractor(2,false);

  static final Extractor lastVerb = new ExtractorLastVerb();
  static final Extractor lastVerbVB = new ExtractorVerbal_VB();
  static final Extractor lastVerbVBN = new ExtractorVerbal_VBN();
  static final Extractor lastVerbThat = new ExtractorLastVerbalTC();
  static final Extractor nextNNC = new ExtractorFollowingNN();
  static final Extractor lastVerbPT = new ExtractorParticlesChris();
  static final Extractor lastVerbDist = new ExtractorLVdist();
  static final Extractor nextWordClass = new ExtractorFollowingWClass(1);
  static final Extractor nextNextWordClass = new ExtractorFollowingWClass(2);
  static final Extractor next2WordClass = new ExtractorFollowing2WClass();
  static final Extractor firstTaggerOutput = new ExtractorOutputTag(0, 0);
  static final Extractor secondTaggerOutput = new ExtractorOutputTag(1, 0);
  static final Extractor thirdTaggerOutput = new ExtractorOutputTag(2, 0);
  static final Extractor firstTaggernTag = new ExtractorOutputTag(0, 1);
  static final Extractor firstTaggern2Tags = new ExtractorNextTwoTagsTagger(0);
  static final Extractor allTaggerOutputs = new ExtractorAllTaggerOutputs();
  static final Extractor cWordNextWord = new ExtractorCWordNextWord();
  static final Extractor cWordPrevWord = new ExtractorCWordPrevWord();
  static final Extractor cWordLowerCase = new ExtractorCWordLowerCase();
  static final Extractor cWordCapCase = new ExtractorCWordCapCase();


  // this config was used in the best NAACL 2003 cyclic dependency tagger
  static final Extractor[] eFrames_bidirectional = {cWord,prevWord,nextWord,prevTag,
      nextTag,prevTwoTags,nextTwoTags,prevNextTag,prevTagWord,nextTagWord,
      cWordPrevWord,cWordNextWord};

  // this is a simple trigram CMM tagger (similar to the EMNLP 2000 tagger)
  static final Extractor[] eFrames_left3words = {cWord, prevWord, nextWord, prevTag, prevTwoTags};

  // like a simple trigram CMM tagger (similar to the EMNLP 2000 tagger) with more word context
  static final Extractor[] eFrames_left5words = {cWord, prevWord, nextWord, prevTag, prevTwoTags,
                                                  prevWord2, nextWord2};

  // like best NAACL 2003 cyclic dependency tagger but adds w_{-2}, w_{+2}
  static final Extractor[] eFrames_bidirectional5words = {cWord, prevWord, nextWord,
      prevWord2, nextWord2, prevTag, nextTag, prevTwoTags, nextTwoTags, prevNextTag,
      prevTagWord, nextTagWord, cWordPrevWord, cWordNextWord};

  // features for 2005 SIGHAN tagger
  static final Extractor[] eFrames_sighan2005 = { cWord, prevWord, prevWord2, nextWord, nextWord2, prevTag, prevTwoTag, prevTwoTags };

  // features for a not-language-particular CMM tagger
  static final Extractor[] eFrames_generic ={ cWord, prevWord, nextWord,
      prevTag, prevTwoTags, prevTagWord, cWordPrevWord };


  // features for a german-language bidirectional tagger
  static final Extractor[] eFrames_german ={ cWord, prevWord, nextWord, nextTag,
      prevTag, prevTwoTags, prevTagWord, cWordPrevWord };

  /**
   * This class is not meant to be instantiated.
   */
  private ExtractorFrames() {
  }


  @SuppressWarnings({"fallthrough"})
  public static Extractor[] getExtractorFrames(String arch) {
    ArrayList<Extractor> extrs = new ArrayList<Extractor>();
    String[] args = arch.split("\\s*,\\s*");
    for (String arg : args) {
      if (arg.equalsIgnoreCase("left3words")) {
        extrs.addAll(Arrays.asList(eFrames_left3words));
      } else if (arg.equalsIgnoreCase("left5words")) {
          extrs.addAll(Arrays.asList(eFrames_left5words));
      } else if (arg.equals("bidirectional")) {
        extrs.addAll(Arrays.asList(eFrames_bidirectional));
      } else if (arg.equals("bidirectional5words")) {
        extrs.addAll(Arrays.asList(eFrames_bidirectional5words));
      } else if (arg.equals("generic")) {
        extrs.addAll(Arrays.asList(eFrames_generic));
      } else if (arg.equals("sighan2005")) {
        extrs.addAll(Arrays.asList(eFrames_sighan2005));
      } else if (arg.equalsIgnoreCase("german")) {
        extrs.addAll(Arrays.asList(eFrames_german));
      } else if (arg.startsWith("words(")) {
        // non-sequence features with just a certain number of words of context
        int window = Extractor.getParenthesizedNum(arg);
        if ( ! (window > 0 && window % 2 == 1)) {
          System.err.println("Bad window size for words option: " + window + "; setting to 1");
          window = 1;
        }
        extrs.add(new Extractor(0, false));
        for (int i = 1; i <= window / 2; i++) {
          extrs.add(new Extractor(i, false));
          extrs.add(new Extractor(-i, false));
        }
      } else if (arg.startsWith("order(")) {
        int order = Extractor.getParenthesizedNum(arg);
        if ( ! (order >= 0 && order <= 3)) {
          System.err.println("Bad order for order option: " + order + "; setting to 2");
          order = 2;
        }
        switch (order) {
          case 3:
            extrs.add(new Extractor(-3, true));
            extrs.add(prevThreeTags);
            // we don't currently have a template for p3t, p2t without pt, but could
            // falls through
          case 2:
            extrs.add(new Extractor(-2, true));
            extrs.add(prevTwoTags);
            // falls through
          case 1:
            extrs.add(new Extractor(-1, true));
            // falls through
          case 0:
        }
      } else if (arg.equalsIgnoreCase("naacl2003unknowns") || arg.equalsIgnoreCase("naacl2003conjunctions")
              || arg.startsWith("wordshapes(") || arg.equalsIgnoreCase("motleyUnknown")
              || arg.startsWith("suffix(") || arg.startsWith("prefix(")
              || arg.startsWith("prefixsuffix") || arg.startsWith("capitalizationsuffix(")
              || arg.startsWith("distsim(") || arg.equalsIgnoreCase("lctagfeatures")
              || arg.startsWith("unicodeshapes(") || arg.startsWith("chinesedictionaryfeatures(")
              || arg.startsWith("unicodeshapeconjunction(")) {
        // okay; known unknown keyword
      } else {
        System.err.println("Unrecognized ExtractorFrames identifier (ignored): " + arg);
      }
    } // end for
    return extrs.toArray(new Extractor[extrs.size()]);
  }

} // end class ExtractorFrames


/**
 * the current word in lower-cased version
 * if the current word does not start with an alpha-numeric character, return 0
 */
class ExtractorCWordLowerCase extends Extractor {

  private static final long serialVersionUID = -7847524200422095441L;

  @Override
  String extract(History h, PairsHolder pH) {
    return ExtractorFrames.cWord.extract(h, pH).toLowerCase();
  }

}


/**
 * conjunction of current word and last verb
 */
class ExtractorCWordLastVerb extends Extractor {

  private static final long serialVersionUID = 5260729022076178529L;

  @Override
  String extract(History h, PairsHolder pH) {
    String cw = ExtractorFrames.cWord.extract(h, pH);
    String lastVer = ExtractorFrames.lastVerb.extract(h, pH);
    return cw + '!' + lastVer;
  }

}


/**
 * the current word if it is capitalized, zero otherwise
 */
class ExtractorCWordCapCase extends Extractor {

  private static final long serialVersionUID = -2393096135964969744L;

  @Override
  String extract(History h, PairsHolder pH) {
    String cw = ExtractorFrames.cWord.extract(h, pH);
    String lk = cw.toLowerCase();
    if (lk.equals(cw)) {
      return zeroSt;
    }
    return cw;
  }

}


/**
 * This extractor extracts the current and the next word in conjunction.
 */
class ExtractorCWordNextWord extends Extractor {

  private static final long serialVersionUID = -1034112287022504917L;

  public ExtractorCWordNextWord() {
  }

  @Override
  String extract(History h, PairsHolder pH) {
    return pH.get(h, 0, false) + '!' + pH.get(h, 1, false);
  }

}


/**
 * This extractor extracts the current and the next word and the next tag in conjunction.
 */
class ExtractorCWordNextWordTag extends Extractor {

  private static final long serialVersionUID = 277004119652781182L;

  public ExtractorCWordNextWordTag() {
  }

  @Override
  public int rightContext() {
    return 1;
  }

  @Override
  String extract(History h, PairsHolder pH) {
    return pH.get(h, 0, false) + '!' + pH.get(h, 1, true) + '!' + pH.get(h, 1, false);
  }

}


/**
 * This extractor extracts the current and the previous word in conjunction.
 */
class ExtractorCWordPrevWord extends Extractor {

  private static final long serialVersionUID = -6505213465359458926L;

  public ExtractorCWordPrevWord() {
  }

  @Override
  String extract(History h, PairsHolder pH) {
    return pH.get(h, -1, false) + '!' + pH.get(h, 0, false);
  }

}


/**
 * This extractor extracts the current and the previous word in conjunction and also the
 * previous tag
 */
class ExtractorCWordPrevWordTag extends Extractor {

  private static final long serialVersionUID = -3271166574128085943L;

  public ExtractorCWordPrevWordTag() {
  }

  @Override
  public int leftContext() {
    return 1;
  }

  @Override
  String extract(History h, PairsHolder pH) {
    return pH.get(h, -1, false) + '!' + pH.get(h, -1, true) + '!' + pH.get(h, 0, false);
  }


}


/**
 * This extractor extracts the previous two tags.
 */
class ExtractorPrevTwoTags extends Extractor {

  private static final int TWO_TAG_ALLOWANCE = 8;
  private static final long serialVersionUID = 5124896556547424355L;

  public ExtractorPrevTwoTags() {
  }

  @Override
  public int leftContext() {
    return 2;
  }

  @Override
  String extract(History h, PairsHolder pH) {
    // most tag pairs are rather less than the default StringBuilder size of 16, so micro-optimize
    // return pH.get(h, -1, true) + '!' + pH.get(h, -2, true);
    StringBuilder sb = new StringBuilder(TWO_TAG_ALLOWANCE);
    sb.append(pH.get(h, -1, true));
    sb.append('!');
    sb.append(pH.get(h, -2, true));
    return sb.toString();
  }

}


/**
 * This extractor extracts the previous three tags.
 */
class ExtractorPrevThreeTags extends Extractor {

  private static final long serialVersionUID = 2123985878223958420L;

  public ExtractorPrevThreeTags() {
  }

  @Override
  public int leftContext() {
    return 3;
  }


  @Override
  String extract(History h, PairsHolder pH) {
    return pH.get(h, -1, true) + '!' + pH.get(h, -2, true) + '!' + pH.get(h, -3, true);
  }

}


/**
 * This extractor extracts the next two tags.
 */
class ExtractorNextTwoTags extends Extractor {

  private static final long serialVersionUID = -2623988469984672798L;

  public ExtractorNextTwoTags() {
  }

  @Override
  public int rightContext() {
    return 2;
  }

  @Override
  String extract(History h, PairsHolder pH) {
    return pH.get(h, 1, true) + '!' + pH.get(h, 2, true);
  }

}


/**
 * This extractor extracts the next three tags.
 */
class ExtractorNextThreeTags extends Extractor {

  private static final long serialVersionUID = 8563584394721620568L;

  public ExtractorNextThreeTags() {
  }

  @Override
  public int rightContext() {
    return 3;
  }

  @Override
  String extract(History h, PairsHolder pH) {
    return pH.get(h, 1, true) + '!' + pH.get(h, 2, true) + '!' + pH.get(h, 3, true);
  }

}


/**
 * This extractor extracts the previous tag and the current word in conjunction.
 */
class ExtractorPrevTagWord extends Extractor {

  private static final long serialVersionUID = 1283543246845193024L;

  public ExtractorPrevTagWord() {
  }

  @Override
  public int leftContext() {
    return 1;
  }

  @Override
  String extract(History h, PairsHolder pH) {
    return pH.get(h, -1, true) + '!' + pH.get(h, 0, false);
  }

}


/**
 * This extractor extracts the previous tag , next tag, and the current word in conjunction.
 */
class ExtractorPrevTagNextTagWord extends Extractor {

  private static final long serialVersionUID = -4942654091455804179L;

  public ExtractorPrevTagNextTagWord() {
  }

  @Override
  public int leftContext() {
    return 1;
  }

  @Override
  public int rightContext() {
    return 1;
  }

  @Override
  String extract(History h, PairsHolder pH) {
    return pH.get(h, -1, true) + '!' + pH.get(h, 0, false) + pH.get(h, 1, true);
  }

}


/**
 * This extractor extracts the previous tag , next tag in conjunction.
 */
class ExtractorPrevTagNextTag extends Extractor {

  private static final long serialVersionUID = -2807770765588266257L;

  public ExtractorPrevTagNextTag() {
  }

  @Override
  public int leftContext() {
    return 1;
  }

  @Override
  public int rightContext() {
    return 1;
  }


  @Override
  String extract(History h, PairsHolder pH) {
    return pH.get(h, -1, true) + '!' + pH.get(h, 1, true);
  }

}


/**
 * This extractor extracts the next tag and the current word in conjunction.
 */
class ExtractorNextTagWord extends Extractor {

  private static final long serialVersionUID = 4037838593446895680L;

  public ExtractorNextTagWord() {
  }

  @Override
  public int rightContext() {
    return 1;
  }

  @Override
  String extract(History h, PairsHolder pH) {
    return pH.get(h, 1, true) + '!' + pH.get(h, 0, false);
  }

}


/**
 * This extractor extracts the next two tags of some tagger.
 */
class ExtractorNextTwoTagsTagger extends Extractor {

  private static final long serialVersionUID = 120979241653956833L;

  private Extractor e1;
  private Extractor e2;

  @Override
  public int rightContext() {
    return 2;
  }

  public ExtractorNextTwoTagsTagger(int num) {
    e1 = new ExtractorOutputTag(num, 1);
    e2 = new ExtractorOutputTag(num, 2);
  }


  @Override
  String extract(History h, PairsHolder pH) {
    return e1.extract(h, pH) + e2.extract(h, pH);
  }

}




