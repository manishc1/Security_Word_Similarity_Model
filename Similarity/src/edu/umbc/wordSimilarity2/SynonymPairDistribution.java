package edu.umbc.wordSimilarity2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import edu.umbc.wordSimilarity.CoOccurModelByArrays;

public class SynonymPairDistribution {

	public CoOccurModelByArrays present_model;
	public CoOccurModelByArrays overall_model;
	FileWriter outputFile;
	private PrintWriter distributionOutput;
	String nounFile;
	String verbFile;
	String adjFile;
	String advFile;
	
	private Vector<String[]> noun_pairs = new Vector<String[]>();
	private double[][] noun_array;
	
	private Vector<String[]> verb_pairs = new Vector<String[]>();
	private double[][] verb_array;
	
	private Vector<String[]> adj_pairs = new Vector<String[]>();
	private double[][] adj_array;
	
	private Vector<String[]> adv_pairs = new Vector<String[]>();
	private double[][] adv_array;  
	
	
	
	public SynonymPairDistribution(String nounFilename, String verbFilename, String adjFilename, String advFilename, String overallModelname, int windowSize) throws IOException {
		// TODO Auto-generated constructor stub
		/*
		present_model = new CoOccurModelByArrays(modelName1, false);
		overall_model = new CoOccurModelByArrays(modelName2, false);
		synonymFileName = synonymFile + ".txt";
		outputFileName = synonymFile + ".csv";
		FileWriter outputFile = new FileWriter(outputFileName);
		distributionOutput = new PrintWriter(new BufferedWriter(outputFile));
		*/
		
		overall_model = new CoOccurModelByArrays(overallModelname, false);
		nounFile = nounFilename;
		verbFile = verbFilename;
		adjFile = adjFilename;
		advFile = advFilename;
		
		//noun
		BufferedReader synonymPairReader = new BufferedReader(new FileReader(nounFilename));
		
		String synonym_pair;
		
		while ((synonym_pair = synonymPairReader.readLine()) != null){
			int split = synonym_pair.indexOf(' ');
			String word1 = synonym_pair.substring(0, split);
			String word2 = synonym_pair.substring(split + 1, synonym_pair.length());
			String[] pair = new String[2];
			pair[0] = word1;
			pair[1] = word2;
			noun_pairs.add(pair);
		}
		
		synonymPairReader.close();
		
		noun_array = new double[noun_pairs.size()][windowSize];
		
		//verb
		synonymPairReader = new BufferedReader(new FileReader(verbFilename));
		
		while ((synonym_pair = synonymPairReader.readLine()) != null){
			int split = synonym_pair.indexOf(' ');
			String word1 = synonym_pair.substring(0, split);
			String word2 = synonym_pair.substring(split + 1, synonym_pair.length());
			String[] pair = new String[2];
			pair[0] = word1;
			pair[1] = word2;
			verb_pairs.add(pair);
		}
		
		synonymPairReader.close();
		
		verb_array = new double[verb_pairs.size()][windowSize];
		
				
		//adj
		synonymPairReader = new BufferedReader(new FileReader(adjFilename));
		
		while ((synonym_pair = synonymPairReader.readLine()) != null){
			int split = synonym_pair.indexOf(' ');
			String word1 = synonym_pair.substring(0, split);
			String word2 = synonym_pair.substring(split + 1, synonym_pair.length());
			String[] pair = new String[2];
			pair[0] = word1;
			pair[1] = word2;
			adj_pairs.add(pair);
		}
		
		synonymPairReader.close();
		
		adj_array = new double[adj_pairs.size()][windowSize];

		
		//adv
		synonymPairReader = new BufferedReader(new FileReader(advFilename));
		
		while ((synonym_pair = synonymPairReader.readLine()) != null){
			int split = synonym_pair.indexOf(' ');
			String word1 = synonym_pair.substring(0, split);
			String word2 = synonym_pair.substring(split + 1, synonym_pair.length());
			String[] pair = new String[2];
			pair[0] = word1;
			pair[1] = word2;
			adv_pairs.add(pair);
		}
		
		synonymPairReader.close();
		
		adv_array = new double[adv_pairs.size()][windowSize];

		
	}

	public void process(String presentModelName, int place) throws IOException{
		
		present_model = new CoOccurModelByArrays(presentModelName + place, false);

		//noun
		//for (String[] pair : noun_pairs){
		for (int i=0; i<noun_pairs.size(); i++){
			
			String word1 = noun_pairs.elementAt(i)[0];
			String word2 = noun_pairs.elementAt(i)[1];
			
			noun_array[i][place - 1] = (double) present_model.getCoOccurrence(word1, word2) / overall_model.getCoOccurrence(word1, word2); // + overall_model.getCoOccurrence(word1, word2);
		}
		
		for (int i=0; i<verb_pairs.size(); i++){
			
			String word1 = verb_pairs.elementAt(i)[0];
			String word2 = verb_pairs.elementAt(i)[1];
			
			verb_array[i][place - 1] = (double) present_model.getCoOccurrence(word1, word2) / overall_model.getCoOccurrence(word1, word2); // + overall_model.getCoOccurrence(word1, word2);
		}
		
		for (int i=0; i<adj_pairs.size(); i++){
			
			String word1 = adj_pairs.elementAt(i)[0];
			String word2 = adj_pairs.elementAt(i)[1];
			
			adj_array[i][place - 1] = (double) present_model.getCoOccurrence(word1, word2) / overall_model.getCoOccurrence(word1, word2); // + overall_model.getCoOccurrence(word1, word2);
		}

		for (int i=0; i<adv_pairs.size(); i++){
			
			String word1 = adv_pairs.elementAt(i)[0];
			String word2 = adv_pairs.elementAt(i)[1];
			
			adv_array[i][place - 1] = (double) present_model.getCoOccurrence(word1, word2) / overall_model.getCoOccurrence(word1, word2); // + overall_model.getCoOccurrence(word1, word2);
		}

		present_model.close();
		present_model = null;
		System.gc();

		/*
		while ((synonym_pair = synonymPairReader.readLine()) != null){
			int split = synonym_pair.indexOf(' ');
			String word1 = synonym_pair.substring(0, split);
			String word2 = synonym_pair.substring(split + 1, synonym_pair.length());
			String outputLine = word1 + "\t" + word2 
				+ "\t" + present_model.getCoOccurrence(word1, word2) 
				+ "\t" + overall_model.getCoOccurrence(word1, word2) 
				+ "\t" + ((double) present_model.getCoOccurrence(word1, word2) / overall_model.getCoOccurrence(word1, word2));
			
			distributionOutput.println(outputLine);
		}
		
		synonymPairReader.close();
		distributionOutput.close();
		*/
		
	}
	
	
	public void output() throws IOException{
		
		outputFile = new FileWriter(nounFile.substring(0, nounFile.length() - 3) + "csv");
		distributionOutput = new PrintWriter(new BufferedWriter(outputFile));
		
		for (int i=0; i < noun_array.length; i++){
			
			String outputLine = "";
			
			for (int j=0; j<noun_array[i].length; j++){
				
				if (j==0)
					outputLine += noun_array[i][j];
				else
					outputLine += ", " + noun_array[i][j];
			}
			
			distributionOutput.println(outputLine);
		}
		
		distributionOutput.close();
		
		
		outputFile = new FileWriter(verbFile.substring(0, verbFile.length() - 3) + "csv");
		distributionOutput = new PrintWriter(new BufferedWriter(outputFile));
		
		for (int i=0; i < verb_array.length; i++){
			
			String outputLine = "";
			
			for (int j=0; j<verb_array[i].length; j++){
				
				if (j==0)
					outputLine += verb_array[i][j];
				else
					outputLine += ", " + verb_array[i][j];
			}
			
			distributionOutput.println(outputLine);
		}
		
		distributionOutput.close();		
		

		outputFile = new FileWriter(adjFile.substring(0, adjFile.length() - 3) + "csv");
		distributionOutput = new PrintWriter(new BufferedWriter(outputFile));
		
		for (int i=0; i < adj_array.length; i++){
			
			String outputLine = "";
			
			for (int j=0; j<adj_array[i].length; j++){
				
				if (j==0)
					outputLine += adj_array[i][j];
				else
					outputLine += ", " + adj_array[i][j];
			}
			
			distributionOutput.println(outputLine);
		}
		
		distributionOutput.close();		
		

		outputFile = new FileWriter(advFile.substring(0, advFile.length() - 3) + "csv");
		distributionOutput = new PrintWriter(new BufferedWriter(outputFile));
		
		for (int i=0; i < adv_array.length; i++){
			
			String outputLine = "";
			
			for (int j=0; j<adv_array[i].length; j++){
				
				if (j==0)
					outputLine += adv_array[i][j];
				else
					outputLine += ", " + adv_array[i][j];
			}
			
			distributionOutput.println(outputLine);
		}
		
		distributionOutput.close();		
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//SynonymPairDistribution run = new SynonymPairDistribution("/home/lushan1/nlp/model/Gutenberg2010/Gutenberg2010AllW4", "/home/lushan1/nlp/model/Gutenberg2010/0221/Gutenberg2010AllW21", "synonym_pairs_noun");
		SynonymPairDistribution run = new SynonymPairDistribution("/home/lushan1/nlp/evaluation/synonymPairs_all_NN.txt", "/home/lushan1/nlp/evaluation/synonymPairs_all_VB.txt", 
				"/home/lushan1/nlp/evaluation/synonymPairs_all_JJ.txt", "/home/lushan1/nlp/evaluation/synonymPairs_all_RB.txt", "/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW41", 40);
		
		for (int i=1; i<41; i++){
			run.process("/home/lushan1/nlp/model/Gutenberg2010sfd/distribution/Gutenberg2010AllAt", i);
			System.out.println(i + " is done.");
		}
		
		run.output();
		System.out.println("Congratulation! Task Finished.");		
	}

}
