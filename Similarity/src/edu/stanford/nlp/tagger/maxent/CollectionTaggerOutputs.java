package edu.stanford.nlp.tagger.maxent;

/**
 * This class will just hold an array of the outputs of all available taggers.
 */
public class CollectionTaggerOutputs {

  int numTaggers;
  static int baseToken = 0;
  private TaggerOutputHolder[] taggers;

  public CollectionTaggerOutputs(int num) {
    this.numTaggers = num;
    taggers = new TaggerOutputHolder[numTaggers];
  }

  public final void readOutput(int nO, String filename, boolean probs) {
    taggers[nO] = new TaggerOutputHolder(filename, probs);
  }


  public String getTag(int numTagger, int numToken) {
    return (taggers[numTagger].getTag(numToken + baseToken));
  }


  /**
   * These will be like commnad-line arguments. Format
   * numTaggers file_0 f|t file_1 f|t file_2 f|t ... file_n f|t
   */
  public CollectionTaggerOutputs(String[] args) {
    int num = Integer.parseInt(args[0]);
    if (args.length != (2 * num + 1)) {
      System.out.println("Error, wrong number of arguments.");
      System.exit(1);
    }
    this.numTaggers = num;
    taggers = new TaggerOutputHolder[numTaggers];
    for (int i = 0; i < num; i++) {
      String filename = args[i * 2 + 1];
      boolean prb = args[i * 2 + 2].equals("t");
      readOutput(i, filename, prb);
    }//for

  }

}
