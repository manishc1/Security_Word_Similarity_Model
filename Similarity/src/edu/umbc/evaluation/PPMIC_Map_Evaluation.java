package edu.umbc.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Vector;

import edu.umbc.wordSimilarity2.FloatElement;
import edu.umbc.wordSimilarity2.PPMIModelByArrays;

public class PPMIC_Map_Evaluation {

	public BufferedReader synonymReader;
	String infileName;
	int size;
	private PrintWriter statMapAllOut;
	public PPMIModelByArrays model;
	String POS;
	public int missingFirstGiven;
	public int missingFirstEncountered;
	public int noFirst;
	public double sumOfAveragePrecision;
	public int numberOfEntries;
	public int numberOfPairs;
	public boolean printSum;
	public String parameter;
	
	
	
	public PPMIC_Map_Evaluation(String InfileName, String pos) {
		// TODO Auto-generated constructor stub
		try{
			File synonymCollection = new File(InfileName);
			synonymReader = new BufferedReader(new FileReader(synonymCollection));
			POS = pos;
			
			infileName = InfileName;
			
			statMapAllOut = new PrintWriter(new FileWriter(infileName.substring(0, infileName.length() - 4) + "_ppmic_map_all.txt", true));
			
			printSum = true;

			
		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
			System.exit(-1);
		}

	}
	
	
	public void set(String modelname, int p_size, int freThreshold){
		
		size = p_size;
		model = new PPMIModelByArrays(modelname + size, false, freThreshold);
		
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
	
	
	public void getStatisticForAllSenses() throws IOException{
		
		String textLine;
		
		while ((textLine = synonymReader.readLine()) != null){

			if (textLine.trim().equals(""))
				continue;
			
			TestEntryWrapper entry = new TestEntryWrapper(textLine);
			
			if (model.getFrequency(entry.targetWord + "_" + POS) >= 10000){
				
				FloatElement[] candidates = model.getDistributionalSimilarWordsWithSamePOS(entry.targetWord + "_" + POS);

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
		statRecallAllOut.println(Utility.A + "\t" + sumOfRecall10/numberOfEntries + "\t" + sumOfRecall25/numberOfEntries + "\t" + sumOfRecall50/numberOfEntries 
				+ "\t" + sumOfRecall100/numberOfEntries + "\t" + sumOfRecall200/numberOfEntries + "\t" + sumOfRecall500/numberOfEntries);
		
		statPrecisionAllOut.println(Utility.A + "\t" + sumOfRanks10/numOfRanks10 + "\t" + sumOfRanks25/numOfRanks25 + "\t" + sumOfRanks50/numOfRanks50 + "\t" 
				+ sumOfRanks100/numOfRanks100 + "\t" + sumOfRanks200/numOfRanks200 + "\t" + sumOfRanks500/numOfRanks500);
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
		
		PPMIC_Map_Evaluation test;
		
		if (args.length > 0){
			
			if (args[0].equals("VB"))
				test = new PPMIC_Map_Evaluation("/home/lushan1/nlp/evaluation/verbSynonyms700.txt", "VB");
			else if (args[0].equals("NN"))
				test = new PPMIC_Map_Evaluation("/home/lushan1/nlp/evaluation/nounSynonyms700.txt", "NN");
			else if (args[0].equals("JJ"))
				test = new PPMIC_Map_Evaluation("/home/lushan1/nlp/evaluation/adjSynonyms700.txt", "JJ");
			else if (args[0].equals("RB"))
				test = new PPMIC_Map_Evaluation("/home/lushan1/nlp/evaluation/advSynonyms700.txt", "RB");
			else{
				throw new Exception("Wrong parameter: " + args[0]);
			}
			
		}else
			test = new PPMIC_Map_Evaluation("/home/lushan1/nlp/evaluation/nounSynonyms700.txt", "NN");
		
		
		test.set("/home/lushan1/nlp/model/PositivePMI/Gutenberg2010AllW", 2, 700);

			
		test.getStatisticForAllSenses();
		test.reset();
		
		test.clear();
			
		test.close();
		System.out.println("Work Completed!");

	}

}
