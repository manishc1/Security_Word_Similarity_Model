package edu.umbc.nlp;

import java.io.IOException;

import edu.umbc.wordSimilarity.ParagraphGenerator;

public class Utility {

	public static boolean IsNormalWriting(String text){
		
		double threshold = 0.09;
		
		if (getPunctuationPercent(text) > threshold)
			return false;
		else
			return true;
		
	}
	
	public static double getPunctuationPercent(String text){
		
		int validChars = 0;
		int extraSpaceChars = 0;
		char lastChar = ' ';
		
		for (int i=0; i<text.length(); i++){

			if (Character.isUpperCase(text.charAt(i)) || Character.isLowerCase(text.charAt(i)) || text.charAt(i) == '"' || (text.charAt(i) == ' ' && lastChar != ' ')){
			
				validChars ++;
			}
			
			if (text.charAt(i) == ' ' && lastChar == ' ')
				extraSpaceChars ++;
		
			lastChar = text.charAt(i);
		}
		
		if (text.length() - extraSpaceChars > 0)
			return (double) (text.length() - validChars - extraSpaceChars) / (text.length() - extraSpaceChars);
		else
			return 1.0;
	}
	
	public Utility() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ParagraphGenerator paragraphs = new ParagraphGenerator("deduce_infer.txt", "test");
		
		String paragraph;
		int i = 1;
		
		/*
		while ((paragraph = paragraphs.getNextParagraph()) != null){
			System.out.println(i + ": " + Utility.getPunctuationPercent(paragraph));
			i += 2;
		}
		*/

		while ((paragraph = paragraphs.getNextParagraph()) != null){
			System.out.println(i + ": " + Utility.IsNormalWriting(paragraph));
			i += 2;
		}

	}

}
