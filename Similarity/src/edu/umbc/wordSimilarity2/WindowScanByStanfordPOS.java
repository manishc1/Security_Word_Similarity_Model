package edu.umbc.wordSimilarity2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;

import edu.stanford.nlp.process.Morphology;

public class WindowScanByStanfordPOS {
	
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

	

	public WindowScanByStanfordPOS(int window_size, String stopwordsFilename, String dirName, String modelName, String word1, String word2) throws IOException {
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
			
			long endTime = System.currentTimeMillis();
			long elipsedTime = endTime - startTime;
			
			System.out.println(totalArticles + " articles have been processed.");
			System.out.println(totalWords + " words have been processed.");
			System.out.println(StanfordTermTokenizer.totalWords + " words have been processed. (with stop words)");
			System.out.println(elipsedTime + " milli seconds have been taken to process the corpus.");
			System.out.println("Start to serialize the model. It may take a few minutes ...");
				
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
				if (file.getName().endsWith(".possf2") && !file.getName().contains("readme")){
					System.out.println("scanning " + file);
					try {
							startScanAndCount(file);							
					} catch (IOException e) {
						System.out.println("File " + file.toString() + " failed!");
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	
	public int startScanAndCount(File textfile) throws IOException{
		
		LinkedList<int[]> windowElements;
		PosParagraphGenerator paragraphs = new PosParagraphGenerator(textfile, model.modelName);
		String paragraph;

		while ((paragraph = paragraphs.getNextParagraph()) != null){
			
			if (paragraph.length() == 0)
				continue;
			
			StanfordTermTokenizer wt = new StanfordTermTokenizer(paragraph, stopWords, morpha, model.vocabulary);
			
			String[] word;
			
			windowElements = new LinkedList<int[]>();
			
			while ((word = wt.getNextValidWord()) != null){
				
				int[] found = new int[2];

				found[0] = model.addFrequency(word[0]);
				
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
						
						if (DEBUG_ON){
							if ((found[0] == debugWord1 && element[0] == debugWord2) || (found[0] == debugWord2 && element[0] == debugWord1)){
								outOfDebug.println(textfile + ": " + paragraph);
								outOfDebug.println();
							}

							if ((found[0] == debugWord1 && element[1] == debugWord2) || (found[0] == debugWord2 && element[1] == debugWord1)){
								outOfDebug.println(textfile + ": " + paragraph);
								outOfDebug.println();
							}
							
							if ((found[1] == debugWord1 && element[0] == debugWord2) || (found[1] == debugWord2 && element[0] == debugWord1)){
								outOfDebug.println(textfile + ": " + paragraph);
								outOfDebug.println();
							}
							
							if ((found[1] == debugWord1 && element[1] == debugWord2) || (found[1] == debugWord2 && element[1] == debugWord1)){
								outOfDebug.println(textfile + ": " + paragraph);
								outOfDebug.println();
							}
						}
	
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
			WindowScanByStanfordPOS test;
			
			if (args.length == 3){
				int size = Integer.parseInt(args[0]);
				test = new WindowScanByStanfordPOS(size, "/home/manish/lushan/nlp/model/stopwords", "/home/manish/lushan/nlp/raw-data/CVE", 
						"/home/manish/lushan/nlp/model/CVE", args[1], args[2]);

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
