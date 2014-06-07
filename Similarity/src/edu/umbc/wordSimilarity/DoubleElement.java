package edu.umbc.wordSimilarity;

public class DoubleElement implements Comparable<DoubleElement>{
	
	public String word;
	public double value;

	public DoubleElement(String par_word, double par_value) {
		// TODO Auto-generated constructor stub
		word = par_word;
		value = par_value;
	}

	public String toString(){
		return word + ": " + value;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public int compareTo(DoubleElement o) {
		// TODO Auto-generated method stub
		return (int) Math.signum((o.value - this.value));
	}

}
