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

public class CrossValidationPromotedPMIEvaluation {

	public BufferedReader synonymReader;
	String infileName;
	int size;
	private PrintWriter individualOut;
	private PrintWriter statPrecisionAllOut;
	private PrintWriter statPrecision1SenseOut;
	private PrintWriter statPrecision1SynonymOut;
	private PrintWriter statRecallAllOut;
	private PrintWriter statRecall1SenseOut;
	private PrintWriter statRecall1SynonymOut;
	public CoOccurModelByArrays model;
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
	
	
	//for cross-validation
	int fold = 2;
	
	public double totalRecall10All;
	public double totalRecall25All;
	public double totalRecall50All;
	public double totalRecall100All;
	public double totalRecall200All;
	public double totalRecall500All;
	
	public double totalAverageRank10All;
	public double totalAverageRank25All;
	public double totalAverageRank50All;
	public double totalAverageRank100All;
	public double totalAverageRank200All;
	public double totalAverageRank500All;

	public double totalRecall10Unique;
	public double totalRecall25Unique;
	public double totalRecall50Unique;
	public double totalRecall100Unique;
	public double totalRecall200Unique;
	public double totalRecall500Unique;
	
	public double totalAverageRank10Unique;
	public double totalAverageRank25Unique;
	public double totalAverageRank50Unique;
	public double totalAverageRank100Unique;
	public double totalAverageRank200Unique;
	public double totalAverageRank500Unique;

	public double totalRecall10FirstSynonym;
	public double totalRecall25FirstSynonym;
	public double totalRecall50FirstSynonym;
	public double totalRecall100FirstSynonym;
	public double totalRecall200FirstSynonym;
	public double totalRecall500FirstSynonym;
	
	public double totalAverageRank10FirstSynonym;
	public double totalAverageRank25FirstSynonym;
	public double totalAverageRank50FirstSynonym;
	public double totalAverageRank100FirstSynonym;
	public double totalAverageRank200FirstSynonym;
	public double totalAverageRank500FirstSynonym;

	public HashSet<Integer> sample;
	
	
	
	public CrossValidationPromotedPMIEvaluation(String InfileName, String pos) {
		// TODO Auto-generated constructor stub
		try{
			File synonymCollection = new File(InfileName);
			synonymReader = new BufferedReader(new FileReader(synonymCollection));
			POS = pos;
			
			infileName = InfileName;
			
			statPrecisionAllOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_cross_pr_all.txt", true));
			statPrecision1SenseOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_cross_pr_1sense.txt", true));
			statPrecision1SynonymOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_cross_pr_1synonym.txt", true));
			
			statRecallAllOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_cross_re_all.txt", true));
			statRecall1SenseOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_cross_re_1sense.txt", true));
			statRecall1SynonymOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_cross_re_1synonym.txt", true));
			
			printSum = false;

			
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
	
	public void getFirstAndBestRanks() throws IOException{
		
		String textLine;
		FileWriter resultFile = new FileWriter(infileName + ".rst" + size);
		individualOut = new PrintWriter(new BufferedWriter(resultFile));
		missingFirstGiven = 0;
		missingFirstEncountered = 0;
		noFirst = 0;

		while ((textLine = synonymReader.readLine()) != null){

			int firstGivenSynonymRank = 0;
			int firstEncounteredSynonymRank = 0;

			if (textLine.trim().equals(""))
				continue;
			
			TestEntryWrapper entry = new TestEntryWrapper(textLine);
			
			if (model.getFrequency(entry.targetWord + "_" + POS) >= 10000){
				
				FloatElement[] candidates = model.getPromotedPMIWordsWithSamePOS(entry.targetWord + "_" + POS);
				
				boolean haveFirstEncountered = false;
				boolean haveFirstGiven = false;
				
				for (int i=0; i<candidates.length; i++){
					
					String word = candidates[i].word;
					
					if (!haveFirstGiven && entry.firstSynonym == null){
						haveFirstGiven = true;
						noFirst ++;
					}
					
					if (!haveFirstGiven && word.equals(entry.firstSynonym + "_" + POS)){
						firstGivenSynonymRank = i + 1;
						haveFirstGiven = true;
					}
					
					if (!haveFirstEncountered && entry.allSynonyms.contains( word.substring(0, word.length() - 3))){
						firstEncounteredSynonymRank = i + 1;
						haveFirstEncountered = true;
					}
					
					if (haveFirstGiven && haveFirstEncountered)
						break;
				}
				
				if (!haveFirstEncountered){
					missingFirstEncountered ++;
					System.out.println(entry.targetWord + " has no matching first encountered!");
				}
				
				if (!haveFirstGiven){
					missingFirstGiven ++;
					System.out.println(entry.targetWord + " has no matching first given!");
				}
				
			}
			
			if (entry.targetWord.length() < 8)
				individualOut.println(entry.targetWord + "\t\t" + firstGivenSynonymRank + "\t" + firstEncounteredSynonymRank);
			else
				individualOut.println(entry.targetWord + "\t" + firstGivenSynonymRank + "\t" + firstEncounteredSynonymRank);
		}
		
		individualOut.close();
		System.out.println("noFirst: " + noFirst);
		System.out.println("missingFirstEncountered: " + missingFirstEncountered);
		System.out.println("missingFirstGiven: " + missingFirstGiven);

	}
	
	public void getStatisticForAllSenses(double k, double p, double q, double bsf, int side) throws IOException{
		
		String textLine;
		int entry_no = 0;
		
		while ((textLine = synonymReader.readLine()) != null){

			if (textLine.trim().equals(""))
				continue;

			entry_no ++;
			
			if (side == 0){
				if (!sample.contains(entry_no)) continue;
			}else
				if (sample.contains(entry_no)) continue;
				
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
		
		/*
		statRecallAllOut.println(parameter + "\t" + String.format("%1.2f", sumOfRecall10/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall25/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall50/numberOfEntries) 
				+ "\t" + String.format("%1.2f", sumOfRecall100/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall200/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall500/numberOfEntries));
		
		statPrecisionAllOut.println(parameter + "\t" + String.format("%1.2f", sumOfRanks10/numOfRanks10) + "\t" + String.format("%1.2f", sumOfRanks25/numOfRanks25) + "\t" + String.format("%1.2f", sumOfRanks50/numOfRanks50) + "\t" 
				+ String.format("%1.2f", sumOfRanks100/numOfRanks100) + "\t" + String.format("%1.2f", sumOfRanks200/numOfRanks200) + "\t" + String.format("%1.2f", sumOfRanks500/numOfRanks500));
		*/

		statRecallAllOut.println(parameter + "\t" + sumOfRecall10/numberOfEntries + "\t" + sumOfRecall25/numberOfEntries + "\t" + sumOfRecall50/numberOfEntries 
				+ "\t" + sumOfRecall100/numberOfEntries + "\t" + sumOfRecall200/numberOfEntries + "\t" + sumOfRecall500/numberOfEntries);
		
		statPrecisionAllOut.println(parameter + "\t" + sumOfRanks10/numOfRanks10 + "\t" + sumOfRanks25/numOfRanks25 + "\t" + sumOfRanks50/numOfRanks50 + "\t" 
				+ sumOfRanks100/numOfRanks100 + "\t" + sumOfRanks200/numOfRanks200 + "\t" + sumOfRanks500/numOfRanks500);
	
	
		totalRecall10All += sumOfRecall10/numberOfEntries;
		totalRecall25All += sumOfRecall25/numberOfEntries;
		totalRecall50All += sumOfRecall50/numberOfEntries;
		totalRecall100All += sumOfRecall100/numberOfEntries;
		totalRecall200All += sumOfRecall200/numberOfEntries;
		totalRecall500All += sumOfRecall500/numberOfEntries;
			
		totalAverageRank10All += sumOfRanks10/numOfRanks10;
		totalAverageRank25All += sumOfRanks25/numOfRanks25;
		totalAverageRank50All += sumOfRanks50/numOfRanks50;
		totalAverageRank100All += sumOfRanks100/numOfRanks100;
		totalAverageRank200All += sumOfRanks200/numOfRanks200;
		totalAverageRank500All += sumOfRanks500/numOfRanks500;
			
	}

	public void getStatisticForTheOnlySense(double k, double p, double q, double bsf, int side) throws IOException{
		
		String textLine;
		int entry_no = 0;
		
		while ((textLine = synonymReader.readLine()) != null){

			if (textLine.trim().equals(""))
				continue;
			
			entry_no ++;
			
			if (side == 0){
				if (!sample.contains(entry_no)) continue;
			}else
				if (sample.contains(entry_no)) continue;
			
			TestEntryWrapper entry = new TestEntryWrapper(textLine);
			
			if (model.getFrequency(entry.targetWord + "_" + POS) >= 10000 && entry.numberOfSenses == 1){
				
				FloatElement[] candidates = model.getPromotedPMIWordsWithSamePOS(entry.targetWord + "_" + POS, k, p, q, bsf);

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
		
		
		totalRecall10Unique += sumOfRecall10/numberOfEntries;
		totalRecall25Unique += sumOfRecall25/numberOfEntries;
		totalRecall50Unique += sumOfRecall50/numberOfEntries;
		totalRecall100Unique += sumOfRecall100/numberOfEntries;
		totalRecall200Unique += sumOfRecall200/numberOfEntries;
		totalRecall500Unique += sumOfRecall500/numberOfEntries;
			
		totalAverageRank10Unique += sumOfRanks10/numOfRanks10;
		totalAverageRank25Unique += sumOfRanks25/numOfRanks25;
		totalAverageRank50Unique += sumOfRanks50/numOfRanks50;
		totalAverageRank100Unique += sumOfRanks100/numOfRanks100;
		totalAverageRank200Unique += sumOfRanks200/numOfRanks200;
		totalAverageRank500Unique += sumOfRanks500/numOfRanks500;

	}

	public void getStatisticForFirstSynonymInTheOnlySense(double k, double p, double q, double bsf, int side) throws IOException{
		
		String textLine;
		int entry_no = 0;
		
		while ((textLine = synonymReader.readLine()) != null){

			if (textLine.trim().equals(""))
				continue;
			
			entry_no ++;
			
			if (side == 0){
				if (!sample.contains(entry_no)) continue;
			}else
				if (sample.contains(entry_no)) continue;
			
			TestEntryWrapper entry = new TestEntryWrapper(textLine);
			
			if (model.getFrequency(entry.targetWord + "_" + POS) >= 10000 && entry.numberOfSenses == 1 && entry.firstSynonym != null){
				
				FloatElement[] candidates = model.getPromotedPMIWordsWithSamePOS(entry.targetWord + "_" + POS, k, p, q, bsf);

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
		
		/*
		statRecall1SynonymOut.println(parameter + "\t" + String.format("%1.2f", sumOfRecall10/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall25/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall50/numberOfEntries) 
				+ "\t" + String.format("%1.2f", sumOfRecall100/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall200/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall500/numberOfEntries));
		
		statPrecision1SynonymOut.println(parameter + "\t" + String.format("%1.2f", sumOfRanks10/numOfRanks10) + "\t" + String.format("%1.2f", sumOfRanks25/numOfRanks25) + "\t" + String.format("%1.2f", sumOfRanks50/numOfRanks50) + "\t" 
				+ String.format("%1.2f", sumOfRanks100/numOfRanks100) + "\t" + String.format("%1.2f", sumOfRanks200/numOfRanks200) + "\t" + String.format("%1.2f", sumOfRanks500/numOfRanks500));
		*/
		
		statRecall1SynonymOut.println(parameter + "\t" + sumOfRecall10/numberOfEntries + "\t" + sumOfRecall25/numberOfEntries + "\t" + sumOfRecall50/numberOfEntries 
				+ "\t" + sumOfRecall100/numberOfEntries + "\t" + sumOfRecall200/numberOfEntries + "\t" + sumOfRecall500/numberOfEntries);
		
		statPrecision1SynonymOut.println(parameter + "\t" + sumOfRanks10/numOfRanks10 + "\t" + sumOfRanks25/numOfRanks25 + "\t" + sumOfRanks50/numOfRanks50 + "\t" 
				+ sumOfRanks100/numOfRanks100 + "\t" + sumOfRanks200/numOfRanks200 + "\t" + sumOfRanks500/numOfRanks500);
		
		
		totalRecall10FirstSynonym += sumOfRecall10/numberOfEntries;
		totalRecall25FirstSynonym += sumOfRecall25/numberOfEntries;
		totalRecall50FirstSynonym += sumOfRecall50/numberOfEntries;
		totalRecall100FirstSynonym += sumOfRecall100/numberOfEntries;
		totalRecall200FirstSynonym += sumOfRecall200/numberOfEntries;
		totalRecall500FirstSynonym += sumOfRecall500/numberOfEntries;
			
		totalAverageRank10FirstSynonym += sumOfRanks10/numOfRanks10;
		totalAverageRank25FirstSynonym += sumOfRanks25/numOfRanks25;
		totalAverageRank50FirstSynonym += sumOfRanks50/numOfRanks50;
		totalAverageRank100FirstSynonym += sumOfRanks100/numOfRanks100;
		totalAverageRank200FirstSynonym += sumOfRanks200/numOfRanks200;
		totalAverageRank500FirstSynonym += sumOfRanks500/numOfRanks500;

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
		
		CrossValidationPromotedPMIEvaluation test;
		int totalEntries;
		
		if (args.length > 0){
			
			if (args[0].equals("VB")){
				test = new CrossValidationPromotedPMIEvaluation("/home/lushan1/nlp/evaluation/verbSynonyms700.txt", "VB");
				totalEntries = 1187;
				
			}else if (args[0].equals("NN")){
				test = new CrossValidationPromotedPMIEvaluation("/home/lushan1/nlp/evaluation/nounSynonyms700.txt", "NN");
				totalEntries = 2286;
				
			}else if (args[0].equals("JJ")){
				test = new CrossValidationPromotedPMIEvaluation("/home/lushan1/nlp/evaluation/adjSynonyms700.txt", "JJ");
				totalEntries = 1015;
				
			}else if (args[0].equals("RB")){
				test = new CrossValidationPromotedPMIEvaluation("/home/lushan1/nlp/evaluation/advSynonyms700.txt", "RB");
				totalEntries = 39;
			}else{
				throw new Exception("Wrong parameter: " + args[0]);
			}
			
		}else{
			test = new CrossValidationPromotedPMIEvaluation("/home/lushan1/nlp/evaluation/adjSynonyms700.txt", "JJ");
			totalEntries = 1015;
		}
		
		test.set("/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW", 41, 700, 0 );
		
		Random generator = new Random(0);
		test.sample = new HashSet<Integer>();
		
		while (test.sample.size() < (totalEntries / 2)){
			int r = generator.nextInt(totalEntries) + 1;
			test.sample.add(r);
		}

		
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
		
		double p = 5;
		double q = 4;
		double k = 70;
		int datasetNo = 0;
		
		test.parameter = k + "-" + p + "-" + q + "-" + bsf;

		
		test.getStatisticForFirstSynonymInTheOnlySense(k, p, q, bsf, datasetNo);
		test.reset();

		test.getStatisticForTheOnlySense(k, p, q, bsf, datasetNo);
		test.reset();
		
		
		test.getStatisticForAllSenses(k, p, q, bsf, datasetNo);
		test.reset();
		
		//test.clear();

		System.out.println(datasetNo + " is done.");
		
		p = 2;
		q = -4;
		k = 70;
		datasetNo = 1;
		
		test.parameter = k + "-" + p + "-" + q + "-" + bsf;

		
		test.getStatisticForFirstSynonymInTheOnlySense(k, p, q, bsf, datasetNo);
		test.reset();

		test.getStatisticForTheOnlySense(k, p, q, bsf, datasetNo);
		test.reset();
		
		
		test.getStatisticForAllSenses(k, p, q, bsf, datasetNo);
		test.reset();
		
		//test.clear();

		System.out.println(datasetNo + " is done.");
		
		test.statRecallAllOut.println("cross result:" + "\t" + String.format("%1.2f", test.totalRecall10All/test.fold) + "\t" + String.format("%1.2f", test.totalRecall25All/test.fold) + "\t" + String.format("%1.2f", test.totalRecall50All/test.fold) 
				+ "\t" + String.format("%1.2f", test.totalRecall100All/test.fold) + "\t" + String.format("%1.2f", test.totalRecall200All/test.fold) + "\t" + String.format("%1.2f", test.totalRecall500All/test.fold));
		
		test.statPrecisionAllOut.println("cross result:" + "\t" + String.format("%1.2f", test.totalAverageRank10All/test.fold) + "\t" + String.format("%1.2f", test.totalAverageRank25All/test.fold) + "\t" + String.format("%1.2f", test.totalAverageRank50All/test.fold) 
				+ "\t" + String.format("%1.2f", test.totalAverageRank100All/test.fold) + "\t" + String.format("%1.2f", test.totalAverageRank200All/test.fold) + "\t" + String.format("%1.2f", test.totalAverageRank500All/test.fold));

		
		test.statRecall1SenseOut.println("cross result:" + "\t" + String.format("%1.2f", test.totalRecall10Unique/test.fold) + "\t" + String.format("%1.2f", test.totalRecall25Unique/test.fold) + "\t" + String.format("%1.2f", test.totalRecall50Unique/test.fold) 
				+ "\t" + String.format("%1.2f", test.totalRecall100Unique/test.fold) + "\t" + String.format("%1.2f", test.totalRecall200Unique/test.fold) + "\t" + String.format("%1.2f", test.totalRecall500Unique/test.fold));
		
		test.statPrecision1SenseOut.println("cross result:" + "\t" + String.format("%1.2f", test.totalAverageRank10Unique/test.fold) + "\t" + String.format("%1.2f", test.totalAverageRank25Unique/test.fold) + "\t" + String.format("%1.2f", test.totalAverageRank50Unique/test.fold) 
				+ "\t" + String.format("%1.2f", test.totalAverageRank100Unique/test.fold) + "\t" + String.format("%1.2f", test.totalAverageRank200Unique/test.fold) + "\t" + String.format("%1.2f", test.totalAverageRank500Unique/test.fold));
		
		test.statRecall1SynonymOut.println("cross result:" + "\t" + String.format("%1.2f", test.totalRecall10FirstSynonym/test.fold) + "\t" + String.format("%1.2f", test.totalRecall25FirstSynonym/test.fold) + "\t" + String.format("%1.2f", test.totalRecall50FirstSynonym/test.fold) 
				+ "\t" + String.format("%1.2f", test.totalRecall100FirstSynonym/test.fold) + "\t" + String.format("%1.2f", test.totalRecall200FirstSynonym/test.fold) + "\t" + String.format("%1.2f", test.totalRecall500FirstSynonym/test.fold));
		
		test.statPrecision1SynonymOut.println("cross result:" + "\t" + String.format("%1.2f", test.totalAverageRank10FirstSynonym/test.fold) + "\t" + String.format("%1.2f", test.totalAverageRank25FirstSynonym/test.fold) + "\t" + String.format("%1.2f", test.totalAverageRank50FirstSynonym/test.fold) 
				+ "\t" + String.format("%1.2f", test.totalAverageRank100FirstSynonym/test.fold) + "\t" + String.format("%1.2f", test.totalAverageRank200FirstSynonym/test.fold) + "\t" + String.format("%1.2f", test.totalAverageRank500FirstSynonym/test.fold));
		
		
		test.close();
		
		System.out.println("Work Completed!");
		long endTime = System.currentTimeMillis();
		long elipsedTime = endTime - startTime;
		System.out.println(elipsedTime + " milli seconds have been taken.");

	}

}
