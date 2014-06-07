/**
 * Title:       StanfordMaxEnt<p>
 * Description: A Maximum Entropy Toolkit<p>
 * Copyright:   The Board of Trustees of The Leland Stanford Junior University
 * Company:     Stanford University<p>
 */

package edu.stanford.nlp.tagger.maxent;

import edu.stanford.nlp.io.InDataStreamFile;
import edu.stanford.nlp.io.OutDataStreamFile;
import edu.stanford.nlp.ling.WordTag;

import java.util.*;


/** A simple class that maintains a list of WordTag pairs which are interned
 *  as they are added.  This stores a tagged corpus.
 *
 *  @author Kristina Toutanova
 *  @version 1.0
 */
public class PairsHolder {

  private HashMap<WordTag, WordTag> arr = new HashMap<WordTag, WordTag>(); // to hold the pairs
  // the pairs are of type WordTag
  // this holds the keys of the corresponding word-tag pair
  private ArrayList<WordTag> arrNum = new ArrayList<WordTag>();


  public PairsHolder() {
  }


  public int getSize() {
    return arrNum.size();
  }

  /* -----------------
     CDM May 2008.  This method was unused.  But it also has a bug in it
     in that the equals() test can never succeed (Integer vs WordTag).
     So I'm commenting it out for now....
  public int[] getIndexes(Object wordtag) {
    ArrayList<Integer> arr1 = new ArrayList<Integer>();
    int l = wordtag.hashCode();
    Integer lO = Integer.valueOf(l);
    for (int i = 0; i < arrNum.size(); i++) {
      if (arrNum.get(i).equals(lO)) {
        arr1.add(Integer.valueOf(i));
      }
    }
    int[] ret = new int[arr1.size()];
    for (int i = 0; i < arr1.size(); i++) {
      ret[i] = arr1.get(i).intValue();
    }
    return ret;
  }
   */


  public void release() {
    arr.clear();
    arrNum.clear();
  }

  /** This method interns as it adds. */
  int add(WordTag wordtag) {
    WordTag o = arr.get(wordtag);
    if (o == null) {
      arr.put(wordtag, wordtag);
      o = wordtag;
    }
    arrNum.add(o);
    return arrNum.size() - 1;
  }

  void remove(int start, int end) {
    int sz = arrNum.size();
    // we remove elements sz-(end-start) through sz-1
    for (int i = sz - 1; i >= sz - (end - start); i--) {
      arrNum.remove(i);
    }
  }


  void setTag(int pos, String tag) {
    arrNum.get(pos).setTag(tag);
  }


  public void save(String filename) {
    try {
      OutDataStreamFile rf = new OutDataStreamFile(filename);
      int sz = arrNum.size();
      rf.writeInt(sz);
      for (int i = 0; i < sz; i++) {
        //save the wordtag in the file
        WordTag wT = arrNum.get(i);
        rf.writeUTF(wT.word());
        rf.writeUTF(wT.tag());
      }
      rf.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void read(String filename) {
    try {
      InDataStreamFile rf = new InDataStreamFile(filename);
      int len = rf.readInt();
      for (int i = 0; i < len; i++) {
        WordTag wT = new WordTag();
        wT.setWord(rf.readUTF());
        wT.setTag(rf.readUTF());
        add(wT);

      }
      rf.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /** Return either the word or tag at a certain position in the corpus.
   *
   *  @param index The corpus position
   *  @param  isTag Whether to return the tag (or the word)
   *  @return A String that is either the word or the tag
   */
  String get(int index, boolean isTag) {
    WordTag wT = arrNum.get(index);

    if (isTag) {
      return wT.tag();
    } else {
      return wT.word();
    }
  }


  String get(History h, int position, boolean isTag) {
    if (((h.current + position) >= h.start) && (h.current + position <= h.end)) {
      return get(h.current + position, isTag);
    } else {
      return "NA";
    }
  }

}
