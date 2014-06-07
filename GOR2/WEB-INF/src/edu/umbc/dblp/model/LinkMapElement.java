package edu.umbc.dblp.model;

import java.util.Vector;

public class LinkMapElement {

	Vector<OutputLink> path;
	double fitness;
	double sim;
	double coOccurrences;
	
	public LinkMapElement() {
		// TODO Auto-generated constructor stub
		path = new Vector<OutputLink>();
		fitness = 0;
		sim = 0;
		coOccurrences = 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
