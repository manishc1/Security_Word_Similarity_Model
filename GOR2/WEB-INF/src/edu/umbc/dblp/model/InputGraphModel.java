package edu.umbc.dblp.model;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

public class InputGraphModel {

	public HashMap<String, InputNode> nodes;
	public Vector<InputLink> relations;
	
	public InputGraphModel(){
		nodes = new HashMap<String, InputNode>();
		relations = new Vector<InputLink>();
	}
	
	public void clear(){
		nodes.clear();
		relations.clear();
	}
	
	public boolean addOneRelation(String flattenedRelation) throws Exception{
		
		StringTokenizer st = new StringTokenizer(flattenedRelation, ",");

		String subject;
		String predicate;
		String object;
		
		try{
			subject = st.nextToken();
			predicate = st.nextToken();
			object = st.nextToken();
		}catch (Exception e){
			throw new Exception("The relation \"" + flattenedRelation + "\" does not contain three elements.");
		}
			
		if (subject == null || predicate == null || object == null || st.hasMoreTokens())
			throw new Exception("The relation \"" + flattenedRelation + "\" does not contain three elements.");
		
		subject = subject.trim();
		predicate = predicate.trim();
		object = object.trim();
		
		String subject_id;
		String subject_type;
		
		int index = subject.indexOf("/");
		if (index > 0){
			subject_id = subject.substring(0, index);
			subject_type = subject.substring(index + 1, subject.length());
		}else{
			subject_id = subject;
			subject_type = null;
		}
		
		if (subject_id.trim().equals("?") || subject_id.trim().equals("*"))
			throw new Exception("You need to assign a unique name to a variable, for example, '?x', '?y' or something meaningful to you");
		
		InputNode subjectNode = nodes.get(subject_id);
		if (subjectNode == null){
			
			/*
			if (subject_type == null) {
				throw new Exception("The subject " + subject + " must specify a type when it is first used.");
			}
			*/
			
			subjectNode = new InputNode(subject_id, getInitialType(subject_id, subject_type));
			
			nodes.put(subject_id, subjectNode);
			
		}else{
			if (subject_type != null && !subjectNode.type.equals(subject_type)){
				throw new Exception("The subject " + subject + " is specified with a type which is not consistent with its initial type.");
			}
		}
		
		String object_id;
		String object_type;

		index = object.indexOf("/");
		if (index > 0){
			object_id = object.substring(0, index);
			object_type = object.substring(index + 1, object.length());
		}else{
			object_id = object;
			object_type = null;
		}
		
		InputNode objectNode = nodes.get(object_id);
		if (objectNode == null){
			objectNode = new InputNode(object_id, getInitialType(object_id, object_type));
			nodes.put(object_id, objectNode);
			
		}else{
			if (objectNode.type == null && object_type !=null){
				throw new Exception("The object " + object + " is specified with a type which is not consistent with its initial null setting.");
			}
			
			if (objectNode.type != null && object_type != null && !objectNode.type.equals(object_type)){
				throw new Exception("The object " + object + " is specified with a type which is not consistent with its initial type.");
			}
		}

		InputLink link = new InputLink(subjectNode, predicate, objectNode);
		relations.add(link);
		
		subjectNode.outgoingLinks.add(link);
		objectNode.incomingLinks.add(link);
		
		return true;
	}
	
	
	public String getInitialType(String id, String type){
		if (type != null)
			return type;
		else if (id.startsWith("?"))
			return "Thing";
		else
			return null;
	}
	
	public boolean addOneNode(String flattenedNode) throws Exception{
		
		String subject = flattenedNode;
		
		if (subject == null )
			return false;
		
		subject = subject.trim();
		
		String subject_id;
		String subject_type;
		
		int index = subject.indexOf("/");
		if (index > 0){
			subject_id = subject.substring(0, index);
			subject_type = subject.substring(index + 1, subject.length());
		}else{
			subject_id = subject;
			subject_type = null;
		}
		
		InputNode subjectNode = nodes.get(subject_id);
		if (subjectNode == null){
			
			if (subject_type == null) {
				throw new Exception("The subject " + subject + " must specify a type when it is first used.");
			}
			
			subjectNode = new InputNode(subject_id, subject_type);
			
			nodes.put(subject_id, subjectNode);
			
		}else{
			if (subject_type != null && !subjectNode.type.equals(subject_type)){
				throw new Exception("The subject " + subject + " is specifying a type which is not consistent with its initial type.");
			}
		}
		
		return true;
	}

	
	public String toString(){
		
		String result = "";
		
		for (InputNode node :nodes.values()){
			result += "* " + node.print() + "\n";
		}

		for (InputLink link :relations){
			result += "& " + link.print() + "\n";
		}
		
		return result;
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		InputGraphModel test = new InputGraphModel();
		test.addOneRelation("*x/Author, writes, ?y/Book");
		//test.addOneRelation("?y/Book, publishedBy, ?z/Publisher");
		//test.addOneRelation("?k/Person, reads, ?y/Book");
		//test.addOneRelation("?y, price, $10");
		System.out.println(test);
	}

}
