package edu.umbc.dblp.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.umbc.configure.Configure;
import edu.umbc.dbpedia.model.QueueElement;
import edu.umbc.dbpedia.model.SimilarityArrayModel;
import edu.umbc.dbpedia.util.ComplexPredicate;
import edu.umbc.dbpedia.util.NounCompound;

public class SimilarityModel {

	MaxentTagger tagger;
	public HashMap<String, NounCompound> classes;
	public HashMap<String, ComplexPredicate> properties;
	
	
	Morphology morph;
	public SimilarityArrayModel conceptSimBox;
	public SimilarityArrayModel relationSimBox;
	HashSet<String> personTypes;
	static double Thing_SIM_LOWER_BOUND = 0.15;
	HashSet<String> internalVirtualClasses = new HashSet<String>();

	
	public SimilarityModel(String path) throws Exception {
		// TODO Auto-generated constructor stub
		//String modelLocation = "/home/lushan1/nlp/model/stanford/left3words-distsim-wsj-0-18.tagger";
		String modelLocation = Configure.model_dir + "stanford/pos-tagger/english-left3words/english-left3words-distsim.tagger";
		//String modelLocation = "/home/lushan1/nlp/model/stanford/pos-tagger/wsj-left3words/wsj-0-18-left3words-distsim.tagger";
		System.out.println("Reading model from file=" + modelLocation);
        tagger = new MaxentTagger(modelLocation);
        morph = new Morphology();
		String rdline;
        
		//conceptSimBox = SimilarityArrayModel.readModel(Configure.model_dir + "BigArray/webbase2012AllW2");
		//relationSimBox = SimilarityArrayModel.readModel(Configure.model_dir + "BigArray/webbase2012AllW5");

		conceptSimBox = SimilarityArrayModel.readModel(Configure.concept_sim_model_loc);
		relationSimBox = SimilarityArrayModel.readModel(Configure.relation_sim_model_loc);
		
		//conceptSimBox = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/ceilling_cut/webbase2012AllW2_S2");
		//relationSimBox = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/ceilling_cut/webbase2012AllW5_S3");
		
		//conceptSimBox = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/webbase2012AllW2_S2");
		//relationSimBox = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/webbase2012AllW5");
		
		//conceptSimBox = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/Gigawords2009AllW2_S2");
		//relationSimBox = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/Gigawords2009AllW5");
		
		//conceptSimBox.setSimilarity("country_NN", "state_NN", (float) 0.45);
		//relationSimBox.setSimilarity("country_NN", "state_NN", (float) 0.50);
		//conceptSimBox.setSimilarity("country_NN", "region_NN", (float) 0.40);
		//relationSimBox.setSimilarity("country_NN", "region_NN", (float) 0.55);

 
        System.out.println("Loading " + Configure.work_dir + " ontology concepts ... ");
		File classFile = new File(Configure.work_dir + "classes.txt");
		BufferedReader classReader = new BufferedReader(new FileReader(classFile));
		
		classes = new HashMap<String, NounCompound>();
		properties = new HashMap<String, ComplexPredicate>();

		while ((rdline = classReader.readLine()) != null){
			NounCompound term = new NounCompound(rdline, tagger, morph, conceptSimBox);
			classes.put(rdline, term);
			
			//ComplexPredicate term2 = new ComplexPredicate(rdline, tagger, morph, relationSimBox);
			//properties.put(rdline, term2);
		}

        System.out.println("Loading " + Configure.work_dir + " ontology properties ... ");
		File propertyFile = new File(Configure.work_dir + "properties.txt");
		BufferedReader propertyReader = new BufferedReader(new FileReader(propertyFile));
		

		while ((rdline = propertyReader.readLine()) != null){
			ComplexPredicate term = new ComplexPredicate(rdline, tagger, morph, relationSimBox);
			properties.put(rdline, term);
		}

		
		personTypes = new HashSet<String>();
		File personFile = new File(path + "PersonTypes.txt");
		BufferedReader personTypesReader = new BufferedReader(new FileReader(personFile));
		
		while ((rdline = personTypesReader.readLine()) != null){
			personTypes.add(rdline);
		}

		personTypesReader.close();
		

		File internalVirtualClassesFile = new File(Configure.work_dir + "InternalVirtualClasses.txt");
		BufferedReader internalVirtualClassesReader = new BufferedReader(new FileReader(internalVirtualClassesFile));

		while ((rdline = internalVirtualClassesReader.readLine()) != null){
			internalVirtualClasses.add(rdline);
		}
		
		internalVirtualClassesReader.close();

		
	}
	
	
	public String getHeaderofNounCompound(String inputConcept){
		
		NounCompound target = new NounCompound(inputConcept, tagger, morph, conceptSimBox);
		String header = target.head;
		return header.substring(0, header.indexOf("_"));
		
	}
	

	public ChoiceElement[] getSimilarConcepts(String inputConcept, int numberOfChoices, boolean allowVirtual, HashMap<String, NounCompound> concepts){
		
		if (inputConcept.equalsIgnoreCase("Thing")){
			
			NounCompound target = new NounCompound(inputConcept, tagger, morph, conceptSimBox);
			concepts.put(inputConcept, target);
			
			ChoiceElement[] out = new ChoiceElement[1];
			out[0] = new ChoiceElement("Thing", 1);
			return out;
		}

		/*
		if (inputConcept.equalsIgnoreCase("Country")){
			
			ChoiceElement[] out = new ChoiceElement[1];
			out[0] = new ChoiceElement("Country", 1);
			return out;
		}
		*/

		HashSet<String> selection = new HashSet<String>();
		
		NounCompound target = new NounCompound(inputConcept, tagger, morph, conceptSimBox);
		
		concepts.put(inputConcept, target);
		
		PriorityQueue<QueueElement> queue = new PriorityQueue<QueueElement>(numberOfChoices);
		
		for (NounCompound compound : classes.values()){
			
			if (!allowVirtual){
				if (compound.isVirtual)
					if (!internalVirtualClasses.contains(compound.ori_phrase))
						continue;
				
			}
			
			//double sim = NounCompound.getSimilarity(target, compound, conceptSimBox);
			double sim = NounCompound.getSimilarity(target, compound, conceptSimBox, relationSimBox);
			
			if (sim > 0){
				if (queue.size() < numberOfChoices){
					queue.add(new QueueElement(compound.ori_phrase, sim));
					selection.add(compound.ori_phrase);
				
				}else{
					if (queue.peek().value < sim){
						queue.remove();
						
						if(!compound.ori_phrase.startsWith("^") || !selection.contains(compound.ori_phrase.substring(1, compound.ori_phrase.length()))){
							queue.add(new QueueElement(compound.ori_phrase, sim));
							selection.add(compound.ori_phrase);
						}else{
							//queue.add(new QueueElement(compound.ori_phrase, sim * 0.50));
							queue.add(new QueueElement(compound.ori_phrase, sim));
							selection.add(compound.ori_phrase);
						}
					}
				}
			}
			
		}
		
		String[] result = new String[queue.size()];
		double[] sims = new double[queue.size()];
		int i = 1;
		
		while (queue.size() > 0){
			
			QueueElement candidate = queue.poll();
			result[result.length - i] = candidate.item;
			sims[sims.length - i] = candidate.value;
			i++;
		}
		
				
		Vector<ChoiceElement> filteredResult = new Vector<ChoiceElement>();
		boolean hasThing;
		
		if (Configure.work_dir.contains("/home/lushan1/dbpedia/native_class/"))
			hasThing = false;
		else
			hasThing = true;

		if (sims.length == 0){
			
			filteredResult.add(new ChoiceElement("Thing", Thing_SIM_LOWER_BOUND));
			return filteredResult.toArray(new ChoiceElement[filteredResult.size()]);
		}

		if (sims[0] == 1 && !result[0].startsWith("^") && target.lengthInContentWords >= 2){
			filteredResult.add(new ChoiceElement(result[0], sims[0]));
		
		}else{

			for (int j = 0; j < result.length; j++){
				
				//if ((sims[j] > sims[0] * 0.5 && sims[j] > 0.10) || sims[j] > 0.2){
				//if ((sims[j] > sims[0] * 0.5 && sims[j] > 0.10) || sims[j] > 0.1){
				//if (sims[j] > 0.10){
				if (sims[j] > NounCompound.HeadSimThreshold){
					
					filteredResult.add(new ChoiceElement(result[j], sims[j]));
					
					if (result[j].equalsIgnoreCase("Thing")){
						hasThing = true;
					}
				}
				
			}
		}
		
		if (!hasThing){
			
			int index = target.head.indexOf("_");
			String word = target.head.substring(0, index);
			
			filteredResult.add(new ChoiceElement("Thing", Thing_SIM_LOWER_BOUND));
		}
		
		ChoiceElement[] results = filteredResult.toArray(new ChoiceElement[filteredResult.size()]);
		
		Arrays.sort(results);
		
		return results;
	
	}
	
	
	public ComplexPredicate convertToComplexPredicate(String input){
		
		return new ComplexPredicate(input, tagger, morph, relationSimBox);
	}
	
	
	public ChoiceElement[] getSimilarProperties(String inputRelation, int numberOfChoices, HashMap<String, ComplexPredicate> relations){
		
		ComplexPredicate target = new ComplexPredicate(inputRelation, tagger, morph, relationSimBox);
		
		relations.put(inputRelation, target);
		
		PriorityQueue<QueueElement> queue = new PriorityQueue<QueueElement>(numberOfChoices);
		
		for (ComplexPredicate predicate : properties.values()){
			
			//double sim = ComplexPredicate.getSimilarity(target, predicate, relationSimBox);
			double sim = ComplexPredicate.getSimilarity(target, predicate, relationSimBox, conceptSimBox);
			
			if (sim > 0){
				if (queue.size() < numberOfChoices){
					queue.add(new QueueElement(predicate.ori_phrase, sim));
				
				}else{
					if (queue.peek().value < sim){
						queue.remove();
						queue.add(new QueueElement(predicate.ori_phrase, sim));
					}
				}
			}
			
		}
		
		String[] result = new String[queue.size()];
		double[] sims = new double[queue.size()];
		int i = 1;
		
		while (queue.size() > 0){
			
			QueueElement candidate = queue.poll();
			result[result.length - i] = candidate.item;
			sims[sims.length - i] = candidate.value;
			i++;
		}
		
		Vector<ChoiceElement> filteredResult = new Vector<ChoiceElement>();
		
		for (int j = 0; j < result.length; j++){
			
			//if ((sims[j] > sims[0] * 0.3 && sims[j] > 0.01) || sims[j] > 0.1){
			if ((sims[j] > sims[0] * 0.3 && sims[j] > 0.01) || sims[j] > 0.05){
				//filteredResult.add(result[j] + " " + sims[j]);
				filteredResult.add(new ChoiceElement(result[j], sims[j]));
			}
			
		}
		
		ChoiceElement[] results = filteredResult.toArray(new ChoiceElement[filteredResult.size()]);
		
		Arrays.sort(results);
		
		return results;

	}

	

	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		SimilarityModel model = new SimilarityModel("");
		
    	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    	String inline;

    	while(true){
        	System.out.println("Please input the target concept");
        	inline = input.readLine();

			ChoiceElement[] candidates = model.getSimilarConcepts(inline, 30, true, new HashMap<String, NounCompound>());
			for (ChoiceElement item : candidates){
				System.out.println(item);
			}

    	}

    	/*
    	while(true){
        	System.out.println("Please input the target predicate");
        	inline = input.readLine();

        	ChoiceElement[] candidates = model.getSimilarProperties(inline, 50);
			for (ChoiceElement item : candidates){
				System.out.println(item);
			}
			
    	}
    	*/
	}

}
