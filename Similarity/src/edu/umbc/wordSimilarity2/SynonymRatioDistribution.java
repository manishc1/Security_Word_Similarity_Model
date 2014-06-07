package edu.umbc.wordSimilarity2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

public class SynonymRatioDistribution {

	public CoOccurModelByArrays present_model;
	public CoOccurModelByArrays overall_model;
	FileWriter outputFile;
	private PrintWriter distributionOutput;
	String nounFile;
	String verbFile;
	String adjFile;
	String advFile;
	
	private Vector<String[]> noun_pairs = new Vector<String[]>();
	private double[][] noun_ratios;
	
	private Vector<String[]> verb_pairs = new Vector<String[]>();
	private double[][] verb_ratios;
	
	private Vector<String[]> adj_pairs = new Vector<String[]>();
	private double[][] adj_ratios;
	
	private Vector<String[]> adv_pairs = new Vector<String[]>();
	private double[][] adv_ratios;  
	
	
	
	public SynonymRatioDistribution(String nounFilename, String verbFilename, String adjFilename, String advFilename, String overallModelname, int trials) throws IOException {
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
		
		noun_ratios = new double[noun_pairs.size()][trials];
		
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
		
		verb_ratios = new double[verb_pairs.size()][trials];
		
				
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
		
		adj_ratios = new double[adj_pairs.size()][trials];

		
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
		
		adv_ratios = new double[adv_pairs.size()][trials];

		
	}

	public void process(String presentModelName, int order) throws IOException{
		
		present_model = new CoOccurModelByArrays(presentModelName, false);

		//noun
		//for (String[] pair : noun_pairs){
		for (int i=0; i<noun_pairs.size(); i++){
			
			String word1 = noun_pairs.elementAt(i)[0];
			String word2 = noun_pairs.elementAt(i)[1];
			
			noun_ratios[i][order] = ((double) present_model.getCoOccurrence(word1, word2) / overall_model.getCoOccurrence(word1, word2)) 
								/ ((double) present_model.getCoOccurrence(word1, word1) / overall_model.getCoOccurrence(word1, word1)); 
		}
		
		for (int i=0; i<verb_pairs.size(); i++){
			
			String word1 = verb_pairs.elementAt(i)[0];
			String word2 = verb_pairs.elementAt(i)[1];
			
			verb_ratios[i][order] = ((double)present_model.getCoOccurrence(word1, word2) / overall_model.getCoOccurrence(word1, word2)) 
			/ ((double)present_model.getCoOccurrence(word1, word1) / overall_model.getCoOccurrence(word1, word1)); 
		}
		
		for (int i=0; i<adj_pairs.size(); i++){
			
			String word1 = adj_pairs.elementAt(i)[0];
			String word2 = adj_pairs.elementAt(i)[1];
			
			adj_ratios[i][order] = ((double)present_model.getCoOccurrence(word1, word2) / overall_model.getCoOccurrence(word1, word2)) 
			/ ((double)present_model.getCoOccurrence(word1, word1) / overall_model.getCoOccurrence(word1, word1)); 
		}

		for (int i=0; i<adv_pairs.size(); i++){
			
			String word1 = adv_pairs.elementAt(i)[0];
			String word2 = adv_pairs.elementAt(i)[1];
			
			adv_ratios[i][order] = ((double)present_model.getCoOccurrence(word1, word2) / overall_model.getCoOccurrence(word1, word2)) 
			/ ((double)present_model.getCoOccurrence(word1, word1) / overall_model.getCoOccurrence(word1, word1)); 
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
		
		for (int i=0; i < noun_ratios.length; i++){
			
			String outputLine = "";
			
			for (int j=0; j<noun_ratios[i].length; j++){
				
				if (j==0)
					outputLine += noun_ratios[i][j];
				else
					outputLine += ", " + noun_ratios[i][j];
			}
			
			distributionOutput.println(outputLine);
		}
		
		distributionOutput.close();
		
		
		outputFile = new FileWriter(verbFile.substring(0, verbFile.length() - 3) + "csv");
		distributionOutput = new PrintWriter(new BufferedWriter(outputFile));
		
		for (int i=0; i < verb_ratios.length; i++){
			
			String outputLine = "";
			
			for (int j=0; j<verb_ratios[i].length; j++){
				
				if (j==0)
					outputLine += verb_ratios[i][j];
				else
					outputLine += ", " + verb_ratios[i][j];
			}
			
			distributionOutput.println(outputLine);
		}
		
		distributionOutput.close();		
		

		outputFile = new FileWriter(adjFile.substring(0, adjFile.length() - 3) + "csv");
		distributionOutput = new PrintWriter(new BufferedWriter(outputFile));
		
		for (int i=0; i < adj_ratios.length; i++){
			
			String outputLine = "";
			
			for (int j=0; j<adj_ratios[i].length; j++){
				
				if (j==0)
					outputLine += adj_ratios[i][j];
				else
					outputLine += ", " + adj_ratios[i][j];
			}
			
			distributionOutput.println(outputLine);
		}
		
		distributionOutput.close();		
		

		outputFile = new FileWriter(advFile.substring(0, advFile.length() - 3) + "csv");
		distributionOutput = new PrintWriter(new BufferedWriter(outputFile));
		
		for (int i=0; i < adv_ratios.length; i++){
			
			String outputLine = "";
			
			for (int j=0; j<adv_ratios[i].length; j++){
				
				if (j==0)
					outputLine += adv_ratios[i][j];
				else
					outputLine += ", " + adv_ratios[i][j];
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
		SynonymRatioDistribution run = new SynonymRatioDistribution("/home/lushan1/nlp/evaluation/synonymPairs_1sense_NN.txt", "/home/lushan1/nlp/evaluation/synonymPairs_1sense_VB.txt", 
				"/home/lushan1/nlp/evaluation/synonymPairs_1sense_JJ.txt", "/home/lushan1/nlp/evaluation/synonymPairs_1sense_RB.txt", "/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW41", 10);
		
		for (int i=0; i<10; i++){
			run.process("/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW" + (6 + i), i);
			
			System.out.println("process " + i + " is done.");
		}
		
		run.output();

		System.out.println("Congratulation! Task Finished.");		
	}

}
