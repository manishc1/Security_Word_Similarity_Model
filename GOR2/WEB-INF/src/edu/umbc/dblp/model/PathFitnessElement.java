package edu.umbc.dblp.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

import edu.umbc.configure.Configure;
import edu.umbc.dbpedia.model.SimilarityArrayModel;
import edu.umbc.dbpedia.util.ComplexPredicate;
import edu.umbc.dbpedia.util.NounCompound;


public class PathFitnessElement implements Comparable<PathFitnessElement>{

	public int length;

	public String[] classLabels;
	
	public String[] propertyLabels;
	public double[] propertyProbabilities;
	public int[] propertyDirections;         // value 0 and 1
	
	public double nodePathCoOccurrence;
	
	public String pathLabel;
	
	public String subjectLabel;
	public double subjectSim;
	
	public String objectLabel;
	public double objectSim;
	boolean isAttribute;
	
	public double alpha;
	public double universe_offset;
	public double uneven_match_parameter;
	double singleStrongConceptJump_threshold = 0.75; 

	
	// need to compute
	public double pathCoOccurrence;
	public double pathSim;
	public double fitness;
	
	public SimilarityModel simModel;
	
	public boolean replaced;
	int replacedClassIndex;
	public PathFitnessElement origin;

	
	public PathFitnessElement(SimilarityModel simModel, int length, double nodePathCoOccurrence, String pathLabel, String subjectLabel, double subjectSim, String objectLabel, double objectSim, boolean isAttribute, String[] classLabels, String[] propertyLabels,
			double[] propertyProbabilities, int[] propertyDirections, double alpha, double universe_offset, double uneven_match_parameter) {
		// TODO Auto-generated constructor stub
		
		this.simModel = simModel;
		this.length = length;
		this.nodePathCoOccurrence = nodePathCoOccurrence;
		this.pathLabel = pathLabel;
		this.subjectLabel = subjectLabel;
		this.subjectSim = subjectSim;
		this.objectLabel = objectLabel;
		this.objectSim = objectSim;
		this.isAttribute = isAttribute;
		
		this.classLabels = classLabels;
		this.propertyLabels = propertyLabels;
		this.propertyProbabilities = propertyProbabilities;
		this.propertyDirections = propertyDirections;
		
		this.alpha = alpha;
		this.universe_offset = universe_offset;
		this.uneven_match_parameter = uneven_match_parameter;
		
		replaced = false;
		replacedClassIndex = -1;
		this.origin = null;
	}
	
	public PathFitnessElement(PathFitnessElement origin, String[] classLabels, String[] propertyLabels, int[] propertyDirections, int replacedClassIndex) {
		// TODO Auto-generated constructor stub
		
		this.simModel = origin.simModel;
		this.length = 0;
		this.nodePathCoOccurrence = 0;
		this.pathLabel = origin.pathLabel;
		this.subjectLabel = origin.subjectLabel;
		this.subjectSim = origin.subjectSim;
		this.objectLabel = origin.objectLabel;
		this.objectSim = origin.objectSim;
		this.isAttribute = origin.isAttribute;
		this.fitness = origin.fitness;
		this.alpha = origin.alpha;
		this.universe_offset = origin.universe_offset;
		
		this.classLabels = classLabels;
		this.propertyLabels = propertyLabels;
		this.propertyProbabilities = null;
		this.propertyDirections = propertyDirections;
		
		this.replaced = true;
		this.replacedClassIndex = replacedClassIndex;
		this.origin = origin;
	}

	
	

	public double computeFitness_split_conceptLabel_propertyOnly_combineEnds_weights_singleStrongConceptJump_defaultRelation(HashMap<String, NounCompound> concepts, HashMap<String, ComplexPredicate> relations, 
			HashMap<String, Double> termSimPool, HashMap<String, Double> pathSimPool){
		
		
		pathCoOccurrence = nodePathCoOccurrence;
			
		for (int i=0; i < length; i++){
			pathCoOccurrence = pathCoOccurrence * propertyProbabilities[i];
		}
		
		
		//String pathSignature = subjectLabel + "," + pathLabel + "," + objectLabel + "|" + classLabels[0] + "," + classLabels[classLabels.length - 1] + "," + Arrays.toString(propertyLabels) + Arrays.toString(propertyDirections);
		String pathSignature = Arrays.toString(propertyLabels) + Arrays.toString(propertyDirections);
		
		Double savedPathSim;
		
		savedPathSim = pathSimPool.get(pathSignature);

		if (savedPathSim == null){
		
			ComplexPredicate pathPredicate = relations.get(pathLabel);
			NounCompound subType = concepts.get(subjectLabel);
			NounCompound objType = concepts.get(objectLabel);
			
			String filteredPathLabel = "";
			int headLocPathLabel = pathPredicate.head_loc;
			boolean changed = false;
			
			ArrayList<String> pathWordList = new ArrayList<String>();
			
			for (int i=0; i < pathPredicate.componentWords.length; i++){
	
				/*
				if (i==pathPredicate.componentWords.length-1 && pathPredicate.componentWords.length > 1){
					
					if (subType.lengthInContentWords == 1 && pathPredicate.componentWords[i].equalsIgnoreCase(subType.componentWords[0]) ){
						changed = true;
						continue;
					}
					
					if (objType.lengthInContentWords == 1 && pathPredicate.componentWords[i].equalsIgnoreCase(objType.componentWords[0]) ){
						changed = true;
						continue;
					}
				}
				*/
	
				pathWordList.add(pathPredicate.componentWords[i]);
				
				filteredPathLabel = filteredPathLabel + pathPredicate.componentWords[i] + " ";
			}
			
			if (classLabels[classLabels.length - 1].equals("Thing") && !objectLabel.equals("") && !objectLabel.equals("Thing")){
				pathWordList.addAll(Arrays.asList(objType.componentWords));
				filteredPathLabel = filteredPathLabel + objectLabel;
				changed = true;
			}
			
			if (changed){
				filteredPathLabel = filteredPathLabel.trim();
				headLocPathLabel = -1;
			}else
				filteredPathLabel = pathLabel;
			
			
			String[] pathWords = new String[pathWordList.size()];
			pathWordList.toArray(pathWords);
			
			
			double[] simSub = new double[propertyLabels.length * 2 + 1];
			double[] simObj = new double[propertyLabels.length * 2 + 1];
			double[] simRel = new double[propertyLabels.length * 2 + 1];
			String[] labels = new String[propertyLabels.length * 2 + 1];
			
			int maxRelSimNo = 0;
			double maxRelSim = 0;
			
			int maxSubSimNo = 0;
			double maxSubSim = 0;
			
			int maxObjSimNo = 0;
			double maxObjSim = 0;
			
			for (int i = 0; i < propertyLabels.length; i++){
	
				ComplexPredicate propertyTerm = simModel.properties.get(propertyLabels[i]);
				boolean isDefaultRelation = false;
				
				if (propertyTerm.lengthInContentWords == 0){
					
					NounCompound classTerm;
					
					if (propertyDirections[i] == 0)
						classTerm = simModel.classes.get(classLabels[i + 1]);
					else
						classTerm = simModel.classes.get(classLabels[i]);
	
					propertyTerm = new ComplexPredicate(classTerm);
					isDefaultRelation = true;
				}
				
				Double savedSim;
				
				savedSim = termSimPool.get(subType.ori_phrase + "|" + propertyTerm.ori_phrase);
				if (savedSim != null)
					simSub[2*i + 1] = savedSim;
				else{
					simSub[2*i + 1] = getSimilarity(subType.componentWords, subType.ori_phrase, subType.head_loc, propertyTerm.componentWords, propertyTerm.ori_phrase, propertyTerm.head_loc, simModel.relationSimBox, simModel.conceptSimBox);
					termSimPool.put(subType.ori_phrase + "|" + propertyTerm.ori_phrase, simSub[2*i + 1]);
				}
	
				//if (isDefaultRelation) 
				//	simSub[2*i + 1] = simSub[2*i + 1] / 2;
	
				savedSim = termSimPool.get(objType.ori_phrase + "|" + propertyTerm.ori_phrase);
				if (savedSim != null)
					simObj[2*i + 1] = savedSim;
				else{
					simObj[2*i + 1] = getSimilarity(objType.componentWords, objType.ori_phrase, objType.head_loc, propertyTerm.componentWords, propertyTerm.ori_phrase, propertyTerm.head_loc, simModel.relationSimBox, simModel.conceptSimBox);
					termSimPool.put(objType.ori_phrase + "|" + propertyTerm.ori_phrase, simObj[2*i + 1]);
				}
					
				//if (isDefaultRelation) 
				//	simObj[2*i + 1] = simObj[2*i + 1] / 2;
				
				savedSim = termSimPool.get(filteredPathLabel + "|" + propertyTerm.ori_phrase);
				if (savedSim != null)
					simRel[2*i + 1] = savedSim;
				else{
					simRel[2*i + 1] = getSimilarity(pathWords, filteredPathLabel, headLocPathLabel, propertyTerm.componentWords, propertyTerm.ori_phrase, propertyTerm.head_loc, simModel.relationSimBox, simModel.conceptSimBox);
					termSimPool.put(filteredPathLabel + "|" + propertyTerm.ori_phrase, simRel[2*i + 1]);
				}
				
				//if (isDefaultRelation)
				//	simRel[2*i + 1] = simRel[2*i + 1] / 2;
				
				labels[2*i + 1] = propertyLabels[i];
				
				// deal with directions
				if (propertyLabels[i].startsWith("@") && propertyDirections[i] == 1){
					pathSim = 0;
					fitness = 0;
					return fitness;
				}
				
				if (pathPredicate.lengthInContentWords == 1 && pathWords.length == 1 && propertyTerm.lengthInContentWords == 1 && propertyTerm.lengthInAllWords == 1){
					
					if (pathWords[0].endsWith("_VB") && propertyTerm.componentWords[0].endsWith("_VB")){
						
						if (simRel[2*i + 1] > 0.75 && propertyDirections[i] == 1 && !pathLabel.toLowerCase().endsWith(" by")){
							pathSim = 0;
							fitness = 0;
							return fitness;
						}
	
						if (simRel[2*i + 1] > 0.75 && propertyDirections[i] == 0 && pathLabel.toLowerCase().endsWith(" by")){
							pathSim = 0;
							fitness = 0;
							return fitness;
						}
	
						
					}else if (pathWords[0].endsWith("_VB") && propertyTerm.componentWords[0].endsWith("_NN")){
						
						String noun = propertyTerm.componentWords[0].substring(0, propertyTerm.componentWords[0].length() - 3).toLowerCase();
						String verb = pathWords[0].substring(0, pathWords[0].length() - 3).toLowerCase();
						
						if (noun.equals(verb + "er") || 
								(verb.endsWith("e") && (noun.equals(verb + "r") || noun.equals(verb.substring(0, verb.length() - 1) + "or") ))){
							
							if (propertyDirections[i] == 0 && !pathLabel.toLowerCase().endsWith(" by")){
								pathSim = 0;
								fitness = 0;
								return fitness;
							}
							
							if (propertyDirections[i] == 1 && pathLabel.toLowerCase().endsWith(" by")){
								pathSim = 0;
								fitness = 0;
								return fitness;
							}
						}
						
					}else if (pathWords[0].endsWith("_NN") && pathPredicate.lengthInAllWords == 1 && propertyTerm.componentWords[0].endsWith("_VB")){
	
						String verb = propertyTerm.componentWords[0].substring(0, propertyTerm.componentWords[0].length() - 3).toLowerCase();
						String noun = pathWords[0].substring(0, pathWords[0].length() - 3).toLowerCase();
						
						if (noun.equals(verb + "er") || 
								(verb.endsWith("e") && (noun.equals(verb + "r") || noun.equals(verb.substring(0, verb.length() - 1) + "or") ))){
							
							if (propertyDirections[i] == 0){
								pathSim = 0;
								fitness = 0;
								return fitness;
							}
						}
					
					/*	
					} else if (pathWords[0].endsWith("_NN") && pathPredicate.lengthInAllWords == 1 && propertyTerm.componentWords[0].endsWith("_NN")){
	
						if (pathWords[0].equals(propertyTerm.componentWords[0]) && propertyDirections[i] == 1){
							pathSim = 0;
							fitness = 0;
							return fitness;
							//simRel[2*i + 1] = simRel[2*i + 1] / 2;
						}
					*/
						
					}
				}
				
				
				
				if (maxRelSim < simRel[2*i + 1]){
					maxRelSim = simRel[2*i + 1];
					maxRelSimNo = 2 * i + 1;
				}
				
				if (maxSubSim < simSub[2*i + 1]){
					maxSubSim = simSub[2*i + 1];
					maxSubSimNo = 2 * i + 1;
				}
	
				if (maxObjSim < simObj[2*i + 1]){
					maxObjSim = simObj[2*i + 1];
					maxObjSimNo = 2 * i + 1;
				}
	
			}
			
	
			for (int i = 0; i < classLabels.length; i++){
	
				labels[2*i] = classLabels[i];
	
				if (i == 0 || i == classLabels.length -1){
					
					NounCompound classTerm = simModel.classes.get(classLabels[i]);
	
					Double savedSim;
					
					savedSim = termSimPool.get(subType.ori_phrase + "|" + classTerm.ori_phrase);
					if (savedSim != null)
						simSub[2*i] = savedSim;
					else{
						simSub[2*i] = getSimilarity(subType.componentWords, subType.ori_phrase, subType.head_loc, classTerm.componentWords, classTerm.ori_phrase, classTerm.head_loc, simModel.relationSimBox, simModel.conceptSimBox);
						termSimPool.put(subType.ori_phrase + "|" + classTerm.ori_phrase, simSub[2*i]);
					}
					
					savedSim = termSimPool.get(objType.ori_phrase + "|" + classTerm.ori_phrase);
					if (savedSim != null)
						simObj[2*i] = savedSim;
					else{
						simObj[2*i] = getSimilarity(objType.componentWords, objType.ori_phrase, objType.head_loc, classTerm.componentWords, classTerm.ori_phrase, classTerm.head_loc, simModel.relationSimBox, simModel.conceptSimBox);
						termSimPool.put(objType.ori_phrase + "|" + classTerm.ori_phrase, simObj[2*i]);
					}
					
					savedSim = termSimPool.get(filteredPathLabel + "|" + classTerm.ori_phrase);
					if (savedSim != null)
						simRel[2*i] = savedSim;
					else{
						simRel[2*i] = getSimilarity(pathWords, filteredPathLabel, headLocPathLabel, classTerm.componentWords, classTerm.ori_phrase, classTerm.head_loc, simModel.relationSimBox, simModel.conceptSimBox);
						termSimPool.put(filteredPathLabel + "|" + classTerm.ori_phrase, simRel[2*i]);
					}	
					
					if (maxRelSim < simRel[2*i]){
						maxRelSim = simRel[2*i];
						maxRelSimNo = 2 * i;
					}
					
					if (maxSubSim < simSub[2*i]){
						maxSubSim = simSub[2*i];
						maxSubSimNo = 2 * i;
					}
		
					if (maxObjSim < simObj[2*i]){
						maxObjSim = simObj[2*i];
						maxObjSimNo = 2 * i;
					}
				}
				
			}
			
			if (pathWords.length == 0){
		
				double pathSubSim = 0;
				double pathObjSim = 0;
	
				if (!isAttribute){
				
					pathSubSim = calculatePathSim(simSub, simObj, simSub, maxSubSimNo, maxSubSim);
					pathObjSim = calculatePathSim(simSub, simObj, simObj, maxObjSimNo, maxObjSim);
					
					/*
					// method No. 1
					if (pathSubSim > pathObjSim)
						pathSim = pathSubSim;
					else
						pathSim = pathObjSim;
	
					// method No. 2
					if (objectSim <= 0.20 && subjectSim > 0.20)
						pathSim = pathObjSim;
					else if (subjectSim <= 0.20 && objectSim > 0.20)
						pathSim = pathSubSim;
					else if (pathSubSim > pathObjSim)
						pathSim = pathSubSim;
					else
						pathSim = pathObjSim;
					*/
					
					
					// methold No. 3
					
					if (pathSubSim > pathObjSim){
						
						double ratio = (objectSim / subjectSim);
						
						if (ratio >= 1.0) 
							ratio = 1.0;
						else
							ratio = Math.pow(ratio, uneven_match_parameter);
						
						pathSim = pathObjSim + (pathSubSim - pathObjSim) * ratio;
						
					}else if (pathSubSim < pathObjSim){
						
						double ratio = (subjectSim / objectSim);
						
						if (ratio >= 1.0) 
							ratio = 1.0;
						else
							ratio = Math.pow(ratio, uneven_match_parameter);
	
						
						pathSim = pathSubSim + (pathObjSim - pathSubSim) * ratio;
						
					}else
						pathSim = pathSubSim;
					
					
					// method No. 5
					/*
					if (objectSim > subjectSim){
						
						double ratio = (subjectSim / objectSim);
						
						if (pathObjSim * ratio > pathSubSim){
							pathSim = pathObjSim;
						}else
							pathSim = pathSubSim;
						
					}else if (subjectSim > objectSim){
						
						double ratio = (objectSim / subjectSim);
						
						if (pathSubSim * ratio > pathObjSim){
							pathSim = pathSubSim;
						}else
							pathSim = pathObjSim;
						
					}else{
						if (pathSubSim > pathObjSim)
							pathSim = pathSubSim;
						else
							pathSim = pathObjSim;
					}
					*/
	 
					
				}else{
					
					pathSim = calculatePathSim(simSub, simObj, simObj, maxObjSimNo, maxObjSim);
				}
				
			}else{
				
				if (maxRelSimNo < simRel.length - 2 && simRel[maxRelSimNo] == simRel[maxRelSimNo + 2]){
					if (simSub[maxRelSimNo] > simObj[maxRelSimNo + 2])
						maxRelSimNo = maxRelSimNo + 2;
				}
				
				pathSim = calculatePathSim(simSub, simObj, simRel, maxRelSimNo, maxRelSim);
			}
		
			pathSimPool.put(pathSignature, pathSim);
		
		} else{
			pathSim = savedPathSim;
		}
		
		if (Configure.work_dir.contains("/home/lushan1/dbpedia/"))
			fitness = subjectSim * objectSim * pathSim * (Math.log(pathCoOccurrence) + universe_offset);
			//fitness = subjectSim * objectSim * pathSim;
			//fitness = subjectSim * objectSim * (Math.log(pathCoOccurrence) + universe_offset);
		else
			fitness = subjectSim * objectSim * pathSim * (Math.log(pathCoOccurrence) + universe_offset);
			//fitness = subjectSim * objectSim * pathSim;
			//fitness = subjectSim * objectSim * (Math.log(pathCoOccurrence) + universe_offset);
		
		if (propertyLabels.length == 1){
			if (propertyDirections[0] == 1)
				fitness -= 0.0001;
		}
		
		return fitness;
		
	}
	
	
	public double calculatePathSim(double[] simSub, double[] simObj, double[] simRel, int maxRelSimNo, double maxRelSim){
		
		double pathSim = 1.0; 
		double relationHighestSim = 0.0;
		
		/*
		boolean subSameAsRel = true;
		boolean objSameAsRel = true;
		
		for (int i = 0; i < simRel.length; i++){
			if (simSub[i] != simRel[i]){
				subSameAsRel = false;
				break;
			}
		}
		
		for (int i = 0; i < simRel.length; i++){
			if (simObj[i] != simRel[i]){
				objSameAsRel = false;
				break;
			}
		}
		*/

		int subSplitNo = maxRelSimNo - 1;
		int objSplitNo = maxRelSimNo + 1;
		
		while (subSplitNo >= 0){
			
			// ignore class nodes in the path, except for two ends.
			if (subSplitNo % 2 == 0 && subSplitNo != 0){
				subSplitNo --;
				continue;
			}
			
			//if (simRel[subSplitNo] >= simSub[subSplitNo] || (subSplitNo % 2 == 1 && subSplitNo < simRel.length - 2 && simRel[subSplitNo] == simRel[subSplitNo + 2]))
			if (simRel[subSplitNo] >= simSub[subSplitNo])
				subSplitNo --;
			else if (subSplitNo == simRel.length - 2 && simRel[subSplitNo] > simSub[subSplitNo] * maxRelSim)
				subSplitNo --;
			else
				break;
		}
		
		while (objSplitNo <= simRel.length - 1){
			
			// ignore class nodes in the path, except for two ends.
			if (objSplitNo % 2 == 0 && objSplitNo != simRel.length - 1){
				objSplitNo ++;
				continue;
			}
			
			//if (simRel[objSplitNo] >= simObj[objSplitNo] || (objSplitNo % 2 == 1 && objSplitNo > 1 && simRel[objSplitNo] == simRel[objSplitNo - 2]))
			if (simRel[objSplitNo] >= simObj[objSplitNo])
				objSplitNo ++;
			else if (objSplitNo == 1 && simRel[objSplitNo] > simObj[objSplitNo] * maxRelSim)
				objSplitNo ++;
			else
				break;
		}
	
		int pathSim_subCount = 0;
		int pathSim_objCount = 0;

		for (int i=0; i < simRel.length; i++){
			
			if (i % 2 == 1){

				if (i <= subSplitNo){
					
						pathSim *= simSub[i];
						
						//if (simSub[i] < relationHighestSim)
						//	relationHighestSim = simSub[i];
						
						if (simSub[i] >= singleStrongConceptJump_threshold)
							pathSim_subCount ++;
						
						if (i >= 3 && iSOverSimilar(propertyLabels[i / 2], propertyLabels[i / 2 - 1])){
							pathSim = 0;
							break;
						}
				
				}else if (i >= objSplitNo){
					
						pathSim *= simObj[i];
						
						//if (simObj[i] < relationHighestSim)
						//	relationHighestSim = simObj[i];
						
						if (simObj[i] >= singleStrongConceptJump_threshold)
							pathSim_objCount ++;
						
						if (i > objSplitNo + 1 && iSOverSimilar(propertyLabels[i / 2], propertyLabels[i / 2 - 1])){
							pathSim = 0;
							break;
						}
						
				
				}else{
						pathSim *= simRel[i];
						
						if (simRel[i] > relationHighestSim)
							relationHighestSim = simRel[i];

						/*
						if (i > subSplitNo + 2 && iSOverSimilar(propertyLabels[i / 2], propertyLabels[i / 2 - 1]) 
								&& (subSameAsRel || objSameAsRel)){
							pathSim = 0;
							break;
						}
						*/
						
						if (i > subSplitNo + 2 && iSOverSimilar(propertyLabels[i / 2], propertyLabels[i / 2 - 1]) 
								&& (propertyDirections[i / 2] == propertyDirections[i / 2 - 1] || propertyLabels.length % 2 == 1)){
							pathSim = 0;
							break;
						}
						
						/*
						if (i > subSplitNo + 4 && iSOverSimilar(propertyLabels[i / 2], propertyLabels[i / 2 - 1]) && iSOverSimilar(propertyLabels[i / 2], propertyLabels[i / 2 - 2])){
							pathSim = 0;
							break;
						}
						*/

						if (i > subSplitNo + 4 && iSOverSimilar(propertyLabels[i / 2], propertyLabels[i / 2 - 2]) && (propertyDirections[i / 2] == propertyDirections[i / 2 - 2])){
							pathSim = 0;
							break;
						}

						
				}
			}
			
			if (i == 0 && maxRelSimNo == 0 && objSplitNo == maxRelSimNo + 1){
				pathSim *= simRel[i];
				
				if (simRel[i] > relationHighestSim)
					relationHighestSim = simRel[i];
			}
			
			if (i == simRel.length - 1 && maxRelSimNo == simRel.length - 1 && subSplitNo == maxRelSimNo - 1){
				pathSim *= simRel[i];
				
				if (simRel[i] > relationHighestSim)
					relationHighestSim = simRel[i];
			}
			
		}
		
		if (pathSim_subCount > 1 || pathSim_objCount > 1)
			pathSim = 0;
			
		
		
		double power = 1.0 / (1 + (propertyLabels.length - 1) * alpha);
		pathSim = Math.pow(pathSim, power);
		
		
		if (pathSim > relationHighestSim)
			pathSim = relationHighestSim;
		
		return pathSim;
		
	}

	
	public boolean iSOverSimilar(String str1, String str2){
		
		str1 = str1.toLowerCase();
		str2 = str2.toLowerCase();
		
		if ((str1.endsWith(str2) && str2.length() >= 5) || (str2.endsWith(str1) && str1.length() >= 5))
			return true;
		else
			return false;
		
	}
	
	
	public static double getSimilarity(String[] target, String target_phrase, int targetHeadLoc, String[] compound, String compound_phrase, int compoundHeadLoc, SimilarityArrayModel relationModel,  SimilarityArrayModel conceptModel){
		
		if (target.length == 0 || compound.length == 0)
			return Double.NEGATIVE_INFINITY;
		
		/*
		if ((target_phrase.equalsIgnoreCase("In" + compound_phrase) && compound_phrase.length() >= 4) 
				|| (compound_phrase.equalsIgnoreCase("In" + target_phrase) && target_phrase.length() >= 4))
			return 0.5;
		*/	

		double maxSimTarget = 0;
		
		for (int i=0; i<target.length; i++){
			
			double localMaxSim = -1.0;
			for (int j=0; j<compound.length; j++){
				
				double localSim = relationModel.getSimilarity(target[i], compound[j]);
			
				if (localSim > localMaxSim)
					localMaxSim = localSim;
			}
			
			if (localMaxSim == 0){ 
				if (targetHeadLoc != -1 && i != targetHeadLoc && target[i].endsWith("_NN")  && relationModel.getFrequency(target[i]) != 0)
					localMaxSim = ComplexPredicate.NON_SIMILAR_PENALTY;
				
				if (i == 0 && target[i].endsWith("_JJS") && !compound[0].endsWith("_JJS"))
					localMaxSim = ComplexPredicate.NON_SIMILAR_PENALTY;

			}
			
			maxSimTarget += localMaxSim;
			
		}

		double maxSimCompound = 0;
		
		for (int i=0; i<compound.length; i++){
			
			double localMaxSim = -1.0;
			for (int j=0; j<target.length; j++){
				
				double localSim = relationModel.getSimilarity(compound[i], target[j]);
			
				if (localSim > localMaxSim)
					localMaxSim = localSim;
			}
			
			
			if (localMaxSim == 0){ 
				if (compoundHeadLoc != -1 && i != compoundHeadLoc && compound[i].endsWith("_NN") && relationModel.getFrequency(compound[i]) != 0)
					localMaxSim = ComplexPredicate.NON_SIMILAR_PENALTY;
				
				if (i == 0 && compound[i].endsWith("_JJS") && !target[0].endsWith("_JJS"))
					localMaxSim = ComplexPredicate.NON_SIMILAR_PENALTY;

			}
			
			maxSimCompound += localMaxSim;
			
		}

		double sim;
		
		//return normalizedSim;
		if (target.length >= compound.length)
			sim = maxSimTarget / target.length * (1 - ComplexPredicate.ShortTermWeight) + maxSimCompound / compound.length * ComplexPredicate.ShortTermWeight;
		else
			sim = maxSimTarget / target.length * ComplexPredicate.ShortTermWeight + maxSimCompound / compound.length * (1 - ComplexPredicate.ShortTermWeight);

		/*
		double compoundSim = maxSimCompound / compound.length;
		double targetSim = maxSimTarget / target.length;
		
		if (compoundSim > targetSim)
			sim = targetSim;
		else
			sim = compoundSim;
		*/
		
		if (sim > 0)
			return sim;
		else
			return 0;

	}

	public String display() {
		
		String result = "";
		
		for (int i = 0; i < classLabels.length; i++){
			
			String direction = "";
			
			if (i > 0){
				if (propertyDirections[i - 1] == 0)
					direction = " > ";
				else
					direction = " < ";
			}                   

			result = result + direction + classLabels[i];
		}
		
		result = "[" + result + "]; " + Arrays.toString(propertyLabels) + "; " + String.format("%1.2f", fitness);
			
		
		return result;
	}
	
	
	public String signature() {
		
		String result = "";
		
		for (int i = 0; i < classLabels.length; i++){
			
			String direction = "";
			
			if (i > 0){
				if (propertyDirections[i - 1] == 0)
					direction = " > ";
				else
					direction = " < ";
			}                   
			
			if (i != replacedClassIndex)
				result = result + direction + classLabels[i];
			else
				result = result + direction + classLabels[i] + "%";
		}
		
		result = "[" + result + "]; " + Arrays.toString(propertyLabels);

		/*
		if (!replaced)
			result = "[" + result + "]; " + Arrays.toString(propertyLabels);
		else
			result = "[" + result + "]; " + Arrays.toString(propertyLabels) + "%";
		*/	
		
		return result;
	}

	
	public String toString() {
		
		String result = "";
		
		for (int i = 0; i < classLabels.length; i++){
			
			String direction = "";
			
			if (i > 0){
				if (propertyDirections[i - 1] == 0)
					direction = " > ";
				else
					direction = " < ";
			}                   

			result = result + direction + classLabels[i];
		}
		
		return "[" + result + "]; " + Arrays.toString(propertyLabels) + "; " + String.format("%1.2f", fitness) + "; " + String.format("%1.2f", pathSim) + "; " + String.format("%1.2f", Math.log(pathCoOccurrence));
	}
	
	public int compareTo(PathFitnessElement o) {
		// TODO Auto-generated method stub
		//return (int) Math.signum((this.fitness - o.fitness));
		return (int) Math.signum((o.fitness - this.fitness));
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
