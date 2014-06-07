package edu.umbc.evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import edu.umbc.wordSimilarity2.FloatElement;
import edu.umbc.wordSimilarity2.CoOccurModelByArrays;
import edu.umbc.wordSimilarity2.Utility;

public class TwoWindowsEvaluation {

	public BufferedReader synonymReader;
	String infileName;
	//int size1;
	//int size2;
	private PrintWriter individualOut;
	private PrintWriter statPrecisionAllOut;
	private PrintWriter statPrecision1SenseOut;
	private PrintWriter statPrecision1SynonymOut;
	private PrintWriter statRecallAllOut;
	private PrintWriter statRecall1SenseOut;
	private PrintWriter statRecall1SynonymOut;
	public CoOccurModelByArrays model1;
	public CoOccurModelByArrays model2;
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
	
		
	public TwoWindowsEvaluation(String InfileName, String pos) {
		// TODO Auto-generated constructor stub
		try{
			File synonymCollection = new File(InfileName);
			synonymReader = new BufferedReader(new FileReader(synonymCollection));
			POS = pos;
			
			infileName = InfileName;

			statPrecisionAllOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_two_pr_all.txt"));
			statPrecision1SenseOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_two_pr_1sense.txt"));
			statPrecision1SynonymOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_two_pr_1synonym.txt"));
			
			statRecallAllOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_two_re_all.txt"));
			statRecall1SenseOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_two_re_1sense.txt"));
			statRecall1SynonymOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_two_re_1synonym.txt"));

			/*
			statPrecisionAllOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_promoted_pr_all.txt"));
			statPrecision1SenseOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_promoted_pr_1sense.txt"));
			statPrecision1SynonymOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_promoted_pr_1synonym.txt"));
			
			statRecallAllOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_promoted_re_all.txt"));
			statRecall1SenseOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_promoted_re_1sense.txt"));
			statRecall1SynonymOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_promoted_re_1synonym.txt"));
			*/

			printSum = true;

			
		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
			System.exit(-1);
		}

	}
	
	public void set(String modelname1, String modelname2, int freThreshold, double conThreshold){
		
		//size1 = p_size1;
		model1 = new CoOccurModelByArrays(modelname1, false, freThreshold, conThreshold);
		
		//size2 = p_size2;
		model2 = new CoOccurModelByArrays(modelname2, false, freThreshold, conThreshold);
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
	
	public String printPCW(FloatElement[] sortedWords, int size){
		
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

	public FloatElement[] getCandidatasByTwoFeatures(String word, String pos, int size_threshold, int coOccurrences_threshold, double weight){
		
		double delta;
		
		//mean for 1sense and window +/-5
		/*
		double nounMean = -2.2054;
		double verbMean = -2.4468;
		double adjMean = -1.9838;
		double advMean= -2.9950;
		*/
		
		double nounMean = 0.8425;
		double verbMean = 0.6364;
		double adjMean = 0.5468;
		double advMean= 0.0303;
		
		FloatElement[] candidates = model1.getPromotedPMIWordsWithSamePOS(word);
		
		Vector<Integer> places = new Vector<Integer>();
		ArrayList<FloatElement> reOrderedWords = new ArrayList<FloatElement>();
		
		int limit;
		
		if (candidates.length < size_threshold)
			limit = candidates.length;
		else
			limit = size_threshold;
		
		for (int i=0; i<limit; i++){
			
			FloatElement candidate = candidates[i];
			int allOccurs = model1.getCoOccurrence(word, candidate.word);
			int allSelfOccurs = model1.getCoOccurrence(word, word);
			
			if (allOccurs >= coOccurrences_threshold){
					
				
				int formerOccurs = model2.getCoOccurrence(word, candidate.word);
				int formerSelfOccurs = model2.getCoOccurrence(word, word);
				
				double formerProb = (double) formerOccurs / allOccurs;
				double formerSelfProb = (double) formerSelfOccurs / allSelfOccurs;
				
				delta = 0;
				
				if (pos.equals("NN"))
					delta = Math.log(formerProb/formerSelfProb) / Math.log(2); // - nounMean;
				else if (pos.equals("VB"))
					delta = Math.log(formerProb/formerSelfProb) / Math.log(2); // - verbMean;
				else if (pos.equals("JJ"))
					delta = Math.log(formerProb/formerSelfProb) / Math.log(2); // - adjMean;
				else if (pos.equals("RB"))
					delta = Math.log(formerProb/formerSelfProb) / Math.log(2); // - advMean;
				
				delta = Math.abs(delta);
				
				FloatElement element = new FloatElement(candidate.word, (float) (candidate.value - weight * delta), candidate.conditionalProb, candidate.frequency);
				
				reOrderedWords.add(element);
				places.add(i);
			}
		}
		
		Collections.sort(reOrderedWords);
		
		for (int i=0; i<places.size(); i++){
			
			int index = places.get(i);
			candidates[index] = reOrderedWords.get(i);
		}

		
		return candidates;
	}
	

	public FloatElement[] showCandidatasByTwoWindowRatio(String word, String pos){
		

		FloatElement[] candidates = model1.getPromotedPMIWordsWithSamePOS(word);
		
		
		for (FloatElement candidate: candidates){
			
			int allOccurs = model1.getCoOccurrence(word, candidate.word);
			int formerOccurs = model2.getCoOccurrence(word, candidate.word);
			double formerProb = (double) formerOccurs / allOccurs;

			candidate.conditionalProb = (float) formerProb;
		}
		
		return candidates;
	}
	
	
	public void getStatisticForAllSenses(int size_threshold, int coOccurrs_threshold, double weight) throws IOException{
		
		String textLine;
		
		while ((textLine = synonymReader.readLine()) != null){

			if (textLine.trim().equals(""))
				continue;
			
			TestEntryWrapper entry = new TestEntryWrapper(textLine);
			
			if (model1.getFrequency(entry.targetWord + "_" + POS) >= 10000){
				
				//FloatElement[] candidates = model1.getPromotedPMIWordsWithSamePOS(entry.targetWord + "_" + POS);
				FloatElement[] candidates;
				
				if (model1.getCoOccurrence(entry.targetWord + "_" + POS, entry.targetWord + "_" + POS) >= coOccurrs_threshold)
					candidates = getCandidatasByTwoFeatures(entry.targetWord + "_" + POS, POS, size_threshold, coOccurrs_threshold, weight);
				else
					candidates = model1.getPromotedPMIWordsWithSamePOS(entry.targetWord + "_" + POS);
				
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
		
		statRecallAllOut.println(Utility.A + "\t" + sumOfRecall10/numberOfEntries + "\t" + sumOfRecall25/numberOfEntries + "\t" + sumOfRecall50/numberOfEntries 
				+ "\t" + sumOfRecall100/numberOfEntries + "\t" + sumOfRecall200/numberOfEntries + "\t" + sumOfRecall500/numberOfEntries);
		
		statPrecisionAllOut.println(Utility.A + "\t" + sumOfRanks10/numOfRanks10 + "\t" + sumOfRanks25/numOfRanks25 + "\t" + sumOfRanks50/numOfRanks50 + "\t" 
				+ sumOfRanks100/numOfRanks100 + "\t" + sumOfRanks200/numOfRanks200 + "\t" + sumOfRanks500/numOfRanks500);
	}

	public void getStatisticForTheOnlySense(int size_threshold, int coOccurrs_threshold, double weight) throws IOException{
		
		String textLine;
		
		while ((textLine = synonymReader.readLine()) != null){

			if (textLine.trim().equals(""))
				continue;
			
			TestEntryWrapper entry = new TestEntryWrapper(textLine);
			
			if (model1.getFrequency(entry.targetWord + "_" + POS) >= 10000 && entry.numberOfSenses == 1){
				
				FloatElement[] candidates;
				
				if (model1.getCoOccurrence(entry.targetWord + "_" + POS, entry.targetWord + "_" + POS) >= coOccurrs_threshold)
					candidates= getCandidatasByTwoFeatures(entry.targetWord + "_" + POS, POS, size_threshold, coOccurrs_threshold, weight);
				else
					candidates = model1.getPromotedPMIWordsWithSamePOS(entry.targetWord + "_" + POS);

				
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
			
		statRecall1SenseOut.println(Utility.A + "\t" + sumOfRecall10/numberOfEntries + "\t" + sumOfRecall25/numberOfEntries + "\t" + sumOfRecall50/numberOfEntries 
				+ "\t" + sumOfRecall100/numberOfEntries + "\t" + sumOfRecall200/numberOfEntries + "\t" + sumOfRecall500/numberOfEntries);
		
		statPrecision1SenseOut.println(Utility.A + "\t" + sumOfRanks10/numOfRanks10 + "\t" + sumOfRanks25/numOfRanks25 + "\t" + sumOfRanks50/numOfRanks50 + "\t" 
				+ sumOfRanks100/numOfRanks100 + "\t" + sumOfRanks200/numOfRanks200 + "\t" + sumOfRanks500/numOfRanks500);
		
	}

	public void getStatisticForFirstSynonymInTheOnlySense(int size_threshold, int coOccurrs_threshold, double weight) throws IOException{
		
		String textLine;
		
		while ((textLine = synonymReader.readLine()) != null){

			if (textLine.trim().equals(""))
				continue;
			
			TestEntryWrapper entry = new TestEntryWrapper(textLine);
			
			if (model1.getFrequency(entry.targetWord + "_" + POS) >= 10000 && entry.numberOfSenses == 1 && entry.firstSynonym != null){
				
				FloatElement[] candidates;
				
				if (model1.getCoOccurrence(entry.targetWord + "_" + POS, entry.targetWord + "_" + POS) >= coOccurrs_threshold)
					candidates = getCandidatasByTwoFeatures(entry.targetWord + "_" + POS, POS, size_threshold, coOccurrs_threshold, weight);
				else
					candidates = model1.getPromotedPMIWordsWithSamePOS(entry.targetWord + "_" + POS);

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
		
		statRecall1SynonymOut.println(Utility.A + "\t" + sumOfRecall10/numberOfEntries + "\t" + sumOfRecall25/numberOfEntries + "\t" + sumOfRecall50/numberOfEntries 
				+ "\t" + sumOfRecall100/numberOfEntries + "\t" + sumOfRecall200/numberOfEntries + "\t" + sumOfRecall500/numberOfEntries);
		
		statPrecision1SynonymOut.println(Utility.A + "\t" + sumOfRanks10/numOfRanks10 + "\t" + sumOfRanks25/numOfRanks25 + "\t" + sumOfRanks50/numOfRanks50 + "\t" 
				+ sumOfRanks100/numOfRanks100 + "\t" + sumOfRanks200/numOfRanks200 + "\t" + sumOfRanks500/numOfRanks500);
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
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		TwoWindowsEvaluation test = new TwoWindowsEvaluation("/home/lushan1/nlp/evaluation/advSynonyms700.txt", "RB");
		
		
		test.set("/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW41", "/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW6", 700, 0 );
		
		test.getStatisticForFirstSynonymInTheOnlySense(10, 50, 1.0);
		test.reset();

		test.getStatisticForTheOnlySense(10, 50, 1.0);
		test.reset();
		
		test.getStatisticForAllSenses(10, 50, 1.0);
		test.reset();
		
		test.clear();
			
		
		test.close();
		System.out.println("Work Completed!");
	}

}
