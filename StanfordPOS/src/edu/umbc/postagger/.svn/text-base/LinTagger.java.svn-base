package edu.umbc.postagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.aliasi.hmm.HiddenMarkovModel;
import com.aliasi.hmm.HmmDecoder;
import com.aliasi.sentences.IndoEuropeanSentenceModel;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.tag.Tagging;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.FastCache;
import com.aliasi.util.Streams;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.umbc.wordSimilarity.GutenbergParagraphGenerator;

public class LinTagger {

    private TokenizerFactory TOKENIZER_FACTORY;
    private SentenceModel SENTENCE_MODEL;
    private HmmDecoder decoder;
    private String directoryName;
	public long totalWords = 0;
	public long totalArticles = 0;

	
	public LinTagger(String directory) throws IOException, ClassNotFoundException {
		// TODO Auto-generated constructor stub
		TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
		SENTENCE_MODEL = new IndoEuropeanSentenceModel();
    	String modelLocation = "/home/lushan1/nlp/model/lingpipe/pos-en-general-brown.HiddenMarkovModel";
        System.out.println("Reading model from file=" + modelLocation);
        FileInputStream fileIn = new FileInputStream(modelLocation);
        ObjectInputStream objIn = new ObjectInputStream(fileIn);
        HiddenMarkovModel hmm = (HiddenMarkovModel) objIn.readObject();
        Streams.closeInputStream(objIn);
        FastCache<String, double[]> emissionCache = new FastCache<String, double[]>(200000);
        FastCache<String, double[]> emissionLog2Cache = new FastCache<String, double[]>(200000);
        decoder = new HmmDecoder(hmm, emissionCache, emissionLog2Cache);
        directoryName = directory;
	}

	public LinTagger() throws IOException, ClassNotFoundException {
		// TODO Auto-generated constructor stub
		TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
		SENTENCE_MODEL = new IndoEuropeanSentenceModel();
    	String modelLocation = "/home/lushan1/nlp/model/lingpipe/pos-en-general-brown.HiddenMarkovModel";
        System.out.println("Reading model from file=" + modelLocation);
        FileInputStream fileIn = new FileInputStream(modelLocation);
        ObjectInputStream objIn = new ObjectInputStream(fileIn);
        HiddenMarkovModel hmm = (HiddenMarkovModel) objIn.readObject();
        Streams.closeInputStream(objIn);
        FastCache<String, double[]> emissionCache = new FastCache<String, double[]>(200000);
        FastCache<String, double[]> emissionLog2Cache = new FastCache<String, double[]>(200000);
        decoder = new HmmDecoder(hmm, emissionCache, emissionLog2Cache);
        directoryName = null;
	}

	public String tagging(String paragraph){
		
		StringBuffer taggedParagraph = new StringBuffer();
		List<String> tokenList = new ArrayList<String>();
		List<String> whiteList = new ArrayList<String>();
		Tokenizer sentence_tokenizer = TOKENIZER_FACTORY.tokenizer(paragraph.toCharArray(),0,paragraph.length());
		sentence_tokenizer.tokenize(tokenList,whiteList);

		String[] tokens = new String[tokenList.size()];
		String[] whites = new String[whiteList.size()];
		tokenList.toArray(tokens);
		whiteList.toArray(whites);
		int[] sentenceBoundaries = SENTENCE_MODEL.boundaryIndices(tokens,whites);

		if (sentenceBoundaries.length < 1) {
		    return "";
		}
		
		int sentStartTok = 0;
		int sentEndTok = 0;
		for (int i = 0; i < sentenceBoundaries.length; ++i) {

			sentEndTok = sentenceBoundaries[i];
		    StringBuffer sentence = new StringBuffer();
			
		    for (int j=sentStartTok; j<=sentEndTok; j++) {
		    	//System.out.print(tokens[j]+whites[j+1]);
		    	sentence.append(tokens[j] + whites[j+1]); 
		    }

		    // <-- Start tokenizing the sentence;
			Reader r = new StringReader(sentence.toString());
			PTBTokenizer<CoreLabel> ptb_tokenizer = new PTBTokenizer<CoreLabel>(r, new CoreLabelTokenFactory(), "");
			Vector<String> ptb_tokens = new Vector<String>();
			
		    while (ptb_tokenizer.hasNext()) {
		        CoreLabel obj = ptb_tokenizer.next();
		        String str = obj.word();
		        
		        if (str.equals("-")) str = "--";
		        if (str.equals("`")) str = "'";
		        
		        if (ptb_tokens.size() > 0 && (str.equals("n't") || str.equals("N'T") || str.equals("'s")))
		        	ptb_tokens.set(ptb_tokens.size() - 1, ptb_tokens.lastElement() + str);
		        else
		        	ptb_tokens.add(str);
		        
		        totalWords++;
		    }
		    // --> Done with tokenizing the sentence;
			
		    
		    // <-- Start tagging the sentence
		    Tagging<String> tagging = decoder.tag(ptb_tokens);
		    
	        for (int k = 0; k < tagging.size(); ++k)
	        	taggedParagraph.append(tagging.token(k) + "_" + tagging.tag(k) + " ");
	        // --> Done with tagging the sentence
	        
		    sentStartTok = sentEndTok+1;
		}
		
		// process the last sentence if it is not properly ended with terminators.
		/*
		if (sentEndTok < tokens.length - 1){
			
			sentEndTok = tokens.length - 1;
		    StringBuffer sentence = new StringBuffer();
			
		    for (int j=sentStartTok; j<=sentEndTok; j++) {
		    	//System.out.print(tokens[j]+whites[j+1]);
		    	sentence.append(tokens[j] + whites[j+1]); 
		    }

		    // <-- Start tokenizing the sentence;
			Reader r = new StringReader(sentence.toString());
			PTBTokenizer<CoreLabel> ptb_tokenizer = new PTBTokenizer<CoreLabel>(r, new CoreLabelTokenFactory(), "");
			Vector<String> ptb_tokens = new Vector<String>();
			
		    while (ptb_tokenizer.hasNext()) {
		        CoreLabel obj = ptb_tokenizer.next();
		        String str = obj.word();
		        
		        if (str.equals("-")) str = "--";
		        if (str.equals("`")) str = "'";
		        
		        if (ptb_tokens.size() > 0 && (str.equals("n't") || str.equals("N'T") || str.equals("'s")))
		        	ptb_tokens.set(ptb_tokens.size() - 1, ptb_tokens.lastElement() + str);
		        else
		        	ptb_tokens.add(str);
		        
		        totalWords++;
		    }
		    // --> Done with tokenizing the sentence;
			
		    
		    // <-- Start tagging the sentence
		    Tagging<String> tagging = decoder.tag(ptb_tokens);
		    
	        for (int k = 0; k < tagging.size(); ++k)
	        	taggedParagraph.append(tagging.token(k) + "_" + tagging.tag(k) + " ");
	        // --> Done with tagging the sentence
	        
		    sentStartTok = sentEndTok+1;			
			
		}
		*/
		

		return taggedParagraph.toString();
		
	}
	
	public void close(){
		
	}
	
	
	public void run(){
		
		try{
			File directory = new File(directoryName);
			
			long startTime = System.currentTimeMillis();

			tagDocs(directory);
			
			/*
		    FilenameFilter filter = new FilenameFilter() {
		        public boolean accept(File dir, String name) {
		            return name.endsWith(".txt");
		        }
		    };


			File[] files = directory.listFiles(filter);

			for (File textfile: files){
				startScanAndCount(textfile);
				totalArticles ++;
			}
			
			*/
			long endTime = System.currentTimeMillis();
			long elipsedTime = endTime - startTime;
			
			System.out.println(totalArticles + " articles have been processed.");
			System.out.println(totalWords + " words have been processed.");
			System.out.println(elipsedTime + " milli seconds have been taken to process the corpus.");
			
			System.out.println("Congratulation! Work Completed!");
			
			
			
		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
			System.exit(-1);
		}
	}
	
	
	public void tagDocs(File file) {
	    // do not try to scan files that cannot be read
	    if (file.canRead()) {
	      if (file.isDirectory()) {
	        String[] files = file.list();
	        // an IO error could occur
	        if (files != null) {
	          for (int i = 0; i < files.length; i++) {
	        	  tagDocs(new File(file, files[i]));
	          }
	        }
	      } else {
	    	  if (file.getName().endsWith(".txt") && !file.getName().contains("-")){
	    	  //if (file.getName().endsWith(".txt")){

	    		  System.out.println("tagging " + file);
				  try {
						startTaggingAndCounting(file);
						//totalArticles ++;
						
						if (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() < 50000000){
							System.out.println("gc starts to run ...");
							System.out.println("Max memory: " + Runtime.getRuntime().maxMemory());
							System.out.println("Total memory: " + Runtime.getRuntime().totalMemory());
							System.out.println("Free memory: " + Runtime.getRuntime().freeMemory());
							System.gc();
							System.out.println("gc complete running.");
						}
						
						//System.out.println("maxMemory: " + Runtime.getRuntime().maxMemory());
						//System.out.println("freeMemory: " + Runtime.getRuntime().freeMemory());
						//System.out.println("totalMemory: " + Runtime.getRuntime().totalMemory());
			          
			        // at least on windows, some temporary files raise this exception with an "access denied" message
			        // checking if the file can be read doesn't help
			      } catch (IOException e) {
			    	  System.out.println("File " + file.toString() + " failed!");
			    	  e.printStackTrace();
			      }
	    	  }
	      }
	    }
	}
	
	
	public int startTaggingAndCounting(File textfile) throws IOException{
		
		GutenbergParagraphGenerator paragraphs = new GutenbergParagraphGenerator(textfile, "Gutenberg");
		String paragraph;
		//int parNo = 0;
		
		
		//if ((model.modelName.contains("Gutenberg2006Books")) && !paragraphs.IsEnglish()) {
		if ((paragraphs.modelName.contains("Gutenberg")) && !paragraphs.IsEnglish()) {
			paragraphs.close();
			return -1;
		}
		
		/* contained in IsEnglish()
		if (!paragraphs.removeCopyright()){
			paragraphs.close();
			return -1;
		}
		*/
		String path = textfile.getPath();
		String POSTaggerFileName = path.substring(0, path.length() - 4) + ".poslp";
		
		FileWriter POSTaggerFile = new FileWriter(POSTaggerFileName);
		PrintWriter outOfTaggerFile = new PrintWriter(new BufferedWriter(POSTaggerFile));


		while ((paragraph = paragraphs.getNextParagraph(path)) != null){
			
			if (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() < 50000000){
				System.out.println("gc starts to run ...");
				System.out.println("Max memory: " + Runtime.getRuntime().maxMemory());
				System.out.println("Total memory: " + Runtime.getRuntime().totalMemory());
				System.out.println("Free memory: " + Runtime.getRuntime().freeMemory());
				System.gc();
				System.out.println("gc complete running.");
			}
			
			if (paragraph.length() == 0)
				continue;

			String taggedParagraph = tagging(paragraph);
			
			outOfTaggerFile.println(taggedParagraph);
			outOfTaggerFile.println();
		}
		
		paragraphs.close();
		outOfTaggerFile.close();
		totalArticles ++;
		return 0;
	}
	
	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		
		LinTagger test = new LinTagger();
        InputStreamReader isReader = new InputStreamReader(System.in);
        BufferedReader bufReader = new BufferedReader(isReader);
        
        while (true) {
            System.out.print("\n\nINPUT> ");
            System.out.flush();
            String line = bufReader.readLine();
            if (line == null || line.length() < 1 
                || line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit"))
                break;

            System.out.println(test.tagging(line));
            
        }
        Streams.closeReader(bufReader);
		
		/*
		Tagger test;
		
		if (args.length == 1){
			test = new Tagger(args[0]);
			test.run();
		}else {
			System.out.println("Argument is not correct!");
			System.out.println(args.length + " arguments were provided!");
			System.exit(-1);
		}
		*/
		
		

	}

}
