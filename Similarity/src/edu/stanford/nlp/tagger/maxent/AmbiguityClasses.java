//AmbiguityClasses -- StanfordMaxEnt, A Maximum Entropy Toolkit
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
//Support/Questions: java-nlp-user@lists.stanford.edu
//Licensing: java-nlp-support@lists.stanford.edu
//http://www-nlp.stanford.edu/software/tagger.shtml


package edu.stanford.nlp.tagger.maxent;

import java.io.IOException;

import edu.stanford.nlp.io.InDataStreamFile;
import edu.stanford.nlp.io.OutDataStreamFile;
import edu.stanford.nlp.util.Index;

/**
 * A collection of Ambiguity Class.
 * <i>The code currently here is rotted and would need to be revived.</i>
 *
 * @author Kristina Toutanova
 * @version 1.0
 */

public class AmbiguityClasses {
  private Index<AmbiguityClass> classes;
  //private HashMap<AmbiguityClass,Integer> s;
  private static final String naWord = "NA";


  public AmbiguityClasses() {
    //s = new HashMap<AmbiguityClass,Integer>();
    classes = new Index<AmbiguityClass>();
    AmbiguityClass.naClass.key = "NA";
    AmbiguityClass.naClass.s.add("NA");
    AmbiguityClass.naClass.init();
  }

//  public void init() {
//
//  }

  public int add(AmbiguityClass a) {
//    if (s.containsKey(a)) {
//      return s.get(a).intValue();
//    }
//    s.put(a, Integer.valueOf(s.size()));
//    return (s.size() - 1);
    if(classes.contains(a)) {
      return classes.indexOf(a);
    }
    classes.add(a);
    return classes.indexOf(a);
  }

  public int getClass(String word) {
    if (word.equals(naWord)) {
      return -2;
    }
    if (GlobalHolder.dict.get(word) == null) {
      return -1;
    }
    AmbiguityClass a;
    int allCount = GlobalHolder.dict.sum(word);
    if (allCount > GlobalHolder.veryCommonWordThresh) {
      a = new AmbiguityClass(word, true);
      //System.out.println("Very common "+word);
    } else {
      a = new AmbiguityClass(word);
    }
    //a.print();
    return add(a);
  }


  public void print() {
    Object[] arrClasses = classes.toArray();//s.keySet().toArray();
    System.out.println(arrClasses.length);
//    System.out.println("Number of ambiguity classes is " + arrClasses.length);
//    for (int i = 0; i < arrClasses.length; i++) {
//      ((AmbiguityClass) arrClasses[i]).print();
//    }
  }


  public void save(String filename) {
    try {
      OutDataStreamFile rf = new OutDataStreamFile(filename);
      Object[] arrClasses = classes.toArray();//s.keySet().toArray();
//      System.out.println("Number of ambiguity classes is " + arrClasses.length);
//      rf.writeInt(arrClasses.length);
      // for (int i = 0; i < arrClasses.length; i++) {
        //rf.writeUTF(((AmbiguityClass) (arrClasses[i])).getWord());
      // }
      rf.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }// save

  public void save(OutDataStreamFile file) {
    try {
      Object[] arrClasses = classes.toArray();//s.keySet().toArray();
//      System.out.println("Number of ambiguity classes is " + arrClasses.length);
//      file.writeInt(arrClasses.length);
      for (int i = 0; i < arrClasses.length; i++) {
        //rf.writeUTF(((AmbiguityClass) (arrClasses[i])).getWord());
        AmbiguityClass cur = (AmbiguityClass) arrClasses[i];
        file.writeBoolean(cur.single);
          file.writeUTF(cur.getWord());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }// save


  public void read(String filename) {
    try {
      InDataStreamFile rf = new InDataStreamFile(filename);
      int len = rf.readInt();//this is the number of ambiguity classes
      for (int i = 0; i < len; i++) {
        boolean singleton = rf.readBoolean();
//        int len_buff = rf.readInt();
//        byte[] buff = new byte[len_buff];
//        rf.read(buff);
        String word = rf.readUTF();//new String(buff);
        word = TestSentence.toNice(word);
        add(new AmbiguityClass(word, singleton));
        //init();
      }//i

      rf.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void read(InDataStreamFile file) {
    try {
      int len = file.readInt();//this is the number of ambiguity classes
      for (int i = 0; i < len; i++) {
        boolean singleton = file.readBoolean();
        String word = file.readUTF();//new String(buff);
        word = TestSentence.toNice(word);
        add(new AmbiguityClass(word, singleton));
      }//i

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
