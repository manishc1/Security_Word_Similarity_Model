package edu.umbc.dblp.model;

import java.util.ArrayList;


public class InputLink {
	
	public InputNode subject;
	public String predicate;
	public InputNode object;

	public ArrayList<ChoiceElement> choices;
	public double temp_fitness;
	public int temp_index;
	public boolean temp_hasBeenComputed;
	public String sub_direction;
	public String obj_direction;
	
	public ArrayList<SimilarPropertyElement> temp_property_cluster;
	
	public InputLink(InputNode s, String p, InputNode o) {
		// TODO Auto-generated constructor stub
		subject = s;
		predicate = p;
		object = o;
		temp_hasBeenComputed = false;
		temp_fitness = 0;
		choices = new ArrayList<ChoiceElement>();
		temp_index = -1;
		sub_direction = "";
		obj_direction = "";
		//temp_property_cluster = new ArrayList<SimilarPropertyElement>();
	}

	public void reset(){
		//temp_hasBeenComputed = false;
		temp_fitness = 0;
		temp_index = -1;
		sub_direction = "";
		obj_direction = "";
	}
	
	/*
    public void setAssociation(double value){
    	temp_association = value;
    	temp_hasBeenComputed = true;
    }
    */

	public ChoiceElement getChoice(){
		if (temp_index != -1)
			return choices.get(temp_index);
		else
			return null;
	}
	
	public boolean hasAttributeNode(){
		
		if (subject.isAttributeNode() || object.isAttributeNode())
			return true;
		else
			return false;
		
	}
	
	public String toString(){
				
		String result;
		
		result = subject + ":" + predicate + ":" + object;
		
		return result; 
	}
	
	public int hashCode(){ 
		
		return this.toString().hashCode(); 
		
	}
	
	public boolean equals(Object obj) { 
	    return this.toString().equals(obj.toString()); 
	 }

	
	public String print(){
		
		String result;
		
		result = subject + ":" + predicate + ":" + object;
		
		if (temp_property_cluster == null){
		
			result += " has a candidate list including ";
			
			if (choices.size() > 0){
				
				//result += " => ";
				
				for (int i = 0; i < choices.size(); i++){
					result += i + ":" + choices.get(i) + " ";
				}
			}
			
			if (temp_index != -1){
				
				/*
				result += "<";
	
				if (temp_reverse)
					result += "!";
				
				result += temp_index + ">";
				*/
				result += "(the selected choice is " + choices.get(temp_index) + ", with subject direction " + sub_direction + " and object direction " + obj_direction + ")";

			}else
				result += "(no reasonable choice exists in the candidate list" + ", with subject direction " + sub_direction + " and object direction " + obj_direction + ")";
			
		
			
		}else{
			
			result += " => ";
			
			for (int i = 0; i < temp_property_cluster.size(); i++){
				result += i + ":" + temp_property_cluster.get(i) + " ";
			}
			
		}
			
		return result; 
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
