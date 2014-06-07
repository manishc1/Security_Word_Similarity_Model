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


/**
 * <i>Notes:</i> This maintains a two way lookup between a History and
 * an Integer index.  It's really a worse implementation of an Index
 * TODO: This should be replace by use of an Index, but at the moment it has binary write/read methods like all Kristina's stuff.
 * todo; cdm 2008: The read/write methods are now unused and so it should be able to just be an Index
 * 
 * @author Kristina Toutanova
 * @version 1.0
 */
public class HistoryTable {

  private HashMap<History, Integer> vHI;
  private HashMap<Integer, History> vIH;
  private static final int capacity = 300000;

  public HistoryTable() {
    this(capacity);
  }

  public void release() {
    vHI.clear();
    vIH.clear();
  }


  public HistoryTable(int capacity) {
    vHI = new HashMap<History, Integer>(capacity);
    vIH = new HashMap<Integer, History>(capacity);
  }

  public int add(History h) {
    if (vHI.containsKey(h)) {
      return vHI.get(h).intValue();
    }
    Integer iO = Integer.valueOf(vHI.size());
    vHI.put(h, iO);
    vIH.put(iO, h);
    return iO.intValue();
  }

  public int put(History h) {
    Integer iO = Integer.valueOf(vHI.size());
    vHI.put(h, iO);
    vIH.put(iO, h);
    return iO.intValue();
  }


  public History getHistory(int index) {
    Integer iO = Integer.valueOf(index);
    return vIH.get(iO);
  }

  public int getIndex(History h) {
    try {
      if (vHI.containsKey(h)) {
        return vHI.get(h).intValue();
      }
      return -1;
    } catch (Exception e) {
      e.printStackTrace();
      return -1;
    }
  }

  public boolean contains(History h) {
    return vHI.containsKey(h);
  }

  public void save(String filename) {
    try {
      OutDataStreamFile rf = new OutDataStreamFile(filename);
      int s = vIH.size();
      rf.writeInt(s);
      for (int i = 0; i < s; i++) {
        History v0 = getHistory(i);
        v0.save(rf);
      }
      rf.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void read(String filename) {
    try {
      InDataStreamFile rf = new InDataStreamFile(filename);
      int size = rf.readInt();
      for (int i = 0; i < size; i++) {
        History v0 = new History();
        v0.read(rf);
        put(v0);
      }
      rf.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
