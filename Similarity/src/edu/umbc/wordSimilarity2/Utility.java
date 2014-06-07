package edu.umbc.wordSimilarity2;

public class Utility {
	
	//public static int A = 700;
	public static double A = 0;
	public static double B = 0;

	/*
	public static float getPromotedPMI(float pmi, int frequency){
		
		if (frequency > threshold)
			return pmi * (float) Math.log(Math.log(frequency));
		else
			return pmi * (float) Math.log(Math.log(threshold));
	}
	*/

	public static float getPromotedPMI(float pmi, int frequency, String pos){
		
		if (pos.equals("NN") || pos.equals("VB"))
			A = 2.5;
		else if (pos.equals("JJ"))
			A = 2.75;
		else if (pos.equals("RB"))
			A = 1.5;
		
		return pmi + (float) (A * Math.log(Math.log(frequency) + B));
		
	}

	
	public static float getPromotedPMI(float pmi, int frequency1, int frequency2, String pos){
		
		if (pos.equals("NN") || pos.equals("VB"))
			A = 2.5;
		else if (pos.equals("JJ"))
			A = 2.75;
		else if (pos.equals("RB"))
			A = 1.5;
		
		return pmi + (float) (A * Math.log(Math.log(frequency1) + B)) + (float) (A * Math.log(Math.log(frequency2) + B));
		
	}
	
	public Utility() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
