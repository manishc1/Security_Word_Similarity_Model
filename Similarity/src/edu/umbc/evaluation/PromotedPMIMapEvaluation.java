package edu.umbc.evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Vector;

import edu.umbc.wordSimilarity2.FloatElement;
import edu.umbc.wordSimilarity2.CoOccurModelByArrays;
import edu.umbc.wordSimilarity2.Utility;

public class PromotedPMIMapEvaluation {

	public BufferedReader synonymReader;
	String infileName;
	int size;
	private PrintWriter statMapAllOut;
	public CoOccurModelByArrays model;
	String POS;
	public int missingFirstGiven;
	public int missingFirstEncountered;
	public int noFirst;
	public double sumOfAveragePrecision;
	public int numberOfEntries;
	public int numberOfPairs;
	public boolean printSum;
	public String parameter;
	
	
	
	public PromotedPMIMapEvaluation(String InfileName, String pos) {
		// TODO Auto-generated constructor stub
		try{
			File synonymCollection = new File(InfileName);
			synonymReader = new BufferedReader(new FileReader(synonymCollection));
			POS = pos;
			
			infileName = InfileName;
			
			statMapAllOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_promoted_map_all.txt", true));
			
			printSum = true;

			
		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
			System.exit(-1);
		}

	}
	
	public void set(String modelname, int p_size, int freThreshold, double conThreshold){
		
		size = p_size;
		model = new CoOccurModelByArrays(modelname + size, false, freThreshold, conThreshold);
		
	}
	
	
	public void reset() throws IOException{
		
		sumOfAveragePrecision = 0;
		
		numberOfEntries = 0;
		numberOfPairs = 0;
		
		synonymReader.close();
		File synonymCollection = new File(infileName);
		synonymReader = new BufferedReader(new FileReader(synonymCollection));
	}
	
	
	public String printPCW(Object[] sortedWords, int size){
		
		StringBuffer temp = new StringBuffer();
		
		for (int i=0; i<size; i++){
			temp.append(sortedWords[i] + ", ");
		}
		
		System.out.println(temp);
		
		return temp.toString();

	}

	public String printPMIs(FloatElement[] sortedWords){
		
		StringBuffer temp = new StringBuffer();

		for (FloatElement word : sortedWords){
			temp.append(word.value + ", ");
		}
		
		System.out.println(temp);
		
		return temp.toString();
	}
	
	public String printWords(FloatElement[] sortedWords, int size){
		
		StringBuffer temp = new StringBuffer();
		
		for (int i=0; i<size; i++){
			temp.append(sortedWords[i].word.substring(0, sortedWords[i].word.length() - 3) + ", ");
		}
		
		System.out.println(temp);
		
		return temp.toString();

	}
	
	public void getStatisticForAllSenses(double k, double p, double q, double bsf) throws IOException{
		
		String textLine;
		int entry_no = 0;
		
		while ((textLine = synonymReader.readLine()) != null){

			if (textLine.trim().equals(""))
				continue;

			entry_no ++;
			
			//if (side == 0){
			//	if (!sample.contains(entry_no)) continue;
			//}else
			//	if (sample.contains(entry_no)) continue;
				
			//if (entry_no % 2 == 0) continue;

			TestEntryWrapper entry = new TestEntryWrapper(textLine);
			
			if (model.getFrequency(entry.targetWord + "_" + POS) >= 10000){
				
				
				FloatElement[] candidates = model.getPromotedPMIWordsWithSamePOS(entry.targetWord + "_" + POS, k, p, q, bsf);

				Vector<Integer> ranks = new Vector<Integer>();
				
				for (int i=0; i<candidates.length; i++){
					
					String word = candidates[i].word;
					
					if (entry.allSynonyms.contains( word.substring(0, word.length() - 3)))
						ranks.add(i + 1);
				}

				Collections.sort(ranks);
		
				/*
				int numberOfSynonymsFound = 0;
				double sumOfPrecision = 0;
				
				for (int i=0; i<ranks.size(); i++){
					
					numberOfSynonymsFound ++;
					sumOfPrecision += ((double) numberOfSynonymsFound) / ranks.elementAt(i);
				}

				double averagePrecision = sumOfPrecision / entry.allSynonyms.size();
				*/
				double averagePrecision = 0;
				
				if (ranks.size() > 0 && ranks.elementAt(0) == 1)
					averagePrecision = 1;

				
				numberOfEntries ++;
				numberOfPairs += entry.allSynonyms.size();
				
				sumOfAveragePrecision  += averagePrecision;
			}
		}
		
		if (printSum){
			statMapAllOut.println("The total number of valid entries is " + numberOfEntries);
			statMapAllOut.println("The total number of synonym pairs is " + numberOfPairs);
		}
		
		statMapAllOut.println(parameter + "\t" + String.format("%1.3f", sumOfAveragePrecision/numberOfEntries));

		
		/*
		statRecallAllOut.println(parameter + "\t" + String.format("%1.2f", sumOfRecall10/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall25/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall50/numberOfEntries) 
				+ "\t" + String.format("%1.2f", sumOfRecall100/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall200/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall500/numberOfEntries));
		
		statPrecisionAllOut.println(parameter + "\t" + String.format("%1.2f", sumOfRanks10/numOfRanks10) + "\t" + String.format("%1.2f", sumOfRanks25/numOfRanks25) + "\t" + String.format("%1.2f", sumOfRanks50/numOfRanks50) + "\t" 
				+ String.format("%1.2f", sumOfRanks100/numOfRanks100) + "\t" + String.format("%1.2f", sumOfRanks200/numOfRanks200) + "\t" + String.format("%1.2f", sumOfRanks500/numOfRanks500));
		*/
	}

	
	public void close() throws IOException{
		synonymReader.close();
		statMapAllOut.close();
	}
	
	public void clear() {
		//model = null;
		//size = 0;
		printSum = false;
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		PromotedPMIMapEvaluation test;
		int totalEntries;
		
		if (args.length > 0){
			
			if (args[0].equals("VB")){
				test = new PromotedPMIMapEvaluation("/home/lushan1/nlp/evaluation/verbSynonyms700.txt", "VB");
				totalEntries = 1187;
				
			}else if (args[0].equals("NN")){
				test = new PromotedPMIMapEvaluation("/home/lushan1/nlp/evaluation/nounSynonyms700.txt", "NN");
				totalEntries = 2286;
				
			}else if (args[0].equals("JJ")){
				test = new PromotedPMIMapEvaluation("/home/lushan1/nlp/evaluation/adjSynonyms700.txt", "JJ");
				totalEntries = 1015;
				
			}else if (args[0].equals("RB")){
				test = new PromotedPMIMapEvaluation("/home/lushan1/nlp/evaluation/advSynonyms700.txt", "RB");
				totalEntries = 39;
			}else{
				throw new Exception("Wrong parameter: " + args[0]);
			}
			
		}else{
			test = new PromotedPMIMapEvaluation("/home/lushan1/nlp/evaluation/nounSynonyms700.txt", "NN");
			totalEntries = 2286;
		}
		
		test.set("/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW", 41, 700, 0 );
		
		
		double bsf = 700;
		long startTime = System.currentTimeMillis();
		
		/*
		for (double p = 0; p <= 10.0; p += 0.5){
			
			for (double q = -6.0; q <= 10.0; q += 1.0){
				
				for (double k = 0; k <= 100; k += 10){

						test.parameter = p + "\t" + q + "\t" + k;

						test.getStatisticForAllSenses(k, p, q, bsf, 1);
						test.reset();
						
						test.clear();
						
						System.out.println(test.parameter + " is done.");
					
					
				}
			}
			
		}
		*/
		
		/*
		//adverb
		double p = 3;
		double q = 0;
		double k = 40;
		
		//adjective
		double p = 2;
		double q = -4;
		double k = 60;

		//verb
		double p = 1.5;
		double q = -5;
		double k = 40;
		*/
		
		//noun
		double p = 1.5;
		double q = -4;
		double k = 30;
		
		test.parameter = k + "-" + p + "-" + q + "-" + bsf;

		test.getStatisticForAllSenses(k, p, q, bsf);
		test.reset();
		
		//test.clear();
		
		System.out.println(test.parameter + " is done.");
			
			
		test.close();
		
		System.out.println("Work Completed!");
		long endTime = System.currentTimeMillis();
		long elipsedTime = endTime - startTime;
		System.out.println(elipsedTime + " milli seconds have been taken.");

	}

}
