/*
 * Copyright 2009 Author: Lushan Han at Ebiquity Lab, UMBC
 *  
 */

package edu.umbc.wordSimilarity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import edu.umbc.nlp.Utility;

public class ParagraphGenerator {

	public String modelName;
	public BufferedReader textReader = null;
	public final int paragraph_lines_limit = 300;
	public final int paragraph_size_limit = 5000 * 10;
	public StringBuffer paragraph;
	public int paragraph_lines = 0;
	//public char[] paragraph = new char[CAPACITY];
	//public int paragraph_len = 0;
	
	public ParagraphGenerator(BufferedReader inputTextStream, String model_name){
		textReader = inputTextStream;
		paragraph = new StringBuffer(paragraph_size_limit);
		modelName = model_name;
	}

	public ParagraphGenerator(File file, String model_name){

		try{
			File textCorpus = file;
			textReader = new BufferedReader(new FileReader(textCorpus), 1000000);
			paragraph = new StringBuffer(paragraph_size_limit);
			modelName = model_name;

		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
			System.exit(-1);
		}
	}

	public ParagraphGenerator(String filename, String model_name){

		try{
			File textCorpus = new File(filename);
			textReader = new BufferedReader(new FileReader(textCorpus), 1000000);
			paragraph = new StringBuffer(paragraph_size_limit);
			modelName = model_name;

		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
			System.exit(-1);
		}
	}
	
	public void close(){
		
		try {
			if (textReader != null)
				textReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getNextParagraph() throws IOException{
		
		if (paragraph.capacity() > paragraph_size_limit)
			paragraph = new StringBuffer(paragraph_size_limit);
			
		paragraph.setLength(0);
		paragraph_lines = 0;
		
		//System.out.println(paragraph.capacity());
		
		String textLine;
		
		do {
			textLine = textReader.readLine();
			// The end of the input stream
			if (textLine == null) {
				textReader.close();
				return null;
			}
			
		} while (textLine.trim().length() == 0);
		
		do {
			if (textLine.endsWith("-"))
				paragraph.append(textLine.substring(0, textLine.length() -1));
			else
				paragraph.append(textLine + " ");
				
			paragraph_lines ++;
			
			textLine = textReader.readLine();
			// The end of the input stream
		} while (textLine != null && textLine.trim().length() != 0);
		
		if (paragraph.length() > paragraph_size_limit || paragraph_lines > paragraph_lines_limit)
			return "";
		else {
			String result = paragraph.toString();
			
			if (modelName.contains("Gutenberg") && (result.contains("End of Project Gutenberg") || result.contains("END OF THIS PROJECT GUTENBERG") || result.contains("End of the Project Gutenberg") || result.contains("END OF THE PROJECT GUTENBERG"))){
				textReader.close();
				return null;
			}else if (!Utility.IsNormalWriting(result))
				return "";
			else
				return result;
		}
		
	}
	
	public boolean removeCopyright() throws IOException{
		
		String textLine;
		
		for (int i=0; i < 250; i++){
			
			textLine = textReader.readLine();
			
			if (textLine == null)
				return false;
		}
		
		return true;
	}
	
	
	public boolean IsEnglish() throws IOException {
		// TODO Auto-generated method stub
		
		int result = -1;
		String textLine;

		for (int i=0; i < 50; i++){
			
			textLine = textReader.readLine();
			
			if (textLine == null) return false;
			
			if (textLine.startsWith("Language: ") && result == -1){
				if (textLine.startsWith("Language: English"))
					result = 1;
				else
					result = 0;
			}
			
			if (textLine.contains("Factbook") || textLine.contains("FACTBOOK") || textLine.contains("Thesaurus"))
				return false;
		}
		
		if (result == 1)
			return true;
		else if (result == 0)
			return false;
		else{
			
			// remove statements from Gutenberg up to the first 280 lines
			for (int i=0; i < 230; i++){
				
				textLine = textReader.readLine();
				
				if (textLine == null)
					return false;

				if (textLine.contains("End of Project Gutenberg") || textLine.contains("END OF THIS PROJECT GUTENBERG") || textLine.contains("End of the Project Gutenberg") || textLine.contains("END OF THE PROJECT GUTENBERG"))
					return false;
				
			}
			
		}
		
		return true;
	}

	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ParagraphGenerator test = new ParagraphGenerator("sample.txt", "test.mdl");
		String paragraph;
		while ((paragraph = test.getNextParagraph()) != null){
			System.out.println(paragraph);
			System.out.println();
		}
	}


}
