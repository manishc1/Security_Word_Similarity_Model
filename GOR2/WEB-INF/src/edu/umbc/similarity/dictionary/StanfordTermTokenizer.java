package edu.umbc.similarity.dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

import edu.stanford.nlp.process.Morphology;
import edu.umbc.dbpedia.model.LSA_Model;
import edu.umbc.dbpedia.model.SimilarityArrayModel;
import edu.umbc.dbpedia.util.LexicalProcess;


public class StanfordTermTokenizer {

	String paragraph;
	String[] stopWords;
	StringTokenizer st;
	String[] vocabulary;
	int frequency[];
	Morphology morpha;
	boolean lastIsHave;
	String result;
	public int lengthInAllWords;

	String nextTaggedWord;
	String nextWord;
	String nextPosTag;

	public static final HashMap<String, String> morpha_corrections = new HashMap<String, String>();
    static {
    	morpha_corrections.put("papers_NN", "paper");
    }

    
	public StanfordTermTokenizer(String paragraph_par, String[] stopwords_par, Morphology morpha_par, String[] vocabulary_par, int[] frequency_par) {
		// TODO Auto-generated constructor stub
		paragraph = paragraph_par;
		stopWords = stopwords_par;
		vocabulary = vocabulary_par;
		frequency = frequency_par;
		
		if (stopWords.length > 1)
			Arrays.sort(stopWords);
		
		morpha = morpha_par;
		st = new StringTokenizer(paragraph, " ");
		lastIsHave = false;

		nextTaggedWord = null;
		nextWord = null;
		nextPosTag = null;
		lengthInAllWords = 0;

	}

	public int index(String term){
		return Arrays.binarySearch(vocabulary, term);
	}
	
	
	public String lemma(String word, String tag){
		
		if (LexicalProcess.isAcronym(word) || LexicalProcess.isAbbreviation(word))
			return word;
		
		String correction = morpha_corrections.get(word.toLowerCase() + "_" + tag);
		
		if (correction != null) return correction;
		
		return morpha.lemma(word, tag);
		
	}


	public int getFrequency(String term){
		int index = index(term);
		if (index >= 0)
			return frequency[index];
		else
			return 0;
	}

	public String getNextValidWord(){
		
		
		while (st.hasMoreTokens() || nextTaggedWord != null){

			lengthInAllWords ++;
			
			result = null;
			
			String taggedWord;
			
			if (nextTaggedWord != null){
				taggedWord = nextTaggedWord;
				nextTaggedWord = null;
				nextWord = null;
				nextPosTag = null;
			}else
				taggedWord = st.nextToken();
				
			int index = taggedWord.lastIndexOf('_');
			if (index <= 0){
				lastIsHave = false;
				continue;
			}
			String word = taggedWord.substring(0, index);
			String posTag = taggedWord.substring(index + 1, taggedWord.length());
			
			if (word.endsWith(">")){
				lastIsHave = false;
				continue;
			}
			
			String wordInlowercase = word.toLowerCase();
			
			if (Character.isUpperCase(word.charAt(0)) || Character.isLowerCase(word.charAt(0)) || word.equals("'s")){
				
				if (stopWords.length == 0 || Arrays.binarySearch(stopWords, wordInlowercase) < 0){
					
					// NN
					if (posTag.startsWith("NN")){
						
						if (st.hasMoreTokens()){
							nextTaggedWord = st.nextToken();
						
							int nextIndex = nextTaggedWord.lastIndexOf('_');

							if (nextIndex > 0){
								nextWord = nextTaggedWord.substring(0, nextIndex);
								nextPosTag = nextTaggedWord.substring(nextIndex + 1, nextTaggedWord.length());
							}
						}
						
						if (nextPosTag != null && nextPosTag.startsWith("NN")){
							
							String twoWordsTerm;
							int found;
							
							if (Character.isUpperCase(word.charAt(0)) && Character.isUpperCase(nextWord.charAt(0))){
								twoWordsTerm = word + " " + nextWord;
								found = index(twoWordsTerm + "_NN");
								
								/*
								if (found < 0){
									twoWordsTerm = wordInlowercase + " " + morpha.stem(nextWord, "NN").word();
									found = index(twoWordsTerm + "_NN");
								}
								*/
								
							}else{
								twoWordsTerm = wordInlowercase + " " + lemma(nextWord, "NN");
								found = index(twoWordsTerm + "_NN");
							}
							
							if (found >= 0){
								result = twoWordsTerm + "_NN";
								lastIsHave = false;
								nextTaggedWord = null;
								nextWord = null;
								nextPosTag = null;
								return result;
							}
						}
						
						if (posTag.startsWith("NNP"))
							result = word + "_NN";
						else{
							result = lemma(word, "NN") + "_NN";
						}	
					
					// VB
					}else if (posTag.startsWith("VB")){
						
						if (st.hasMoreTokens()){
							nextTaggedWord = st.nextToken();
						
							int nextIndex = nextTaggedWord.lastIndexOf('_');

							if (nextIndex > 0){
								nextWord = nextTaggedWord.substring(0, nextIndex);
								nextPosTag = nextTaggedWord.substring(nextIndex + 1, nextTaggedWord.length());
							}
						}
						
						if (nextPosTag != null){
							
							String twoWordsTerm;
							
							twoWordsTerm = wordInlowercase + " " + nextWord.toLowerCase();

							int found = index(twoWordsTerm + "_VB");
							
							if (found >= 0){
								result = twoWordsTerm + "_VB";
								lastIsHave = false;
								nextTaggedWord = null;
								nextWord = null;
								nextPosTag = null;
								return result;
							}
						}

						//result[0] = morpha.stem(word, "VB").word() + "_VB";

						if (posTag.equals("VBN")){
							
							if (lastIsHave){
								result = lemma(word, "VB") + "_VB";
								
							}else{
								String adj_form = wordInlowercase + "_JJ";
								String vb_form = lemma(word, "VB") + "_VB";
								
								if (getFrequency(adj_form) > getFrequency(vb_form))
									result = adj_form;
								else
									result = vb_form;
							}
							
						}else{
							result = lemma(word, "VB") + "_VB";
						}
						
					
					// JJ
					}else if (posTag.startsWith("JJ")){
						
						if (st.hasMoreTokens()){
							nextTaggedWord = st.nextToken();
						
							int nextIndex = nextTaggedWord.lastIndexOf('_');

							if (nextIndex > 0){
								nextWord = nextTaggedWord.substring(0, nextIndex);
								nextPosTag = nextTaggedWord.substring(nextIndex + 1, nextTaggedWord.length());
							}
						}
						
						if (nextPosTag != null && nextPosTag.startsWith("NN")){
							
							String twoWordsTerm;
							int found;
							
							if (Character.isUpperCase(word.charAt(0)) && Character.isUpperCase(nextWord.charAt(0))){
								twoWordsTerm = word + " " + nextWord;
								found = index(twoWordsTerm + "_NN");
								
								/*
								if (found < 0){
									twoWordsTerm = wordInlowercase + " " + morpha.stem(nextWord, "NN").word();
									found = index(twoWordsTerm + "_NN");
								}
								*/
								
							}else{
								twoWordsTerm = wordInlowercase + " " + lemma(nextWord, "NN");
								found = index(twoWordsTerm + "_NN");
							}

							
							if (found >= 0){
								result = twoWordsTerm + "_NN";
								lastIsHave = false;
								nextTaggedWord = null;
								nextWord = null;
								nextPosTag = null;
								return result;
							}
						}

						if (wordInlowercase.endsWith("ed")){
							
							String vb_form = lemma(wordInlowercase, "VB") + "_VB";
							String adj_form = wordInlowercase + "_JJ";
						
							if (getFrequency(adj_form) > getFrequency(vb_form))
								result = adj_form;
							else
								result = vb_form;
						
						}else
							//result = wordInlowercase + "_JJ";
							result = wordInlowercase + "_" + posTag;
					
					
					// RB
					}else if (posTag.startsWith("RB")){
						
						if (getFrequency(wordInlowercase + "_JJ") > 0)
							result = wordInlowercase + "_JJ";
						else
							result = wordInlowercase + "_RB";
					
					// Others
					}else 
						result = wordInlowercase + "_OT";
					
					
					if (wordInlowercase.equals("have") || wordInlowercase.equals("has") || wordInlowercase.equals("had") || wordInlowercase.equals("having"))
						lastIsHave = true;
					else
						lastIsHave = false;
	
					return result;
				
				}
			}
			
			
			if (wordInlowercase.equals("have") || wordInlowercase.equals("has") || wordInlowercase.equals("had") || wordInlowercase.equals("having"))
				lastIsHave = true;
			else
				lastIsHave = false;

		}
		
		return null;
		
	}
	/**
	 * @param args
	 * @throws IOException 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		Morphology morph = new Morphology();
		SimilarityArrayModel test1 = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/wiki5_outlier_gutn");

        InputStreamReader isReader = new InputStreamReader(System.in);
        BufferedReader bufReader = new BufferedReader(isReader);
        while (true) {
            System.out.print("\n\nINPUT> ");
            System.out.flush();
            String line = bufReader.readLine();
            
            if (line == null || line.length() < 1 
                || line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit"))
                break;
            
            StanfordTermTokenizer test = new StanfordTermTokenizer(line, new String[]{"have", "is", "an", "the"}, morph, test1.vocabulary, test1.frequency);
            
            String output;
            while ((output = test.getNextValidWord()) != null){ 
            	System.out.print(output);
            	
            	System.out.print(" ");
            }
        }
	}

}
