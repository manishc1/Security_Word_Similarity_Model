package edu.umbc.evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.umbc.wordSimilarity2.CoOccurModelByArrays;

public class GenerateFrequencyAndNumberOfSenses {

	WordNetDatabase database;
	private PrintWriter nounWriter;
	private PrintWriter verbWriter;
	private PrintWriter adjWriter;
	private PrintWriter advWriter;
	public CoOccurModelByArrays model;
	
	public GenerateFrequencyAndNumberOfSenses(String modelname, String fileOut) throws IOException {
		// TODO Auto-generated constructor stub
		model = new CoOccurModelByArrays(modelname, false);

		nounWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileOut + "_noun.csv")));
		verbWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileOut + "_verb.csv")));
		adjWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileOut + "_adj.csv")));
		advWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileOut + "_adv.csv")));

		System.setProperty("wordnet.database.dir", "/opt/WordNet-3.0/dict/");
		database = WordNetDatabase.getFileInstance();

	}

	
	public void process() throws IOException{
		
		String word;
		String pos;
		int frequency;
		int numOfSenses;
		String type;
		
		for (int i = 0; i < model.sizeOfVocabulary; i++){
			
			word = model.vocabulary[i].substring(0, model.vocabulary[i].length() - 3);
			pos = model.vocabulary[i].substring(model.vocabulary[i].length() - 2, model.vocabulary[i].length());
			frequency = model.frequency[i];
			numOfSenses = 0;
			
			if (pos.equals("NN"))
				type = "noun";
			else if (pos.equals("VB"))
				type = "verb";
			else if (pos.equals("JJ"))
				type = "adj";
			else if (pos.equals("RB"))
				type = "adv";
			else
				type = "";
			
			
			if (frequency < 700) continue;
			if (word.contains(".")) continue;
			
			boolean isNormalWord = true;

			for (int k=0; k<word.length(); k++){
				if (!Character.isLowerCase(word.charAt(k))){
					isNormalWord = false;
					break;
				}
			}
			
			if (!isNormalWord) continue;
			
			Synset[] synsets = database.getSynsets(word, null, false);
			
			if (synsets.length == 0){
				continue;
			}else{
				
				for (int j=0; j<synsets.length; j++){
					
					if (synsets[j].getType().toString().equals(type))
						numOfSenses ++;
				}
				
				if (pos.equals("NN"))
					nounWriter.println(word + ", " + frequency + ", " + numOfSenses);
				else if (pos.equals("VB"))
					verbWriter.println(word + ", " + frequency + ", " + numOfSenses);
				else if (pos.equals("JJ"))
					adjWriter.println(word + ", " + frequency + ", " + numOfSenses);
				else if (pos.equals("RB"))
					advWriter.println(word + ", " + frequency + ", " + numOfSenses);
	
			}
			
		}
		
		nounWriter.close();
		verbWriter.close();
		adjWriter.close();
		advWriter.close();
		
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		GenerateFrequencyAndNumberOfSenses test = new GenerateFrequencyAndNumberOfSenses("/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW41", "/home/lushan1/nlp/evaluation/frequency_senses");
		test.process();
		System.out.println("Congratulation! Task finished.");
	}

}
