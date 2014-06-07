/**
 * Title:        StanfordMaxEnt<p>
 * Description:  A Maximum Entropy Toolkit<p>
 * Copyright:    Copyright (c) Kristina Toutanova<p>
 * Company:      Stanford University<p>
 */


package edu.stanford.nlp.tagger.maxent;

import edu.stanford.nlp.io.InDataStreamFile;
import edu.stanford.nlp.io.OutDataStreamFile;


/**
 * Stores a triple of an extractor ID, a feature value (derived from history)
 * and a y (tag) value.  Used to compute a feature number in the loglinear
 * model.
 *
 * @author Kristina Toutanova
 * @version 1.0
 */
public class FeatureKey {

  int num;
  String val;
  String tag;


  public FeatureKey() {
  }


  public FeatureKey(int num, String val, String tag) {
    this.num = num;
    //this.val=cleanup(val);
    this.val = val;
    this.tag = tag;
  }


  public void set(int num, String val, String tag) {
    this.num = num;
    //this.val=cleanup(val);
    this.val = val;
    this.tag = tag;
  }


  @Override
  public String toString() {
    return num + ' ' + val + ' ' + tag;
  }

  public void save(OutDataStreamFile f) {
    try {
      f.writeInt(num);
      f.writeUTF(val);
      f.writeUTF(tag);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void read(InDataStreamFile inf) {
    try {
      num = inf.readInt();
      val = inf.readUTF();
      // intern the tag strings as they are read, since there are few of them. This saves tons of memory.
      tag = inf.readUTF().intern();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /* --------------------
   * this was to clean-up some empties left from before
   *
  String cleanup(String val) {

    int index = val.indexOf('!');
    if (index > -1) {
      String first = val.substring(0, index);
      String last = val.substring(index + 1);
      System.out.println("in " + first + " " + last);
      first = TestSentence.toNice(first);
      last = TestSentence.toNice(last);
      System.out.println("out " + first + " " + last);
      return first + '!' + last;
    } else {
      return val;
    }
  }

  ---------- */


  @Override
  public int hashCode() {
    /* I'm not sure why this is happening, and i really don't want to
       spend a month tracing it down. -wmorgan. */
    if (val == null) return num << 16 ^ 1 << 5 ^ tag.hashCode();
    return num << 16 ^ val.hashCode() << 5 ^ tag.hashCode(); }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (! (o instanceof FeatureKey)) {
      return false;
    }
    FeatureKey f1 = (FeatureKey) o;
    return (num == f1.num) && (tag.equals(f1.tag)) && (val.equals(f1.val));
  }

}
