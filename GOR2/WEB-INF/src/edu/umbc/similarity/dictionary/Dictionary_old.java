package edu.umbc.similarity.dictionary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;
import edu.stanford.nlp.process.Morphology;
import edu.umbc.dbpedia.model.LSA_Model;
import edu.umbc.dbpedia.model.SimilarityArrayModel;

public class Dictionary_old {

	public DictEntry[] dict;
	public int sizeOfVocabulary;
	public WordNetDatabase database;
	public HashSet<String> stopWords;
	public SimilarityArrayModel wiki5;
	//public LSA_Model gutenberg5;
	public String stopwordsFilename = "/home/lushan1/nlp/dictionary/stopwords";

	
	public Dictionary_old(String dictionary) throws IOException, Exception {
		// TODO Auto-generated constructor stub
		
		File dictionaryFile = new File(dictionary + ".dict");
		BufferedReader dictionaryReader = new BufferedReader(new FileReader(dictionaryFile), 1000000);

		String rdline;
		rdline = dictionaryReader.readLine();
		sizeOfVocabulary = Integer.valueOf(rdline);
		dict = new DictEntry[sizeOfVocabulary];
		
		for (int i = 0; i < sizeOfVocabulary; i++){
			String entry = dictionaryReader.readLine();
			int index = entry.indexOf(":\t");
			if (index > 0){
				DictEntry dict_entry = new DictEntry(entry.substring(0, index), entry.substring(index + 1, entry.length()));
				dict[i] = dict_entry;
			}else{
				// System.out.println(entry);
				DictEntry dict_entry = new DictEntry(entry, "");
				dict[i] = dict_entry;
			}
		}
	
		dictionaryReader.close();
		Arrays.sort(dict);
		
        System.setProperty("wordnet.database.dir", "/usr/share/wordnet-3.0/dict/");
        database = WordNetDatabase.getFileInstance();
        
        
		File stopwordsFile = new File(stopwordsFilename + ".stw");
		BufferedReader stopwordsReader = new BufferedReader(new FileReader(stopwordsFile));
		stopWords = new HashSet<String>();
		
		String stopword;
		while ((stopword = stopwordsReader.readLine()) != null){
			stopWords.add(stopword);
		}
		
		stopwordsReader.close();

		//gutenberg5 = new LSA_Model("/home/lushan1/nlp/model/SVD/Gutenberg2010AllW5");
		wiki5 = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/wiki5");

	}
	
	
	public void saveAs(String dictionary) throws IOException{
		
		FileWriter fileOfDictionary = new FileWriter(dictionary + ".dict");
		PrintWriter outOfDictionary = new PrintWriter(new BufferedWriter(fileOfDictionary, 1000000));
		
		outOfDictionary.println(dict.length);
		
		for (DictEntry entry:dict){
			outOfDictionary.print(entry.term);
			
			if (!entry.definition.equals("")){
				outOfDictionary.print(":\t");
				outOfDictionary.print(entry.definition);
			}
			
			outOfDictionary.println();
		}

		outOfDictionary.close();

	}
	
	public static boolean isAllUpperCase(String word){
		
		boolean result = true;
		boolean containLetter = false;
		
		if (word.length() == 0) return false;
		
		for (int i=0; i < word.length(); i++){
			
			if (Character.isLetter(word.charAt(i)))
				containLetter = true;
			
			if (Character.isLetter(word.charAt(i)) && !Character.isUpperCase(word.charAt(i))){
				result = false;
				break;
			}
		}

		boolean firstLetterValid = false;
		
		if (Character.isLetter(word.charAt(0)) || word.charAt(0) == '-' || word.charAt(0) == '\'')
			firstLetterValid = true;
		
		if (result && containLetter && firstLetterValid)
			return true;
		else
			return false;
	}
	
	public static void extractDictionaryFromWebster(String filename) throws IOException{
		
		
		Vector<DictEntry> collection = new Vector<DictEntry>();
		
		File WebsterFile = new File(filename);
		BufferedReader WebsterReader = new BufferedReader(new FileReader(WebsterFile), 100000);

		String rdline;
		
		String word = null;
		WebsterDefinition def = null;
		
		while((rdline = WebsterReader.readLine()) != null){
			
			if (isAllUpperCase(rdline) && rdline.length() < 30){
				
				if (word == null){

					word = rdline.toLowerCase();
					def = new WebsterDefinition();

				}else{

					if (collection.size() > 0){
						
						DictEntry last = collection.lastElement();
						
						if (last.term.equals(word)){
							last.definition += " " + def.definition;
						}else
							collection.add(new DictEntry(word, def.definition));
						
					}else
						collection.add(new DictEntry(word, def.definition));
					
					word = rdline.toLowerCase();
					def = new WebsterDefinition();
				}
				
			}else
				if (def != null)
					def.append(rdline);
		}

		Collections.sort(collection);
		
		//save results
		FileWriter fileOfDictionary = new FileWriter("webster.dict");
		PrintWriter outOfDictionary = new PrintWriter(new BufferedWriter(fileOfDictionary, 1000000));
		
		outOfDictionary.println(collection.size());
		
		for (DictEntry entry:collection){
			outOfDictionary.print(entry.term);
			
			if (!entry.definition.equals("")){
				outOfDictionary.print(":\t");
				outOfDictionary.print(entry.definition);
			}
			
			outOfDictionary.println();
		}

		outOfDictionary.close();

	}

	
	public HashSet<String> getDerivativeSet(String posWord, int wordnet_tag_count_threshold, HashSet<Synset>DerivativeSynsets){
		
		HashSet<String> derivativeSet = new HashSet<String>();
		
		int _index = posWord.indexOf("_");
		String word = posWord.substring(0, _index);
		String pos = posWord.substring(_index + 1, posWord.length());
		
		SynsetType type;
		
		if (pos.equals("NN"))
			type = SynsetType.NOUN;
		else if (pos.equals("VB"))
			type = SynsetType.VERB;
		else if (pos.equals("JJ"))
			type = SynsetType.ADJECTIVE;
		else if (pos.equals("RB"))
			type = SynsetType.ADVERB;
		else
			return derivativeSet;

		Synset[] synsets = database.getSynsets(word, type, false);

		for (int i = 0; i < synsets.length; i++) {

			int currentTagCount;
			try{
				currentTagCount = synsets[i].getTagCount(word);
			}catch (Exception e){
				currentTagCount = 0;
			}
			//int senseNumber = synsets[i].getSenseNumber(word);
			if (currentTagCount < wordnet_tag_count_threshold) // && senseNumber != 1)
				continue;

			String[] wordForms = synsets[i].getWordForms();
			
			for (String synonym : wordForms){
				
				//int senseNumberOfSynonym = synsets[i].getSenseNumber(synonym);
				if (synsets[i].getTagCount(synonym) < wordnet_tag_count_threshold) // && senseNumberOfSynonym != 1)
					continue;
				
				WordSense[] derivatives = synsets[i].getDerivationallyRelatedForms(synonym);
				
				for (WordSense derivative : derivatives){
					String derivedWord = derivative.getWordForm();
					derivativeSet.add(derivedWord);
				}
			}
		}
		
		return derivativeSet;
		
	}
	
	
	public HashSet<String> getSynonymSet(String posWord, int wordnet_tag_count_threshold, HashSet<String> glosses, HashSet<Synset> synonymSynsets, HashSet<String> derivativeSet, HashSet<Synset>derivativeSynsets, int[] numberOfSenses){
		
		HashSet<String> synonymSet = new HashSet<String>();
		
		int _index = posWord.indexOf("_");
		String word = posWord.substring(0, _index);
		String pos = posWord.substring(_index + 1, posWord.length());
		
		SynsetType type;
		
		if (pos.equals("NN"))
			type = SynsetType.NOUN;
		else if (pos.equals("VB"))
			type = SynsetType.VERB;
		else if (pos.equals("JJ"))
			type = SynsetType.ADJECTIVE;
		else if (pos.equals("RB"))
			type = SynsetType.ADVERB;
		else
			return synonymSet;

		Synset[] synsets = database.getSynsets(word, type, false);
		numberOfSenses[0] = synsets.length;
		
		int currentTagCount = 0;
		int lastTagCount = 0;
		
		for (int i = 0; i < synsets.length; i++) {
			
			try{
				currentTagCount = synsets[i].getTagCount(word);
			}catch (Exception e){
				currentTagCount = 0;
			}
			
			int senseNumber = i + 1;

			if (currentTagCount >= wordnet_tag_count_threshold || senseNumber == 1 
					|| (senseNumber == 2 
							&& (lastTagCount <= 5 || (currentTagCount > 0 && lastTagCount / currentTagCount <= 10)))) {
				
				String[] wordForms = synsets[i].getWordForms();
				glosses.add(posWord + ": " + synsets[i].getDefinition());
				synonymSynsets.add(synsets[i]);
				
				for (String wordForm : wordForms){
					
					String synonym = wordForm + "_" + pos;
					
					if (synonym.equals(posWord)){
					
						WordSense[] derivatives = synsets[i].getDerivationallyRelatedForms(wordForm);
						
						for (WordSense derivative : derivatives){
							
							String derivedWord = derivative.getWordForm();
							Synset derivativeSynset = derivative.getSynset();
							SynsetType derivative_type = derivativeSynset.getType();

							//int senseNumberOfDerivative = derivativeSynset.getSenseNumber(derivedWord);
							//int tagCountOfDerivative = derivativeSynset.getTagCount(derivedWord);

							//if (tagCountOfDerivative >= wordnet_tag_count_threshold || senseNumberOfDerivative == 1 || (senseNumberOfDerivative <= 3 && tagCountOfDerivative > 0)){														
								if (derivative_type == SynsetType.NOUN && wiki5.getSimilarity(posWord, derivedWord + "_NN") > 0.1){
									derivativeSet.add(derivedWord + "_NN");
									derivativeSynsets.add(derivative.getSynset());
								}else if (derivative_type == SynsetType.VERB && wiki5.getSimilarity(posWord, derivedWord + "_VB") > 0.1){
									derivativeSet.add(derivedWord + "_VB");
									derivativeSynsets.add(derivative.getSynset());
								}
							//}
						}

						continue;
					}
						
					int senseNumberOfSynonym = synsets[i].getSenseNumber(wordForm);
					int tagCountOfSynonym = synsets[i].getTagCount(wordForm);
					
					if (tagCountOfSynonym >= wordnet_tag_count_threshold || senseNumberOfSynonym == 1 || (senseNumberOfSynonym == 2 && tagCountOfSynonym > 0)){

						synonymSet.add(synonym);
						
						int index = wordForm.indexOf(' ');
						if (index > 0){
							glosses.add(posWord + ": " +  wordForm);
						}

						WordSense[] derivatives = synsets[i].getDerivationallyRelatedForms(wordForm);
						
						for (WordSense derivative : derivatives){
							
							String derivedWord = derivative.getWordForm();
							Synset derivativeSynset = derivative.getSynset();
							SynsetType derivative_type = derivativeSynset.getType();
							
							int senseNumberOfDerivative = derivativeSynset.getSenseNumber(derivedWord);
							int tagCountOfDerivative = derivativeSynset.getTagCount(derivedWord);
							
							if (tagCountOfDerivative >= wordnet_tag_count_threshold || senseNumberOfDerivative == 1 || (senseNumberOfDerivative <= 3 && tagCountOfDerivative > 0)){														

								if (derivative_type == SynsetType.NOUN && wiki5.getSimilarity(posWord, derivedWord + "_NN") > 0.1){
									derivativeSet.add(derivedWord + "_NN");
									derivativeSynsets.add(derivative.getSynset());
								}else if (derivative_type == SynsetType.VERB && wiki5.getSimilarity(posWord, derivedWord + "_VB") > 0.1){
									derivativeSet.add(derivedWord + "_VB");
									derivativeSynsets.add(derivative.getSynset());
								}
							}
						}
						
						/*
						if (type == SynsetType.NOUN){
							int index = wordForm.indexOf(' ');
							if (index > 0 && !wordForm.contains(" of ")){
								String baseWord = wordForm.substring(index + 1, wordForm.length()) + "_" + pos;
								if (!baseWord.equals(posWord))
									synonymSet.add(baseWord);
							}
						}
						*/
					}
				}
			}
			
			lastTagCount = currentTagCount;
		}
		
		return synonymSet;
		
	}

	
	public HashSet<String> getHypernymSet(String posWord, int wordnet_tag_count_threshold, HashSet<Synset> setOfSynonymSynsets, HashSet<Synset> setOfHypernymSynsets, HashSet<String> glosses, HashSet<NounSynset> topLevelNounSynsets){
		
		HashSet<String> hypernymSet = new HashSet<String>();
		
		for (Synset synset: setOfSynonymSynsets){
			
			SynsetType senseType = synset.getType();
			
			if (senseType == SynsetType.NOUN){
				
				NounSynset[] hypernymSynsets = ((NounSynset)synset).getHypernyms();
				
				for (NounSynset hypernymSynset : hypernymSynsets){
					
					if (topLevelNounSynsets.contains(hypernymSynset))
						continue;
					
					setOfHypernymSynsets.add(hypernymSynset);
					
					String[] wordForms = hypernymSynset.getWordForms();
					
					for (String wordForm : wordForms){

						String hypernym = wordForm + "_NN";
						
						if (hypernym.equals(posWord))
							continue;

						int senseNumberOfHypernym = hypernymSynset.getSenseNumber(wordForm);
						int tagCountOfHypernym = hypernymSynset.getTagCount(wordForm);
						
						if (wordForms.length == 1 || tagCountOfHypernym >= wordnet_tag_count_threshold || senseNumberOfHypernym == 1 || (senseNumberOfHypernym == 2 && tagCountOfHypernym > 0)){

							hypernymSet.add(hypernym);
							
							int index = wordForm.indexOf(' ');
							if (index > 0){
								glosses.add(hypernym + ": " +  wordForm);
							}
							
						}

					}
					
					glosses.add(wordForms[0] + "_NN: " + hypernymSynset.getDefinition());
				}
			
			}else if (senseType == SynsetType.VERB){
				
				VerbSynset[] hypernymSynsets = ((VerbSynset)synset).getHypernyms();
				
				for (VerbSynset hypernymSynset : hypernymSynsets){
					
					setOfHypernymSynsets.add(hypernymSynset);
					
					String[] wordForms = hypernymSynset.getWordForms();
					
					for (String wordForm : wordForms){

						String hypernym = wordForm + "_VB";
						
						if (hypernym.equals(posWord))
							continue;

						int senseNumberOfHypernym = hypernymSynset.getSenseNumber(wordForm);
						int tagCountOfHypernym = hypernymSynset.getTagCount(wordForm);

						if (wordForms.length == 1 || tagCountOfHypernym >= wordnet_tag_count_threshold || senseNumberOfHypernym == 1 || (senseNumberOfHypernym == 2 && tagCountOfHypernym > 0)){

							hypernymSet.add(hypernym);
							
							int index = wordForm.indexOf(' ');
							if (index > 0){
								glosses.add(hypernym + ": " +  wordForm);
							}

						}
					}
					
					glosses.add(wordForms[0] + "_VB: " + hypernymSynset.getDefinition());
				}
			}
			
		}
		
		return hypernymSet;
	}
	
	/*
	public HashSet<String> getHypernymSet(String word, int wordnet_tag_count_threshold, HashSet<String> glosses){
		
		HashSet<String> hypernymSet = new HashSet<String>();
		
		Synset[] synsets = database.getSynsets(word);
		
		for (int i = 0; i < synsets.length; i++) {
			
			int currentTagCount;
			try{
				currentTagCount = synsets[i].getTagCount(word);
			}catch (Exception e){
				currentTagCount = 0;
			}
			//int senseNumber = synsets[i].getSenseNumber(word);
			if (currentTagCount < wordnet_tag_count_threshold) // && senseNumber != 1)
				continue;

			SynsetType senseType = synsets[i].getType();
			
			if (senseType == SynsetType.NOUN){
				
				NounSynset[] hypernymSynsets = ((NounSynset)synsets[i]).getHypernyms();
				
				for (NounSynset hypernymSynset : hypernymSynsets){
					
					String[] wordForms = hypernymSynset.getWordForms();
					
					for (String wordForm : wordForms){

						//int senseNumberOfHypernym = hypernymSynset.getSenseNumber(wordForm);

						if (hypernymSynset.getTagCount(wordForm) < wordnet_tag_count_threshold) // && senseNumberOfHypernym != 1)
							continue;

						hypernymSet.add(wordForm);
						
						int index = wordForm.indexOf(' ');
						if (index > 0)
							hypernymSet.add(wordForm.substring(index + 1, wordForm.length()));

					}
					
					glosses.add(hypernymSynset.getWordForms()[0] + ":" + hypernymSynset.getDefinition());
				}
			
			}else if (senseType == SynsetType.VERB){
				
				VerbSynset[] hypernymSynsets = ((VerbSynset)synsets[i]).getHypernyms();
				
				for (VerbSynset hypernymSynset : hypernymSynsets){
					
					String[] wordForms = hypernymSynset.getWordForms();
					
					for (String wordForm : wordForms){

						//int senseNumberOfHypernym = hypernymSynset.getSenseNumber(wordForm);

						if (hypernymSynset.getTagCount(wordForm) < wordnet_tag_count_threshold) // && senseNumberOfHypernym != 1)
							continue;

						hypernymSet.add(wordForm);
					}
					
					glosses.add(hypernymSynset.getWordForms()[0] + ":" + hypernymSynset.getDefinition());
				}
			}
			
		}
		
		return hypernymSet;
	}
	*/
	
	public HashSet<String> getChildrenSet(String word, int wordnet_tag_count_threshold, HashSet<String> glosses){
		
		HashSet<String> childrenSet = new HashSet<String>();
		
		Synset[] synsets = database.getSynsets(word);
		
		for (int i = 0; i < synsets.length; i++) {
			
			int currentTagCount;
			try{
				currentTagCount = synsets[i].getTagCount(word);
			}catch (Exception e){
				currentTagCount = 0;
			}
			//int senseNumber = synsets[i].getSenseNumber(word);
			if (currentTagCount < wordnet_tag_count_threshold) // && senseNumber != 1)
				continue;

			SynsetType senseType = synsets[i].getType();
			
			if (senseType == SynsetType.NOUN){
				
				NounSynset[] hyponymSynsets = ((NounSynset)synsets[i]).getHyponyms();
				
				for (NounSynset hyponymSynset : hyponymSynsets){
					
					String[] wordForms = hyponymSynset.getWordForms();
					
					boolean added = false;
					
					for (String wordForm : wordForms){

						//int senseNumberOfHyponym = hyponymSynset.getSenseNumber(wordForm);

						if (hyponymSynset.getTagCount(wordForm) < wordnet_tag_count_threshold) // && senseNumberOfHyponym != 1)
							continue;

						int index = wordForm.indexOf(' ');
						if (index > 0)
							childrenSet.add(wordForm.substring(index + 1, wordForm.length()));
						else
							childrenSet.add(wordForm);

						if (added == false){
							glosses.add(hyponymSynset.getWordForms()[0] + ":" + hyponymSynset.getDefinition());
							added = true;
						}
					}
					
				}
			
			}else if (senseType == SynsetType.VERB){
				
				VerbSynset[] hyponymSynsets = ((VerbSynset)synsets[i]).getTroponyms();
				
				for (VerbSynset hyponymSynset : hyponymSynsets){
					
					String[] wordForms = hyponymSynset.getWordForms();

					boolean added = false;

					for (String wordForm : wordForms){

						//int senseNumberOfHyponym = hyponymSynset.getSenseNumber(wordForm);

						if (hyponymSynset.getTagCount(wordForm) < wordnet_tag_count_threshold) // && senseNumberOfHyponym != 1)
							continue;

						childrenSet.add(wordForm);
						
						if (added == false){
							glosses.add(hyponymSynset.getWordForms()[0] + ":" + hyponymSynset.getDefinition());
							added = true;
						}
					}
					
				}
			}
			
		}
		
		return childrenSet;
	}



	/*
	public float getMaxSimilarity(String word1, String word2){
		
		return wikipedia5.getWordSimilarity(word1, word2);

		float sim1 = wikipedia5.getWordSimilarity(word1, word2);
		float sim2 = gutenberg5.getWordSimilarity(word1, word2);
		
		if (sim1 >= sim2)
			return sim1;
		else
			return sim2;
	}
	*/
	
	public HashSet<NounSynset> getTopLevelSynsets(NounSynset root, int depth){

		if (depth == 0)
			return new HashSet<NounSynset>();
		else{
			HashSet<NounSynset> topLevelSynsets = new HashSet<NounSynset>();
			
			NounSynset[] children = root.getHyponyms();
			
			for (NounSynset child: children)
				topLevelSynsets.add(child);
			
			int level = depth - 1;
			
			for (NounSynset child : children){
				topLevelSynsets.addAll(getTopLevelSynsets(child, level));
			}

			return topLevelSynsets;

		}
		
	}
	
	public void addWordNet(int wordnet_tag_count_threshold, float defining_word_sim_threshold) {
		
		//Morphology morpha = new Morphology();
		
		NounSynset root = (NounSynset) database.getSynsets("entity", SynsetType.NOUN, false)[0];
		
		HashSet<NounSynset> topLevelSynsets = getTopLevelSynsets(root, 2);
		
		for (DictEntry entry:dict){
			
			HashSet<String> wordnetGlosses = new HashSet<String>();

			HashSet<Synset> setOfDerivativeSynsets = new HashSet<Synset>();
			HashSet<String> derivativeSet = new HashSet<String>();
			HashSet<Synset> setOfSynonymSynsets = new HashSet<Synset>();
			int[] numberOfsenses = new int[1];
			HashSet<String> synonymSet = getSynonymSet(entry.term, wordnet_tag_count_threshold, wordnetGlosses, setOfSynonymSynsets, derivativeSet, setOfDerivativeSynsets, numberOfsenses);
			
			HashSet<String> synonymOfDerivativeSet = new HashSet<String>();
			//for (String derivative: derivativeSet){
			//	synonymOfDerivativeSet.addAll(getSynonymSet(derivative, wordnet_tag_count_threshold, wordnetGlosses));
			//}
			
			//HashSet<String> hypernymSet = new HashSet<String>();
			HashSet<Synset> setOfHypernymSynsets = new HashSet<Synset>();
			HashSet<String> parentSet = getHypernymSet(entry.term, wordnet_tag_count_threshold, setOfSynonymSynsets, setOfHypernymSynsets, wordnetGlosses, topLevelSynsets);
			
			//HashSet<String> hypernymOfHypernymSet = new HashSet<String>();
			//for (String hypernym: hypernymSet){
			//	hypernymOfHypernymSet.addAll(getHypernymSet(hypernym, wordnet_tag_count_threshold, wordnetGlosses));
			//}
			HashSet<Synset> setOfHypernymOfHypernymSynsets = new HashSet<Synset>();
			HashSet<String> grandParentSet = getHypernymSet(entry.term, wordnet_tag_count_threshold, setOfHypernymSynsets, setOfHypernymOfHypernymSynsets, wordnetGlosses, topLevelSynsets);
			
			/*
			HashSet<Synset> setOfHypernymOfHypernymOfHypernymSynsets = new HashSet<Synset>();
			HashSet<String> hypernymOfHypernymOfHypernymSet = getHypernymSet(entry.term, wordnet_tag_count_threshold, setOfHypernymOfHypernymSynsets, setOfHypernymOfHypernymOfHypernymSynsets, wordnetGlosses, topLevelSynsets);
			*/
			
			HashSet<String> derivativeOfHypernymSet = new HashSet<String>();
			//for (String hypernym: hypernymSet){
			//	derivativeOfHypernymSet.addAll(getDerivativeSet(hypernym, wordnet_tag_count_threshold));
			//}
			
			HashSet<String> synonymOfHypernymSet = new HashSet<String>();
			//for (String hypernym: hypernymSet){
			//	synonymOfHypernymSet.addAll(getSynonymSet(hypernym, wordnet_tag_count_threshold, wordnetGlosses));
			//}
			
			HashSet<String> childrenSet = new HashSet<String>();
			//HashSet<String> childrenSet = getChildrenSet(entry.term, wordnet_tag_count_threshold, wordnetGlosses);
			
			HashSet<String> setOfDefiningWords = new HashSet<String>();
			
			HashSet<String> setOfFrequentWords = new HashSet<String>();
			
			/*
			HashMap<String, Integer> wordCountMap = new HashMap<String, Integer>();
	
			for (String wordGloss : wordnetGlosses){
				
				String gloss = wordGloss.substring(wordGloss.indexOf(':') + 1, wordGloss.length());
				
				TermTokenizer tt = new TermTokenizer(this, gloss, morpha);
				String term;
				int numberOfNonStopWords = 0;
				while ((term = tt.nextTerm()) != null){
					
					if (stopWords.contains(term))
						continue;
					
					numberOfNonStopWords ++;
					
					if (wikipedia5.getWordFrequency(term) > 400000)
						continue;
					
					int count = wordCountMap.containsKey(term) ? wordCountMap.get(term) : 0;
					wordCountMap.put(term, count + 1);
				}
				
				tt = new TermTokenizer(this, gloss, morpha);
				float maxSim = 0;
				float secondMaxSim = 0;
				String maxTerm = null;
				String secondMaxTerm = null;
				int numOfAddedTerms = 0;
				
				while ((term = tt.nextTerm()) != null){
					
					if (stopWords.contains(term))
						continue;
					
					float sim = getMaxSimilarity(term, entry.term);
					
					if (sim > maxSim){
						secondMaxSim = maxSim;
						secondMaxTerm = maxTerm;
						maxSim = sim;
						maxTerm = term;
					}else if (sim > secondMaxSim){
						secondMaxSim = sim;
						secondMaxTerm = term;
					}
					
					if (sim > defining_word_sim_threshold){
						setOfDefiningWords.add(term);
						numOfAddedTerms ++;
					}
				}
				
				if (numberOfNonStopWords <= 3){
					if (maxSim > 0.10)
						setOfDefiningWords.add(maxTerm);
				}else{
					if (maxSim > 0.10)
						setOfDefiningWords.add(maxTerm);
					if (secondMaxSim > 0.10)
						setOfDefiningWords.add(secondMaxTerm);
				}
			}
			
			for (Map.Entry<String, Integer> item: wordCountMap.entrySet()){
				
				if (item.getValue() >= 3){
					
					setOfFrequentWords.add(item.getKey());
				}
			}
			*/
			
			WordDefinition wordDef = new WordDefinition(numberOfsenses[0], derivativeSet, synonymSet, synonymOfDerivativeSet, parentSet, grandParentSet, derivativeOfHypernymSet, synonymOfHypernymSet, childrenSet, setOfDefiningWords, setOfFrequentWords);
			
			entry.definition = wordDef.serialize();
			
			System.out.println(entry.term + ": " + entry.definition);
		
		}
		
	}
	
	


	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Dictionary_old dict = new Dictionary_old("/home/lushan1/nlp/dictionary/vocabulary");
		//Dictionary dict = new Dictionary("/home/lushan1/nlp/dictionary/test");
		System.out.println(dict.dict[0]);
		//System.out.println(dict.dict[1]);
		
		dict.addWordNet(5, (float) 0.2);
		dict.saveAs("/home/lushan1/nlp/dictionary/wordnet");
		System.out.println("done!");
		
		
		//Dictionary.extractDictionaryFromWebster("/home/lushan1/dictionary/29765.dict");
	}

}
