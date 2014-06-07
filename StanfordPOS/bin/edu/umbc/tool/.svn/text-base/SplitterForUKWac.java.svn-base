package edu.umbc.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.umbc.nlp.Utility;

public class SplitterForUKWac {

	File textfile;
	int paragraphNo;
	
	public SplitterForUKWac() {
		// TODO Auto-generated constructor stub
		textfile = new File("/home/lushan1/nlp/ukwac/ukwac_preproc");
		paragraphNo = 0;
	}
	
	public void run() throws IOException {
		
		BufferedReader textReader = new BufferedReader(new FileReader(textfile), 1000000);
		String line;
		
		String path = textfile.getPath();
		
		String outputFileName = path + "_" + paragraphNo + ".txt";
		
		FileWriter outputFileWriter = new FileWriter(outputFileName);
		PrintWriter outFile = new PrintWriter(new BufferedWriter(outputFileWriter));
		
		while ((line = textReader.readLine()) != null){
			
			line = line.trim();
			
			if (line.startsWith("CURRENT URL"))
				continue;

			/*
			int fromIndex = 0;
			int endIndex;
			StringBuffer paragraph = new StringBuffer();

			while ((endIndex = line.indexOf(".", fromIndex)) > 0){
				
				String sentence = line.substring(fromIndex, endIndex + 1);
				
				if (Utility.IsNormalWriting(sentence))
					paragraph.append(sentence);
				
				fromIndex = endIndex;
			}
			
			outFile.println(paragraph);
			outFile.println();
			*/
			
			outFile.println(line);
			outFile.println();
			
			paragraphNo ++;
			
			if (paragraphNo % 20000 == 0){
				outFile.close();
				outputFileName = path + "_" + paragraphNo / 20000 + ".txt";
				outputFileWriter = new FileWriter(outputFileName);
				outFile = new PrintWriter(new BufferedWriter(outputFileWriter));
			}
			
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
		SplitterForUKWac splitter = new SplitterForUKWac();
		splitter.run();
	}

}
