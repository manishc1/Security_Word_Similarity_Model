package edu.stanford.nlp.tagger.maxent;

import java.util.*;
import java.io.*;



public class CtbDict {

  private static final String defaultFilename = "ctb_dict.txt";
  private static String pathPrefix;

  private static CtbDict ctbDictSingleton; // = null;


  public static synchronized CtbDict getInstance() {
    if (ctbDictSingleton == null) {
      ctbDictSingleton = new CtbDict();
    }
    return ctbDictSingleton;
  }


  private CtbDict() {
    try {
      readCtbDict("/u/nlp/data/pos-tagger/dictionary" + '/' + defaultFilename);
    } catch(IOException e) {
      throw new RuntimeException("can't open file: " + e.getMessage());
      /* java sucks */
    }
  }


  public HashMap <String, Set <String>> ctb_pre_dict;
  public HashMap <String, Set <String>> ctb_suf_dict;


  private void readCtbDict(String filename) throws IOException {
    BufferedReader ctbDetectorReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "GB18030"));
    String ctbDetectorLine;

    ctb_pre_dict = new HashMap<String, Set <String>>();
    ctb_suf_dict = new HashMap<String, Set <String>>();

    while ((ctbDetectorLine = ctbDetectorReader.readLine()) != null) {
      String[] fields = ctbDetectorLine.split("	");
      String tag=fields[0];
      Set<String> pres=ctb_pre_dict.get(tag);
      Set<String> sufs=ctb_suf_dict.get(tag);

      if(pres==null){
        pres = new HashSet<String>();
        ctb_pre_dict.put(tag,pres);
      }
      pres.add(fields[1]);

      if(sufs==null){
        sufs = new HashSet<String>();
        ctb_suf_dict.put(tag,sufs);
      }
      sufs.add(fields[2]);


    }
  }//try


  public static String getTagPre(String a1, String a2) {
    CtbDict dict = CtbDict.getInstance();
    if (dict.getpre(a1)== null)	return "0";
    if (dict.getpre(a1).contains(a2))
      return "1";
    return "0";
  }



  public static String getTagSuf(String a1, String a2) {
    CtbDict dict = CtbDict.getInstance();
    if (dict.getsuf(a1)== null)	return "0";
    if (dict.getsuf(a1).contains(a2))
      return "1";
    return "0";
  }


  Set getpre(String a){
    return ctb_pre_dict.get(a);
  }

  Set getsuf(String a){
    return ctb_suf_dict.get(a);
  }


  public static String getPathPrefix() {
    return pathPrefix;
  }


  public static void setPathPrefix(String pathPrefix) {
    CtbDict.pathPrefix = pathPrefix;
  }




}//class
