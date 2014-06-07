/*
 * Copyright 2009 Author: Lushan Han at Ebiquity Lab, UMBC
 *  
 */

package edu.umbc.gor.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class TestCaseGenerator {

	public BufferedReader textReader = null;
	public StringBuffer paragraph;
	public String curQuestionType = "";
	public String curQuestionNo = "";
	//public char[] paragraph = new char[CAPACITY];
	//public int paragraph_len = 0;
	

	public TestCaseGenerator(String filename){

		try{
			File textCorpus = new File(filename);
			textReader = new BufferedReader(new FileReader(textCorpus));

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
		
		paragraph = new StringBuffer();
		
		String textLine;
		
		do {
			textLine = textReader.readLine();
			// The end of the input stream
			if (textLine == null) {
				textReader.close();
				//System.out.println(filename + ": " + "Read to the end of the file without meeting END PROJECT sign");
				return null;
			}
			
		} while (textLine.trim().length() == 0);
		
		
		
		do {
			paragraph.append(textLine + "\n");
				
			textLine = textReader.readLine();
			// The end of the input stream
		} while (textLine != null && textLine.trim().length() != 0);
		
		String result = paragraph.toString();
		
		return result;
		
	}
	
	public TestCase getNextTestCase() throws Exception{
		
		String paragraph = getNextParagraph();
		
		if (paragraph != null){
			TestCase testcase = new TestCase(paragraph, curQuestionType, curQuestionNo);
			
			if (!testcase.questionType.equals(curQuestionType))
				curQuestionType = testcase.questionType;
			
			if (!testcase.questionNo.equals(curQuestionNo))
				curQuestionNo = testcase.questionNo;
			
			return testcase;
		}else{
			return null;
		}
		
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		TestCaseGenerator test = new TestCaseGenerator("/home/lushan1/dblp/evaluation/testcases.txt");
		TestCase testcase;
		while ((testcase = test.getNextTestCase()) != null){
			System.out.println(testcase);
			System.out.println();
		}
	}


}
