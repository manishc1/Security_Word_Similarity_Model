package edu.umbc.nlp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.umbc.wordSimilarity.GutenbergParagraphGenerator;

public class Utility {
	
	public static int Starter_Length = 80;

	public static boolean IsNormalWriting(String text){
		
		double threshold = 0.09;
		double percent;
		
		percent = getPunctuationPercent(text);
		
		if (percent > threshold)
			return false;
		else
			return true;
		
	}

	
	public static boolean IsNormalWritingForWildText(String text){
		
		double threshold = 0.085; // 0.09;
		double percent;
		
		if (text.length() < 200) return false;
		
		if (text.charAt(0) != '"' && !Character.isUpperCase(text.charAt(0)))
			return false;
		
		if (!text.endsWith(".") && !text.endsWith("?") && !text.endsWith("\""))
			return false;
		
		percent = getInvalidCharactersPercent(text);
		
		if (percent > threshold)
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
			}else{
				//System.out.println(text.charAt(i));
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
	
	public static double getInvalidCharactersPercent(String text){
		
		int validChars = 0;
		char lastChar = ' ';
		int state = 0;   // 0:initial; 1:wait; 2:safe; 3:wrong
		
		for (int i=0; i<text.length(); i++){

			if (lastChar == '?' && text.charAt(i) != ' ' && text.charAt(i) != '"'){
				return 1.0;
			}
			
			if (!Character.isLowerCase(lastChar) && text.charAt(i) == '?'){
				return 1.0;
			}
			
			if (text.charAt(i) == '�' || text.charAt(i) == ' ')
				return 1.0;
			
			if (text.charAt(i) == ' '){
			
				state = 0;

			}else if (state == 0){
				
				if (Character.isLowerCase(text.charAt(i)) || Character.isDigit(text.charAt(i)))
					state = 1;
				else
					state = 2;
			
			}else if (state == 1){
				
				if (Character.isUpperCase(text.charAt(i)))
					state = 3;
			}
			
			if (state != 3 && 
					(Character.isLowerCase(text.charAt(i)) || text.charAt(i) == '"' || text.charAt(i) == '.' || (text.charAt(i) == ' ' && lastChar != ' '))){
					//(Character.isUpperCase(text.charAt(i)) || Character.isLowerCase(text.charAt(i)) || text.charAt(i) == '"' || (text.charAt(i) == ' ' && lastChar != ' '))){
				
				validChars ++;
			
			}else{
				//System.out.println(text.charAt(i));
				//if (Character.isUpperCase(text.charAt(i)))
				//	validChars = validChars - 1;
			}
			
			lastChar = text.charAt(i);
		}
		
		if (text.length() == 0)
			return 1.0;
		else
			return (double) (text.length() - validChars) / text.length();
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
		GutenbergParagraphGenerator paragraphs = new GutenbergParagraphGenerator("test.txt", "test");
		
		String paragraph;
		int i = 1;
		
		/*
		while ((paragraph = paragraphs.getNextParagraph()) != null){
			System.out.println(i + ": " + Utility.getPunctuationPercent(paragraph));
			i += 2;
		}
		*/

		/*
		while ((paragraph = paragraphs.getNextParagraph("test.txt")) != null){
			System.out.println(i + ": " + Utility.IsNormalWriting(paragraph));
			i += 2;
		}
		*/

    	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    	String inline = "good today";

    	while(true){

    		System.out.println(Utility.getInvalidCharactersPercent(inline));
    		System.out.println(Utility.IsNormalWritingForWildText(inline));
			
	       	System.out.println("Please input");
        	inline = input.readLine();

		}

		
	}

}
