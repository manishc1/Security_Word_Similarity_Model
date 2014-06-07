package edu.umbc.similarity.dictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import edu.umbc.dbpedia.model.SimilarityArrayModel;

public class SimilarityFactory {
	
	public HashMap<String, WordDefinition> word_definition_map;
	public SimilarityArrayModel base_model;
	public SimilarityArrayModel target_model;
	public static final HashSet<String> ignoredWords = new HashSet<String>(Arrays.asList("be_VB", "do_VB", "have_VB", "get_VB", "take_VB"));
	
	public static float one_link_away_bonus = (float) 0.5;
	public static float two_links_away_bonus = one_link_away_bonus * (float) 0.7788;
	public static float three_links_away_bonus = one_link_away_bonus * (float) 0.6065;
	public static float four_links_away_bonus = one_link_away_bonus * (float) 0.4724;
	
	public static float webbase_corpus_size = (float) 3.3 ; // measured in giga words
	public static float gigawords_corpus_size = (float) 2.0 ; // measured in giga words
	
	public float corpus_size = webbase_corpus_size;
	//public float corpus_size = gigawords_corpus_size;
	
	public int substitution_freq_threshold = (int) (10000  * corpus_size / webbase_corpus_size); // 2000;
	public int derivative_freq_threshold = (int) (10000 * corpus_size / webbase_corpus_size); // 2000;


	public SimilarityFactory() throws Exception {

		word_definition_map = new HashMap<String, WordDefinition>();
		
		//base_model = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/webbase2012AllPlusV2W5_S2");
		//target_model = new SimilarityArrayModel("/home/lushan1/nlp/model/BigArray/webbase2012AllPlusV2W5");

		base_model = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/20131001/webbase2012AllW5_S2");
		target_model = new SimilarityArrayModel("/home/lushan1/nlp/model/BigArray/20131001/webbase2012AllW5");

		//base_model = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/Gigawords2009AllW2_S1");
		//target_model = new SimilarityArrayModel("/home/lushan1/nlp/model/BigArray/Gigawords2009AllW2");

		
	}
	
	public void importDefinition() throws Exception{
		
		//File dictionaryFile = new File("/home/lushan1/nlp/dictionary/wordnet_webbase_all_plus_v2_w5.dict");
		File dictionaryFile = new File("/home/lushan1/nlp/dictionary/wordnet_webbase_all_w5.dict");
		//File dictionaryFile = new File("/home/lushan1/nlp/dictionary/wordnet_gigawords_all_w5.dict");
		
		BufferedReader dictionaryReader = new BufferedReader(new FileReader(dictionaryFile), 1000000);

		String rdline;
		rdline = dictionaryReader.readLine();
		int sizeOfVocabulary = Integer.valueOf(rdline);
		
		for (int i = 0; i < sizeOfVocabulary; i++){
			String entry = dictionaryReader.readLine();
			int index = entry.indexOf(":\t");
			if (index > 0){
				String word = entry.substring(0, index);
				String definition = entry.substring(index + 2, entry.length());
				WordDefinition def = new WordDefinition(definition);
				word_definition_map.put(word, def);
			}else{
				System.out.println(entry);
			}
		}
	
		dictionaryReader.close();

	}
	
	public void populateNewModel_PromoteImmediateRelations(boolean isConcept){
		
		for (int i = 0; i < target_model.sizeOfVocabulary; i++){

			for (int j = 0; j <= i; j++){
			
				float sim = base_model.getSimilarity(i, j);
				float newSim = sim;
				
				//if (sim > 0 && sim != 1){
				if (sim != 1){
					
					String word1 = base_model.vocabulary[i];
					String word2 = base_model.vocabulary[j];
					String pos1 = word1.substring(word1.length() - 2, word1.length());
					String pos2 = word2.substring(word2.length() - 2, word2.length());
					
					WordDefinition def1 = word_definition_map.get(word1);
					WordDefinition def2 = word_definition_map.get(word2);

					if (!ignoredWords.contains(word1) && !ignoredWords.contains(word2)){
						
						if (pos1.equals(pos2)){
							
							if (def1.synonymSet.contains(word2) || def2.synonymSet.contains(word1)){
								
								if (isConcept && pos1.equals("NN") && sim < 0.1){
									// do nothing
								} else {
									newSim = sim + one_link_away_bonus;
									if (newSim > 1) newSim = 1;
								}
							
							
							}else if (pos1.equals("NN")){
	
								if (def1.setOfDefiningWords.contains(word2) && (def2.parentSet.contains(word1) || def2.grandParentSet.contains(word1))){
									newSim = sim + one_link_away_bonus;
									if (newSim > 1) newSim = 1;
		
								}else if (def2.setOfDefiningWords.contains(word1) && (def1.parentSet.contains(word2) || def1.grandParentSet.contains(word2))){
									newSim = sim + one_link_away_bonus;
									if (newSim > 1) newSim = 1;
								
								}else if (def1.parentSet.contains(word2) || def2.parentSet.contains(word1)){
									if ((def1.numberOfSenses <= 10 || def2.numberOfSenses <= 10) && sim < 0.15){
										
									}else{
										newSim = sim + two_links_away_bonus;
										if (newSim > 1) newSim = 1;
									}
								
								}else if (def1.grandParentSet.contains(word2) || def2.grandParentSet.contains(word1)){

									if ((def1.numberOfSenses <= 10 || def2.numberOfSenses <= 10) && sim < 0.075){
										
									}else{
										newSim = sim + three_links_away_bonus;
										if (newSim > 1) newSim = 1;
									}
								
								}else if (def1.setOfDefiningWords.contains(word2) || def2.setOfDefiningWords.contains(word1)){

									newSim = sim + three_links_away_bonus;
									if (newSim > 1) newSim = 1;
								
								}
								
								/*
								else if (def1.grandGrandParentSet.contains(word2) || def2.grandGrandParentSet.contains(word1)){

									if (def1.numberOfSenses <= 4 && def2.numberOfSenses <= 4 && sim < 0.1){
										
									}else{
										newSim = sim + four_links_away_bonus;
										if (newSim > 1) newSim = 1;
									}
								} 
								*/
								
							}else if (pos1.equals("VB")){
								
								if (def1.parentSet.contains(word2) || def2.parentSet.contains(word1)){
									newSim = sim + two_links_away_bonus;
									if (newSim > 1) newSim = 1;
								
								}else if (def1.setOfDefiningWords.contains(word2) || def2.setOfDefiningWords.contains(word1)) {
									
									if (sim < 0.5){
										newSim = sim + three_links_away_bonus;
										if (newSim > 1) newSim = 1;
									}
								}

							}else if (pos1.equals("JJ")){
								
								if (def1.parentSet.contains(word2) || def2.parentSet.contains(word1)){
									newSim = sim + two_links_away_bonus;
									if (newSim > 1) newSim = 1;
								
								}else if (def1.grandParentSet.contains(word2) || def2.grandParentSet.contains(word1)){
									newSim = sim + three_links_away_bonus;
									if (newSim > 1) newSim = 1;
									
								}else if (def1.setOfDefiningWords.contains(word2) || def2.setOfDefiningWords.contains(word1)) {
									newSim = sim + three_links_away_bonus;
									if (newSim > 1) newSim = 1;
								}
							}
							
						}else if (!isConcept) {
	
							if (def1.derivativeSet.contains(word2) || def2.derivativeSet.contains(word1)){
								//newSim = sim + one_link_away_bonus;
								newSim = sim + two_links_away_bonus;
								if (newSim > 1) newSim = 1;
							
							}else if (def1.setOfDefiningWords.contains(word2) || def2.setOfDefiningWords.contains(word1)) {
								newSim = sim + three_links_away_bonus;
								if (newSim > 1) newSim = 1;
							}
						}
					}
				}
				
				target_model.setSimilarity(i, j, newSim);
				
			}
			
			if (i % 1000 == 0)
				System.out.println(i + " rows have been processed.");
		}
		
	}
	
	public void populateNewModel_PromoteImmediateRelations2(boolean isConcept){
		
		for (int i = 0; i < target_model.sizeOfVocabulary; i++){

			for (int j = 0; j <= i; j++){
			
				float sim = base_model.getSimilarity(i, j);
				float newSim = sim;
				
				//if (sim > 0 && sim != 1){
				if (sim != 1){
					
					String word1 = base_model.vocabulary[i];
					String word2 = base_model.vocabulary[j];
					String pos1 = word1.substring(word1.length() - 2, word1.length());
					String pos2 = word2.substring(word2.length() - 2, word2.length());
					
					WordDefinition def1 = word_definition_map.get(word1);
					WordDefinition def2 = word_definition_map.get(word2);

					if (!ignoredWords.contains(word1) && !ignoredWords.contains(word2)){
						
						if (pos1.equals(pos2)){
							
							if (def1.synonymSet.contains(word2) || def2.synonymSet.contains(word1)){
								
								if (isConcept && pos1.equals("NN") && sim < 0.1){
									// do nothing
								} else {
									newSim = sim + one_link_away_bonus;
									//if (newSim > 1) newSim = 1;
								}
							
							
							}else if (pos1.equals("NN")){
	
								if (def1.setOfDefiningWords.contains(word2) && (def2.parentSet.contains(word1) || def2.grandParentSet.contains(word1))){
									newSim = sim + one_link_away_bonus;
									//if (newSim > 1) newSim = 1;
		
								}else if (def2.setOfDefiningWords.contains(word1) && (def1.parentSet.contains(word2) || def1.grandParentSet.contains(word2))){
									newSim = sim + one_link_away_bonus;
									//if (newSim > 1) newSim = 1;
								
								}else if (def1.parentSet.contains(word2) || def2.parentSet.contains(word1)){
									if ((def1.numberOfSenses <= 10 || def2.numberOfSenses <= 10) && sim < 0.15){
										
									}else{
										newSim = sim + two_links_away_bonus;
										//if (newSim > 1) newSim = 1;
									}
								
								}else if (def1.grandParentSet.contains(word2) || def2.grandParentSet.contains(word1)){

									if ((def1.numberOfSenses <= 10 || def2.numberOfSenses <= 10) && sim < 0.075){
										
									}else{
										newSim = sim + three_links_away_bonus;
										//if (newSim > 1) newSim = 1;
									}
								
								}else if (def1.setOfDefiningWords.contains(word2) || def2.setOfDefiningWords.contains(word1)){

									newSim = sim + three_links_away_bonus;
									//if (newSim > 1) newSim = 1;
								
								}
								
								/*
								else if (def1.grandGrandParentSet.contains(word2) || def2.grandGrandParentSet.contains(word1)){

									if (def1.numberOfSenses <= 4 && def2.numberOfSenses <= 4 && sim < 0.1){
										
									}else{
										newSim = sim + four_links_away_bonus;
										// if (newSim > 1) newSim = 1;
									}
								} 
								*/
								
							}else if (pos1.equals("VB")){
								
								if (def1.parentSet.contains(word2) || def2.parentSet.contains(word1)){
									newSim = sim + two_links_away_bonus;
									//if (newSim > 1) newSim = 1;
								
								}else if (def1.setOfDefiningWords.contains(word2) || def2.setOfDefiningWords.contains(word1)) {
									
									if (sim < 0.5){
										newSim = sim + three_links_away_bonus;
										//if (newSim > 1) newSim = 1;
									}
								}
							}
							
						}else if (!isConcept) {
	
							if (def1.derivativeSet.contains(word2) || def2.derivativeSet.contains(word1)){
								//newSim = sim + one_link_away_bonus;
								newSim = sim + two_links_away_bonus;
								//if (newSim > 1) newSim = 1;
							
							}else if (def1.setOfDefiningWords.contains(word2) || def2.setOfDefiningWords.contains(word1)) {
								newSim = sim + three_links_away_bonus;
								//if (newSim > 1) newSim = 1;
							}
						}
					}
					
				// if sim == 1
				} else {
					
					newSim = sim + one_link_away_bonus;
				}
				
				newSim = newSim / (1 + one_link_away_bonus);
				
				target_model.setSimilarity(i, j, newSim);
				
			}
			
			if (i % 1000 == 0)
				System.out.println(i + " rows have been processed.");
		}
		
	}
	
	
	public void populateNewModel_WordsHavingManySenses(){
		
		for (int i = 0; i < target_model.sizeOfVocabulary; i++){

			for (int j = 0; j <= i; j++){
			
				float sim = base_model.getSimilarity(i, j);
				float newSim = sim;
				
				if (sim > 0 && sim != 1){
					
					String word1 = base_model.vocabulary[i];
					String word2 = base_model.vocabulary[j];
					String pos1 = word1.substring(word1.length() - 2, word1.length());
					String pos2 = word2.substring(word2.length() - 2, word2.length());

					WordDefinition def1 = word_definition_map.get(word1);
					WordDefinition def2 = word_definition_map.get(word2);
					int senseNum1 = def1.numberOfSenses;
					int senseNum2 = def2.numberOfSenses;
					HashSet<String> substitutions1 = new HashSet<String>();
					HashSet<String> substitutions2 = new HashSet<String>();
						

					if (pos1.equals(pos2) && !ignoredWords.contains(word1) && !ignoredWords.contains(word2)){
						
						if (senseNum1 > 10){
							
							for (String synonym: def1.synonymSet){
								
								WordDefinition synonymDef = word_definition_map.get(synonym);
								if (synonymDef != null && (synonymDef.numberOfSenses <= 3 || senseNum1 / synonymDef.numberOfSenses >= 3)){
									if (base_model.getFrequency(synonym) > substitution_freq_threshold)
										substitutions1.add(synonym);
								}
							}

							/*
							for (String parent: def1.parentSet){
								
								WordDefinition parentDef = word_definition_map.get(parent);
								if (parentDef != null && (parentDef.numberOfSenses <= 3 || senseNum1 / parentDef.numberOfSenses >= 5)){
									if (base_model.getFrequency(parent) > 1000)
										substitutions1.add(parent);
								}
							}
							*/
						}

						if (senseNum2 > 10){
							
							for (String synonym: def2.synonymSet){
								
								WordDefinition synonymDef = word_definition_map.get(synonym);
								if (synonymDef != null && (synonymDef.numberOfSenses <= 3 || senseNum2 / synonymDef.numberOfSenses >= 3)){
									if (base_model.getFrequency(synonym) > substitution_freq_threshold)
										substitutions2.add(synonym);
								}
							}
							
							/*
							for (String parent: def2.parentSet){
								
								WordDefinition parentDef = word_definition_map.get(parent);
								if (parentDef != null && (parentDef.numberOfSenses <= 3 || senseNum2 / parentDef.numberOfSenses >= 5)){
									if (base_model.getFrequency(parent) > 1000)
										substitutions2.add(parent);
								}
							}
							*/
						}

						for (String sub1: substitutions1){

							if (sub1.equals(word2))
								continue;
							
							float testSim = base_model.getSimilarity(sub1, word2);
							if (testSim > newSim)
								newSim = testSim;
							
						}
						
						for (String sub2: substitutions2){
							
							if (sub2.equals(word1))
								continue;
							
							float testSim = base_model.getSimilarity(word1, sub2);
							if (testSim > newSim)
								newSim = testSim;
						}
					}
					
				}
				
				target_model.setSimilarity(i, j, newSim);
				
			}
			
			if (i % 1000 == 0)
				System.out.println(i + " rows have been processed.");
		}
		
	}
	
	public void populateNewModel_DifferentPOS(){
		
		for (int i = 0; i < target_model.sizeOfVocabulary; i++){

			for (int j = 0; j <= i; j++){
			
				float sim = base_model.getSimilarity(i, j);
				float newSim = sim;
				
				//if (sim > 0 && sim != 1){
				if (sim >= 0 && sim != 1){
					
					String word1 = base_model.vocabulary[i];
					String word2 = base_model.vocabulary[j];
					String pos1 = word1.substring(word1.length() - 2, word1.length());
					String pos2 = word2.substring(word2.length() - 2, word2.length());

					WordDefinition def1 = word_definition_map.get(word1);
					WordDefinition def2 = word_definition_map.get(word2);
					//int senseNum1 = def1.numberOfSenses;
					//int senseNum2 = def2.numberOfSenses;
					HashSet<String> substitutions1 = new HashSet<String>();
					HashSet<String> substitutions2 = new HashSet<String>();
						

					//if (!pos1.equals(pos2) && !ignoredWords.contains(word1) && !ignoredWords.contains(word2)){
					if (!ignoredWords.contains(word1) && !ignoredWords.contains(word2)){
						
						for (String derivative: def1.derivativeSet){
							if (base_model.getFrequency(derivative) > derivative_freq_threshold)
								
								if (def1.numberOfSenses > 10)
									substitutions1.add(derivative);
								else if (word1.length() >= 4 && derivative.length() >= 4 && word1.substring(0, 4).equals(derivative.substring(0, 4)))
									substitutions1.add(derivative);
						}


						for (String derivative: def2.derivativeSet){
							if (base_model.getFrequency(derivative) > derivative_freq_threshold)
								
								if (def2.numberOfSenses > 10)
									substitutions2.add(derivative);
								else if (word2.length() >= 4 && derivative.length() >= 4 && word2.substring(0, 4).equals(derivative.substring(0, 4)))
									substitutions2.add(derivative);
								
						}


						for (String sub1: substitutions1){

							if (sub1.equals(word2))
								continue;
							
							//int index1 = sub1.lastIndexOf('_');
							//int index2 = word2.lastIndexOf('_');
							float testSim;
							
							//if (sub1.substring(0, index1).equals(word2.substring(0, index2)))
							if (sub1.endsWith("_VB"))
								testSim = base_model.getSimilarity(sub1, word2) * base_model.getSimilarity(word1, sub1);
							else
								testSim = base_model.getSimilarity(sub1, word2) * base_model.getSimilarity(word1, sub1) / 2;
							
							if (testSim > newSim)
								newSim = testSim;
							
						}
						
						for (String sub2: substitutions2){
							
							if (sub2.equals(word1))
								continue;
							
							//int index2 = sub2.lastIndexOf('_');
							//int index1 = word1.lastIndexOf('_');
							float testSim;
							
							//if (sub2.substring(0, index2).equals(word1.substring(0, index1)))
							if (sub2.endsWith("_VB"))
								testSim = base_model.getSimilarity(word1, sub2) * base_model.getSimilarity(word2, sub2);
							else
								testSim = base_model.getSimilarity(word1, sub2) * base_model.getSimilarity(word2, sub2) / 2;
							
							if (testSim > newSim)
								newSim = testSim;
						}
					}
					
				}
				
				target_model.setSimilarity(i, j, newSim);
				
			}
			
			if (i % 1000 == 0)
				System.out.println(i + " rows have been processed.");
		}

		
	}


	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		SimilarityFactory test = new SimilarityFactory();
		System.out.println("loading done!");
		
		test.importDefinition();
		System.out.println("import done!");

		boolean isConcept = false;
		//test.populateNewModel_PromoteImmediateRelations(isConcept);
		//test.populateNewModel_WordsHavingManySenses();
		test.populateNewModel_DifferentPOS();
		
		System.out.println("Finished populating!");

		SimilarityArrayModel.writeModel("/home/lushan1/nlp/model/BigArray/20131001/webbase2012AllW5_S3", test.target_model);
		System.out.println("Finished serialization!");

		
	}

}
