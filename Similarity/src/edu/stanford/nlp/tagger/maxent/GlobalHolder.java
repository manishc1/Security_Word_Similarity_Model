//GlobalHolder -- StanfordMaxEnt, A Maximum Entropy Toolkit
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

import java.io.*;
import java.util.*;

import edu.stanford.nlp.io.InDataStreamFile;
import edu.stanford.nlp.io.OutDataStreamFile;
import edu.stanford.nlp.io.PrintFile;
import edu.stanford.nlp.maxent.iis.LambdaSolve;
import edu.stanford.nlp.util.StringUtils;


/** This class holds many global variables and other things that are used by
 *  the Stanford MaxEnt Part-of-speech Tagger package.
 *
 *  @author Kristina Toutanova
 *  @author Anna Rafferty
 *  @version 1.1
 */
public class GlobalHolder {

  private GlobalHolder() {}

  static TaggerExperiments domain;
  static Dictionary dict = new Dictionary();
  static TTags tags;

  static byte[][] fnumArr;
  static LambdaSolveTagger prob;
  // todo: if we move the add method from here, we can also move sTemplates entirely to TaggerExperiments
  static HashSet<FeatureKey> sTemplates = new HashSet<FeatureKey>();
  static HashMap<FeatureKey,Integer> fAssociations = new HashMap<FeatureKey,Integer>();
  // todo; cdm 2008: I'm pretty sure the HistoryTable is only used at training time, but we allocate it and hence two big HashMaps always.
  // todo: it can just be initialized in TaggerExperiments constructor (and PairsHolderTest's testPairsHolder()
  static HistoryTable tHistories = new HistoryTable();
  static TemplateHash tFeature = new TemplateHash();
  static PairsHolder pairs = new PairsHolder();
  static Extractors extractors;
  static Extractors extractorsRare;
  static AmbiguityClasses ambClasses = new AmbiguityClasses();
  static CollectionTaggerOutputs collectionTaggers = new CollectionTaggerOutputs(0);
  static boolean alltags = false;
  static HashMap<String, HashSet<String>> tagTokens = new HashMap<String, HashSet<String>>();


  static final int RARE_WORD_THRESH = 5;
  static final int MIN_FEATURE_THRESH = 5;
  static final int CUR_WORD_MIN_FEATURE_THRESH = 2;
  static final int RARE_WORD_MIN_FEATURE_THRESH = 10;
  static final int VERY_COMMON_WORD_THRESH = 250;


  /**
   * Determines which words are considered rare.  All words with count
   * in the training data strictly less than this number (standardly, &lt; 5) are
   * considered rare.
   */
  private static int rareWordThresh = RARE_WORD_THRESH;

  /**
   * Determines which features are included in the model.  The model
   * includes features that occurred strictly more times than this number
   * (standardly, &gt; 5) in the training data.  Here I look only at the
   * history (not the tag), so the history appearing this often is enough.
   */
  static int minFeatureThresh = MIN_FEATURE_THRESH;

  /**
   * This is a special threshold for the current word feature.
   * Only words that have occurred strictly &gt; this number of times
   * in total will generate word features with all of their occurring tags.
   * The traditional default was 2.
   */
  static int curWordMinFeatureThresh = CUR_WORD_MIN_FEATURE_THRESH;

  /**
   * Determines which rare word features are included in the model.
   * The features for rare words have a strictly higher support than
   * this number are included. Traditional default is 10.
   */
  static int rareWordMinFeatureThresh = RARE_WORD_MIN_FEATURE_THRESH;

  /**
   * If using tag equivalence classes on following words, words that occur
   * strictly more than this number of times (in total with any tag)
   * are sufficiently frequent to form an equivalence class
   * by themselves. (Not used unless using equivalence classes.)
   */
  static int veryCommonWordThresh = VERY_COMMON_WORD_THRESH;


  static int xSize;
  static int ySize;
  static boolean occuringTagsOnly = false;

  static boolean initted = false;

  static final boolean VERBOSE = false;



  public static LambdaSolve getLambdaSolve() {
    return prob;
  }

  public static void init() {
    init(null);
  }

  public static void init(TaggerConfig config) {
    if (initted) return;

    extractors = new Extractors();
    extractorsRare = new Extractors();

    String lang, arch;
    String[] openClassTags, closedClassTags;

    if (config == null) {
      lang = "english";
      arch = "left3words";
      openClassTags = StringUtils.EMPTY_STRING_ARRAY;
      closedClassTags = StringUtils.EMPTY_STRING_ARRAY;
    } else {
      lang = config.getLang();
      arch = config.getArch();
      openClassTags = config.getOpenClassTags();
      closedClassTags = config.getClosedClassTags();

      if (((openClassTags.length > 0) && !lang.equals("")) || ((closedClassTags.length > 0) && !lang.equals("")) || ((closedClassTags.length > 0) && (openClassTags.length > 0))) {
        throw new RuntimeException("At least two of lang (\"" + lang + "\"), openClassTags (length " + openClassTags.length + ": " + Arrays.toString(openClassTags) + ")," +
            "and closedClassTags (length " + closedClassTags.length + ": " + Arrays.toString(closedClassTags) + ") specified---you must choose one!");
      } else if ((openClassTags.length == 0) && lang.equals("") && (closedClassTags.length == 0) && ! config.getLearnClosedClassTags()) {
        System.err.println("warning: no language set, no open-class tags specified, and no closed-class tags specified; assuming ALL tags are open class tags");
      }
    }


    if (openClassTags.length > 0) {
      tags = new TTags();
      tags.setOpenClassTags(openClassTags);
    } else if (closedClassTags.length > 0) {
      tags = new TTags();
      tags.setClosedClassTags(closedClassTags);
    } else {
      tags = new TTags(lang);
    }

    if (config != null) {
      rareWordThresh = config.getRareWordThresh();
      minFeatureThresh = config.getMinFeatureThresh();
      curWordMinFeatureThresh = config.getCurWordMinFeatureThresh();
      rareWordMinFeatureThresh = config.getRareWordMinFeatureThresh();
      veryCommonWordThresh = config.getVeryCommonWordThresh();
    }

    extractors.init(ExtractorFrames.getExtractorFrames(arch));
    extractorsRare.init(ExtractorFramesRare.getExtractorFramesRare(arch));

    initted = true;
  }


  // todo: this method is only used in TaggerExperiments; move there
  /** Adds a FeatureKey to the set of known FeatureKeys.
   *
   * @param s The feature key to be added
   * @return Whether the key was already known (false) or added (true)
   */
  public static boolean add(FeatureKey s) {
    if ((sTemplates.contains(s))) {
      return false;
    }
    sTemplates.add(s);
    return true;
  }


  public static int getNum(FeatureKey s) {
    return getNum(s, fAssociations);
  }


  public static int getNum(FeatureKey s, HashMap<FeatureKey, Integer> fAssocs) {
    Integer num = fAssocs.get(s);
    if (num == null) {
      return -1;
    } else {
      return num.intValue();
    }
  }


  /**
   * serialize the TaggerConfig
   */
  public static void saveConfig(TaggerConfig config, OutputStream os) throws IOException {
    ObjectOutputStream out = new ObjectOutputStream(os);
    out.writeObject(config);
  }

  /**
   * read in the TaggerConfig
   */
  public static TaggerConfig readConfig(DataInputStream stream) throws IOException, ClassNotFoundException {
    ObjectInputStream in = new ObjectInputStream(stream);
    TaggerConfig config = (TaggerConfig) in.readObject();
    return config;
  }


  /**
   * serialize the ExtractorFrames and ExtractorFramesRare in filename
   */
  public static void saveExtractors(OutputStream os) throws IOException {

    ObjectOutputStream out = new ObjectOutputStream(os);

    System.out.println(extractors.toString() + "\nrare" + extractorsRare.toString());
    out.writeObject(extractors);
    out.writeObject(extractorsRare);
    //out.close();
  }

  /**
   * Read the extractors from a filename.
   */
  public static void readExtractors(String filename) throws Exception {
    InputStream in = new BufferedInputStream(new FileInputStream(filename));
    readExtractors(in);
    in.close();
  }

  /**
   * Read the extractors from a stream.
   */
  public static void readExtractors(InputStream file) throws IOException, ClassNotFoundException {

    ObjectInputStream in = new ObjectInputStream(file);
    extractors = (Extractors) in.readObject();
    //System.err.println("Common word extractors: "+extractors.toString());
    extractorsRare = (Extractors) in.readObject();
    //System.err.println(extractorsRare.toString());
    //extractorsRare=new Extractors();
    //extractorsRare.init(ExtractorFramesRare.eFrames);
    int left = extractors.leftContext();
    int left_u = extractorsRare.leftContext();
    if (left_u > left) {
      left = left_u;
    }
    TestSentence.leftContext = left;
    int right = extractors.rightContext();
    int right_u = extractorsRare.rightContext();
    if (right_u > right) {
      right = right_u;
    }
    TestSentence.rightContext = right;
  }


  /**
   * This reads the .assoc file.  It is only used by LambdaSolveTagger.java
   * The same associations also appear in the main file, and are read by
   * read(), read_prev().
   *
   * @param modelFilename The string .assoc is appended and feature
   *    associations are then read from this file
   * @return The feature associations HashMap, or null if there is an error
   */
  public static HashMap<FeatureKey, Integer> readAssociations(String modelFilename) {
    try {
      HashMap<FeatureKey, Integer> fAssocs = new HashMap<FeatureKey, Integer>();
      String filename = modelFilename + ".assoc";
      InDataStreamFile rf = new InDataStreamFile(filename);
      int sizeAssoc = rf.readInt();
      for (int i = 0; i < sizeAssoc; i++) {
        int numF = rf.readInt();
        FeatureKey fK = new FeatureKey();
        fK.read(rf);
        fAssocs.put(fK, Integer.valueOf(numF));
      }
      rf.close();
      return fAssocs;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /* ----------- unused
  public static void release_mem() {
    // release dict
    dict.release();
    pairs.release();
    tHistories.release();
    tFeature.release();
    fAssociations.clear();
  }
  -------------- */


  public static void save_model(String filename, TaggerConfig config) {
    try {
      OutDataStreamFile file = new OutDataStreamFile(filename);
      //      System.out.println("Tag counts are:");
      //      for(String key : GlobalHolder.tagTokens.keySet()) {
      //        System.out.println(key + "\t" + GlobalHolder.tagTokens.get(key).size());
      //      }
      // config.writeConfig(file);
      saveConfig(config, file);
      file.writeInt(xSize);
      file.writeInt(ySize);
      dict.save(file);
      tags.save(file);

      saveExtractors(file);
      // write a boolean as to whether you're saving the config - this is messy and probably
      // not the best thing to do...
      //      if(config != null) {
      //        file.writeBoolean(true);
      //        saveConfig(config, file);
      //      } else {
      //        file.writeBoolean(false);
      //      }
      file.writeInt(fAssociations.size());
      for (Map.Entry<FeatureKey,Integer> item : fAssociations.entrySet()) {
        int numF = item.getValue().intValue();
        file.writeInt(numF);
        FeatureKey fk = item.getKey();
        fk.save(file);
      }

      //we don't save these because we can reconstruct them on opening
      //TODO: test time/space tradeoff of saving versus not saving
      //ambClasses.save(rf);

      LambdaSolve.save_lambdas(file, prob.lambda);
      file.close();
    } catch (IOException ioe) {
      System.err.println("Error saving tagger to file " + filename);
      ioe.printStackTrace();
    }
  }


  /**
   * This method is provided for backwards compatibility with the old tagger.  It reads
   * a tagger that was saved as multiple files into the current format and saves it back
   * out as a single file, newFilename.
   *
   * @param filename The name of the holder file, which is also used as a prefix for other filenames
   * @param newFilename The name of the new one-file model that will be written
   * @param config
   * @return true (whether this operation succeeded; always true
   * @throws Exception
   */
  public static boolean convertMultifileTagger(String filename, String newFilename, TaggerConfig config) throws Exception {
    InDataStreamFile rf = new InDataStreamFile(filename);
    GlobalHolder.init(config);
    if (VERBOSE) {
      System.err.println(" length of holder " + new File(filename).length());
    }

    xSize = rf.readInt();
    ySize = rf.readInt();
    dict.read(filename + ".dict");

    if (VERBOSE) {
      System.err.println(" dictionary read ");
    }
    tags.read(filename + ".tags");
    readExtractors(filename + ".ex");

    dict.setAmbClasses();

    int[] numFA = new int[extractors.getSize() + extractorsRare.getSize()];
    int sizeAssoc = rf.readInt();
    PrintFile pfVP = null;
    if (VERBOSE) {
      pfVP = new PrintFile("pairs.txt");
    }
    for (int i = 0; i < sizeAssoc; i++) {
      int numF = rf.readInt();
      FeatureKey fK = new FeatureKey();
      fK.read(rf);
      numFA[fK.num]++;
      fAssociations.put(fK, Integer.valueOf(numF));
    }

    if (VERBOSE) {
      pfVP.close();
    }
    if (VERBOSE) {
      for (int k = 0; k < numFA.length; k++) {
        System.err.println(" Number of features of kind " + k + ' ' + numFA[k]);
      }
    }
    prob = new LambdaSolveTagger(filename + ".prob");
    if (VERBOSE) {
      System.err.println(" prob read ");
    }

    save_model(newFilename, config);
    rf.close();
    return true;
  }


  /** This reads the complete tagger from a single model file, and inits
   *  the tagger using a combination of the properties passed in and
   *  those read from the file.
   *
   *  @param filename Filename to read from.  It's closed afterwords
   *  @throws Exception If I/O errors, etc.
   */
  public static void readModelAndInit(String filename) throws Exception {
    readModelAndInit(null, filename);
  }

  /** This reads the complete tagger from a single model file, and inits
   *  the tagger using a combination of the properties passed in and
   *  parameters from the file.
   *  <p>
   *  <i>Note for the future: This assumes that the TaggerConfig in the file
   *  has already been read and used.  It might be better to refactor
   *  things so that is all done inside this method, but for the moment
   *  it seemed better to leave working code alone [cdm 2008].</i>
   *
   *  @param config The tagger config
   *  @param filename Filename to read from.  It's closed afterwards.
   *  @throws Exception If I/O errors, etc.
   */
  public static void readModelAndInit(TaggerConfig config, String filename) throws Exception {
    // first check can open file ... or else leave with exception
    InDataStreamFile rf = new InDataStreamFile(filename);
    // then init tagger
    GlobalHolder.init(config);

    if (VERBOSE) {
      System.err.println(" length of model holder " + new File(filename).length());
    }
    readConfig(rf); // taggerconfig in file has already been put into config, so just read past it.

    xSize = rf.readInt();
    ySize = rf.readInt();
    dict.read(rf);

    if (VERBOSE) {
      System.err.println(" dictionary read ");
    }
    //if(tags == null) tags = new TTags();//this will just be written over anyway - seems like read should actually be static...
    tags.read(rf);
    readExtractors(rf);
//    boolean configSaved = rf.readBoolean();
//    if(configSaved)
//      readConfig(rf);
    dict.setAmbClasses();

    int[] numFA = new int[extractors.getSize() + extractorsRare.getSize()];
    int sizeAssoc = rf.readInt();
    // init the Hash at the right size for efficiency (avoid resizing ops)
    fAssociations = new HashMap<FeatureKey,Integer>(sizeAssoc);
    PrintFile pfVP = null;
    if (VERBOSE) {
      pfVP = new PrintFile("pairs.txt");
    }
    for (int i = 0; i < sizeAssoc; i++) {
      int numF = rf.readInt();
      FeatureKey fK = new FeatureKey();
      fK.read(rf);
      numFA[fK.num]++;
      fAssociations.put(fK, Integer.valueOf(numF));
    }
    if (VERBOSE) {
      pfVP.close();
    }
    if (VERBOSE) {
      for (int k = 0; k < numFA.length; k++) {
        System.err.println(" Number of features of kind " + k + ' ' + numFA[k]);
      }
    }
    prob = new LambdaSolveTagger(rf);
    if (VERBOSE) {
      System.err.println(" prob read ");
    }
    rf.close();
    //tFile.close();
  }


  public static void dumpModel() {
    assert fAssociations.size() == prob.lambda.length;
    for (Map.Entry<FeatureKey,Integer> fk : fAssociations.entrySet()) {
      System.out.println(fk.getKey() + ": " + prob.lambda[fk.getValue().intValue()]);
    }
  }


  public static boolean isRare(String word) {
    return dict.sum(word) < rareWordThresh;
  }

}
