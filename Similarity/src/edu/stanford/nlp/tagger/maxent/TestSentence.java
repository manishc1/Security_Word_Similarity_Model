/**
 * Title:        StanfordMaxEnt<p>
 * Description:  A Maximum Entropy Toolkit<p>
 * Copyright:    Copyright (c) Kristina Toutanova<p>
 * Company:      Stanford University<p>
 */

package edu.stanford.nlp.tagger.maxent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

import edu.stanford.nlp.io.PrintFile;
import edu.stanford.nlp.io.EncodingPrintWriter;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.WordTag;
import edu.stanford.nlp.maxent.iis.LambdaSolve;
import edu.stanford.nlp.sequences.BestSequenceFinder;
import edu.stanford.nlp.sequences.ExactBestSequenceFinder;
import edu.stanford.nlp.sequences.SequenceModel;

/**
 * @author Kristina Toutanova
 * @version 1.0
 */
public class TestSentence implements SequenceModel {

  static String delimiter = "/"; // currently a yucky static. Remove someday.
  private LambdaSolve prob;
  // public static int[] hPos;
  // public static boolean[] isTag;
  private String sentence;
  private List<String> sent = new ArrayList<String>();
  static boolean VERBOSE = false;
  private static final boolean DBG = false;
  private int size; // this always has the value of sent.size(). Remove it? [cdm 2008]
  private int beamSize = 10;
  private String[][] nBest;
  private double[] pBest;
  private String[] correctTags;
  private String[] tags;
  int numRight;
  int numWrong;
  int numUnknown;
  int numWrongUnknown;
  int numUnassigned, numUnknownUnassigned;
  int numWrongUnknownT3;
  int numWrongT3;
  private int startSizePairs = GlobalHolder.pairs.getSize();
  private int endSizePairs = startSizePairs;
  private static final String naTag = "NA";
  private static final String eosWord = "EOS";
  private double[][][] probabilities;
  private static final double sigma = 1.1;
  private boolean taginference = true;
  static int leftContext = 2;
  static int rightContext = 2;
  private static boolean doDeterministicTagExpansion = true;


  // todo: Have less constructors. Have them all able to use either Viterbi (tagInference) or beam via a properties flag

  public TestSentence() {
  }

  public TestSentence(LambdaSolve prob, String s) {
    this(prob, s, null);
  }

  public TestSentence(LambdaSolve prob, String s, PrintFile pf) {
    this.prob = prob;
    this.sentence = s;
    init();
    testBeam();
    if (pf != null) {
      pf.println(getTaggedNice());
    }
    revert(startSizePairs, endSizePairs);
  }

  // currently unused
  @SuppressWarnings({"UnusedDeclaration"})
  private TestSentence(LambdaSolve prob, Sentence<? extends HasWord> s, PrintFile pf) {
    this.prob = prob;
    this.sentence = s.toString(true);

    for(HasWord word : s) {
      sent.add(word.toString());
    }
    init2();
    testBeam();
  }


  public TestSentence(LambdaSolve prob, String[] s, String[] correctTags, PrintFile pf, Dictionary wrongWords) {
    if (DBG) {
      assert(s.length == correctTags.length);
      System.err.println("Entering TestSentence(); s.length is " + s.length + "; startSizePairs is " + endSizePairs + "; endSizePairs is " + endSizePairs);
    }
    this.prob = prob;
    this.sent = new ArrayList<String>(Arrays.asList(s));
    this.correctTags = correctTags;
    this.tags = new String[correctTags.length];
    for (int i = 0; i < correctTags.length; i++) {
      tags[i] = naTag;
    }
    init1();

    if ( ! taginference) {
      testAndWriteErrors(pf, wrongWords);
      revert(startSizePairs, endSizePairs);
    } else {
      testTagInference(pf, wrongWords);
    }
    if (DBG) {
      System.err.println("Exiting TestSentence(); startSizePairs is " + endSizePairs + "; endSizePairs is " + endSizePairs);
    }
  }


  // currently unused
  @SuppressWarnings({"UnusedDeclaration"})
  private TestSentence(LambdaSolve prob, String[] s, String[] tags, String[] correctTags, PrintFile pf, Dictionary wrongWords) {
    this.prob = prob;
    this.sent = new ArrayList<String>(Arrays.asList(s));
    this.correctTags = correctTags;
    this.tags = tags;
    init1();
    testAndWriteErrors(pf, wrongWords);
    revert(startSizePairs, endSizePairs);
  }


  /**
   * Tags the sentence s by solving prob.  Returns a sentence of
   * TaggedWord objects.
   * @param prob
   * @param s
   * @return
   */
  public Sentence<TaggedWord> tagSentence(LambdaSolve prob, Sentence<? extends HasWord> s) {
    // System.err.println("Entering tagSentence2");
    // System.err.println("pairs.size()=" + GlobalHolder.pairs.getSize() +
    //                    "; size=" + size + "; startSizePairs=" + startSizePairs +
    //                    "; endSizePairs=" + endSizePairs);
    this.prob = prob;
    int sz = s.length();
    this.sent = new ArrayList<String>(sz + 1);
    for (int j = 0; j < sz; j++) {
      this.sent.add(s.get(j).word());
    }
    size = sz;
    sent.add(eosWord);
    //this.sentence=s;
    if (VERBOSE) {
      System.err.println("Sentence is " + s.toString());
    }
    init1();
    tags = new String[size];
    for (int j = 0; j < size; j++) {
      tags[j] = naTag;
    }
    // BEAM option
    // testBeam();
    // revert(startSizePairs, endSizePairs);
    //
    // Sentence<TaggedWord> taggedWords = new Sentence<TaggedWord>();
    // for (int j = 0; j < sent.size() - 1; j++) {
    //   String tag = nBest[0][j];
    //   TaggedWord w = new TaggedWord(sent.get(j), tag);
    //   taggedWords.add(w);
    // }
    Sentence<TaggedWord> taggedWords = testTagInference();

    // System.err.println("Exiting tagSentence2");
    // System.err.println("pairs.size()=" + GlobalHolder.pairs.getSize() +
    //                    "; size=" + size + "; startSizePairs=" + startSizePairs +
    //                    "; endSizePairs=" + endSizePairs);
    return taggedWords;
  }



  private void revert(int prevSize, int afterSize) {
    GlobalHolder.pairs.remove(prevSize, afterSize);
    endSizePairs = prevSize;
    CollectionTaggerOutputs.baseToken += (afterSize - prevSize);
  }


  private void init() {
    StringTokenizer sT = new StringTokenizer(sentence);
    size = 0;
    while (sT.hasMoreTokens()) {
      String word = sT.nextToken();
      sent.add(word);
      size++;
    }

    sent.add(eosWord);
    size++;
    nBest = new String[beamSize][size];
    pBest = new double[beamSize];
    tags = new String[size];
    for (int i = 0; i < size; i++) {
      tags[i] = naTag;
    }
  }


  private void init1() {
    //the eos are assumed already there
    size = sent.size();

    nBest = new String[beamSize][size];
    pBest = new double[beamSize];

    for (int i = 0; i < size - 1; i++) {
      if (GlobalHolder.dict.sum(sent.get(i)) == 0) {
        numUnknown++;
      }
    }
  }


  private void init2() {
    //the eos are assumed already there
    size = sent.size();

    nBest = new String[beamSize][size];
    pBest = new double[beamSize];

    tags = new String[size];
    for (int i = 0; i < size; i++) {
      tags[i] = naTag;
    }
  }


  /**
   * Returns a string representation of the sentence.
   * @return
   */
  public String getTaggedNice() {
    StringBuilder sb = new StringBuilder();
    // size - 1 means to exclude the EOS (end of string) symbol
    for (int i = 0; i < size - 1; i++) {
      sb.append(toNice(sent.get(i))).append(delimiter).append(toNice(nBest[0][i]));
      sb.append(' ');
    }
    return sb.toString();
  }

  public Sentence<TaggedWord> getTaggedSentence() {
    Sentence<TaggedWord> taggedSentence = new Sentence<TaggedWord>();

    for (int j = 0; j < sent.size() - 1; j++) {
      String tag = nBest[0][j];
      TaggedWord w = new TaggedWord(sent.get(j), tag);
      taggedSentence.add(w);
    }
    return taggedSentence;
  }

  public static String toNice(String s) {
    if (s == null) {
      return naTag;
    } else {
      return s;
    }
  }


  /**
   * Tag a sentence using Dan's TagScorer interface.
   */
  private static void tagSentenceTagScorer(List<String> sent) {
    TestSentence ts = new TestSentence();
    ts.initializeScorer(GlobalHolder.getLambdaSolve(), sent);
    BestSequenceFinder ti = new ExactBestSequenceFinder();//new BeamBestSequenceFinder(15);
    int[] bestTags = ti.bestSequence(ts);
    for (int j = 0; j < sent.size(); j++) {
      System.out.print(sent.get(j) + delimiter + GlobalHolder.tags.getTag(bestTags[j + ts.leftWindow()]) + '\t');
    }

    ts.cleanUpScorer();
  }


  public void writeProbs() {
    // System.err.println("Entering writeProbs");
    // System.err.println("pairs.size()=" + GlobalHolder.pairs.getSize() +
    //                    "; size=" + size + "; startSizePairs=" + startSizePairs +
    //                    "; endSizePairs=" + endSizePairs);
    // for the hypotheses equal 1 to 5
    int numTags = GlobalHolder.tags.getSize();
    probabilities = new double[size][beamSize][numTags];
    for (int hyp = 0; hyp < beamSize; hyp++) {
      // put the whole thing on the pairs, give its beginning and end
      for (int i = 0; i < size; i++) {
        WordTag wT = new WordTag(sent.get(i), nBest[hyp][i]);
        GlobalHolder.pairs.add(wT);
      }
      int start = endSizePairs;
      int end = endSizePairs + size - 1;
      endSizePairs = endSizePairs + size;
      // iterate over the sentence
      for (int current = 0; current < size; current++) {
        History h = new History(start, end, current + start);
        double[] probs = getHistories(h);
        double s = 0;
        for (int k = 0; k < numTags; k++) {
          s += probs[k];
        }
        for (int k = 0; k < numTags; k++) {
          probabilities[current][hyp][k] = probs[k] / s;
        }
      } // for current
    } // for hyp
    // clean up the stuff in PairsHolder (added by cdm in Aug 2008
    revert(startSizePairs, endSizePairs);
    // System.err.println("Exiting writeProbs");
    // System.err.println("pairs.size()=" + GlobalHolder.pairs.getSize() +
    //                    "; size=" + size + "; startSizePairs=" + startSizePairs +
    //                    "; endSizePairs=" + endSizePairs);
  } // end writeProbs()


  /**
   * Tokenize s into words, and dump unknown word activations.
   *
   * @param s Sentence to print unknown word features of
   */
  private void dumpActivations(String s) {
    this.sentence = s;
    init();

    for (int i = 0; i < size; i++) {
      WordTag wT = new WordTag(sent.get(i), naTag);
      GlobalHolder.pairs.add(wT);
    }

    int start = endSizePairs;
    int end = endSizePairs + size - 1;
    endSizePairs = endSizePairs + size;
    // iterate over the sentence
    for (int current = 0; current < size; current++) {
      History h = new History(start, end, current + start);
      printActivations(h);

    } // for current

    revert(startSizePairs, endSizePairs);
  }


  /** Do tagging using beam search.
   *  @return The likely best tags for the sentence
   */
  private String[] testBeam() {
    for (int j = 0; j < beamSize; j++) {
      for (int i = 0; i < size; i++) {
        nBest[j][i] = naTag;
        pBest[j] = 1.0;
      }
    }

    for (int current = 0; current < size; current++) {
      if (VERBOSE) {
        System.err.println("current is " + current + " word " + sent.get(current));
      }
      String[][] hCurrent = new String[beamSize][size];
      double[] pCurrent = new double[beamSize];
      for (int j = 0; j < pCurrent.length; j++) {
        pCurrent[j] = Double.NEGATIVE_INFINITY;
      }
      insertTags(current, hCurrent, pCurrent);  // this decrements CollectionTaggerOutputs.baseToken
      if (VERBOSE) {
        System.err.println("current is " + current + " word " + sent.get(current));
      }

      for (int i = 0; i < beamSize; i++) {
        if (hCurrent[i][0] == null) {
          // fill it and to the end with hCurrent[i-1]
          for (int s = i; s < beamSize; s++) {
            System.arraycopy(hCurrent[i - 1], 0, hCurrent[s], 0, current + 1);
            pCurrent[s] = pCurrent[i - 1];
          }
          break;
        }  // end if
      }
      nBest = hCurrent;
      pBest = pCurrent;
    }

//  for(int i = 0; i < size - 1; i++) {
//    System.out.print(sent.get(i) + delimiter + nBest[0][i] + ' ');
//  }
//  System.out.println();

    CollectionTaggerOutputs.baseToken += size;  // this resets it to where it was

    return nBest[0];  // [0] is best
  }


  /** Do tagging using beam search.
   *
   *  @param pf Stream to write errors to
   *  @param wrongWords Dictionary to add wrongly tagged words to
   */
  private void testAndWriteErrors(PrintFile pf, Dictionary wrongWords) {
    String[] chosenTags = testBeam();
    writeTagsAndErrors(chosenTags, pf, wrongWords);
  }


  /** Write the tagging and note any errors (if pf != null) and accumulate
   *  global statistics.
   *
   *  @param finalTags Chosen tags for sentence
   *  @param pf File to write tagged output too (can be null, then no output;
   *               at present it is non-null iff the debug property is set)
   *  @param wrongWords Dictionary to accumulate wrong word counts in (cannot be null)
   */
  private void writeTagsAndErrors(String[] finalTags, PrintFile pf, Dictionary wrongWords) {
    //without the EOS word
    for (int i = 0; i < correctTags.length - 1; i++) {
      boolean nReliable = false;
      if (true) {
        nReliable = true;
      }
      if (pf != null) {
        pf.print(toNice(sent.get(i)));
        pf.print('_');
        pf.print(finalTags[i]);
      }
      if ((correctTags[i]).equals(finalTags[i])) {
        numRight++;
      } else {
        numWrong++;
        if (pf != null) pf.print('|' + correctTags[i]);
        // TODO: This writes the below output in utf8, not the user's specified encoding
        EncodingPrintWriter.out.println("Word: " + sent.get(i) + "; correct: " + correctTags[i] + "; guessed: " + finalTags[i]);

        if (nReliable) {
          numUnassigned++;
        }
        wrongWords.add(sent.get(i) + correctTags[i], finalTags[i]);
        if (GlobalHolder.dict.sum(sent.get(i)) == 0) {
          numWrongUnknown++;
          if (pf != null) pf.print("*");
          if (nReliable) {
            numUnknownUnassigned++;
          }
        }// if
        if (pf != null) pf.print(" ");
      }// else
    }// for
    if (pf != null) pf.println();
  }


  /**
   * Test using (exact Viterbi) TagInference.
   */
  public void testTagInference(PrintFile pf, Dictionary wrongWords) {
    String[] finalTags = runTagInference();
    writeTagsAndErrors(finalTags, pf, wrongWords);
  }


  /**
   * Test using (exact Viterbi) TagInference.
   *
   * @return The tagged sentence
   */
  public Sentence<TaggedWord> testTagInference() {
    String[] finalTags = runTagInference();

    ArrayList<TaggedWord> taggedWords = new ArrayList<TaggedWord>();
    // leave out EOS
    for (int j = 0, len = sent.size() - 1; j < len; j++) {
      String tag = finalTags[j]; // nBest[0][j];
      TaggedWord w = new TaggedWord(sent.get(j), tag);
      taggedWords.add(w);
    }

    return new Sentence<TaggedWord>(taggedWords);
  }


  private String[] runTagInference() {
    this.initializeScorer(GlobalHolder.getLambdaSolve(), sent);

    BestSequenceFinder ti = new ExactBestSequenceFinder(); //new BeamBestSequenceFinder(50);//
    int[] bestTags = ti.bestSequence(this);
    String[] finalTags = new String[bestTags.length];
    for (int j = 0, len = sent.size(); j < len; j++) {
      finalTags[j] = GlobalHolder.tags.getTag(bestTags[j + leftWindow()]);
    }
    cleanUpScorer();
    return finalTags;
  }


  public boolean known(String w) {
    return GlobalHolder.dict.sum(w) > 0;
  }


  public boolean reliable(int current) {
    String tag = nBest[0][current];
    int y = GlobalHolder.tags.getIndex(tag);
    double max = 0;
    int maxInd = -1;
    double p = probabilities[current][0][y];
    if (known(sent.get(current))) {
      System.out.println(" known " + sent.get(current));
      String[] tags = GlobalHolder.dict.getTags(sent.get(current));
      String[] tags1 = append(tags, sent.get(current));
      for (int k = 0; k < tags1.length; k++) {
        int i = GlobalHolder.tags.getIndex(tags1[k]);
        if (i == y) {
          continue;
        }
        if ((probabilities[current][0][i] >= max) && (probabilities[current][0][i] <= p)) {
          max = probabilities[current][0][i];
          maxInd = i;
        }
      }
    }// if known
    else {
      System.out.println(" unknown " + sent.get(current));
      for (int i = 0; i < GlobalHolder.ySize; i++) {
        if (i == y) {
          continue;
        }
        if ((probabilities[current][0][i] >= max) && (probabilities[current][0][i] <= p)) {
          max = probabilities[current][0][i];
          maxInd = i;
        }
      }
    }// else
    if (maxInd == -1) {
      return true;
    }
    String tag1 = GlobalHolder.tags.getTag(maxInd);
    System.out.println(tag + ' ' + GlobalHolder.tags.getTag(maxInd) + ' ' + p / max);
    if ((GlobalHolder.dict.sum(sent.get(current)) == 0) && (GlobalHolder.tags.isClosed(tag1))) {
      return true;
    } else {
      return (p / max) > sigma;
    }
  }


  /**
   * This is used for Dan's tag inference methods.
   * current is the actual word number + leftW
   */
  void setHistory(int current, History h, int[] tags) {
    //writes over the tags in the last thing in pairs

    int left = leftWindow();
    int right = rightWindow();

    for (int j = current - left; j <= current + right; j++) {
      if (j < left) {
        continue;
      } //but shouldn't happen
      if (j >= size + left) {
        break;
      } //but shouldn't happen
      h.setTag(j - left, GlobalHolder.tags.getTag(tags[j]));
    }
  }

  /**
   * do initializations for the TagScorer interface
   */
  void initializeScorer(LambdaSolve prob, List<String> sentence) {
    this.sent = sentence;
    this.prob = prob;
    size = sent.size();
    for (int i = 0; i < size; i++) {
      WordTag wT = new WordTag(sent.get(i), "NN");
      GlobalHolder.pairs.add(wT);

    }
    endSizePairs += size;
  }


  /**
   * clean-up after the scorer
   */
  void cleanUpScorer() {
    revert(startSizePairs, endSizePairs);
  }


  void getHistory(int current, History h, int hyp) {
    for (int i = 0; i < current; i++) {
      WordTag wT = new WordTag(sent.get(i), nBest[hyp][i]);
      GlobalHolder.pairs.add(wT);
    }
    for (int i = current; i < size; i++) {
      WordTag wT = new WordTag(sent.get(i), tags[i]);
      GlobalHolder.pairs.add(wT);
    }

    h.set(endSizePairs, endSizePairs + size - 1, current + endSizePairs);
    this.endSizePairs = endSizePairs + size;
  }


  void insertTags(int current, String[][] hCurrent, double[] pCurrent) {
    History h = new History();
    double sum = 0.0;
    boolean hasHistory = true;
    double[] histories = null;

    int allCount = GlobalHolder.dict.sum(sent.get(current));
    if (allCount == 0) {
    }
    //numUnknown++;
    for (int hyp = 0; hyp < beamSize; hyp++) {
      // Find the history
      try {
        getHistory(current, h, hyp); // sets History h
      } catch (Exception e) {
        System.err.println("Error: num hyp " + hyp);
        System.err.println(sent.get(current));
      }

      if (true) {
        // System.err.println("Exception "+current);
        hasHistory = false;
        //return;
      }
      if (!hasHistory) {
        histories = getHistories(h);

        sum = 0;
        for (int tR = 0; tR < histories.length; tR++) {
          sum = sum + histories[tR];
        }
      }
      // Generate tags for current
      // Should implement dictionary later
      int x = 0;
      if ((allCount == 0)) {
        for (int y = 0; y < GlobalHolder.ySize; y++) {
          String tag = GlobalHolder.tags.getTag(y);
          if (GlobalHolder.tags.isClosed(tag)) {
            continue;
          }
          insertArray(current, hyp, x, y, hCurrent, pCurrent, tag, hasHistory, histories, sum);
        }
      } else { // the word is known and cut the search

        String[] tags1 = GlobalHolder.dict.getTags(sent.get(current));
        String[] tags = append(tags1, sent.get(current));

        for (String tag : tags) {
          int y = GlobalHolder.tags.getIndex(tag);
          //if(y==-1)
          // System.out.println(" y is " +y+" "+ tag);
          insertArray(current, hyp, x, y, hCurrent, pCurrent, tag, hasHistory, histories, sum);
        }

      }//else

      CollectionTaggerOutputs.baseToken -= size;
    }//hyp
  }


  public static String[] append(String[] tags, String word) {
    if (doDeterministicTagExpansion) {
      return GlobalHolder.tags.deterministicallyExpandTags(tags, word);
    } else {
      return tags;
    }
  }


  void insertArray(int current, int hyp, int x, int y, String[][] hCurrent, double[] pCurrent, String tag, boolean hasHistory, double[] histories, double sum) {
    if (DBG) {
      System.err.println(" inserting tag " + tag + " for word " + sent.get(current));
    }

    double p; // initialized below
    if (hasHistory) {
      //p=(pBest[hyp]>0?prob.pcond(y,x)*pBest[hyp]:prob.pcond(y,x));
      p = prob.pcond(y, x) * pBest[hyp];
    } else {
      //p=(histories[y]/sum)*pBest[hyp]; * chqange back to *
      p = Math.log(histories[y]) - Math.log(sum) + pBest[hyp];
    }//else no history
    if (!(p == p)) {
      System.err.println(" p ia NaN " + pBest[hyp] + ' ' + p + " current is" + current + " tag " + tag);
      return;
    }
    if (p == 0.0) {
      System.err.println(" p is 0 inside insertArray");
      //smooth them
      return;
    }

    // this is acrobatics to avoid putting equal hypotheses on the beam
    for (int i = 0; i < beamSize; i++) {
      //here is the comparison
      if (p == pCurrent[i]) {
        boolean isDifferent = false;
        for (int j = 0; j < current; j++) {
          if (!(hCurrent[i][j].equals(nBest[hyp][j]))) {
            isDifferent = true;
          }
        }
        if (!(hCurrent[i][current].equals(tag))) {
          isDifferent = true;
        }
        if (!isDifferent) {
          return; // do not put the same thing twice
        }
      }
    }

    if (p < pCurrent[beamSize - 1]) {
      if (DBG) {
        System.err.println(" for word " + current + " did not insert tag " + tag + " prob " + (Math.log(histories[y]) - Math.log(sum)) + " total " + p);

      }
      return;
    }

    int i = beamSize - 2;
    while (true) {
      if ((i == -1) || (p < pCurrent[i])) {
        //Put the element at i+1
        pCurrent[i + 1] = p;
        hCurrent[i + 1][current] = tag;
        if (DBG) {
          System.err.println(" added for word " + current + " tag " + tag + " prob " + (Math.log(histories[y]) - Math.log(sum)) + " total " + p + " at place " + (i + 1));
        }

        System.arraycopy(nBest[hyp], 0, hCurrent[i + 1], 0, current);
        return;
      }
      // p>=p i
      // Write the p and the string for i on place i+1
      pCurrent[i + 1] = pCurrent[i];
      System.arraycopy(hCurrent[i], 0, hCurrent[i + 1], 0, current + 1);
      i--;
    } // end while
  }


  /**
   * This scores the current assignment in PairsHolder at
   * current position h.current
   */
  private double[] getScores(History h) {
    String cWord = ExtractorFrames.cWord.extract(h);
    String[] tags = stringTagsAt(h.current - h.start + leftWindow());
    if (DBG) {
      System.err.println("Entering getScores(); got " + tags.length + " tags for \"" + cWord + "\" at position " + (h.current - h.start + leftWindow()));
      System.err.print("  tags are  ");
      for (String tag : tags) {
        System.err.print('\"' + tag + "\" ");
      }
      System.err.println();
      System.err.print("  ttags has " + GlobalHolder.tags.getSize() + " tags: ");
      for (int i = 0; i < GlobalHolder.tags.getSize(); i++) {
        System.err.print('\"' + GlobalHolder.tags.getTag(i) + "\" ");
      }
      System.err.println();
    }

    // String ctag = ExtractorFrames.cTag.extract(h); //the current tag

    // todo: this next part could be done more efficiently with Arrays.logSum, but we'd need to deal with smoothing (if it's really needed)
    double[] scores = new double[tags.length]; // has log scores, later probabilities, of possible tags
    double[] histories = getHistories(h);  // has real (not log) scores for each tag
    double sum = 0.0;
    for (int i = 0; i < histories.length; i++) {
      sum += histories[i];
    }
    double logsum = Math.log(sum);

    // sum=0;

    for (int j = 0; j < tags.length; j++) {
      // score the j-th tag
      String tag = tags[j];
      int tagindex = GlobalHolder.tags.getIndex(tag);
      if (DBG) { System.err.print("Scoring tag " + tag + "; index is " + tagindex); }
      scores[j] = Math.log(histories[tagindex]);
      if (DBG) { System.err.println("; unnormalized log score is " + scores[j]); }
      // sum += histories[tagindex];  // cdm 2008: this caused a AIOOBE; but it seems like this line just shouldn't be here.
    }

    // this bit is doing normalization!
    for (int j = 0; j < tags.length; j++) {
      scores[j] -= logsum;
    }

    if (DBG) {
      System.err.println("For word " + ExtractorFrames.cWord.extract(h) + "; index " + h.current);
      System.err.print("  History features: ");
      h.print(System.err);
      for (int j = 0; j < tags.length; j++) {
        System.err.println("  Norm. score for tag " + tags[j] + " = " + scores[j] + " = prob.: " + Math.exp(scores[j]));
      }
    }

    return scores;
  }


  /**
   * Print out the unknown word feature values of the features in ExtractorFramesRare.
   */
  private void printActivations(History h) {
    String word = ExtractorFrames.cWord.extract(h);
    //FeatureKey s = new FeatureKey();

    System.out.println("features for word " + word);
    Extractors ext = GlobalHolder.extractorsRare;
    for (int j = 0; j < ext.getSize(); j++) {
      String key = ext.extract(j, h);
      System.out.println(j + '\t' + ext.toString() + " value " + key);
    }
    System.out.println();
  }


  // Returns an unnormalized score (in real, not log, space) for each tag
  // This is the most cpu intensive method in the tagging runtime!
  // It should do less string manipulation.
  // Changed to only do the exponentiation once -- cdm
  // todo: But it loops through all possible tags regardless.  Change to allow lexitagging for non-rare words. it seems that the code that calls this in getScores is using a lexicon anyway
  // todo: Try turning off smooth; I'm not sure it's needed or doing anything.
  public double[] getHistories(History h) {
    if (DBG) {
      System.err.println("getting probs for word " + ExtractorFrames.cWord.extract(h));
      System.err.println("previous tag is " + ExtractorFrames.prevTag.extract(h));
      System.err.println("previous two tags are " + ExtractorFrames.prevTwoTags.extract(h));
      System.err.print("History:\t");
      h.print(System.err);
    }

    int fAll = ((GlobalHolder.isRare(ExtractorFrames.cWord.extract(h))) ? GlobalHolder.extractors.getSize() + GlobalHolder.extractorsRare.getSize() : GlobalHolder.extractors.getSize());
    //int fAll=GlobalHolder.extractors.getSize()+GlobalHolder.extractorsRare.getSize();
    int fMain = GlobalHolder.extractors.getSize();

    // reuse one FeatureKey rather than allocating each time for speed
    FeatureKey s = new FeatureKey();
    double[] arrLocal = new double[GlobalHolder.ySize];

    for (int i = 0; i < GlobalHolder.ySize; i++) {
      double lambda = 0.0;  // was =1;
      String tag = GlobalHolder.tags.getTag(i);
      if (DBG) System.err.println(" trying tag " + tag);
      for (int kf = 0; kf < fMain; kf++) {
        s.set(kf, GlobalHolder.extractors.extract(kf, h), tag);
        int fNum = GlobalHolder.getNum(s);
        if (fNum > -1) {
          // lambda=lambda*Math.exp(prob.lambda[fNum]);
          lambda += prob.lambda[fNum];
          // if (kf > 3) {
          //   System.out.println(s.toString()+" "+prob.lambda[fNum]);
          // }
        }
      } // end for

      for (int kf = fMain; kf < fAll; kf++) {
        s.set(kf, GlobalHolder.extractorsRare.extract(kf - fMain, h), tag);
        int fNum = GlobalHolder.getNum(s);
        if (fNum > -1) {
          // lambda=lambda*Math.exp(prob.lambda[fNum]);
          lambda += prob.lambda[fNum];
          // if (kf > 19) {
          //   System.out.println("feature "+kf+"lambda "+prob.lambda[fNum]+" word "+word+" "+s.toString());
          // }
        }
      } // end for
      // arrLocal[i]=lambda;

      //if we are multiplying in by the generation prob, do it here

      arrLocal[i] = Math.exp(lambda);
      if (DBG) {
        String word = ExtractorFrames.cWord.extract(h);
        if (GlobalHolder.dict.getCount(word, tag) > 0) {
          System.err.println("   score for " + tag + ' ' + arrLocal[i]);
        }
      }
    } // end for tags

    // double sum=0;   // [cdm -- these additions weren't doing anything!]
    boolean smooth = false;
    for (int i = 0; i < arrLocal.length; i++) {
      // sum+=arrLocal[i];
      if (arrLocal[i] == 0.0) {
        smooth = true;
      }
    }
    if (smooth) {
      for (int i = 0; i < arrLocal.length; i++) {
        arrLocal[i] += .0001;
      }
    }//if
    return arrLocal;
  }


  /* ------------------------

  private static int[] intersect(int[] arr1, int[] arr2) {
    int[] result;
    HashSet<Integer> s = new HashSet<Integer>();
    if (arr1 == null) {
      return null;
    }
    if (arr2 == null) {
      return null;
    }
    for (int i = 0; i < arr1.length; i++) {
      for (int j = 0; j < arr2.length; j++) {
        if (arr1[i] == arr2[j]) {
          s.add(Integer.valueOf(arr1[i]));
          break;
        }
      }
    }
    if (s.isEmpty()) {
      return null;
    }
    result = new int[s.size()];
    Integer[] res = s.toArray(new Integer[0]);
    for (int i = 0; i < s.size(); i++) {
      result[i] = res[i].intValue();
    }
    return result;
  }

  private static int[] unite(int[] arr1, int[] arr2) {
    int[] result;
    HashSet<Integer> s = new HashSet<Integer>();
    if (arr1 == null) {
      return arr2;
    }
    if (arr2 == null) {
      return arr1;
    }
    for (int i = 0; i < arr1.length; i++) {
      s.add(Integer.valueOf(arr1[i]));
    }

    for (int i = 0; i < arr2.length; i++) {
      s.add(Integer.valueOf(arr2[i]));
    }
    if (s.isEmpty()) {
      return null;
    }
    result = new int[s.size()];
    Integer[] res = s.toArray(new Integer[0]);
    for (int i = 0; i < s.size(); i++) {
      result[i] = res[i].intValue();
    }
    return result;
  }

  -------------------------- */


  public double[][][] getProbs() {
    return probabilities;
  }


  public void printProbs() {
    for (int tag = 0; tag < GlobalHolder.tags.getSize(); tag++) {
      System.out.print(GlobalHolder.tags.getTag(tag) + '\t');
    }
    System.out.println();
    for (int current = 0; current < size; current++) {
      for (int hyp = 0; hyp < beamSize; hyp++) {
        System.out.print(sent.get(current) + '\t');
        for (int tag = 0; tag < GlobalHolder.tags.getSize(); tag++) {
          System.out.print((int) (1000 * probabilities[current][hyp][tag]) / (float) 1000 + "\t");
        }
        System.out.println();
      }// hyp
    }//current
  }

  /**
   * This method should be called after the sentence has been tagged. For every unknown word, this method adds the 3 most probable tags to the dictionary uDict
   */

  public void addUnknown(Dictionary uDict) {
    for (int current = 0; current < this.size; current++) {
      if (!known(sent.get(current))) {
        for (int i = 0; i < 3; i++) {
          uDict.add(sent.get(current), nBest[i][current]);
        }
      }// if
    }// for
  }


  /**
   * This method should be called after the sentence has been tagged.
   * For every unknown word, this method prints the 3 most probable tags
   * to the file pfu.
   *
   * @param numSent The sentence number
   * @param pfu The file to print the probable tags to
   */
  public void printUnknown(int numSent, PrintFile pfu) {
    writeProbs();
    for (int current = 0; current < this.size; current++) {
      if ( ! known(sent.get(current))) {
        pfu.print(sent.get(current));
        pfu.print(':');
        pfu.print(numSent);
        double[] probs = new double[3];
        String[] tag3 = new String[3];
        getTop3(current, probs, tag3);
        for (int i = 0; i < 3; i++) {
          pfu.print('\t');
          pfu.print(tag3[i]);
          pfu.print(' ');
          pfu.print(probs[i]);
        }
        int rank;
        String correctTag = toNice(this.correctTags[current]);
        for (rank = 0; rank < 3; rank++) {
          if (correctTag.equals(tag3[rank])) {
            break;
          } //if
        }
        if (rank > 3) {
          numWrongUnknownT3++;
        }
        pfu.print('\t');
        switch (rank) {
          case 0:
            pfu.print("Correct");
            break;
          case 1:
            pfu.print("2nd");
            break;
          case 2:
            pfu.print("3rd");
            break;
          default:
            pfu.print("Not top 3");
        }
        pfu.println();
      }// if
    }// for
  }


  /**
   * This method should be called after a sentence has been tagged.
   * For every word token, this method prints the 3 most probable tags
   * to the file pfu except for
   *
   * @param pfu File to print top tags to
   */
  public void printTop(PrintFile pfu) {
    writeProbs();
    for (int current = 0; current < this.size; current++) {
      pfu.print(sent.get(current));
      double[] probs = new double[3];
      String[] tag3 = new String[3];
      getTop3(current, probs, tag3);
      for (int i = 0; i < 3; i++) {
        pfu.print('\t');
        pfu.print(tag3[i]);
        pfu.print(' ');
        pfu.print(probs[i]);
      }
      pfu.println();
      boolean wrong = true;
      String correctTag = toNice(this.correctTags[current]);
      for (int i = 0; i < 3; i++) {
        if (correctTag.equals(tag3[i])) {
          wrong = false;
          break;
        }//if
      }
      if (wrong) {
        numWrongT3++;
      }
    }// for
  }


  void getTop3(int current, double[] probs, String[] tags) {
    int[] topIds = new int[3];
    double[] probTags = probabilities[current][0];
    for (int j = 0; j < 3; j++) {
      double maxP = 0.0;
      for (int i = 0; i < probTags.length; i++) {
        if (probTags[i] > maxP) {
          maxP = probTags[i];
          topIds[j] = i;
        }
      }//for
      probs[j] = probTags[topIds[j]];
      probTags[topIds[j]] = 0;
    }//j
    double total = 0;
    for (int j = 0; j < 3; j++) {
      total += probs[j];
    }
    for (int j = 0; j < 3; j++) {
      probs[j] /= total;
    }
    for (int j = 0; j < 3; j++) {
      tags[j] = toNice(GlobalHolder.tags.getTag(topIds[j]));
    }
  }


  /*
   * Implementation of the TagScorer interface follows
   */


  public int length() {
    return size;
  }

  public int leftWindow() {
    return leftContext; //hard-code for now
  }

  public int rightWindow() {
    return rightContext; //hard code for now
  }


  public int[] getPossibleValues(int pos) {
    String[] arr1 = stringTagsAt(pos);
    int[] arr = new int[arr1.length];
    for (int i = 0; i < arr.length; i++) {
      arr[i] = GlobalHolder.tags.getIndex(arr1[i]);
    }

    return arr;
  }


  public String[] stringTagsAt(int pos) {

    String[] arr1;
    if ((pos < leftWindow()) || (pos >= size + leftWindow())) {
      arr1 = new String[1];
      arr1[0] = naTag;
      return arr1;
    }

    int count = GlobalHolder.dict.sum(sent.get(pos - leftWindow()));
    if (count == 0) {
      arr1 = GlobalHolder.tags.getOpenTags().toArray(new String[GlobalHolder.tags.getOpenTags().size()]);
    } else {
      arr1 = GlobalHolder.dict.getTags(sent.get(pos - leftWindow()));
    }

    arr1 = append(arr1, sent.get(pos - leftWindow()));

    if (VERBOSE) {
      //System.err.println("for word " + sent.get(pos - leftWindow()) + " count " + count + " tags are " + Arrays.toString(arr1));
    }

    return arr1;
  }


  public double scoreOf(int[] tags, int pos) {

    double[] scores = scoresOf(tags, pos);
    double score = Double.NEGATIVE_INFINITY;

    int[] pv = getPossibleValues(pos);

    for (int i = 0; i < scores.length; i++) {
      if (pv[i] == tags[pos]) {
        score = scores[i];
      }
    }

    return score;
  }

  public double scoreOf(int[] sequence) {
    throw new UnsupportedOperationException();
  }


  public double[] scoresOf(int[] tags, int pos) {
    if (DBG) {
      System.err.println("scoresOf(): length of tags is " + tags.length + "; position is " + pos + "; endSizePairs = " + endSizePairs + "; size is " + size + "; leftWindow is " + leftWindow());
      System.err.println("  History h = new History(" + (endSizePairs - size) + ", " + (endSizePairs - 1) + ", " + (endSizePairs - size + pos - leftWindow()) + ")");
    }

    History h = new History(endSizePairs - size, endSizePairs - 1, endSizePairs - size + pos - leftWindow());
    setHistory(pos, h, tags);
    return getScores(h);
  }


  /**
   * Tags a test sentence.
   *
   * @param args A single argument giving the filename of the model file. If
   *             none is provided, a default tagger in <code>/u/nlp/data</code> is used.
   */
  public static void main(String[] args) {
    GlobalHolder.init();
    TestSentence tS = new TestSentence();

    System.err.println("Type a line to debug, Ctrl-C to exit.");
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 

    try {
      for (String temp; (temp = br.readLine()) != null; ) {
        tS.dumpActivations(temp);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
