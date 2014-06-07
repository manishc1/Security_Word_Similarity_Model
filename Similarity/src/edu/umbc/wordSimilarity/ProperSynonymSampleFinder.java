package edu.umbc.wordSimilarity;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashSet;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class ProperSynonymSampleFinder {

	public CoOccurModelByArrays model;
	public int FrequencyLimitStrict = 500000;
	public int FrequencyLimitWeak = 100000;
	WordNetDatabase database;
	public String pos;
	public LinkedHashSet<String> synonymPairs;
	private String synonymFileName = "synonym_pairs";
	private PrintWriter synonymOutput;
	public int count = 0;
	public double dominatingThreshold = 0.65;
	
	
	public ProperSynonymSampleFinder(String word_type) throws IOException {
		// TODO Auto-generated constructor stub
		pos = word_type.toLowerCase();
		model = new CoOccurModelByArrays("/home/lushan1/nlp/model/Gutenberg2010/Gutenberg2010AllW21", false);
		System.setProperty("wordnet.database.dir", "/opt/WordNet-3.0/dict/");
		database = WordNetDatabase.getFileInstance();
		synonymPairs = new LinkedHashSet<String>();
		FileWriter synonymFile = new FileWriter(synonymFileName + "_" + pos + ".txt");
		synonymOutput = new PrintWriter(new BufferedWriter(synonymFile));
				
	}

	public int processByWordNet(){
		
		String[] vocabulary = model.vocabulary; 
		
		for (String word : vocabulary){
			
			if (word.length() < 3) continue;
			
			boolean IsNormalWord = true;
			for (int index=0; index < word.length(); index++){
				if (!Character.isLowerCase(word.charAt(index))){
					IsNormalWord = false;
					break;
				}
			}
			
			if (!IsNormalWord) continue;
			
			
			boolean HaveOtherPOS = false;

			/*
			if (word.equals("acclaimed")){
				System.out.println("We encountered the word: " + word);
			}
			*/
			
			Synset[] synsets = database.getSynsets(word);
			if (synsets.length == 0){
				//System.out.println("there is no synset for the word: " + word);
				continue;
			}
			
			for (Synset synset : synsets){
				if (!synset.getType().toString().equals(pos)){
					HaveOtherPOS = true;
					break;
				}
			}
			
			if (HaveOtherPOS) continue;
			
/*			
			if (pos.equals("verb")){
				word = morphology.getBaseFormCandidates(word, SynsetType.VERB)[0];
			}
*/
			
			int maximumCount = 0;
			int place = 0;
			int sumOfCount = 0;
			boolean notBaseForm = false;
			
			for (int i = 0; i < synsets.length; i++){
				
				int tagCount;
				try{
					tagCount = synsets[i].getTagCount(word);
				}catch (Exception e){
					notBaseForm = true;
					//System.out.println("The word " + word + " is not in its base form.");
					break;
				}
				
				if (tagCount > maximumCount){
					maximumCount = tagCount;
					place = i;
				}
				
				sumOfCount += tagCount;
			}
			
			if (notBaseForm) continue;
			
			if (maximumCount != 0 && (double)maximumCount / sumOfCount < dominatingThreshold )
				continue;

			String[] synonymousWords = synsets[place].getWordForms();
			
			for (String synonym : synonymousWords){
				
				if (synonym.equals(word))
					continue;
				
				if (model.index(synonym) < 0)
					continue;
				
				if (synonym.length() < 3) continue;
				
				boolean synonym_IsNormalWord = true;
				for (int index=0; index < synonym.length(); index++){
					if (!Character.isLowerCase(synonym.charAt(index))){
						synonym_IsNormalWord = false;
						break;
					}
				}
				
				if (!synonym_IsNormalWord) continue;
				
				boolean synonym_HaveOtherPOS = false;
				Synset[] synonym_Synsets = database.getSynsets(synonym);
				
				for (Synset synset : synonym_Synsets){
					if (!synset.getType().toString().equals(pos)){
						synonym_HaveOtherPOS = true;
						break;
					}
				}
				
				if (synonym_HaveOtherPOS) continue;
				
				int synonym_maximumCount = 0;
				int synonym_sumOfCount = 0;
				notBaseForm = false;
				int max_place = 0;
				
				for (int i = 0; i < synonym_Synsets.length; i++){
					
					int tagCount;
					try{
						tagCount = synonym_Synsets[i].getTagCount(synonym);
					}catch (Exception e){
						notBaseForm = true;
						//System.out.println("The word " + word + " is not in its base form.");
						break;
					}

					
					if (tagCount > synonym_maximumCount){
						synonym_maximumCount = tagCount;
						max_place = i;
					}
					
					synonym_sumOfCount += tagCount;
				}
				
				if (notBaseForm) continue;
				
				if (synonym_maximumCount != 0 && (double)synonym_maximumCount / synonym_sumOfCount < dominatingThreshold )
					continue;
				
				if (synonym_maximumCount != 0){
					
					boolean match = false;
					for (String wordForm: synonym_Synsets[max_place].getWordForms()){
						if (wordForm.equals(word)){
							match = true;
							break;
						}
					}
					
					if (!match)
						continue;
				}
				/*
				if (model.getFrequency(word) < model.totalWords / FrequencyLimitStrict && model.getFrequency(synonym) < model.totalWords / FrequencyLimitStrict)
					continue;
					
				if (model.getFrequency(word) < model.totalWords / FrequencyLimitWeak || model.getFrequency(synonym) < model.totalWords / FrequencyLimitWeak)
					continue;
				*/
				
				if (model.getCoOccurrence(word, synonym) < 20)
					continue;
				
				String synonym_pair;
				
				if (synonym.compareTo(word) > 0)
					synonym_pair = word + " " + synonym;
				else
					synonym_pair = synonym + " " + word;
				
				System.out.println(synonym_pair);
				
				if (synonymPairs.add(synonym_pair))
					count ++;
				
			}
			
		}
		
		for (String synonymPair : synonymPairs){
			synonymOutput.println(synonymPair);
		}
		
		synonymOutput.close();
		
		return 0;
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ProperSynonymSampleFinder finder = new ProperSynonymSampleFinder("adv");
		finder.processByWordNet();
		System.out.println("Congratualtion! Task Finished.");
		System.out.println(finder.count + " synonym pairs have been found.");
	}

}
