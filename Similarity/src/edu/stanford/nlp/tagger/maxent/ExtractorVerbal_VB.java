/**
 * Title:        StanfordMaxEnt<p>
 * Description:  A Maximum Entropy Toolkit<p>
 * Copyright:    Copyright (c) Kristina Toutanova<p>
 * Company:      Stanford University<p>
 */

package edu.stanford.nlp.tagger.maxent;


/**
 * Look for features selecting a VB verb.
 * Last 'verbal' thing was MD, TO-infinitive, or word do|does|did &
 * tag = VBP Should be very low
 *
 * @author Kristina Toutanova
 * @version 1.0
 */
class ExtractorVerbal_VB extends Extractor {

  private static final String mdTag = "MD";
  private static final String vbTag = "VB";
  private static final String vbpTag = "VBP";
  private static final String naWord = "NA";
  private static final String andWord = "and";
  // private static HashMap v = new HashMap(); // if smth is a VB, find a trigger for it and place it here
  // private static String bar = "|";
  // private static int unRec = 0;
  // private static int total = 0;
  // private static int notUsual = 0;
  private static final String vb = "VB";
  private static final String to = "TO";
  private static final String oneSt = "1";
  private static final String zeroSt = "0";
  private static final boolean DBG = false;
  private final int bound;

  public ExtractorVerbal_VB() {
    this(8);
  }

  public ExtractorVerbal_VB(int bound) {
    this.bound = bound;
  }


  @Override
  public boolean precondition(String tag) {
    return (tag.equals(vbTag) || tag.equals(vbpTag));
  }


  @Override
  String extract(History h, PairsHolder pH) {
    // should extract last verbal word and also the current word
    String cword = ExtractorFrames.cWord.extract(h, pH);
    int start = h.start;
    int allCount = GlobalHolder.dict.sum(cword);
    int vBCount = GlobalHolder.dict.getCount(cword, vbTag);
    int vBPCount = GlobalHolder.dict.getCount(cword, vbpTag);
    if (false) {
      System.out.println("## VB: allCount=" + allCount + " vBCount=" + vBCount + " vBPCount=" + vBPCount + " return0=" + ((allCount > 0) && (vBCount + vBPCount <= allCount / 100)));
    }
    // only words that are unknown or known to be verb >= 1% of the time
    if ((allCount > 0) && (vBCount + vBPCount <= allCount / 100)) {
      return "0";
    }
    String lastverb = naWord;
    String lastvtag = "";
    int current = h.current;
    int index = current - 1;
    while ((index >= start) && (index >= current - bound)) {
      String tag = pH.get(index, true);
      if ((tag.equals(mdTag)) || (tag.startsWith(vb)) || (tag.startsWith(to))) {
        lastverb = pH.get(index, false);
        lastvtag = tag;
        break;
      }
      if (tag.equals("CC")) {
        break;
      }
      index--;
    }

    if (DBG) {
      System.out.println("## VB: last verb is " + lastverb + " last tag is " + lastvtag);
    }
    if (isForVB(h, pH, lastverb, lastvtag)) {
      if (DBG) {
        System.out.println("## VB: fired");
      }
      return oneSt;
    }
    return zeroSt;
  }


  private static boolean isForVB(History h, PairsHolder pH, String verb, String tag) {
    int index = 0;
    // maybe add for conjunctions stuff , if the previous is and
    String verbnice = TestSentence.toNice(verb).toLowerCase();
    //System.out.println(verb);
    if (tag.equals("MD")) {
      return true;
    }
    if (tag.equals("TO")) {
      return true;
    }
    if (verbnice.equals("do")) {
      return true;
    }
    if (verbnice.equals("did")) {
      return true;
    }
    if (verbnice.equals("does")) {
      return true;
    }
    if (verbnice.startsWith("help")) {
      return true;
    }
    if (verbnice.startsWith("let")) {
      return true;
    }
    if (verbnice.startsWith("make")) {
      return true;
    }
    if (verbnice.equals("made")) {
      return true;
    }
    if (verbnice.equals("making")) {
      return true;
    }
    // could also add see, watch, notice, hear, listen, feel, but rare?
    // if (verb.equals(thatWord)) return true;
    // Beginning of sentence verb is (sometimes) VB -- imperative
    String prevWord = ExtractorFrames.prevWord.extract(h, pH);
    if (prevWord.equals(naWord) || prevWord.startsWith("``") || prevWord.startsWith("-LRB-") || prevWord.startsWith("--")) {
      return true;
    }
    if (prevWord.equals(andWord) && tag.equals(vbTag)) {
      return true;
    }
    return false;
  }

  private static final long serialVersionUID = 3441425711549364480L;

  /*----
    Below here I put apparent detritus (CDM, Dec 2002)

  public void print(){
    System.out.println("total "+total+" unrec "+unRec+" not usual "+notUsual);
    Object[] arr=v.keySet().toArray();
    String key;
    for(int i=0;i<arr.length;i++){
      key=(String)arr[i];
      System.out.println(key+" "+((Counter)v.get(key)).cnt);
    }
  }


  public void getTrigger(History h, String tag1) {
   if (!tag1.equals(vbTag)) return;
   String lastvtag="";
   String lastverb=naWord;
    int current=h.current;
    int start=h.start;
    int index=current-1;
    while((index>=start)&&(index>=current-15)){
     String tag=GlobalHolder.pairs.get(index,true);
     String word=GlobalHolder.pairs.get(index,false);
     if((tag.equals(mdTag))||(tag.startsWith("VB"))||(tag.startsWith("TO"))){
      lastverb=GlobalHolder.pairs.get(index,false);
      lastvtag=tag;
      break;
    }
    index--;
  }

  if(isForVB(h,lastverb,lastvtag)){
    total++;
    h.printSent();
    System.out.println(lastverb+bar+lastvtag);
    return;
  }
   //h.printSent();
  notUsual++;
  if(lastvtag.length()==0) {
    //System.out.println(" unrecognized cause ");
    unRec++;
    total++;
    return;
    }
   else{
   //System.out.println(lastverb+bar+lastvtag);
   total++;
   if(v.containsKey(lastverb+bar+lastvtag))
    ((Counter)(v.get(lastverb+bar+lastvtag))).inc();
   else
     v.put(lastverb+bar+lastvtag,new Counter());
    } // else

  }



  static class Counter{
  int cnt;
  public Counter(){
  cnt=1;
  }

  public void inc(){
  cnt++;
  }

  }

  ---- */

}
