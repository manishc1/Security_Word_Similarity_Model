package edu.umbc.wordSimilarity2;

public class CoElement implements Comparable<CoElement>{
	
	public String word;
	public int co_occurrences;

	public CoElement(String par_word, int par_co_occurr) {
		// TODO Auto-generated constructor stub
		word = par_word;
		co_occurrences = par_co_occurr;
	}

	public String toString(){
		return word + ": " + co_occurrences;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public int compareTo(CoElement o) {
		// TODO Auto-generated method stub
		return o.co_occurrences - this.co_occurrences;
	}

}
