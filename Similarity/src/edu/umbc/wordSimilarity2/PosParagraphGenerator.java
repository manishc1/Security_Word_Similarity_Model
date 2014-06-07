/*
 * Copyright 2009 Author: Lushan Han at Ebiquity Lab, UMBC
 *  
 */

package edu.umbc.wordSimilarity2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PosParagraphGenerator {

	public String modelName;
	public BufferedReader textReader = null;
	public final int paragraph_size_limit = 5000 * 10 * 2;
	public StringBuffer paragraph;
	public int paragraph_lines = 0;
	//public char[] paragraph = new char[CAPACITY];
	//public int paragraph_len = 0;
	
	public PosParagraphGenerator(BufferedReader inputTextStream, String model_name){
		textReader = inputTextStream;
		paragraph = new StringBuffer(paragraph_size_limit);
		modelName = model_name;
	}

	public PosParagraphGenerator(File file, String model_name){

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

	public PosParagraphGenerator(String filename, String model_name){

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
			paragraph.append(textLine + " ");
				
			paragraph_lines ++;
			
			textLine = textReader.readLine();
			// The end of the input stream
		} while (textLine != null && textLine.trim().length() != 0);
		

		String result = paragraph.toString();
		
		return result;
		
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		PosParagraphGenerator test = new PosParagraphGenerator("91.poslp", "test.mdl");
		String paragraph;
		int paragraphs = 0;
		while ((paragraph = test.getNextParagraph()) != null){
			System.out.println(paragraph);
			System.out.println("-----------------------------------------------------------------------------------");
			paragraphs ++;
		}
		
		System.out.println(paragraphs);
	}


}
