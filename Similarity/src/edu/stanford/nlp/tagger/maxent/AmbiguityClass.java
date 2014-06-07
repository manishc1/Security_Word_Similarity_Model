//AmbiguityClass -- StanfordMaxEnt, A Maximum Entropy Toolkit
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


import java.util.ArrayList;
import java.util.HashSet;

/**
 * An ambiguity class for a word is the word by itself or its set of observed tags.
 */
public class AmbiguityClass {
  HashSet<String> s; // this is a set of the tags that are in the ambiguity class
  ArrayList<Integer> sortedIds;
  String key;
  String word;
  boolean single;
  int members = 0;
  public static final AmbiguityClass naClass = new AmbiguityClass();

  public AmbiguityClass() {
    s = new HashSet<String>();
    sortedIds = new ArrayList<Integer>();
  }

  public AmbiguityClass(String word) {
    this();
    String[] tags = GlobalHolder.dict.getTags(word);
    for (int i = 0; i < tags.length; i++) {
      add(GlobalHolder.tags.getIndex(tags[i]));
    }
    members++;
    init();
    //this.word = word;
  }

  public AmbiguityClass(String word, boolean single) {
    this();
    this.word = word;
    this.single = single;
    if(!single) {
      String[] tags = GlobalHolder.dict.getTags(word);
      for (int i = 0; i < tags.length; i++) {
        add(GlobalHolder.tags.getIndex(tags[i]));
      }
    }
    members++;
    init();
  }

  public String getWord() {
    return word;

  }

  public boolean belongs(String word) {
    String[] tags = GlobalHolder.dict.getTags(word);
    if (tags.length != sortedIds.size()) {
      return false;
    }
    for (int i = 0; i < tags.length; i++) {
      if (!s.contains(tags[i])) {
        return false;
      }
    }
    members++;
    return true;
  } // belongs

  public boolean add(int tagId) {
    for (int j = 0; j < sortedIds.size(); j++) {
      if (tagId < sortedIds.get(j).intValue()) {
        sortedIds.add(j, Integer.valueOf(tagId));
        return true;
      }
      if (tagId == sortedIds.get(j).intValue()) {
        return false;
      }
    }//j
    sortedIds.add(Integer.valueOf(tagId));
    return true;
  }

  @Override
  public String toString() {
    if (single) {
      return word;
    }
    StringBuilder sb = new StringBuilder();
    for (Integer sID : sortedIds) {
      sb.append(":").append(sID.intValue());
    }
    return sb.toString();
  }

  public void print() {
    //System.out.print(word + " ");
    for (Integer sortedId : sortedIds) {
      System.out.print(GlobalHolder.tags.getTag(sortedId.intValue()));
    }
    System.out.println();
  }

  @Override
  public int hashCode() {
    return key.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof AmbiguityClass)) {
      return false;
    }
    return key.equals(((AmbiguityClass) o).key);
  }

  public void init() {
    if (!single) {
      for (Integer sortedId : sortedIds) {
        s.add(GlobalHolder.tags.getTag(sortedId.intValue()));
      }
    }
    key = this.toString();
  }// init

}
