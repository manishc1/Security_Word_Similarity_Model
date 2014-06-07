package edu.umbc.dblp.model;

import java.util.ArrayList;
import java.util.HashSet;


public class InputNode {

	public String id;         // id is a (numeric or literal) value if type is null.
	public String type;
	public boolean isVariable;
	public boolean isBlankNode;
	
	public HashSet<InputLink> outgoingLinks;
	public HashSet<InputLink> incomingLinks;
	
	public int temp_index;
	public ArrayList<ChoiceElement> choices;
	
	public GeneralizingClassElement temp_class;
	
	public InputNode(String ID, String TYPE){

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
		
		type = TYPE;
		outgoingLinks = new HashSet<InputLink>();
		incomingLinks = new HashSet<InputLink>();
		choices = new ArrayList<ChoiceElement>();
		temp_index = -1;
		temp_class = new GeneralizingClassElement();
	}

	public ChoiceElement getChoice(){
		if (temp_index != -1)
			return choices.get(temp_index);
		else
			return null;
	}
	
	public boolean isAttributeNode(){
		
		String concept = getChoice().term;
		
		//if (concept.startsWith("^") || concept.equals("Literal") || concept.equals("Number") || concept.equals("Year") || concept.equals("Date"))
		if (concept.startsWith("^"))
			return true;
		else
			return false;
		
	}
	
	public String toString(){
		
		String result;
		
		result = id + "/" + type;
		
		return result; 
	}

	public int hashCode(){ 
		
		return toString().hashCode(); 
		
	}
	
	public boolean equals(Object obj) { 
	
	    return toString().equals(obj.toString()); 
	
	}

	
	public String print(){
		
		String result;
		
		result = id + "/" + type;
	
		if (temp_class.term == null){
			
			result += " has a candidate list including ";
			
			if (choices.size() > 0){
				
				//result += " => ";
				
				for (int i = 0; i < choices.size(); i++){
					result += i + ":" + choices.get(i) + " ";
				}
			}

			if (temp_index != -1){
				
				//result += "<" + temp_index + ">";
				result += "(the selected choice is " + choices.get(temp_index) + ")";  
			
			}else{
				result += "(no reasonable choice exists in the candidate list)";
			}
				
		
		}else{
			
			result += " => ";
			result += temp_class.term;
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
