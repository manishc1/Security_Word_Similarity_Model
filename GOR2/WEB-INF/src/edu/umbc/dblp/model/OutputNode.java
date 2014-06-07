package edu.umbc.dblp.model;

import java.util.ArrayList;
import java.util.HashSet;


public class OutputNode {

	public String id;         // id is a (numeric or literal) value if type is null.
	public String class_id;
	public boolean isBlankNode;
	public boolean isVariable;
	
	
	public HashSet<OutputLink> outgoingLinks;
	public HashSet<OutputLink> incomingLinks;
	
	
	public OutputNode(String id, String class_id, boolean isBlankNode, boolean isVariable){

		this.id = id;
		this.class_id = class_id;
		this.isBlankNode = isBlankNode;
		this.isVariable = isVariable;
		outgoingLinks = new HashSet<OutputLink>();
		incomingLinks = new HashSet<OutputLink>();
	}
	
	public OutputNode(String ID, String TYPE){

		if (ID.startsWith("*")){
			id = "?" + ID.substring(1, ID.length());
			isBlankNode = true;
			isVariable = true;
		}else if (ID.startsWith("?")){
			id = ID;
			isBlankNode = false;
			isVariable = true;
		}else{	
			id = ID;
			isBlankNode = false;
			isVariable = false;
		}
		
		class_id = TYPE;
		outgoingLinks = new HashSet<OutputLink>();
		incomingLinks = new HashSet<OutputLink>();
	}


	public String toString(){
		
		String result;
		
		result = id + "/" + class_id;
		
		return result; 
	}

	
	public String print(){
		
		String result;
		
		result = id + "/" + class_id;
	
		return result; 
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
