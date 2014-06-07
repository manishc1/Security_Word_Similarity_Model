package edu.umbc.similarity.dictionary;

public class DictEntry implements Comparable<DictEntry>{

	public String term;
	public String definition;
	
	public DictEntry(String p_term, String p_gross) {
		// TODO Auto-generated constructor stub
		term = p_term;
		definition = p_gross;
	}
	
	public int compareTo(DictEntry o) {
		// TODO Auto-generated method stub
		return this.term.compareTo(o.term);
	}
	
	public String toString(){
		return term + ": " + definition;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
