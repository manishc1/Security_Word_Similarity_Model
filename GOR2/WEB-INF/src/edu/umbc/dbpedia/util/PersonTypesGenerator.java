package edu.umbc.dbpedia.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class PersonTypesGenerator {

	WordNetDatabase database;
	private int Threshold_Count = 5;
	
	public PersonTypesGenerator() {
		// TODO Auto-generated constructor stub
        System.setProperty("wordnet.database.dir", "/usr/share/wordnet-3.0/dict/");
        database = WordNetDatabase.getFileInstance();

	}

	public void process() throws IOException{
		
		FileWriter file = new FileWriter("PersonTypes.txt");
		PrintWriter output = new PrintWriter(new BufferedWriter(file));
		
		Synset[] synsets = database.getSynsets("person", SynsetType.NOUN, false);
		
		NounSynset personSynset = (NounSynset) synsets[0];
		
		populate(personSynset, output);
		
		output.close();
	}

	public void populate(NounSynset parent, PrintWriter out){
		
		for (NounSynset child :parent.getHyponyms()){
			
			String[] wordForms = child.getWordForms();
			
			for (String wordForm : wordForms){
				
				if (child.getTagCount(wordForm) < Threshold_Count)
					continue;

				if (!Character.isUpperCase(wordForm.charAt(0)))
					out.println(wordForm);
			}
			
			populate(child, out);
		}
		
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		PersonTypesGenerator generator = new PersonTypesGenerator();
		generator.process();
	}

}
