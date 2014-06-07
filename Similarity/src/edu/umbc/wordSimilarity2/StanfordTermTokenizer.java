package edu.umbc.wordSimilarity2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

import edu.stanford.nlp.process.Morphology;

public class StanfordTermTokenizer {

	String paragraph;
	String[] stopWords;
	StringTokenizer st;
	String[] vocabulary;
	Morphology morpha;
	boolean lastIsHave;
	String[] result;
	public static int totalWords = 0;

	String nextTaggedWord;
	String nextWord;
	String nextPosTag;

	
	public StanfordTermTokenizer(String paragraph_par, String[] stopwords_par, Morphology morpha_par, String[] vocabulary_par) {
		// TODO Auto-generated constructor stub
		paragraph = paragraph_par;
		stopWords = stopwords_par;
		vocabulary = vocabulary_par;
		Arrays.sort(stopWords);
		morpha = morpha_par;
		st = new StringTokenizer(paragraph, " ");
		lastIsHave = false;
		result = new String[2];

		nextTaggedWord = null;
		nextWord = null;
		nextPosTag = null;

	}

	public int index(String term){
		return Arrays.binarySearch(vocabulary, term);
	}

	
	public String[] getNextValidWord(){
		
		
		while (st.hasMoreTokens() || nextTaggedWord != null){

			result[0] = null;
			result[1] = null;
			
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
				
				totalWords ++;
				
				if (Arrays.binarySearch(stopWords, wordInlowercase) < 0){
					
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
								if (Character.isLowerCase(nextWord.charAt(0)))
									twoWordsTerm = wordInlowercase + " " + morpha.stem(nextWord, "NN").word();
								else
									twoWordsTerm = wordInlowercase + " " + nextWord;
									
								found = index(twoWordsTerm + "_NN");
							}
							
							if (found >= 0){
								result[0] = twoWordsTerm + "_NN";
								lastIsHave = false;
								nextTaggedWord = null;
								nextWord = null;
								nextPosTag = null;
								return result;
							}
						}
						
						if (posTag.startsWith("NNP"))
							result[0] = word + "_NN";
						else{
							result[0] = morpha.stem(word, "NN").word() + "_NN";
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
								result[0] = twoWordsTerm + "_VB";
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
								result[0] = morpha.stem(word, "VB").word() + "_VB";
								
							}else{
								result[0] = wordInlowercase + "_JJ";
								result[1] = morpha.stem(word, "VB").word() + "_VB";
							}
							
						}else{
							result[0] = morpha.stem(word, "VB").word() + "_VB";
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
								if (Character.isLowerCase(nextWord.charAt(0)))
									twoWordsTerm = wordInlowercase + " " + morpha.stem(nextWord, "NN").word();
								else
									twoWordsTerm = wordInlowercase + " " + nextWord;

								found = index(twoWordsTerm + "_NN");
							}

							
							if (found >= 0){
								result[0] = twoWordsTerm + "_NN";
								lastIsHave = false;
								nextTaggedWord = null;
								nextWord = null;
								nextPosTag = null;
								return result;
							}
						}

						result[0] = wordInlowercase + "_JJ";
						
						if (wordInlowercase.endsWith("ed"))
							result[1] = morpha.stem(wordInlowercase, "VB").word() + "_VB";
						else if (wordInlowercase.equals("alone") || wordInlowercase.equals("alike") || wordInlowercase.equals("asleep") || wordInlowercase.equals("awake"))
							result[1] = wordInlowercase + "_RB";
					
					
					// RB
					}else if (posTag.startsWith("RB")){
						
						result[0] = wordInlowercase + "_RB";
						
						if (wordInlowercase.equals("alone") || wordInlowercase.equals("alike") || wordInlowercase.equals("asleep") || wordInlowercase.equals("awake"))
							result[1] = wordInlowercase + "_JJ";
					
					// Others
					}else 
						result[0] = wordInlowercase + "_OT";
					
					
					if (wordInlowercase.equals("have") || wordInlowercase.equals("has") || wordInlowercase.equals("had") || wordInlowercase.equals("having"))
						lastIsHave = true;
					else
						lastIsHave = false;
	
					return result;
				
				}
				
				if (wordInlowercase.equals("have") || wordInlowercase.equals("has") || wordInlowercase.equals("had") || wordInlowercase.equals("having"))
					lastIsHave = true;
				else
					lastIsHave = false;
		
			// neither regular words nor stop words	
			}else{
			
				result[0] = taggedWord;

				lastIsHave = false;
				
				return result;
			}

		}
		
		return null;
		
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		Morphology morph = new Morphology();
		LSA_Model test1 = new LSA_Model("/home/lushan1/nlp/model/SVD/20120611/Gutenberg2010AllW5");

        InputStreamReader isReader = new InputStreamReader(System.in);
        BufferedReader bufReader = new BufferedReader(isReader);
        while (true) {
            System.out.print("\n\nINPUT> ");
            System.out.flush();
            String line = bufReader.readLine();
            
            if (line == null || line.length() < 1 
                || line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit"))
                break;
            
            StanfordTermTokenizer test = new StanfordTermTokenizer(line, new String[]{"have", "is", "an", "the"}, morph, test1.vocabulary);
            
            String[] outputs;
            while ((outputs = test.getNextValidWord()) != null){ 
            	System.out.print(outputs[0]);
            	
            	if (outputs[1] != null)
            		System.out.print("|" + outputs[1]);
            	
            	System.out.print(" ");
            }
        }
	}

}
