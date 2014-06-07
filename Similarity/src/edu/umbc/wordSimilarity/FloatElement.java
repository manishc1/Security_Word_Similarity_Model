package edu.umbc.wordSimilarity;

public class FloatElement implements Comparable<FloatElement>{
	
	public String word;
	public float value;

	public FloatElement(String par_word, float par_value) {
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

	public int compareTo(FloatElement o) {
		// TODO Auto-generated method stub
		return (int) Math.signum((o.value - this.value));
	}

}
