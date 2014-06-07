package demo.sentence;
import com.aliasi.util.Files;

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;

import com.aliasi.sentences.SentenceModel;
import com.aliasi.sentences.IndoEuropeanSentenceModel;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class SentenceOffsets {

    public static void main(String[] args) throws IOException {
	TokenizerFactory tf = IndoEuropeanTokenizerFactory.INSTANCE;
	SentenceModel sm = new IndoEuropeanSentenceModel();

	File file = new File(args[0]);
	char[] cs = Files.readCharsFromFile(file,"ISO-8859-1");

	System.out.println("\nTEXT");
	System.out.println(new String(cs,0,cs.length));

	List<String> tokenList = new ArrayList<String>();
	List<String> whiteList = new ArrayList<String>();

	Tokenizer tokenizer = tf.tokenizer(cs,0,cs.length);

	tokenizer.tokenize(tokenList,whiteList);
	String[] tokens = new String[tokenList.size()];
	String[] whites = new String[whiteList.size()];
	tokenList.toArray(tokens);
	whiteList.toArray(whites);

	System.out.println("\n" + tokens.length + " TOKENS");
	System.out.println(tokenList);
	System.out.println("\n" + whites.length + " WHITESPACES");
	System.out.println(whiteList);
	
	if (tokens.length == 0) {
	    System.out.println("No tokens found in input.");
	    return;
	}

	int[] tokenStarts = new int[tokens.length];
	int[] tokenEnds = new int[tokens.length];
	
	int pos = whites[0].length();
	int nextStart = whites[0].length();
	for (int i = 0; i < tokens.length; ++i) {
	    tokenStarts[i] = pos;
	    pos += tokens[i].length();
	    tokenEnds[i] = pos;
	    pos += whites[i+1].length();
	    nextStart = pos;
	}

	System.out.println("\nTOKEN CHAR OFFSETS (start, end+1)");
	for (int i = 0; i < tokens.length; ++i) {
	    System.out.println(tokens[i] + "(" + tokenStarts[i]
			       + "," + tokenEnds[i] + ")");
	}

	int[] sentenceBoundaries = sm.boundaryIndices(tokens,whites);

	System.out.println("\n" + sentenceBoundaries.length 
			   + " SENTENCE END TOKEN OFFSETS");
	for (int i = 0; i < sentenceBoundaries.length; ++i) {
	    if (i > 0) System.out.print(", ");
	    System.out.print(sentenceBoundaries[i]);
	}
	System.out.println();

	if (sentenceBoundaries.length < 1) {
	    System.out.println("No sentence boundaries found.");
	    return;
	}

	int[] sentenceStarts = new int[sentenceBoundaries.length];
	int[] sentenceEnds = new int[sentenceBoundaries.length];
	int nextSentStart = tokenStarts[0];
	for (int i = 0; i < sentenceBoundaries.length; ++i) {
	    sentenceStarts[i] = nextSentStart;
	    int tokIdx = sentenceBoundaries[i];
	    sentenceEnds[i] = tokenEnds[tokIdx];
	    nextSentStart = sentenceEnds[i] + whites[tokIdx+1].length();
	}

	System.out.println("\nSENTENCE CHAR OFFSETS");
	for (int i = 0; i < sentenceStarts.length; ++i) {
	    int start = sentenceStarts[i];
	    int end = sentenceEnds[i];
	    String text = new String(cs,start,end-start);
	    System.out.println(i + "(" + start + "," + end + ")=/"
			       + text + "/");
	}
    }

}
