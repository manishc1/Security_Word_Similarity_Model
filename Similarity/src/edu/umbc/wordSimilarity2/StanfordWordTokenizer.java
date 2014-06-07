package edu.umbc.wordSimilarity2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

import edu.stanford.nlp.process.Morphology;

public class StanfordWordTokenizer {

	String paragraph;
	String[] stopWords;
	StringTokenizer st;
	Morphology morpha;
	boolean lastIsHave;
	String[] result;
	public static int totalWords = 0;

	
	public StanfordWordTokenizer(String paragraph_par, String[] stopwords_par, Morphology morpha_par) {
		// TODO Auto-generated constructor stub
		paragraph = paragraph_par;
		stopWords = stopwords_par;
		Arrays.sort(stopWords);
		morpha = morpha_par;
		st = new StringTokenizer(paragraph, " ");
		lastIsHave = false;
		result = new String[2];

	}

	
	public String[] getNextValidWord(){
		

		while (st.hasMoreTokens()){

			result[0] = null;
			result[1] = null;
			
			String taggedWord = st.nextToken();
				
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
					
					if (posTag.startsWith("NN")){
						
						if (posTag.startsWith("NNP"))
							result[0] = word + "_NN";
						else{
							result[0] = morpha.stem(word, "NN").word() + "_NN";
						}	
					}else if (posTag.startsWith("VB")){
						
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
						
					}else if (posTag.startsWith("JJ")){
						result[0] = wordInlowercase + "_JJ";
						
						if (wordInlowercase.endsWith("ed"))
							result[1] = morpha.stem(wordInlowercase, "VB").word() + "_VB";
						else if (wordInlowercase.equals("alone") || wordInlowercase.equals("alike") || wordInlowercase.equals("asleep") || wordInlowercase.equals("awake"))
							result[1] = wordInlowercase + "_RB";
					
					}else if (posTag.startsWith("RB")){
						
						result[0] = wordInlowercase + "_RB";
						
						if (wordInlowercase.equals("alone") || wordInlowercase.equals("alike") || wordInlowercase.equals("asleep") || wordInlowercase.equals("awake"))
							result[1] = wordInlowercase + "_JJ";
					
					}else 
						result[0] = wordInlowercase + "_OT";
					
					
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
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		Morphology morph = new Morphology();

        InputStreamReader isReader = new InputStreamReader(System.in);
        BufferedReader bufReader = new BufferedReader(isReader);
        while (true) {
            System.out.print("\n\nINPUT> ");
            System.out.flush();
            String line = bufReader.readLine();
            
            if (line == null || line.length() < 1 
                || line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit"))
                break;
            
            StanfordWordTokenizer test = new StanfordWordTokenizer(line, new String[]{"have", "is", "an", "the"}, morph);
            
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
