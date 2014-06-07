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
import edu.umbc.wordSimilarity2.PPMIModelByArrays;
import edu.umbc.wordSimilarity2.Utility;

public class PromotedPMI_PPMIC_Evaluation {

	public BufferedReader synonymReader;
	String infileName;
	//int size;
	//private PrintWriter individualOut;
	private PrintWriter statPrecisionAllOut;
	private PrintWriter statPrecision1SenseOut;
	private PrintWriter statPrecision1SynonymOut;
	private PrintWriter statRecallAllOut;
	private PrintWriter statRecall1SenseOut;
	private PrintWriter statRecall1SynonymOut;
	public CoOccurModelByArrays model;
	public PPMIModelByArrays pmi_model;
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
	
	
	
	public PromotedPMI_PPMIC_Evaluation(String InfileName, String pos) {
		// TODO Auto-generated constructor stub
		try{
			File synonymCollection = new File(InfileName);
			synonymReader = new BufferedReader(new FileReader(synonymCollection));
			POS = pos;
			
			infileName = InfileName;
			
			statPrecisionAllOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_promoted_ppmic_pr_all.txt", true));
			statPrecision1SenseOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_promoted_ppmic_pr_1sense.txt", true));
			statPrecision1SynonymOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_promoted_ppmic_pr_1synonym.txt", true));
			
			statRecallAllOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_promoted_ppmic_re_all.txt", true));
			statRecall1SenseOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_promoted_ppmic_re_1sense.txt", true));
			statRecall1SynonymOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_promoted_ppmic_re_1synonym.txt", true));
			
			printSum = false;

			
		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
			System.exit(-1);
		}

	}
	
	public void set(String modelname1, int p_size1, String modelname2, int p_size2, int freThreshold, double conThreshold){
		
		//size = p_size;
		model = new CoOccurModelByArrays(modelname1 + p_size1, false, freThreshold, conThreshold);
		pmi_model = new PPMIModelByArrays(modelname2 + p_size2, false, freThreshold);
		
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
	
	
	public String[] getIntersection(FloatElement[] filterlist, int filter_length, FloatElement[] baselist, int base_length){
		
		Vector<String> result = new Vector<String>();
		
		HashSet<String> elementSet1 = new HashSet<String>();
		
		for (int i=0; i < filter_length && i <filterlist.length; i++){
			elementSet1.add(filterlist[i].word);
		}

		for (int i=0; i < base_length && i <baselist.length; i++){
			if (elementSet1.contains(baselist[i].word)){
				result.add(baselist[i].word);
			}
		}
		
		return result.toArray(new String[result.size()]);
	}

	
	
	public void getStatisticForAllSenses(double k, double p, double q, double bsf, int pmi_max_length, int ppmic_length) throws IOException{
		
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
				
				
				//FloatElement[] candidates = model.getPromotedPMIWordsWithSamePOS(entry.targetWord + "_" + POS, k, p, q, bsf);
				String[] candidates;
				if (POS.equals("NN"))
					candidates = getIntersection(model.getPromotedPMIWordsWithSamePOS(entry.targetWord + "_" + POS, k, p, q, bsf), pmi_max_length, pmi_model.getDistributionalSimilarWordsWithSamePOS(entry.targetWord + "_" + POS), ppmic_length);
				else
					candidates = getIntersection(pmi_model.getDistributionalSimilarWordsWithSamePOS(entry.targetWord + "_" + POS), ppmic_length, model.getPromotedPMIWordsWithSamePOS(entry.targetWord + "_" + POS, k, p, q, bsf), pmi_max_length);

				Vector<Integer> ranks = new Vector<Integer>();
				
				for (int i=0; i<candidates.length; i++){
					
					String word = candidates[i];
					
					if (entry.allSynonyms.contains( word.substring(0, word.length() - 3)))
						ranks.add(i + 1);
				}

				Collections.sort(ranks);
		
				int numOfRanksIn10 = 0;
				int numOfRanksIn25 = 0;
				//int numOfRanksIn50 = 0;
				//int numOfRanksIn100 = 0;
				//int numOfRanksIn200 = 0;
				//int numOfRanksIn500 = 0;
 				
				for (int i=0; i<ranks.size(); i++){
					
					if (ranks.elementAt(i) <= 10){
						sumOfRanks10 += ranks.elementAt(i);
						numOfRanksIn10 ++;
					}
					
					if (ranks.elementAt(i) <= 25){
						sumOfRanks25 += ranks.elementAt(i);
						numOfRanksIn25 ++;
					}

					/*
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
					*/
				}
				
				numberOfEntries ++;
				numberOfPairs += entry.allSynonyms.size();
				
				sumOfRecall10 += (double) numOfRanksIn10 / entry.allSynonyms.size();
				sumOfRecall25 += (double) numOfRanksIn25 / entry.allSynonyms.size();
				//sumOfRecall50 += (double) numOfRanksIn50 / entry.allSynonyms.size();
				//sumOfRecall100 += (double) numOfRanksIn100 / entry.allSynonyms.size();
				//sumOfRecall200 += (double) numOfRanksIn200 / entry.allSynonyms.size();
				//sumOfRecall500 += (double) numOfRanksIn500 / entry.allSynonyms.size();
				
				numOfRanks10 += numOfRanksIn10;
				numOfRanks25 += numOfRanksIn25;
				//numOfRanks50 += numOfRanksIn50;
				//numOfRanks100 += numOfRanksIn100;
				//numOfRanks200 += numOfRanksIn200;
				//numOfRanks500 += numOfRanksIn500;
				
			}
		}
		
		if (printSum){
			statRecallAllOut.println("The total number of valid entries is " + numberOfEntries);
			statRecallAllOut.println("The total number of synonym pairs is " + numberOfPairs);
	
			statPrecisionAllOut.println("The total number of valid entries is " + numberOfEntries);
			statPrecisionAllOut.println("The total number of synonym pairs is " + numberOfPairs);
		}
		
		statRecallAllOut.println(parameter + "\t" + String.format("%1.2f", sumOfRecall10/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall25/numberOfEntries) /* + "\t" + String.format("%1.2f", sumOfRecall50/numberOfEntries) 
				+ "\t" + String.format("%1.2f", sumOfRecall100/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall200/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall500/numberOfEntries) */);
		
		statPrecisionAllOut.println(parameter + "\t" + String.format("%1.2f", sumOfRanks10/numOfRanks10) + "\t" + String.format("%1.2f", sumOfRanks25/numOfRanks25) /* + "\t" + String.format("%1.2f", sumOfRanks50/numOfRanks50) + "\t" 
				+ String.format("%1.2f", sumOfRanks100/numOfRanks100) + "\t" + String.format("%1.2f", sumOfRanks200/numOfRanks200) + "\t" + String.format("%1.2f", sumOfRanks500/numOfRanks500) */);

		
		/*
		statRecallAllOut.println(parameter + "\t" + sumOfRecall10/numberOfEntries + "\t" + sumOfRecall25/numberOfEntries  + "\t" + sumOfRecall50/numberOfEntries 
				+ "\t" + sumOfRecall100/numberOfEntries + "\t" + sumOfRecall200/numberOfEntries + "\t" + sumOfRecall500/numberOfEntries);
		
		statPrecisionAllOut.println(parameter + "\t" + sumOfRanks10/numOfRanks10 + "\t" + sumOfRanks25/numOfRanks25  + "\t" + sumOfRanks50/numOfRanks50 + "\t" 
				+ sumOfRanks100/numOfRanks100 + "\t" + sumOfRanks200/numOfRanks200 + "\t" + sumOfRanks500/numOfRanks500 );
		*/
	}

	public void getStatisticForTheOnlySense(double k, double p, double q, double bsf, int pmi_max_length, int ppmic_length) throws IOException{
		
		String textLine;
		
		while ((textLine = synonymReader.readLine()) != null){

			if (textLine.trim().equals(""))
				continue;
			
			TestEntryWrapper entry = new TestEntryWrapper(textLine);
			
			if (model.getFrequency(entry.targetWord + "_" + POS) >= 10000 && entry.numberOfSenses == 1){
				
				//FloatElement[] candidates = model.getPromotedPMIWordsWithSamePOS(entry.targetWord + "_" + POS, k, p, q, bsf);
				String[] candidates;
				if (POS.equals("NN"))
					candidates = getIntersection(model.getPromotedPMIWordsWithSamePOS(entry.targetWord + "_" + POS, k, p, q, bsf), pmi_max_length, pmi_model.getDistributionalSimilarWordsWithSamePOS(entry.targetWord + "_" + POS), ppmic_length);
				else
					candidates = getIntersection(pmi_model.getDistributionalSimilarWordsWithSamePOS(entry.targetWord + "_" + POS), ppmic_length, model.getPromotedPMIWordsWithSamePOS(entry.targetWord + "_" + POS, k, p, q, bsf), pmi_max_length);

				
				Vector<Integer> ranks = new Vector<Integer>();
				
				for (int i=0; i<candidates.length; i++){
					
					String word = candidates[i];
					
					if (entry.synonymsInFirstSense.contains( word.substring(0, word.length() - 3)))
						ranks.add(i + 1);
				}

				Collections.sort(ranks);
		
				int numOfRanksIn10 = 0;
				int numOfRanksIn25 = 0;
				//int numOfRanksIn50 = 0;
				//int numOfRanksIn100 = 0;
				//int numOfRanksIn200 = 0;
				//int numOfRanksIn500 = 0;
 				
				for (int i=0; i<ranks.size(); i++){
					
					if (ranks.elementAt(i) <= 10){
						sumOfRanks10 += ranks.elementAt(i);
						numOfRanksIn10 ++;
					}
					
					if (ranks.elementAt(i) <= 25){
						sumOfRanks25 += ranks.elementAt(i);
						numOfRanksIn25 ++;
					}

					/*
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
					*/
				}
				
				numberOfEntries ++;
				numberOfPairs += entry.synonymsInFirstSense.size();
				
				sumOfRecall10 += (double) numOfRanksIn10 / entry.synonymsInFirstSense.size();
				sumOfRecall25 += (double) numOfRanksIn25 / entry.synonymsInFirstSense.size();
				//sumOfRecall50 += (double) numOfRanksIn50 / entry.synonymsInFirstSense.size();
				//sumOfRecall100 += (double) numOfRanksIn100 / entry.synonymsInFirstSense.size();
				//sumOfRecall200 += (double) numOfRanksIn200 / entry.synonymsInFirstSense.size();
				//sumOfRecall500 += (double) numOfRanksIn500 / entry.synonymsInFirstSense.size();
				
				numOfRanks10 += numOfRanksIn10;
				numOfRanks25 += numOfRanksIn25;
				//numOfRanks50 += numOfRanksIn50;
				//numOfRanks100 += numOfRanksIn100;
				//numOfRanks200 += numOfRanksIn200;
				//numOfRanks500 += numOfRanksIn500;
				
			}
		}
		
		if (printSum){
			statRecall1SenseOut.println("The total number of valid entries is " + numberOfEntries);
			statRecall1SenseOut.println("The total number of synonym pairs is " + numberOfPairs);
	
			statPrecision1SenseOut.println("The total number of valid entries is " + numberOfEntries);
			statPrecision1SenseOut.println("The total number of synonym pairs is " + numberOfPairs);
		}

		statRecall1SenseOut.println(parameter + "\t" + String.format("%1.2f", sumOfRecall10/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall25/numberOfEntries) /* + "\t" + String.format("%1.2f", sumOfRecall50/numberOfEntries) 
				+ "\t" + String.format("%1.2f", sumOfRecall100/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall200/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall500/numberOfEntries) */);
		
		statPrecision1SenseOut.println(parameter + "\t" + String.format("%1.2f", sumOfRanks10/numOfRanks10) + "\t" + String.format("%1.2f", sumOfRanks25/numOfRanks25) /* + "\t" + String.format("%1.2f", sumOfRanks50/numOfRanks50) + "\t" 
				+ String.format("%1.2f", sumOfRanks100/numOfRanks100) + "\t" + String.format("%1.2f", sumOfRanks200/numOfRanks200) + "\t" + String.format("%1.2f", sumOfRanks500/numOfRanks500) */);

		/*
		statRecall1SenseOut.println(parameter + "\t" + sumOfRecall10/numberOfEntries + "\t" + sumOfRecall25/numberOfEntries + "\t" + sumOfRecall50/numberOfEntries 
				+ "\t" + sumOfRecall100/numberOfEntries + "\t" + sumOfRecall200/numberOfEntries + "\t" + sumOfRecall500/numberOfEntries);
		
		statPrecision1SenseOut.println(parameter + "\t" + sumOfRanks10/numOfRanks10 + "\t" + sumOfRanks25/numOfRanks25 + "\t" + sumOfRanks50/numOfRanks50 + "\t" 
				+ sumOfRanks100/numOfRanks100 + "\t" + sumOfRanks200/numOfRanks200 + "\t" + sumOfRanks500/numOfRanks500);
		*/
	}

	public void getStatisticForFirstSynonymInTheOnlySense(double k, double p, double q, double bsf, int pmi_max_length, int ppmic_length) throws IOException{
		
		String textLine;
		
		while ((textLine = synonymReader.readLine()) != null){

			if (textLine.trim().equals(""))
				continue;
			
			TestEntryWrapper entry = new TestEntryWrapper(textLine);
			
			if (model.getFrequency(entry.targetWord + "_" + POS) >= 10000 && entry.numberOfSenses == 1 && entry.firstSynonym != null){
				
				//FloatElement[] candidates = model.getPromotedPMIWordsWithSamePOS(entry.targetWord + "_" + POS, k, p, q, bsf);
				String[] candidates;
				if (POS.equals("NN"))
					candidates = getIntersection(model.getPromotedPMIWordsWithSamePOS(entry.targetWord + "_" + POS, k, p, q, bsf), pmi_max_length, pmi_model.getDistributionalSimilarWordsWithSamePOS(entry.targetWord + "_" + POS), ppmic_length);
				else
					candidates = getIntersection(pmi_model.getDistributionalSimilarWordsWithSamePOS(entry.targetWord + "_" + POS), ppmic_length, model.getPromotedPMIWordsWithSamePOS(entry.targetWord + "_" + POS, k, p, q, bsf), pmi_max_length);

				
				int rank = 0;
				
				for (int i=0; i<candidates.length; i++){
					
					String word = candidates[i];
					
					if (entry.firstSynonym.equals(word.substring(0, word.length() - 3))){
						rank = i + 1;
						break;
					}
				}

				int numOfRanksIn10 = 0;
				int numOfRanksIn25 = 0;
				//int numOfRanksIn50 = 0;
				//int numOfRanksIn100 = 0;
				//int numOfRanksIn200 = 0;
				//int numOfRanksIn500 = 0;
 				
				if (rank <= 10 && rank > 0){
					sumOfRanks10 += rank;
					numOfRanksIn10 ++;
				}
				
				if (rank <= 25 && rank > 0){
					sumOfRanks25 += rank;
					numOfRanksIn25 ++;
				}

				/*
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
				*/
				
				numberOfEntries ++;
				numberOfPairs ++;
				
				sumOfRecall10 += (double) numOfRanksIn10 ;
				sumOfRecall25 += (double) numOfRanksIn25 ;
				//sumOfRecall50 += (double) numOfRanksIn50 ;
				//sumOfRecall100 += (double) numOfRanksIn100 ;
				//sumOfRecall200 += (double) numOfRanksIn200 ;
				//sumOfRecall500 += (double) numOfRanksIn500 ;
				
				numOfRanks10 += numOfRanksIn10;
				numOfRanks25 += numOfRanksIn25;
				//numOfRanks50 += numOfRanksIn50;
				//numOfRanks100 += numOfRanksIn100;
				//numOfRanks200 += numOfRanksIn200;
				//numOfRanks500 += numOfRanksIn500;
				
			}
		}
		
		if (printSum){
			statRecall1SynonymOut.println("The total number of valid entries is " + numberOfEntries);
			statRecall1SynonymOut.println("The total number of synonym pairs is " + numberOfPairs);
	
			statPrecision1SynonymOut.println("The total number of valid entries is " + numberOfEntries);
			statPrecision1SynonymOut.println("The total number of synonym pairs is " + numberOfPairs);
		}
		
		statRecall1SynonymOut.println(parameter + "\t" + String.format("%1.2f", sumOfRecall10/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall25/numberOfEntries) /* + "\t" + String.format("%1.2f", sumOfRecall50/numberOfEntries) 
				+ "\t" + String.format("%1.2f", sumOfRecall100/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall200/numberOfEntries) + "\t" + String.format("%1.2f", sumOfRecall500/numberOfEntries) */);
		
		statPrecision1SynonymOut.println(parameter + "\t" + String.format("%1.2f", sumOfRanks10/numOfRanks10) + "\t" + String.format("%1.2f", sumOfRanks25/numOfRanks25) /* + "\t" + String.format("%1.2f", sumOfRanks50/numOfRanks50) + "\t" 
				+ String.format("%1.2f", sumOfRanks100/numOfRanks100) + "\t" + String.format("%1.2f", sumOfRanks200/numOfRanks200) + "\t" + String.format("%1.2f", sumOfRanks500/numOfRanks500) */);

		/*
		statRecall1SynonymOut.println(parameter + "\t" + sumOfRecall10/numberOfEntries + "\t" + sumOfRecall25/numberOfEntries + "\t" + sumOfRecall50/numberOfEntries 
				+ "\t" + sumOfRecall100/numberOfEntries + "\t" + sumOfRecall200/numberOfEntries + "\t" + sumOfRecall500/numberOfEntries);
		
		statPrecision1SynonymOut.println(parameter + "\t" + sumOfRanks10/numOfRanks10 + "\t" + sumOfRanks25/numOfRanks25 + "\t" + sumOfRanks50/numOfRanks50 + "\t" 
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
		
		PromotedPMI_PPMIC_Evaluation test;
		int totalEntries;
		
		if (args.length > 0){
			
			if (args[0].equals("VB")){
				test = new PromotedPMI_PPMIC_Evaluation("/home/lushan1/nlp/evaluation/verbSynonyms700.txt", "VB");
				totalEntries = 1187;
				
			}else if (args[0].equals("NN")){
				test = new PromotedPMI_PPMIC_Evaluation("/home/lushan1/nlp/evaluation/nounSynonyms700.txt", "NN");
				totalEntries = 2286;
				
			}else if (args[0].equals("JJ")){
				test = new PromotedPMI_PPMIC_Evaluation("/home/lushan1/nlp/evaluation/adjSynonyms700.txt", "JJ");
				totalEntries = 1015;
				
			}else if (args[0].equals("RB")){
				test = new PromotedPMI_PPMIC_Evaluation("/home/lushan1/nlp/evaluation/advSynonyms700.txt", "RB");
				totalEntries = 39;
			}else{
				throw new Exception("Wrong parameter: " + args[0]);
			}
			
		}else{
			test = new PromotedPMI_PPMIC_Evaluation("/home/lushan1/nlp/evaluation/adjSynonyms700.txt", "JJ");
			totalEntries = 1015;
		}
		
		test.set("/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW", 41, "/home/lushan1/nlp/model/PositivePMI/Gutenberg2010AllW", 2, 700, 0 );
		
		
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
		
		double p = 2;
		double q = -4;
		double k = 70;
		int pmi_max_length = 100;
		int ppmic_length = 100;
		
		test.parameter = k + "-" + p + "-" + q + "-" + bsf;

		
		test.getStatisticForFirstSynonymInTheOnlySense(k, p, q, bsf, pmi_max_length, ppmic_length);
		test.reset();

		test.getStatisticForTheOnlySense(k, p, q, bsf, pmi_max_length, ppmic_length);
		test.reset();
		
		test.getStatisticForAllSenses(k, p, q, bsf, pmi_max_length, ppmic_length);
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
