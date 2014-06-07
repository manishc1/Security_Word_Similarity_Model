package edu.umbc.postagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import com.aliasi.util.Streams;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.umbc.wordSimilarity.GutenbergParagraphGenerator;

public class SfdTagger {

    private MaxentTagger tagger;
    private String directoryName;
	public long totalWords = 0;
	public long totalArticles = 0;

	
   public SfdTagger(String directory, String model_location) throws Exception {
		// TODO Auto-generated constructor stub
		String modelLocation = model_location;
        System.out.println("Reading model from file=" + modelLocation);
        tagger = new MaxentTagger(modelLocation);
        directoryName = directory;
	}


	public SfdTagger(String directory) throws Exception {
		// TODO Auto-generated constructor stub
    	String modelLocation = "/home/manish/lushan/nlp/model/stanford/pos-tagger/wsj-left3words/wsj-0-18-left3words-distsim.tagger";
        System.out.println("Reading model from file=" + modelLocation);
        tagger = new MaxentTagger(modelLocation);
        directoryName = directory;
	}


	public SfdTagger() throws Exception {
		// TODO Auto-generated constructor stub
    	String modelLocation = "/home/manish/lushan/nlp/model/stanford/pos-tagger/wsj-left3words/wsj-0-18-left3words-distsim.tagger";
        System.out.println("Reading model from file=" + modelLocation);
        tagger = new MaxentTagger(modelLocation);
        directoryName = null;
	}


	public String tagging(String paragraph){
		return tagger.tagString(paragraph.replace("_", ""));		
	}
	

	public void close(){
		
	}
	
	
	public void run() {
		// traverses the directory and tags the '.txt' files in it.
		
		try{
			File directory = new File(directoryName);
			
			long startTime = System.currentTimeMillis();

			tagDocs(directory);
			
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
	
	
	/**
	 * @param file
	 */
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
				if (file.getName().endsWith(".txt")) {					
					try {
						startTaggingAndCounting(file);						
					} catch (IOException e) {
						System.out.println("File " + file.toString() + " failed!");
						e.printStackTrace();
					}
				}
			}
	    }	    
	}
	
	
	public int startTaggingAndCounting(File textfile) throws IOException{
		
		try{
			File textCorpus = textfile;
			BufferedReader textReader = new BufferedReader(new FileReader(textCorpus), 1000000);
			String text = textReader.readLine();

			String path = textfile.getPath();
			String POSTaggerFileName = path.substring(0, path.length() - 4) + ".possf2";		
			FileWriter POSTaggerFile = new FileWriter(POSTaggerFileName);
			PrintWriter outOfTaggerFile = new PrintWriter(new BufferedWriter(POSTaggerFile));

			String taggedParagraph = tagging(text);
			
			outOfTaggerFile.println(taggedParagraph);
			outOfTaggerFile.println();
			outOfTaggerFile.close();

			totalArticles ++;

			return 0;

		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
			System.exit(-1);
		}
		return 0;
	}

		
	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
				
		SfdTagger sfd_tagger;
		
		if (args.length == 2){
			sfd_tagger = new SfdTagger(args[0], args[1]);
			sfd_tagger.run();
		} else if (args.length == 1){
			sfd_tagger = new SfdTagger(args[0]);
			sfd_tagger.run();
		} else {
			System.out.println("Argument is not correct!");
			System.out.println(args.length + " arguments were provided!");
			System.out.println("Usage: java SfdTagger.class <directory> <.tagger>");
			System.exit(-1);
		}

	}

}
