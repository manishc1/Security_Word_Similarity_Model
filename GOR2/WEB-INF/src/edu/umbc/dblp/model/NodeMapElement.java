package edu.umbc.dblp.model;

import java.util.Arrays;


public class NodeMapElement {

	public String id;         // id is a (numeric or literal) value if type is null.
	public String type;
	public boolean isBlankNode;
	public boolean isVariable;
	
	String classlabel;
	double sim;
	
	public NodeMapElement(String id, String type, boolean isBlankNode, boolean isVariable, String classlabel, double sim) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.type = type;
		this.isBlankNode = isBlankNode;
		this.isVariable = isVariable;
		this.classlabel = classlabel;
		this.sim = sim;
	}

	public String toString(){
		
		return classlabel + "(" + String.format("%1.2f", sim) + ")";
	}
	
	
	public String signature() {
		
		return "[" + classlabel + "]";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
