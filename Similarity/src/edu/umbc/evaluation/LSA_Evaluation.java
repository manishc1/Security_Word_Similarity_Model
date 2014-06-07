package edu.umbc.evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Vector;

import edu.umbc.wordSimilarity2.FloatElement;
import edu.umbc.wordSimilarity2.CoOccurModelByArrays;
import edu.umbc.wordSimilarity2.LSA_Model;
import edu.umbc.wordSimilarity2.Utility;

public class LSA_Evaluation {

	public BufferedReader synonymReader;
	String infileName;
	//int size;
	private PrintWriter statPrecisionAllOut;
	private PrintWriter statPrecision1SenseOut;
	private PrintWriter statPrecision1SynonymOut;
	private PrintWriter statRecallAllOut;
	private PrintWriter statRecall1SenseOut;
	private PrintWriter statRecall1SynonymOut;
	public LSA_Model model;
	String POS;
	public int missingFirstGiven;
	public int missingFirstEncountered;
	public int noFirst;
	public double sumOfRecall10;
	public double sumOfRecall25;
	public double sumOfRecall50;
	public double sumOfRecall100;
	public double sumOfRecall200;
	public double sumOfRecall500;
	public int numberOfEntries;
	public int numberOfPairs;
	public double sumOfRanks10;
	public double sumOfRanks25;
	public double sumOfRanks50;
	public double sumOfRanks100;
	public double sumOfRanks200;
	public double sumOfRanks500;
	public int numOfRanks10;
	public int numOfRanks25;
	public int numOfRanks50;
	public int numOfRanks100;
	public int numOfRanks200;
	public int numOfRanks500;
	public boolean printSum;
	public String parameter;
	
	
	
	public LSA_Evaluation(String InfileName, String pos) {
		// TODO Auto-generated constructor stub
		try{
			File synonymCollection = new File(InfileName);
			synonymReader = new BufferedReader(new FileReader(synonymCollection));
			POS = pos;
			
			infileName = InfileName;
			
			statPrecisionAllOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_lsa_pr_all.txt", true));
			statPrecision1SenseOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_lsa_pr_1sense.txt", true));
			statPrecision1SynonymOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_lsa_pr_1synonym.txt", true));
			
			statRecallAllOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_lsa_re_all.txt", true));
			statRecall1SenseOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_lsa_re_1sense.txt", true));
			statRecall1SynonymOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_lsa_re_1synonym.txt", true));
			
			printSum = true;

			
		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
			System.exit(-1);
		}

	}
	
	public void set(String modelname, int freThreshold){
		
		model = new LSA_Model(modelname, freThreshold);
		
	}
	
	
	public void reset() throws IOException{
		
		sumOfRecall10 = 0;
		sumOfRecall25 = 0;
		sumOfRecall50 = 0;
		sumOfRecall100 = 0;
		sumOfRecall200 = 0;
		sumOfRecall500 = 0;
		
		numberOfEntries = 0;
		numberOfPairs = 0;
		
		sumOfRanks10 = 0;
		sumOfRanks25 = 0;
		sumOfRanks50 = 0;
		sumOfRanks100 = 0;
		sumOfRanks200 = 0;
		sumOfRanks500 = 0;
		
		numOfRanks10 = 0;
		numOfRanks25 = 0;
		numOfRanks50 = 0;
		numOfRanks100 = 0;
		numOfRanks200 = 0;
		numOfRanks500 = 0;
		
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
	
	
	public void getStatisticForAllSenses() throws IOException{
		
		String textLine;
		
		while ((textLine = synonymReader.readLine()) != null){

			if (textLine.trim().equals(""))
				continue;
			
			TestEntryWrapper entry = new TestEntryWrapper(textLine);
			
			if (model.getFrequency(entry.targetWord + "_" + POS) >= 10000){
				
				FloatElement[] candidates = model.getSortedWordsByCosineSimWithPOS(entry.targetWord + "_" + POS);

				Vector<Integer> ranks = new Vector<Integer>();
				
				for (int i=0; i<candidates.length; i++){
					
					String word = candidates[i].word;
					
					if (entry.allSynonyms.contains( word.substring(0, word.length() - 3)))
						ranks.add(i + 1);
				}

				Collections.sort(ranks);
		
				int numOfRanksIn10 = 0;
				int numOfRanksIn25 = 0;
				int numOfRanksIn50 = 0;
				int numOfRanksIn100 = 0;
				int numOfRanksIn200 = 0;
				int numOfRanksIn500 = 0;
 				
				for (int i=0; i<ranks.size(); i++){
					
					if (ranks.elementAt(i) <= 10){
						sumOfRanks10 += ranks.elementAt(i);
						numOfRanksIn10 ++;
					}
					
					if (ranks.elementAt(i) <= 25){
						sumOfRanks25 += ranks.elementAt(i);
						numOfRanksIn25 ++;
					}

					if (ranks.elementAt(i) <= 50){
						sumOfRanks50 += ranks.elementAt(i);
						numOfRanksIn50 ++;
					}
					
					if (ranks.elementAt(i) <= 100){
						sumOfRanks100 += ranks.elementAt(i);
						numOfRanksIn100 ++;
					}

					if (ranks.elementAt(i) <= 200){
						sumOfRanks200 += ranks.elementAt(i);
						numOfRanksIn200 ++;
					}

					if (ranks.elementAt(i) <= 500){
						sumOfRanks500 += ranks.elementAt(i);
						numOfRanksIn500 ++;
					}
				}
				
				numberOfEntries ++;
				numberOfPairs += entry.allSynonyms.size();
				
				sumOfRecall10 += (double) numOfRanksIn10 / entry.allSynonyms.size();
				sumOfRecall25 += (double) numOfRanksIn25 / entry.allSynonyms.size();
				sumOfRecall50 += (double) numOfRanksIn50 / entry.allSynonyms.size();
				sumOfRecall100 += (double) numOfRanksIn100 / entry.allSynonyms.size();
				sumOfRecall200 += (double) numOfRanksIn200 / entry.allSynonyms.size();
				sumOfRecall500 += (double) numOfRanksIn500 / entry.allSynonyms.size();
				
				numOfRanks10 += numOfRanksIn10;
				numOfRanks25 += numOfRanksIn25;
				numOfRanks50 += numOfRanksIn50;
				numOfRanks100 += numOfRanksIn100;
				numOfRanks200 += numOfRanksIn200;
				numOfRanks500 += numOfRanksIn500;
				
			}
		}
		
		if (printSum){
			statRecallAllOut.println("The total number of valid entries is " + numberOfEntries);
			statRecallAllOut.println("The total number of synonym pairs is " + numberOfPairs);
	
			statPrecisionAllOut.println("The total number of valid entries is " + numberOfEntries);
			statPrecisionAllOut.println("The total number of synonym pairs is " + numberOfPairs);
		}
		
		statRecallAllOut.println(parameter + "\t" + String.format("%1.2f", sumOfRecall10/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall25/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall50/numberOfEntries) 
				+ "\t" + String.format("%1.2f", sumOfRecall100/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall200/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall500/numberOfEntries));
		
		statPrecisionAllOut.println(parameter + "\t" + String.format("%1.2f", sumOfRanks10/numOfRanks10) + "\t" + String.format("%1.2f", sumOfRanks25/numOfRanks25) + "\t" + String.format("%1.2f", sumOfRanks50/numOfRanks50) + "\t" 
				+ String.format("%1.2f", sumOfRanks100/numOfRanks100) + "\t" + String.format("%1.2f", sumOfRanks200/numOfRanks200) + "\t" + String.format("%1.2f", sumOfRanks500/numOfRanks500));

		/*
		statRecallAllOut.println(Utility.A + "\t" + sumOfRecall10/numberOfEntries + "\t" + sumOfRecall25/numberOfEntries + "\t" + sumOfRecall50/numberOfEntries 
				+ "\t" + sumOfRecall100/numberOfEntries + "\t" + sumOfRecall200/numberOfEntries + "\t" + sumOfRecall500/numberOfEntries);
		
		statPrecisionAllOut.println(Utility.A + "\t" + sumOfRanks10/numOfRanks10 + "\t" + sumOfRanks25/numOfRanks25 + "\t" + sumOfRanks50/numOfRanks50 + "\t" 
				+ sumOfRanks100/numOfRanks100 + "\t" + sumOfRanks200/numOfRanks200 + "\t" + sumOfRanks500/numOfRanks500);
		*/
	}

	public void getStatisticForTheOnlySense() throws IOException{
		
		String textLine;
		
		while ((textLine = synonymReader.readLine()) != null){

			if (textLine.trim().equals(""))
				continue;
			
			TestEntryWrapper entry = new TestEntryWrapper(textLine);
			
			if (model.getFrequency(entry.targetWord + "_" + POS) >= 10000 && entry.numberOfSenses == 1){
				
				FloatElement[] candidates = model.getSortedWordsByCosineSimWithPOS(entry.targetWord + "_" + POS);

				Vector<Integer> ranks = new Vector<Integer>();
				
				for (int i=0; i<candidates.length; i++){
					
					String word = candidates[i].word;
					
					if (entry.synonymsInFirstSense.contains( word.substring(0, word.length() - 3)))
						ranks.add(i + 1);
				}

				Collections.sort(ranks);
		
				int numOfRanksIn10 = 0;
				int numOfRanksIn25 = 0;
				int numOfRanksIn50 = 0;
				int numOfRanksIn100 = 0;
				int numOfRanksIn200 = 0;
				int numOfRanksIn500 = 0;
 				
				for (int i=0; i<ranks.size(); i++){
					
					if (ranks.elementAt(i) <= 10){
						sumOfRanks10 += ranks.elementAt(i);
						numOfRanksIn10 ++;
					}
					
					if (ranks.elementAt(i) <= 25){
						sumOfRanks25 += ranks.elementAt(i);
						numOfRanksIn25 ++;
					}

					if (ranks.elementAt(i) <= 50){
						sumOfRanks50 += ranks.elementAt(i);
						numOfRanksIn50 ++;
					}
					
					if (ranks.elementAt(i) <= 100){
						sumOfRanks100 += ranks.elementAt(i);
						numOfRanksIn100 ++;
					}

					if (ranks.elementAt(i) <= 200){
						sumOfRanks200 += ranks.elementAt(i);
						numOfRanksIn200 ++;
					}

					if (ranks.elementAt(i) <= 500){
						sumOfRanks500 += ranks.elementAt(i);
						numOfRanksIn500 ++;
					}
				}
				
				numberOfEntries ++;
				numberOfPairs += entry.synonymsInFirstSense.size();
				
				sumOfRecall10 += (double) numOfRanksIn10 / entry.synonymsInFirstSense.size();
				sumOfRecall25 += (double) numOfRanksIn25 / entry.synonymsInFirstSense.size();
				sumOfRecall50 += (double) numOfRanksIn50 / entry.synonymsInFirstSense.size();
				sumOfRecall100 += (double) numOfRanksIn100 / entry.synonymsInFirstSense.size();
				sumOfRecall200 += (double) numOfRanksIn200 / entry.synonymsInFirstSense.size();
				sumOfRecall500 += (double) numOfRanksIn500 / entry.synonymsInFirstSense.size();
				
				numOfRanks10 += numOfRanksIn10;
				numOfRanks25 += numOfRanksIn25;
				numOfRanks50 += numOfRanksIn50;
				numOfRanks100 += numOfRanksIn100;
				numOfRanks200 += numOfRanksIn200;
				numOfRanks500 += numOfRanksIn500;
				
			}
		}
		
		if (printSum){
			statRecall1SenseOut.println("The total number of valid entries is " + numberOfEntries);
			statRecall1SenseOut.println("The total number of synonym pairs is " + numberOfPairs);
	
			statPrecision1SenseOut.println("The total number of valid entries is " + numberOfEntries);
			statPrecision1SenseOut.println("The total number of synonym pairs is " + numberOfPairs);
		}

		/*
		statRecall1SenseOut.println(parameter + "\t" + String.format("%1.2f", sumOfRecall10/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall25/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall50/numberOfEntries) 
				+ "\t" + String.format("%1.2f", sumOfRecall100/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall200/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall500/numberOfEntries));
		
		statPrecision1SenseOut.println(parameter + "\t" + String.format("%1.2f", sumOfRanks10/numOfRanks10) + "\t" + String.format("%1.2f", sumOfRanks25/numOfRanks25) + "\t" + String.format("%1.2f", sumOfRanks50/numOfRanks50) + "\t" 
				+ String.format("%1.2f", sumOfRanks100/numOfRanks100) + "\t" + String.format("%1.2f", sumOfRanks200/numOfRanks200) + "\t" + String.format("%1.2f", sumOfRanks500/numOfRanks500));
		*/

		statRecall1SenseOut.println(parameter + "\t" + sumOfRecall10/numberOfEntries + "\t" + sumOfRecall25/numberOfEntries + "\t" + sumOfRecall50/numberOfEntries 
				+ "\t" + sumOfRecall100/numberOfEntries + "\t" + sumOfRecall200/numberOfEntries + "\t" + sumOfRecall500/numberOfEntries);
		
		statPrecision1SenseOut.println(parameter + "\t" + sumOfRanks10/numOfRanks10 + "\t" + sumOfRanks25/numOfRanks25 + "\t" + sumOfRanks50/numOfRanks50 + "\t" 
				+ sumOfRanks100/numOfRanks100 + "\t" + sumOfRanks200/numOfRanks200 + "\t" + sumOfRanks500/numOfRanks500);
		
	}

	public void getStatisticForFirstSynonymInTheOnlySense() throws IOException{
		
		String textLine;
		
		while ((textLine = synonymReader.readLine()) != null){

			if (textLine.trim().equals(""))
				continue;
			
			TestEntryWrapper entry = new TestEntryWrapper(textLine);
			
			if (model.getFrequency(entry.targetWord + "_" + POS) >= 10000 && entry.numberOfSenses == 1 && entry.firstSynonym != null){
				
				FloatElement[] candidates = model.getSortedWordsByCosineSimWithPOS(entry.targetWord + "_" + POS);

				int rank = 0;
				
				for (int i=0; i<candidates.length; i++){
					
					String word = candidates[i].word;
					
					if (entry.firstSynonym.equals(word.substring(0, word.length() - 3))){
						rank = i + 1;
						break;
					}
				}

				int numOfRanksIn10 = 0;
				int numOfRanksIn25 = 0;
				int numOfRanksIn50 = 0;
				int numOfRanksIn100 = 0;
				int numOfRanksIn200 = 0;
				int numOfRanksIn500 = 0;
 				
				if (rank <= 10 && rank > 0){
					sumOfRanks10 += rank;
					numOfRanksIn10 ++;
				}
				
				if (rank <= 25 && rank > 0){
					sumOfRanks25 += rank;
					numOfRanksIn25 ++;
				}

				if (rank <= 50 && rank > 0){
					sumOfRanks50 += rank;
					numOfRanksIn50 ++;
				}
				
				if (rank <= 100 && rank > 0){
					sumOfRanks100 += rank;
					numOfRanksIn100 ++;
				}

				if (rank <= 200 && rank > 0){
					sumOfRanks200 += rank;
					numOfRanksIn200 ++;
				}

				if (rank <= 500 && rank > 0){
					sumOfRanks500 += rank;
					numOfRanksIn500 ++;
				}
				
				numberOfEntries ++;
				numberOfPairs ++;
				
				sumOfRecall10 += (double) numOfRanksIn10 ;
				sumOfRecall25 += (double) numOfRanksIn25 ;
				sumOfRecall50 += (double) numOfRanksIn50 ;
				sumOfRecall100 += (double) numOfRanksIn100 ;
				sumOfRecall200 += (double) numOfRanksIn200 ;
				sumOfRecall500 += (double) numOfRanksIn500 ;
				
				numOfRanks10 += numOfRanksIn10;
				numOfRanks25 += numOfRanksIn25;
				numOfRanks50 += numOfRanksIn50;
				numOfRanks100 += numOfRanksIn100;
				numOfRanks200 += numOfRanksIn200;
				numOfRanks500 += numOfRanksIn500;
				
			}
		}
		
		if (printSum){
			statRecall1SynonymOut.println("The total number of valid entries is " + numberOfEntries);
			statRecall1SynonymOut.println("The total number of synonym pairs is " + numberOfPairs);
	
			statPrecision1SynonymOut.println("The total number of valid entries is " + numberOfEntries);
			statPrecision1SynonymOut.println("The total number of synonym pairs is " + numberOfPairs);
		}
		
		statRecall1SynonymOut.println(parameter + "\t" + String.format("%1.2f", sumOfRecall10/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall25/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall50/numberOfEntries) 
				+ "\t" + String.format("%1.2f", sumOfRecall100/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall200/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall500/numberOfEntries));
		
		statPrecision1SynonymOut.println(parameter + "\t" + String.format("%1.2f", sumOfRanks10/numOfRanks10) + "\t" + String.format("%1.2f", sumOfRanks25/numOfRanks25) + "\t" + String.format("%1.2f", sumOfRanks50/numOfRanks50) + "\t" 
				+ String.format("%1.2f", sumOfRanks100/numOfRanks100) + "\t" + String.format("%1.2f", sumOfRanks200/numOfRanks200) + "\t" + String.format("%1.2f", sumOfRanks500/numOfRanks500));

		
		/*
		statRecall1SynonymOut.println(Utility.A + "\t" + sumOfRecall10/numberOfEntries + "\t" + sumOfRecall25/numberOfEntries + "\t" + sumOfRecall50/numberOfEntries 
				+ "\t" + sumOfRecall100/numberOfEntries + "\t" + sumOfRecall200/numberOfEntries + "\t" + sumOfRecall500/numberOfEntries);
		
		statPrecision1SynonymOut.println(Utility.A + "\t" + sumOfRanks10/numOfRanks10 + "\t" + sumOfRanks25/numOfRanks25 + "\t" + sumOfRanks50/numOfRanks50 + "\t" 
				+ sumOfRanks100/numOfRanks100 + "\t" + sumOfRanks200/numOfRanks200 + "\t" + sumOfRanks500/numOfRanks500);
		*/
	}

	
	public void close() throws IOException{
		synonymReader.close();
		statPrecisionAllOut.close();
		statPrecision1SenseOut.close();
		statPrecision1SynonymOut.close();
		
		statRecallAllOut.close();
		statRecall1SenseOut.close();
		statRecall1SynonymOut.close();
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
		
		LSA_Evaluation test;
		
		if (args.length > 0){
			
			if (args[0].equals("VB"))
				test = new LSA_Evaluation("/home/lushan1/nlp/evaluation/verbSynonyms700.txt", "VB");
			else if (args[0].equals("NN"))
				test = new LSA_Evaluation("/home/lushan1/nlp/evaluation/nounSynonyms700.txt", "NN");
			else if (args[0].equals("JJ"))
				test = new LSA_Evaluation("/home/lushan1/nlp/evaluation/adjSynonyms700.txt", "JJ");
			else if (args[0].equals("RB"))
				test = new LSA_Evaluation("/home/lushan1/nlp/evaluation/advSynonyms700.txt", "RB");
			else{
				throw new Exception("Wrong parameter: " + args[0]);
			}
			
		}else
			test = new LSA_Evaluation("/home/lushan1/nlp/evaluation/nounSynonyms700.txt", "NN");
		
		
		test.set("/home/lushan1/nlp/model/SVD/Gutenberg2010AllW5", 700);
		
			
		test.getStatisticForFirstSynonymInTheOnlySense();
		test.reset();

		test.getStatisticForTheOnlySense();
		test.reset();
		
		test.getStatisticForAllSenses();
		test.reset();
		
		test.clear();
			
		test.close();
		System.out.println("Work Completed!");

	}

}
