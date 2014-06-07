package edu.umbc.wordSimilarity2;

public class FloatElement implements Comparable<FloatElement>{
	
	public String word;
	public float value;
	public float conditionalProb;
	public int frequency;

	public FloatElement(String par_word, float par_value) {
		// TODO Auto-generated constructor stub
		word = par_word;
		value = par_value;
	}

	public FloatElement(String par_word, float par_value, float par_condProb, int par_freq) {
		// TODO Auto-generated constructor stub
		word = par_word;
		value = par_value;
		conditionalProb = par_condProb;
		frequency = par_freq;
	}

	
	public String toString(){

		if (conditionalProb == 0 && frequency == 0)
			return word + ": " + value;
		else
			return word + ": " + String.format("%1.2f", value) + ", " + String.format("%1.2f",conditionalProb) + ", " + frequency;
		
		/*
		if (conditionalProb == 0 && frequency == 0)
			return word;
		else
			return word + "(" + frequency + ")" + String.format("%1.2f",conditionalProb);
		*/
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
