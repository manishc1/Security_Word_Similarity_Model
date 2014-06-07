package edu.umbc.nlp.util;

import java.util.ArrayList;
import java.util.List;

public class StringSimilarity {

	public StringSimilarity() {
		// TODO Auto-generated constructor stub
	}

	public static float getSim(String str1, String str2){
		
		return dice(bigram(str1), bigram(str2));
		
		
	}
	
	public static List<char[]> bigram(String input)
	{
	    ArrayList<char[]> bigram = new ArrayList<char[]>();
	    for (int i = 0; i < input.length() - 1; i++)
	    {
	        char[] chars = new char[2];
	        chars[0] = input.charAt(i);
	        chars[1] = input.charAt(i+1);
	        bigram.add(chars);
	    }
	    return bigram;
	}
	
	public static float dice(List<char[]> bigram1, List<char[]> bigram2)
	{
	    List<char[]> copy = new ArrayList<char[]>(bigram2);
	    
	    int matches = 0;
	    for (int i = bigram1.size(); --i >= 0;)
	    {
	        char[] bigram = bigram1.get(i);
	        for (int j = copy.size(); --j >= 0;)
	        {
	            char[] toMatch = copy.get(j);
	            if (bigram[0] == toMatch[0] && bigram[1] == toMatch[1])
	            {
	                copy.remove(j);
	                matches += 2;
	                break;
	            }
	        }
	    }
	    return (float) matches / (bigram1.size() + bigram2.size());
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Mubarak_NN, Muybarak_NNt

		System.out.println(StringSimilarity.getSim("great", "greatly"));
		System.out.println(StringSimilarity.getSim("advisor", "adviser"));
		System.out.println(StringSimilarity.getSim("e-mail", "email"));
		System.out.println(StringSimilarity.getSim("co-author", "author"));
		System.out.println(StringSimilarity.getSim("coauthor", "author"));
		System.out.println(StringSimilarity.getSim("runtime", "running time"));
		System.out.println(StringSimilarity.getSim("last name", "surname"));
		System.out.println(StringSimilarity.getSim("", "surname"));
		System.out.println(StringSimilarity.getSim("proceedings", "inproceedings"));
		System.out.println(StringSimilarity.getSim("book", "inbook"));
		System.out.println(StringSimilarity.getSim("lat", "latitude"));
		System.out.println(StringSimilarity.getSim("president", "presenter"));


	}

}
