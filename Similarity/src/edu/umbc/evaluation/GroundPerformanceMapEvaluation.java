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

public class GroundPerformanceMapEvaluation {

	public BufferedReader synonymReader;
	String infileName;
	int size;
	private PrintWriter statMapAllOut;
	private PrintWriter statMap1SenseOut;
	private PrintWriter statMap1SynonymOut;
	public CoOccurModelByArrays model;
	String POS;
	public int missingFirstGiven;
	public int missingFirstEncountered;
	public int noFirst;
	public double sumOfAveragePrecision;
	
	
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
	
	
	
	public GroundPerformanceMapEvaluation(String InfileName, String pos) {
		// TODO Auto-generated constructor stub
		try{
			File synonymCollection = new File(InfileName);
			synonymReader = new BufferedReader(new FileReader(synonymCollection));
			POS = pos;
			
			infileName = InfileName;
			
			statMapAllOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_map_all.txt"));
			//statMap1SenseOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_map_1sense.txt"));
			//statMap1SynonymOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_map_1synonym.txt"));
			
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

	
	public void getStatisticForAllSenses() throws IOException{
		
		String textLine;
		
		while ((textLine = synonymReader.readLine()) != null){

			if (textLine.trim().equals(""))
				continue;
			
			TestEntryWrapper entry = new TestEntryWrapper(textLine);
			
			if (model.getFrequency(entry.targetWord + "_" + POS) >= 10000){
				
				FloatElement[] candidates = model.getSortedPMIWordsWithSamePOS(entry.targetWord + "_" + POS);

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
		
		statMapAllOut.println(size + "\t" + String.format("%1.3f", sumOfAveragePrecision/numberOfEntries));
		
	}

	public void getStatisticForTheOnlySense() throws IOException{
		
		String textLine;
		
		while ((textLine = synonymReader.readLine()) != null){

			if (textLine.trim().equals(""))
				continue;
			
			TestEntryWrapper entry = new TestEntryWrapper(textLine);
			
			if (model.getFrequency(entry.targetWord + "_" + POS) >= 10000 && entry.numberOfSenses == 1){
				
				FloatElement[] candidates = model.getSortedPMIWordsWithSamePOS(entry.targetWord + "_" + POS);

				Vector<Integer> ranks = new Vector<Integer>();
				
				for (int i=0; i<candidates.length; i++){
					
					String word = candidates[i].word;
					
					if (entry.synonymsInFirstSense.contains( word.substring(0, word.length() - 3)))
						ranks.add(i + 1);
				}

				Collections.sort(ranks);
		
				int numberOfSynonymsFound = 0;
				double sumOfPrecision = 0;
				
				for (int i=0; i<ranks.size(); i++){
					
					numberOfSynonymsFound ++;
					sumOfPrecision += ((double) numberOfSynonymsFound) / ranks.elementAt(i);
				}

				double averagePrecision = sumOfPrecision / entry.allSynonyms.size();
				
				
				numberOfEntries ++;
				numberOfPairs += entry.allSynonyms.size();
				
				sumOfAveragePrecision  += averagePrecision;
			}
		}
		
		if (printSum){
			statMap1SenseOut.println("The total number of valid entries is " + numberOfEntries);
			statMap1SenseOut.println("The total number of synonym pairs is " + numberOfPairs);
		}
		
		statMap1SenseOut.println(size + "\t" + String.format("%1.2f", sumOfAveragePrecision/numberOfEntries));

		
		/*
		statRecall1SenseOut.println(size + "\t" + sumOfRecall10/numberOfEntries + "\t" + sumOfRecall25/numberOfEntries + "\t" + sumOfRecall50/numberOfEntries 
				+ "\t" + sumOfRecall100/numberOfEntries + "\t" + sumOfRecall200/numberOfEntries + "\t" + sumOfRecall500/numberOfEntries);
		
		statPrecision1SenseOut.println(size + "\t" + sumOfRanks10/numOfRanks10 + "\t" + sumOfRanks25/numOfRanks25 + "\t" + sumOfRanks50/numOfRanks50 + "\t" 
				+ sumOfRanks100/numOfRanks100 + "\t" + sumOfRanks200/numOfRanks200 + "\t" + sumOfRanks500/numOfRanks500);
		*/
	}


	public void close() throws IOException{
		synonymReader.close();
		
		statMapAllOut.close();
		//statMap1SenseOut.close();
		//statMap1SynonymOut.close();
	}
	
	public void clear() {
		model = null;
		size = 0;
		printSum = false;
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		GroundPerformanceMapEvaluation test = new GroundPerformanceMapEvaluation("/home/lushan1/nlp/evaluation/advSynonyms700.txt", "RB");
		
		//for (int i=6; i<=71; i += 5){
		for (int i=41; i<=41; i += 5){
			
			test.set("/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW", i, 700, 0 );

			//test.getStatisticForTheOnlySense();
			//test.reset();
			
			test.getStatisticForAllSenses();
			test.reset();
			
			test.clear();
			System.gc();
			
			System.out.println(i + " is done.");
		}
		
		test.close();
		System.out.println("Work Completed!");
	}

}
