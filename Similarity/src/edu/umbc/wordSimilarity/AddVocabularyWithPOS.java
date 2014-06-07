package edu.umbc.wordSimilarity;

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

public class AddVocabularyWithPOS {

	WordNetDatabase database;
	private PrintWriter vocabularyWriter;
	private BufferedReader vocabularyReader;
	
	public AddVocabularyWithPOS(String fileIn, String fileOut) throws IOException {
		// TODO Auto-generated constructor stub
		File vocabularyFile = new File(fileIn);
		vocabularyReader = new BufferedReader(new FileReader(vocabularyFile));

		FileWriter outFile = new FileWriter(fileOut);
		vocabularyWriter = new PrintWriter(new BufferedWriter(outFile));

		System.setProperty("wordnet.database.dir", "/opt/WordNet-3.0/dict/");
		database = WordNetDatabase.getFileInstance();

	}

	
	public void process() throws IOException{
		
		String rdline = vocabularyReader.readLine();
		int sizeOfVocabulary = Integer.valueOf(rdline);
	
		String word;
		for (int i = 0; i < sizeOfVocabulary; i++){
			word = vocabularyReader.readLine();
			
			Synset[] synsets = database.getSynsets(word, null, false);
			
			HashSet<String> types = new HashSet<String>();
			
			for (Synset synset : synsets){

				types.add(synset.getType().toString());
			}
			
			if (types.size() == 0 && word.contains(".")){
				vocabularyWriter.println(word + "_" + "NN");
			}else{
			
				String[] sortedTypes = new String[types.size()];
				int j = 0;
				for (String type : types){
					
					if (type.equals("adj"))
						sortedTypes[j] = word + "_" + "JJ";
					else if (type.equals("noun"))
						sortedTypes[j] = word + "_" + "NN";
					else if (type.equals("adv"))
						sortedTypes[j] = word + "_" + "RB";
					else if (type.equals("verb"))
						sortedTypes[j] = word + "_" + "VB";
	
					j++;
				}
				
				Arrays.sort(sortedTypes);
				
				for (String type : sortedTypes){
	
					vocabularyWriter.println(type);
					
				}
			}
			
		}
		
		vocabularyReader.close();
		vocabularyWriter.close();
		
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		AddVocabularyWithPOS test = new AddVocabularyWithPOS("English.voc", "English_pos.voc");
		test.process();
		System.out.println("Congratulation! Task finished.");
	}

}
