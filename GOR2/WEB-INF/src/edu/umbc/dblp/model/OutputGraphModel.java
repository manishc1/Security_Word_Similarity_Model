package edu.umbc.dblp.model;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;


public class OutputGraphModel implements Comparable<OutputGraphModel>{

	public HashMap<String, OutputNode> nodes;
	public Vector<OutputLink> relations;
	public double fitness;
	public PathFitnessElement[] PathFitnessElementInOrder;
	public InputLink[] linksInOrder;
	public NodeMapElement[] NodeMapElementInOrder;
	public InputNode[] nodesInOrder;
	public HashMap<InputLink, PathFitnessElement> link_mapping;
	public HashMap<InputNode, NodeMapElement> node_mapping;
	public int surrogate_key;
	

	
	private static String PREFIX = 	"PREFIX dbo: <http://dbpedia.org/ontology/>" + "\n";
	public HashMap<String, String> foafTermMapping;

	
	public OutputGraphModel(int numberOfInputNodes, int numberOfInputLinks, HashMap<String, String> foafMapping){
		nodes = new HashMap<String, OutputNode>();
		relations = new Vector<OutputLink>();
		NodeMapElementInOrder = new NodeMapElement[numberOfInputNodes];
		PathFitnessElementInOrder = new PathFitnessElement[numberOfInputLinks];
		link_mapping = new HashMap<InputLink, PathFitnessElement>();
		node_mapping = new HashMap<InputNode, NodeMapElement>();
		fitness = 1.0;
		surrogate_key = 0;
		foafTermMapping = foafMapping;
	}
	
	public String getNewNodeID(){
		
		surrogate_key ++;
		return "000" + surrogate_key;
		
	}
	
	
	public OutputLink addOneRelation(String flattenedRelation) throws Exception{
		
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
		
		OutputNode subjectNode = nodes.get(subject_id);
		if (subjectNode == null){
			
			if (subject_type == null) {
				throw new Exception("The subject " + subject + " must specify a type when it is first used.");
			}
			
			subjectNode = new OutputNode(subject_id, subject_type);
			
			nodes.put(subjectNode.id, subjectNode);
			
		}else{
			if (subject_type != null && !subjectNode.class_id.equals(subject_type)){
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
		
		OutputNode objectNode = nodes.get(object_id);
		if (objectNode == null){
			objectNode = new OutputNode(object_id, object_type);
			nodes.put(objectNode.id, objectNode);
			
		}else{
			if (objectNode.class_id == null && object_type !=null){
				throw new Exception("The object " + object + " is specified with a type which is not consistent with its initial null setting.");
			}
			
			if (objectNode.class_id != null && object_type != null && !objectNode.class_id.equals(object_type)){
				throw new Exception("The object " + object + " is specified with a type which is not consistent with its initial type.");
			}
		}

		OutputLink link = new OutputLink(subjectNode, predicate, objectNode);
		relations.add(link);
		
		subjectNode.outgoingLinks.add(link);
		objectNode.incomingLinks.add(link);
		
		return link;
	}
	
	
	
	public OutputNode addOneNode(String flattenedNode) throws Exception{
		
		String subject = flattenedNode;
		
		if (subject == null )
			return null;
		
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
		
		OutputNode subjectNode = nodes.get(subject_id);
		if (subjectNode == null){
			
			if (subject_type == null) {
				throw new Exception("The subject " + subject + " must specify a type when it is first used.");
			}
			
			subjectNode = new OutputNode(subject_id, subject_type);
			
			nodes.put(subjectNode.id, subjectNode);
			
		}else{
			if (subject_type != null && !subjectNode.class_id.equals(subject_type)){
				throw new Exception("The subject " + subject + " is specifying a type which is not consistent with its initial type.");
			}
		}
		
		return subjectNode;
	}

	
	public String toString(){
		
		String result = "";
		
		/*
		for (OutputNode node :nodes.values()){
			result += "* " + node.print() + "\n";
		}

		for (OutputLink link :relations){
			result += "& " + link.print() + "\n";
		}
		*/
		result += "overall fitness is " + fitness + "||";
		
		for (int i = 0; i < PathFitnessElementInOrder.length; i++){
			result += " " + PathFitnessElementInOrder[i].display() + " |";
		}
		
		if (nodesInOrder.length == 1){
			result += " " + NodeMapElementInOrder[0].toString() + " |";
		}
		
		result += "\n";
		
		return result;
	}
	
	
	public String signature(){
		
		String result = "";
		
		for (int i = 0; i < PathFitnessElementInOrder.length; i++){
			
			if (i != PathFitnessElementInOrder.length - 1)
				result += PathFitnessElementInOrder[i].signature() + " || ";
			else
				result += PathFitnessElementInOrder[i].signature();
		}
		
		if (nodesInOrder.length == 1){
			result += NodeMapElementInOrder[0].signature();
		}
		
		return result;
	}
	
	
	public String display(){
		
		String result = "";
		
		for (OutputNode node :nodes.values()){
			result += "* " + node.print() + "\n";
		}

		for (OutputLink link :relations){
			result += "& " + link.print() + "\n";
		}
		
		result += "\n";
		
		return result;
	}

	
	
	public void populateGraph(CorrelationModel correlation_model) throws Exception{
		
		
		for (int i = 0; i < NodeMapElementInOrder.length; i++){
			
			NodeMapElement nodeMap = NodeMapElementInOrder[i];
			
			OutputNode node = new OutputNode(nodeMap.id, nodeMap.classlabel, nodeMap.isBlankNode, nodeMap.isVariable);
			
			nodes.put(node.id, node);
		}
		
		for (int i = 0; i < PathFitnessElementInOrder.length; i++){
			
			if (PathFitnessElementInOrder[i].isAttribute && PathFitnessElementInOrder[i].propertyLabels.length > 1)
				continue;
			
			OutputNode[] nodesInPath = new OutputNode[PathFitnessElementInOrder[i].classLabels.length];
			
			nodesInPath[0] = nodes.get(linksInOrder[i].subject.id);
			nodesInPath[nodesInPath.length - 1] = nodes.get(linksInOrder[i].object.id);
			
			for (int j = 1; j < PathFitnessElementInOrder[i].classLabels.length - 1; j++){
				
				String nodeID = getNewNodeID();
				nodesInPath[j] = addOneNode("*" + nodeID + "/" + PathFitnessElementInOrder[i].classLabels[j]);
			}
			
			
			for (int j = 0; j < PathFitnessElementInOrder[i].propertyLabels.length; j++){
				
				String property = PathFitnessElementInOrder[i].propertyLabels[j];
				
				if (PathFitnessElementInOrder[i].propertyDirections[j] == 0){
					
					String subject = nodesInPath[j].toString();
					String object = nodesInPath[j+1].toString();
					addOneRelation(subject + ", " + property + ", " + object);

				}else{
					String object = nodesInPath[j].toString();
					String subject = nodesInPath[j+1].toString();
					addOneRelation(subject + ", " + property + ", " + object);
				}
			}
		}
		
		
		for (int i = 0; i < PathFitnessElementInOrder.length; i++){
			
			if (!PathFitnessElementInOrder[i].isAttribute || PathFitnessElementInOrder[i].propertyLabels.length == 1)
				continue;
			
			boolean replaced = false;
			
			int pathLength = PathFitnessElementInOrder[i].propertyLabels.length;
			
			if (pathLength == 2){
			
				String attribute = PathFitnessElementInOrder[i].propertyLabels[PathFitnessElementInOrder[i].propertyLabels.length - 1];
				String intermediateProperty = PathFitnessElementInOrder[i].propertyLabels[0];
				int intermediatePropertyDirection = PathFitnessElementInOrder[i].propertyDirections[0];
				
				if (!attribute.startsWith("@"))
					throw new Exception("invalid attribute: " + attribute);
				
				OutputNode subjectNode = nodes.get(linksInOrder[i].subject.id);
				OutputNode attributeNode = nodes.get(linksInOrder[i].object.id);
				
				String subjectClass = subjectNode.class_id;
				
				if (correlation_model.getConceptRelationPMI("<" + subjectClass, attribute) < 0){
					
					if (intermediatePropertyDirection == 1){
						
						for (OutputLink outlink: subjectNode.incomingLinks){
							
							if (replaced) break;
							
							if (!outlink.predicate.equals(intermediateProperty))
								continue;
							
							OutputNode connectedNode = outlink.subject;
							
							if (correlation_model.getConceptRelationPMI("<" + connectedNode.class_id, attribute) > 4){
								
								boolean sameAttributefound = false;
	
								for (OutputLink alink: connectedNode.outgoingLinks){
									if (alink.predicate.equals(attribute))
										sameAttributefound = true;
								}
								
								if (!sameAttributefound){
									
									String replacedClassLabels[] = new String[3];
									String replacedPropertyLabels[] = new String[2];
									int replacedPropertyDirections[] = new int[2];
									
									replacedClassLabels[0] = subjectClass;
									replacedClassLabels[1] = connectedNode.class_id;
									replacedClassLabels[2] = attributeNode.class_id;
									
									replacedPropertyLabels[0] = outlink.predicate;
									replacedPropertyLabels[1] = attribute;
									
									replacedPropertyDirections[0] = 1;
									replacedPropertyDirections[1] = 0;
									
									PathFitnessElement modified = new PathFitnessElement(PathFitnessElementInOrder[i], replacedClassLabels, replacedPropertyLabels, replacedPropertyDirections, 1);
									
									PathFitnessElementInOrder[i] = modified;
									link_mapping.put(linksInOrder[i], PathFitnessElementInOrder[i]);
									
									addOneRelation(connectedNode + ", " + attribute + ", " + attributeNode);
									
									replaced = true;
									break;
								}
							}
						}
						

					}else{
					
						for (OutputLink outlink: subjectNode.outgoingLinks){
							
							if (replaced) break;
							
							if (!outlink.predicate.equals(intermediateProperty))
								continue;
							
							OutputNode connectedNode = outlink.object;
							
							if (correlation_model.getConceptRelationPMI("<" + connectedNode.class_id, attribute) > 4){
								
								boolean sameAttributefound = false;
	
								for (OutputLink alink: connectedNode.outgoingLinks){
									if (alink.predicate.equals(attribute))
										sameAttributefound = true;
								}
								
								if (!sameAttributefound){
									
									String replacedClassLabels[] = new String[3];
									String replacedPropertyLabels[] = new String[2];
									int replacedPropertyDirections[] = new int[2];
									
									replacedClassLabels[0] = subjectClass;
									replacedClassLabels[1] = connectedNode.class_id;
									replacedClassLabels[2] = attributeNode.class_id;
									
									replacedPropertyLabels[0] = outlink.predicate;
									replacedPropertyLabels[1] = attribute;
									
									replacedPropertyDirections[0] = 0;
									replacedPropertyDirections[1] = 0;
									
									PathFitnessElement modified = new PathFitnessElement(PathFitnessElementInOrder[i], replacedClassLabels, replacedPropertyLabels, replacedPropertyDirections, 1);
									
									PathFitnessElementInOrder[i] = modified;
									link_mapping.put(linksInOrder[i], PathFitnessElementInOrder[i]);
									
									addOneRelation(connectedNode + ", " + attribute + ", " + attributeNode);
									
									replaced = true;
									break;
								}
							}
						}
						
					} // end intermediatePropertyDirection if clause
					
				}
			}
			

			if (!replaced){

				OutputNode[] nodesInPath = new OutputNode[PathFitnessElementInOrder[i].classLabels.length];
				
				nodesInPath[0] = nodes.get(linksInOrder[i].subject.id);
				nodesInPath[nodesInPath.length - 1] = nodes.get(linksInOrder[i].object.id);
				
				for (int j = 1; j < PathFitnessElementInOrder[i].classLabels.length - 1; j++){
					
					String nodeID = getNewNodeID();
					nodesInPath[j] = addOneNode("*" + nodeID + "/" + PathFitnessElementInOrder[i].classLabels[j]);
				}
				
				
				for (int j = 0; j < PathFitnessElementInOrder[i].propertyLabels.length; j++){
					
					String property = PathFitnessElementInOrder[i].propertyLabels[j];
					
					if (PathFitnessElementInOrder[i].propertyDirections[j] == 0){
						
						String subject = nodesInPath[j].toString();
						String object = nodesInPath[j+1].toString();
						addOneRelation(subject + ", " + property + ", " + object);
	
					}else{
						String object = nodesInPath[j].toString();
						String subject = nodesInPath[j+1].toString();
						addOneRelation(subject + ", " + property + ", " + object);
					}
				}

			}
			
		}

		//System.out.println(this.display());
		
	}
	
	
	public void computeFitness() throws Exception{
		
		for (int i = 0; i < PathFitnessElementInOrder.length; i++){
			
			fitness *= PathFitnessElementInOrder[i].fitness;
		}
		
		if (nodesInOrder.length == 1){
			fitness = NodeMapElementInOrder[0].sim;
		}
		
	}

	
	public int compareTo(OutputGraphModel o) {
		// TODO Auto-generated method stub
		return (int) Math.signum((o.fitness - this.fitness));
	}

	/*
	 * wayToMatchEntityName: 
	 * 		0: exact match
	 * 		1: append class
	 * 		2: bif:contains
	 */
	public String toSparqlQuery(int wayToMatchEntityName){
		
		OutputNode[] OutputNodes = nodes.values().toArray(new OutputNode[nodes.values().size()]);
		OutputLink[] OutputLinks = relations.toArray(new OutputLink[relations.size()]);

		try{
	
			Vector<String> variables = new Vector<String>();
			int instanceKeyNo = 0;
			int dummyNo = 0;
			HashMap<String, Integer> instanceKeyMap = new HashMap<String, Integer>();
			
			
			String query = "";

			for (int i = 0; i < OutputNodes.length; i++){
	
				if (OutputNodes[i].id.startsWith("?")){
					
					String classTerm = addPrefix(OutputNodes[i]);
					if (classTerm != null && !classTerm.equals("dbo:Thing")){
						
						if (classTerm.startsWith("dbo:~")){
							
							String virutal_class = OutputNodes[i].class_id;

							String vClass = virutal_class.substring(1, virutal_class.length());
							boolean found = false;
							
							for (OutputLink link:OutputNodes[i].incomingLinks){
								if (link.predicate.endsWith(vClass) || link.predicate.equalsIgnoreCase(vClass)){
									found = true;
									break;
								}
							}

							if (!found){
								query += "  ?dummy" + dummyNo + " dbo:" + vClass.toLowerCase() + " " + OutputNodes[i].id + " ." + "\n";
								dummyNo ++;
							}
								
						}else 
							query += "  " + OutputNodes[i].id + " a " + classTerm + " ." + "\n";
						
					}
					
				}else{
					
					boolean useTextSearch = true;
					String entityName = "";
					
					if (useTextSearch){
						
						String classTerm = addPrefix(OutputNodes[i]);
						
						if (classTerm != null && !classTerm.equals("dbo:Thing")){
							
							if (classTerm.startsWith("dbo:~")){
								
								String virutal_class = OutputNodes[i].class_id;
								
								String vClass = virutal_class.substring(1, virutal_class.length());
								boolean found = false;
								
								for (OutputLink link:OutputNodes[i].incomingLinks){
									if (link.predicate.endsWith(vClass) || link.predicate.equalsIgnoreCase(vClass)){
										found = true;
										break;
									}
								}
	
								if (!found){
									query += "  ?dummy" + dummyNo + " dbo:" + vClass.toLowerCase() + " ?" + instanceKeyNo + " ." + "\n";
									dummyNo ++;
								}

									
							}else
								query += "  " + "?" + instanceKeyNo + " a " + classTerm + " ." + "\n";
						}
						
						String term = OutputNodes[i].class_id;
						
						if (term.equals("^Date")){
							
						}else if (term.equals("^Number")){
							
						}else{
							
							if (wayToMatchEntityName == 0){
								query += "  " + "?" + instanceKeyNo + " rdfs:label \"" + OutputNodes[i].id + "\"@en ." + "\n";
								
							}else if (wayToMatchEntityName == 1){
								query += "  " + "?" + instanceKeyNo + " rdfs:label \"" + OutputNodes[i].id + " " + OutputNodes[i].class_id + "\"@en ." + "\n";
								
							}else{
								query += "  " + "?" + instanceKeyNo + " rdfs:label ?label" + instanceKeyNo + " ." + "\n";
								query += "  " + "?label" + instanceKeyNo + " bif:contains '\"" + OutputNodes[i].id + "\"' ." + "\n";
							}
						}

						instanceKeyMap.put(OutputNodes[i].id + "/" + OutputNodes[i].class_id, instanceKeyNo);
						instanceKeyNo ++;
					}
				}
			}
			
			for (int i = 0; i < OutputLinks.length; i++){
				query += "  " + getEntityID(OutputLinks[i].subject.id, OutputLinks[i].subject.class_id, instanceKeyMap) + " "
					+ addPrefix(OutputLinks[i]) + " "
					+ getEntityID(OutputLinks[i].object.id, OutputLinks[i].object.class_id, instanceKeyMap) + " ." + "\n";
			}
			
			query += "}" + "\n";
			
			
			String selection = "";
			
			selection += PREFIX;
			selection += "\n";
			
			selection += "SELECT DISTINCT ";
			
			for (int i = 0; i < OutputNodes.length; i++){
				if (OutputNodes[i].id.startsWith("?") && !OutputNodes[i].isBlankNode)
					variables.add(OutputNodes[i].id);
			}

			/*
			for (int i = 0; i < instanceKeyNo; i++){
				variables.add("?" + i);
			}
			*/
			
			for (int i = 0; i < variables.size(); i++){
				if (i == variables.size() - 1){
					selection += variables.get(i) + " WHERE {" + "\n";
				}else
					selection += variables.get(i) + ", ";
			}

			return selection + query;
		
		}catch (Exception e){
			return "This SPARQL query is not available because " + e.getMessage();
		}
		
	}
	 
	
	private String capitalize(String id) {
		// TODO Auto-generated method stub
		
		String result = "";
		StringTokenizer st = new StringTokenizer(id, " ");
		
		int count = 0;
		
		while (st.hasMoreElements()){
			
			String word = st.nextToken();
			String capWord = Character.toUpperCase(word.charAt(0)) + word.substring(1, word.length());
			
			if (count == 0)
				result = capWord;
			else
				result += " " + capWord;
			
			count ++;
		}
		
		return result;
	}

	private String addPrefix(OutputNode node) throws Exception{
		
		String term = node.class_id;
		
		String ontologyTerm = foafTermMapping.get(term);
		
		if (ontologyTerm != null)
			return ontologyTerm;
		//else if (term.startsWith("#") || term.equals("Number") || term.equals("Date") || term.equals("Time"))
		else if (term.startsWith("^")){
			return null;
		}else
			return "dbo:" + term;
		
	}
	
	private String addPrefix(OutputLink link) throws Exception{
		
		
		String term = link.predicate;
		
		String ontologyTerm = foafTermMapping.get(term);
		
		if (ontologyTerm != null)
			return ontologyTerm;
		else if (term.equals("@name"))
			return "rdfs:label";
		else if (term.startsWith("@"))
			return "dbo:" + term.substring(1, term.length());
		else
			return "dbo:" + term;

	}

	
	private String getEntityID(String id, String type, HashMap<String, Integer> idMap) throws Exception{
		
		if (id.startsWith("?"))
			return id;
		else{
			Integer instanceId = idMap.get(id + "/" + type);
			if (instanceId != null)
				return "?" + String.valueOf(idMap.get(id + "/" + type));
			else
				throw new Exception("no id " + id + " in the idMap");
			
		}
	}

	
	public boolean reduceNonEssentialRelation() {

		for (int i = 0; i < relations.size(); i++){
			
			OutputLink link = relations.elementAt(i);
			
			if (!link.subject.isVariable && !link.object.isVariable && link.object.incomingLinks.size() == 1 && link.object.outgoingLinks.size() == 0){
				relations.remove(i);
				nodes.remove(link.object.id);
				return true;
			}
		}
		
		return false;
		
	}

	public boolean hasTypeConstraints() {

		for (OutputNode node: nodes.values()){
			
			if (!node.class_id.equals("Thing") && !node.class_id.startsWith("^") && !node.class_id.startsWith("~"))
				return true;
		}
		
		return false;
	}

	
	public void removeMostGeneralTypeConstraint(CorrelationModel model) {

		OutputNode nodeWithMostGeneralType = null;
		int mostGeneralTypeFrequency = 0;
		
		for (OutputNode node: nodes.values()){

			if (!node.class_id.equals("Thing") && !node.class_id.startsWith("^") && !node.class_id.startsWith("~")){
				
				int frequency = model.undirectedConceptFrequencies.get(node.class_id);
				
				if (frequency > mostGeneralTypeFrequency){
					nodeWithMostGeneralType = node;
					mostGeneralTypeFrequency = frequency;
				}
			}
		}
		
		nodeWithMostGeneralType.class_id = "Thing";

	}

	

	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		OutputGraphModel test = new OutputGraphModel(2, 1, null);
		test.addOneRelation("*x/Author, writes, ?y/Book");
		//test.addOneRelation("?y/Book, publishedBy, ?z/Publisher");
		//test.addOneRelation("?k/Person, reads, ?y/Book");
		//test.addOneRelation("?y, price, $10");
		System.out.println(test);
		System.out.println(test.toSparqlQuery(2));
	}




}
