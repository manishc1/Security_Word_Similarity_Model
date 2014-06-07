package edu.stanford.nlp.tagger.maxent;

import java.util.*;
import java.io.*;

import edu.stanford.nlp.io.RuntimeIOException;


public class ASBCDict {

  private static final String defaultFilename = "adbcdict";
  private static String pathPrefix = "/u/nlp/data/pos-tagger/dictionary";

  public static synchronized void ensureLoaded()  {
     if (ASBC_pre_dict == null) {
       readASBCDict(pathPrefix + '/' + defaultFilename);
     }
  }


  private ASBCDict() {}


  private static Map<String, Set<String>> ASBC_pre_dict;
  private static Map<String, Set<String>> ASBC_suf_dict;


  private static void readASBCDict(String filename) {
    BufferedReader ASBCDetectorReader = null;
    try {
      ASBC_pre_dict = new HashMap<String, Set <String>>();
      ASBC_suf_dict = new HashMap<String, Set <String>>();

      ASBCDetectorReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "GB18030"));
      for (String ASBCDetectorLine; (ASBCDetectorLine = ASBCDetectorReader.readLine()) != null; ) {
        String[] fields = ASBCDetectorLine.split("	");
        String tag=fields[0];
        Set<String> pres = ASBC_pre_dict.get(tag);
        Set<String> sufs = ASBC_suf_dict.get(tag);
        //System.err.println(fields[0]+fields[1]+fields[2]);
        if (pres==null) {
          pres = new HashSet<String>();
          ASBC_pre_dict.put(tag,pres);
        }
        pres.add(fields[1]);

        if (sufs==null) {
          sufs = new HashSet<String>();
          ASBC_suf_dict.put(tag,sufs);
        }
        sufs.add(fields[2]);
      }
      ASBCDetectorReader.close();
    } catch (FileNotFoundException e) {
      throw new RuntimeIOException("ASBC dictionary not found", e);
    } catch (IOException e) {
      throw new RuntimeIOException("ASBC dictionary problem", e);
    } finally {
      if (ASBCDetectorReader != null) {
        try {
          ASBCDetectorReader.close();
        } catch (IOException ioe) {
          // do nothin'
        }
      }
    }
  }


  public static String getTagPre(String a1, String a2) {
    ensureLoaded();
    if (getpre(a1)== null) {
      return "0";
    }
    if (getpre(a1).contains(a2)) {
      return "1";
    }
    return "0";
  }



  public static String getTagSuf(String a1, String a2) {
    ensureLoaded();
    if (getsuf(a1)== null) {
      return "0";
    }
    if (getsuf(a1).contains(a2)) {
      return "1";
    }
    return "0";
  }


  private static Set<String> getpre(String a) {
    return ASBC_pre_dict.get(a);
  }

  private static Set<String> getsuf(String a){
    return ASBC_suf_dict.get(a);
  }


  public static String getPathPrefix() {
    return pathPrefix;
  }


  public static void setPathPrefix(String pathPrefix) {
    ASBCDict.pathPrefix = pathPrefix;
  }

} //class
