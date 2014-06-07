/**
 * Title:        StanfordMaxEnt<p>
 * Description:  A Maximum Entropy Toolkit<p>
 * Copyright:    Copyright (c) Kristina Toutanova<p>
 * Company:      Stanford University<p>
 */


package edu.stanford.nlp.tagger.maxent;

import edu.stanford.nlp.io.InDataStreamFile;
import edu.stanford.nlp.io.OutDataStreamFile;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.IOException;


/** Maintains a set of featuror extractors and applies them.
 *
 *  @author Kristina Toutanova
 *  @version 1.0
 */
public class Extractors implements Serializable {

  private Extractor[] v;

  public Extractors() {
  }


  /**
   * Set the extractors from an array.
   * @param extrs The array of extractors.  It is copied in this init.
   */
  public void init(Extractor[] extrs) {
    v = new Extractor[extrs.length];
    System.arraycopy(extrs, 0, v, 0, extrs.length);
  }


  /**
   * Extract using the i'th extractor.
   * @param i The extractor to use
   * @param h The history to extract from
   * @return String The feature value
   */

  public String extract(int i, History h) {
    return v[i].extract(h);

  }

  public void set(Extractor e, int index) {
    v[index] = e;
  }

  public boolean equals(History h, History h1) {
    for (Extractor extractor : v) {
      if ( ! (extractor.extract(h).equals(extractor.extract(h1)))) {
        return false;
      }
    }
    return true;
  }


  /** Find maximum left context of extractors. Used in TagInference to decide windows for dynamic programming.
   * @return The maximum of the left contexts used by all extractors.
   */
  public int leftContext() {
    int max = 0;

    for (Extractor extractor : v) {
      int lf = extractor.leftContext();
      if (lf > max) {
        max = lf;
      }
    }

    return max;
  }


  /** Find maximum right context of extractors. Used in TagInference to decide windows for dynamic programming.
   * @return The maximum of the right contexts used by all extractors.
   */
  public int rightContext() {
    int max = 0;

    for (Extractor extractor : v) {
      int lf = extractor.rightContext();
      if (lf > max) {
        max = lf;
      }
    }

    return max;
  }


  public int getSize() {
    return v.length;
  }

  public void save(String filename) {
    try {
      OutDataStreamFile rf = new OutDataStreamFile(filename);
      rf.writeInt(v.length);
      for (Extractor extr : v) {
        rf.writeBytes(extr.toString());
      }
      rf.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void read(String filename) {
    try {
      InDataStreamFile rf = new InDataStreamFile(filename);
      int len = rf.readInt();
      v = new Extractor[len];
      //GlobalHolder.init();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  Extractor get(int index) {
    return v[index];
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Extractors[");
    for (int i = 0; i < v.length; i++) {
      sb.append(v[i].toString());
      if (i < v.length - 1) {
        sb.append(", ");
      }
    }
    sb.append(']');
    return sb.toString();
  }


  /**
   * Prints out the pair of <code>Extractors</code> objects found in the
   * file that is the first and only argument.
   * @param args Filename of extractors file (standardly written with
   *       <code>.ex<code> extension)
   */
  public static void main(String[] args) {
    try {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(args[0]));
      Extractors extrs = (Extractors) in.readObject();
      Extractors extrsRare = (Extractors) in.readObject();
      in.close();
      System.out.println("All words:  " + extrs);
      System.out.println("Rare words: " + extrsRare);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private static final long serialVersionUID = -4777107742414749890L;

}
