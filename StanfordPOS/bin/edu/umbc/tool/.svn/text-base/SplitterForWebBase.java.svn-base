package edu.umbc.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

import edu.umbc.nlp.Utility;

public class SplitterForWebBase {

	public File directory; 
	HashSet<String> previousParagraphs;
	
	public SplitterForWebBase(String directoryName) {
		// TODO Auto-generated constructor stub
		//textfile = new File("/disk2/corpus/webbase/text-2007-02/university+publication/utexas_iit.pages");
		//textfile = new File("/home/lushan1/Download/webbase/text-02-2007/university-publication.ucdavis-mtu");
		//textfile = new File("/home/lushan1/Download/webbase/text-02-2007/university-publication.gsulaw.gsu-csmp.ucop");
		//textfile = new File("/home/lushan1/Download/webbase/text-02-2007/university-publication.library.sau.edu-dbpubs.stanford");
		//textfile = new File("/home/lushan1/Download/webbase/text-02-2007/university-publication.www.library.gatech.edu-www.hsls.pitt.edu");
		//textfile = new File("/home/lushan1/Download/webbase/text-02-2007/university-publication.www.cs.williams.edu-www.magee.edu");
		//textfile = new File("/home/lushan1/Download/webbase/www.bfranklin_www.library.gsu");
		
		directory = new File(directoryName);
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
	    	  if (file.getName().endsWith(".pages")){
	    	  //if (file.getName().endsWith(".txt")){

	    		  System.out.println("scanning " + file);
				  try {
						process(file);

				  } catch (IOException e) {
			    	  System.out.println("File " + file.toString() + " failed!");
			    	  e.printStackTrace();
			      }
	    	  }
	      }
	  }
	
	}
	

	
	public void process(File textfile) throws IOException {
		
		int paragraphNo = 0;
		
		BufferedReader textReader = new BufferedReader(new FileReader(textfile), 1000000);
				
		String path = textfile.getPath();
		
		String outputFileName = path + "_" + paragraphNo + ".txt";
		
		FileWriter outputFileWriter = new FileWriter(outputFileName);
		PrintWriter outFile = new PrintWriter(new BufferedWriter(outputFileWriter));
		
		String preLine; 		
		int preLineWidth;
		String preLineTrim;

		String curLine;
		int curLineWidth;
		String curLineTrim;
		
		StringBuffer paragraph = new StringBuffer(2000);
		int linesInParagraph = 0;
		String domain = null;
		boolean validDomain = false;
		
		preLine = textReader.readLine();
		if (preLine == null) return;
		preLine = preLine.replace("� ", " ");
		preLine = preLine.replace("  ", " ");
		preLineWidth = preLine.length();
		preLineTrim = preLine.trim();
		paragraph.append(preLineTrim);
		linesInParagraph ++;

		while ((curLine = textReader.readLine()) != null){
			
			if (preLine.startsWith("URL: http://") && curLine.startsWith("Date:")){
				
				int endIndex = preLine.indexOf('/', 12);
				if (endIndex < 0) endIndex = preLine.length();
				
				String newDomain = preLine.substring(12, endIndex);
				
				if (newDomain.endsWith(".edu") ||  newDomain.endsWith(".ac.uk"))
					validDomain = true;
				else
					//validDomain = false;
					validDomain = true;
				
				if (domain == null){
					
					domain = newDomain;
					previousParagraphs = new HashSet<String>(100000);
					System.out.println(domain);
					
				}else if (!newDomain.equals(domain)){
					
					previousParagraphs.clear();
					domain = newDomain;
					//System.out.println(domain);
				}
			}
			
    		curLine = curLine.replace("� ", " ");
    		curLine = curLine.replace("  ", " ");
			curLineWidth = curLine.length();
			curLineTrim = curLine.trim();
				
			if (validDomain) {

				int preLineStart = preLineWidth - preLineTrim.length();
				int curLineStart = curLineWidth - curLineTrim.length();
				
				int deltaStarts = Math.abs(curLineStart - preLineStart);
				int deltaEnds = Math.abs(curLineWidth - preLineWidth);
				
				if (curLineTrim.equals("")){
	
					String paraOut = paragraph.toString();
	
					if (paraOut.length() != 0){
						
						if (Utility.IsNormalWritingForWildText(paraOut)){
							
							String starter = paraOut.substring(0, Utility.Starter_Length);
							
							if (!previousParagraphs.contains(starter)){
								outFile.println(paraOut);
								outFile.println();
								paragraphNo ++;
								previousParagraphs.add(starter);
							} else {
								//System.out.println(starter);
							}
						}
	
						paragraph = new StringBuffer(2000);
						linesInParagraph = 0;
					}
	
					
				}else if ((Character.isLowerCase(curLineTrim.charAt(0)) || (preLineTrim.length() > 0 && linesInParagraph > 1 && (Character.isLowerCase(preLineTrim.charAt(preLineTrim.length() - 1)) || preLineTrim.charAt(preLineTrim.length() - 1) == ',' ))) 
						&& preLineTrim.length() < 210 && (deltaStarts < 20  || deltaEnds < 20)){
	
					paragraph.append(" " + curLineTrim);
					linesInParagraph ++;
	
					/*
					if (preLine.endsWith(" "))
						paragraph.append(curLineTrim);
					else
						paragraph.append(" " + curLineTrim);
					*/
				
				}else{
					
					String paraOut = paragraph.toString();
					
					if (paraOut.length() != 0){
					
						if (Utility.IsNormalWritingForWildText(paraOut)){
	
							String starter = paraOut.substring(0, Utility.Starter_Length);
							
							if (!previousParagraphs.contains(starter)){
								outFile.println(paraOut);
								outFile.println();
								paragraphNo ++;
								previousParagraphs.add(starter);
							} else {
								//System.out.println(starter);
							}
						}
						
						paragraph = new StringBuffer(2000);
						linesInParagraph = 0;
					}
					
					paragraph.append(curLineTrim);
					linesInParagraph ++;
				}
				
				if (paragraphNo % 100000 == 0){
					outFile.close();
					outputFileName = path + "_" + paragraphNo / 100000 + ".txt";
					outputFileWriter = new FileWriter(outputFileName);
					outFile = new PrintWriter(new BufferedWriter(outputFileWriter));
				}
			
			}
			
			preLine = curLine;
			preLineWidth = curLineWidth;
			preLineTrim = curLineTrim;
		}
		
		if (textReader != null)
			textReader.close();
		
		outFile.close();
		System.out.println("Total paragraph is " + paragraphNo);
		
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		SplitterForWebBase splitter = new SplitterForWebBase("/disk2/corpus/webbase/text-2007-02/university+publication/");
		splitter.scanDocs(splitter.directory);
	}

}
