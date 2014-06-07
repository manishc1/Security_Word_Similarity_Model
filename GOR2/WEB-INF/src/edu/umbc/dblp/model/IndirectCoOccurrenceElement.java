package edu.umbc.dblp.model;

import java.io.Serializable;
import java.util.HashMap;

public class IndirectCoOccurrenceElement implements Serializable{

	private static final long serialVersionUID = 1L;
	
	double maxCoOccurrence;
	HashMap<String, Double> paths;
	
	public IndirectCoOccurrenceElement() {
		// TODO Auto-generated constructor stub
		this.maxCoOccurrence = 0;
		this.paths = new HashMap<String, Double>();
	}
	
	
	public String toString(){
		return Double.toString(maxCoOccurrence);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
