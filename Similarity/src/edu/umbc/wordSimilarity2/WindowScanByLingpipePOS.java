package edu.umbc.wordSimilarity2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.Morphology;

public class WindowScanByLingpipePOS {
	
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

	

	public WindowScanByLingpipePOS(int window_size, String stopwordsFilename, String dirName, String modelName, String word1, String word2) throws IOException {
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
			System.out.println("Start to serialize the model. It may take a few minutes ...");
				
			//close debug file
			if (DEBUG_ON)
				outOfDebug.close();
			
			/*
			FileOutputStream fileOut;
			ObjectOutputStream objOut;
			fileOut = new FileOutputStream(model.modelName + ".mdl2");
			objOut = new ObjectOutputStream(fileOut);
			objOut.writeObject(model);
			objOut.close();
			*/
			
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
	    	  if (file.getName().endsWith(".poslp")){
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

	
	
	public int startScanAndCount(File textfile) throws IOException{
		
		//int numberOfElements = 0;
		LinkedList<int[]> windowElements;
		PosParagraphGenerator paragraphs = new PosParagraphGenerator(textfile, model.modelName);
		String paragraph;
		//int parNo = 0;
		
		

		while ((paragraph = paragraphs.getNextParagraph()) != null){
			
			//if (Runtime.getRuntime().freeMemory() < 50000000){
			if (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() < 50000000){
				System.out.println("gc starts to run ...");
				System.gc();
				System.out.println("gc complete running.");
			}
			
			if (paragraph.length() == 0)
				continue;
			
			LingpipeWordTokenizer wt = new LingpipeWordTokenizer(paragraph, stopWords, morpha);
			
			String[] word;
			
			windowElements = new LinkedList<int[]>();
			
			while ((word = wt.getNextValidWord()) != null){

				/*
				int found = model.addFrequency(word);
				
				if (found < 0){
					word = morpha.stem(new Word(word)).value();
					found = model.addFrequency(word);
				}
				*/
				
				int[] found = new int[2];

				found[0] = model.addFrequency(word[0]);
				
				if (found[0] >= 0) model.totalWords ++;
				
				if (word[1] != null)
					found[1] = model.addFrequency(word[1]);
				else
					found[1] = -1;
				
				if (found[0] >=0 || found[1] >= 0){
				
					for (int element[]: windowElements){
						
						model.addCoOccurrence(found[0], element[0]);
						model.addCoOccurrence(found[0], element[1]);
						model.addCoOccurrence(found[1], element[0]);
						model.addCoOccurrence(found[1], element[1]);
						//model.addCoOccurrence(word, element);
						
						if (DEBUG_ON){
							if ((found[0] == debugWord1 && element[0] == debugWord2) || (found[0] == debugWord2 && element[0] == debugWord1)){
								outOfDebug.println(textfile + ": " + paragraph);
								outOfDebug.println();
							}
						}
	
						/*
						if ((word.equals("use") && element.equals("type")) || (word.equals("type") && element.equals("use")))
							System.out.println(element + " " + word + " ParNo. " + parNo + ": " + windowElements.toString());
						*/
					}
				}
					
				//if (numberOfElements < windowSize){
				if (windowElements.size() < windowSize){
					windowElements.add(found);
					//numberOfElements ++;
				}else{
					windowElements.poll();
					windowElements.add(found);
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
			WindowScanByLingpipePOS test;
			
			/*
			test = new WindowScanByParaAndPOS(20, "/home/lushan1/nlp/model/Gutenberg2010/stopwords", "/home/lushan1/nlp/test/corpus/test", 
					"/home/lushan1/nlp/test/model/test", "temperate_JJ", "mild_JJ");

			test.run();
			*/
			
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
				test = new WindowScanByLingpipePOS(size, "/home/lushan1/nlp/model/Gutenberg2010pos/stopwords", "/home/lushan1/nlp/Gutenberg2010Books", 
						"/home/lushan1/nlp/model/Gutenberg2010pos/Gutenberg2010AllW" + (size + 1), args[1], args[2]);
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
