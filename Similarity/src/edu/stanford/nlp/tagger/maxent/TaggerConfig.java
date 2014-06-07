package edu.stanford.nlp.tagger.maxent;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.DataInputStream;
import java.util.HashMap;
import java.util.Properties;

import edu.stanford.nlp.io.InDataStreamFile;
import edu.stanford.nlp.trees.TreeTransformer;
import edu.stanford.nlp.trees.TreeNormalizer;
import edu.stanford.nlp.util.StringUtils;
import edu.stanford.nlp.ling.TaggedWord;

/**
 * Reads and stores configuration information for a POS tagger.
 *
 *  @author William Morgan
 *  @author Anna Rafferty
 */
public class TaggerConfig extends Properties /* Inherits implementation of serializable! */ {

  private static final long serialVersionUID = -4136407850147157497L;

  private String[] openClassTags, closedClassTags, xmlInput;

  public enum Mode {
    TRAIN, TEST, TAG, CONVERT, DUMP
  }

  private Mode mode = Mode.TAG;

  /* defaults. Note: no known property has a null value! Don't need to test. */
  private static final String
  SEARCH = "cg",
  DELIMITER = "/",
  TOKENIZE = "true",
  DEBUG = "false",
  ITERATIONS = "100",
  ARCH = "",
  RARE_WORD_THRESH = String.valueOf(GlobalHolder.RARE_WORD_THRESH),
  MIN_FEATURE_THRESH = String.valueOf(GlobalHolder.MIN_FEATURE_THRESH),
  CUR_WORD_MIN_FEATURE_THRESH = String.valueOf(GlobalHolder.CUR_WORD_MIN_FEATURE_THRESH),
  RARE_WORD_MIN_FEATURE_THRESH = String.valueOf(GlobalHolder.RARE_WORD_MIN_FEATURE_THRESH),
  VERY_COMMON_WORD_THRESH = String.valueOf(GlobalHolder.VERY_COMMON_WORD_THRESH),
  SIGMA_SQUARED = String.valueOf(0.5),
  ENCODING = "UTF-8",
  LEARN_CLOSED_CLASS = "false",
  CLOSED_CLASS_THRESHOLD = String.valueOf(TTags.CLOSED_TAG_THRESHOLD),
  VERBOSE = "false",
  SGML = "false",
  INIT_FROM_TREES = "false",
  LANG = "",
  TOKENIZER_FACTORY = "",
  XML_OUTPUT = "",  // it's now a filename not boolean!
  TREE_TRANSFORMER = "",
  TREE_NORMALIZER = "",
  TREE_RANGE = "",
  TAG_INSIDE = "";

  private static final HashMap<String, String> defaultValues = new HashMap<String, String>();
  static {
    defaultValues.put("arch", ARCH);
    defaultValues.put("closedClassTags", "");
    defaultValues.put("closedClassTagThreshold", CLOSED_CLASS_THRESHOLD);
    defaultValues.put("search", SEARCH);
    defaultValues.put("delimiter", DELIMITER);
    defaultValues.put("tokenize", TOKENIZE);
    defaultValues.put("debug", DEBUG);
    defaultValues.put("iterations", ITERATIONS);
    defaultValues.put("rareWordThresh", RARE_WORD_THRESH);
    defaultValues.put("minFeatureThresh", MIN_FEATURE_THRESH);
    defaultValues.put("curWordMinFeatureThresh", CUR_WORD_MIN_FEATURE_THRESH);
    defaultValues.put("rareWordMinFeatureThresh", RARE_WORD_MIN_FEATURE_THRESH);
    defaultValues.put("veryCommonWordThresh", VERY_COMMON_WORD_THRESH);
    defaultValues.put("sigmaSquared", SIGMA_SQUARED);
    defaultValues.put("encoding", ENCODING);
    defaultValues.put("learnClosedClassTags", LEARN_CLOSED_CLASS);
    defaultValues.put("verbose", VERBOSE);
    defaultValues.put("sgml", SGML);
    defaultValues.put("initFromTrees", INIT_FROM_TREES);
    defaultValues.put("treeTransformer", TREE_TRANSFORMER);
    defaultValues.put("treeNormalizer", TREE_NORMALIZER);
    defaultValues.put("lang", LANG);
    defaultValues.put("tokenizerFactory", TOKENIZER_FACTORY);
    defaultValues.put("xmlOutput", XML_OUTPUT);
    defaultValues.put("treeRange", TREE_RANGE);
    defaultValues.put("tagInside", TAG_INSIDE);
    defaultValues.put("sgml", SGML);
  }

  /**
   * This constructor is just for creating an instance with default values.
   * Used internally.
   */
  private TaggerConfig() {
    super();
    this.putAll(defaultValues);
  }

  public TaggerConfig(String[] args) throws IOException {
    super();
    Properties props = new Properties();
    props.putAll(StringUtils.argsToProperties(args));

    if (props.getProperty("") != null) {
      throw new RuntimeException("unknown argument(s): \"" + props.getProperty("") + '\"');
    }

    if (props.getProperty("genprops") != null) {
      printGenProps();
      System.exit(0);
    }

    //First thing to do is check if we're loading a classifier - if so, we need to load the config file from there
    //Figure out what mode we're in
    if (props.containsKey("trainFile")) {
      //Training mode
      mode = Mode.TRAIN;
      this.setProperty("file", props.getProperty("trainFile", "").trim());
    } else if (props.containsKey("testFile")) {
      //Testing mode
      mode = Mode.TEST;
      this.setProperty("file", props.getProperty("testFile", "").trim());
    } else if (props.containsKey("textFile")) {
      //Tagging mode
      mode = Mode.TAG;
      this.setProperty("file", props.getProperty("textFile", "").trim());
    } else if (props.containsKey("convertToSingleFile")) {
      mode = Mode.CONVERT;
      this.setProperty("file", props.getProperty("convertToSingleFile").trim());
    } else if (props.containsKey("dump")) {
      mode = Mode.DUMP;
      this.setProperty("file", props.getProperty("dump").trim());
      props.setProperty("model", props.getProperty("dump").trim());
    } else {
      mode = Mode.TAG;
      this.setProperty("file", "stdin");
    }
    //for any mode other than train, we load a classifier, which means we load a config - model always needs to be specified
    //on command line/in props file
    //Get the path to the model (or the path where you'd like to save the model); this is necessary for training, testing, and tagging
    this.setProperty("model", props.getProperty("model", "").trim());
    if ( ! (mode == Mode.DUMP) && this.getProperty("model").equals("")) {
      throw new RuntimeException("'model' parameter must be specified");
    }

    /* Try and use the default properties from the model */
    //Properties modelProps = new Properties();
    TaggerConfig oldConfig = new TaggerConfig(); // loads default values in oldConfig
    if (mode != Mode.TRAIN && mode != Mode.CONVERT) {
      try {
        System.err.println("Loading default properties from trained tagger " + getProperty("model"));
        DataInputStream in = new InDataStreamFile(this.getProperty("model"));
        oldConfig.putAll(GlobalHolder.readConfig(in)); // overwrites defaults with any serialized values.
        in.close();
        // System.err.println("Read config");
        // System.err.println(oldConfig);
        //modelProps.load(new BufferedInputStream(new FileInputStream(mpfn)));
      } catch (Exception e) {
        System.err.println("No such trained tagger config file found.");
        e.printStackTrace();
      }
    }

    this.setProperty("search", props.getProperty("search", oldConfig.getProperty("search")).trim().toLowerCase());
    if ( ! this.getProperty("search").equals("cg") && ! this.getProperty("search").equals("iis")) {
      throw new RuntimeException("'search' must be one of 'cg' or 'iis': " + this.getProperty("search"));
    }

    this.setProperty("sigmaSquared", props.getProperty("sigmaSquared", oldConfig.getProperty("sigmaSquared")));

    this.setProperty("delimiter", props.getProperty("delimiter", oldConfig.getProperty("delimiter")));
    TestSentence.delimiter = undefined("delimiter") ? TestSentence.delimiter : this.getDelimiter();
    TaggedWord.setDivider(TestSentence.delimiter); // for output

    this.setProperty("iterations", props.getProperty("iterations", oldConfig.getProperty("iterations")));
    this.setProperty("rareWordThresh", props.getProperty("rareWordThresh", oldConfig.getProperty("rareWordThresh")));
    this.setProperty("minFeatureThresh", props.getProperty("minFeatureThresh", oldConfig.getProperty("minFeatureThresh")));
    this.setProperty("curWordMinFeatureThresh", props.getProperty("curWordMinFeatureThresh", oldConfig.getProperty("curWordMinFeatureThresh")));
    this.setProperty("rareWordMinFeatureThresh", props.getProperty("rareWordMinFeatureThresh", oldConfig.getProperty("rareWordMinFeatureThresh")));
    this.setProperty("veryCommonWordThresh", props.getProperty("veryCommonWordThresh", oldConfig.getProperty("veryCommonWordThresh")));

    this.setProperty("lang", props.getProperty("lang", oldConfig.getProperty("lang")));

    //this is a property that is stored (not like the general properties)
    String ocs = props.getProperty("openClassTags", "");
    if (ocs.equals("") || ocs.equals("true"))  {/* necessary because of peculiarities of java -what does this note mean?? i seriously doubt its veracity...*/
      if (oldConfig.getOpenClassTags() == null) {
        openClassTags = StringUtils.EMPTY_STRING_ARRAY;
      } else {
        openClassTags = oldConfig.getOpenClassTags();
      }
    } else {
      openClassTags = ocs.split("\\s+");
    }

    //this is a property that is stored (not like the general properties)
    String ccs = props.getProperty("closedClassTags");

    if ((ccs == null) || ccs.equals("") || ccs.equals("true")) {/* necessary because of peculiarities of java */
      if (oldConfig.getClosedClassTags() == null) {
        closedClassTags = StringUtils.EMPTY_STRING_ARRAY;
      } else {
        closedClassTags = oldConfig.getClosedClassTags();
      }
    } else {
      closedClassTags = ccs.split("\\s+");
    }

    this.setProperty("learnClosedClassTags", props.getProperty("learnClosedClassTags", oldConfig.getProperty("learnClosedClassTags")));

    this.setProperty("closedClassTagThreshold", props.getProperty("closedClassTagThreshold", oldConfig.getProperty("closedClassTagThreshold")));

    this.setProperty("arch", props.getProperty("arch", oldConfig.getProperty("arch")));
    this.setProperty("tokenize", props.getProperty("tokenize", oldConfig.getProperty("tokenize")));
    this.setProperty("tokenizerFactory", props.getProperty("tokenizerFactory", oldConfig.getProperty("tokenizerFactory")));

    this.setProperty("debugPrefix", props.getProperty("debugPrefix", oldConfig.getProperty("debugPrefix", "")));
    this.setProperty("debug", props.getProperty("debug", DEBUG));

    this.setProperty("encoding", props.getProperty("encoding", oldConfig.getProperty("encoding")));
    this.setProperty("sgml", props.getProperty("sgml", oldConfig.getProperty("sgml")));
    this.setProperty("verbose", props.getProperty("verbose", oldConfig.getProperty("verbose")));

    this.setProperty("initFromTrees", props.getProperty("initFromTrees", oldConfig.getProperty("initFromTrees")));
    this.setProperty("treeRange", props.getProperty("treeRange", oldConfig.getProperty("treeRange")));

    this.setProperty("treeTransformer", props.getProperty("treeTransformer", oldConfig.getProperty("treeTransformer")));
    this.setProperty("treeNormalizer", props.getProperty("treeNormalizer", oldConfig.getProperty("treeNormalizer")));

    //this is a property that is stored (not like the general properties)
    String xmlInputProp = props.getProperty("xmlInput", null);
    if ((xmlInputProp == null) || xmlInputProp.equals("")) {/* necessary because of peculiarities of java */
      if(oldConfig.getXMLInput() == null)
        xmlInput = StringUtils.EMPTY_STRING_ARRAY;
      else
        xmlInput = oldConfig.getXMLInput();
    } else {
      xmlInput = xmlInputProp.split("\\s+");
    }
    this.setProperty("xmlOutput", props.getProperty("xmlOutput", oldConfig.getProperty("xmlOutput"))); //this isn't something we save from time to time
    this.setProperty("tagInside", props.getProperty("tagInside", oldConfig.getProperty("tagInside"))); //this isn't something we save from time to time
  }


  public String getModel() { return getProperty("model"); }

  public String getFile() { return getProperty("file"); }

  public String getSearch() { return getProperty("search"); }

  public double getSigmaSquared() { return Double.parseDouble(getProperty("sigmaSquared")); }

  public int getIterations() { return Integer.parseInt(getProperty("iterations")); }

  public int getRareWordThresh() { return Integer.parseInt(getProperty("rareWordThresh")); }

  public int getMinFeatureThresh() { return Integer.parseInt(getProperty("minFeatureThresh")); }

  public int getCurWordMinFeatureThresh() { return Integer.parseInt(getProperty("curWordMinFeatureThresh")); }

  public int getRareWordMinFeatureThresh() { return Integer.parseInt(getProperty("rareWordMinFeatureThresh")); }

  public int getVeryCommonWordThresh() { return Integer.parseInt(getProperty("veryCommonWordThresh")); }

  public String getLang() { return getProperty("lang"); }

  public String[] getOpenClassTags() { return openClassTags; }

  public String[] getClosedClassTags() { return closedClassTags; }

  public boolean getLearnClosedClassTags() { return Boolean.parseBoolean(getProperty("learnClosedClassTags")); }

  public int getClosedTagThreshold() { return Integer.parseInt(getProperty("closedClassTagThreshold")); }

  public String getArch() { return getProperty("arch"); }

  public boolean getDebug() { return Boolean.parseBoolean(getProperty("debug")); }

  public String getDebugPrefix() { return getProperty("debugPrefix"); }

  public String getTokenizerFactory() { return getProperty("tokenizerFactory"); }

  public final String getDelimiter() { return getProperty("delimiter"); }

  public boolean getTokenize() { return Boolean.parseBoolean(getProperty("tokenize")); }

  public String getEncoding() { return getProperty("encoding"); }

  public String[] getXMLInput() { return xmlInput; }

  public String getXMLOutput() { return getProperty("xmlOutput"); }

  public boolean getInitFromTrees() { return Boolean.parseBoolean(getProperty("initFromTrees")); }

  public String getTreeRange() { return getProperty("treeRange"); }

  public boolean getVerbose() { return Boolean.parseBoolean(getProperty("verbose")); }

  public boolean getSGML() { return Boolean.parseBoolean(getProperty("sgml")); }

  public String getTagInside() { return getProperty("tagInside"); }

  /** Return true if a property has either no (null) value or an empty String
   *  value
   *  @param property The property to test
   *  @return true if the value is null or ""
   */
  private boolean undefined(String property) {
    String val = getProperty(property);
    return val == null || "".equals(val);
  }

  public TreeTransformer getTreeTransformer() {

    if (undefined("treeTransformer")) {
        return null;
    }
    //Try to load the tree transformer by reflection
    try {
      TreeTransformer t = (TreeTransformer) Class.forName(getProperty("treeTransformer")).newInstance();
      return t;
    } catch (Exception e) {
      System.err.println("Error loading treeTransformer - no transformer will be used.");
      e.printStackTrace();
      return null;
    }
  }

  public TreeNormalizer getTreeNormalizer() {

    if (undefined("treeNormalizer")) {
        return null;
    }
    //Try to load the tree normalizer by reflection
    try {
      TreeNormalizer t = (TreeNormalizer) Class.forName(getProperty("treeNormalizer")).newInstance();
      return t;
    } catch (Exception e) {
      System.err.println("Error loading treeNormalizer - no TreeNormalizer will be used.");
      e.printStackTrace();
      return null;
    }
  }


  public void dump() { dump(new PrintWriter(System.err)); }

  public void dump(PrintStream stream) {
    PrintWriter pw = new PrintWriter(stream);
    dump(pw);
  }

  public void dump(PrintWriter pw) {
    pw.println("                   model = " + getProperty("model"));
    pw.println("                    arch = " + getProperty("arch"));
    if (mode == Mode.TRAIN) {
      pw.println("               trainFile = " + getProperty("file"));
    } else if (mode == Mode.TAG) {
      pw.println("                textFile = " + getProperty("file"));
    } else if (mode == Mode.TEST) {
      pw.println("                testFile = " + getProperty("file"));
    }

    pw.println("         closedClassTags = " + (closedClassTags == null ? "": StringUtils.join(closedClassTags, " ")));
    pw.println(" closedClassTagThreshold = " + getProperty("closedClassTagThreshold"));
    pw.println(" curWordMinFeatureThresh = " + getProperty("curWordMinFeatureThresh"));
    pw.println("                   debug = " + getProperty("debug"));
    pw.println("             debugPrefix = " + getProperty("debugPrefix"));
    pw.println("               delimiter = " + getProperty("delimiter"));
    pw.println("                encoding = " + getProperty("encoding"));
    pw.println("           initFromTrees = " + getProperty("initFromTrees"));
    pw.println("              iterations = " + getProperty("iterations"));
    pw.println("                    lang = " + getProperty("lang"));
    pw.println("    learnClosedClassTags = " + getProperty("learnClosedClassTags"));
    pw.println("        minFeatureThresh = " + getProperty("minFeatureThresh"));
    pw.println("           openClassTags = " + (openClassTags == null ? "": StringUtils.join(openClassTags, " ")));
    pw.println("rareWordMinFeatureThresh = " + getProperty("rareWordMinFeatureThresh"));
    pw.println("          rareWordThresh = " + getProperty("rareWordThresh"));
    pw.println("                  search = " + getProperty("search"));
    pw.println("                    sgml = " + getProperty("sgml"));
    pw.println("            sigmaSquared = " + getProperty("sigmaSquared"));
    pw.println("               tagInside = " + getProperty("tagInside"));
    pw.println("                tokenize = " + getProperty("tokenize"));
    pw.println("        tokenizerFactory = " + getProperty("tokenizerFactory"));
    pw.println("               treeRange = " + getProperty("treeRange"));
    pw.println("          treeNormalizer = " + getProperty("treeNormalizer"));
    pw.println("         treeTransformer = " + getProperty("treeTransformer"));
    pw.println("                 verbose = " + getProperty("verbose"));
    pw.println("    veryCommonWordThresh = " + getProperty("veryCommonWordThresh"));
    pw.println("                xmlInput = " + getProperty("xmlInput"));
    pw.println("               xmlOutput = " + getProperty("xmlOutput"));
    pw.flush();
  }

  @Override
  public String toString() {
    StringWriter sw = new StringWriter(200);
    PrintWriter pw = new PrintWriter(sw);
    dump(pw);
    return sw.toString();
  }


  /**
   * Prints out the automatically generated props file - in its own
   * method to make code above easier to read
   */
  private static void printGenProps() {
    System.out.println("## Sample properties file for maxent tagger. This file is used for three main");
    System.out.println("## operations: training, testing, and tagging. It may also be used to convert a");
    System.out.println("## To train or test a model, or to tag something, run:");
    System.out.println("##   java edu.stanford.nlp.tagger.maxent.MaxentTagger -prop <properties file>");
    System.out.println("## Arguments can be overridden on the commandline, e.g.:");
    System.out.println("##   java ....MaxentTagger -prop <properties file> -file /other/file ");
    System.out.println();

    System.out.println("# Model file name (created at train time; used at tag and test time)");
    System.out.println("# (you can leave this blank and specify it on the commandline with -model)");
    System.out.println("# model = ");
    System.out.println();

    System.out.println("# Path to file to be operated on (trained from, tested against, or tagged)");
    System.out.println("# Specify -textFile <filename> to tag text in the given file, -trainFile <filename> to");
    System.out.println("# to train a model using data in the given file, or -testFile <filname> to test your");
    System.out.println("# model using data in the given file.  Alternatively, you may specify");
    System.out.println("# -dump <filename> to dump the parameters stored in a model or ");
    System.out.println("# -convertToSingleFile <filename> to save an old, multi-file model (specified as -model)");
    System.out.println("# to the new single file format.  The new model will be saved in the file filename.");
    System.out.println("# If you choose to convert an old file, you must specify ");
    System.out.println("# the correct 'arch' parameter used to create the original model.");
    System.out.println("# trainFile = ");
    System.out.println();

    System.out.println("# Delimiter character that separates word and pos tags (for both training");
    System.out.println("# and test data).");
    System.out.println("# delimiter = " + DELIMITER);
    System.out.println();

    System.out.println("# Encoding format in which files are stored.  If left blank, UTF-8 is assumed.");
    System.out.println("# encoding = " + ENCODING);
    System.out.println();


    System.out.println("######### parameters for tag and test operations #########");
    System.out.println();

    System.out.println("# Class to use for tokenization. Default blank value means Penn Treebank");
    System.out.println("# tokenization.  If you'd like to just assume that tokenization has been done,");
    System.out.println("# and the input is whitespace-tokenized, use");
    System.out.println("# edu.stanford.nlp.process.WhitespaceTokenizer or set tokenize to false.");
    System.out.println("# tokenizerFactory = ");
    System.out.println();

    System.out.println("# Whether to tokenize text for tag and test operations. Default is true.");
    System.out.println("# If false, your text must already be whitespace tokenized.");
    System.out.println("# tokenize = " + TOKENIZE);
    System.out.println();

    System.out.println("# Write debugging information (words, top words, unknown words). Useful for");
    System.out.println("# error analysis. Default is false.");
    System.out.println("# debug = "+ DEBUG);
    System.out.println();

    System.out.println("# Prefix for debugging output (if debug == true). Default is to use the");
    System.out.println("# filename from 'file'");
    System.out.println("# debugPrefix = ");
    System.out.println();

    System.out.println("######### parameters for training  #########");
    System.out.println();

    System.out.println("# model architecture: This is one or more comma separated strings, which");
    System.out.println("# specify which extractors to use. Some of them take an integer or string");
    System.out.println("# (file path) argument in parentheses, written as n and s below:");
    System.out.println("# 'left3words', 'left5words', 'bidirectional', 'bidirectional5words',");
    System.out.println("# 'generic', 'sighan2005', 'german', 'words(n)', 'wordshapes(n)',");
    System.out.println("# 'naacl2003unknowns', 'naacl2003conjunctions', 'distsim(s)',");
    System.out.println("# 'suffix(n)', 'prefix(n)', 'prefixsuffix(n)', 'capitalizationsuffix(n)',");
    System.out.println("# 'lctagfeatures', 'order(k)', 'chinesedictionaryfeatures(s)'.");
    System.out.println("# These keywords determines the features extracted.  'generic' is language independent.");
    System.out.println("# distsim: Distributional similarity classes can be an added source of information");
    System.out.println("# about your words. An English distsim file is included, or you can use your own.");
    System.out.println("# arch = ");
    System.out.println();

    System.out.println("# 'language'.  This is really the tag set which is used for the");
    System.out.println("# list of open-class tags, and perhaps deterministic  tag");
    System.out.println("# expansion). Currently we have 'english', 'arabic', 'german', 'chinese'");
    System.out.println("# or 'polish' predefined. For your own language, you can specify ");
    System.out.println("# the same information via openClassTags or closedClassTags below");
    System.out.println("# (only ONE of these three options may be specified). ");
    System.out.println("# 'english' means UPenn English treebank tags. 'german' is STTS");
    System.out.println("# 'chinese' is CTB, and Arabic is an expanded Bies mapping from the ATB");
    System.out.println("# 'polish' means some tags that some guy on the internet once used. ");
    System.out.println("# See the TTags class for more information.");
    System.out.println("# lang = ");
    System.out.println();

    System.out.println("# a space-delimited list of open-class parts of speech");
    System.out.println("# alternatively, you can specify language above to use a pre-defined list or specify the closed class tags (below)");
    System.out.println("# openClassTags = ");
    System.out.println();

    System.out.println("# a space-delimited list of closed-class parts of speech");
    System.out.println("# alternatively, you can specify language above to use a pre-defined list or specify the open class tags (above)");
    System.out.println("# closedClassTags = ");
    System.out.println();

    System.out.println("# A boolean indicating whether you would like the trained model to set POS tags as closed");
    System.out.println("# based on their frequency in training; default is false.  The frequency threshold can be set below. ");
    System.out.println("# This option is ignored if any of {openClassTags, closedClassTags, lang} are specified.");
    System.out.println("# learnClosedClassTags = ");
    System.out.println();

    System.out.println("# Used only if learnClosedClassTags=true.  Tags that have fewer tokens than this threshold are");
    System.out.println("# considered closed in the trained model.");
    System.out.println("# closedClassTagThreshold = ");
    System.out.println();

    System.out.println("# search method. 'cg' (conjugate gradient (default)) or 'iis' (improved ");
    System.out.println("# iterative scaling)");
    System.out.println("# search = " + SEARCH);
    System.out.println();

    System.out.println("# for conjugate gradient search, sigma-squared smoothing/regularization");
    System.out.println("# parameter. if left blank, the default is 0.5, which is usually okay");
    System.out.println("# sigmaSquared = " + SIGMA_SQUARED);
    System.out.println();

    System.out.println("# For improved iterative scaling, the number of iterations, otherwise ignored");
    System.out.println("# iterations = " + ITERATIONS);
    System.out.println();

    System.out.println("# rare word threshold. words that occur less than this number of");
    System.out.println("# times are considered rare words.");
    System.out.println("# rareWordThresh = " + RARE_WORD_THRESH);
    System.out.println();

    System.out.println("# minimum feature threshold. features whose history appears less");
    System.out.println("# than this number of times are ignored.");
    System.out.println("# minFeatureThresh = " + MIN_FEATURE_THRESH);
    System.out.println();

    System.out.println("# current word feature threshold. words that occur more than this");
    System.out.println("# number of times will generate features with all of their occurring");
    System.out.println("# tags.");
    System.out.println("# curWordMinFeatureThresh = " + CUR_WORD_MIN_FEATURE_THRESH);
    System.out.println();

    System.out.println("# rare word minimum feature threshold. features of rare words whose histories");
    System.out.println("# appear less than this times will be ignored.");
    System.out.println("# rareWordMinFeatureThresh = " + RARE_WORD_MIN_FEATURE_THRESH);
    System.out.println();

    System.out.println("# very common word threshold. words that occur more than this number of");
    System.out.println("# times will form an equivalence class by themselves. ignored unless");
    System.out.println("# you are using equivalence classes.");
    System.out.println("# veryCommonWordThresh = " + VERY_COMMON_WORD_THRESH);

    System.out.println("# distSimPath: Path to the distributional similarity file.  The included English distsim file");
    System.out.println("# is egw.bnc.200.pruned, located in the main part of speech tagger directory.");
    System.out.println("# distSimPath = ");
    System.out.println();

    System.out.println("# initFromTrees =");
    System.out.println("# treeTransformer =");
    System.out.println("# treeNormalizer =");
    System.out.println("# treeRange =");
    System.out.println("# sgml = ");
    System.out.println("# tagInside = ");
  }

  public Mode getMode() {
    return mode;
  }

}
