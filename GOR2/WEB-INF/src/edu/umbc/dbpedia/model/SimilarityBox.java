package edu.umbc.dbpedia.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;
import edu.smu.tspell.wordnet.impl.file.SenseIndexEntry;
import edu.smu.tspell.wordnet.impl.file.SenseIndexReader;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.umbc.dbpedia.util.ComplexPredicate;
import edu.umbc.dbpedia.util.LexicalProcess;
import edu.umbc.dbpedia.util.NounCompound;

public class SimilarityBox {

	LSA_Model lsa;
	public WordNetDatabase database;
	public HashMap<String, Float> cache;
	//public HashMap<String, Float> wordnetSim;
	//public HashMap<String, String> wordnetSubs;
	private static ThreadLocal<HashMap<String, Float>> localWordnetSim = new ThreadLocal<HashMap<String, Float>>();
	private static ThreadLocal<HashMap<String, String>> localWordnetSubs = new ThreadLocal<HashMap<String, String>>();
	public float SYNONYM_SIMILARITY_UPPER = (float) 1.0;
	public float SYNONYM_SIMILARITY_BOTTOM = (float) 0.5;
	public float HYPERNYM_SIMILARITY_UPPER = (float) 1.0;
	public float HYPERNYM_SIMILARITY_BOTTOM = (float) 0.2;
	public float DERIVATIVE_SIMILARITY_UPPER = (float) 1.0;
	public float DERIVATIVE_SIMILARITY_BOTTOM = (float) 0.3;
	public float DERIVATIVE_SIMILARITY = (float) 1.0;
	public int WORDNET_COUNT_THRESHOLD = 5;
	
	
	public SimilarityBox() throws IOException {
		// TODO Auto-generated constructor stub
        System.out.println("Loading LSA model ... ");
        //lsa = new LSA_Model("/home/lushan1/nlp/model/SVD/Gutenberg2010AllW5");
        //lsa = new LSA_Model("/home/lushan1/nlp/model/SVD/Guten2010_Wiki2006_AllW5");
        lsa = new LSA_Model("/home/lushan1/nlp/model/SVD/Wikipedia2006AllW5");
        
        System.setProperty("wordnet.database.dir", "/usr/share/wordnet-3.0/dict/");
        database = WordNetDatabase.getFileInstance();
        
		/* Read exceptions */
		cache = new HashMap<String, Float>();
		//wordnetSim = new HashMap<String, Float>();
		//wordnetSubs = new HashMap<String, String>();
		
		File cacheFile = new File("/home/lushan1/dbpedia/cache.sim");
		BufferedReader cacheReader = new BufferedReader(new FileReader(cacheFile));
		
		String rdline;
		while((rdline = cacheReader.readLine()) != null){
			if (rdline.startsWith("#")) continue;
			StringTokenizer st = new StringTokenizer(rdline, " ");
			String word1 = st.nextToken();
			String word2 = st.nextToken();
			
			String key;
			if (word1.compareToIgnoreCase(word2) < 0)
				key = word1 + "|" + word2;
			else
				key = word2 + "|" + word1;

			Float value = Float.valueOf(st.nextToken());
			cache.put(key, value);
		}


	}
	
	/*
	public int getSenseNumber(Synset synset, String wordForm){
		
		int offset = synset.getOffset();
		
		SenseIndexReader reader = SenseIndexReader.getInstance();
		SenseIndexEntry[] indexEntries = reader.getLemmaEntries(wordForm);
		
		for (SenseIndexEntry entry : indexEntries){
			if (entry.getSynsetOffset() == offset)
				return entry.getSenseNumber();
		}
		
		return 0;
	}
	*/
	
	public Float getWordNetSim(String key){
		HashMap<String, Float> wordnetSim = localWordnetSim.get();
		if (wordnetSim == null){
			wordnetSim = new HashMap<String, Float>();
			localWordnetSim.set(wordnetSim);
		}
		return wordnetSim.get(key);
	}
	
	public void setWordNetSim(String key, float sim){
		HashMap<String, Float> wordnetSim = localWordnetSim.get();
		if (wordnetSim == null){
			wordnetSim = new HashMap<String, Float>();
			localWordnetSim.set(wordnetSim);
		}
		wordnetSim.put(key, sim);
	}

	public void clearWordNetSim(){
		HashMap<String, Float> wordnetSim = localWordnetSim.get();
		if (wordnetSim == null){
			wordnetSim = new HashMap<String, Float>();
			localWordnetSim.set(wordnetSim);
		}
		wordnetSim.clear();
	}

	public String getWordNetSubs(String key){
		HashMap<String, String> wordnetSubs = localWordnetSubs.get();
		if (wordnetSubs == null){
			wordnetSubs = new HashMap<String, String>();
			localWordnetSubs.set(wordnetSubs);
		}
		return wordnetSubs.get(key);
	}
	
	public void setWordNetSubs(String key, String sub){
		HashMap<String, String> wordnetSubs = localWordnetSubs.get();
		if (wordnetSubs == null){
			wordnetSubs = new HashMap<String, String>();
			localWordnetSubs.set(wordnetSubs);
		}
		wordnetSubs.put(key, sub);
	}

	public void clearWordNetSubs(){
		HashMap<String, String> wordnetSubs = localWordnetSubs.get();
		if (wordnetSubs == null){
			wordnetSubs = new HashMap<String, String>();
			localWordnetSubs.set(wordnetSubs);
		}
		wordnetSubs.clear();
	}
	
	public void addNounCompound(NounCompound compound){
		
		try{
			for (String posWord: compound.componentWords){
				
				int index = posWord.indexOf("_");
				String word = posWord.substring(0, index);
				String pos = posWord.substring(index + 1, posWord.length());
				
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
					continue;
				
				Synset[] synsets = database.getSynsets(word, type, false);
				int primaryTagCount = 0;
				
				if (synsets.length > 0){
					primaryTagCount = synsets[0].getTagCount(word);
				}
				
				for (int i = 0; i < synsets.length; i++) {
					
					//add derivatives
					WordSense[] derivatives = synsets[i].getDerivationallyRelatedForms(word);
					
					for (WordSense derivative : derivatives){
						
						SynsetType senseType = derivative.getSynset().getType();
						String sensePOS = "";
						
						if (senseType == SynsetType.NOUN)
							sensePOS = "NN";
						else if (senseType == SynsetType.VERB)
							sensePOS = "VB";
						else if (senseType == SynsetType.ADJECTIVE || senseType == SynsetType.ADJECTIVE_SATELLITE)
							sensePOS = "JJ";
						else if (senseType == SynsetType.ADVERB)
							sensePOS = "RB";
						
						String derivedWord = derivative.getWordForm() + "_" + sensePOS;
						
						String key;
						if (derivedWord.compareToIgnoreCase(posWord) < 0)
							key = derivedWord + "|" + posWord;
						else
							key = posWord + "|" + derivedWord;
						
						float sim = DERIVATIVE_SIMILARITY_BOTTOM + lsa.getCosineSimilarity(posWord, derivedWord);
						
						if (sim > DERIVATIVE_SIMILARITY_UPPER) sim = DERIVATIVE_SIMILARITY_UPPER;

						setWordNetSim(key, sim);
					}

					//check condition for whether going on
					int currentTagCount = synsets[i].getTagCount(word);
					if ((currentTagCount < WORDNET_COUNT_THRESHOLD || currentTagCount < primaryTagCount / 2) && i != 0)
						continue;

					
					//add synonyms 
					String[] wordForms = synsets[i].getWordForms();
					
					for (String wordForm : wordForms){
						
						if (wordForm.equals(word))
							continue;
						
						int senseNumber = synsets[i].getSenseNumber(wordForm);
						
						if (synsets[i].getTagCount(wordForm) < WORDNET_COUNT_THRESHOLD && senseNumber != 1)
							continue;
						
						String synonym = wordForm + "_" + pos;
	
						if (synonym.equals(posWord))
							continue;
						
						String key;
						if (synonym.compareToIgnoreCase(posWord) < 0)
							key = synonym + "|" + posWord;
						else
							key = posWord + "|" + synonym;
						
						float sim = SYNONYM_SIMILARITY_BOTTOM + lsa.getCosineSimilarity(posWord, synonym);
						
						if (sim > SYNONYM_SIMILARITY_UPPER) sim = SYNONYM_SIMILARITY_UPPER;
						
						setWordNetSim(key, sim);
						//wordnetSim.put(key, SYNONYM_SIMILARITY_UPPER);
					}
					
					
					//add hypernyms
					if (pos.equals("NN")){
						
						NounSynset[] hypernymSets = ((NounSynset)synsets[i]).getHypernyms();
						
						for (NounSynset hypernymSet : hypernymSets){
							
							wordForms = hypernymSet.getWordForms();
							
							for (String wordForm : wordForms){

								int senseNumber = hypernymSet.getSenseNumber(wordForm);

								if (hypernymSet.getTagCount(wordForm) < WORDNET_COUNT_THRESHOLD && senseNumber != 1)
									continue;

								String hypernym = wordForm + "_" + pos;
								
								String key;
								if (hypernym.compareToIgnoreCase(posWord) < 0)
									key = hypernym + "|" + posWord;
								else
									key = posWord + "|" + hypernym;
								
								float sim = HYPERNYM_SIMILARITY_BOTTOM + lsa.getCosineSimilarity(posWord, hypernym);
								
								if (sim > HYPERNYM_SIMILARITY_UPPER) sim = HYPERNYM_SIMILARITY_UPPER;

								setWordNetSim(key, sim);
								//wordnetSim.put(key, HYPERNYM_SIMILARITY_UPPER);
							}
							
							NounSynset[] secondHypernymSets = hypernymSet.getHypernyms();
							
							for (NounSynset secondHypernymSet : secondHypernymSets){
								
								wordForms = secondHypernymSet.getWordForms();
								
								for (String wordForm : wordForms){

									int senseNumber = secondHypernymSet.getSenseNumber(wordForm);

									if (secondHypernymSet.getTagCount(wordForm) < WORDNET_COUNT_THRESHOLD && senseNumber != 1)
										continue;

									String hypernym = wordForm + "_" + pos;
									
									String key;
									if (hypernym.compareToIgnoreCase(posWord) < 0)
										key = hypernym + "|" + posWord;
									else
										key = posWord + "|" + hypernym;
									
									float sim = HYPERNYM_SIMILARITY_BOTTOM + lsa.getCosineSimilarity(posWord, hypernym);
									
									if (sim > HYPERNYM_SIMILARITY_UPPER) sim = HYPERNYM_SIMILARITY_UPPER;

									setWordNetSim(key, sim);
									//wordnetSim.put(key, HYPERNYM_SIMILARITY_UPPER);

								}
							}

						}
	
					}else if (pos.equals("VB")){
					
						VerbSynset[] hypernymSets = ((VerbSynset)synsets[i]).getHypernyms();
						
						for (VerbSynset hypernymSet : hypernymSets){
							
							wordForms = hypernymSet.getWordForms();
							
							for (String wordForm : wordForms){

								int senseNumber = hypernymSet.getSenseNumber(wordForm);
								
								if (hypernymSet.getTagCount(wordForm) < WORDNET_COUNT_THRESHOLD && senseNumber != 1)
									continue;

								String hypernym = wordForm + "_" + pos;
								
								String key;
								if (hypernym.compareToIgnoreCase(posWord) < 0)
									key = hypernym + "|" + posWord;
								else
									key = posWord + "|" + hypernym;
								
								float sim = HYPERNYM_SIMILARITY_BOTTOM + lsa.getCosineSimilarity(posWord, hypernym);
								
								if (sim > HYPERNYM_SIMILARITY_UPPER) sim = HYPERNYM_SIMILARITY_UPPER;
								
								setWordNetSim(key, sim);
								//wordnetSim.put(key, HYPERNYM_SIMILARITY_UPPER);
							}
						}
					}
	
				}
				
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}

		
	}
	
	public void addComplexPredicate(ComplexPredicate predicate){

		try{
			for (String posWord: predicate.componentWords){
				
				int index = posWord.indexOf("_");
				String word = posWord.substring(0, index);
				String pos = posWord.substring(index + 1, posWord.length());
				
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
					continue;
				
				Synset[] synsets = database.getSynsets(word, type, false);
				int primaryTagCount = 0;
				
				if (synsets.length > 0){
					primaryTagCount = synsets[0].getTagCount(word);
				}
				
				for (int i = 0; i < synsets.length; i++) {
					
					//add derivatives
					WordSense[] derivatives = synsets[i].getDerivationallyRelatedForms(word);
					
					for (WordSense derivative : derivatives){
						
						SynsetType senseType = derivative.getSynset().getType();
						String sensePOS = "";
						
						if (senseType == SynsetType.NOUN)
							sensePOS = "NN";
						else if (senseType == SynsetType.VERB)
							sensePOS = "VB";
						else if (senseType == SynsetType.ADJECTIVE || senseType == SynsetType.ADJECTIVE_SATELLITE)
							sensePOS = "JJ";
						else if (senseType == SynsetType.ADVERB)
							sensePOS = "RB";
						
						String derivedWord = derivative.getWordForm() + "_" + sensePOS;
						
						String key;
						if (derivedWord.compareToIgnoreCase(posWord) < 0)
							key = derivedWord + "|" + posWord;
						else
							key = posWord + "|" + derivedWord;
						
						float sim = DERIVATIVE_SIMILARITY_BOTTOM + lsa.getCosineSimilarity(posWord, derivedWord);
						
						if (sim > DERIVATIVE_SIMILARITY_UPPER) sim = DERIVATIVE_SIMILARITY_UPPER;

						setWordNetSim(key, sim);
					}

					//check condition for whether going on
					int currentTagCount = synsets[i].getTagCount(word);
					if ((currentTagCount < WORDNET_COUNT_THRESHOLD || currentTagCount < primaryTagCount / 2) && i != 0)
						continue;
						
					//add synonyms 
					String[] wordForms = synsets[i].getWordForms();
					boolean found = false;
					
					for (String wordForm : wordForms){
						
						if (wordForm.equals(word))
							continue;
						
						int senseNumber = synsets[i].getSenseNumber(wordForm);

						if (synsets[i].getTagCount(wordForm) < WORDNET_COUNT_THRESHOLD && senseNumber != 1)
							continue;

						String synonym = wordForm + "_" + pos;
						
						String key;
						if (synonym.compareToIgnoreCase(posWord) < 0)
							key = synonym + "|" + posWord;
						else
							key = posWord + "|" + synonym;
						
						float sim = SYNONYM_SIMILARITY_BOTTOM + lsa.getCosineSimilarity(posWord, synonym);
						
						if (sim > SYNONYM_SIMILARITY_UPPER) sim = SYNONYM_SIMILARITY_UPPER;

						setWordNetSim(key, sim);
						//wordnetSim.put(key, SYNONYM_SIMILARITY_UPPER);
						
						
						if (!found && i == 0 && posWord.contains(" ") && !synonym.contains(" ") && synsets[i].getTagCount(word) >= ComplexPredicate.phrase_verb_threshold){
							
							setWordNetSubs(posWord, synonym);
							found = true;
						}
					}
					
					
					//add hypernyms
					if (pos.equals("NN")){
						
						NounSynset[] hypernymSets = ((NounSynset)synsets[i]).getHypernyms();
						
						for (NounSynset hypernymSet : hypernymSets){
							
							wordForms = hypernymSet.getWordForms();
							
							for (String wordForm : wordForms){

								int senseNumber = hypernymSet.getSenseNumber(wordForm);

								if (hypernymSet.getTagCount(wordForm) < WORDNET_COUNT_THRESHOLD && senseNumber != 1)
									continue;

								String hypernym = wordForm + "_" + pos;
								
								String key;
								if (hypernym.compareToIgnoreCase(posWord) < 0)
									key = hypernym + "|" + posWord;
								else
									key = posWord + "|" + hypernym;
								
								float sim = HYPERNYM_SIMILARITY_BOTTOM + lsa.getCosineSimilarity(posWord, hypernym);
								
								if (sim > HYPERNYM_SIMILARITY_UPPER) sim = HYPERNYM_SIMILARITY_UPPER;

								setWordNetSim(key, sim);
								//wordnetSim.put(key, HYPERNYM_SIMILARITY_UPPER);

							}
							
							NounSynset[] secondHypernymSets = hypernymSet.getHypernyms();
							
							for (NounSynset secondHypernymSet : secondHypernymSets){
								
								wordForms = secondHypernymSet.getWordForms();
								
								for (String wordForm : wordForms){

									int senseNumber = secondHypernymSet.getSenseNumber(wordForm);

									if (secondHypernymSet.getTagCount(wordForm) < WORDNET_COUNT_THRESHOLD && senseNumber != 1)
										continue;

									String hypernym = wordForm + "_" + pos;
									
									String key;
									if (hypernym.compareToIgnoreCase(posWord) < 0)
										key = hypernym + "|" + posWord;
									else
										key = posWord + "|" + hypernym;
									
									float sim = HYPERNYM_SIMILARITY_BOTTOM + lsa.getCosineSimilarity(posWord, hypernym);
									
									if (sim > HYPERNYM_SIMILARITY_UPPER) sim = HYPERNYM_SIMILARITY_UPPER;

									setWordNetSim(key, sim);
									//wordnetSim.put(key, HYPERNYM_SIMILARITY_UPPER);

								}
							}
						}
	
					}else if (pos.equals("VB")){
					
						VerbSynset[] hypernymSets = ((VerbSynset)synsets[i]).getHypernyms();
						
						for (VerbSynset hypernymSet : hypernymSets){
							
							wordForms = hypernymSet.getWordForms();
							
							for (String wordForm : wordForms){
								
								int senseNumber = hypernymSet.getSenseNumber(wordForm);

								if (hypernymSet.getTagCount(wordForm) < WORDNET_COUNT_THRESHOLD && senseNumber != 1)
									continue;
								
								String hypernym = wordForm + "_" + pos;
								
								String key;
								if (hypernym.compareToIgnoreCase(posWord) < 0)
									key = hypernym + "|" + posWord;
								else
									key = posWord + "|" + hypernym;
								
								float sim = HYPERNYM_SIMILARITY_BOTTOM + lsa.getCosineSimilarity(posWord, hypernym);
								
								if (sim > HYPERNYM_SIMILARITY_UPPER) sim = HYPERNYM_SIMILARITY_UPPER;

								setWordNetSim(key, sim);
								//wordnetSim.put(key, HYPERNYM_SIMILARITY_UPPER);
							}
						}
					}
				}
				
			}
		
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean haveAntonym(ComplexPredicate predicate1, ComplexPredicate predicate2){
		
		HashSet<String> antonymSet = new HashSet<String>();
		
		for (String word1: predicate1.componentWords){
			
			int index1 = word1.indexOf("_");
			String word1form = word1.substring(0, index1);
			//String word1pos = word1.substring(index1 + 1, word1.length());
			
			/*
			SynsetType type;
			
			if (word1pos.equals("NN"))
				type = SynsetType.NOUN;
			else if (word1pos.equals("VB"))
				type = SynsetType.VERB;
			else if (word1pos.equals("JJ"))
				type = SynsetType.ADJECTIVE;
			else 
				type = SynsetType.ADVERB;
			*/

			Synset[] synsets = database.getSynsets(word1form, null, false);
			
			for (int i = 0; i < synsets.length; i++) {
				
				//if (synsets[i].getTagCount(word1form) < WORDNET_COUNT_THRESHOLD && i != 0)
				//	continue;
					
				//add synonyms 
				WordSense[] antonymArray = synsets[i].getAntonyms(word1form);

				for (WordSense antonym : antonymArray){
					antonymSet.add(antonym.getWordForm());
				}
			}	
			
		}	
			
		for (String word2: predicate2.componentWords){
			
			int index2 = word2.indexOf("_");
			String word2form = word2.substring(0, index2);
			
			if (antonymSet.contains(word2form))
				return true;
		}
		
		return false;
		
	}

	
	public double getSimilarity(String word1, String word2) {
		
		if (word1.equalsIgnoreCase(word2))
			return 1;
		
		String key;
		if (word1.compareToIgnoreCase(word2) < 0)
			key = word1 + "|" + word2;
		else
			key = word2 + "|" + word1;
		
		Float sim = cache.get(key.replaceAll(" ", "+"));
		if (sim != null)
			return sim;

		sim = getWordNetSim(key);
		if (sim != null)
			return sim;
		
		// reduce to simple words without space
		if (word1.contains(" ")){
			String replace = getWordNetSubs(word1);
			if (replace != null)
				word1 = replace;
			else
				word1 = word1.substring(0, word1.indexOf(" ")) + word1.substring(word1.length() - 3, word1.length());
		}
			
		if (word2.contains(" ")){
			String replace = getWordNetSubs(word2);
			if (replace != null)
				word2 = replace;
			else
				word2 = word2.substring(0, word2.indexOf(" ")) + word2.substring(word2.length() - 3, word2.length());;
		}
			
		if (word1.compareToIgnoreCase(word2) < 0)
			key = word1 + "|" + word2;
		else
			key = word2 + "|" + word1;

		// recheck
		sim = cache.get(key);
		if (sim != null)
			return sim;

		sim = getWordNetSim(key);
		if (sim != null)
			return sim;
			
		if ((word1.endsWith("_VB") || word1.endsWith("_NN")) && (word2.endsWith("_VB") || word2.endsWith("_NN"))){
			
			sim = lsa.getCosineSimilarity(word1, word2);
			
			String word1NN;
			String word1VB;
			String word2NN;
			String word2VB;
			
			if (word1.endsWith("_VB")){
				word1VB = word1;
				word1NN = LexicalProcess.getNounForm(word1);
			}else{
				word1VB = getVerbForm(word1);
				word1NN = word1;
			}
			
			if (word2.endsWith("_VB")){
				word2VB = word2;
				word2NN = LexicalProcess.getNounForm(word2);
			}else{
				word2VB = getVerbForm(word2);
				word2NN = word2;
			}

			//case 1
			if (word1VB != null && word2VB != null){
				
				if (word1VB.compareToIgnoreCase(word2VB) < 0)
					key = word1VB + "|" + word2VB;
				else
					key = word2VB + "|" + word1VB;
				
				Float tempSim = cache.get(key);
				if (tempSim != null)
					return tempSim;

				tempSim = getWordNetSim(key);
				if (tempSim != null)
					return tempSim;

				Float simVB_VB = lsa.getCosineSimilarity(word1VB, word2VB) * DERIVATIVE_SIMILARITY;
				if (simVB_VB > sim)
					sim = simVB_VB;
			}

			//case 2
			if (word1NN != null && word2NN != null){
				
				if (word1NN.compareToIgnoreCase(word2NN) < 0)
					key = word1NN + "|" + word2NN;
				else
					key = word2NN + "|" + word1NN;
				
				Float tempSim = cache.get(key);
				if (tempSim != null)
					return tempSim;

				tempSim = getWordNetSim(key);
				if (tempSim != null)
					return tempSim;
				
				Float simNN_NN = lsa.getCosineSimilarity(word1NN, word2NN) * DERIVATIVE_SIMILARITY;
				if (simNN_NN > sim)
					sim = simNN_NN;
			}
			
			//case 3
			if (word1NN != null && word2VB != null){
				
				if (word1NN.compareToIgnoreCase(word2VB) < 0)
					key = word1NN + "|" + word2VB;
				else
					key = word2VB + "|" + word1NN;
				
				Float tempSim = cache.get(key);
				if (tempSim != null)
					return tempSim;

				tempSim = getWordNetSim(key);
				if (tempSim != null)
					return tempSim;

				Float simNN_VB = lsa.getCosineSimilarity(word1NN, word2VB) * DERIVATIVE_SIMILARITY;
				if (simNN_VB > sim)
					sim = simNN_VB;
			}
			
			//case 4
			if (word1VB != null && word2NN != null){
				
				if (word1VB.compareToIgnoreCase(word2NN) < 0)
					key = word1VB + "|" + word2NN;
				else
					key = word2NN + "|" + word1VB;
				
				Float tempSim = cache.get(key);
				if (tempSim != null)
					return tempSim;

				tempSim = getWordNetSim(key);
				if (tempSim != null)
					return tempSim;
				
				Float simVB_NN = lsa.getCosineSimilarity(word1VB, word2NN) * DERIVATIVE_SIMILARITY;
				if (simVB_NN > sim)
					sim = simVB_NN;
			}


		}else{
			
			sim = lsa.getCosineSimilarity(word1, word2);
		}
		
		return sim;
		
	}

	
	public String getVerbForm(String noun){

		int index = noun.indexOf("_");
		String word = noun.substring(0, index);

		Synset[] synsets = database.getSynsets(word, SynsetType.NOUN, false);
		
		for (int i = 0; i < synsets.length; i++) {
			WordSense[] derivatives = synsets[i].getDerivationallyRelatedForms(word);
			
			for (WordSense derivative : derivatives){
				
				SynsetType senseType = derivative.getSynset().getType();
				
				if (senseType != SynsetType.VERB)
					continue;
				
				if (derivative.getWordForm().length() >= word.length())
					continue;
				
				String verbForm = derivative.getWordForm() + "_VB";
				
				return verbForm;
			}
		}
		
		return null;
	}
		
		
	public int getFrequency(String word){
		return lsa.getFrequency(word);
	}
	
	public void clear(){
		clearWordNetSim();
		clearWordNetSubs();
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
    	String modelLocation = "/home/lushan1/nlp/model/stanford/left3words-distsim-wsj-0-18.tagger";
    	//String modelLocation = "/home/lushan1/nlp/model/stanford/left3words-wsj-0-18.tagger";
    	//String modelLocation = "/home/lushan1/nlp/model/stanford/bidirectional-distsim-wsj-0-18.tagger";
        System.out.println("Reading model from file=" + modelLocation);
        MaxentTagger tagger = new MaxentTagger(modelLocation);
		Morphology morph = new Morphology();

		
		SimilarityBox simBox = new SimilarityBox();
		//NounCompound firstCompound = new NounCompound("author", tagger, morph, simBox);
		//simBox.addNounCompound(firstCompound);
		System.out.println(simBox.getSimilarity("write_VB", "author_NN"));
		
	}

}
