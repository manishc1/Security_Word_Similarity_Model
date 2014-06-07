/*
 * Copyright 2009 Author: Lushan Han at Ebiquity Lab, UMBC
 *  
 */
package edu.umbc.dblp.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.umbc.configure.Configure;
import edu.umbc.dbpedia.model.FloatElement;
import edu.umbc.dbpedia.model.RelationDimensionElement;
import edu.umbc.dbpedia.model.SimilarityArrayModel;
import edu.umbc.dbpedia.util.LexicalProcess;
import edu.umbc.dbpedia.util.NounCompound;




public class CorrelationModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TreeMap<String, TreeMap<String, Integer>> cpt_rel_co_occurring_matrix;
	public TreeMap<String, Integer> conceptFrequencies;
	public TreeMap<String, Integer> relationFrequencies;

	public TreeMap<String, TreeMap<String, RelationDimensionElement>> cpt_cpt_co_occurring_matrix;
	public TreeMap<String, Integer> undirectedConceptFrequencies;
	
	public String modelName;
	public long in_universeSize;
	public long out_universeSize;
	public long conceptUniverseSize;
	
	public int FrequencyLimit = 100;
	public int CoOccurrenceFrequencyLimit = 10;
	public int CorrelatedRelationFrequencyLimit = 10;
	public boolean REMOVE_NON_FREQUENT_ITEMS = true;
	File coOccurModelFile;


	public TreeMap<String, TreeMap<String, IndirectCoOccurrenceElement>> in_cpt_cpt_co_occurring_matrix;
	
	public HashSet<String> publications = new HashSet<String>(Arrays.asList("<Publication", ">Publication", "Publication", "<Paper", ">Paper", "Paper", "<Book", ">Book", "Book", "<InBook", ">InBook", "InBook", "<Thesis", ">Thesis", "Thesis",  
			"<Proceedings", ">Proceedings", "Proceedings", "<InProceedings", ">InProceedings", "InProceedings", "<Article", ">Article", "Article"));
	


	public double lambda; // 1.0; 0.75; 0.5

	public CorrelationModel(){
		
        cpt_rel_co_occurring_matrix = new TreeMap<String, TreeMap<String, Integer>>();
        conceptFrequencies = new TreeMap<String, Integer>();
        relationFrequencies = new TreeMap<String, Integer>();
        in_universeSize = 0;
        out_universeSize = 0;
        
        cpt_cpt_co_occurring_matrix = new TreeMap<String, TreeMap<String, RelationDimensionElement>>();
        undirectedConceptFrequencies = new TreeMap<String, Integer>();
        conceptUniverseSize = 0;
	}
	
	
	public String printPCW(Object[] sortedWords, int size){
		
		if (sortedWords == null){
			System.out.println("null");
			return null;
		}
			
		StringBuffer temp = new StringBuffer();
		
		for (int i=0; i<size && i<sortedWords.length; i++){
			temp.append(sortedWords[i] + ", ");
		}
		
		System.out.println(temp);
		
		return temp.toString();

	}
	
	public boolean collectInternalVirtualClasses(String filename) throws Exception{
		
    	MaxentTagger tagger;
    	//SimilarityBox simBox;
    	SimilarityArrayModel simBox = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/webbase2012AllW2_S2");
    	Morphology morph;
    	HashMap<String, Integer> virtualTypes;

		String modelLocation = "/home/lushan1/nlp/model/stanford/pos-tagger/wsj-left3words/wsj-0-18-left3words-distsim.tagger"; 	//"/home/lushan1/nlp/model/stanford/left3words-distsim-wsj-0-18.tagger";
		System.out.println("Reading model from file=" + modelLocation);
        tagger = new MaxentTagger(modelLocation);
        morph = new Morphology();
 
        //simBox = new SimilarityBox();

        virtualTypes = new HashMap<String, Integer>();

        
		File relationFile = new File(filename);
		BufferedReader relationReader = new BufferedReader(new FileReader(relationFile), 1000000);
		
		int lineNo = 0;
		String rdline;
		
		while ((rdline = relationReader.readLine()) != null){
			
			StringTokenizer st = new StringTokenizer(rdline, " ");
			boolean isAttribute = false;

			String subject;
			String predicate;
			String object;

			try{
				subject = st.nextToken();
				predicate = st.nextToken();
				object = st.nextToken();
			}catch (Exception e){
				
				//throw new Exception("error format in " + rdline);
				System.out.println("invalid line " + lineNo + ": " + rdline + "\n");
				continue;
			}
			
			if (object.startsWith("\"")){
				isAttribute = true;
			}
						
			if (!isAttribute){
				
				NounCompound compound = new NounCompound(predicate, tagger, morph, simBox);
				
				if (compound.isValidNounCompound()){
				//if (compound.lengthInContentWords == 1 && compound.isValidNounCompound()){
				
					int index = compound.head.lastIndexOf('_');
					String type = Character.toUpperCase(compound.head.charAt(0)) + compound.head.substring(1, index);
					Integer count = virtualTypes.get(type);
					if (count == null) count = 0;
					count ++;
					virtualTypes.put(type, count);
					//System.out.println(type);
				}
				
			}
			
			lineNo ++;
			
			if (lineNo % 10000 == 0)
				System.out.println(lineNo);

		}
		
		for (Entry<String, Integer> entry: virtualTypes.entrySet()){
			System.out.println(entry.getKey() + ", " + entry.getValue());
		}
		
		return true;
	}
	
	
	public boolean populateInternalVirtualClasses(String filename) throws Exception{
		
        
    	MaxentTagger tagger;
    	SimilarityArrayModel simBox = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/webbase2012AllW2_S2");
    	//SimilarityBox simBox;
    	Morphology morph;
    	HashSet<String> internalVirtualClasses = new HashSet<String>();

		String modelLocation = "/home/lushan1/nlp/model/stanford/pos-tagger/wsj-left3words/wsj-0-18-left3words-distsim.tagger"; 	//"/home/lushan1/nlp/model/stanford/left3words-distsim-wsj-0-18.tagger";
		System.out.println("Reading model from file=" + modelLocation);
        tagger = new MaxentTagger(modelLocation);
        morph = new Morphology();
 
		File outFile = new File("/home/lushan1/dbpedia/new_instance_type.txt");
		PrintWriter out = new PrintWriter(new FileWriter(outFile));


		File internalVirtualClassesFile = new File("/home/lushan1/dbpedia/new_classes.txt");
		BufferedReader internalVirtualClassesReader = new BufferedReader(new FileReader(internalVirtualClassesFile));
		
		String rdline;
		
		while ((rdline = internalVirtualClassesReader.readLine()) != null){
			internalVirtualClasses.add(rdline);
		}

        
		File relationFile = new File(filename);
		BufferedReader relationReader = new BufferedReader(new FileReader(relationFile), 1000000);
		int count = 0;
		
		while ((rdline = relationReader.readLine()) != null){
			
			StringTokenizer st = new StringTokenizer(rdline, " ");
			boolean isAttribute = false;
			String attributeChar = "";

			String subject;
			String predicate;
			String object;

			try{
				subject = st.nextToken();
				predicate = st.nextToken();
				object = st.nextToken();
			}catch (Exception e){
				
				//throw new Exception("error format in " + rdline);
				System.out.println("invalid line " + count + ": " + rdline + "\n");
				continue;
			}
			
			if (object.startsWith("\"")){
				isAttribute = true;
				attributeChar = "@";
			}
						
			if (!isAttribute){
				
				NounCompound compound = new NounCompound(predicate, tagger, morph, simBox);
				
				if (compound.isValidNounCompound()){
				//if (compound.lengthInContentWords == 1 && compound.isValidNounCompound()){
				
					int index = compound.head.lastIndexOf('_');
					String type = "~" + Character.toUpperCase(compound.head.charAt(0)) + compound.head.substring(1, index);
					
					if (internalVirtualClasses.contains(type)){
						
						out.println(object + "\t" + type);
					}
				}
				
			}
			
			count ++;
			
			if (count % 10000 == 0)
				System.out.println(count);
			
		}
		
		out.close();
		
		return true;
	}

	
	public boolean processInputDataFile(String filename) throws Exception{
		
        String dbHost = "localhost";
        String dbName = "dbpedia_gor";
        String dbUser = "lushan1";
        
    	MaxentTagger tagger;
    	//SimilarityBox simBox;
    	SimilarityArrayModel simBox = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/webbase2012AllW2_S2");

    	Morphology morph;

		String modelLocation = "/home/lushan1/nlp/model/stanford/pos-tagger/wsj-left3words/wsj-0-18-left3words-distsim.tagger"; 	//"/home/lushan1/nlp/model/stanford/left3words-distsim-wsj-0-18.tagger";
		System.out.println("Reading model from file=" + modelLocation);
        tagger = new MaxentTagger(modelLocation);
        morph = new Morphology();
 
        //simBox = new SimilarityBox();

        
        Connection conn = null;
        java.sql.Statement stmt = null;

        /*-------------------------------------------*/
        /* Setup JDBC connection                     */
        /*-------------------------------------------*/
        try{
                // Load the MySQL Connector
                Class.forName("com.mysql.jdbc.Driver").newInstance();

                // Set connect string to sparql MySQL database
                String connString = "jdbc:mysql://" + dbHost + "/" + dbName;

                System.out.println("Trying connection with " + connString);
                conn = DriverManager.getConnection(connString, dbUser, "lingoblin");
                
                stmt = conn.createStatement();

        } catch (Exception e) {
                System.out.println("Error connecting sparql: " + e.toString());
                return false;
        }
        System.out.println("Connected Successfully.");
		
        
		File relationFile = new File(filename);
		BufferedReader relationReader = new BufferedReader(new FileReader(relationFile), 1000000);
		
		File logFile = new File(filename + "_log");
		PrintWriter error = new PrintWriter(new FileWriter(logFile));
		
		
		String rdline;
		int count = 0;
		
		while ((rdline = relationReader.readLine()) != null){
			
			StringTokenizer st = new StringTokenizer(rdline, " ");
			boolean isAttribute = false;
			String attributeChar = "";

			String subject;
			String predicate;
			String object;

			try{
				subject = st.nextToken();
				predicate = st.nextToken();
				object = st.nextToken();
			}catch (Exception e){
				
				//throw new Exception("error format in " + rdline);
				System.out.println("invalid line " + count + ": " + rdline + "\n");
				error.println("invalid line " + count + ": " + rdline + "\n");
				continue;
			}
			
			// add concept-relation co-occurrence
			String[] subjectTypes = getTypes(subject.replace("'", "\\'"), conn, stmt);
			String[] objectTypes = null;

			if (object.startsWith("\"")){
				isAttribute = true;
				attributeChar = "@";
			}
			
			if (subjectTypes.length == 0){
				subjectTypes = new String[1];
				subjectTypes[0] = "Thing";
				System.out.println("invalid subject " + count + ": " + rdline + "\n");
				error.println("invalid subject " + count + ": " + rdline + "\n");
			}
			/*
			for (String type:subjectTypes){
				addCoOccurrence("<" + type, attributeChar + predicate);
			}
			*/
			addCoOccurrence(subjectTypes, attributeChar + predicate, "<");
			
			
			if (!isAttribute){
				objectTypes = getTypes(object.replace("'", "\\'"), conn, stmt);

				if (objectTypes.length == 0){
					
					objectTypes = new String[1];
					objectTypes[0] = "Thing";

					//System.out.println("invalid object " + count + ": " + rdline + "\n");
					error.println("invalid object " + count + ": " + rdline + "\n");
				}

				addCoOccurrence(objectTypes, predicate, ">");
				
			}else{
				
				String cleanObject = object.substring(2, object.length() - 1);
				
				ArrayList<String> types = new ArrayList<String>();
				
				types.add("Thing");
				types.add("^Literal");
				
				int index = cleanObject.indexOf("^^");
				if (index < 0){
					
					NounCompound compound = new NounCompound(predicate, tagger, morph, simBox);
					if (compound.isValidNounCompound()){
						types.add("^" + Character.toUpperCase(compound.head.charAt(0)) + compound.head.substring(1, compound.head.lastIndexOf('_')));
						//types.add("^" + Character.toUpperCase(predicate.charAt(0)) + predicate.substring(1, predicate.length()));
						
					}
					
					if (LexicalProcess.isNumber(cleanObject)){
						types.add("^Number");
					}else{
						types.add("^Text");
					}
					
				}else{

					String data = cleanObject.substring(0, index);
					String type = cleanObject.substring(index + 3, cleanObject.length() - 1);
					
					if (type.equals("gYear") || type.equals("date")){
						
						types.add("^Date");
						
						if (type.equals("gYear")){
							types.add("^Year");
						}
					
					}else{
						types.add("^Number");
					}
					
					NounCompound compound = new NounCompound(predicate, tagger, morph, simBox);
					if (compound.isValidNounCompound()){
						types.add("^" + Character.toUpperCase(compound.head.charAt(0)) + compound.head.substring(1, compound.head.lastIndexOf('_')));
						//types.add("^" + Character.toUpperCase(predicate.charAt(0)) + predicate.substring(1, predicate.length()));
					}
					
					
				}

				objectTypes = new String[types.size()];
				types.toArray(objectTypes);
				
				addCoOccurrence(objectTypes, attributeChar + predicate, ">");

			}
			
			// add concept-concept co-occurrence
			if (objectTypes != null){
				
				addConceptCoOccurrence(subjectTypes, objectTypes, attributeChar + predicate);
			}
			
			count ++;
			
			if (count % 10000 == 0)
				System.out.println(count);

		}
		
		//close jdbc connection.
		conn.close();
		
		error.close();
		return true;
	}
	
	private String[] getTypes(String instance, Connection conn, java.sql.Statement stmt) throws SQLException {

		ResultSet rs = null;
        String varSQL = null;

		// TODO Auto-generated method stub
        varSQL = "SELECT type FROM native_instance_type WHERE ";
        varSQL += "instance = '" + instance + "'";
        
        rs = stmt.executeQuery(varSQL);
        Vector<String> types = new Vector<String>();
        
        while (rs.next() == true){
        	types.add(rs.getString(1));
        }
		
		return types.toArray(new String[types.size()]);
	}

	

	public void addCoOccurrence(String[] concepts, String relation, String direction){
		
		
		for (String concept : concepts){
			
			concept = direction + concept; 
		
			TreeMap<String, Integer> relationMap = cpt_rel_co_occurring_matrix.get(concept);
			
			if (relationMap == null){
				relationMap = new TreeMap<String, Integer>();
				cpt_rel_co_occurring_matrix.put(concept, relationMap);
			}
			
			Integer co_occurrances = relationMap.get(relation);
			
			if (co_occurrances == null){
				co_occurrances = 1;
			} else {
				co_occurrances ++;
			}
			
			relationMap.put(relation, co_occurrances);
			
			
			Integer conceptFrequency = conceptFrequencies.get(concept);
			
			if (conceptFrequency == null){
				conceptFrequency = 1;
			}else{
				conceptFrequency ++;
			}
			
			conceptFrequencies.put(concept, conceptFrequency);
			
			if (direction.equals("<"))
				out_universeSize ++;
			else
				in_universeSize ++;
		}
		
		
		Integer relationFrequency = relationFrequencies.get(relation);
		
		if (relationFrequency == null){
			relationFrequency = 1;
		}else{
			relationFrequency ++;
		}
		
		relationFrequencies.put(relation, relationFrequency);
	
	}

		
	public int getConceptRelationCoOccurrence(String concept, String relation){
		
		TreeMap<String, Integer> relationMap = cpt_rel_co_occurring_matrix.get(concept);
		
		if (relationMap == null)
			return 0;
		
		Integer co_occurrances = relationMap.get(relation);
		
		if (co_occurrances == null)
			return 0;
		
		return co_occurrances;
	}

	
	public int getConceptFrequency(String concept){
		
		Integer conceptFrequency = conceptFrequencies.get(concept);
		
		if (conceptFrequency == null)
			return 0;
		else
			return conceptFrequency;
	}

	
	public int getRelationFrequency(String relation){
		
		Integer relationFrequency = relationFrequencies.get(relation);
		
		if (relationFrequency == null)
			return 0;
		else
			return ((int) relationFrequency) / 2;
	}
	

	public float getConceptRelationPMI(String concept, String relation) {
	
		double frqConcept = getConceptFrequency(concept);
		double frqRelation = getRelationFrequency(relation);
		
		double co_occurrences = getConceptRelationCoOccurrence(concept, relation);
		
		if (frqConcept == 0 || frqRelation == 0) 
			//throw new Exception(concept + " or " + relation + " has zero frequency");
			return Float.NEGATIVE_INFINITY;
		
		if (REMOVE_NON_FREQUENT_ITEMS){
			if (frqConcept < FrequencyLimit || frqRelation < FrequencyLimit || co_occurrences < CoOccurrenceFrequencyLimit){
				//throw new Exception(concept + " or " + relation + " has very low frequency");
				return Float.NEGATIVE_INFINITY;
			}
		}
		
		long universeSize = 0;
		
		/*
		if (concept.startsWith("<"))
			universeSize = out_universeSize;
		else if (concept.startsWith(">"))
			universeSize = in_universeSize;
		*/
		
		universeSize= out_universeSize + in_universeSize;
		
		
		//double pmi = Math.log(((double) co_occurrences * universeSize) / ((double) frqConcept * frqRelation)) + Relation_PMI_Offset; 
		//double delta1 =	(Math.log(Math.log(frqRelation)) + Relation_Augment_PMI_Offset) * Relation_Augment_PMI_Ratio;
		//double delta2 =	(Math.log(Math.log(frqConcept)) + Concept_Augment_PMI_Offset) * Concept_Augment_PMI_Ratio;
	
		return getPMI(co_occurrences, universeSize, frqConcept, frqRelation); 
	}
	
	public float getPMI(double co_occurrences, double universeSize, double frq1, double frq2){
		
		double p = 1.5; // 2;
		double q = -5; 
		long gutenberg_universeSize = 2000000000;

		double times = ((double )gutenberg_universeSize) / universeSize;
		double c = p * (Math.log(700) + q);
		double pmi = Math.log(((double) co_occurrences * universeSize) / ((double) frq1 * frq2)); 
		double delta1 =	(Math.log(Math.log(frq2 * times) + q)) * p - c;
		double delta2 =	(Math.log(Math.log(frq1 * times) + q)) * p - c;
		
		return (float) (pmi + delta1 + delta2);
		//return (float) pmi;
	}
	

	public float getCondiProb(String concept, String relation) throws Exception{
	
		double frqConcept = getConceptFrequency(concept);
		
		double co_occurrences = getConceptRelationCoOccurrence(concept, relation);
		
		if (frqConcept == 0) 
			throw new Exception(concept + " has zero frequency");
		
		double condiProb = ((double) co_occurrences) / (double) frqConcept;
	
		return (float) condiProb; 
	}


	public FloatElement[] getRelationsSortedByPMI(String concept){
		
		
		TreeMap<String, Integer> relationMap = cpt_rel_co_occurring_matrix.get(concept);
		if (relationMap == null)
			return null;

		Vector<FloatElement> correlatedRelations = new Vector<FloatElement>();
		int frqConcept = getConceptFrequency(concept);
	
		for (Entry<String, Integer> entry : relationMap.entrySet()){
			
			String relation = entry.getKey();
			int co_occurrences = entry.getValue();
			
			int frqRelation = getRelationFrequency(relation);
	
			if (REMOVE_NON_FREQUENT_ITEMS){
				if (frqConcept < FrequencyLimit || frqRelation < FrequencyLimit){
					continue;
				}
			}
			
			long universeSize = 0;
			
			/*
			if (concept.startsWith("<"))
				universeSize = out_universeSize;
			else if (concept.startsWith(">"))
				universeSize = in_universeSize;
			*/
			
			universeSize = out_universeSize + in_universeSize;
				
			FloatElement element = new FloatElement(relation, getPMI(co_occurrences, universeSize, frqConcept, frqRelation), ((float) co_occurrences) / frqConcept, frqRelation);
			
			correlatedRelations.add(element);
		}
		
		FloatElement[] result = correlatedRelations.toArray(new FloatElement[correlatedRelations.size()]);
		
		Arrays.sort(result);
		
		return result;
		
	}

	
	public FloatElement[] getUndirectedDirectConceptsSortedByPMI(String concept1){
		
		
		Vector<FloatElement> correlatedConcepts = new Vector<FloatElement>();
		
		for (Entry<String, Integer> entry: undirectedConceptFrequencies.entrySet()){
		
			String concept2 = entry.getKey();
			
			double pmi = getUndirectedDirectConceptPMI(concept1, concept2);
			
			FloatElement element = new FloatElement(concept2, (float) (pmi), 0, entry.getValue());
			
			correlatedConcepts.add(element);
		}

		FloatElement[] result = correlatedConcepts.toArray(new FloatElement[correlatedConcepts.size()]);
		
		Arrays.sort(result);
		
		return result;
		
	}

	public FloatElement[] getDirectConceptsOfConceptSortedByPMI(String concept1){
		
		
		Vector<FloatElement> correlatedConcepts = new Vector<FloatElement>();
		
		for (Entry<String, Integer> entry: conceptFrequencies.entrySet()){
		
			String concept2 = entry.getKey();
			
			double pmi = getDirectConceptPMI(concept1, concept2);
			
			FloatElement element = new FloatElement(concept2, (float) (pmi), 0, entry.getValue());
			
			correlatedConcepts.add(element);
		}

		FloatElement[] result = correlatedConcepts.toArray(new FloatElement[correlatedConcepts.size()]);
		
		Arrays.sort(result);
		
		return result;
		
	}

	
	public FloatElement[] getIndirectConceptsOfConceptSortedByPMI(String concept1){
		
		
		Vector<FloatElement> correlatedConcepts = new Vector<FloatElement>();
		
		for (Entry<String, Integer> entry: conceptFrequencies.entrySet()){
		
			String concept2 = entry.getKey();
			
			double pmi = getIndirectConceptPMI(concept1, concept2);
			
			FloatElement element = new FloatElement(concept2, (float) (pmi), 0, entry.getValue());
			
			correlatedConcepts.add(element);
		}

		FloatElement[] result = correlatedConcepts.toArray(new FloatElement[correlatedConcepts.size()]);
		
		Arrays.sort(result);
		
		return result;
		
	}

	public FloatElement[] getConceptsOfRelationSortedByPMI(String relation){
		
		
		Vector<FloatElement> correlatedConcepts = new Vector<FloatElement>();
		
		for (Entry<String, Integer> entry: conceptFrequencies.entrySet()){
		
			String concept = entry.getKey();
			
			double pmi = getConceptRelationPMI(concept, relation);
			
			FloatElement element = new FloatElement(concept, (float) (pmi), 0, entry.getValue());
			
			correlatedConcepts.add(element);
		}

		FloatElement[] result = correlatedConcepts.toArray(new FloatElement[correlatedConcepts.size()]);
		
		Arrays.sort(result);
		
		return result;
		
	}


	public FloatElement[] getRelationsSortedByCondiProb(String concept){
				
		TreeMap<String, Integer> relationMap = cpt_rel_co_occurring_matrix.get(concept);
		if (relationMap == null)
			return null;

		Vector<FloatElement> correlatedRelations = new Vector<FloatElement>();
		int frqConcept = getConceptFrequency(concept);
	
		for (Entry<String, Integer> entry : relationMap.entrySet()){
			
			String relation = entry.getKey();
			int co_occurrences = entry.getValue();
			
	
			if (REMOVE_NON_FREQUENT_ITEMS){
				if (frqConcept < FrequencyLimit){
					continue;
				}
			}
	
			FloatElement element = new FloatElement(relation, ((float) co_occurrences) / frqConcept);
			
			correlatedRelations.add(element);
		}
		
		FloatElement[] result = correlatedRelations.toArray(new FloatElement[correlatedRelations.size()]);
		
		Arrays.sort(result);
		
		return result;
		
	}


	public int getUndirectedConceptFrequency(String concept){
		
		Integer conceptFrequency = undirectedConceptFrequencies.get(concept);
		
		if (conceptFrequency == null)
			return 0;
		else
			return conceptFrequency;
	}
		
	public int getDirectConceptCoOccurrence(String concept1, String concept2){
		
		if (concept1.compareToIgnoreCase(concept2) > 0){
			String temp = concept1;
			concept1 = concept2;
			concept2 = temp;
		}

		TreeMap<String, RelationDimensionElement> concept1Map = cpt_cpt_co_occurring_matrix.get(concept1);
		
		if (concept1Map == null)
			return 0;
		
		RelationDimensionElement relations = concept1Map.get(concept2);
		
		if (relations == null)
			return 0;
		else
			return relations.totalFrequency;
	}
	
	
	public TreeMap<String, Integer> getRelationsBetweenDirectConcepts(String concept1, String concept2){
		
		if (concept1.compareToIgnoreCase(concept2) > 0){
			String temp = concept1;
			concept1 = concept2;
			concept2 = temp;
		}

		TreeMap<String, RelationDimensionElement> concept1Map = cpt_cpt_co_occurring_matrix.get(concept1);
		
		if (concept1Map == null)
			return null;
		
		RelationDimensionElement relations = concept1Map.get(concept2);
		
		if (relations == null)
			return null;
		else
			return relations.relationFrequencies;
	}
	
	
	public double getIndirectConceptCoOccurrence(String concept1, String concept2){
		
		if (concept1.compareToIgnoreCase(concept2) > 0){
			String temp = concept1;
			concept1 = concept2;
			concept2 = temp;
		}

		TreeMap<String, IndirectCoOccurrenceElement> concept1Map = in_cpt_cpt_co_occurring_matrix.get(concept1);
		
		if (concept1Map == null)
			return 0;
		
		IndirectCoOccurrenceElement coOccurrenceElement = concept1Map.get(concept2);
		
		if (coOccurrenceElement == null)
			return 0;
		else
			return coOccurrenceElement.maxCoOccurrence;
	}

	
	public HashMap<String, Double> getIndirectConceptCoOccurrencePath(String concept1, String concept2){
		
		if (concept1.compareToIgnoreCase(concept2) > 0){
			String temp = concept1;
			concept1 = concept2;
			concept2 = temp;
		}

		TreeMap<String, IndirectCoOccurrenceElement> concept1Map = in_cpt_cpt_co_occurring_matrix.get(concept1);
		
		if (concept1Map == null)
			return null;
		
		IndirectCoOccurrenceElement coOccurrenceElement = concept1Map.get(concept2);
		
		if (coOccurrenceElement == null)
			return null;
		else
			return coOccurrenceElement.paths;
	}
	
	
	public double getConceptPathCoOccurrence(String concept1, String concept2, String conceptConnectedPath){
		
		
		if (concept1.compareToIgnoreCase(concept2) > 0){
			String temp = concept1;
			concept1 = concept2;
			concept2 = temp;
		}

		TreeMap<String, IndirectCoOccurrenceElement> concept1Map = in_cpt_cpt_co_occurring_matrix.get(concept1);
		
		if (concept1Map == null)
			return 0;
		
		IndirectCoOccurrenceElement coOccurrenceElement = concept1Map.get(concept2);
		
		if (coOccurrenceElement == null)
			return 0;
		else{
			
			Double result = coOccurrenceElement.paths.get(conceptConnectedPath);
			
			if (result != null)
				return result;
			else
				return 0;
		}
	}

	
	int getJumps(String path){
		
		int depth = 0;
		int place;
		String rest = path;
		
		do{
			place = rest.indexOf(", ");
			
			if (place > 0){
				rest = rest.substring(place + 2);
				depth ++;
			}
			
		}while (place > 0);
		
		return depth - 1;
	}
	
	public boolean putIndirectConceptCoOccurrence(String concept1, String concept2, double value, String path){
		
		boolean update = false;
		
		if (concept1.compareToIgnoreCase(concept2) > 0){
			String temp = concept1;
			concept1 = concept2;
			concept2 = temp;
		}

		TreeMap<String, IndirectCoOccurrenceElement> concept1Map = in_cpt_cpt_co_occurring_matrix.get(concept1);
		
		if (concept1Map == null){
			concept1Map = new TreeMap<String, IndirectCoOccurrenceElement>();
			in_cpt_cpt_co_occurring_matrix.put(concept1, concept1Map);
		}
		
		IndirectCoOccurrenceElement coOccurrenceElement = concept1Map.get(concept2);
		
		if (coOccurrenceElement == null){
			coOccurrenceElement = new IndirectCoOccurrenceElement();
			coOccurrenceElement.maxCoOccurrence = value;
			coOccurrenceElement.paths.put(path, value);
			concept1Map.put(concept2, coOccurrenceElement);
			update = true;
			
		}else{
			
			if (value > coOccurrenceElement.maxCoOccurrence){
				coOccurrenceElement.maxCoOccurrence = value;
				coOccurrenceElement.paths.put(path, value);
				update = true;
			}else{
				
				int jumps = getJumps(path);
				
				if (jumps < 0)
					System.out.println(path + " has jumps " + jumps);
				
				if (value > Configure.ConceptCoOccurrenceThreshold * Math.pow(lambda, jumps))
					coOccurrenceElement.paths.put(path, value);
				
			}
		}
		
		return update;
	}
	

	

	public void addConceptCoOccurrence(String[] conceptArray1, String[] conceptArray2, String relation){
		
		for (int i = 0; i < conceptArray1.length; i++){
			
			for (int j = 0; j < conceptArray2.length; j++){
				
				String concept1 = "<" + conceptArray1[i];
				
				String concept2 = ">" + conceptArray2[j];
				
				if (concept1.compareToIgnoreCase(concept2) > 0){
					String temp = concept1;
					concept1 = concept2;
					concept2 = temp;
				}
				
				TreeMap<String, RelationDimensionElement> concept1Map = cpt_cpt_co_occurring_matrix.get(concept1);
				
				if (concept1Map == null){
					concept1Map = new TreeMap<String, RelationDimensionElement>();
					cpt_cpt_co_occurring_matrix.put(concept1, concept1Map);
				}
				
				
				RelationDimensionElement relations = concept1Map.get(concept2);
				
				if (relations == null){
					relations = new RelationDimensionElement();
					concept1Map.put(concept2, relations);
				}
				
		
				Integer co_occurrances = relations.relationFrequencies.get(relation);
				
				if (co_occurrances == null){
					co_occurrances = 1;
				} else {
					co_occurrances ++;
				}
				
				relations.relationFrequencies.put(relation, co_occurrances);
				
				//add marginal frequency for relation dimension
				relations.totalFrequency ++;
				
				//conceptUniverseSize++;

			}
		}
		
		// modify marginal frequency for concepts
		for (String concept1 : conceptArray1){		
		
			Integer concept1Frequency = undirectedConceptFrequencies.get(concept1);
			
			if (concept1Frequency == null){
				concept1Frequency = 1;
			}else{
				concept1Frequency ++;
			}
			
			undirectedConceptFrequencies.put(concept1, concept1Frequency);
			
			conceptUniverseSize++;
		}
	
		
	
		for (String concept2 : conceptArray2){	
			
			Integer concept2Frequency = undirectedConceptFrequencies.get(concept2);
			
			if (concept2Frequency == null){
				concept2Frequency = 1;
			}else{
				concept2Frequency ++;
			}
			
			undirectedConceptFrequencies.put(concept2, concept2Frequency);
			
			conceptUniverseSize++;
		}
		
		
	}

	
	public float getUndirectedConceptCondiProb(String priorConcept, String posteriorConcept) throws Exception{
	
		double frqConcept = getUndirectedConceptFrequency(priorConcept);
		
		double co_occurrences = getDirectConceptCoOccurrence("<" + priorConcept, "> " + posteriorConcept) +
								getDirectConceptCoOccurrence(">" + priorConcept, "< " + posteriorConcept);
		
		if (frqConcept == 0) 
			throw new Exception(priorConcept + " has zero frequency");
		
		double condiProb = ((double) co_occurrences) / (double) frqConcept;
	
		return (float) condiProb; 
	}


	public float getUndirectedDirectConceptPMI(String conceptA, String conceptB){
	
		float maxPMI = Float.NEGATIVE_INFINITY;
		String concept1; 
		String concept2;
		
		for (int i = 0; i < 2; i++){
			
			if (i == 0){
				concept1 = ">" + conceptA;
			}else{
				concept1 = "<" + conceptA;
			}
			
			for (int j = 0; j < 2; j++){
				
				if (j == 0){
					concept2 = ">" + conceptB;
				}else{
					concept2 = "<" + conceptB;
				}
				
				double frqConcept1 = getConceptFrequency(concept1);
				double frqConcept2 = getConceptFrequency(concept2);
				
				double co_occurrences = getDirectConceptCoOccurrence(concept1, concept2);
				
				if (frqConcept1 == 0 || frqConcept2 == 0) 
					//throw new Exception(concept1 + " or " + concept2 + " has zero frequency");
					break;
				
				if (REMOVE_NON_FREQUENT_ITEMS){
					if (frqConcept1 < FrequencyLimit || frqConcept2 < FrequencyLimit){
						//throw new Exception(concept1 + " or " + concept2 + " has very low frequency");
						break;
					}
				}
				
				float pmi = getPMI(co_occurrences, conceptUniverseSize, frqConcept1, frqConcept2);
				
				if (pmi > maxPMI){
					maxPMI = pmi;
				}
	
			}
		
		}
		
		return maxPMI; 
		

	}

	
	public float getDirectConceptPMI(String concept1, String concept2){
		
		double frqConcept1 = getConceptFrequency(concept1);
		double frqConcept2 = getConceptFrequency(concept2);
		
		double co_occurrences = getDirectConceptCoOccurrence(concept1, concept2);
		
		if (frqConcept1 == 0 || frqConcept2 == 0) 
			//throw new Exception(concept1 + " or " + concept2 + " has zero frequency");
			return Float.NEGATIVE_INFINITY;
		
		if (REMOVE_NON_FREQUENT_ITEMS){
			if (frqConcept1 < FrequencyLimit || frqConcept2 < FrequencyLimit){
				//throw new Exception(concept1 + " or " + concept2 + " has very low frequency");
				return Float.NEGATIVE_INFINITY;
			}
		}
		
		double pmi = getPMI(co_occurrences, conceptUniverseSize, frqConcept1, frqConcept2);
	
		return (float) pmi; 
	}

	
	public float getIndirectConceptPMI(String concept1, String concept2){
		
		double frqConcept1 = getConceptFrequency(concept1);
		double frqConcept2 = getConceptFrequency(concept2);
		
		double co_occurrences = getIndirectConceptCoOccurrence(concept1, concept2);
		
		if (frqConcept1 == 0 || frqConcept2 == 0) 
			//throw new Exception(concept1 + " or " + concept2 + " has zero frequency");
			return Float.NEGATIVE_INFINITY;
		
		if (REMOVE_NON_FREQUENT_ITEMS){
			if (frqConcept1 < FrequencyLimit || frqConcept2 < FrequencyLimit){
				//throw new Exception(concept1 + " or " + concept2 + " has very low frequency");
				return Float.NEGATIVE_INFINITY;
			}
		}
		
		double pmi = getPMI(co_occurrences, conceptUniverseSize, frqConcept1, frqConcept2);
	
		return (float) pmi; 
	}

	
	public FloatElement[] getCorrelatedRelationsSortedByCondiProb(String concept1, String concept2){
		
		if (concept1.compareToIgnoreCase(concept2) > 0){
			String temp = concept1;
			concept1 = concept2;
			concept2 = temp;
		}
		
		TreeMap<String, RelationDimensionElement> concept1Map = cpt_cpt_co_occurring_matrix.get(concept1);
		if (concept1Map == null)
			return null;

		RelationDimensionElement relations = concept1Map.get(concept2);
		
		if (relations == null || relations.totalFrequency == 0){
			return null;
		}

		Vector<FloatElement> correlatedRelations = new Vector<FloatElement>();
		int totalFrequency = relations.totalFrequency;
		
		for (Entry<String, Integer> entry : relations.relationFrequencies.entrySet()){
			
			String relation = entry.getKey();
			int co_occurrences = entry.getValue();
			
			if (relation.startsWith("twinCountry"))
				continue;
			
			if (REMOVE_NON_FREQUENT_ITEMS){
				if (co_occurrences < CorrelatedRelationFrequencyLimit){
					continue;
				}
			}
	
			FloatElement element = new FloatElement(relation, ((float) co_occurrences) / totalFrequency);
			
			correlatedRelations.add(element);
		}
		
		FloatElement[] result = correlatedRelations.toArray(new FloatElement[correlatedRelations.size()]);
		
		Arrays.sort(result);
		
		return result;
		
	}
	
	public double getRelationProbabilityConditionedByConcepts(String relation_p, String concept1, String concept2){
		
		if (concept1.compareToIgnoreCase(concept2) > 0){
			String temp = concept1;
			concept1 = concept2;
			concept2 = temp;
		}
		
		TreeMap<String, RelationDimensionElement> concept1Map = cpt_cpt_co_occurring_matrix.get(concept1);
		if (concept1Map == null)
			return 0;
		
		RelationDimensionElement relations = concept1Map.get(concept2);
		
		if (relations == null || relations.totalFrequency == 0){
			return 0;
		}

		int totalFrequency = relations.totalFrequency;
		
		Integer co_occurrences = relations.relationFrequencies.get(relation_p);
		
		if (co_occurrences == null)
			return 0;
		else
			return ((double) co_occurrences) / totalFrequency;
		
	}
	
	
	public int getNumberOfCoOccurrences(String relation_p, String concept1, String concept2){
		
		if (concept1.compareToIgnoreCase(concept2) > 0){
			String temp = concept1;
			concept1 = concept2;
			concept2 = temp;
		}
		
		TreeMap<String, RelationDimensionElement> concept1Map = cpt_cpt_co_occurring_matrix.get(concept1);
		if (concept1Map == null)
			return 0;
		
		RelationDimensionElement relations = concept1Map.get(concept2);
		
		if (relations == null || relations.totalFrequency == 0){
			return 0;
		}

		Integer co_occurrences = relations.relationFrequencies.get(relation_p);
		
		if (co_occurrences == null)
			return 0;
		else
			return (co_occurrences);
		
	}
	
	public void computeIndirectedCoOccurrence() throws IOException{
		
		//HashMap<String, Double> traverse = new HashMap<String, Double>();
		TreeMap<String, Double> traverse = new TreeMap<String, Double>();
		PrintWriter out = new PrintWriter(new FileWriter("/home/lushan1/dbpedia/IndirectedCoOccurrence" + lambda + ".txt"));
		int count = 0;
		
		for (String concept: undirectedConceptFrequencies.keySet()){
			
			//if (!concept.contains("^") && !concept.equals("Literal") && !concept.equals("Number") && !concept.equals("Year") && !concept.equals("Date") && !concept.equals("Text")){
			if (!concept.contains("^")){
				
				Vector<TraceElement> trace = new Vector<TraceElement>();
				trace.add(new TraceElement(concept, getUndirectedConceptFrequency(concept)));
	
				computeIndirectedCoOccurrenceFromSingleSource(trace, traverse, 0);
			}
			
			count ++;
			
			System.out.println(count);
		}
		
		for (Entry<String, Double> entry: traverse.entrySet()){
			
			String path = entry.getKey();
			
			int firstIndex = path.indexOf(",");
			String direction = path.substring(firstIndex + 2,  firstIndex + 3);
			String sourceConcept = path.substring(0, firstIndex);
			
			if (direction.equals("<"))
				sourceConcept = ">" + sourceConcept;
			else if (direction.equals(">"))
				sourceConcept = "<" + sourceConcept;
			else{
				System.err.println(path);
				System.exit(-1);
			}
				
			int lastIndex = path.lastIndexOf(",");
			String endConcept = path.substring(lastIndex + 2, path.length());
			
			if (putIndirectConceptCoOccurrence(sourceConcept, endConcept, entry.getValue(), path)){
				out.println(path + " (" + entry.getValue() + ")");
			}
			
		}
		
		out.close();
		
	}
	
	public void computeIndirectedCoOccurrenceFromSingleSource(Vector<TraceElement> trace, TreeMap<String, Double> result, int length){
		
		if (length == Configure.PathMaxLength)
			return;
			
		String lastConcept = trace.lastElement().concept;
		double lastCoOccurrence = trace.lastElement().coOccurrence;
		
		if (lastConcept.startsWith("<") || lastConcept.startsWith(">"))
			lastConcept = lastConcept.substring(1, lastConcept.length());
		
		HashSet<String> connectedNode;
		
		connectedNode = getConnectedConcept(lastConcept);
		
		for (String curConcept: connectedNode){
			
			String direction;
			
			if (curConcept.startsWith("<"))
				direction = ">";
			else
				direction = "<";
			
			double newCoOccurrence;
			
			if (length == 0)
				newCoOccurrence = lastCoOccurrence * getDirectConceptCoOccurrence(direction + lastConcept, curConcept) / getUndirectedConceptFrequency(lastConcept);
			else
				newCoOccurrence = lambda * lastCoOccurrence * getDirectConceptCoOccurrence(direction + lastConcept, curConcept) / getUndirectedConceptFrequency(lastConcept);

			String traceString = trace.toString();
			
			/*
			 * The following "if else" clause can be replaced with
			 * result.put(traceString.substring(1, traceString.length() - 1) + ", " +  curConcept, newCoOccurrence);
			 * if we want to avoid semantic constraints made by human judgment.
			 */
			
			/*
			if (length >= 1){
				
				if (curConcept.contains("NumberOfCitations") || curConcept.contains("NumberOfPublications")){
					
					boolean found = false;
					
					for (int i=0; i < trace.size(); i++){
						
						if (publications.contains(trace.elementAt(i).concept)){
							found = true;
							break;
						}
					}
					
					if (!found)
						result.put(traceString.substring(1, traceString.length() - 1) + ", " +  curConcept, newCoOccurrence);
					
				}else
					result.put(traceString.substring(1, traceString.length() - 1) + ", " +  curConcept, newCoOccurrence);
			
			}else
				result.put(traceString.substring(1, traceString.length() - 1) + ", " +  curConcept, newCoOccurrence);
			*/
			
			result.put(traceString.substring(1, traceString.length() - 1) + ", " +  curConcept, newCoOccurrence);
			
			
			
			
			
			//System.out.println(traceString.substring(1, traceString.length() - 1) + ", " +  curConcept + " (" + newCoOccurrence + ")");
			

			if (!curConcept.contains("^")
					//&& !trace.contains(curConcept.substring(1, curConcept.length())) && !trace.contains("<" + curConcept.substring(1, curConcept.length())) && !trace.contains(">" + curConcept.substring(1, curConcept.length()))
					){
				
				trace.add(new TraceElement(curConcept, newCoOccurrence));
				computeIndirectedCoOccurrenceFromSingleSource(trace, result, length + 1);
				trace.removeElementAt(trace.size() - 1);
			}
			
		}
		
	}
	
	
	HashSet<String> getConnectedConcept(String concept){
		
		HashSet<String> connectedConcept = new HashSet<String>();
		
		for (Entry<String, TreeMap<String, RelationDimensionElement>> entry: cpt_cpt_co_occurring_matrix.entrySet()){
			
			if (entry.getKey().equals("<" + concept)){

				connectedConcept.addAll(entry.getValue().keySet());
				
				if (entry.getValue().keySet().contains(">" + concept))
					connectedConcept.add(entry.getKey());
			
			}else{
			
				for (String connectednode: entry.getValue().keySet()){
					
					if (connectednode.equals(">" + concept))
						connectedConcept.add(entry.getKey());
				}
			}

		}
		
		connectedConcept.remove("<Thing");
		connectedConcept.remove(">Thing");

		//System.out.println(connectedConcept);
		return connectedConcept;
	}
	

	static public CorrelationModel readModel(String modelname) throws IOException, ClassNotFoundException{
        
		FileInputStream fileIn = new FileInputStream(modelname + ".cpt_rel_comodel");
        ObjectInputStream objIn = new ObjectInputStream(fileIn);
        return (CorrelationModel) objIn.readObject();
	}
	
	
	static public void writeModel(String modelname, CorrelationModel model) throws IOException{

		FileOutputStream fileOut;
		ObjectOutputStream objOut;
		fileOut = new FileOutputStream(modelname + ".cpt_rel_comodel");
		objOut = new ObjectOutputStream(fileOut);
		objOut.writeObject(model);
		objOut.close();
		System.out.println("seriliazation done!");

	}
	
	public void close(){
		
	}
	
	public void printCoOccurrenceUpdatedByIndirectedCoOccurrence(){
		
		System.out.println("Concept-Concept matrix ------------------------------------");
		for (Entry<String, TreeMap<String, RelationDimensionElement>> entry: this.cpt_cpt_co_occurring_matrix.entrySet()){
			
			System.out.print(entry.getKey() + ": ");
			
			for (Entry<String, RelationDimensionElement> entry2: entry.getValue().entrySet()){
				
				double coOccurence = this.getIndirectConceptCoOccurrence(entry.getKey(), entry2.getKey());
				
				if (coOccurence > entry2.getValue().totalFrequency){
					
					double oldPMI = this.getDirectConceptPMI(entry.getKey(), entry2.getKey());
					double newPMI = this.getIndirectConceptPMI(entry.getKey(), entry2.getKey());
					
					System.out.print(entry2.getKey() + "(" + entry2.getValue().totalFrequency + ", " + coOccurence +  ") " + "(" + oldPMI + ", " + newPMI +  ") " );
				}
			}

			System.out.println();

		}

	}
	
	public void printDirectConnectDegree(String outFileName) throws IOException{
		
		PrintWriter out = null;

		if (outFileName != null)
			out = new PrintWriter(new FileWriter(outFileName, true));

		int rows = 0;
		
		for (String concept1: undirectedConceptFrequencies.keySet()){
			
			if (concept1.equals("Thing"))
				continue;
			
			if (concept1.contains("^") || concept1.contains("~"))
				continue;
			
			String directedConcept1 = "<" + concept1;
			
			rows ++;
			
			int columns = 0;
			for (String concept2: undirectedConceptFrequencies.keySet()){
				
				if (concept2.equals("Thing"))
					continue;
				
				if (concept2.contains("^") || concept2.contains("~"))
					continue;
				
				String directedConcept2 = ">" + concept2;


				columns ++;
				TreeMap<String, Integer> map = getRelationsBetweenDirectConcepts(directedConcept1, directedConcept2);
				
				if (map != null){
					out.println(map.size());
					
					/*
					if (map.size() >= 2 && map.size() <= 4){
						System.out.println(directedConcept1 + " " + directedConcept2 + ": " + map);
					}
					*/
				}else
					out.println(0);

			}
			System.out.println(columns);
		}
		
		System.out.println("# of rows is " + rows);
		out.close();
	}
	
	public void printPrunedIndirectPathConnectDegree(String outFileName) throws IOException{
		
		PrintWriter out = null;

		if (outFileName != null)
			out = new PrintWriter(new FileWriter(outFileName, true));

		
		for (String concept1: undirectedConceptFrequencies.keySet()){
			
			if (concept1.equals("Thing"))
				continue;
			
			if (concept1.contains("^") || concept1.contains("~"))
				continue;
			
			String directedConcept1 = "<" + concept1;

			
			for (String concept2: undirectedConceptFrequencies.keySet()){
				
				if (concept2.equals("Thing"))
					continue;
				
				if (concept2.contains("^") || concept2.contains("~"))
					continue;
				
				String directedConcept2 = ">" + concept2;


				int uniquePathNumber = 0;
				
				HashMap<String, Double> paths = getIndirectConceptCoOccurrencePath(directedConcept1, directedConcept2);
				
				if (paths != null){
					
					for (Entry<String, Double> entry: paths.entrySet()){
						
						
						String path = entry.getKey();
						Vector<String> pathList = new Vector<String>();
						
						
						StringTokenizer st = new StringTokenizer(path, ",");
						
						while (st.hasMoreElements()){
							
							String node = st.nextToken();
							
							if (node.startsWith(" "))
								node = node.substring(1, node.length());
	
							pathList.add(node);
							
						}
						
						if (!pathList.elementAt(0).equals(directedConcept1.substring(1, directedConcept1.length()))){
								continue;
						}
						
						
						int[] dimensions = new int[pathList.size() - 1];
						String[][] propertyLabelArrays = new String[pathList.size() - 1][];
						int[][] propertyFrequencyArrays = new int[pathList.size() - 1][];
						
						int spaceSize = 1;
	
						for (int i = 1; i < pathList.size(); i++){
							
							String cur;
							
							if (i == 1)
								cur = pathList.elementAt(i - 1);
							else
								cur = pathList.elementAt(i - 1).substring(1, pathList.elementAt(i - 1).length());
							
							
							String next = pathList.elementAt(i);
							
							if (next.startsWith("<")){
								cur = ">" + cur;
							}else{
								cur = "<" + cur;
							}	
							
							TreeMap<String, Integer> relationFrequencies = getRelationsBetweenDirectConcepts(cur, next);
							
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
						
						int[] indexes = new int[pathList.size() - 1];
						
						int count = 0;
						for (int places = 0; places < spaceSize; places ++){
							
							int dividend = places;
							boolean isTrivial = false;
							
							for (int i = dimensions.length - 1; i >= 0; i--){
								
								int divisor = dimensions[i];
								
								indexes[i] = dividend % divisor; 
								dividend = dividend / divisor; 
								
							}
						
						    for (int i=0; i < indexes.length; i++){
						    	
						    	if (propertyFrequencyArrays[i][indexes[i]] < Configure.PropertyFrequencyThreshold){
						    		isTrivial = true;
						    		break;
						    	}

						    }
						    
						    if (isTrivial)
						    	continue;
						    
						    count ++;
						}

						uniquePathNumber += count;
						
						//System.out.println(pathList);
					}
				
				}
					

				out.println(uniquePathNumber);

			}
		}
		
		out.close();
	}
	
	public void printIndirectPathConnectDegree(String outFileName) throws IOException{
		
		PrintWriter out = null;

		if (outFileName != null)
			out = new PrintWriter(new FileWriter(outFileName, true));

		
		for (String concept1: undirectedConceptFrequencies.keySet()){
			
			if (concept1.equals("Thing"))
				continue;
			
			if (concept1.contains("^") || concept1.contains("~"))
				continue;
			
			String directedConcept1 = "<" + concept1;

			
			for (String concept2: undirectedConceptFrequencies.keySet()){
				
				if (concept2.equals("Thing"))
					continue;
				
				if (concept2.contains("^") || concept2.contains("~"))
					continue;
				
				String directedConcept2 = ">" + concept2;


				int uniquePathNumber = 0;
				
				HashMap<String, Double> paths = getIndirectConceptCoOccurrencePath(directedConcept1, directedConcept2);
				
				if (paths != null){
					
					for (Entry<String, Double> entry: paths.entrySet()){
						
						
						String path = entry.getKey();
						Vector<String> pathList = new Vector<String>();
						
						
						StringTokenizer st = new StringTokenizer(path, ",");
						
						while (st.hasMoreElements()){
							
							String node = st.nextToken();
							
							if (node.startsWith(" "))
								node = node.substring(1, node.length());
	
							pathList.add(node);
							
						}
						
						if (!pathList.elementAt(0).equals(directedConcept1.substring(1, directedConcept1.length()))){
								continue;
						}
						
						
						int[] dimensions = new int[pathList.size() - 1];
						
						int spaceSize = 1;
	
						for (int i = 1; i < pathList.size(); i++){
							
							String cur;
							
							if (i == 1)
								cur = pathList.elementAt(i - 1);
							else
								cur = pathList.elementAt(i - 1).substring(1, pathList.elementAt(i - 1).length());
							
							
							String next = pathList.elementAt(i);
							
							if (next.startsWith("<")){
								cur = ">" + cur;
							}else{
								cur = "<" + cur;
							}	
							
							TreeMap<String, Integer> relationFrequencies = getRelationsBetweenDirectConcepts(cur, next);
							
							dimensions[i-1] = relationFrequencies.size();
							spaceSize = spaceSize * dimensions[i-1];
							
						}
						
						uniquePathNumber += spaceSize;
						
						//System.out.println(pathList);
					}
				
				}
					

				out.println(uniquePathNumber);

			}
		}
		
		out.close();
	}
	
	public void printIndirectClassConnectDegree(String outFileName) throws IOException{
		
		PrintWriter out = null;

		if (outFileName != null)
			out = new PrintWriter(new FileWriter(outFileName, true));

		
		for (String concept1: undirectedConceptFrequencies.keySet()){
			
			if (concept1.equals("Thing"))
				continue;
			
			if (concept1.contains("^") || concept1.contains("~"))
				continue;
			
			String directedConcept1 = "<" + concept1;

			
			for (String concept2: undirectedConceptFrequencies.keySet()){
				
				if (concept2.equals("Thing"))
					continue;
				
				if (concept2.contains("^") || concept2.contains("~"))
					continue;
				
				String directedConcept2 = ">" + concept2;


				int uniquePathNumber = 0;
				
				HashMap<String, Double> paths = getIndirectConceptCoOccurrencePath(directedConcept1, directedConcept2);
				
				if (paths != null){
					
					for (Entry<String, Double> entry: paths.entrySet()){
						
						
						String path = entry.getKey();
						Vector<String> pathList = new Vector<String>();
						
						
						StringTokenizer st = new StringTokenizer(path, ",");
						
						while (st.hasMoreElements()){
							
							String node = st.nextToken();
							
							if (node.startsWith(" "))
								node = node.substring(1, node.length());
	
							pathList.add(node);
							
						}
						
						if (!pathList.elementAt(0).equals(directedConcept1.substring(1, directedConcept1.length()))){
								continue;
						}
						
						
						uniquePathNumber ++;
						
						//System.out.println(pathList);
					}
				
				}
					

				out.println(uniquePathNumber);

			}
		}
		
		out.close();
	}

	/**
	 * @throws Exception 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @param args
	 * @throws IOException 
	 * @throws  
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		/*
		CorrelationModel model = new CorrelationModel();
		model.collectInternalVirtualClasses("/home/lushan1/dbpedia/triple.csv");
		*/
		
		/*
		CorrelationModel model = new CorrelationModel();
		model.populateInternalVirtualClasses("/home/lushan1/dbpedia/triple.csv");
		System.out.println("done!");
		*/
		
		/*
		CorrelationModel model = new CorrelationModel();
		model.processInputDataFile("/home/lushan1/dbpedia/triple.csv");
		CorrelationModel.writeModel("/home/lushan1/dbpedia/all_triples", model);
		System.out.println("Finished!");
		*/
		
		CorrelationModel test2 = CorrelationModel.readModel(Configure.work_dir + "all_triples");
		//CorrelationModel test2 = CorrelationModel.readModel(Configure.work_dir + "latest_triples_NoConstraint_lambda0.4");
		
		//System.out.println("start computing ...");
		//test2.printDirectConnectDegree(Configure.work_dir + "evaluation/output/direct_connect_degree.out");
		//test2.printIndirectClassConnectDegree(Configure.work_dir + "evaluation/output/indirect_class_connect_degree.out");
		//test2.printIndirectPathConnectDegree(Configure.work_dir + "evaluation/output/indirect_path_connect_degree.out");
		//test2.printPrunedIndirectPathConnectDegree(Configure.work_dir + "evaluation/output/pruned_indirect_path_connect_degree.out");
		
		/*
		System.out.println("lamda is " + test2.lambda);
		System.out.println("recreation done!");
		System.out.println("in_universeSize: " + test2.in_universeSize);
		System.out.println("out_universeSize: " + test2.out_universeSize);
		System.out.println("concept_universeSize: " + test2.conceptUniverseSize);


		test2.printPCW(test2.getCorrelatedRelationsSortedByCondiProb("<River", ">Country"), 50);
		test2.printPCW(test2.getRelationsSortedByPMI("<River"), 50);
		test2.printPCW(test2.getRelationsSortedByCondiProb("<River"), 50);

		test2.printPCW(test2.getRelationsSortedByPMI("<Book"), 50);
		test2.printPCW(test2.getRelationsSortedByPMI(">Book"), 50);	 
		test2.printPCW(test2.getRelationsSortedByPMI("<Writer"), 50);	 
		test2.printPCW(test2.getRelationsSortedByPMI(">Writer"), 50);	 
		
		
		test2.printPCW(test2.getConceptsOfRelationSortedByPMI("author"), 50);
		
		test2.printPCW(test2.getDirectConceptsOfConceptSortedByPMI(">Book"), 50);
		test2.printPCW(test2.getIndirectConceptsOfConceptSortedByPMI(">Book"), 200);
		test2.printPCW(test2.getDirectConceptsOfConceptSortedByPMI("<Book"), 200);
		test2.printPCW(test2.getIndirectConceptsOfConceptSortedByPMI("<Book"), 200);
		test2.printPCW(test2.getDirectConceptsOfConceptSortedByPMI("<Writer"), 200);
		test2.printPCW(test2.getIndirectConceptsOfConceptSortedByPMI("<Writer"), 200);
		test2.printPCW(test2.getIndirectConceptsOfConceptSortedByPMI(">Actor"), 200);
		*/
		
		//System.out.println(test2.getConceptRelationPMI(">Bridge", "significantDesign"));
		
		//test2.getConnectedConcept("Publication");

		/*
		for (Entry<String, Integer> entry: test2.undirectedConceptFrequencies.entrySet()){
			if (!entry.getKey().startsWith("^"))
				System.out.println(entry.getKey() + ", " + entry.getValue());
			else if (entry.getValue() >= 1000)
				System.out.println(entry.getKey() + ", " + entry.getValue());
		}
		*/

		//for (Entry<String, Integer> entry: test2.relationFrequencies.entrySet()){
		//	System.out.println(entry.getKey());
		//}
		

		/*
		System.out.println("Concept-Relation matrix ------------------------------------");
		for (Entry<String, TreeMap<String, Integer>> entry: test2.cpt_rel_co_occurring_matrix.entrySet()){
			
			if (entry.getKey().startsWith("<")){
				
				String out;
				boolean trivial = true;
				
				out = entry.getKey() + ": ";
				
				for (Entry<String, Integer> entry2: entry.getValue().entrySet()){
					
						if (entry2.getValue() > 500)
							trivial = false;
						
						out += entry2.getKey() + "(" + entry2.getValue() + ") ";
				}
	
				if (!trivial)
					System.out.println(out);
			}

		}

		System.out.println();
		System.out.println();
		System.out.println();
		
		System.out.println("Concept-Concept matrix ------------------------------------");
		for (Entry<String, TreeMap<String, RelationDimensionElement>> entry: test2.cpt_cpt_co_occurring_matrix.entrySet()){
			
			System.out.print(entry.getKey() + ": ");
			
			for (Entry<String, RelationDimensionElement> entry2: entry.getValue().entrySet()){
				System.out.print(entry2.getKey() + "(" + entry2.getValue().totalFrequency + ") " );
			}

			System.out.println();

		}
		*/
		
		//HashMap<String, Double> traverse = new HashMap<String, Double>();
		//Vector<TraceElement> trace = new Vector<TraceElement>();
		//trace.add(new TraceElement("Article", test2.getUndirectedConceptFrequency("Article")));
		//test2.computeIndirectedCoOccurrenceFromSingleSource(trace, traverse, 0);
		//System.out.println(traverse);
		
		test2.lambda = 1.0;
		test2.in_cpt_cpt_co_occurring_matrix = new TreeMap<String, TreeMap<String, IndirectCoOccurrenceElement>>();
		test2.computeIndirectedCoOccurrence();
		CorrelationModel.writeModel(Configure.work_dir + "latest_triples_NoConstraint_lambda" + test2.lambda, test2);
		
		/*
		System.out.println("Indirected Concept-Concept matrix ------------------------------------");
		for (String concept1: test2.conceptFrequencies.keySet()){
			
			System.out.print(concept1 + ": ");
			test2.printPCW(test2.getDirectConceptsOfConceptSortedByPMI(concept1), 200);
			System.out.print(concept1 + ": ");
			test2.printPCW(test2.getIndirectConceptsOfConceptSortedByPMI(concept1), 200);
		}

		test2.printCoOccurrenceUpdatedByIndirectedCoOccurrence();
		test2.printPCW(test2.getRelationsSortedByPMI(">Country"), 50);
		
		
		System.out.println(test2.getIndirectConceptCoOccurrencePath(">Book", ">Person"));
		System.out.println(test2.getIndirectConceptCoOccurrencePath("<Person", ">Paper"));
		System.out.println(test2.getIndirectConceptCoOccurrencePath(">Country", ">Publisher"));
		System.out.println(test2.getIndirectConceptCoOccurrencePath("<Person", ">Country"));
		*/
	}

}
