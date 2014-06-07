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

import edu.smu.tspell.wordnet.AdjectiveSynset;
import edu.smu.tspell.wordnet.AdverbSynset;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.umbc.dbpedia.model.LSA_Model;
import edu.umbc.dbpedia.model.SimilarityArrayModel;
import edu.umbc.nlp.util.MyHeaderFinder;

public class Dictionary2 {

	public DictEntry[] dict;
	public int sizeOfVocabulary;
	public WordNetDatabase database;
	public HashSet<String> stopWords;
	public SimilarityArrayModel sim_model;
	public SimilarityArrayModel sim_model2;
	public static final HashSet<String> ignoredNounHeaders = new HashSet<String>(Arrays.asList( 
															"object", "feature", "attribute", "object",  "substance", "entity", "relation", "thing", "something", "anything", "process", "event", "form", "concept",
															"amount", "totality", "one", "term", "activity", "body", "way", "list", "action", "formation", "act", "shape", "use", "entry", "unit", 
															"reference", "name",
															"set","group", "collection", 
															"piece", "part", "fraction", "portion", "member", "branch",
															"output", "result",
															"state", "quality", "level",
															"opposite",
															"sort", "type", "kind", "line", "category", "class"));
	
	
	public static final HashSet<String> ignoredNounHeadersForChildren = new HashSet<String>(Arrays.asList( 	
															"object", "feature", "attribute", "object",  "substance", "entity", "relation", "thing", "something", "anything", "process", "event", "form", "concept",
															"amount", "totality", "one", "term", "activity", "body", "way", "list", "action", "formation", "act", "shape", "use", "entry", "unit",
															"reference", "name",
															"set","group", "collection", 
															"piece", "part", "fraction", "portion", "member", "branch",
															"output", "result",
															"state", "quality", "level",
															"opposite",
															"sort", "type", "kind", "line", "category", "class",
															"woman", "man", "female", "male"));

	//public int adj_vb_discriminator_threshold = 500;
	//public LSA_Model gutenberg5;
	public String stopwordsFilename = "/home/lushan1/nlp/dictionary/stopwords";
	
	public double derivative_threshold = 0.1;

	
	public Dictionary2(String dictionary) throws IOException, Exception {
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
		//sim_model = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/wiki5_outlier_gutn");
		sim_model = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/webbase2012AllW5");
		sim_model2 = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/webbase2012AllW2");
		//sim_model = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/Gigawords2009AllW5");
		//sim_model = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/webbase2012AllPlusV2W5");
		//sim_model = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/webbase2012AllPlusW5");
		//sim_model = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/webbase2012Allv2W5");

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
		
		if (synsets.length == 0 && type == SynsetType.ADJECTIVE){
			type = SynsetType.ADJECTIVE_SATELLITE;
			synsets = database.getSynsets(word, type, false);
		}


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
		
		if (synsets.length == 0 && type == SynsetType.ADJECTIVE){
			type = SynsetType.ADJECTIVE_SATELLITE;
			synsets = database.getSynsets(word, type, false);
			numberOfSenses[0] = synsets.length;
		}
		
		int currentTagCount = 0;
		int lastTagCount = 0;
		boolean isFirstForm;
		int RankOfZeroTagCount = 0;
		
		for (int i = 0; i < synsets.length; i++) {
			
			try{
				currentTagCount = synsets[i].getTagCount(word);
			}catch (Exception e){
				currentTagCount = 0;
			}
			
			if (currentTagCount == 0)
				RankOfZeroTagCount ++;
			
			int senseNumber = i + 1;
			
			String[] wordForms = synsets[i].getWordForms();
			
			if (word.equals(wordForms[0]))
				isFirstForm = true;
			else
				isFirstForm = false;
			
			//if (currentTagCount >= wordnet_tag_count_threshold || senseNumber == 1 || (senseNumber <=5 && isFirstForm)) {
			//if (currentTagCount >= wordnet_tag_count_threshold || senseNumber == 1 || ((currentTagCount > 0 || RankOfZeroTagCount <= 2) && isFirstForm)) {
			if (currentTagCount >= wordnet_tag_count_threshold || senseNumber == 1 || ((currentTagCount > 0 || RankOfZeroTagCount <= 2) && senseNumber <=7 && isFirstForm) 
					|| type == SynsetType.ADJECTIVE || type == SynsetType.ADJECTIVE_SATELLITE || type == SynsetType.ADVERB) {
				
				String definition = synsets[i].getDefinition().trim();
				
				/*
				if (definition.startsWith("(")){
					definition = definition.substring(definition.indexOf(')') + 1, definition.length()).trim();
				}
				*/
				
				if (!definition.contains("("))
					glosses.add(posWord + ": " + definition);
				
				
				
				synonymSynsets.add(synsets[i]);
				
				//for (String wordForm : wordForms){
				for (int j = 0; j < wordForms.length; j++){
					
					String synonym = wordForms[j] + "_" + pos;
					
					if (synonym.equals(posWord)){
						
						WordSense[] derivatives;
					
						if (type == SynsetType.ADVERB){
							derivatives = ((AdverbSynset) synsets[i]).getPertainyms(wordForms[j]);
						}else
							derivatives = synsets[i].getDerivationallyRelatedForms(wordForms[j]);
						
						for (WordSense derivative : derivatives){
							
							String derivedWord = derivative.getWordForm();
							Synset derivativeSynset = derivative.getSynset();
							SynsetType derivative_type = derivativeSynset.getType();

							//int senseNumberOfDerivative = derivativeSynset.getSenseNumber(derivedWord);
							//int tagCountOfDerivative = derivativeSynset.getTagCount(derivedWord);

							//if (tagCountOfDerivative >= wordnet_tag_count_threshold || senseNumberOfDerivative == 1 || (senseNumberOfDerivative <= 3 && tagCountOfDerivative > 0)){														
								if (derivative_type == SynsetType.NOUN && getSimilarity(posWord, derivedWord + "_NN") > derivative_threshold){
									derivativeSet.add(derivedWord + "_NN");
									derivativeSynsets.add(derivative.getSynset());
								}else if (derivative_type == SynsetType.VERB && getSimilarity(posWord, derivedWord + "_VB") > derivative_threshold){
									derivativeSet.add(derivedWord + "_VB");
									derivativeSynsets.add(derivative.getSynset());
								}else if ((derivative_type == SynsetType.ADJECTIVE || derivative_type == SynsetType.ADJECTIVE_SATELLITE) && getSimilarity(posWord, derivedWord + "_JJ") > derivative_threshold){
									derivativeSet.add(derivedWord + "_JJ");
									derivativeSynsets.add(derivative.getSynset());
								}
							//}
						}
						

						continue;
					}
						
					int senseNumberOfSynonym = synsets[i].getSenseNumber(wordForms[j]);
					int tagCountOfSynonym = synsets[i].getTagCount(wordForms[j]);
					boolean isFirstFormOfSynonym;
					
					if (j == 0)
						isFirstFormOfSynonym = true;
					else
						isFirstFormOfSynonym = false;
					
					//if (tagCountOfSynonym >= wordnet_tag_count_threshold || senseNumberOfSynonym == 1 || (senseNumberOfSynonym == 2 && tagCountOfSynonym > 0) || (senseNumberOfSynonym <= 5 && isFirstFormOfSynonym)){
					if (tagCountOfSynonym >= wordnet_tag_count_threshold || tagCountOfSynonym > currentTagCount || senseNumberOfSynonym == 1 || (senseNumberOfSynonym == 2 && tagCountOfSynonym > 0) || (tagCountOfSynonym > 0 && senseNumberOfSynonym <= 7 && isFirstFormOfSynonym)
							|| type == SynsetType.ADJECTIVE || type == SynsetType.ADJECTIVE_SATELLITE || type == SynsetType.ADVERB){

						synonymSet.add(synonym);
						
						int index = wordForms[j].indexOf(' ');
						//if (index > 0 && j < 2){
						if (index > 0){
							glosses.add(posWord + "*: " +  wordForms[j]);
						}

						WordSense[] derivatives;
						
						if (type == SynsetType.ADVERB){
							derivatives = ((AdverbSynset) synsets[i]).getPertainyms(wordForms[j]);
						}else
							derivatives = synsets[i].getDerivationallyRelatedForms(wordForms[j]);
						
						for (WordSense derivative : derivatives){
							
							String derivedWord = derivative.getWordForm();
							Synset derivativeSynset = derivative.getSynset();
							SynsetType derivative_type = derivativeSynset.getType();
							
							int senseNumberOfDerivative = derivativeSynset.getSenseNumber(derivedWord);
							int tagCountOfDerivative = derivativeSynset.getTagCount(derivedWord);
							
							if (tagCountOfDerivative >= wordnet_tag_count_threshold || senseNumberOfDerivative == 1 || (senseNumberOfDerivative <= 3 && tagCountOfDerivative > 0) 
									|| type == SynsetType.ADJECTIVE || type == SynsetType.ADJECTIVE_SATELLITE || type == SynsetType.ADVERB){														

								if (derivative_type == SynsetType.NOUN && getSimilarity(posWord, derivedWord + "_NN") > derivative_threshold){
									derivativeSet.add(derivedWord + "_NN");
									derivativeSynsets.add(derivative.getSynset());
								}else if (derivative_type == SynsetType.VERB && getSimilarity(posWord, derivedWord + "_VB") > derivative_threshold){
									derivativeSet.add(derivedWord + "_VB");
									derivativeSynsets.add(derivative.getSynset());
								}else if ((derivative_type == SynsetType.ADJECTIVE || derivative_type == SynsetType.ADJECTIVE_SATELLITE) && getSimilarity(posWord, derivedWord + "_JJ") > derivative_threshold){
									derivativeSet.add(derivedWord + "_JJ");
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
					
					//for (String wordForm : wordForms){
					for (int j = 0; j < wordForms.length; j++){

						String hypernym = wordForms[j] + "_NN";
						
						if (hypernym.equals(posWord))
							continue;

						int senseNumberOfHypernym = hypernymSynset.getSenseNumber(wordForms[j]);
						int tagCountOfHypernym = hypernymSynset.getTagCount(wordForms[j]);
						
						boolean isFirstFormOfHypernym;
						
						if (j == 0)
							isFirstFormOfHypernym = true;
						else
							isFirstFormOfHypernym = false;

						
						//if (wordForms.length == 1 || tagCountOfHypernym >= wordnet_tag_count_threshold || senseNumberOfHypernym == 1 || (senseNumberOfHypernym == 2 && tagCountOfHypernym > 0) || (senseNumberOfHypernym <= 5 && isFirstFormOfHypernym)){
						if (wordForms.length == 1 || tagCountOfHypernym >= wordnet_tag_count_threshold || senseNumberOfHypernym == 1 || (senseNumberOfHypernym == 2 && tagCountOfHypernym > 0) || ((tagCountOfHypernym > 0 || senseNumberOfHypernym == 3) && isFirstFormOfHypernym && senseNumberOfHypernym <= 7)){

							hypernymSet.add(hypernym);
							
							int index = wordForms[j].indexOf(' ');
							//if (index > 0 && j < 2){
							if (index > 0){
								glosses.add(hypernym + "*: " +  wordForms[j]);
							}
						}
					}
					
					String definition = hypernymSynset.getDefinition().trim();
					
					/*
					if (definition.startsWith("(")){
						definition = definition.substring(definition.indexOf(')') + 1, definition.length()).trim();
					}*/
					if (!definition.contains("("))
						glosses.add(wordForms[0] + "_NN: " + definition);
				}
			
			}else if (senseType == SynsetType.VERB){
				
				VerbSynset[] hypernymSynsets = ((VerbSynset)synset).getHypernyms();
				
				for (VerbSynset hypernymSynset : hypernymSynsets){
					
					setOfHypernymSynsets.add(hypernymSynset);
					
					String[] wordForms = hypernymSynset.getWordForms();
					
					//for (String wordForm : wordForms){
					for (int j = 0; j < wordForms.length; j++){

						String hypernym = wordForms[j] + "_VB";
						
						if (hypernym.equals(posWord))
							continue;

						int senseNumberOfHypernym = hypernymSynset.getSenseNumber(wordForms[j]);
						int tagCountOfHypernym = hypernymSynset.getTagCount(wordForms[j]);
						
						boolean isFirstFormOfHypernym;
						
						if (j == 0)
							isFirstFormOfHypernym = true;
						else
							isFirstFormOfHypernym = false;

						//if (wordForms.length == 1 || tagCountOfHypernym >= wordnet_tag_count_threshold || senseNumberOfHypernym == 1 || (senseNumberOfHypernym == 2 && tagCountOfHypernym > 0) || (senseNumberOfHypernym <= 5 && isFirstFormOfHypernym)){
						if (wordForms.length == 1 || tagCountOfHypernym >= wordnet_tag_count_threshold || senseNumberOfHypernym == 1 || (senseNumberOfHypernym == 2 && tagCountOfHypernym > 0) || ((tagCountOfHypernym > 0 || senseNumberOfHypernym == 3) && isFirstFormOfHypernym && senseNumberOfHypernym <= 7)){

							hypernymSet.add(hypernym);
							
							int index = wordForms[j].indexOf(' ');
							//if (index > 0 && j < 2){
							if (index > 0){
								glosses.add(hypernym + "*: " +  wordForms[j]);
							}

						}
					}
					
					String definition = hypernymSynset.getDefinition().trim();
					
					/*
					if (definition.startsWith("(")){
						definition = definition.substring(definition.indexOf(')') + 1, definition.length()).trim();
					}
					*/
					
					if (!definition.contains("("))
						glosses.add(wordForms[0] + "_VB: " + definition);
				}
				
				
			}else if (senseType == SynsetType.ADJECTIVE || senseType == SynsetType.ADJECTIVE_SATELLITE){
				
				AdjectiveSynset[] hypernymSynsets = ((AdjectiveSynset)synset).getSimilar();
				
				for (AdjectiveSynset hypernymSynset : hypernymSynsets){
					
					setOfHypernymSynsets.add(hypernymSynset);
					
					String[] wordForms = hypernymSynset.getWordForms();
					
					//for (String wordForm : wordForms){
					for (int j = 0; j < wordForms.length; j++){

						String hypernym = wordForms[j] + "_JJ";
						
						if (hypernym.equals(posWord))
							continue;

						hypernymSet.add(hypernym);
					}
					
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
	
	public HashSet<String> getChildrenSet(String posWord, int wordnet_tag_count_threshold, HashSet<Synset> setOfSynonymSynsets, HashSet<String> glosses){
		
		HashSet<String> childrenSet = new HashSet<String>();
		
		for (Synset synset: setOfSynonymSynsets) {
			
			SynsetType senseType = synset.getType();
			
			if (senseType == SynsetType.NOUN){
				
				NounSynset[] hyponymSynsets = ((NounSynset)synset).getHyponyms();
				
				for (NounSynset hyponymSynset : hyponymSynsets){
					
					String[] wordForms = hyponymSynset.getWordForms();

					String gloss = "";
                    for (String form :wordForms){
                    	gloss += form + " ";
                    }
					
					String definition = hyponymSynset.getDefinition().trim();
					
					/*
					if (definition.startsWith("(")){
						definition = definition.substring(definition.indexOf(')') + 1, definition.length()).trim();
					}
					*/

					if (!definition.contains("("))
						glosses.add(gloss + "_NN: " + definition);
					
					for (String wordForm : wordForms){

						//int senseNumberOfHyponym = hyponymSynset.getSenseNumber(wordForm);

						if (hyponymSynset.getTagCount(wordForm) >= wordnet_tag_count_threshold || wordForm.equals(wordForms[0])) 
							childrenSet.add(wordForm);
					}
					
				}
			
			}else if (senseType == SynsetType.VERB){
				
				VerbSynset[] hyponymSynsets = ((VerbSynset)synset).getTroponyms();
				
				for (VerbSynset hyponymSynset : hyponymSynsets){
					
					String[] wordForms = hyponymSynset.getWordForms();
					
					String gloss = "";
                    for (String form :wordForms){
                    	gloss += form + " ";
                    }

					String definition = hyponymSynset.getDefinition().trim();
					
					/*
					if (definition.startsWith("(")){
						definition = definition.substring(definition.indexOf(')') + 1, definition.length()).trim();
					}
					*/

					if (!definition.contains("("))
						glosses.add(gloss + "_VB: " + definition);

					for (String wordForm : wordForms){

						//int senseNumberOfHyponym = hyponymSynset.getSenseNumber(wordForm);

						if (hyponymSynset.getTagCount(wordForm) >= wordnet_tag_count_threshold || wordForm.equals(wordForms[0])) 
							childrenSet.add(wordForm);
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
	
	
	public HashSet<String> getTopLevelWords(NounSynset root, int depth){

		if (depth == 0)
			return new HashSet<String>();
		else{
			HashSet<String> topLevelWords = new HashSet<String>();
			
			NounSynset[] children = root.getHyponyms();
			
			for (NounSynset child: children){

				String[] wordforms = child.getWordForms();
				for (String word : wordforms){
					topLevelWords.add(word);
				}
			}
			
			int level = depth - 1;
			
			for (NounSynset child : children){
				topLevelWords.addAll(getTopLevelWords(child, level));
			}

			return topLevelWords;

		}
		
	}
	
	public HashSet<String> getChildrenGlossTerms(String posWord, HashSet<String> children_glosses, MaxentTagger tagger, Morphology morpha, HashMap<String, Integer> wordCountMap, float sim_threshold, int fre_threshold){
		
		HashSet<String> similarGlossTerms = new HashSet<String>();
		
		for (String wordGloss : children_glosses){
			
			int seperator = wordGloss.indexOf(':');
			String childrenWord = wordGloss.substring(0, seperator);
			String gloss = wordGloss.substring(seperator + 1, wordGloss.length());
			
			StringTokenizer childrenWordSt = new StringTokenizer(childrenWord, " ");
			HashSet<String> childrenWordSet = new HashSet<String>();
			
			while (childrenWordSt.hasMoreElements()){
				childrenWordSet.add(childrenWordSt.nextToken());
			}
			
			
			if (posWord.endsWith("_NN"))
				gloss = "I see " + gloss;
			else if (posWord.endsWith("_VB"))
				gloss = "to " + gloss;
			
			String taggedGloss = tagger.tagString(gloss).replace('/', '_');
			
			if (posWord.endsWith("_NN")){
				int start = taggedGloss.indexOf(' ', taggedGloss.indexOf(' ') + 1);
				taggedGloss = taggedGloss.substring(start, taggedGloss.length());
				
			}else if (posWord.endsWith("_VB")){
				int start = taggedGloss.indexOf(' ');
				taggedGloss = taggedGloss.substring(start + 1, taggedGloss.length());
			}
			
			StanfordTermTokenizer stt = new StanfordTermTokenizer(taggedGloss, new String[0], morpha, sim_model.vocabulary, sim_model.frequency);
			String pos_tagged_lemma;
			
			while ((pos_tagged_lemma = stt.getNextValidWord()) != null){
				
				int index = pos_tagged_lemma.lastIndexOf('_');
				String lemma = pos_tagged_lemma.substring(0, index);

				if (childrenWordSet.contains(lemma))
					continue;

				int count = wordCountMap.containsKey(pos_tagged_lemma) ? wordCountMap.get(pos_tagged_lemma) : 0;
				wordCountMap.put(pos_tagged_lemma, count + 1);
				
			}
		}
		
		for (Map.Entry<String, Integer> item: wordCountMap.entrySet()){
			
			if (item.getValue() >= fre_threshold){
				
				if (!posWord.equals(item.getKey()) && getSimilarity(posWord, item.getKey()) >= sim_threshold){
					
					if (posWord.endsWith("_NN") && item.getKey().endsWith("_VB"))
						similarGlossTerms.add(item.getKey());
					else if (posWord.endsWith("_VB") && item.getKey().endsWith("_VB"))
						similarGlossTerms.add(item.getKey());

				}
			}
		}

		return similarGlossTerms;
	}
	
	
	public HashSet<String> getSelfParentGlossTerms(String posWord, HashSet<String> self_parent_glosses, MaxentTagger tagger, Morphology morpha, HashMap<String, Integer> wordCountMap, float sim_threshold){

		HashSet<String> similarGlossTerms = new HashSet<String>();

		for (String wordGloss : self_parent_glosses){
			
			String gloss = wordGloss.substring(wordGloss.indexOf(':') + 1, wordGloss.length());
			
			if (posWord.endsWith("_NN"))
				gloss = "I see " + gloss;
			else if (posWord.endsWith("_VB"))
				gloss = "to " + gloss;
			
			String taggedGloss = tagger.tagString(gloss).replace('/', '_');
			
			if (posWord.endsWith("_NN")){
				int start = taggedGloss.indexOf(' ', taggedGloss.indexOf(' ') + 1);
				taggedGloss = taggedGloss.substring(start, taggedGloss.length());
				
			}else if (posWord.endsWith("_VB")){
				int start = taggedGloss.indexOf(' ') + 1;
				taggedGloss = taggedGloss.substring(start, taggedGloss.length());
			}

			StanfordTermTokenizer stt = new StanfordTermTokenizer(taggedGloss, new String[0], morpha, sim_model.vocabulary, sim_model.frequency);
			String pos_tagged_lemma;
			float maxSim = 0;
			String maxTermLemma = null;
			
			while ((pos_tagged_lemma = stt.getNextValidWord()) != null){

				int count = wordCountMap.containsKey(pos_tagged_lemma) ? wordCountMap.get(pos_tagged_lemma) : 0;
				wordCountMap.put(pos_tagged_lemma, count + 1);

				float sim = getSimilarity(posWord, pos_tagged_lemma);
				
				if (!posWord.equals(pos_tagged_lemma) && sim >= sim_threshold){
				
					if (posWord.endsWith("_NN") && pos_tagged_lemma.endsWith("_VB"))
						similarGlossTerms.add(pos_tagged_lemma);
					else if (posWord.endsWith("_VB") && pos_tagged_lemma.endsWith("_VB"))
						similarGlossTerms.add(pos_tagged_lemma);
				}
				
				if (sim > maxSim){
					maxSim = sim;
					maxTermLemma = pos_tagged_lemma;
				}
					
			}
			
			//if (maxSim > 0 && maxSim < sim_threshold)
			//	similarGlossTerms.add(maxTermLemma);
		}

		return similarGlossTerms;
	}

	
	public void addWordNetForRelations(int wordnet_tag_count_threshold, float defining_word_sim_threshold) throws Exception {
		
		Morphology morpha = new Morphology();
    	//String modelLocation = "./edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
    	String modelLocation = "/home/lushan1/nlp/model/stanford/pos-tagger/english-left3words/english-left3words-distsim.tagger";

        System.out.println("Reading model from file=" + modelLocation);
        MaxentTagger tagger = new MaxentTagger(modelLocation);
        
        System.out.println("Loading parser ...");
        LexicalizedParser parser = LexicalizedParser.loadModel("/home/lushan1/nlp/model/stanford/lexparser/englishPCFG.ser.gz", "-maxLength", "500");

		
		NounSynset root = (NounSynset) database.getSynsets("entity", SynsetType.NOUN, false)[0];
		
		HashSet<NounSynset> topLevelSynsets = getTopLevelSynsets(root, 2);
		//HashSet<String> topLevelWords = getTopLevelWords(root, 2);
		
		
		for (DictEntry entry:dict){
			
			HashSet<String> self_parent_glosses = new HashSet<String>();

			HashSet<Synset> setOfDerivativeSynsets = new HashSet<Synset>();
			HashSet<String> derivativeSet = new HashSet<String>();
			HashSet<Synset> setOfSynonymSynsets = new HashSet<Synset>();
			int[] numberOfsenses = new int[1];
			HashSet<String> synonymSet = getSynonymSet(entry.term, wordnet_tag_count_threshold, self_parent_glosses, setOfSynonymSynsets, derivativeSet, setOfDerivativeSynsets, numberOfsenses);
			
			HashSet<String> synonymOfDerivativeSet = new HashSet<String>();
			//for (String derivative: derivativeSet){
			//	synonymOfDerivativeSet.addAll(getSynonymSet(derivative, wordnet_tag_count_threshold, wordnetGlosses));
			//}
			
			//HashSet<String> hypernymSet = new HashSet<String>();
			HashSet<Synset> setOfHypernymSynsets = new HashSet<Synset>();
			HashSet<String> parentSet = getHypernymSet(entry.term, wordnet_tag_count_threshold, setOfSynonymSynsets, setOfHypernymSynsets, self_parent_glosses, topLevelSynsets);
			
			//HashSet<String> hypernymOfHypernymSet = new HashSet<String>();
			//for (String hypernym: hypernymSet){
			//	hypernymOfHypernymSet.addAll(getHypernymSet(hypernym, wordnet_tag_count_threshold, wordnetGlosses));
			//}
			HashSet<Synset> setOfHypernymOfHypernymSynsets = new HashSet<Synset>();
			//HashSet<String> grandParentSet = getHypernymSet(entry.term, wordnet_tag_count_threshold, setOfHypernymSynsets, setOfHypernymOfHypernymSynsets, new HashSet<String>(), topLevelSynsets);
			HashSet<String> grandParentSet = getHypernymSet(entry.term, wordnet_tag_count_threshold, setOfHypernymSynsets, setOfHypernymOfHypernymSynsets, self_parent_glosses, topLevelSynsets);
			
			
			HashSet<Synset> setOfHypernymOfHypernymOfHypernymSynsets = new HashSet<Synset>();
			HashSet<String> grandGrandParentSet = new HashSet<String>();
			if (entry.term.endsWith("_NN"))
				grandGrandParentSet = getHypernymSet(entry.term, wordnet_tag_count_threshold, setOfHypernymOfHypernymSynsets, setOfHypernymOfHypernymOfHypernymSynsets, new HashSet<String>(), topLevelSynsets);
			
			//HashSet<String> derivativeOfHypernymSet = new HashSet<String>();
			//for (String hypernym: hypernymSet){
			//	derivativeOfHypernymSet.addAll(getDerivativeSet(hypernym, wordnet_tag_count_threshold));
			//}
			
			HashSet<String> synonymOfHypernymSet = new HashSet<String>();
			//for (String hypernym: hypernymSet){
			//	synonymOfHypernymSet.addAll(getSynonymSet(hypernym, wordnet_tag_count_threshold, wordnetGlosses));
			//}
			
			HashSet<String> children_glosses = new HashSet<String>();
			HashSet<String> childrenSet = getChildrenSet(entry.term, wordnet_tag_count_threshold, setOfSynonymSynsets, children_glosses);
			childrenSet = new HashSet<String>();

			HashMap<String, Integer> wordCountMap = new HashMap<String, Integer>();
			
			HashSet<String> definingWordsFromChildren = getChildrenGlossTerms(entry.term, children_glosses, tagger, morpha, wordCountMap, defining_word_sim_threshold, 2);
			
			HashSet<String> definingWords = getSelfParentGlossTerms(entry.term, self_parent_glosses, tagger, morpha, wordCountMap, defining_word_sim_threshold);
			
			definingWords.addAll(definingWordsFromChildren);
			
			HashSet<String> setOfDefiningWords = new HashSet<String>();

			for(String term: definingWords){
				if (!synonymSet.contains(term) && !parentSet.contains(term) && !grandParentSet.contains(term) && !derivativeSet.contains(term) && !term.equals(entry.term)){
						setOfDefiningWords.add(term);
				}
			}
			
			for (Map.Entry<String, Integer> item: wordCountMap.entrySet()){
				
				if (item.getValue() >= 3 && getSimilarity(entry.term, item.getKey()) >= 0.1 
						&& !synonymSet.contains(item.getKey()) && !parentSet.contains(item.getKey()) && !grandParentSet.contains(item.getKey()) && !derivativeSet.contains(item.getKey()) && !item.getKey().equals(entry.term)){
					
					if (entry.term.endsWith("_NN") && item.getKey().endsWith("_VB"))
						setOfDefiningWords.add(item.getKey());
					else if (entry.term.endsWith("_VB") && item.getKey().endsWith("_VB"))
						setOfDefiningWords.add(item.getKey());
					
				}
			}
			
			
			//HashSet<String> setOfHeaders = getNounHeaders(self_parent_glosses, parser, morpha);
			MyHeaderFinder headerFinder = new MyHeaderFinder(parser, sim_model.vocabulary, morpha);
			
			if (entry.term.endsWith("_NN")){
				
				String targetWord = entry.term.substring(0, entry.term.indexOf('_'));
				
				HashMap<String, Integer> headerCountMap = new HashMap<String, Integer>();
				
				for (String wordGloss: self_parent_glosses){
					
					int seperator = wordGloss.indexOf(':');
					String word_term = wordGloss.substring(0, seperator).trim();
					String gloss = wordGloss.substring(seperator + 1, wordGloss.length()).trim();
					
					seperator = gloss.indexOf(';');

					if (seperator > 0)
						gloss = gloss.substring(0, seperator);

					//if (word_term.startsWith(gloss) || word_term.endsWith("*") || gloss.contains("(") || gloss.contains(targetWord) || gloss.contains("persons"))
					if (gloss.contains("(") || gloss.contains(targetWord) || gloss.contains("persons"))
						continue;
					
					String pos_tagged_header = headerFinder.findNounPhraseHead(gloss);
					
					if (!pos_tagged_header.endsWith("_NN"))
						continue;
					
					if (pos_tagged_header.equals("someone_NN"))
						pos_tagged_header = "person_NN";
					
					String header = pos_tagged_header.substring(0, pos_tagged_header.indexOf("_")).trim();
					
					if (!ignoredNounHeaders.contains(header) && !synonymSet.contains(pos_tagged_header) && !parentSet.contains(pos_tagged_header) && !grandParentSet.contains(pos_tagged_header) 
							&& !derivativeSet.contains(pos_tagged_header) && !entry.term.equals(pos_tagged_header) && getSimilarity(entry.term, pos_tagged_header) > 0.1){
						setOfDefiningWords.add(pos_tagged_header);
					}
					
					if (!ignoredNounHeaders.contains(header) && !synonymSet.contains(pos_tagged_header) && !parentSet.contains(pos_tagged_header) && !grandParentSet.contains(pos_tagged_header) 
							&& !derivativeSet.contains(pos_tagged_header) && !entry.term.equals(pos_tagged_header) && getSimilarity(entry.term, pos_tagged_header) <= 0.1){
						
						int count = headerCountMap.containsKey(pos_tagged_header) ? headerCountMap.get(pos_tagged_header) : 0;
						headerCountMap.put(pos_tagged_header, count + 1);
					}

				}
				
				for (Map.Entry<String, Integer> item: headerCountMap.entrySet()){
					
					if (item.getValue() > 2){
						setOfDefiningWords.add(item.getKey());
						System.out.println(entry.term + ": " + item.getKey());

					}						
				}

				
				headerCountMap = new HashMap<String, Integer>();

				for (String wordGloss: children_glosses){
					
					int seperator = wordGloss.indexOf(':');
					//String word_term = wordGloss.substring(0, seperator);
					String gloss = wordGloss.substring(seperator + 1, wordGloss.length()).trim();
					
					seperator = gloss.indexOf(';');

					if (seperator > 0)
						gloss = gloss.substring(0, seperator);

					if (gloss.contains("(") || gloss.contains(targetWord) || gloss.contains("persons"))
						continue;
					
					String pos_tagged_header = headerFinder.findNounPhraseHead(gloss);
					
					if (!pos_tagged_header.endsWith("_NN"))
						continue;
					
					String header = pos_tagged_header.substring(0, pos_tagged_header.indexOf("_")).trim();
					
					if (!ignoredNounHeadersForChildren.contains(header) && !synonymSet.contains(pos_tagged_header) && !parentSet.contains(pos_tagged_header) && !grandParentSet.contains(pos_tagged_header) && !derivativeSet.contains(pos_tagged_header) && !entry.term.equals(pos_tagged_header)){
						int count = headerCountMap.containsKey(pos_tagged_header) ? headerCountMap.get(pos_tagged_header) : 0;
						headerCountMap.put(pos_tagged_header, count + 1);
					}
				}
				
				for (Map.Entry<String, Integer> item: headerCountMap.entrySet()){
					
					if (children_glosses.size() > 10){
						if (item.getValue() >= 2 && getSimilarity(entry.term, item.getKey()) > 0.1){
							setOfDefiningWords.add(item.getKey());
						}						
					}else{
						if (item.getValue() >= 1 && getSimilarity(entry.term, item.getKey()) > 0.1){
							setOfDefiningWords.add(item.getKey());
						}
					}
				
				}

			}

			if (setOfDefiningWords.contains("someone_NN") || setOfDefiningWords.contains("human being_NN"))
				setOfDefiningWords.add("person_NN");
			
			for (String parent : parentSet){
				if (parent.endsWith(" person_NN"))
					setOfDefiningWords.add("person_NN");
			}

			for (String grandParent : grandParentSet){
				if (grandParent.endsWith(" person_NN"))
					setOfDefiningWords.add("person_NN");
			}

			HashSet<String> setOfFrequentWords = new HashSet<String>();
			
			
			WordDefinition wordDef = new WordDefinition(numberOfsenses[0], derivativeSet, synonymSet, synonymOfDerivativeSet, parentSet, grandParentSet, grandGrandParentSet, synonymOfHypernymSet, childrenSet, setOfDefiningWords, setOfFrequentWords);
			
			entry.definition = wordDef.serialize();
			
			//System.out.println(entry.term + ": " + entry.definition);
		
		}
		
	}
	
	public float getSimilarity(String word1, String word2){
		
		float sim = sim_model.getSimilarity(word1, word2);
		float sim2 = sim_model2.getSimilarity(word1, word2);
		
		if (sim > sim2)
			return sim;
		else
			return sim2;
		
	}


	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//Dictionary2 dict = new Dictionary2("/home/lushan1/nlp/dictionary/test2");
		Dictionary2 dict = new Dictionary2("/home/lushan1/nlp/dictionary/vocabulary");
		
		System.out.println(dict.dict[0]);
		//System.out.println(dict.dict[1]);
		
		//dict.addWordNetForRelations(5, (float) 0.2);
		dict.addWordNetForRelations(5, (float) 0.25);
		//dict.saveAs("/home/lushan1/nlp/dictionary/test_out");
		dict.saveAs("/home/lushan1/nlp/dictionary/wordnet_webbase_all_w5");
		System.out.println("done!");
		
		
		//Dictionary.extractDictionaryFromWebster("/home/lushan1/dictionary/29765.dict");
	}

}
