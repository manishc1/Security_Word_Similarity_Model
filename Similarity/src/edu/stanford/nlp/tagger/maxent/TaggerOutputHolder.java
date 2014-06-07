/**
 * This class will have instances for each tagger used in the tagger combination. It will contain an array of elements
 *  - one element per token in the training/test sets.
 * Each element will be an array of length 3 - the top probable 3 tags with scaled probability.
 */


package edu.stanford.nlp.tagger.maxent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TaggerOutputHolder {
  boolean probs;
  /**
   * This array is the size of the training data, the number of tokens.
   */
  private OutputTags[] tagArrays;

  public TaggerOutputHolder(boolean probs) {
    this.probs = probs;
    //tagArrays=new OutputTags[GlobalHolder.pairs.getSize()];
  }


  public String getTag(int numToken) {
    return tagArrays[numToken].getTag();
  }

  public TaggerOutputHolder(String fileName, boolean probs) {
    this(probs);
    try {

      BufferedReader in = new BufferedReader(new FileReader(fileName));
      int nO = 0;
      ArrayList<OutputTags> v = new ArrayList<OutputTags>();
      for (String lineS; (lineS = in.readLine()) != null; ) {
        if (lineS.startsWith("%%")) {
          continue;
        }
        v.add(new OutputTags(lineS, probs));
        nO++;
        //if(nO==tagArrays.length) break;
      }
      tagArrays = new OutputTags[nO];
      for (int i = 0; i < nO; i++) {
        tagArrays[i] = v.get(i);
      }
      v.clear();
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
