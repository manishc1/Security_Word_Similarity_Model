package edu.umbc.similarity.dictionary;

import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;
import edu.stanford.nlp.process.Morphology;


import edu.stanford.nlp.ling.Word;

public class TermTokenizer {

	String text;
	StringTokenizer st;
	String pre_word;
	public DictEntry[] vocabulary;
	public Morphology morpha;
	
	public TermTokenizer(Dictionary2 dict, String text, Morphology m) {
		// TODO Auto-generated constructor stub
		vocabulary = dict.dict;
		morpha = m;
		
		st = new StringTokenizer(text, " ");
		
		if (st.hasMoreTokens())
			pre_word = st.nextToken();
		else
			pre_word = null;
	}
	
	public int index(String term){
		return Arrays.binarySearch(vocabulary, new DictEntry(term, ""));
	}

	
	public String stem(String word){
		
		if (!word.equals(""))
			return morpha.stem(new Word(word)).value();
		else
			return "";
	}
	
	public String nextTerm(){

		String cur_word;

		if (pre_word == null)
			return null;
		
		if (st.hasMoreTokens())
			cur_word = st.nextToken();
		else{
			String wordToReturn = pre_word;
			pre_word = null;
			return stem(clean(wordToReturn));
		}
		
		if ((Character.isUpperCase(pre_word.charAt(pre_word.length()-1)) || Character.isLowerCase(pre_word.charAt(pre_word.length()-1))) &&
			(Character.isUpperCase(cur_word.charAt(0)) || Character.isLowerCase(cur_word.charAt(0)))) {
			
			String twoWordsTerm;
			
			if (Character.isUpperCase(pre_word.charAt(0)) && Character.isUpperCase(cur_word.charAt(0)))
				twoWordsTerm = pre_word + " " + cur_word;
			else
				twoWordsTerm = stem(clean(pre_word)) + " " + stem(clean(cur_word)); 
			
			int found = index(twoWordsTerm);
			
			if (found >= 0){
			
				if (st.hasMoreTokens())
					pre_word = st.nextToken();
				else
					pre_word = null;
				
				return twoWordsTerm;
			}
		}
		
		String wordToReturn = pre_word;
		pre_word = cur_word;
		
		return stem(clean(wordToReturn));

	}
	
	public static String clean(String word){
		
		int pre_index;
		int post_index;
		
		for (pre_index=0; pre_index < word.length(); pre_index++){
			if (Character.isLowerCase(word.charAt(pre_index)) || Character.isUpperCase(word.charAt(pre_index)))
				break;
		}
		
		if (pre_index == word.length())
			return "";
		
		for (post_index = word.length()-1; post_index > 0; post_index--){
			if (Character.isLowerCase(word.charAt(post_index)) || Character.isUpperCase(word.charAt(post_index)) || word.charAt(post_index) == '.')
				break;
		}
		
		// Mr. Dr. U.S. etc
		if (Character.isUpperCase(word.charAt(pre_index)) && word.charAt(post_index) == '.')
			return word.substring(pre_index, post_index + 1);
		
		// USA
		int index;
		
		for (index = pre_index; index <= post_index; index++){
			if (Character.isLowerCase(word.charAt(index)))
				break;
		}
		
		if (index == post_index + 1)
			return word.substring(pre_index, post_index + 1);
		

		for (post_index = word.length()-1; post_index > 0; post_index--){
			if (Character.isLowerCase(word.charAt(post_index)) || Character.isUpperCase(word.charAt(post_index)))
				break;
		}
		
		return word.substring(pre_index, post_index + 1).toLowerCase();
		
	}
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		/*
		System.out.println(TermTokenizer.clean("(USA"));
		System.out.println(TermTokenizer.clean("U.S.A."));
		System.out.println(TermTokenizer.clean("Dr."));
		System.out.println(TermTokenizer.clean("Dr"));
		System.out.println(TermTokenizer.clean("Alma alter."));
		System.out.println(TermTokenizer.clean("Drive)"));
		System.out.println(TermTokenizer.clean("*"));
		*/
		
		/*
		Dictionary2 dict = new Dictionary2("/home/lushan1/nlp/dictionary/vocabulary");
		Morphology morpha = new Morphology();
		TermTokenizer tt = new TermTokenizer(dict, "a (computer) connected to the internet that organisation a series of web pages on the World Wide Web", morpha);

		
		String term;
		while ((term = tt.nextTerm()) != null){
			System.out.println(term);
		}
		*/

		Morphology morpha = new Morphology();

		System.out.println(morpha.lemma("organisation", "NN"));
		
	}

}
