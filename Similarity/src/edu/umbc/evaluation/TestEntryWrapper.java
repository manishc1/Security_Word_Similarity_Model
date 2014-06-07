package edu.umbc.evaluation;

import java.util.HashSet;
import java.util.StringTokenizer;

public class TestEntryWrapper {

	String targetWord;
	
	String firstSynonym;
	
	HashSet<String> allSynonyms;
	
	HashSet<String> synonymsInFirstSense;
	
	int numberOfSenses;
	
	
	public TestEntryWrapper(String line) {
		// TODO Auto-generated constructor stub
		allSynonyms = new HashSet<String>();
		synonymsInFirstSense = new HashSet<String>();
		numberOfSenses = 1;
		
		StringTokenizer st = new StringTokenizer(line, ":,;", true);
		
		targetWord = st.nextToken();
		
		if (!st.nextToken().equals(":")){
			System.out.println(": is missing!");
			System.exit(-1);
		}
		
		boolean doneFirstSynonym = false;
		boolean doneFirstSense = false;
		
		while (st.hasMoreTokens()){
			String token = st.nextToken();
			
			if (!doneFirstSynonym){
				if (token.equals(",") || token.equals(";"))
					firstSynonym = null;
				else
					firstSynonym = token;
				
				doneFirstSynonym = true;
			}
			
			if (!doneFirstSense && token.equals(";"))
				doneFirstSense = true;
			
			if (token.equals(";"))
				numberOfSenses ++;
			
			if (!token.equals(",") && !token.equals(";")){

				allSynonyms.add(token);
				
				if (!doneFirstSense){
					synonymsInFirstSense.add(token);
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//TestEntryWrapper entry = new TestEntryWrapper("absolute:unconditional,,uncontrolled,supreme;consummate,faultless,ideal;actual,real;,;autocratic,despotic");
		TestEntryWrapper entry = new TestEntryWrapper("absolute:ideal,abc");
		System.out.println(entry.targetWord);
		System.out.println(entry.firstSynonym);
		System.out.println(entry.synonymsInFirstSense);
		System.out.println(entry.allSynonyms);
		System.out.println(entry.numberOfSenses);
	}

}
