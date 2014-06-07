package edu.umbc.wordSimilarity2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

import edu.stanford.nlp.process.Morphology;

public class LingpipeWordTokenizer {

	String paragraph;
	String[] stopWords;
	StringTokenizer st;
	Morphology morpha;
	boolean lastIsHave;
	String[] result;

	
	public LingpipeWordTokenizer(String paragraph_par, String[] stopwords_par, Morphology morpha_par) {
		// TODO Auto-generated constructor stub
		paragraph = paragraph_par;
		stopWords = stopwords_par;
		//Arrays.sort(stopWords);
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
			
			if (Character.isUpperCase(word.charAt(0)) || Character.isLowerCase(word.charAt(0))){
			
				String wordInlowercase = word.toLowerCase();
				
				if (Arrays.binarySearch(stopWords, wordInlowercase) < 0){
					
					if (posTag.startsWith("nn")){
						if (posTag.equals("nn$")){
							int k = word.indexOf("'");
							if (k > 0)
								result[0] = morpha.stem(word.substring(0, k), "NN").word() + "_NN";
							else{
								if (word.endsWith(">"))
									continue;
								else
									result[0] = morpha.stem(word, "NN").word() + "_NN";
							}
							
						}else if (posTag.equals("nns$")){
							int k = word.indexOf("'");
							if (k > 0)
								result[0] = morpha.stem(word.substring(0, k), "NN").word() + "_NN";
							else{
								if (word.endsWith(">"))
									continue;
								else
									result[0] = morpha.stem(word, "NN").word() + "_NN";
							}
						}else
							result[0] = morpha.stem(word, "NN").word() + "_NN";
					}
						
					else if (posTag.startsWith("v")){ 
						
						if (posTag.equals("vbn")){
							
							if (lastIsHave){
								result[0] = morpha.stem(word, "VB").word() + "_VB";
								
							}else{
								result[0] = morpha.stem(word, "VB").word() + "_VB";
								result[1] = wordInlowercase + "_JJ";
							}
							
						}else{
							result[0] = morpha.stem(word, "VB").word() + "_VB";
						}
						
					}else if (posTag.startsWith("jj"))
						result[0] = wordInlowercase + "_JJ";
					
					else if (posTag.startsWith("rb") || posTag.startsWith("ql"))
						result[0] = wordInlowercase + "_RB";
					
					else if (posTag.equals("np") && word.endsWith("."))
						result[0] = word + "_NN";

					else 
						result[0] = wordInlowercase + "_OT";
					
					
					if (posTag.startsWith("hv"))
						lastIsHave = true;
					else
						lastIsHave = false;
	
					return result;
				
				}
			}
			
			
			if (posTag.startsWith("hv"))
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
            
            LingpipeWordTokenizer test = new LingpipeWordTokenizer(line, new String[]{"have", "is", "an", "the"}, morph);
            
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
