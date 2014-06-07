/**
 * Title:        StanfordMaxEnt<p>
 * Description:  A Maximum Entropy Toolkit<p>
 * Copyright:    Copyright (c) Kristina Toutanova<p>
 * Company:      Stanford University<p>
 */
package edu.stanford.nlp.tagger.maxent;

import edu.stanford.nlp.io.InDataStreamFile;
import edu.stanford.nlp.io.OutDataStreamFile;

import java.util.HashMap;
import java.io.IOException;


/** Maintains a map from words to tags and their counts.
 *
 *  @author Kristina Toutanova
 *  @version 1.0
 */
public class Dictionary {

  HashMap<String,TagCount> dict = new HashMap<String,TagCount>();
  private HashMap<Integer,CountWrapper> partTakingVerbs = new HashMap<Integer,CountWrapper>();
  static final String naWord = "NA";

  public Dictionary() {
  }

  public HashMap<String,TagCount> getDict() {
    return dict;
  }

  public void add(String word, String tag) {
    if (dict.containsKey(word)) {
      TagCount cT = dict.get(word);
      cT.add(tag);
      return;
    }
    TagCount cT = new TagCount();
    //cT.wordId=dict.size();
    cT.add(tag);
    dict.put(word, cT);
  }


  public void release() {
    dict.clear();
  }

  public void addVPTaking(String verb, String tag, String partWord) {
    int h = verb.hashCode();
    Integer i = Integer.valueOf(h);
    if (tag.startsWith("RP")) {
      if (this.partTakingVerbs.containsKey(i)) {
        this.partTakingVerbs.get(i).incPart(partWord);
      } else {
        this.partTakingVerbs.put(i, new CountWrapper(verb, 0, 0, 0, 0));
        this.partTakingVerbs.get(i).incPart(partWord);
      }
    } else if (tag.startsWith("RB")) {
      if (this.partTakingVerbs.containsKey(i)) {
        this.partTakingVerbs.get(i).incRB(partWord);
      } else {
        this.partTakingVerbs.put(i, new CountWrapper(verb, 0, 0, 0, 0));
        this.partTakingVerbs.get(i).incRB(partWord);
      }
    } else if (tag.startsWith("IN")) {
      if (this.partTakingVerbs.containsKey(i)) {
        this.partTakingVerbs.get(i).incIn(partWord);
      } else {
        this.partTakingVerbs.put(i, new CountWrapper(verb, 0, 0, 0, 0));
        this.partTakingVerbs.get(i).incIn(partWord);
      }
    }

  }


  public void addVThatTaking(String verb) {
    int h = verb.hashCode();
    Integer i = Integer.valueOf(h);
    if (this.partTakingVerbs.containsKey(i)) {
      this.partTakingVerbs.get(i).incThat();
    } else {
      this.partTakingVerbs.put(i, new CountWrapper(verb, 0, 1, 0, 0));
    }
  }

  public int getCountPart(String verb) {
    int h = verb.hashCode();
    Integer i = Integer.valueOf(h);
    if (this.partTakingVerbs.containsKey(i)) {
      return this.partTakingVerbs.get(i).getCountPart();
    }
    return 0;
  }


  public int getCountThat(String verb) {
    int h = verb.hashCode();
    Integer i = Integer.valueOf(h);
    if (this.partTakingVerbs.containsKey(i)) {
      return this.partTakingVerbs.get(i).getCountThat();
    }
    return 0;
  }


  public int getCountIn(String verb) {
    int h = verb.hashCode();
    Integer i = Integer.valueOf(h);
    if (this.partTakingVerbs.containsKey(i)) {
      return this.partTakingVerbs.get(i).getCountIn();
    }
    return 0;
  }


  public int getCountRB(String verb) {
    int h = verb.hashCode();
    Integer i = Integer.valueOf(h);
    if (this.partTakingVerbs.containsKey(i)) {
      return this.partTakingVerbs.get(i).getCountRB();
    }
    return 0;
  }


  public int getCount(String word, String tag) {
    TagCount tc = dict.get(word);
    if (tc == null) {
      return 0;
    } else {
      return tc.get(tag);
    }
  }


  public String[] getTags(String word) {

    TagCount tC = get(word);
    if (tC == null) {
      return null;
    }
    return tC.getTags();
  }


  public TagCount get(String word) {
    return dict.get(word);
  }


  public boolean known(String word) {
    return dict.containsKey(word);
  }


  public String getFirstTag(String word) {
    if (dict.containsKey(word)) {
      return dict.get(word).getFirstTag();
    }
    return null;

  }


  public int sum(String word) {
    if (dict.containsKey(word)) {
      return dict.get(word).sum();
    }
    return 0;
  }

  public void save(String filename) {
    try {
      OutDataStreamFile rf = new OutDataStreamFile(filename);
      save(rf);
      rf.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void save(OutDataStreamFile file) {
    String[] arr = dict.keySet().toArray(new String[dict.keySet().size()]);
    try {
      file.writeInt(arr.length);
      System.err.println("Saving dictionary of " + arr.length + " words ...");
      for (String word : arr) {
        TagCount tC = get(word);
        file.writeUTF(word);
        tC.save(file);
      }
      Integer[] arrverbs = this.partTakingVerbs.keySet().toArray(new Integer[partTakingVerbs.keySet().size()]);
      file.writeInt(arrverbs.length);
      for (Integer iO : arrverbs) {
        CountWrapper tC = this.partTakingVerbs.get(iO);
        file.writeInt(iO.intValue());
        tC.save(file);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void read(InDataStreamFile rf, String filename) throws IOException {
    // Object[] arr=dict.keySet().toArray();

    int maxNumTags = 0;
    int len = rf.readInt();
    if (TestSentence.VERBOSE) {
      System.err.println("Reading Dictionary of " + len + " words from " + filename + '.');
    }

    for (int i = 0; i < len; i++) {
      String word = rf.readUTF();
      TagCount tC = new TagCount();
      tC.read(rf);
      int numTags = tC.numTags();
      if (numTags > maxNumTags) {
        maxNumTags = numTags;
      }
      this.dict.put(word, tC);
      if (TestSentence.VERBOSE) {
        System.err.println("  " + word + " [idx=" + i + "]: " + tC);
      }
    }
    if (TestSentence.VERBOSE) {
      System.err.println("Read dictionary of " + len + " words; max tags for word was " + maxNumTags + '.');
    }
  }

  private void readTags(InDataStreamFile rf) throws IOException {
    // Object[] arr=dict.keySet().toArray();

    int maxNumTags = 0;
    int len = rf.readInt();
    if (TestSentence.VERBOSE) {
      System.err.println("Reading Dictionary of " + len + " words.");
    }

    for (int i = 0; i < len; i++) {
      String word = rf.readUTF();
      TagCount tC = new TagCount();
      tC.read(rf);
      int numTags = tC.numTags();
      if (numTags > maxNumTags) {
        maxNumTags = numTags;
      }
      this.dict.put(word, tC);
      if (TestSentence.VERBOSE) {
        System.err.println("  " + word + " [idx=" + i + "]: " + tC);
      }
    }
    if (TestSentence.VERBOSE) {
      System.err.println("Read dictionary of " + len + " words; max tags for word was " + maxNumTags + '.');
    }
  }

  public void read(String filename) {
    try {
      InDataStreamFile rf = new InDataStreamFile(filename);
      read(rf, filename);

      //Object[] arrverbs=this.partTakingVerbs.keySet().toArray();
      int len1 = rf.readInt();
      for (int i = 0; i < len1; i++) {
        int code = rf.readInt();
        Integer iO = Integer.valueOf(code);
        CountWrapper tC = new CountWrapper();
        tC.read(rf);

        this.partTakingVerbs.put(iO, tC);
      }
      rf.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void read(InDataStreamFile file) {
    try {
      readTags(file);

      //Object[] arrverbs=this.partTakingVerbs.keySet().toArray();
      int len1 = file.readInt();
      for (int i = 0; i < len1; i++) {
        int code = file.readInt();
        Integer iO = Integer.valueOf(code);
        CountWrapper tC = new CountWrapper();
        tC.read(file);

        this.partTakingVerbs.put(iO, tC);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }




  public void printAmbiguous() {
    String[] arr = dict.keySet().toArray(new String[dict.keySet().size()]);
    try {
      int countAmbiguous = 0;
      int countUnAmbiguous = 0;
      int countAmbDisamb = 0;
      for (String word : arr) {
        if (word.indexOf('|') == -1) {
          continue;
        }
        TagCount tC = get(word);
        if (tC.numTags() > 1) {
          System.out.print(word);
          countAmbiguous++;
          tC.print();
          System.out.println();
        } else {
          String wordA = word.substring(0, word.indexOf('|'));
          if (get(wordA).numTags() > 1) {
            System.out.print(word);
            countAmbDisamb++;
            countUnAmbiguous++;
            tC.print();
            System.out.println();
          } else {
            countUnAmbiguous++;
          }
        }// else
      }
      System.out.println(" ambg " + countAmbiguous + " unambg " + countUnAmbiguous + " disamb " + countAmbDisamb);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * This makes ambiguity classes from all words in the dictionary and remembers
   * their classes in the TagCounts
   */

  public void setAmbClasses() {
    String[] arr = dict.keySet().toArray(new String[dict.keySet().size()]);
    for (String w : arr) {
      int ambClassId = GlobalHolder.ambClasses.getClass(w);
      dict.get(w).setAmbClassId(ambClassId);
    }
  }

  public int getAmbClass(String word) {
    if (word.equals(naWord)) {
      return -2;
    }
    if (get(word) == null) {
      return -1;
    }
    return get(word).getAmbClassId();
  }

  public static void main(String[] args) {
    String s = "word";
    String tag = "tag";
    Dictionary d = new Dictionary();

    System.out.println(d.getCount(s, tag));
    System.out.println(d.getFirstTag(s));
  }

}
