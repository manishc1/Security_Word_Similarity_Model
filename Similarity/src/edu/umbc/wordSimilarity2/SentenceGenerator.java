/*
 * Copyright 2009 Author: Lushan Han at Ebiquity Lab, UMBC
 *  
 */
package edu.umbc.wordSimilarity2;

import java.text.BreakIterator;

public class SentenceGenerator {
	
	public static BreakIterator bi;
	int index;
	String text;

	public SentenceGenerator() {
		// TODO Auto-generated constructor stub
		bi = BreakIterator.getSentenceInstance();
	}

	public void setText(String inputText){
		text = inputText;
		bi.setText(inputText);
		index = 0;
	}
	
	public String getSentence(){
		
		if (bi.next() != BreakIterator.DONE){
			String sentense = text.substring(index, bi.current());
			index = bi.current();
			return sentense;
			
		} else {
			return null;
		}
			
	}
	
	public void close(){
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String text = "Tan is a single-precision Mr. Brown 32-bit IEEE 754 floating point. Its range of values is beyond the scope of this discussion, but is specified in section 4.2.3 of the Java Language Specification. As with the recommendations for byte and short, use a float (instead of double) if you need to save memory in large arrays of floating point numbers. This data type should never be used for precise values, such as currency. For that, you will need to use the java.math.BigDecimal class instead. Numbers and Strings covers BigDecimal and other useful classes provided by the Java platform.";
		SentenceGenerator test = new SentenceGenerator();
		test.setText(text);
		String sentence;
		int i = 0;
		while ((sentence = test.getSentence()) != null){
			i++;
			System.out.println(i + " " + sentence);
		}
	}

}
