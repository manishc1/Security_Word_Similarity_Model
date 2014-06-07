package edu.umbc.dbpedia.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.TreeMap;

import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.umbc.nlp.util.StringSimilarity;

public class SimilarityArrayModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public float[] sim_array;  
	public int sizeOfVocabulary;
	public String[] vocabulary;
	public int frequency[];
	

	public SimilarityArrayModel(String filename) {
		// TODO Auto-generated constructor stub
		/* Read vocabulary */
		
		try{
			File vocabularyFile = new File(filename + ".voc");
			BufferedReader vocabularyReader = new BufferedReader(new FileReader(vocabularyFile), 1000000);
			
			String rdline;
			rdline = vocabularyReader.readLine();
			sizeOfVocabulary = Integer.valueOf(rdline);
			
			int m = sizeOfVocabulary * (sizeOfVocabulary + 1) / 2;
			sim_array = new float[m];
			vocabulary = new String[sizeOfVocabulary];
			frequency = new int[sizeOfVocabulary];
			
			for (int i = 0; i < sizeOfVocabulary; i++){
				vocabulary[i] = vocabularyReader.readLine();
			}
	
			vocabularyReader.close();
			
			/* Read Frequency array */
			File frequencyFile = new File(filename + ".frq");
			BufferedReader frequencyReader = new BufferedReader(new FileReader(frequencyFile), 1000000);
			
			for (int i = 0; i < sizeOfVocabulary; i++){
				frequency[i] = Integer.valueOf(frequencyReader.readLine());
			}
			
			frequencyReader.close();

			
		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
			System.exit(-1);
		}

		
	}
	
	public int getFrequency(int index){

		if (index >= 0)
			return frequency[index];
		else
			return 0;
	}
	
	
	public int getFrequency(String word){
		int index = index(word);
		if (index >= 0)
			return frequency[index];
		else
			return 0;
	}
	
	public float getSimilarity(int i, int j){
		
		if (i < j){
			int temp = j;
			j = i;
			i = temp;
		}

		int n = sizeOfVocabulary;
		
		int k = (2 * n - j + 1) * j / 2 + i - j;
		
		return sim_array[k];
		
		
	}

	public void setSimilarity(int i, int j, float sim){
		
		if (i < j){
			int temp = j;
			j = i;
			i = temp;
		}

		int n = sizeOfVocabulary;
		
		int k = (2 * n - j + 1) * j / 2 + i - j;
		
		sim_array[k] = sim;
		
		
	}

	public void loadInitialSimilarity_LSA(LSA_Model lsa){
		
		int count = 0;
		
		for (int i = 0; i < sizeOfVocabulary; i++){

			for (int j = 0; j <= i; j++){
			
				float lsa_sim = lsa.getCosineSimilarity(i, j);
				
				if (lsa_sim > 0){

					setSimilarity(i, j, lsa_sim);
					count ++;
					
				}else
					setSimilarity(i, j, 0);
			}
			
			if (i % 1000 == 0)
				System.out.println(i + " rows have been processed.");
		}
		
		System.out.println("There are totally " + count + " non-zero similarity values");

	}

	public void loadInitialSimilarity_outlier(LSA_Model gutenberg_lsa, LSA_Model wiki_lsa){
		
		int count = 0;
		
		for (int i = 0; i < sizeOfVocabulary; i++){

			for (int j = 0; j <= i; j++){
			
				float gutn_sim = gutenberg_lsa.getCosineSimilarity(i, j);
				float wiki_sim = wiki_lsa.getCosineSimilarity(i, j);
				
				/*
				//if (wiki_sim > 0.4 && gutn_sim < 0.1 && wiki_lsa.frequency[i] > 1000 && wiki_lsa.frequency[j] > 1000 && gutenberg_lsa.frequency[i] > 3000 && gutenberg_lsa.frequency[j] > 3000){
				if (gutn_sim > 0.4 && wiki_sim < 0.1 && wiki_lsa.frequency[i] > 2000 && wiki_lsa.frequency[j] > 2000 && gutenberg_lsa.frequency[i] > 4000 && gutenberg_lsa.frequency[j] > 4000){
					String word1 = vocabulary[i];
					String word2 = vocabulary[j];
					if ((word1.endsWith("NN") && word2.endsWith("NN")) || (word1.endsWith("VB") && word2.endsWith("VB")))
					//if ((word1.endsWith("VB") && word2.endsWith("VB")))
						System.out.println(word1 + " " + word2 + " :" + wiki_sim + ", " + gutn_sim);
				}
				*/
				float variation = (float) 0.20;
				
				if (wiki_sim > 0){

					if (gutenberg_lsa.frequency[i] > 4000 && gutenberg_lsa.frequency[j] > 4000){
						if (wiki_sim - gutn_sim > variation || gutn_sim - wiki_sim > variation){
							if ((wiki_sim + gutn_sim) / 2 > 0){
								setSimilarity(i, j, (wiki_sim + gutn_sim) / 2);
								count ++;
							}else
								setSimilarity(i, j, 0);
						}else{
							setSimilarity(i, j, wiki_sim);
							count ++;
						}
					
					}else{
						setSimilarity(i, j, wiki_sim);
						count ++;
					}
					
				}else if (gutn_sim > 0){
					
					if (gutenberg_lsa.frequency[i] > 4000 && gutenberg_lsa.frequency[j] > 4000){
						if (gutn_sim - wiki_sim < variation){
							setSimilarity(i, j, gutn_sim);
							count ++;
						}else if ((wiki_sim + gutn_sim) / 2 > 0) {
							setSimilarity(i, j, (wiki_sim + gutn_sim) / 2);
							count ++;
						}else
							setSimilarity(i, j, 0);
							
					}else
						setSimilarity(i, j, 0);
					
				}else
					setSimilarity(i, j, 0);
			}
			
			if (i % 1000 == 0)
				System.out.println(i + " rows have been processed.");
		}
		
		System.out.println("There are totally " + count + " non-zero similarity values");

	}

	
	public void loadInitialSimilarity_max(SimilarityArrayModel model1, SimilarityArrayModel model2){
		
		for (int i = 0; i < sizeOfVocabulary; i++){

			for (int j = 0; j <= i; j++){
			
				float sim1= model1.getSimilarity(i, j);
				float sim2 = model2.getSimilarity(i, j);
				
				if (sim1 > sim2)
					setSimilarity(i, j, sim1);
				else
					setSimilarity(i, j, sim2);
				
			}
			
			if (i % 1000 == 0)
				System.out.println(i + " rows have been processed.");
		}

	}

	public String getAdjBaseFromSuperlative(String word){
		
		String base;
		
		if (word.endsWith("est")){
			
			base = word.substring(0, word.length() - 3);
			
			if (base.length() > 1){
				
				if (base.charAt(base.length() - 1) == base.charAt(base.length() - 2))
					base = base.substring(0, base.length() - 1);
			
				String guess = base + "_JJ";
				
				if (index(guess) > 0)
					return guess;
				
				guess = base + "e_JJ";
				
				if (index(guess) > 0)
					return guess;
				
			}else{

				if (word.equals("best"))
					return "good_JJ";
				else if (word.equals("lest"))
					return "little_JJ";
			}
		}
		
		return word + "_JJ";
	}


	public int index(String word){
		return Arrays.binarySearch(vocabulary, word);
	}

	
	public float getSimilarity(String word1, String word2){
		
		String lemma1 = word1.substring(0, word1.lastIndexOf("_"));
		String lemma2 = word2.substring(0, word2.lastIndexOf("_"));

		if (lemma1.equalsIgnoreCase(lemma2))
			return 1;
		
		if (word1.endsWith("_JJS") && word2.endsWith("_JJS")){
			word1 = getAdjBaseFromSuperlative(lemma1);
			word2 = getAdjBaseFromSuperlative(lemma2);
			return getSimilarity(word1, word2);
		}

		int index1 = index(word1);
		int index2 = index(word2);

		if (index1 < 0 || index2 < 0){

			float sim = StringSimilarity.getSim(lemma1, lemma2);
			
			if (sim > 0.6)
				return sim;
			
			else{
				int indexOfHypen;
				boolean hasHypen = false;
				
				indexOfHypen = word1.indexOf('-');
				if (indexOfHypen > 0){
					hasHypen = true;
					word1 = word1.substring(indexOfHypen + 1, word1.length());
				}
				
				indexOfHypen = word2.indexOf('-');
				if (indexOfHypen > 0){
					hasHypen = true;
					word2 = word2.substring(indexOfHypen + 1, word2.length());
				}
				
				if (hasHypen)
					return getSimilarity(word1, word2);
				else
					return 0;
			}
		}	
			
		float sim = getSimilarity(index1, index2);
		
		if (word1.endsWith("_NN") && word1.contains(" ")){
			
			int indexOfSpace = word1.indexOf(' ');
			
			float sim1 = (getSimilarity(word1.substring(indexOfSpace + 1, word1.length()), word2) + getSimilarity(word1.substring(0, indexOfSpace) + "_NN", word2)) / 2;
			
			if (sim1 > sim)
				sim = sim1;
		}
		
		if (word2.endsWith("_NN") && word2.contains(" ")){
			
			int indexOfSpace = word2.indexOf(' ');
			
			float sim2 = (getSimilarity(word1, word2.substring(indexOfSpace + 1, word2.length())) + getSimilarity(word1, word2.substring(0, indexOfSpace) + "_NN")) / 2;
			
			if (sim2 > sim)
				sim = sim2;
		}
		
		return sim;
		
	}
	
	
	public float getStringSimilarity(String word1, String word2){
		
		String lemma1 = word1.substring(0, word1.lastIndexOf("_"));
		String lemma2 = word2.substring(0, word2.lastIndexOf("_"));

		if (lemma1.equalsIgnoreCase(lemma2))
			return 1;
		
		if (word1.endsWith("_JJS") && word2.endsWith("_JJS")){
			word1 = getAdjBaseFromSuperlative(lemma1);
			word2 = getAdjBaseFromSuperlative(lemma2);
			return getSimilarity(word1, word2);
		}

		int index1 = -1;
		int index2 = -1;

		if (index1 < 0 || index2 < 0){

			float sim = StringSimilarity.getSim(lemma1, lemma2);
			
			return sim;
			
			/*
			if (sim > 0.6)
				return sim;
			
			else{
				int indexOfHypen;
				boolean hasHypen = false;
				
				indexOfHypen = word1.indexOf('-');
				if (indexOfHypen > 0){
					hasHypen = true;
					word1 = word1.substring(indexOfHypen + 1, word1.length());
				}
				
				indexOfHypen = word2.indexOf('-');
				if (indexOfHypen > 0){
					hasHypen = true;
					word2 = word2.substring(indexOfHypen + 1, word2.length());
				}
				
				if (hasHypen)
					return getSimilarity(word1, word2);
				else
					return 0;
			}
			*/
		}	
			
		float sim = getSimilarity(index1, index2);
		
		if (word1.endsWith("_NN") && word1.contains(" ")){
			
			int indexOfSpace = word1.indexOf(' ');
			
			float sim1 = (getSimilarity(word1.substring(indexOfSpace + 1, word1.length()), word2) + getSimilarity(word1.substring(0, indexOfSpace) + "_NN", word2)) / 2;
			
			if (sim1 > sim)
				sim = sim1;
		}
		
		if (word2.endsWith("_NN") && word2.contains(" ")){
			
			int indexOfSpace = word2.indexOf(' ');
			
			float sim2 = (getSimilarity(word1, word2.substring(indexOfSpace + 1, word2.length())) + getSimilarity(word1, word2.substring(0, indexOfSpace) + "_NN")) / 2;
			
			if (sim2 > sim)
				sim = sim2;
		}
		
		return sim;
		
	}

	

	public boolean setSimilarity(String word1, String word2, float sim){
		
		int index1 = index(word1);
		int index2 = index(word2);
		
		if (index1 < 0 || index2 < 0)
			return false;

		setSimilarity(index1, index2, sim);
		
		return true;
	}
	
	
	public FloatElement[] getSortedWordsBySimilarity(String word){
		
		int index1 = index(word);
		ArrayList<FloatElement> correlateWords = new ArrayList<FloatElement>();
		
		for (int i=0; i<sizeOfVocabulary; i++){
			
			if (i == index1)
				continue;
				
			float sim = 0;
			int frequency = 0;
			
			try {
				frequency =  getFrequency(i);
				
				if (frequency < 1000) 
					continue;

				sim = getSimilarity(index1, i);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				continue;
			}
			
			FloatElement element = new FloatElement(vocabulary[i], sim, 0, frequency);
			correlateWords.add(element);
		}
		
		FloatElement[] result = correlateWords.toArray(new FloatElement[correlateWords.size()]);
		Arrays.sort(result);
		
		return result;
		
	}

	
	public FloatElement[] getSortedWordsBySimilarityWithSamePOS(String word){
		
		int index1 = index(word);
		ArrayList<FloatElement> correlateWords = new ArrayList<FloatElement>();
		
		for (int i=0; i<sizeOfVocabulary; i++){
			
			String pos = word.substring(word.length() - 2, word.length());
			
			if (!vocabulary[i].endsWith(pos))
				continue;
			
			if (i == index1)
				continue;
				
			float sim = 0;
			int frequency = 0;
			
			try {
				frequency =  getFrequency(i);
				
				if (frequency < 1000) 
					continue;

				sim = getSimilarity(index1, i);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				continue;
			}
			
			FloatElement element = new FloatElement(vocabulary[i], sim, 0, frequency);
			correlateWords.add(element);
		}
		
		FloatElement[] result = correlateWords.toArray(new FloatElement[correlateWords.size()]);
		Arrays.sort(result);
		
		return result;
		
	}

	
	public String printPCW(Object[] sortedWords, int size){
		
		StringBuffer temp = new StringBuffer();
		
		for (int i=0; i<size; i++){
			temp.append(sortedWords[i] + ", ");
		}
		
		System.out.println(temp);
		
		return temp.toString();

	}


	static public void writeModel(String modelname, SimilarityArrayModel model) throws IOException{

		FileOutputStream fileOut;
		ObjectOutputStream objOut;
		fileOut = new FileOutputStream(modelname + ".BigArray_model");
		objOut = new ObjectOutputStream(fileOut);
		objOut.writeObject(model);
		objOut.close();
		System.out.println("seriliazation done!");

	}

	
	static public SimilarityArrayModel readModel(String modelname) throws IOException, ClassNotFoundException{
        
		FileInputStream fileIn = new FileInputStream(modelname + ".BigArray_model");
        ObjectInputStream objIn = new ObjectInputStream(fileIn);
        return (SimilarityArrayModel) objIn.readObject();
	}

	
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws IOException, Exception {
		// TODO Auto-generated method stub

		/*
		LSA_Model webbase = new LSA_Model("/home/manish/lushan/nlp/SVD/CVEAllW");		
		
		SimilarityArrayModel sa_model = new SimilarityArrayModel("/home/manish/lushan/nlp/SVD/CVEAllW");
		
		sa_model.loadInitialSimilarity_LSA(webbase);
		System.out.println("Finished loading!");
		
		SimilarityArrayModel.writeModel("/home/manish/lushan/nlp/BigArray/CVEAllW", sa_model);
		System.out.println("Finished serialization!");
		*/
		
		/*
		LSA_Model gutn = new LSA_Model("/home/lushan1/nlp/model/SVD/Gutenberg2010AllW5");
		LSA_Model wiki = new LSA_Model("/home/lushan1/nlp/model/SVD/Wikipedia2006AllW5");
		
		SimilarityArrayModel model = new SimilarityArrayModel("/home/lushan1/nlp/model/BigArray/wiki5");
		
		model.loadInitialSimilarity_outlier(gutn, wiki);
		System.out.println("Finished loading!");
		
		SimilarityArrayModel.writeModel("/home/lushan1/nlp/model/BigArray/wiki5_outlier_gutn", model);
		System.out.println("Finished serialization!");
		*/
		
		/*
		LSA_Model webbase = new LSA_Model("/home/lushan1/nlp/model/SVD/webbase2012AllW5");
		
		SimilarityArrayModel model = new SimilarityArrayModel("/home/lushan1/nlp/model/BigArray/webbase2012AllPlusV2W5");
		
		model.loadInitialSimilarity_LSA(webbase);
		System.out.println("Finished loading!");
		
		SimilarityArrayModel.writeModel("/home/lushan1/nlp/model/BigArray/webbase2012AllPlusV2W5", model);
		System.out.println("Finished serialization!");
		*/
		
		/*
		LSA_Model gigawords = new LSA_Model("/home/lushan1/nlp/model/SVD/Gigawords2009AllW5");
		
		SimilarityArrayModel model = new SimilarityArrayModel("/home/lushan1/nlp/model/BigArray/Gigawords2009AllW5");
		
		model.loadInitialSimilarity_LSA(gigawords);
		System.out.println("Finished loading!");
		
		SimilarityArrayModel.writeModel("/home/lushan1/nlp/model/BigArray/Gigawords2009AllW5", model);
		System.out.println("Finished serialization!");
		*/
		
		/*
		SimilarityArrayModel model1 = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/webbase2012AllW2");
		SimilarityArrayModel model2 = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/webbase2012AllW5");
		
		SimilarityArrayModel model = new SimilarityArrayModel("/home/lushan1/nlp/model/BigArray/webbase2012AllW2W5Max");
		
		model.loadInitialSimilarity_max(model1, model2);
		System.out.println("Finished loading!");
		
		SimilarityArrayModel.writeModel("/home/lushan1/nlp/model/BigArray/webbase2012AllW2W5Max", model);
		System.out.println("Finished serialization!");
		*/
		
		
    	/*String modelLocation = "./edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";*/
		String modelLocation = "/home/manish/lushan/nlp/model/stanford/pos-tagger/english-left3words/english-left3words-distsim.tagger";
        System.out.println("Reading model from file=" + modelLocation);
        MaxentTagger tagger = new MaxentTagger(modelLocation);
        Morphology morpha = new Morphology();
		
		//LSA_Model gutn = new LSA_Model("/home/lushan1/nlp/model/SVD/Gutenberg2010AllW5", 6500);
		//LSA_Model wiki = new LSA_Model("/home/lushan1/nlp/model/SVD/Wikipedia2006AllW5", 1000);
		
		//SimilarityArrayModel model0 = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/wiki5_outlier_gutn");
		//SimilarityArrayModel model1 = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/wiki5_outlier_gutn_step1");
		//SimilarityArrayModel model2 = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/wiki5_outlier_gutn_step2");
		//SimilarityArrayModel model3 = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/wiki5_outlier_gutn_step3");

		/*
		SimilarityArrayModel model0 = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/webbase2012AllW2");		
		SimilarityArrayModel model1 = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/webbase2012AllW5");
		*/
        SimilarityArrayModel model0 = SimilarityArrayModel.readModel("/home/manish/lushan/nlp/BigArray/CVEAllW");		
		SimilarityArrayModel model1 = SimilarityArrayModel.readModel("/home/manish/lushan/nlp/BigArray/CVEAllW");
		
		
		//SimilarityArrayModel model1 = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/Gigawords2009AllW2");
		//SimilarityArrayModel model2 = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/webbase2012AllW2_S2");

		//SimilarityArrayModel model0 = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/Gigawords2009AllW5");
		//SimilarityArrayModel model1 = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/Gigawords2009AllW5_S2");
		//SimilarityArrayModel model2 = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/Gigawords2009AllW5_S3");

		
		SimilarityArrayModel model = model0;
		
		System.out.println("Input the word.");
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		String word = input.readLine();
		int adj_vb_discriminator_threshold = 500;
		
		while (!word.equals("quit")){
			
			System.out.println("Input the gloss.");
			String gloss = input.readLine();
			
			if (word.endsWith("_NN"))
				gloss = "I see " + gloss;
			else if (word.endsWith("_VB"))
				gloss = "to " + gloss;
			
			String taggedGloss = tagger.tagString(gloss).replace('/', '_');
			
			StringTokenizer st = new StringTokenizer(taggedGloss, " ");

			int tokenNo = 0;
			while (st.hasMoreElements()){
				
				String token = st.nextToken();
				tokenNo ++;

				if (word.endsWith("_NN")){
					if (tokenNo <= 2) continue;
				}else if (word.endsWith("_VB")){
					if (tokenNo <= 1) continue;
				}
				
				int index = token.lastIndexOf('_');
				String token_word = token.substring(0, index);
				String token_posTag = token.substring(index + 1, token.length());
				String token_lemma = "";
				
				if (token_posTag.startsWith("NN")){
					
					if (token_posTag.startsWith("NNP"))
						token_lemma = token_word + "_NN";
					else{
						//lemmatizedWord = morpha.stem(word, "NN").word() + "_NN";
						token_lemma = morpha.lemma(token_word, "NN") + "_NN";
					}	
					
				}else if (token_posTag.startsWith("VB")){
					
					if (token_posTag.equals("VBN")){
						
						//lemmatizedWord = word + "_JJ";
						//lemmatizedWord = morpha.stem(word, "VB").word() + "_VB";
						token_lemma = morpha.lemma(token_word, "VB") + "_VB";
						
					}else{
						//lemmatizedWord = morpha.stem(word, "VB").word() + "_VB";
						token_lemma = morpha.lemma(token_word, "VB") + "_VB";
					}
					
				}else if (token_posTag.startsWith("JJ")){
					
					if (token_word.endsWith("ed")){
						
						//lemmatizedWord = morpha.stem(word, "VB").word() + "_VB";
						token_lemma = morpha.lemma(token_word, "VB") + "_VB";
						
						if (model.getFrequency(token_lemma) < adj_vb_discriminator_threshold && model.getFrequency(token_word + "_JJ") > adj_vb_discriminator_threshold)
							token_lemma = token_word + "_JJ";
						
					}else
						token_lemma = token_word + "_JJ";
						
				
				}else if (token_posTag.startsWith("RB")){
					
					if (token_word.endsWith("ly"))
						token_lemma = token_word + "_RB";
					else
						token_lemma = token_word + "_JJ";
					
				
				}else if (token_posTag.equals("FW") || token_posTag.equals("WP")){
					
					token_lemma = token_word + "_NN";
				}

				
				float sim = model.getSimilarity(word, token_lemma);
				
				//if (sim > 0.2)
				System.out.print(token_lemma + ":" + sim + " ");
			}

			System.out.println();
			
			System.out.println("Input the word.");
			word = input.readLine();
			
		}
		
		
		System.out.println("done!");
		
	}

}
