package edu.umbc.dblp.model;

import java.util.Arrays;




public class BestCombinationElement implements Comparable<BestCombinationElement> {

	double bestTotalFitness;
	int numberOfLinks;
	
	public int[] bestNodeChoiceInOrder;
	public int[] bestLinkChoiceInOrder;
	public String[] bestLinkChoiceSubjectDirectionInOrder;
	public String[] bestLinkChoiceObjectDirectionInOrder;
	
	public BestCombinationElement(double fitness, int[] nodeChoice, int[] linkChoice, String[] linkChoiceSubjectDirection, String[] linkChoiceObjectDirection ){
		// TODO Auto-generated constructor stub
		bestTotalFitness = fitness;
		bestNodeChoiceInOrder = nodeChoice;
		bestLinkChoiceInOrder = linkChoice;
		bestLinkChoiceSubjectDirectionInOrder = linkChoiceSubjectDirection;
		bestLinkChoiceObjectDirectionInOrder = linkChoiceObjectDirection;
		numberOfLinks = bestLinkChoiceInOrder.length;
	}

	public int compareTo(BestCombinationElement o) {
		// TODO Auto-generated method stub
		return (int) Math.signum((this.bestTotalFitness - o.bestTotalFitness));
	}
	
	public String toString(){
		return Arrays.toString(bestNodeChoiceInOrder);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
