package edu.stanford.nlp.tagger.maxent;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.stanford.nlp.io.RuntimeIOException;



public class CTBunkDict {

  private final static String defaultFilename = "ctb_amb";
  private static String pathPrefix;

  private static CTBunkDict CTBunkDictSingleton = null;

  private static HashMap<String, Set<String>> CTBunk_dict;


  public static CTBunkDict getInstance() {

    if (CTBunkDictSingleton == null) {
      CTBunkDictSingleton = new CTBunkDict(0);
    }
    return CTBunkDictSingleton;
  }


  private CTBunkDict(int i) {
    readCTBunkDict("/u/nlp/data/pos-tagger/dictionary" + "/" + defaultFilename);
  }


  private static void readCTBunkDict(String filename)   {
    CTBunk_dict = new HashMap<String, Set <String>>();

    try{

      BufferedReader CTBunkDetectorReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "GB18030"));
      for (String CTBunkDetectorLine; (CTBunkDetectorLine = CTBunkDetectorReader.readLine()) != null; ) {
        String[] fields = CTBunkDetectorLine.split(" ");
        String tag=fields[1];
        Set<String> words=CTBunk_dict.get(tag);

        if(words==null){
          words = new HashSet<String>();
          CTBunk_dict.put(tag,words);
        }
        words.add(fields[0]);

      }

    } catch (FileNotFoundException e) {
      throw new RuntimeIOException("CTBunk file not found: " + filename, e);
    } catch (IOException e) {
      throw new RuntimeIOException("CTBunk I/O error: " + filename, e);
    }

    return;
  }



  /**
   * Returns "1" as true if the dictionary listed this word with this tag,
   *  and "0" otherwise.
   *
   * @param tag  The POS tag
   * @param word The word
   * @return "1" as true if the dictionary listed this word with this tag,
   *  and "0" otherwise.
   */
  public static String getTag(String tag, String word) {
    CTBunkDict dict = CTBunkDict.getInstance();
    Set<String> words = dict.get(tag);
    if (words != null && words.contains(word)) {
      return "1";
    } else {
      return "0";
    }
  }


  private Set<String> get(String a) {
    return CTBunk_dict.get(a);
  }

  public static String getPathPrefix() {
    return pathPrefix;
  }

  public static void setPathPrefix(String pathPrefix) {
    CTBunkDict.pathPrefix = pathPrefix;
  }


} // end class CTBunkDict
