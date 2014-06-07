// TaggerExperiments -- StanfordMaxEnt, A Maximum Entropy Toolkit
// Copyright (c) 2002-2008 Leland Stanford Junior University
//
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//
// For more information, bug reports, fixes, contact:
//    Christopher Manning
//    Dept of Computer Science, Gates 1A
//    Stanford CA 94305-9010
//    USA
//    Support/Questions: java-nlp-user@lists.stanford.edu
//    Licensing: java-nlp-support@lists.stanford.edu
//    http://www-nlp.stanford.edu/software/tagger.shtml

package edu.stanford.nlp.tagger.maxent;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;

import edu.stanford.nlp.maxent.Experiments;
import edu.stanford.nlp.util.Pair;


/**
 * This class represents the training samples. It can return statistics of
 * them, for example the frequency of each x or y in the training data.
 *
 * @author Kristina Toutanova
 * @version 1.0
 */
public class TaggerExperiments extends Experiments {

  /**
   * For the tagger, it was more comfortable to also keep the features here, but this need not be the case.
   */
  static TaggerFeatures feats = new TaggerFeatures();
  private static final String zeroSt = "0";
  private int numFeatsGeneral = GlobalHolder.extractors.getSize();
  private int numFeatsAll = numFeatsGeneral + GlobalHolder.extractorsRare.getSize();


  public TaggerExperiments() {
  }

  // add the whole thing again , supposing the extractors are in GlobalHolder
  // already

  public TaggerExperiments(TaggerConfig config) throws IOException {
    System.err.println("TaggerExperiments: adding word/tags");
    ReadDataTagged c = new ReadDataTagged(config);
    vArray = new int[c.getSize()][2];

    initTemplatesNew();
    System.err.println("Featurizing tagged data tokens...");
    for (int i = 0, size = c.getSize(); i < size; i++) {

      DataWordTag d = c.get(i);
      String yS = d.getY();
      //System.out.println("DataWordTag is: " + d.getHistory() + "; y is: " + yS);
      History h = d.getHistory();
      int indX = GlobalHolder.tHistories.add(h);
      int indY = d.getYInd();
      addTemplatesNew(h, yS, indX, indY);
      addRareTemplatesNew(h, yS, indX, indY);
      //System.out.println("Stemplates: " + GlobalHolder.sTemplates);
      vArray[i][0] = indX;
      vArray[i][1] = indY;

      //System.out.println(" added elements "+indX+" "+indY+" elements "+ExtractorFrames.cWord.extract(h)+" "+ExtractorFrames.cTag.extract(h));

      if (i > 0 && (i % 10000) == 0) {
        System.err.print(i + " ");
        if (i % 100000 == 0) { System.err.println(); }
      }
    }
    System.err.println();
    System.err.println("Featurized " + c.getSize() + " data tokens [done].");
    // System.err.println(" before release ");
    c.release();
    // System.err.println(" after release ");
    ptilde();
    GlobalHolder.xSize = xSize;
    GlobalHolder.ySize = ySize;
    System.err.println("xSize [num Phi templates] = " + xSize + "; ySize [num classes] = " + ySize);

    hashHistories();

    // if we'll look at occurring tags only, we need the histories and pairs still
    if (!GlobalHolder.occuringTagsOnly) {
      GlobalHolder.tHistories.release();
      GlobalHolder.pairs.release();
    }

    getFeaturesNew();
  }


  /** This method uses and deletes a file temp.x in the current directory! */
  public void getFeaturesNew() {

    final boolean VERBOSE = false;

    try {
      System.out.println("TaggerExperiments.getFeaturesNew: initializing fnumArr.");
      GlobalHolder.fnumArr = new byte[xSize][ySize]; // what is the maximum number of active features
      File hFile = new File("temp.x");
      RandomAccessFile hF = new RandomAccessFile(hFile, "rw");
      System.out.println("  length of GlobalHolder.sTemplates keys: " + GlobalHolder.sTemplates.size());
      System.out.println("getFeaturesNew adding features ...");
      int current = 0;
      int numFeats = 0;
      for (FeatureKey fK : GlobalHolder.sTemplates) {
      // for (int i = 0; i < arr.length; i++) {
        // FeatureKey fK = (FeatureKey) arr[i];
        int numF = fK.num;
        int[] xValues;
        Pair<Integer, String> wT = new Pair<Integer, String>(Integer.valueOf(numF), fK.val);
        xValues = GlobalHolder.tFeature.getXValues(wT);
        if (xValues == null) {
          System.out.println("  xValues is null: " + fK.toString()); //  + " " + i
          continue;
        }
        int numEvidence = 0;
        int y = GlobalHolder.tags.getIndex(fK.tag);
        for (int q = 0; q < xValues.length; q++) {

          if (GlobalHolder.occuringTagsOnly) {
            //check whether the current word in x has occurred with y
            String word = ExtractorFrames.cWord.extract(GlobalHolder.tHistories.getHistory(xValues[q]));
            if (GlobalHolder.dict.getCount(word, fK.tag) == 0) {
              continue;
            }
          }
          numEvidence += this.px[xValues[q]];
        }

        if (populated(numF, numEvidence)) {
          //System.out.println("populated "+numEvidence+" "+numF);
          int[] positions = GlobalHolder.tFeature.getPositions(fK);
          if (GlobalHolder.occuringTagsOnly) {
            positions = null;
          }

          if (positions == null) {
            // write this in the file and create a TaggerFeature for it
            //int numElem
            int numElements = 0;

            for (int j = 0; j < xValues.length; j++) {
              int x = xValues[j];
              if (GlobalHolder.occuringTagsOnly) {
                //check whether the current word in x has occurred with y
                String word = ExtractorFrames.cWord.extract(GlobalHolder.tHistories.getHistory(x));
                if (GlobalHolder.dict.getCount(word, fK.tag) == 0) {
                  continue;
                }
              }
              numElements++;

              hF.writeInt(x);
              GlobalHolder.fnumArr[x][y]++;
            }
            TaggerFeature tF = new TaggerFeature(current, current + numElements - 1, fK);
            GlobalHolder.tFeature.addPositions(current, current + numElements - 1, fK);
            current = current + numElements;
            feats.add(tF);
            if (VERBOSE) {
              System.out.println("  added feature with key " + fK.toString() + " has support " + numElements);
            }
          } else {

            for (int j = 0; j < xValues.length; j++) {
              int x = xValues[j];
              GlobalHolder.fnumArr[x][y]++;
            }
            // this is the second time to write these values
            TaggerFeature tF = new TaggerFeature(positions[0], positions[1], fK);
            feats.add(tF);
            if (VERBOSE) {
              System.out.println("  added feature with key " + fK.toString() + " has support " + xValues.length);
            }
          }

          GlobalHolder.fAssociations.put(fK, Integer.valueOf(numFeats));
          numFeats++;
        } else {
          //System.out.println("feature " + numF + " not populated (" + numEvidence + ")");
          //System.exit(0);
        }

        // if (i > 0 && i % 50000 == 0) {
        //   System.out.print(i + " ");
        //  if (i % 500000 == 0) { System.out.println(); }
        // }
      } // foreach FeatureKey fK
      // read out the file and put everything in an array of ints stored in Feats
      GlobalHolder.tFeature.release();
      // System.gc();
      TaggerFeatures.xIndexed = new int[current];
      hF.seek(0);
      int current1 = 0;
      while (current1 < current) {
        TaggerFeatures.xIndexed[current1] = hF.readInt();
        current1++;
      }
      System.out.println("  total feats: " + GlobalHolder.sTemplates.size() + ", populated: " + numFeats);
      hF.close();
      hFile.delete();

      // what is the maximum number of active features per pair
      int max = 0;
      int maxGt = 0;
      int numZeros = 0;
      for (int x = 0; x < xSize; x++) {
        int numGt = 0;
        for (int y = 0; y < ySize; y++) {
          if (GlobalHolder.fnumArr[x][y] > 0) {
            numGt++;
            if (max < GlobalHolder.fnumArr[x][y]) {
              max = GlobalHolder.fnumArr[x][y];
            }
          } else {
            // if 00
            numZeros++;
          }
        }
        if (maxGt < numGt) {
          maxGt = numGt;
        }
      } // for x

      System.out.println("  Max features per x,y pair: " + max);
      System.out.println("  Max non-zero y values for an x: " + maxGt);
      System.out.println("  Number of non-zero feature x,y pairs: " +
          (xSize * ySize - numZeros));
      System.out.println("  Number of zero feature x,y pairs: " + numZeros);
      System.out.println("end getFeaturesNew.");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public static void hashHistories() {
    int fAll = GlobalHolder.extractors.getSize() + GlobalHolder.extractorsRare.getSize();
    int fGeneral = GlobalHolder.extractors.getSize();
    System.err.println("Hashing histories ...");
    for (int x = 0; x < xSize; x++) {
      History h = GlobalHolder.tHistories.getHistory(x);
      if (x > 0 && x % 10000 == 0) {
        System.err.print(x + " ");
        if (x % 100000 == 0) { System.err.println(); }
      }
      int fSize = (GlobalHolder.isRare(ExtractorFrames.cWord.extract(h)) ? fAll : fGeneral);
      for (int i = 0; i < fSize; i++) {
        GlobalHolder.tFeature.addPrev(i, h, x);
      }
    } // for x
    // now for the populated ones
    System.err.println();
    System.err.println("Hashed " + xSize + " histories.");
    System.err.println("Hashing populated histories ...");
    for (int x = 0; x < xSize; x++) {
      //if(x >590000) System.out.println("Starting populated histories: " + x);
      History h = GlobalHolder.tHistories.getHistory(x);
      //if(x >59000) System.out.println("Got history for " + x + ": " + h);
      if (x > 0 && x % 10000 == 0) {
        System.err.print(x + " ");
        if (x % 100000 == 0) { System.err.println(); }
      }
      int fSize = (GlobalHolder.isRare(ExtractorFrames.cWord.extract(h)) ? fAll : fGeneral);
      //if(x >590000) System.out.println("Starting extraction with size: " + fSize);
      for (int i = 0; i < fSize; i++) {
        //if(x >590000) System.err.println("Adding: " + i + "; " + h + "; " + x);
        GlobalHolder.tFeature.add(i, h, x); // write this to check whether to add
      }
    } // for x
    System.err.println();
    System.err.println("Hashed populated histories.");
  }


  public void hashHistories(TemplateHash tFeats, int fNo) {
    for (int x = 0; x < xSize; x++) {
      History h = GlobalHolder.tHistories.getHistory(x);
      tFeats.add(fNo, h, x);
    } // for x
  }


  public static boolean populated(int fNo, int size) {

    //if(fNo>19){System.out.println("feature "+fNo+" support "+size);}
    // Feature number 0 is hard-coded as the current word feature, which has a special threshold
    if (fNo == 0) {
      return (size > GlobalHolder.curWordMinFeatureThresh);
    } else if (fNo < GlobalHolder.extractors.getSize()) {
      return (size > GlobalHolder.minFeatureThresh);
    } else {
      return (size > GlobalHolder.rareWordMinFeatureThresh);
    }
  }


  public void initTemplatesNew() {
    GlobalHolder.tFeature.init();
    GlobalHolder.dict.setAmbClasses();
  }


  /**
   * Add a new feature key in a hashtable of feature templates
   */
  public void addTemplatesNew(History h, String tag, int x, int y) {
    // Feature templates general

    for (int i = 0; i < numFeatsGeneral; i++) {
      String s = GlobalHolder.extractors.extract(i, h);
      if (s.equals(zeroSt)) {
        continue;
      } //do not add the feature
      //iterate over tags in dictionary
      if (GlobalHolder.alltags) {
        int numTags = GlobalHolder.tags.getSize();
        for (int j = 0; j < numTags; j++) {

          String tag1 = GlobalHolder.tags.getTag(j);

          FeatureKey key = new FeatureKey(i, s, tag1);
          //System.out.println("Adding new feature: " + s + " with tag: " + tag1 + " and count: " + key.num);

          if (!GlobalHolder.extractors.get(i).precondition(tag1)) {
            continue;
          }

          GlobalHolder.add(key);
        }
      } else {
        //only this tag
        FeatureKey key = new FeatureKey(i, s, tag);
        //System.out.println("Adding new feature: " + s + " with tag: " + tag + " and count: " + key.num);

        if (!GlobalHolder.extractors.get(i).precondition(tag)) {
          continue;
        }

        GlobalHolder.add(key);
      }
    }
  }


  public void addRareTemplatesNew(History h, String tag, int x, int y) {
    // Feature templates rare

    if (!(GlobalHolder.isRare(ExtractorFrames.cWord.extract(h)))) {
      return;
    }
    int start = numFeatsGeneral;
    for (int i = start; i < numFeatsAll; i++) {
      String s = GlobalHolder.extractorsRare.extract(i - start, h);
      //System.out.println("extracted "+s+" feature "+ (i-start));

      if (s.equals(zeroSt)) {
        continue;
      } //do not add the feature
      if (GlobalHolder.alltags) {
        int numTags = GlobalHolder.tags.getSize();
        for (int j = 0; j < numTags; j++) {

          String tag1 = GlobalHolder.tags.getTag(j);

          FeatureKey key = new FeatureKey(i, s, tag1);

          if (!GlobalHolder.extractorsRare.get(i - start).precondition(tag1)) {
            continue;
          }

          GlobalHolder.add(key);
        }
      } else {
        //only this tag
        FeatureKey key = new FeatureKey(i, s, tag);

        if (!GlobalHolder.extractorsRare.get(i - start).precondition(tag)) {
          continue;
        }

        GlobalHolder.add(key);
      }
    }
  }


  public String getY(int index) {
    return GlobalHolder.tags.getTag(vArray[index][1]);
  }


  /*
  public static void main(String[] args) {
    int[] hPos = {0, 1, 2, -1, -2};
    boolean[] isTag = {false, false, false, true, true};
    GlobalHolder.init();
    TaggerExperiments gophers = new TaggerExperiments("trainhuge.txt", null);
    //gophers.ptilde();
  }
  */

}
