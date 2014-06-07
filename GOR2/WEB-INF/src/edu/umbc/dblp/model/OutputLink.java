package edu.umbc.dblp.model;

import java.util.ArrayList;


public class OutputLink {
	
	public OutputNode subject;
	public String predicate;
	public OutputNode object;

	//public double sim;
	//public double fitness;
	public String sub_direction;
	public String obj_direction;
	
	
	
	public OutputLink(OutputNode s, String p, OutputNode o) {
		// TODO Auto-generated constructor stub
		subject = s;
		predicate = p;
		object = o;
		sub_direction = "";
		obj_direction = "";
	}

	
	
	public String toString(){
				
		String result;
		
		result = sub_direction + subject + ":" + predicate + ":" + obj_direction + object;
		
		return result; 
	}
	
	
	public String print(){
		
		String result;
		
		result = sub_direction + subject + ":" + predicate + ":" + obj_direction + object;
		
			
		return result; 
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
