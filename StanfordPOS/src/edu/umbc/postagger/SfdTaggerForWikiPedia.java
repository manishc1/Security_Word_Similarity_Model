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
import edu.umbc.nlp.Utility;
import edu.umbc.wordSimilarity.GutenbergParagraphGenerator;

public class SfdTaggerForWikiPedia {

    private MaxentTagger tagger;
    private String directoryName;
	public long totalWords = 0;
	public long totalArticles = 0;

	
	public SfdTaggerForWikiPedia(String directory) throws Exception {
		// TODO Auto-generated constructor stub
    	String modelLocation = "/home/lushan1/nlp/model/stanford/20120612/left3words-distsim-wsj-0-18.tagger";
    	//String modelLocation = "/home/lushan1/nlp/model/stanford/left3words-wsj-0-18.tagger";
    	//String modelLocation = "/home/lushan1/nlp/model/stanford/bidirectional-distsim-wsj-0-18.tagger";
        System.out.println("Reading model from file=" + modelLocation);
        tagger = new MaxentTagger(modelLocation);
        directoryName = directory;
	}

	public SfdTaggerForWikiPedia() throws Exception {
		// TODO Auto-generated constructor stub
    	String modelLocation = "/home/lushan1/nlp/model/stanford/20120612/left3words-distsim-wsj-0-18.tagger";
    	//String modelLocation = "/home/lushan1/nlp/model/stanford/left3words-wsj-0-18.tagger";
    	//String modelLocation = "/home/lushan1/nlp/model/stanford/bidirectional-distsim-wsj-0-18.tagger";
        System.out.println("Reading model from file=" + modelLocation);
        tagger = new MaxentTagger(modelLocation);
        directoryName = null;
	}

	public String tagging(String paragraph){
		
		return tagger.tagString(paragraph.replace("_", ""));
		
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
						
						/*
						if (Runtime.getRuntime().maxMemory() - Runtime.getRuntime()..totalMemory() < 50000000){
							System.out.println("gc starts to run ...");
							System.out.println("Max memory: " + Runtime.getRuntime().maxMemory());
							System.out.println("Total memory: " + Runtime.getRuntime().totalMemory());
							System.out.println("Free memory: " + Runtime.getRuntime().freeMemory());
							System.gc();
							System.out.println("gc complete running.");
						}
						*/
						
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
		
		//ParagraphGenerator paragraphs = new ParagraphGenerator(textfile, "Gutenberg");
		//String paragraph;
		BufferedReader textReader = new BufferedReader(new FileReader(textfile), 1000000);
		String line;
		
		String path = textfile.getPath();
		
		String POSTaggerFileName = path.substring(0, path.length() - 4) + ".possf2";
		
		FileWriter POSTaggerFile = new FileWriter(POSTaggerFileName);
		PrintWriter outOfTaggerFile = new PrintWriter(new BufferedWriter(POSTaggerFile));


		while ((line = textReader.readLine()) != null){
			
			/*
			if (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() < 50000000){
				System.out.println("gc starts to run ...");
				System.out.println("Max memory: " + Runtime.getRuntime().maxMemory());
				System.out.println("Total memory: " + Runtime.getRuntime().totalMemory());
				System.out.println("Free memory: " + Runtime.getRuntime().freeMemory());
				System.gc();
				System.out.println("gc complete running.");
			}
			*/
			line = line.trim();
			
			if (line.length() > 50000 || line.length() < 120)
				line = "";
			else {
				if (!Utility.IsNormalWriting(line))
					line = "";
			}
			
			if (line.length() == 0)
				continue;

			String taggedParagraph = tagging(line);
			
			outOfTaggerFile.println(taggedParagraph);
			outOfTaggerFile.println();
		}
		
		if (textReader != null)
			textReader.close();
		
		outOfTaggerFile.close();
		totalArticles ++;
		return 0;
	}
	
	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		/*
		SfdTaggerForWikiPedia test = new SfdTaggerForWikiPedia();
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
        */
		
		SfdTaggerForWikiPedia test;
		
		if (args.length == 1){
			test = new SfdTaggerForWikiPedia(args[0]);
			test.run();
		}else {
			//test = new SfdTaggerForWikiPedia("/home/lushan1/nlp/ukwac");
			//test.run();
			System.out.println("Argument is not correct!");
			System.out.println(args.length + " arguments were provided!");
			System.exit(-1);
		}

	}

}
