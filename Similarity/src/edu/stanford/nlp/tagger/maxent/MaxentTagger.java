//MaxentTagger -- StanfordMaxEnt, A Maximum Entropy Toolkit
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

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Iterator;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.io.PrintFile;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.SentenceProcessor;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.objectbank.ObjectBank;
import edu.stanford.nlp.objectbank.ReaderIteratorFactory;
import edu.stanford.nlp.process.*;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;
import edu.stanford.nlp.util.Function;
import edu.stanford.nlp.util.Timing;
import edu.stanford.nlp.sequences.PlainTextDocumentReaderAndWriter;
import edu.stanford.nlp.ling.CoreAnnotations;


/**
 * The main class for users to run, train, and test the part of speech tagger.
 *
 * You can tag things through the Java API or from the command line.
 * The two taggers included in this distribution are:
 * <ul>
 * <li> A bi-directional dependency network tagger in models/bidirectional-wsj-0-18.tagger.
 *      Its accuracy was 97.27% on Penn WSJ sec 22-24.</li>
 * <li> A model using only left sequence information and otherwise the same
 *      unknown words and lexical features as the previous model in models/left3words-wsj-0-18.tagger.
 *      Its accuracy was 96.99% on Penn WSJ sec 22-24.</li>
 * </ul>
 *
 * <h3>Using the Java API</h3>
 * <dl>
 * <dt>
 * A MaxentTagger can be made with a constructor taking as argument the location of parameter files for a trained tagger: </dt>
 * <dd> <code>MaxentTagger tagger = new MaxentTagger("models/left3words-wsj-0-18.tagger");</code></dd>
 * <p>
 * <dt>Alternatively, a constructor with no arguments can be used, which reads the parameters from a default location (which has to be set in the source file, and is set to a path that works on the Stanford NLP machines):</dt>
 * <dd><code>MaxentTagger tagger = new MaxentTagger(); </code></dd>
 * <p>
 * <dt>To tag a string of words and get a string of tagged words:</dt>
 * <dd> <code>String taggedString = maxentTagger.tagString("Here's a tagged string.")</code></dd>
 * <p>
 * <dt>To tag a Sentence and get a TaggedSentence: </dt>
 * <dd><code>Sentence taggedSentence = maxentTagger.tagSentence(Sentence sentence)</code></dd>
 * <dd><code>Sentence taggedSentence = maxentTagger.apply(Sentence sentence)</code></dd>
 * <p>
 * <dt>To tag a list of sentences and get back a list of tagged sentences:
 * <dd><code> List taggedList = maxentTagger.process(List sentences)</code></dd>
 * </dl>
 * <p/>
 * Here is an example of using the static tagString method. This method assumes that the text
 * in the input String is already correctly tokenized. <br>
 * <code>MaxentTagger.init("models/left3words-wsj-0-18.tagger");<br>
 * String taggedString = MaxentTagger.tagString("Here's a tagged string.");<br>
 * String taggedString2 = MaxentTagger.tagString("This is your life.");<br>
 * </code> <br>
 * Output:<br>
 * <code>Here's/JJ a/DT tagged/VBD string./NNP</code><br>
 * <code>This/DT is/VBZ your/PRP$ life./NN</code><p>
 * The MaxentTagger is initialized using a static call to <code>init</code>,
 * which takes the path to a trained model as an argument.  The trained model is loaded immediately (which may take awhile);
 * subsequent calls to <code>init</code> will have no effect.
 * If no path is passed to <code>init</code>, the default trained model is used.
 * <code>tagString</code> can then be called with an untagged String; a tagged String is
 * returned (serious errors could result in a <code>null</code> return value).
 * <p/>
 *
 * <h3>Using the command line</h3>
 *
 * Tagging, testing, and training can all also be done via the command line.
 * <h3>Training from the command line</h3>
 * To train a model from the command line, first generate a property file:
 * <pre>java edu.stanford.nlp.tagger.maxent.MaxentTagger -genprops </pre>
 *
 * This gets you a default properties file with descriptions of each parameter you can set in
 * your trained model.  You can modify the properties file , or use the default options.  To train, run:
 * <pre>java -mx1g edu.stanford.nlp.tagger.maxent.MaxentTagger -props myPropertiesFile.props </pre>
 *
 *  with the appropriate properties file specified; any argument you give in the properties file can also
 *  be specified on the command line.  You must have specified a model using -model, either in the properties file
 *  or on the command line, as well as a file containing tagged words using -trainFile.
 *
 * <h3>Tagging and Testing from the command line</h3>
 *
 * Usage:
 * For tagging (plain text):
 * <pre>java edu.stanford.nlp.tagger.maxent.MaxentTagger -model &lt;modelFile&gt; -textFile &lt;textfile&gt; </pre>
 * For testing (evaluating against tagged text):
 * <pre>java edu.stanford.nlp.tagger.maxent.MaxentTagger -model &lt;modelFile&gt; -testFile &lt;testfile&gt; </pre>
 * You can use the same properties file as for training
 * if you pass it in with the "-props" argument. The most important
 * arguments for tagging (besides "model" and "file") are "tokenize"
 * and "tokenizerFactory". See below for more details.
 *
 * Note that the tagger assumes input has not yet been tokenized and tokenizes it using a default
 * English tokenizer.  If your input has already been tokenized, use the flag "-tokenized".
 *
 * <p> Parameters can be defined using a Properties file
 * (specified on the command-line with <code>-prop</code> <i>propFile</i>),
 * or directly on the command line (by preceding their name with a minust sign
 * ("-") to turn them into a flag. The following properties are recognized:
 * </p>
 * <table border="1">
 * <tr><td><b>Property Name</b></td><td><b>Type</b></td><td><b>Default Value</b></td><td><b>Relevant Phase(s)</b></td><td><b>Description</b></td></tr>
 * <tr><td><b>model</b></td><td><b>String</b></td><td><b>N/A</b></td><td><b>All</b></td><td><b>Path and filename where you would like to save the model (training) or where the model should be loaded from (testing, tagging).</b></td></tr>
 * <tr><td><b>trainFile</b></td><td><b>String</b></td><td><b>N/A</b></td><td><b>Train</b></td><td><b>Path to the file holding the training data; specifying this option puts the tagger in training mode.  Only one of 'trainFile','testFile','texFile', and 'convertToSingleFile' may be specified.</b></td></tr>
 * <tr><td><b>testFile</b></td><td><b>String</b></td><td><b>N/A</b></td><td><b>Test</b></td><td><b>Path to the file holding the test data; specifying this option puts the tagger in testing mode.  Only one of 'trainFile','testFile','texFile', and 'convertToSingleFile' may be specified.</b></td></tr>
 * <tr><td><b>textFile</b></td><td><b>String</b></td><td><b>N/A</b></td><td><b>Tag</b></td><td><b>Path to the file holding the text to tag; specifying this option puts the tagger in tagging mode.  Only one of 'trainFile','testFile','textFile', and 'convertToSingleFile' may be specified.</b></td></tr>
 * <tr><td><b>convertToSingleFile</b></td><td><b>String</b></td><td><b>N/A</b></td><td><b>N/A</b></td><td><b>Provided only for backwards compatibility, this option allows you to convert a tagger trained using a previous version of the tagger to the new single-file format.  The value of this flag should be the path for the new model file, 'model' should be the path prefix to the old tagger (up to but not including the ".holder"), and you should supply the properties configuration for the old tagger with -props (before these two arguments).</b></td></tr>
 * <tr><td><b>genprops</b></td><td><b>boolean</b></td><td><b>N/A</b></td><td><b>N/A</b></td><td><b>Use this option to output a default properties file, containing information about each of the possible configuration options.</b></td></tr>
 * <tr><td><b>delimiter</b></td><td><b>char</b></td><td><b>/</b></td><td><b>All</b></td><td><b>Delimiter character that separates word and part of speech tags.  For training and testing, this is the delimiter used in the train/test files.  For tagging, this is the character that will be inserted between words and tags in the output.</b></td></tr>
 * <tr><td><b>encoding</b></td><td><b>String</b></td><td><b>UTF-8</b></td><td><b>All</b></td><td><b>Encoding of the read files (training, testing) and the output text files.</b></td></tr>
 * <tr><td><b>tokenize</b></td><td><b>boolean</b></td><td><b>true</b></td><td><b>Tag,Test</b></td><td><b>Whether or not the file has been tokenized (so that white space separates all and only those things that should be tagged as separate words).</b></td></tr>
 * <tr><td><b>tokenizerFactory</b></td><td><b>String</b></td><td><b>edu.stanford.nlp.process.PTBTokenizer</b></td><td><b>Tag,Test</b></td><td><b>Fully qualified classname of the tokenizer to use.  edu.stanford.nlp.process.PTBTokenizer does basic English tokenization.</b></td></tr>
 * <tr><td><b>arch</b></td><td><b>String</b></td><td><b>generic</b></td><td><b>Train</b></td><td><b>Architecture of the model, as a comma-separated list of options, some with a parenthesized integer argument written k here: this determines what features are sed to build your model.  Options are 'left3words', 'left5words', 'bidirectional', 'bidirectional5words', generic', 'sighan2005' (Chinese), 'german', 'words(k),' 'naacl2003unknowns', 'naacl2003conjunctions', wordshapes(k), motleyUnknown, suffix(k), prefix(k), prefixsuffix(k), capitalizationsuffix(k), distsim(s), chinesedictionaryfeatures(s), lctagfeatures, unicodeshapes(k). The left3words architectures are faster, but slightly less accurate, than the bidirectional architectures.  'naacl2003unknowns' was our traditional set of unknown word features, but you can now specify features more flexibility via the various other supported keywords. The 'shapes' options map words to equivalence classes, which slightly increase accuracy.</b></td></tr>
 * <tr><td><b>lang</b></td><td><b>String</b></td><td><b>english</b></td><td><b>Train</b></td><td><b>Language from which the part of speech tags are drawn. This option determines which tags are considered closed-class (only fixed set of words can be tagged with a closed-class tag, such as prepositions). Defined languages are 'english' (Penn tagset), 'polish' (very rudimentary), 'chinese', 'arabic', 'german', and 'medline'.  </b></td></tr>
 * <tr><td><b>openClassTags</b></td><td><b>String</b></td><td><b>N/A</b></td><td><b>Train</b></td><td><b>Space separated list of tags that should be considered open-class.  All tags encountered that are not in this list are considered closed-class.  E.g. format: "NN VB"</b></td></tr>
 * <tr><td><b>closedClassTags</b></td><td><b>String</b></td><td><b>N/A</b></td><td><b>Train</b></td><td><b>Space separated list of tags that should be considered closed-class.  All tags encountered that are not in this list are considered open-class.</b></td></tr>
 * <tr><td><b>learnClosedClassTags</b></td><td><b>boolean</b></td><td><b>false</b></td><td><b>Train</b></td><td><b>If true, induce which tags are closed-class by counting as closed-class tags all those tags which have fewer unique word tokens than closedClassTagThreshold. </b></td></tr>
 * <tr><td><b>closedClassTagThreshold</b></td><td><b>int</b></td><td><b>int</b></td><td><b>Train</b></td><td><b>Number of unique word tokens that a tag may have and still be considered closed-class; relevant only if learnClosedClassTags is true.</b></td></tr>
 * <tr><td><b>sgml</b></td><td><b>boolean</b></td><td><b>false</b></td><td><b>Tag, Test</b></td><td><b>Very basic tagging of the contents of all sgml fields; for more complex mark-up, consider using the xmlInput option.</b></td></tr>
 * <tr><td><b>xmlInput</b></td><td><b>String</b></td><td><b></b></td><td><b>Tag, Test</b></td><td><b>Give a space separated list of tags in an XML file whose content you would like tagged.  Any internal tags that appear in the content of fields you would like tagged will be discarded; the rest of the XML will be preserved and the original text of specified fields will be replaced with the tagged text.</b></td></tr>
 * <tr><td><b>xmlOutput</b></td><td><b>String</b></td><td><b>""</b></td><td><b>Tag</b></td><td><b>If a path is given, the tagged data be written out to the given file in xml.  If non-empty, each word will be written out within a word tag, with the part of speech as an attribute.  If original input was XML, this will just appear in the field where the text originally came from.  Otherwise, word tags will be surrounded by sentence tags as well.  E.g., &lt;sentence id="0"&gt;&lt;word id="0" pos="NN"&gt;computer&lt;/word&gt;&lt;/sentence&gt;</b></td></tr>
 * <tr><td><b>tagInside</b></td><td><b>String</b></td><td><b>""</b></td><td><b>Tag</b></td><td><b>Tags inside elements that match the regular expression given in the String.</b></td></tr>
 * <tr><td><b>search</b></td><td><b>String</b></td><td><b>cg</b></td><td><b>Train</b></td><td><b>Specify the search method to be used in the optimization method for training.  Options are 'cg' (conjugate gradient) or 'iis' (improved iterative scaling).</b></td></tr>
 * <tr><td><b>sigmaSquared</b></td><td><b>double</b></td><td><b>0.5</b></td><td><b>Train</b></td><td><b>Sigma-squared smoothing/regularization parameter to be used for conjugate gradient search.  Default usually works reasonably well.</b></td></tr>
 * <tr><td><b>iterations</b></td><td><b>int</b></td><td><b>100</b></td><td><b>Train</b></td><td><b>Number of iterations to be used for improved iterative scaling.</b></td></tr>
 * <tr><td><b>rareWordThresh</b></td><td><b>int</b></td><td><b>5</b></td><td><b>Train</b></td><td><b>Words that appear fewer than this number of times during training are considered rare words and use extra rare word features.</b></td></tr>
 * <tr><td><b>minFeatureThreshold</b></td><td><b>int</b></td><td><b>5</b></td><td><b>Train</b></td><td><b>Features whose history appears fewer than this number of times are discarded.</b></td></tr>
 * <tr><td><b>curWordMinFeatureThreshold</b></td><td><b>int</b></td><td><b>2</b></td><td><b>Train</b></td><td><b>Words that occur more than this number of times will generate features with all of the tags they've been seen with.</b></td></tr>
 * <tr><td><b>rareWordMinFeatureThresh</b></td><td><b>int</b></td><td><b>10</b></td><td><b>Train</b></td><td><b>Features of rare words whose histories occur fewer than this number of times are discarded.</b></td></tr>
 * <tr><td><b>veryCommonWordThresh</b></td><td><b>int</b></td><td><b>250</b></td><td><b>Train</b></td><td><b>Words that occur more than this number of times form an equivalence class by themselves.  Ignored unless you are using ambiguity classes.</b></td></tr>
 * <tr><td><b>debug</b></td><td><b>boolean</b></td><td><b>boolean</b></td><td><b>All</b></td><td><b>Whether to write debugging information (words, top words, unknown words).  Useful for error analysis.</b></td></tr>
 * <tr><td><b>debugPrefix</b></td><td><b>String</b></td><td><b>N/A</b></td><td><b>All</b></td><td><b>Prefix for where to write out the debugging information (relevant only if debug=true).</b></td></tr>
 * </table>
 * <p/>
 *
 * @author Kristina Toutanova
 * @author Miler Lee
 * @author Joseph Smarr
 * @author Anna Rafferty
 */
public class MaxentTagger implements Function<Sentence<? extends HasWord>,Sentence<TaggedWord>>, SentenceProcessor, ListProcessor<Sentence<? extends HasWord>,Sentence<TaggedWord>> {

  private static boolean isInitialized; // = false;

  public static final String DEFAULT_NLP_GROUP_MODEL_PATH = "/u/nlp/data/pos-tagger/wsj3t0-18-left3words/left3words-wsj-0-18.tagger";
  public static final String DEFAULT_DISTRIBUTION_PATH = "models/left3words-wsj-0-18.tagger";


  /**
   * Non-static version of the tagger for the Function interface.
   * A tagger is not initialized in the constructor, but will be
   * initialized the first time it is used from
   * <code>DEFAULT_NLP_GROUP_MODEL_PATH</code>.
   */
  public MaxentTagger() {
  }

  /**
   * Constructor from a fileName. The <code>modelFile</code> is both a
   * filename and a prefix that is used from which other filenames are
   * built and their data loaded.  The tagger data is loaded when the
   * constructor is called (this can be slow). Since some of the data
   * for the tagger is static, two different taggers cannot exist at
   * the same time.
   */
  public MaxentTagger(String modelFile) throws Exception {
    init(modelFile);
  }

  /**
   * Initializer that loads the dictionary.  This maintains a flag as
   * to whether initialization has been done previously, and if so,
   * running this is a no-op.
   *
   * @param modelFile Filename of the trained model, for example <code>
   *        /u/nlp/data/pos-tagger/wsj3t0-18-left3words/train-wsj-0-18.holder
   *        </code>
   */
  public static void init(String modelFile) throws Exception {
    init(modelFile, null);
  }

  /**
   * Initializer that loads the dictionary.  This maintains a flag as
   * to whether initialization has been done previously, and if so,
   * running this is a no-op.
   *
   * @param config TaggerConfig based on command-line arguments
   * @param modelFile Filename of the trained model, for example <code>
   *        /u/nlp/data/pos-tagger/wsj3t0-18-left3words/train-wsj-0-18.holder
   *        </code>
   * @throws Exception if IO problem
   */
  public static void init(String modelFile, TaggerConfig config) throws Exception {
    if ( ! isInitialized) {
      GlobalHolder.readModelAndInit(config, modelFile);
      // cdm: what does this next line do??  Yuck.
      CollectionTaggerOutputs.baseToken -= GlobalHolder.pairs.getSize();
      GlobalHolder.tFeature.init();
      isInitialized = true;
    }
  }


  /**
   * Tags the input string and returns the tagged version.
   * This method requires the input to already be tokenized.
   * The tagger wants input that is whitespace separated tokens, tokenized
   * according to the conventions of the training data. (For instance,
   * for the Penn Treebank, punctuation marks and possessive "'s" should
   * be separated from words.)
   *
   * @param toTag The untagged input String
   * @return The same string with tags inserted in the form word/tag
   * @throws Exception If there are IO errors or class initialization problems
   */
  public static synchronized String tagString(String toTag) throws Exception {

    if (!isInitialized) {
      init(DEFAULT_NLP_GROUP_MODEL_PATH);
    }

    if (isInitialized) {
      try {
        TestSentence ts = new TestSentence(GlobalHolder.getLambdaSolve(), toTag);
        return ts.getTaggedNice();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }


  /**
   * Tags the input string and returns the tagged version.
   * This method requires the input to already be tokenized.
   * The tagger wants input that is whitespace separated tokens, tokenized
   * according to the conventions of the training data. (For instance,
   * for the Penn Treebank, punctuation marks and possessive "'s" should
   * be separated from words.)
   *
   * @param toTag The untagged input String
   * @return The same string with tags inserted in the form word/tag
   * @throws Exception If there are IO errors or class initialization problems
   */
  public static synchronized Sentence<TaggedWord> tagStringTokenized(String toTag) throws Exception {
    if (!isInitialized) {
      init(DEFAULT_NLP_GROUP_MODEL_PATH);
    }

    if (isInitialized) {
      try {
        TestSentence ts = new TestSentence(GlobalHolder.getLambdaSolve(), toTag);

        return ts.getTaggedSentence();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * Expects a sentence and returns a tagged sentence
   *
   * @param in This needs to be a Sentence
   * @return A Sentence of TaggedWord
   */
  public synchronized Sentence<TaggedWord> apply(Sentence<? extends HasWord> in) {

    if (!isInitialized) {
      try {
        init(DEFAULT_NLP_GROUP_MODEL_PATH);
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(-1);
      }
    }

    if (isInitialized) {
      try {
        TestSentence ts = new TestSentence();
        return ts.tagSentence(GlobalHolder.getLambdaSolve(), in);
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(-1);
      }
    }
    return null;
  }

  /**
   * Tags the Words in each Sentence in the given List with their
   * grammatical part-of-speech. The returned List contains Sentences
   * consisting of TaggedWords.
   * <p><b>NOTE: </b>The input document must contain sentences as its elements,
   * not words. To turn a Document of words into a Document of sentences, run
   * it through {@link WordToSentenceProcessor}.
   *
   * @param sentences A List of Sentence
   * @return A List of Sentence of TaggedWord (final generification cannot be listed due to lack of complete generification of super classes)
   */
  public List<Sentence<TaggedWord>> process(List<Sentence<? extends HasWord>> sentences) {
    List<Sentence<TaggedWord>> taggedSentences = new ArrayList<Sentence<TaggedWord>>();
    TestSentence ts = new TestSentence();

    for (Sentence<? extends HasWord> sentence : sentences) {
      taggedSentences.add(ts.tagSentence(GlobalHolder.getLambdaSolve(), sentence));
    }
    return taggedSentences;
  }


  /**
   * Returns a new Sentence that is a copy of the given sentence with all the
   * words tagged with their part-of-speech. Convenience method when you only
   * want to tag a single Sentence instead of a Document of sentences.
   */
  @SuppressWarnings({"unchecked"})
  public Sentence<TaggedWord> processSentence(Sentence sentence) {
    return tagSentence(sentence);
  }


  /**
   * Returns a new Sentence that is a copy of the given sentence with all the
   * words tagged with their part-of-speech. Convenience method when you only
   * want to tag a single Sentence instead of a Document of sentences.
   */
  public static Sentence<TaggedWord> tagSentence(Sentence<? extends HasWord> sentence) {
    return new TestSentence().tagSentence(GlobalHolder.getLambdaSolve(), sentence);
  }


  /**
   * Reads data from r, tokenizes it with the default (Penn Treebank
   * tokenizer), and returns a List of Sentence objects, which can
   * then be fed into tagSentence.
   *
   * @param r Reader to read text from
   * @return A List of Sentences
   */
  public static List<Sentence<? extends HasWord>> tokenizeText(Reader r) {
    return tokenizeText(r, null);
  }

  /**
   * Reads data from r, tokenizes it with the given tokenizer, and
   * returns a List of Lists of (extends) HasWord objects, which can then be
   * fed into tagSentence.
   */
  @SuppressWarnings({"unchecked"})
  public static List<Sentence<? extends HasWord>> tokenizeText(Reader r, TokenizerFactory tokenizerFactory) {
    DocumentPreprocessor documentPreprocessor = (tokenizerFactory == null) ?
            new DocumentPreprocessor(): new DocumentPreprocessor(tokenizerFactory);
    List<List<? extends HasWord>> lis = documentPreprocessor.getSentencesFromText(r);
    List<Sentence<? extends HasWord>> out = new ArrayList<Sentence<? extends HasWord>>(lis.size());
    for (List<? extends HasWord> item : lis) {
      out.add(new Sentence(item));
    }
    return out;
  }

  /**
   * This method reads in a file in the old multi-file format and saves it to a single file
   * named newName.  The resulting model can then be used with the current architecture. A
   * model must be specified in config that corresponds to the model prefix of the existing
   * multi-file tagger. The new file will be saved to the path specified for the property
   * "convertToSingleFile".
   *
   * @param config The processed form of the command-line arguments.
   */
  private static void convertToSingleFileFormat(TaggerConfig config) {
    try {
      config.dump();
      GlobalHolder.convertMultifileTagger(config.getModel() + ".holder", config.getFile(), config);
    } catch (Exception e) {
      System.err.println("An error occurred while converting to the new tagger format.");
      e.printStackTrace();
    }

  }

  private static void dumpModel(TaggerConfig config) {
    try {
      GlobalHolder.readModelAndInit(config, config.getFile());
      GlobalHolder.dumpModel();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * Tests a tagger on data with gold tags available.  This is TEST mode.
   *
   * @param config Properties giving parameters for the testing run
   */
  private static void runTest(TaggerConfig config) {
    if (config.getVerbose()) {
      System.err.println("## tagger testing invoked at " + new Date() + " with arguments:");
      config.dump();
    }

    if (config.getDebug()) {
      TestClassifier.writeUnknDict = true;
      TestClassifier.writeWords = true;
      TestClassifier.writeTopWords = true;
    }
    try {
      TestClassifier.testModel(config);
    } catch(Exception e) {
      System.err.println("An error occured while testing the tagger.");
      e.printStackTrace();
    }
  }


  // todo: It's basically random during training what goes to System.out vs. System.err.
  /**
   * Trains a tagger model.
   *
   * @param config Properties giving parameters for the training run
   */
  private static void runTraining(TaggerConfig config) {
    Date now = new Date();

    System.err.println("## tagger training invoked at " + now + " with arguments:");
    config.dump();
    Timing tim = new Timing();
    try {
      PrintFile log = new PrintFile(config.getModel() + ".props");
      log.println("## tagger training invoked at " + now + " with arguments:");
      config.dump(log);
      log.close();

      TestClassifier.trainAndSaveModel(config);
      tim.done("Training POS tagger");
    } catch(Exception e) {
      System.err.println("An error occurred while training a new tagger.");
      e.printStackTrace();
    }
  }


  private static class TaggerWrapper implements Function<String, Object> {

    private final TaggerConfig config;
    private TokenizerFactory tokenizerFactory;
    private int sentNum = 0;
    public TaggerWrapper(TaggerConfig config) {
      this.config = config;
      try {
        if (config.getTokenizerFactory().trim().length() != 0) {

          tokenizerFactory = (TokenizerFactory) Class.forName(config.getTokenizerFactory()).newInstance();
        } else if(config.getTokenize()){
          tokenizerFactory = PTBTokenizerFactory.newPTBTokenizerFactory();
        }
      } catch (Exception e) {
        System.err.println("Error in tokenizer factory instantiation for class: " + config.getTokenizerFactory());
        e.printStackTrace();
        tokenizerFactory = PTBTokenizerFactory.newPTBTokenizerFactory();
      }
    }

    public String apply(String o) {
      StringBuilder taggedSentence = new StringBuilder();
      StringBuilder xmlWords = new StringBuilder();
      if (config.getTokenize()) {
        Reader r = new StringReader(o);
        List<Sentence<? extends HasWord>> l = tokenizeText(r, tokenizerFactory);

        for (Sentence<? extends HasWord> s : l) {
          Sentence<TaggedWord> taggedSentenceTok =  MaxentTagger.tagSentence(s);
          if (outStream != null) {
            writeXMLSentence(taggedSentenceTok, sentNum);
            xmlWords.append(getXMLWords(taggedSentenceTok, sentNum++)).append('\n');
            //return getXMLWords(taggedSentenceTok, sentNum++);
          }
          taggedSentence.append(taggedSentenceTok.toString(false)).append(' ');
        }
      } else {
        try {
          TestSentence ts = new TestSentence(GlobalHolder.getLambdaSolve(), o);
          if(outStream != null) {
            writeXMLSentence(ts.getTaggedSentence(), sentNum);
            xmlWords.append(getXMLWords(ts.getTaggedSentence(), sentNum++)).append('\n');
            //return getXMLWords(ts.getTaggedSentence(), sentNum++);
          }
          taggedSentence.append(ts.getTaggedNice()).append(' ');

        } catch (Exception e) {
          System.err.println("Error tagging string: " + o);
          e.printStackTrace();
        }
      }
      if (outStream != null) {
        return xmlWords.toString();
      } else {
        return taggedSentence.toString();
      }
    }

  } // end class TaggerWrapper


  /** for writing out xml */
  private static Writer outStream;

  private static void closeOutStream() {
    try {
      outStream.flush();
      outStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static String getXMLWords(Sentence<TaggedWord> s, int sentNum) {
    StringBuilder sb = new StringBuilder();
    sb.append("<sentence id=\"").append(sentNum).append("\">\n");
    for(int i = 0, sz = s.size(); i < sz; i++) {
      String word = s.get(i).word();
      String tag = s.get(i).tag();
      sb.append("\t<word wid=\"").append(i).append("\" pos=\"").append(tag).append("\">").append(word).append("</word>\n");
    }
    sb.append("</sentence>");
    return sb.toString();
  }

  /**
   * Takes a tagged sentence and writes out the xml version.
   *
   * @param s A tagged sentence
   * @param sentNum The sentence index for XML printout
   */
  private static void writeXMLSentence(Sentence<TaggedWord> s, int sentNum) {
    try {
      outStream.write(getXMLWords(s, sentNum));
    } catch(Exception e) {
      System.err.println("Error writing sentence " + sentNum + ": " + s.toString(false));
      e.printStackTrace();
    }

  }

  public static void tagFromXML(TaggerConfig config) {
    TransformXML txml = new TransformXML();
    try {
      InputStream is = new FileInputStream(config.getFile());
      txml.transformXML(config.getXMLInput(), new TaggerWrapper(config), is, System.out, new TaggerSaxInterface());
      is.close();
    } catch (FileNotFoundException e) {
      System.err.println("Input file not found: " + config.getFile());
      e.printStackTrace();
    } catch (IOException ioe) {
      System.err.println("tagFromXML: mysterious IO Exception");
      ioe.printStackTrace();
    }
    if (outStream != null) closeOutStream();
  }

  /**
   * Runs the tagger when we're in TAG mode.
   *
   * @param config The configuration parameters for the run.
   */
  @SuppressWarnings("unchecked")
  private static void runTagger(TaggerConfig config) {
    if (config.getVerbose()) {
      Date now = new Date();
      System.err.println("## tagger invoked at " + now + " with arguments:");
      config.dump();
    }
    try {
      Timing t = new Timing();
      System.err.print("Reading POS tagger from " + config.getModel() + " ... ");
      MaxentTagger.init(config.getModel(), config);
      t.done();
      t.start(); // reset clock

      String sentenceDelimiter = null;
      TokenizerFactory tokenizerFactory; // initialized immediately below
      if (config.getTokenize() && config.getTokenizerFactory().trim().length() != 0) {
        tokenizerFactory = (TokenizerFactory) Class.forName(config.getTokenizerFactory()).newInstance();
      } else if (config.getTokenize()){
        tokenizerFactory = PTBTokenizerFactory.newPTBTokenizerFactory();
      } else {
        tokenizerFactory = WhitespaceTokenizer.factory();
        sentenceDelimiter = "\n";
      }
      DocumentPreprocessor docProcessor = new DocumentPreprocessor(tokenizerFactory);
      docProcessor.setEncoding(config.getEncoding());

      //Counts
      int numWords = 0;
      int numSentences = 0;
      BufferedWriter systemOut = new BufferedWriter(new OutputStreamWriter(System.out,config.getEncoding()));

      //Check if we need to set up an xml output stream
      if (config.getXMLOutput().length() > 0) {
        try {
          outStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config.getXMLOutput()),config.getEncoding()));
        } catch (Exception e) {
          System.err.println("Error opening an output to " + config.getXMLOutput() + " for XML.");
          e.printStackTrace();
        }
      }

      if (config.getXMLInput().length > 0) {
        tagFromXML(config);
        return;
      }
      boolean stdin = config.getFile().trim().equalsIgnoreCase("stdin");

      while (true) {
        //Now determine if we're tagging from stdin or from a file
        BufferedReader br;
        if (!stdin) {
          br = IOUtils.readReaderFromString(config.getFile(), config.getEncoding());
        } else {
          System.err.println("Type some text to tag, then EOF.");
          System.err.println("  (For EOF, use Return, Ctrl-D on Unix; Enter, Ctrl-Z, Enter on Windows.)");
          br = new BufferedReader(new InputStreamReader(System.in));
        }

        if (config.getSGML()) {
          PlainTextDocumentReaderAndWriter readerAndWriter = new PlainTextDocumentReaderAndWriter();
          ObjectBank<List<CoreLabel>> ob = new ObjectBank<List<CoreLabel>>(new ReaderIteratorFactory(br), readerAndWriter);
          for (List<CoreLabel> sentence : ob) {
            Sentence<CoreLabel> s = new Sentence<CoreLabel>(sentence);
            numWords += s.length();
            Sentence<TaggedWord> taggedSentence = MaxentTagger.tagSentence(s);
            Iterator<CoreLabel> origIter = sentence.iterator();
            for (TaggedWord tw : taggedSentence) {
              CoreLabel cl = origIter.next();
              cl.set(CoreAnnotations.AnswerAnnotation.class, tw.tag());
            }
            systemOut.write(readerAndWriter.getAnswers(sentence));
          }
        } else {
          //Now we do everything through the doc preprocessor
          List<List<? extends HasWord>> document;
          if ((config.getTagInside() != null && !config.getTagInside().equals(""))) {
            document = docProcessor.getSentencesFromXML(br, config.getTagInside(), null, false);
          } else if (stdin) {
            document = docProcessor.getSentencesFromText(new StringReader(br.readLine()));
          } else {
            document = docProcessor.getSentencesFromText(br, sentenceDelimiter);
          }

          for(List<? extends HasWord> sentence : document) {
            numWords += sentence.size();
            Sentence<? extends HasWord> s = new Sentence(sentence);
            Sentence<TaggedWord> taggedSentence = MaxentTagger.tagSentence(s);

            systemOut.write(taggedSentence.toString(false));
            systemOut.write("\n");
            if (stdin) {
              systemOut.newLine();
              systemOut.flush();
            }
            if (outStream != null) {
              writeXMLSentence(taggedSentence, numSentences);
            }
            numSentences++;
          }
        }
        if (!stdin) break;
      }
      systemOut.close();
      long millis = t.stop();
      TestClassifier.printErrWordsPerSec(millis, numWords);
    } catch (Exception e) {
      System.err.println("An error occurred while tagging.");
      e.printStackTrace();
    }
    if (outStream != null) {
      closeOutStream();
    }
  }


  /**
   * Command-line tagger that takes input from stdin or a file.
   * See class documentation for usage.
   *
   * @param args Command-line arguments
   * @throws IOException If any file problems
   */
  public static void main(String[] args) throws IOException {
    TaggerConfig config = new TaggerConfig(args);

    if (config.getMode() == TaggerConfig.Mode.TRAIN) {
      runTraining(config);
    } else if (config.getMode() == TaggerConfig.Mode.TAG) {
      runTagger(config);
    } else if (config.getMode() == TaggerConfig.Mode.TEST) {
      runTest(config);
    } else if (config.getMode() == TaggerConfig.Mode.CONVERT) {
      convertToSingleFileFormat(config);
    } else if (config.getMode() == TaggerConfig.Mode.DUMP) {
      dumpModel(config);
    } else {
      System.err.println("Impossible: nothing to do. None of train, tag, test, or convert was specified.");
    }
  } // end main()

}
