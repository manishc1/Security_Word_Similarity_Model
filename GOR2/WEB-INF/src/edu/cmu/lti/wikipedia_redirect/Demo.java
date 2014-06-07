package edu.cmu.lti.wikipedia_redirect;
import java.io.File;
import java.util.Set;

/**
 * Showcase for what you can do with Wikipedia Redirect.
 * 
 * @author Hideki Shima
 */
public class Demo {
  
  public static void main(String[] args) throws Exception {
    // Initialization
    System.out.print("Deserializing Wikipedia Redirect ...");
    long t0 = System.nanoTime();
    WikipediaRedirect wr = IOUtil.loadWikipediaRedirect(new File(args[0]));
    long t1 = System.nanoTime();
    System.out.println(" done in "+(double)(t1-t0)/(double)1000000000+" sec.\n");
    
    // Let's find a redirection given a source word.
    String[] srcTerms = {"US", "U.S.", "USA", "MIT", "IBM", "UMBC" };
    StringBuilder sb = new StringBuilder();
    for ( String src : srcTerms ) {
      sb.append("\""+wr.get(src)+"\" was redirected from \""+src+"\"\n");
    }
    System.out.println(sb.toString()+"--\n");

    // Let's find which source words redirect to the given target word.
    String target = "United States";
    Set<String> keys = wr.getKeysByValue(target);
    System.out.println("All of the following redirect to \""+target+"\":\n"+keys);
  }
  
}
