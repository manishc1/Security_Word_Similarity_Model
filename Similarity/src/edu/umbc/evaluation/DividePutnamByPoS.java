/*
 * Copyright 2009 Author: Lushan Han at Ebiquity Lab, UMBC
 *  
 */

package edu.umbc.evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import edu.umbc.wordSimilarity2.CoOccurModelByArrays;

public class DividePutnamByPoS {

	public BufferedReader textReader = null;
	public StringBuffer paragraph;
	public int paragraph_lines = 0;
	public String nounFilename = "/home/lushan1/nlp/evaluation/nounSynonyms700_2000.txt";
	private PrintWriter outOfNoun;
	public String verbFilename = "/home/lushan1/nlp/evaluation/verbSynonyms700_2000.txt";
	private PrintWriter outOfVerb;
	public String adjFilename = "/home/lushan1/nlp/evaluation/adjSynonyms700_2000.txt";
	private PrintWriter outOfAdjective;
	public String advFilename = "/home/lushan1/nlp/evaluation/advSynonyms700_2000.txt";
	private PrintWriter outOfAdverb;
	public CoOccurModelByArrays model;
	public int threshold = 700;
	public int targetThreshold = 700;
	public int totalNounPairs = 0;
	public int totalVerbPairs = 0;
	public int totalAdjPairs = 0;
	public int totalAdvPairs = 0;
	
	
	public DividePutnamByPoS(String filename, String modelname){

		try{
			File textCorpus = new File(filename);
			textReader = new BufferedReader(new FileReader(textCorpus), 1000000);
			paragraph = new StringBuffer();
			
			FileWriter nounFile = new FileWriter(nounFilename);
			outOfNoun = new PrintWriter(new BufferedWriter(nounFile));
			
			FileWriter verbFile = new FileWriter(verbFilename);
			outOfVerb = new PrintWriter(new BufferedWriter(verbFile));
			
			FileWriter adjFile = new FileWriter(adjFilename);
			outOfAdjective = new PrintWriter(new BufferedWriter(adjFile));
			
			FileWriter advFile = new FileWriter(advFilename);
			outOfAdverb = new PrintWriter(new BufferedWriter(advFile));
			
			model = new CoOccurModelByArrays(modelname, false);
					

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
			
			outOfNoun.close();
			outOfVerb.close();
			outOfAdjective.close();
			outOfAdverb.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getNextParagraph() throws IOException{
		
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
		
		String result = paragraph.toString();
		
		return result;
		
	}
	

	public void process() throws IOException{
		
		String passage;
		int totalEntries = 0;
		
		while ((passage = getNextParagraph()) != null){
			totalEntries ++;
			
			StringTokenizer st = new StringTokenizer(passage, ",;.", true);
			StringBuffer synonyms = new StringBuffer();
			
			String targetWord = st.nextToken();
			
			if (targetWord.contains(" ") || !Character.isLowerCase(targetWord.charAt(0))) continue;
			
			
			String comma = st.nextToken();
			
			if (!comma.equals(","))
				continue;
			
			String pos = st.nextToken().trim();
			
			PrintWriter out;
			
			if (pos.equals("n")){
				out = outOfNoun;
				pos = "_NN";
			}else if (pos.equals("v")){
				out = outOfVerb;
				pos = "_VB";
			}else if (pos.equals("a")){
				out = outOfAdjective;
				pos = "_JJ";
			}else if (pos.equals("adv")){
				out = outOfAdverb;
				pos = "_RB";
			}else
				continue;
			
			if (!st.nextToken().equals("."))
				continue;
			
			if (model.getFrequency(targetWord + pos) < targetThreshold || model.getFrequency(targetWord + pos) > 2000)
				continue;

			
			int numberOfSynonyms = 0;
			while (st.hasMoreTokens()){
				String token = st.nextToken();
				if (token.trim().equals("pl")) break;
				if (token.equals(".")) {
					
					break;
					/*
					String nexttoken = st.nextToken();
					
					if (nexttoken != null && nexttoken.startsWith(")"))
						token = token + nexttoken;
					else
						break;
					*/
				}
				if (token.trim().startsWith("Associated")) break;
				if (token.trim().startsWith("Antonyms")) break;
				
				if (token.equals(",") || token.equals(";")){
					synonyms.append(token);
				}
				else{
					int start;
					int end;
					if ((start = token.indexOf("(")) > -1){
						end = token.indexOf(")");
						
						while (end < 0){
							token = token + st.nextToken();
							end = token.indexOf(")");
						}
						
						/*
						if (end < 0){
							token = token + st.nextToken() + st.nextToken();
							end = token.indexOf(")");
							if (end < 0){
								token = token + st.nextToken() + st.nextToken();
								end = token.indexOf(")");
								if (end <0){
									System.out.println("no matching ) in " + token);
									System.exit(-1);
								}
							}
						}
						*/
						token = token.substring(0, start) + token.substring(end + 1, token.length());
					}
					
					token = token.trim();
					
					boolean isNormalWord = true;
					
					for (int i=0; i<token.length(); i++){
						if (!Character.isLowerCase(token.charAt(i))){
							isNormalWord = false;
							break;
						}
					}
					
					if (isNormalWord){
						if (model.getFrequency(token + pos) >= threshold){
							synonyms.append(token);
							numberOfSynonyms ++;
						}
					}
				}
			}
			
			if (numberOfSynonyms == 0)
				continue;
			
			if (pos.equals("_NN"))
				totalNounPairs += numberOfSynonyms;
			else if (pos.equals("_VB"))
				totalVerbPairs += numberOfSynonyms;
			else if (pos.equals("_JJ"))
				totalAdjPairs += numberOfSynonyms;
			else if (pos.equals("_RB"))
				totalAdvPairs += numberOfSynonyms;
			
			
			out.println(targetWord + ":" + synonyms);
			out.println();
		}

		System.out.println("total entries is: " + totalEntries);
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		DividePutnamByPoS test = new DividePutnamByPoS("/home/lushan1/nlp/evaluation/AllSynonymPairs.txt", "/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW41");
		test.process();
		test.close();
		System.out.println("total noun pairs is " + test.totalNounPairs);
		System.out.println("total verb pairs is " + test.totalVerbPairs);
		System.out.println("total adj pairs is " + test.totalAdjPairs);
		System.out.println("total adv pairs is " + test.totalAdvPairs);
		System.out.println("Work completed!");
	}


}
