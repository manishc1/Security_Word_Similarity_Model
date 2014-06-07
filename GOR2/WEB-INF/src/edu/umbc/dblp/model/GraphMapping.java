package edu.umbc.dblp.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import edu.cmu.lti.wikipedia_redirect.IOUtil;
import edu.cmu.lti.wikipedia_redirect.WikipediaRedirect;
import edu.umbc.configure.Configure;
import edu.umbc.dbpedia.model.FloatElement;
import edu.umbc.dbpedia.model.ImprovedRecallResult;
import edu.umbc.dbpedia.util.ComplexPredicate;
import edu.umbc.dbpedia.util.FlexibleDateParser;
import edu.umbc.dbpedia.util.LexicalProcess;
import edu.umbc.dbpedia.util.NounCompound;
import edu.umbc.web.SparqlRunner;

public class GraphMapping {

	//public LinkedGraphModel graph;
	//public Node[] nodesInOrder;
	//public Link[] linksInOrder;
	//public PriorityQueue<BestCombinationElement> bestCombinations;
	//public int[] bestNodeChoiceInOrder;   
	//public int[] bestLinkChoiceInOrder;
	//public boolean[] bestLinkChoiceReverseInOrder;
	//public int MAX_PLACE;
	//private int[] preNodeChoiceInOrder;
	//private int[] curNodeChoiceInOrder;
	//public BestCombinationElement[] results;
	//public ImprovedRecallResult[] improvedResults;
	//long startTime;
	private static String PREFIX = 	"PREFIX dbo: <http://dbpedia.org/ontology/>" + "\n"; // +
									// "PREFIX : <http://dbpedia.org/resource/>" + "\n";
	
	private static String dbpedia = "http://dbpedia.org/resource/";
	
	private static ThreadLocal<ImprovedRecallResult[]> local_ImprovedResults = new ThreadLocal<ImprovedRecallResult[]>();
	
	public ImprovedRecallResult[] getImprovedResults(){
		ImprovedRecallResult[] improvedResults = local_ImprovedResults.get();
		return improvedResults;
	}

	
	private static ThreadLocal<BestCombinationElement[]> local_CombinationResults = new ThreadLocal<BestCombinationElement[]>();
	
	public BestCombinationElement[] getCombinationResults(){
		BestCombinationElement[] results = local_CombinationResults.get();
		return results;
	}

	private static ThreadLocal<InputGraphModel> local_InputGraph = new ThreadLocal<InputGraphModel>();
	
	public InputGraphModel getGraph(){
		InputGraphModel graph = local_InputGraph.get();
		if (graph == null){
			graph = new InputGraphModel();
			local_InputGraph.set(graph);
		}
		return graph;
	}
	
	private static ThreadLocal<OutputGraphModel[]> local_OutputGraphs = new ThreadLocal<OutputGraphModel[]>();
	
	public OutputGraphModel[] getOutputGraphs(){
		OutputGraphModel[] graphs = local_OutputGraphs.get();
		return graphs;
	}


	private static ThreadLocal<InputNode[]> local_NodesInOrder = new ThreadLocal<InputNode[]>();

	public void newNodesInOrder(int size){
		InputNode[] nodesInOrder = new InputNode[size];
		local_NodesInOrder.set(nodesInOrder);
	}
	
	public InputNode[] getNodesInOrder(){
		InputNode[] nodesInOrder = local_NodesInOrder.get();
		return nodesInOrder;
	}

	
	private static ThreadLocal<InputLink[]> local_LinksInOrder = new ThreadLocal<InputLink[]>();

	public void newLinksInOrder(int size){
		InputLink[] linksInOrder = new InputLink[size];
		local_LinksInOrder.set(linksInOrder);
	}
	
	public InputLink[] getLinksInOrder(){
		InputLink[] linksInOrder = local_LinksInOrder.get();
		return linksInOrder;
	}

	
	private static ThreadLocal<HashMap<String, NounCompound>> local_concepts = new ThreadLocal<HashMap<String, NounCompound>>();
	
	public HashMap<String, NounCompound> getConcepts(){
		HashMap<String, NounCompound> concepts = local_concepts.get();
		if (concepts == null){
			concepts = new HashMap<String, NounCompound>();
			local_concepts.set(concepts);
		}
		return concepts;
	}

	
	private static ThreadLocal<HashMap<String, ComplexPredicate>> local_relations = new ThreadLocal<HashMap<String, ComplexPredicate>>();
	
	public HashMap<String, ComplexPredicate> getRelations(){
		HashMap<String, ComplexPredicate> relations = local_relations.get();
		if (relations == null){
			relations = new HashMap<String, ComplexPredicate>();
			local_relations.set(relations);
		}
		return relations;
	}
	

	private static ThreadLocal<HashMap<String, Double>> local_termSimPool = new ThreadLocal<HashMap<String, Double>>();
	
	public HashMap<String, Double> getTermSimPool(){
		HashMap<String, Double> simPool = local_termSimPool.get();
		if (simPool == null){
			simPool = new HashMap<String, Double>();
			local_termSimPool.set(simPool);
		}
		return simPool;
	}

	/*
	private static ThreadLocal<HashMap<String, Double>> local_pathSimPool = new ThreadLocal<HashMap<String, Double>>();
	
	public HashMap<String, Double> getPathSimPool(){
		HashMap<String, Double> simPool = local_pathSimPool.get();
		if (simPool == null){
			simPool = new HashMap<String, Double>();
			local_pathSimPool.set(simPool);
		}
		return simPool;
	}
	*/

	
	
	SimilarityModel sim_model;
	public CorrelationModel correlation_model;
	public int NumberOfClassCandidates = 10;
	public int NumberOfPropertyCandidates = 20;
	public double Concept_Weight = 2.0;
	
	public int NumberOfBestCombinations = 40; // 10;
	public int NumberOfOutputGraphs = 10; 
	public double multiplicative_alpha = 0.25;
	public double universe_offset = 0;
	public double uneven_match_parameter = 1.0;
	
	public int PathFilter_MaxChoices = 3;
	public double PathFilter_Threshold = 0.20;
	
	public boolean batchMode = false;


	public HashMap<String, String> foafTermMapping;

	
	
	public GraphMapping(String path) throws Exception {
		// TODO Auto-generated constructor stub
		
		foafTermMapping = new HashMap<String, String>();
		
		File foafFile = new File(path + "foaf_term.txt");
		BufferedReader foafReader = new BufferedReader(new FileReader(foafFile));
		
		String rdline;
		while((rdline = foafReader.readLine()) != null){

			foafTermMapping.put(rdline, "foaf:" + rdline);
		}

		foafReader.close();

		sim_model = new SimilarityModel(path);
		
	}	
		
	public void setAllParameters(int numberOfClassCandidates, int numberOfPropertyCandidates, int numberOfClassCombinations, double multiplicative_alpha, double universe_offset,
			int numberOfOutputGraphs, double concept_weight, int pathFilter_maxChoices, double pathFilter_threshold, double transfer_rate_lamda, double uneven_match_offset) throws Exception{
		
		this.NumberOfClassCandidates = numberOfClassCandidates;
		this.NumberOfPropertyCandidates = numberOfPropertyCandidates;
		this.NumberOfBestCombinations = numberOfClassCombinations;
		this.multiplicative_alpha = multiplicative_alpha;
		this.universe_offset = universe_offset;
		this.NumberOfOutputGraphs = numberOfOutputGraphs;
		this.Concept_Weight = concept_weight;
		this.PathFilter_MaxChoices = pathFilter_maxChoices;
		this.PathFilter_Threshold = pathFilter_threshold;
		this.correlation_model = CorrelationModel.readModel(Configure.work_dir + "latest_triples_NoConstraint_lambda" + transfer_rate_lamda);
		this.uneven_match_parameter = uneven_match_offset;
		
	}

	
	public void setupInput() throws Exception{

		//getGraph().addOneRelation("?x/Author, writes, ?y/Book");
		//getGraph().addOneRelation("?x/Author, , ?z/Institution");

	
		/*
		nodesInOrder = new Node[graph.nodes.size()];
		graph.nodes.values().toArray(nodesInOrder);
		*/
		
		if (!batchMode)
			System.out.println("Starting computation ...");
		
		ArrayList<InputNode> realNodes = new ArrayList<InputNode>();
		
		for (InputNode node: getGraph().nodes.values()){
			if (node.type != null && !node.type.equalsIgnoreCase("null"))
				realNodes.add(node);
		}

		ArrayList<InputNode> validNodes = new ArrayList<InputNode>();
		
		newLinksInOrder(getGraph().relations.size());
		
		getGraph().relations.toArray(getLinksInOrder());
		
		InputLink[] linksInOrder = getLinksInOrder();
		
		/*
		for(Node node : realNodes){
			sim_model.simBox.addNounCompound(new NounCompound(node.type, sim_model.tagger, sim_model.morph, sim_model.simBox));
		}
		
		for(Link link : linksInOrder){
			
			if (!link.predicate.equals("")){
				sim_model.simBox.addComplexPredicate(new ComplexPredicate(link.predicate, sim_model.tagger, sim_model.morph, sim_model.simBox));
			}
		}
		*/
		
		for(InputNode node : realNodes){
			
			boolean allowVirtual = true;
			
			node.choices.addAll(Arrays.asList(sim_model.getSimilarConcepts(node.type, NumberOfClassCandidates, allowVirtual, getConcepts())));
			//node.choices.addAll(Arrays.asList(sim_model.getSimilarConcepts(node.type, 20, allowVirtual)));
			//node.choices.addAll(Arrays.asList(sim_model.getSimilarConcepts(node.type, 20, true)));
			
			if (node.choices.size() > 0)
				validNodes.add(node);
		}

		for(InputLink link : linksInOrder){
			
			//if (link.predicate.equalsIgnoreCase("location") || link.predicate.startsWith("located"))
			//	link.predicate = "in";
			
			ComplexPredicate predicate = sim_model.convertToComplexPredicate(link.predicate);
			
			getRelations().put(link.predicate, predicate);
			
			if (predicate.lengthInContentWords != 0){
				link.choices.addAll(Arrays.asList(sim_model.getSimilarProperties(link.predicate, NumberOfPropertyCandidates, getRelations())));
			}else{
				
				ChoiceElement[] fromSubject = sim_model.getSimilarProperties(link.subject.type, NumberOfPropertyCandidates * 3 / 4, getRelations());
				for (ChoiceElement choice: fromSubject){
					choice.from = "<";
				}
				
				ChoiceElement[] fromObject = sim_model.getSimilarProperties(link.object.type, NumberOfPropertyCandidates * 3 / 4, getRelations());
				for (ChoiceElement choice: fromObject){
					choice.from = ">";
				}
					
				if (validNodes.contains(link.subject) && validNodes.contains(link.object)){
					link.choices.addAll(Arrays.asList(fromSubject));
					link.choices.addAll(Arrays.asList(fromObject));
				}else if (!validNodes.contains(link.object))
					link.choices.addAll(Arrays.asList(fromObject));
				else if (!validNodes.contains(link.subject))
					link.choices.addAll(Arrays.asList(fromSubject));
					
				//if (link.predicate.equalsIgnoreCase("in"))
				//link.choices.addAll(Arrays.asList(sim_model.getSimilarProperties("location", 10)));
				//link.choices.addAll(Arrays.asList(sim_model.getSimilarProperties("located", 5)));
				//link.choices.addAll(Arrays.asList(sim_model.getSimilarProperties("owned", 5)));
			}
		}
		
		newNodesInOrder(validNodes.size());
		validNodes.toArray(getNodesInOrder());
		
		populateSimPool(getConcepts(), getRelations(), getTermSimPool());

	}

	
	public void populateSimPool(HashMap<String, NounCompound> concepts, HashMap<String, ComplexPredicate> relations, HashMap<String, Double> simPool){
		
		TreeMap<String, CompoundElement> userTerms = new TreeMap<String, CompoundElement>();
		TreeMap<String, CompoundElement> ontoTerms = new TreeMap<String, CompoundElement>();
		
		for (Entry<String, NounCompound> entry: concepts.entrySet()){
			userTerms.put(entry.getKey(), new CompoundElement(entry.getValue().componentWords, entry.getValue().head_loc));
		}
		
		for (Entry<String, ComplexPredicate> entry: relations.entrySet()){
			userTerms.put(entry.getKey(), new CompoundElement(entry.getValue().componentWords, entry.getValue().head_loc));
		}
		
		for (Entry<String, NounCompound> entry: sim_model.classes.entrySet()){
			ontoTerms.put(entry.getKey(), new CompoundElement(entry.getValue().componentWords, entry.getValue().head_loc));
		}
		
		for (Entry<String, ComplexPredicate> entry: sim_model.properties.entrySet()){
			ontoTerms.put(entry.getKey(), new CompoundElement(entry.getValue().componentWords, entry.getValue().head_loc));
		}
		
		for (Entry<String, CompoundElement> userEntry :userTerms.entrySet()){
			
			for (Entry<String, CompoundElement> ontoEntry :ontoTerms.entrySet()){
		
				double sim = PathFitnessElement.getSimilarity(userEntry.getValue().components, userEntry.getKey(), userEntry.getValue().head_loc, 
						ontoEntry.getValue().components, ontoEntry.getKey(), ontoEntry.getValue().head_loc, sim_model.relationSimBox, sim_model.conceptSimBox);
				
				simPool.put(userEntry.getKey() + "|" + ontoEntry.getKey(), sim);
			}
		}
	}
	

	public void optimize() throws Exception{
		
		if (!batchMode)
			System.out.println(getGraph());
		
		InputNode[] nodesInOrder = getNodesInOrder();
		InputLink[] linksInOrder = getLinksInOrder();
		PriorityQueue<BestCombinationElement> bestCombinations = new PriorityQueue<BestCombinationElement>(NumberOfBestCombinations);
		int[] bestNodeChoiceInOrder;
		int[] bestLinkChoiceInOrder;
		String[] bestLinkChoiceSubjectDirectionInOrder;
		String[] bestLinkChoiceObjectDirectionInOrder;
		//double DirectionThreshold = 0; // 0.5 * 0.5 * 4;

		int[] preNodeChoiceInOrder = new int[nodesInOrder.length];
		int[] curNodeChoiceInOrder = new int[nodesInOrder.length];

		int MAX_PLACE;
		
		if (nodesInOrder.length == 1){
		
			MAX_PLACE = 0;
			preNodeChoiceInOrder[0] = -1;
		
		}else{
		
			MAX_PLACE = 1;
			
			for (int i = 0; i < nodesInOrder.length; i++){
				MAX_PLACE *= nodesInOrder[i].choices.size();
				preNodeChoiceInOrder[i] = -1;
			}
			
			MAX_PLACE --;
		}	
		

		int places;
		
		for (places = 0; places <= MAX_PLACE; places++){
			
			for (InputLink link : linksInOrder){
				link.temp_hasBeenComputed = false;     // has not been computed in THIS TURN;
			}

			//decode(places);
			int dividend = places;
			
			for (int i = nodesInOrder.length - 1; i >= 0; i--){
				
				int divisor = nodesInOrder[i].choices.size();
				
				curNodeChoiceInOrder[i] = dividend % divisor; 
				dividend = dividend / divisor; 
				
				nodesInOrder[i].temp_index = curNodeChoiceInOrder[i];
			}

			
			for (int i = 0; i < nodesInOrder.length; i++){
				
				if (preNodeChoiceInOrder[i] != curNodeChoiceInOrder[i]){
					
					for (InputLink link : nodesInOrder[i].outgoingLinks){
						
						if (!link.temp_hasBeenComputed){
							
							link.reset();
							
							
							for (int j = 0; j < link.choices.size(); j++){
								
								/*
								if (link.predicate == null || link.predicate.equals("") || ComplexPredicate.stopwords.contains(link.predicate.trim().toLowerCase())){
									
									double subjectVirutalFactor = 1;
									double objectVirutalFactor = 1;
									
									if (link.subject.choices.get(link.subject.temp_index).term.contains("^"))
										subjectVirutalFactor = 1 / NounCompound.virtualProportion;

									if (link.object.choices.get(link.object.temp_index).term.contains("^"))
										objectVirutalFactor = 1 / NounCompound.virtualProportion;
									
									
									if (link.subject.choices.get(link.subject.temp_index).sim * subjectVirutalFactor - link.object.choices.get(link.object.temp_index).sim * objectVirutalFactor > Default_Preference_Threshold_Via_Delta_Sim
											&& !link.subject.type.contains(" ") && !link.object.type.contains(" ")){
										if (link.choices.get(j).from.equals("<") && !link.choices.get(j).term.startsWith("@"))
											continue;
									}

									if (link.object.choices.get(link.object.temp_index).sim * objectVirutalFactor - link.subject.choices.get(link.subject.temp_index).sim * subjectVirutalFactor > Default_Preference_Threshold_Via_Delta_Sim
											&& !link.subject.type.contains(" ") && !link.object.type.contains(" ")){
										if (link.choices.get(j).from.equals(">") && !link.choices.get(j).term.startsWith("@"))
											continue;
									}
								}
								*/

								for (int k = 0; k < 4; k++){
									
									String sub_direction = "";
									String obj_direction = "";
									
									if (k==0){
										sub_direction = "<";
										obj_direction = "<";
									}else if (k==1){
										sub_direction = "<";
										obj_direction = ">";
									}else if (k==2){
										sub_direction = ">";
										obj_direction = "<";
									}else if (k==3){
										sub_direction = ">";
										obj_direction = ">";
									}
								
									
									double outPMI = 0;
									double outFit = 0;
									if (link.subject.choices.size() > 0 && !sub_direction.equals(obj_direction)){
										outPMI = correlation_model.getConceptRelationPMI(sub_direction + link.subject.choices.get(link.subject.temp_index).term, link.choices.get(j).term);
										if (outPMI >= 0)
											outFit = outPMI * link.subject.choices.get(link.subject.temp_index).sim * link.choices.get(j).sim;
										else
											outFit = outPMI / (link.subject.choices.get(link.subject.temp_index).sim * link.choices.get(j).sim);
									}
										
									double inPMI = 0;
									double inFit = 0;
									if (link.object.choices.size() > 0 && !sub_direction.equals(obj_direction)){
										inPMI = correlation_model.getConceptRelationPMI(obj_direction + link.object.choices.get(link.object.temp_index).term, link.choices.get(j).term);
										if (inPMI >= 0)
											inFit = inPMI * link.object.choices.get(link.object.temp_index).sim * link.choices.get(j).sim;
										else
											inFit = inPMI / (link.object.choices.get(link.object.temp_index).sim * link.choices.get(j).sim);
									}
										
									double total = outFit + inFit;
									
									if (total < 0) total = 0;
									double directTotal = total;
									double indirectTotal = 0;
									
									
									if (link.object.temp_index != -1){
										
										double directConceptsPMI = correlation_model.getDirectConceptPMI(sub_direction + link.subject.choices.get(link.subject.temp_index).term, obj_direction + link.object.choices.get(link.object.temp_index).term);
										
										if (directConceptsPMI >= 0)
											directTotal += Concept_Weight * directConceptsPMI * link.subject.choices.get(link.subject.temp_index).sim * link.object.choices.get(link.object.temp_index).sim;
										else
											directTotal += Concept_Weight * directConceptsPMI / (link.subject.choices.get(link.subject.temp_index).sim * link.object.choices.get(link.object.temp_index).sim);

										
										double indirectConceptsPMI = correlation_model.getIndirectConceptPMI(sub_direction + link.subject.choices.get(link.subject.temp_index).term, obj_direction + link.object.choices.get(link.object.temp_index).term);
										
										if (indirectConceptsPMI >= 0)
											indirectTotal = Concept_Weight * indirectConceptsPMI * link.subject.choices.get(link.subject.temp_index).sim * link.object.choices.get(link.object.temp_index).sim;
										else
											indirectTotal = Concept_Weight * indirectConceptsPMI / (link.subject.choices.get(link.subject.temp_index).sim * link.object.choices.get(link.object.temp_index).sim);
										
										if (indirectTotal > directTotal)
											total = indirectTotal;
										else
											total = directTotal;
										
									}

									
									boolean checkFlag = true;
									//if (outPMI < 0 || inPMI < 0)
									//	checkFlag = false;
									
									if (total > link.temp_fitness && checkFlag){
										link.temp_fitness = total;
										
										if (outFit > 0 && inFit > 0 && total == directTotal)
											link.temp_index = j;
										else
											link.temp_index = -1;
										
										link.sub_direction = sub_direction;
										link.obj_direction = obj_direction;
									}
								
								} // end of k loop
								
							} // end of j loop
							
							
							link.temp_hasBeenComputed = true;
						}
					}
					
					for (InputLink link : nodesInOrder[i].incomingLinks){
						
						if (!link.temp_hasBeenComputed){
						
							link.reset();
							
							
							for (int j = 0; j < link.choices.size(); j++){
								
								/*
								if (link.predicate == null || link.predicate.equals("") || ComplexPredicate.stopwords.contains(link.predicate.trim().toLowerCase())){
									
									double subjectVirutalFactor = 1;
									double objectVirutalFactor = 1;
									
									if (link.subject.choices.get(link.subject.temp_index).term.contains("^"))
										subjectVirutalFactor = 1 / NounCompound.virtualProportion;

									if (link.object.choices.get(link.object.temp_index).term.contains("^"))
										objectVirutalFactor = 1 / NounCompound.virtualProportion;

									
									if (link.subject.choices.get(link.subject.temp_index).sim * subjectVirutalFactor - link.object.choices.get(link.object.temp_index).sim * objectVirutalFactor > Default_Preference_Threshold_Via_Delta_Sim
											&& !link.subject.type.contains(" ") && !link.object.type.contains(" ")){
										if (link.choices.get(j).from.equals("<") && !link.choices.get(j).term.startsWith("@")) 
											continue;
									}

									if (link.object.choices.get(link.object.temp_index).sim * objectVirutalFactor - link.subject.choices.get(link.subject.temp_index).sim * subjectVirutalFactor > Default_Preference_Threshold_Via_Delta_Sim
											&& !link.subject.type.contains(" ") && !link.object.type.contains(" ")){
										if (link.choices.get(j).from.equals(">") && !link.choices.get(j).term.startsWith("@"))
											continue;
									}
								}
								*/
								
								
								for (int k = 0; k < 4; k++){
									
									String sub_direction = "";
									String obj_direction = "";
									
									if (k==0){
										sub_direction = "<";
										obj_direction = "<";
									}else if (k==1){
										sub_direction = "<";
										obj_direction = ">";
									}else if (k==2){
										sub_direction = ">";
										obj_direction = "<";
									}else if (k==3){
										sub_direction = ">";
										obj_direction = ">";
									}
								
									double outPMI = 0;
									double outFit = 0;
									if (link.subject.choices.size() > 0 && !sub_direction.equals(obj_direction)){
										outPMI = correlation_model.getConceptRelationPMI(sub_direction + link.subject.choices.get(link.subject.temp_index).term, link.choices.get(j).term);
										if (outPMI >= 0)
											outFit = outPMI * link.subject.choices.get(link.subject.temp_index).sim * link.choices.get(j).sim;
										else
											outFit = outPMI / (link.subject.choices.get(link.subject.temp_index).sim * link.choices.get(j).sim);
									}
										
									double inPMI = 0;
									double inFit = 0;
									if (link.object.choices.size() > 0 && !sub_direction.equals(obj_direction)){
										inPMI = correlation_model.getConceptRelationPMI(obj_direction + link.object.choices.get(link.object.temp_index).term, link.choices.get(j).term);
										if (inPMI >= 0)
											inFit = inPMI * link.object.choices.get(link.object.temp_index).sim * link.choices.get(j).sim;
										else
											inFit = inPMI / (link.object.choices.get(link.object.temp_index).sim * link.choices.get(j).sim);
									}
									
									double total= outFit + inFit;
									
									if (total < 0) total = 0;
									double directTotal = total;
									double indirectTotal = 0;

									if (link.object.temp_index != -1){
										
										double directConceptsPMI = correlation_model.getDirectConceptPMI(sub_direction + link.subject.choices.get(link.subject.temp_index).term, obj_direction + link.object.choices.get(link.object.temp_index).term);
										
										if (directConceptsPMI >= 0)
											directTotal += Concept_Weight * directConceptsPMI * link.subject.choices.get(link.subject.temp_index).sim * link.object.choices.get(link.object.temp_index).sim;
										else
											directTotal += Concept_Weight * directConceptsPMI / (link.subject.choices.get(link.subject.temp_index).sim * link.object.choices.get(link.object.temp_index).sim);

										
										double indirectConceptsPMI = correlation_model.getIndirectConceptPMI(sub_direction + link.subject.choices.get(link.subject.temp_index).term, obj_direction + link.object.choices.get(link.object.temp_index).term);
										
										if (indirectConceptsPMI >= 0)
											indirectTotal = Concept_Weight * indirectConceptsPMI * link.subject.choices.get(link.subject.temp_index).sim * link.object.choices.get(link.object.temp_index).sim;
										else
											indirectTotal = Concept_Weight * indirectConceptsPMI / (link.subject.choices.get(link.subject.temp_index).sim * link.object.choices.get(link.object.temp_index).sim);
										
										if (indirectTotal > directTotal)
											total = indirectTotal;
										else
											total = directTotal;
										
									}	
									
									
									boolean checkFlag = true;
									//if (outPMI < 0 || inPMI < 0)
									//	checkFlag = false;
									
									if (total > link.temp_fitness && checkFlag){
										link.temp_fitness = total;
										
										if (outFit > 0 && inFit > 0 && total == directTotal)
											link.temp_index = j;
										else
											link.temp_index = -1;
										
										link.sub_direction = sub_direction;
										link.obj_direction = obj_direction;
									}
									
								} // end of k loop
								
							} // end of j loop
							
								
							link.temp_hasBeenComputed = true;
						}
					}
				}
			}
			
			double curTotalFitness = 0;
			
			for (InputLink link: linksInOrder){
				curTotalFitness += link.temp_fitness;
			}
			
			
			if (curTotalFitness > 0){
				
				if (bestCombinations.size() < NumberOfBestCombinations){
					
					bestNodeChoiceInOrder = new int[nodesInOrder.length];
					bestLinkChoiceInOrder = new int[linksInOrder.length];
					bestLinkChoiceSubjectDirectionInOrder = new String[linksInOrder.length];
					bestLinkChoiceObjectDirectionInOrder = new String[linksInOrder.length];
					
					for (int i = 0; i < nodesInOrder.length; i++){
						bestNodeChoiceInOrder[i] = nodesInOrder[i].temp_index;
					}
					
					for (int i = 0; i < linksInOrder.length; i++){
						bestLinkChoiceInOrder[i] = linksInOrder[i].temp_index;
						bestLinkChoiceSubjectDirectionInOrder[i] = linksInOrder[i].sub_direction;
						bestLinkChoiceObjectDirectionInOrder[i] = linksInOrder[i].obj_direction;
					}
					
					bestCombinations.add(new BestCombinationElement(curTotalFitness, bestNodeChoiceInOrder, bestLinkChoiceInOrder, bestLinkChoiceSubjectDirectionInOrder, bestLinkChoiceObjectDirectionInOrder));
				
				}else{
					if (bestCombinations.peek().bestTotalFitness < curTotalFitness){
						
						bestNodeChoiceInOrder = new int[nodesInOrder.length];
						bestLinkChoiceInOrder = new int[linksInOrder.length];
						bestLinkChoiceSubjectDirectionInOrder = new String[linksInOrder.length];
						bestLinkChoiceObjectDirectionInOrder = new String[linksInOrder.length];
						
						for (int i = 0; i < nodesInOrder.length; i++){
							bestNodeChoiceInOrder[i] = nodesInOrder[i].temp_index;
						}
						
						for (int i = 0; i < linksInOrder.length; i++){
							bestLinkChoiceInOrder[i] = linksInOrder[i].temp_index;
							bestLinkChoiceSubjectDirectionInOrder[i] = linksInOrder[i].sub_direction;
							bestLinkChoiceObjectDirectionInOrder[i] = linksInOrder[i].obj_direction;
						}
						
						bestCombinations.remove();
						bestCombinations.add(new BestCombinationElement(curTotalFitness, bestNodeChoiceInOrder, bestLinkChoiceInOrder, bestLinkChoiceSubjectDirectionInOrder, bestLinkChoiceObjectDirectionInOrder));
					}
				}
			
			}else if (nodesInOrder.length == 1){
				
				int size = nodesInOrder[0].choices.size();
				
				if (NumberOfBestCombinations < size)
					size = NumberOfBestCombinations;
				
				for (int i = 0; i < size; i++){
					
					bestNodeChoiceInOrder = new int[nodesInOrder.length];
					bestLinkChoiceInOrder = new int[linksInOrder.length];
					bestLinkChoiceSubjectDirectionInOrder = new String[linksInOrder.length];
					bestLinkChoiceObjectDirectionInOrder = new String[linksInOrder.length];
					
					bestNodeChoiceInOrder[0] = i;
	
					bestCombinations.add(new BestCombinationElement(curTotalFitness, bestNodeChoiceInOrder, bestLinkChoiceInOrder, bestLinkChoiceSubjectDirectionInOrder, bestLinkChoiceObjectDirectionInOrder));
				}
			}
			
			

			for (int i = 0; i < nodesInOrder.length; i++){
				preNodeChoiceInOrder[i] = curNodeChoiceInOrder[i];
			}
			
		}
		
		BestCombinationElement[] results = new BestCombinationElement[bestCombinations.size()];
		local_CombinationResults.set(results);

		int j = 1;
		while (bestCombinations.size() > 0){
			results[results.length - j] = bestCombinations.poll();
			j++;
		}

	}
	
	/*
	public void decode(int orderInNumber){
		
		int dividend = orderInNumber;
		Node[] nodesInOrder = getNodesInOrder();
		
		for (int i = nodesInOrder.length - 1; i >= 0; i--){
			
			int divisor = nodesInOrder[i].choices.size();
			
			curNodeChoiceInOrder[i] = dividend % divisor; 
			dividend = dividend / divisor; 
			
			nodesInOrder[i].temp_index = curNodeChoiceInOrder[i];
		}
	}
	*/

	
	public void printOptimizedResult(){
		
		InputNode[] nodesInOrder = getNodesInOrder();
		InputLink[] linksInOrder = getLinksInOrder();
		BestCombinationElement[] results = getCombinationResults();
		
		for (int k = 0; k < results.length; k++){
		
			System.out.println("The " + (k+1) + "th highest fitness value is: " + results[k].bestTotalFitness / results[k].numberOfLinks);
			
			for (int i = 0; i < nodesInOrder.length; i++){
				nodesInOrder[i].temp_index = results[k].bestNodeChoiceInOrder[i];
			}
			
			
			for (int i = 0; i < linksInOrder.length; i++){
				linksInOrder[i].temp_index= results[k].bestLinkChoiceInOrder[i];
				linksInOrder[i].sub_direction = results[k].bestLinkChoiceSubjectDirectionInOrder[i]; 
				linksInOrder[i].obj_direction = results[k].bestLinkChoiceObjectDirectionInOrder[i]; 
				
			}
			
			System.out.println(getGraph());
			System.out.println();
			
		}

	}

	public Vector<String> reverse(Vector<String> pathList){
		
		Vector<String> newList = new Vector<String>();
		
		String preDirection= "";
		
		for (int i = pathList.size() - 1; i >= 0; i--){
		
			String node = pathList.get(i);
			
			if (i > 0)
				newList.add(preDirection + node.substring(1, node.length()));
			else
				newList.add(preDirection + node);
			
			if (node.substring(0, 1).equals("<"))
				preDirection = ">";
			else
				preDirection = "<";
		}
		
		return newList;
	}
	
	public void resolvePath() throws Exception{
		
		InputNode[] nodesInOrder = getNodesInOrder();
		InputLink[] linksInOrder = getLinksInOrder();
		BestCombinationElement[] combinationResults = getCombinationResults();
		ArrayList<OutputGraphModel> outputGraphModels = new ArrayList<OutputGraphModel>();
		HashMap<String, ArrayList<PathFitnessElement>> pathMappingCache = new HashMap<String, ArrayList<PathFitnessElement>>();
		
		for (int k = 0; k < combinationResults.length; k++){
		
			for (int i = 0; i < nodesInOrder.length; i++){
				nodesInOrder[i].temp_index = combinationResults[k].bestNodeChoiceInOrder[i];
			}
			
			for (int i = 0; i < linksInOrder.length; i++){
				linksInOrder[i].temp_index= combinationResults[k].bestLinkChoiceInOrder[i];
				linksInOrder[i].sub_direction = combinationResults[k].bestLinkChoiceSubjectDirectionInOrder[i]; 
				linksInOrder[i].obj_direction = combinationResults[k].bestLinkChoiceObjectDirectionInOrder[i]; 
			}

			NodeMapElement[] node_mappings = new NodeMapElement[nodesInOrder.length];
			ArrayList<PathFitnessElement>[] link_mappings = new ArrayList[linksInOrder.length];
			
			int numberOfOutputGraphModels = 1;
			
			for (int t = 0; t < nodesInOrder.length; t++){
				
				InputNode inputnode = nodesInOrder[t];
				NodeMapElement nodeMap = new NodeMapElement(inputnode.id, inputnode.type, inputnode.isBlankNode, inputnode.isVariable, inputnode.getChoice().term, inputnode.getChoice().sim);
				node_mappings[t] = nodeMap;
			}


			for (int t = 0; t < linksInOrder.length; t++){
				
				InputLink inputlink = linksInOrder[t];
				
				//if (inputlink.hasAttributeNode())
				//	continue;
				
				/*
				String subject = inputlink.sub_direction + inputlink.subject.getChoice().term;
				String object = inputlink.obj_direction + inputlink.object.getChoice().term;
				HashMap<String, Double> paths = correlation_model.getIndirectConceptCoOccurrencePath(subject, object);
				*/
				
				String subject = inputlink.subject.getChoice().term;
				String object = inputlink.object.getChoice().term;
				
				String pathMappingId = inputlink.toString() + ":" + subject + ":" + object;
				
				ArrayList<PathFitnessElement> filteredpathElements = pathMappingCache.get(pathMappingId);
				
				if (filteredpathElements == null){
				
					HashMap<String, Double> paths = new HashMap<String, Double>();
					HashMap<String, Double> collection;
					
					collection = correlation_model.getIndirectConceptCoOccurrencePath("<" + subject, "<" + object);
					if (collection != null)
						paths.putAll(collection);
	
					collection = correlation_model.getIndirectConceptCoOccurrencePath("<" + subject, ">" + object);
					if (collection != null)
						paths.putAll(collection);
					
					collection = correlation_model.getIndirectConceptCoOccurrencePath(">" + subject, "<" + object);
					if (collection != null)
						paths.putAll(collection);
					
					collection = correlation_model.getIndirectConceptCoOccurrencePath(">" + subject, ">" + object);
					if (collection != null)
						paths.putAll(collection);
				
					ArrayList<PathFitnessElement> pathElements = new ArrayList<PathFitnessElement>();
					
					//HashSet<String> pathsEncountered = new HashSet<String>();
					HashMap<String, Double> pathSimPool = new HashMap<String, Double>(1024);
					
					for (Entry<String, Double> entry: paths.entrySet()){
						
						
						String path = entry.getKey();
						Vector<String> pathList = new Vector<String>();
						
						
						//StringTokenizer st = new StringTokenizer(path, " ");
						StringTokenizer st = new StringTokenizer(path, ",");
						
						while (st.hasMoreElements()){
							
							String node = st.nextToken();
							
							//if (node.endsWith(","))
							//	node = node.substring(0, node.length() - 1);
	
							if (node.startsWith(" "))
								node = node.substring(1, node.length());
	
							pathList.add(node);
							
						}
						
						if (!pathList.elementAt(0).equals(inputlink.subject.getChoice().term)){
	
							if (pathList.elementAt(0).equals("Thing")){
								pathList = reverse(pathList);
							}else
								continue;
						}
						
						/*
						if (!pathList.elementAt(0).equals(inputlink.subject.getChoice().term)){
							pathList = reverse(pathList);
						}
	
						if (!pathsEncountered.contains(pathList.toString()))
							pathsEncountered.add(pathList.toString());
						else
							continue;
						*/
	
						String[] classLabels = new String[pathList.size()];
						int[] propertyDirections = new int[pathList.size() - 1];
						
						int[] dimensions = new int[pathList.size() - 1];
						String[][] propertyLabelArrays = new String[pathList.size() - 1][];
						int[][] propertyFrequencyArrays = new int[pathList.size() - 1][];
						int[] totalFrequencyArrays = new int[pathList.size() - 1];
						
						int spaceSize = 1;
	
						for (int i = 1; i < pathList.size(); i++){
							
							String cur;
							
							if (i == 1)
								cur = pathList.elementAt(i - 1);
							else
								cur = pathList.elementAt(i - 1).substring(1, pathList.elementAt(i - 1).length());
							
							classLabels[i-1] = cur;
							
							String next = pathList.elementAt(i);
							
							if (next.startsWith("<")){
								cur = ">" + cur;
								propertyDirections[i-1] = 1;
							}else{
								cur = "<" + cur;
								propertyDirections[i-1] = 0;
							}	
							
							totalFrequencyArrays[i-1] = correlation_model.getDirectConceptCoOccurrence(cur, next); 
								
							TreeMap<String, Integer> relationFrequencies = correlation_model.getRelationsBetweenDirectConcepts(cur, next);
							
							dimensions[i-1] = relationFrequencies.size();
							spaceSize = spaceSize * dimensions[i-1];
							
							String[] propertyLabelArray = new String[dimensions[i-1]];
							int[] propertyFrequencyArray = new int[dimensions[i-1]];
							
							int j = 0;
							for (Entry<String, Integer> relation: relationFrequencies.entrySet()){
								propertyLabelArray[j] = relation.getKey();
								propertyFrequencyArray[j] = relation.getValue();
								j++;
							}
							
							propertyLabelArrays[i-1] = propertyLabelArray;
							propertyFrequencyArrays[i-1] = propertyFrequencyArray;
							
						}
						
						String label = pathList.elementAt(pathList.size() - 1);
						classLabels[pathList.size() - 1] = label.substring(1, label.length());
						
						
						int[] indexes = new int[pathList.size() - 1];
						
						for (int places = 0; places < spaceSize; places ++){
							
							int dividend = places;
							boolean isTrivial = false;
							
							for (int i = dimensions.length - 1; i >= 0; i--){
								
								int divisor = dimensions[i];
								
								indexes[i] = dividend % divisor; 
								dividend = dividend / divisor; 
								
							}
						
							String[] propertyLabels = new String[pathList.size() - 1];
							double[] propertyProbabilities = new double[pathList.size() - 1];
							
						    for (int i=0; i < indexes.length; i++){
						    	
						    	if (propertyFrequencyArrays[i][indexes[i]] < Configure.PropertyFrequencyThreshold){
						    		isTrivial = true;
						    		break;
						    	}

						    	propertyLabels[i]  = propertyLabelArrays[i][indexes[i]];
						    	propertyProbabilities[i]  = (double) propertyFrequencyArrays[i][indexes[i]] / totalFrequencyArrays[i];
						    }
						    
						    if (isTrivial)
						    	continue;
						    
							PathFitnessElement pathElement = new PathFitnessElement(sim_model, pathList.size() - 1, entry.getValue(), inputlink.predicate, 
									inputlink.subject.type, inputlink.subject.getChoice().sim, inputlink.object.type, inputlink.object.getChoice().sim, inputlink.object.isAttributeNode(), 
									classLabels, propertyLabels, propertyProbabilities, propertyDirections, multiplicative_alpha, universe_offset, uneven_match_parameter);
							
							//pathElement.computeFitness();
							//pathElement.computeFitness_split_concept_label();
							//pathElement.computeFitness_split_conceptLabel_propertyOnly_combineEnds_weights_singleStrongConceptJump();
							pathElement.computeFitness_split_conceptLabel_propertyOnly_combineEnds_weights_singleStrongConceptJump_defaultRelation(getConcepts(), getRelations(), getTermSimPool(), pathSimPool);
							
							pathElements.add(pathElement);
	
						}
					
					}
					
					Collections.sort(pathElements);
					
					filteredpathElements = filter(pathElements);
					
					pathMappingCache.put(pathMappingId, filteredpathElements);
					
				}
				
				if (filteredpathElements.size() > 0){
					link_mappings[t] = filteredpathElements;
				}
				
				numberOfOutputGraphModels *= filteredpathElements.size();

			}
			
			//--------------------------------------------------
			// start creating the output graph models
			//--------------------------------------------------
			
			
			int[] indexes = new int[link_mappings.length];
			
			for (int places = 0; places < numberOfOutputGraphModels; places ++){
				
				OutputGraphModel outputGraph = new OutputGraphModel(nodesInOrder.length, linksInOrder.length, foafTermMapping);
				
				int dividend = places;
				
				for (int i = link_mappings.length - 1; i >= 0; i--){
					
					int divisor = link_mappings[i].size();
					
					indexes[i] = dividend % divisor; 
					dividend = dividend / divisor; 
					
				}

				outputGraph.linksInOrder = linksInOrder;
				outputGraph.nodesInOrder = nodesInOrder;
				
				for (int i = 0; i < link_mappings.length; i++){
					outputGraph.link_mapping.put(linksInOrder[i], link_mappings[i].get(indexes[i]));
					outputGraph.PathFitnessElementInOrder[i] = link_mappings[i].get(indexes[i]);
				}
				
				for (int i = 0; i < node_mappings.length; i++){
					outputGraph.node_mapping.put(nodesInOrder[i], node_mappings[i]);
					outputGraph.NodeMapElementInOrder[i] = node_mappings[i];
				}
				
				// produce the output graph from the mappings.
				//outputGraph.populateGraph(correlation_model);
				outputGraph.computeFitness();
				
				outputGraphModels.add(outputGraph);
			}	
			
		}

		Collections.sort(outputGraphModels);
		
		if (!batchMode)
			System.out.println(outputGraphModels.size());

		int size = NumberOfOutputGraphs;
		
		if (outputGraphModels.size() < size)
			size = outputGraphModels.size();
		
		OutputGraphModel[] results = new OutputGraphModel[size];
		local_OutputGraphs.set(results);
		
		for (int i = 0; i < outputGraphModels.size() && i < NumberOfOutputGraphs; i++){
			
			outputGraphModels.get(i).populateGraph(correlation_model);
			//System.out.println(outputGraphModels.get(i).display());
			//System.out.println(outputGraphModels.get(i).toSparqlQuery(0));
			//System.out.println(SparqlRunner.run(outputGraphModels.get(i).toSparqlQuery(0)));
			
			results[i] = outputGraphModels.get(i);

		}
		
	}

	
	ArrayList<PathFitnessElement> filter(ArrayList<PathFitnessElement> pathElements){
		
		
		ArrayList<PathFitnessElement> result = new ArrayList<PathFitnessElement>();
		
		LinkedHashMap<String, Double> usedProperties = new LinkedHashMap<String, Double>();
		HashSet<String> pathsEncountered = new HashSet<String>();
		
		int count = 0;
		
		for (int i = 0; i < pathElements.size() && count < PathFilter_MaxChoices; i++){
			
			if (i == 0){
				result.add(pathElements.get(i));
				count ++;
				
				for (String property: pathElements.get(i).propertyLabels){
					usedProperties.put(property, pathElements.get(i).pathSim);
				}
				
				String path_id = Arrays.toString(pathElements.get(i).propertyLabels) + Arrays.toString(pathElements.get(i).propertyDirections);
				
				pathsEncountered.add(path_id);
				
			}else if (pathElements.get(i).fitness > result.get(0).fitness * PathFilter_Threshold){
				
				boolean found = false;
				
				for (String property: pathElements.get(i).propertyLabels){
					
					for (Entry<String, Double> used: usedProperties.entrySet()){
						
						String usedTerm = used.getKey();
						
						if (property.length() > usedTerm.length() && property.endsWith(Character.toUpperCase(usedTerm.charAt(0)) + usedTerm.substring(1, usedTerm.length())) 
								&& pathElements.get(i).pathSim <= used.getValue()){
							found = true;
							break;
						}
					}
					
					if (found)
						break;
				}
				
				if (found) continue;
				
				
				String path_id = Arrays.toString(pathElements.get(i).propertyLabels) + Arrays.toString(pathElements.get(i).propertyDirections);

				if (!pathsEncountered.contains(path_id))
					pathsEncountered.add(path_id);
				else
					continue;
				
				result.add(pathElements.get(i));
				count ++;
				
				for (String property: pathElements.get(i).propertyLabels){
					
					Double savedSim = usedProperties.get(property);
					
					if (savedSim == null)
						usedProperties.put(property, pathElements.get(i).pathSim);
					else if (pathElements.get(i).pathSim > savedSim)
						usedProperties.put(property, pathElements.get(i).pathSim);
				}

			}
		}
		
		return result;
	}
	
	

	public void clear(){
		getGraph().clear();
		getConcepts().clear();
		getRelations().clear();
		getTermSimPool().clear();

		//sim_model.clear();
	}
	
	public String getInterpretationFromOptimalResult(BestCombinationElement result){
		
		InputNode[] nodesInOrder = getNodesInOrder();
		InputLink[] linksInOrder = getLinksInOrder();

		for (int i = 0; i < nodesInOrder.length; i++){
			nodesInOrder[i].temp_class = new GeneralizingClassElement();
			nodesInOrder[i].temp_index = result.bestNodeChoiceInOrder[i];
		}
		
		for (int i = 0; i < linksInOrder.length; i++){
			linksInOrder[i].temp_property_cluster = null;
			linksInOrder[i].temp_index= result.bestLinkChoiceInOrder[i];
			linksInOrder[i].sub_direction = result.bestLinkChoiceSubjectDirectionInOrder[i]; 
			linksInOrder[i].obj_direction = result.bestLinkChoiceObjectDirectionInOrder[i]; 
		}

		return getGraph().toString();
	}

	
	public String getInterpretationFromImprovedResult(ImprovedRecallResult result){
		
		InputNode[] nodesInOrder = getNodesInOrder();
		InputLink[] linksInOrder = getLinksInOrder();

		for (int i = 0; i < nodesInOrder.length; i++){
			nodesInOrder[i].temp_class = result.mappedClassInOrder[i];
		}

		for (int i = 0; i < linksInOrder.length; i++){
			linksInOrder[i].temp_property_cluster = result.property_clusterInOrder[i];;
		}

		return getGraph().toString();
	}
	
	public String makeSparqlQueryFromOptimalResult(
			BestCombinationElement bestCombinationElement) {
		// TODO Auto-generated method stub
		return "";
	}
	
	public String makeConciseSparqlQueryFromOptimalResult(
			BestCombinationElement bestCombinationElement) {
		// TODO Auto-generated method stub
		return "";
	}

	public String makeSparqlQueryFromImprovedResult(
			ImprovedRecallResult improvedRecallResult) {
		// TODO Auto-generated method stub
		return "";
	}

	public String makeConciseSparqlQueryFromImprovedResult(
			ImprovedRecallResult improvedRecallResult) {
		// TODO Auto-generated method stub
		return "";
	}

	public String makeReducedSparqlQueryFromImprovedResult(
			ImprovedRecallResult improvedRecallResult) {
		// TODO Auto-generated method stub
		return "";
	}

	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		GraphMapping test = new GraphMapping("");
		
		long startTime = System.currentTimeMillis();
		test.setupInput();
		System.out.println("Starting translating ...");
		test.optimize();
		try{
			test.printOptimizedResult();
			//System.out.println(test.makeSparqlQueryFromOptimalResult(test.getResults()[0]));
			//System.out.println(test.makeConciseSparqlQueryFromOptimalResult(test.getResults()[0]));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//test.improveRecallByCollectingSimilarProperties();

		System.out.println(test.getInterpretationFromOptimalResult(test.getCombinationResults()[0]));
		System.out.println();
		
		
		/*
		System.out.println("The 1th best result after clustering similar properties is as follows: ");
		System.out.println(test.getInterpretationFromImprovedResult(test.getImprovedResults()[0]));
		System.out.println();

		System.out.println("The 2th best result after clustering similar properties is as follows: ");
		System.out.println(test.getInterpretationFromImprovedResult(test.getImprovedResults()[1]));
		System.out.println();

		System.out.println("The 3th best result after clustering similar properties is as follows: ");
		System.out.println(test.getInterpretationFromImprovedResult(test.getImprovedResults()[2]));
		System.out.println();

		System.out.println(test.makeSparqlQueryFromImprovedResult(test.getImprovedResults()[0]));
		System.out.println(test.makeConciseSparqlQueryFromImprovedResult(test.getImprovedResults()[0]));
		System.out.println(test.makeReducedSparqlQueryFromImprovedResult(test.getImprovedResults()[0]));
		*/
		
		long endTime = System.currentTimeMillis();
		long elipsedTime = endTime - startTime;

		System.out.println(elipsedTime + " milliseconds have been taken to produce the queries.");
	}






}
