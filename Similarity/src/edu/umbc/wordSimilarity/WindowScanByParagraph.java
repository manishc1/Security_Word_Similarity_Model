package edu.umbc.wordSimilarity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.Morphology;

public class WindowScanByParagraph {
	
	public int windowSize;
	public String[] stopWords;
	public CoOccurModelByArrays model;
	public String directoryName;
	public Morphology morpha;
	public long totalWords;
	public long totalArticles;
	
	/* variables for debug */
	private String debugFileName = "debug.txt";
	private PrintWriter outOfDebug;
	private int debugWord1;
	private int debugWord2;
	private boolean DEBUG_ON = true;
	private boolean PHRASE_BOUNDARY_ON = false;
	private int pre_repetition = 0;
	private int post_repetition = 0;
	//private static int COMMA = 3;
	private static int PUNCTUATION = 3;
	private int DUMMY = -1;

	

	public WindowScanByParagraph(int window_size, String stopwordsFilename, String dirName, String modelName, String word1, String word2) throws IOException {
		// TODO Auto-generated constructor stub
		windowSize = window_size;
		model = new CoOccurModelByArrays(modelName, true);
		directoryName = dirName;
		morpha = new Morphology();
		totalWords = 0;
		totalArticles = 0;
		
		File stopwordsFile = new File(stopwordsFilename + ".stw");
		BufferedReader stopwordsReader = new BufferedReader(new FileReader(stopwordsFile));
		
		Vector<String> stopWordsVtr = new Vector<String>();
		
		String stopword;
		while ((stopword = stopwordsReader.readLine()) != null){
			stopWordsVtr.add(stopword);
		}
		
		stopwordsReader.close();
		stopWords = stopWordsVtr.toArray(new String[stopWordsVtr.size()]);
		Arrays.sort(stopWords);
		
		//open debug file
		if (DEBUG_ON){
			FileWriter debugFile = new FileWriter(debugFileName);
			outOfDebug = new PrintWriter(new BufferedWriter(debugFile));
			debugWord1 = model.index(word1.trim());
			debugWord2 = model.index(word2.trim());
		}

	}
		
	public void run(){
		
		try{
			File directory = new File(directoryName);
			
			long startTime = System.currentTimeMillis();

			scanDocs(directory);
			
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
			System.out.println("Start to save the model. It may take a few minutes ...");
				
			//close debug file
			if (DEBUG_ON)
				outOfDebug.close();
			
			model.saveModel();
			
			System.out.println("Congratulation! Work Completed!");
			
			
			
		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
			System.exit(-1);
		}
	}
	
	
	public void scanDocs(File file) {
	    // do not try to scan files that cannot be read
	    if (file.canRead()) {
	      if (file.isDirectory()) {
	        String[] files = file.list();
	        // an IO error could occur
	        if (files != null) {
	          for (int i = 0; i < files.length; i++) {
	        	  scanDocs(new File(file, files[i]));
	          }
	        }
	      } else {
	    	  if (file.getName().endsWith(".txt") && !file.getName().contains("-")){
	    	  //if (file.getName().endsWith(".txt")){

	    		  System.out.println("scanning " + file);
				  try {
						startScanAndCount(file);
						//totalArticles ++;
						
						//if (Runtime.getRuntime().freeMemory() < 50000000){
						if (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() < 50000000){
							System.out.println("gc starts to run ...");
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

	
	public String getNextValidWord(StringTokenizer st){
		
		while (st.hasMoreTokens()){
			String word = st.nextToken();
			pre_repetition = 0;
			post_repetition = 0;
			
			if (PHRASE_BOUNDARY_ON){

				if ((!Character.isLowerCase(word.charAt(0))) && (!Character.isUpperCase(word.charAt(0))))
					pre_repetition = PUNCTUATION;
				
				if ((!Character.isLowerCase(word.charAt(word.length()-1))) && (!Character.isUpperCase(word.charAt(word.length()-1))))
					post_repetition = PUNCTUATION;

				int pre_index;
				int post_index;
				
				for (pre_index=0; pre_index < word.length(); pre_index++){
					if (Character.isLowerCase(word.charAt(pre_index)) || Character.isUpperCase(word.charAt(pre_index)))
						break;
				}
				
				if (pre_index == word.length())
					return "";
				
				for (post_index = word.length()-1; post_index > 0; post_index--){
					if (Character.isLowerCase(word.charAt(post_index)) || Character.isUpperCase(word.charAt(post_index)))
						break;
				}
				
				if (Character.isUpperCase(word.charAt(pre_index)) && word.length() > post_index + 1 && word.charAt(post_index + 1) == '.'){

					if (post_index + 2 - pre_index > 5)   // Mr. Dr. Mrs. Prof.
						word = word.substring(pre_index, post_index + 1);
					else {
						post_repetition = 0;
						word = word.substring(pre_index, post_index + 2);
					}
					
				}else
					word = word.substring(pre_index, post_index + 1);
				
			} else { 
				
				if ((word.endsWith(".") && Character.isLowerCase(word.charAt(0))) || word.endsWith(",")){
					word = word.substring(0, word.length() - 1);
				}
				
				if (word.length() == 0) continue;
			}
			

			if (Arrays.binarySearch(stopWords, word.toLowerCase()) < 0)
				return word;
			else {
				if (pre_repetition !=0 || post_repetition !=0)
					return "";
			}
			
		}
		
		return null;
	}
	
	
	public int startScanAndCount(File textfile) throws IOException{
		
		//int numberOfElements = 0;
		LinkedList<Integer> windowElements;
		ParagraphGenerator paragraphs = new ParagraphGenerator(textfile, model.modelName);
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

		while ((paragraph = paragraphs.getNextParagraph()) != null){
			
			//if (Runtime.getRuntime().freeMemory() < 50000000){
			if (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() < 50000000){
				System.out.println("gc starts to run ...");
				System.gc();
				System.out.println("gc complete running.");
			}
			
			if (paragraph.length() == 0)
				continue;
			
			// to simulate phrase seperator
			if (PHRASE_BOUNDARY_ON)
				paragraph = paragraph.replaceAll("--", ", ");
				//paragraph = paragraph.replaceAll(" \"|--| \\(|[\")]", ", ");
			
			//parNo ++;
			StringTokenizer st;
			
			if (PHRASE_BOUNDARY_ON)
				st = new StringTokenizer(paragraph, " -<>'");
			else
				st = new StringTokenizer(paragraph, " *()<>!-_?:;'\"");
			
			String word;
			windowElements = new LinkedList<Integer>();
			//numberOfElements = 0;
			
			while ((word = getNextValidWord(st)) != null){

				/*
				int found = model.addFrequency(word);
				
				if (found < 0){
					word = morpha.stem(new Word(word)).value();
					found = model.addFrequency(word);
				}
				*/
				
				if (!word.equals("") && !word.endsWith("."))
					word = morpha.stem(new Word(word)).value();
				
				if (!PHRASE_BOUNDARY_ON && word == null){
					continue;
				}
				
				while (PHRASE_BOUNDARY_ON && pre_repetition > 0){
					
					if (windowElements.size() < windowSize){
						windowElements.add(DUMMY);
						//numberOfElements ++;
					}else{
						windowElements.poll();
						windowElements.add(DUMMY);
					}
					
					pre_repetition --;
				}
				
				int found;
				
				if (word == null || word.equals(""))
					found = DUMMY;
				else
					found = model.addFrequency(word);
					
				
				if (found >=0){
				
					for (Integer element: windowElements){
						
						model.addCoOccurrence(found, element);
						//model.addCoOccurrence(word, element);
						
						if (DEBUG_ON){
							if ((found == debugWord1 && element == debugWord2) || (found == debugWord2 && element == debugWord1)){
								outOfDebug.println(textfile + ": " + paragraph);
								outOfDebug.println();
							}
						}
	
						/*
						if ((word.equals("use") && element.equals("type")) || (word.equals("type") && element.equals("use")))
							System.out.println(element + " " + word + " ParNo. " + parNo + ": " + windowElements.toString());
						*/
	
						/*
						if ((word.equals("maximum") && element.equals("memory")) || (word.equals("memory") && element.equals("maximum")))
							System.out.println(element + " " + word + " ParNo. " + parNo + ": " + windowElements.toString());
						*/
					}
				}
					
				//if (numberOfElements < windowSize){
				if (found != DUMMY){
				
					if (windowElements.size() < windowSize){
						windowElements.add(found);
						//numberOfElements ++;
					}else{
						windowElements.poll();
						windowElements.add(found);
					}
				
				}
				
				while (PHRASE_BOUNDARY_ON && post_repetition > 0){
					
					if (windowElements.size() < windowSize){
						windowElements.add(DUMMY);
						//numberOfElements ++;
					}else{
						windowElements.poll();
						windowElements.add(DUMMY);
					}
					
					post_repetition --;
				}
				
				totalWords ++;
				
			}
		}
		
		paragraphs.close();
		totalArticles ++;
		return 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			//WindowScanByParagraph test = new WindowScanByParagraph(20, "/home/lushan1/nlp/model/usenet/stopwords", "/home/lushan1/nlp/newscorpus2", "/home/lushan1/nlp/model/usenet/usenetAllW21");
			//WindowScanByParagraph test = new WindowScanByParagraph(3, "/home/lushan1/nlp/model/usenet/stopwords", "/home/lushan1/nlp/newscorpus2", "/home/lushan1/nlp/model/usenet/usenetAllW4");
			//WindowScanByParagraph test = new WindowScanByParagraph(20, "/home/lushan1/nlp/model/Gutenberg2006/stopwords", "/home/lushan1/nlp/Gutenberg2006Books", "/home/lushan1/nlp/model/Gutenberg2006/Gutenberg2006AllW21");
			//WindowScanByParagraph test = new WindowScanByParagraph(3, "/home/lushan1/nlp/model/Gutenberg2006/stopwords", "/home/lushan1/nlp/Gutenberg2006Books", "/home/lushan1/nlp/model/Gutenberg2006/Gutenberg2006AllW4");
			//WindowScanByParagraph test = new WindowScanByParagraph(3, "/home/lushan1/nlp/model/Gutenberg2006/stopwords", "/home/lushan1/nlp/Gutenberg2006Books", "/home/lushan1/nlp/model/Gutenberg2006/Gutenberg2006AllW4");
			WindowScanByParagraph test;
			
			/*
			if (args.length > 0)
				test = new WindowScanByParagraph(5, "/home/lushan1/nlp/model/Gutenberg2010/stopwords", "/home/lushan1/nlp/Gutenberg2010Books", "/home/lushan1/nlp/model/Gutenberg2010/Gutenberg2010AllW6", args[0], args[1]);
			else
				test = new WindowScanByParagraph(5, "/home/lushan1/nlp/model/Gutenberg2010/stopwords", "/home/lushan1/nlp/Gutenberg2010Books", "/home/lushan1/nlp/model/Gutenberg2010/Gutenberg2010AllW6", "temperate", "mild");
			*/
			
			/*
			if (args.length > 0)
				test = new WindowScanByParagraph(20, "/home/lushan1/nlp/model/Gutenberg2010/stopwords", "/home/lushan1/nlp/Gutenberg2010Books", "/home/lushan1/nlp/model/Gutenberg2010/Gutenberg2010AllW21", args[0], args[1]);
			else
				test = new WindowScanByParagraph(20, "/home/lushan1/nlp/model/Gutenberg2010/stopwords", "/home/lushan1/nlp/Gutenberg2010Books", "/home/lushan1/nlp/model/Gutenberg2010/Gutenberg2010AllW21", "temperate", "mild");
			
			if (args.length > 0)
				test = new WindowScanByParagraph(10, "/home/lushan1/nlp/model/Gutenberg2010/stopwords", "/home/lushan1/nlp/Gutenberg2010Books", "/home/lushan1/nlp/model/Gutenberg2010/Gutenberg2010AllW11", args[0], args[1]);
			else
				test = new WindowScanByParagraph(10, "/home/lushan1/nlp/model/Gutenberg2010/stopwords", "/home/lushan1/nlp/Gutenberg2010Books", "/home/lushan1/nlp/model/Gutenberg2010/Gutenberg2010AllW11", "temperate", "mild");
			*/
			
			/*
			if (args.length > 0)
				test = new WindowScanByParagraph(20, "/home/lushan1/nlp/model/Gutenberg2010/stopwords", "/home/lushan1/nlp/test", "/home/lushan1/nlp/model/test/Gutenberg2010AllW21", args[0], args[1]);
			else
				test = new WindowScanByParagraph(20, "/home/lushan1/nlp/model/Gutenberg2010/stopwords", "/home/lushan1/nlp/test", "/home/lushan1/nlp/model/test/Gutenberg2010AllW21", "temperate", "mild");\
			*/

			if (args.length == 3){
				int size = Integer.parseInt(args[0]);
				test = new WindowScanByParagraph(size, "/home/lushan1/nlp/model/Gutenberg2010/stopwords", "/home/lushan1/nlp/Gutenberg2010Books", "/home/lushan1/nlp/model/Gutenberg2010/Gutenberg2010AllW" + (size + 1), args[1], args[2]);
				test.run();
			}else {
				System.out.println("Argument is not correct!");
				System.out.println(args.length + " arguments were provided!");
				System.exit(-1);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
