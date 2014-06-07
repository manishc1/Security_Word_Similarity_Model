package edu.umbc.dblp.model;

public class TraceElement {

	String concept;
	double coOccurrence;
	
	public TraceElement(String concept, double coOccurrence) {
		// TODO Auto-generated constructor stub
		this.concept = concept;
		this.coOccurrence = coOccurrence;
	}
	
	boolean equals(TraceElement o){
		if (concept.equals(o.concept))
			return true;
		else 
			return false;
	}
	
	public String toString(){
		return concept;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
