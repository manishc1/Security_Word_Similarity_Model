package edu.umbc.wordSimilarity2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.Morphology;

public class SimilarityAnalysisWithPOS {
	
	public CoOccurModelByArrays model1;
	public CoOccurModelByArrays model2;
	public PPMIModelByArrays pmi_model;
	public Morphology morpha;
	public float BASE_PMI = (float) 3.0;
	public boolean REMOVE_NON_FREQUENT_WORDS = true;
	public int WINDOW_THRESHOLD = 100;
	//public static float CORPUS_PMI_DELTA = 1;
	public int numberOfCombinedPMI = 0;
	public String testFileName = "toefl_pos.tst";

	public SimilarityAnalysisWithPOS(String modelName, boolean isPPMIModel) {
		// TODO Auto-generated constructor stub
		morpha = new Morphology();
		
		if (!isPPMIModel)
			model1 = new CoOccurModelByArrays(modelName, false);
		else
			pmi_model = new PPMIModelByArrays(modelName, false);
	}

	public SimilarityAnalysisWithPOS(String modelName1, String modelName2, boolean isPPMIModel) {
		// TODO Auto-generated constructor stub
		morpha = new Morphology();
		model1 = new CoOccurModelByArrays(modelName1, false);
		
		if (!isPPMIModel)
			model2 = new CoOccurModelByArrays(modelName2, false);
		else
			pmi_model = new PPMIModelByArrays(modelName2, false);
	}
	
	public float combinedPMI(float PMI_LargerW, float PMI_SmallW){
		
		if (PMI_SmallW < BASE_PMI) 
			PMI_SmallW = BASE_PMI;
		
		return (float) (PMI_LargerW + (PMI_LargerW - PMI_SmallW));
		//return (float) (PMI_LargerW*(PMI_LargerW - PMI_SmallW));
	}
	
	public float deltaPMI(float PMI_LargerW, float PMI_SmallW){
		
		float base = (float) BASE_PMI;
		
		if (PMI_SmallW < base)
			return PMI_LargerW - base;
		else
			return PMI_LargerW - PMI_SmallW;
	}

	public FloatElement[] getSortedDivides(int[] vectorInWindow1, int[] vectorInWindow2, String[] vocabulary){

		//FloatElement[] compares = new FloatElement[vocabulary.length];
		
		Vector<FloatElement> compares = new Vector<FloatElement>();
		
		for (int i=0; i<vocabulary.length; i++){
			
			FloatElement element;
			
			if (vectorInWindow1[i] != 0 && vectorInWindow2[i] != 0){
				
				element = new FloatElement(vocabulary[i],(float) vectorInWindow1[i] / (float) vectorInWindow2[i]);
				compares.add(element);
			}
		}
		
		Collections.sort(compares);
		
		return compares.toArray(new FloatElement[compares.size()]);
		
	}

	
	public FloatElement[] getSortedDivides(String word){

		int[] vector1 = model1.getVector(word);
		int[] vector2 = model2.getVector(word);
		
		return getSortedDivides(vector1, vector2, model1.vocabulary);
	}
	

	public FloatElement[] geInversetSortedDivides(int[] vectorInWindow1, int[] vectorInWindow2, String[] vocabulary){

		//FloatElement[] compares = new FloatElement[vocabulary.length];
		
		Vector<FloatElement> compares = new Vector<FloatElement>();
		
		for (int i=0; i<vocabulary.length; i++){
			
			FloatElement element;
			
			if (vectorInWindow1[i] != 0 && vectorInWindow2[i] != 0){
				
				element = new FloatElement(vocabulary[i],(float) vectorInWindow2[i] / (float) (vectorInWindow1[i] - vectorInWindow2[i] + 1));
				compares.add(element);
			}
		}
		
		Collections.sort(compares);
		
		return compares.toArray(new FloatElement[compares.size()]);
		
	}

	
	public FloatElement[] geInversetSortedDivides(String word){

		int[] vector1 = model1.getVector(word);
		int[] vector2 = model2.getVector(word);
		
		return geInversetSortedDivides(vector1, vector2, model1.vocabulary);
	}
	
	
	public FloatElement[] getSortedWordsByDeltaWindow(String word, int threshold) {
		
		FloatElement[] sortedWordsFromBoth;
		FloatElement[] sortedWordsFromLatter;
		FloatElement[] output = new FloatElement[threshold];
		HashMap<String, FloatElement> comparedWords = new HashMap<String, FloatElement>();
		
		
		sortedWordsFromBoth = model1.getSortedPMIWordsWithSamePOS(word);
		sortedWordsFromLatter = model2.getSortedPMIWordsWithSamePOS(word);
		
		for (int i=0; i<threshold; i++){
			output[i] = sortedWordsFromLatter[i];
		}
		
		for (int i=0; i<threshold; i++){
			comparedWords.put(sortedWordsFromBoth[i].word, sortedWordsFromBoth[i]);
		}
		
		
		for (int i=0; i<threshold; i++){
			
			String key = output[i].word;
			FloatElement correspondWord = comparedWords.get(key);
			if (correspondWord == null)
				output[i].value = Float.NEGATIVE_INFINITY;
			else
				output[i].value -= 10 * (correspondWord.value - output[i].value);  
		}
		
		Arrays.sort(output);
		
		return output;
		
	}

	public FloatElement[] getSortedSimilarWords(String word) throws Exception{
		
		int index = model1.index(word);
		FloatElement[] similarWords = new FloatElement[model1.sizeOfVocabulary];
		
		for (int i=0; i<model1.sizeOfVocabulary; i++){
			
			float pmi1 = 0;
			float pmi2 = 0;
			float similarity = Float.NEGATIVE_INFINITY;
			
			try {
				pmi2 = model2.getPMI(index, i);
				//if (pmi2 < BASE_PMI) pmi2 = BASE_PMI;
			
			} catch (Exception e) {

				// if not exist in the smaller window
				try {
					pmi1 = model1.getPMI(index, i);
					
					if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(i) < model1.FREQUENCY_LIMIT) 
						similarity = 0; // exclude rarely appearing words
					else{
						//similarity = 0;
						similarity = combinedPMI(pmi1, BASE_PMI);
						
						/* 
						if (model1.getCoOccurrence(index, i) > 10) 
							similarity = combinedPMI(pmi1, BASE_PMI);
						else
							similarity = pmi1; // + CORPUS_PMI_DELTA;
						*/
						
					}
					
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					similarity = 0;
				}
				
			}
			
			if (similarity == Float.NEGATIVE_INFINITY){
				
				try {
					pmi1 = model1.getPMI(index, i);
					
					if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(i) < model1.FREQUENCY_LIMIT) 
						similarity = 0; // exclude rarely appearing words
					else
						similarity = combinedPMI(pmi1, pmi2);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//throw new Exception("Error in program! The two words " + word + " " + model1.vocabulary[i] + " do not co-occur in the bigger window but co-occur in the smaller window");
					similarity = 0;
				}
				
			}
			
			FloatElement element = new FloatElement(model1.vocabulary[i], similarity);
			similarWords[i] = element;
		}
		
		Arrays.sort(similarWords);
		
		return similarWords;
		
	}
	
	
	public FloatElement[] getSortedSimilarWordsByDelta(String word) throws Exception{
		
		int index = model1.index(word);
		FloatElement[] similarWords = new FloatElement[model1.sizeOfVocabulary];
		//float invalid = -10;
		
		for (int i=0; i<model1.sizeOfVocabulary; i++){
			
			float pmi1 = 0;
			float pmi2 = 0;
			float similarity = Float.NEGATIVE_INFINITY;
			
			try {
				pmi2 = model2.getPMI(index, i);
				//if (pmi2 < BASE_PMI) pmi2 = BASE_PMI;
			
			} catch (Exception e) {

				// if not exist in the smaller window
				try {
					pmi1 = model1.getPMI(index, i);
					
					if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(i) < model1.FREQUENCY_LIMIT) 
						similarity = 0; // exclude rarely appearing words
					else{
						similarity = deltaPMI(pmi1, BASE_PMI);
						
						/* 
						if (model1.getCoOccurrence(index, i) > 10) 
							similarity = combinedPMI(pmi1, BASE_PMI);
						else
							similarity = pmi1; // + CORPUS_PMI_DELTA;
						*/
						
					}
					
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					similarity = 0;
				}
				
			}
			
			if (similarity == Float.NEGATIVE_INFINITY){
				
				try {
					pmi1 = model1.getPMI(index, i);
					
					/*
					if ((double) model1.getCoOccurrence(index, i) / model1.getFrequency(index) < model1.CONDITIONAL_THRESHOLD)
						similarity = invalid;
					else
					*/ 
						
					if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(i) < model1.FREQUENCY_LIMIT) 
						similarity = 0; // exclude rarely appearing words
					else
						similarity = deltaPMI(pmi1, pmi2);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//throw new Exception("Error in program! The two words " + word + " " + model1.vocabulary[i] + " do not co-occur in the bigger window but co-occur in the smaller window");
					similarity = 0;
				}
				
			}
			
			FloatElement element = new FloatElement(model1.vocabulary[i], similarity);
			similarWords[i] = element;
		}
		
		Arrays.sort(similarWords);
		
		return similarWords;
		
	}
	
	
	
	
	
	public String printPCW(Object[] sortedWords, int size){
		
		StringBuffer temp = new StringBuffer();
		
		for (int i=0; i<size && i<sortedWords.length; i++){
			temp.append(sortedWords[i] + ", ");
		}
		
		System.out.println(temp);
		
		return temp.toString();

	}
	
	
	public String[] getIntersection(FloatElement[] list1, FloatElement[] list2, int size){
		
		Vector<String> result = new Vector<String>();
		
		HashSet<String> elementSet1 = new HashSet<String>();
		
		for (int i=0; i < size && i <list1.length; i++){
			elementSet1.add(list1[i].word);
		}

		for (int i=0; i < size && i <list2.length; i++){
			if (elementSet1.contains(list2[i].word)){
				result.add(list2[i].word);
			}
		}
		
		return result.toArray(new String[result.size()]);
	}
	
	public float getPromotedPMI(CoOccurModelByArrays model, String question, String choice, String pos) throws Exception{
		
		float pmi = 0;
		double baseSenseFrq = 700;
		
		if (pos.equals("NN"))
			pmi = model.getModifiedPMI(question, choice, 30, 1.5, -4, baseSenseFrq);
		else if (pos.equals("VB"))
			pmi = model.getModifiedPMI(question, choice, 40, 1.5, -5, baseSenseFrq);
		else if (pos.equals("JJ"))
			pmi = model.getModifiedPMI(question, choice, 60, 2, -4, baseSenseFrq);
		else if (pos.equals("RB"))
			pmi = model.getModifiedPMI(question, choice, 40, 3, 0, baseSenseFrq);
		
		return pmi;
	}
	
	public String computedPosAnswerByPromotedPMI(CoOccurModelByArrays model, String question, String choice1, String choice2, String choice3, String choice4){
		
		float maxPMI = Float.NEGATIVE_INFINITY;
		String answer = "";
		
		float pmi1 = 0;
		
		try {
			//pmi1 = Utility.getPromotedPMI(model.getPMI(question, choice1), model.getFrequency(choice1), question.substring(question.length() - 2, question.length()));
			pmi1 = getPromotedPMI(model, question, choice1, question.substring(question.length() - 2, question.length()));
			maxPMI = pmi1;
			answer = choice1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		
		System.out.println(choice1 + " has a pmi value: " + pmi1);
		
		
		float pmi2 = 0;
		
		try {
			//pmi2 = Utility.getPromotedPMI(model.getPMI(question, choice2),  model.getFrequency(choice2), question.substring(question.length() - 2, question.length()));
			pmi2 = getPromotedPMI(model, question, choice2, question.substring(question.length() - 2, question.length()));

			if (pmi2 > maxPMI){
				maxPMI = pmi2;
				answer = choice2;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		System.out.println(choice2 + " has a pmi value: " + pmi2);
		
		float pmi3 = 0;
		
		try {
			//pmi3 = Utility.getPromotedPMI(model.getPMI(question, choice3),  model.getFrequency(choice3), question.substring(question.length() - 2, question.length()));
			pmi3 = getPromotedPMI(model, question, choice3, question.substring(question.length() - 2, question.length()));

			if (pmi3 > maxPMI){
				maxPMI = pmi3;
				answer = choice3;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		System.out.println(choice3 + " has a pmi value: " + pmi3);
		
		float pmi4 = 0;
		try {
			//pmi4 = Utility.getPromotedPMI(model.getPMI(question, choice4),  model.getFrequency(choice4), question.substring(question.length() - 2, question.length()));
			pmi4 = getPromotedPMI(model, question, choice4, question.substring(question.length() - 2, question.length()));

			
			if (pmi4 > maxPMI){
				maxPMI = pmi4;
				answer = choice4;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		System.out.println(choice4 + " has a pmi value: " + pmi4);
		
		return answer;
	}

	public String computedPosAnswerByPMI(CoOccurModelByArrays model, String question, String choice1, String choice2, String choice3, String choice4){
		
		float maxPMI = Float.NEGATIVE_INFINITY;
		String answer = "";
		
		float pmi1 = 0;
		
		try {
			pmi1 = model.getPMI(question, choice1);
			maxPMI = pmi1;
			answer = choice1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		
		System.out.println(choice1 + " has a pmi value: " + pmi1);
		
		
		float pmi2 = 0;
		
		try {
			pmi2 = model.getPMI(question, choice2);

			if (pmi2 > maxPMI){
				maxPMI = pmi2;
				answer = choice2;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		System.out.println(choice2 + " has a pmi value: " + pmi2);
		
		float pmi3 = 0;
		
		try {
			pmi3 = model.getPMI(question, choice3);

			if (pmi3 > maxPMI){
				maxPMI = pmi3;
				answer = choice3;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		System.out.println(choice3 + " has a pmi value: " + pmi3);
		
		float pmi4 = 0;
		try {
			pmi4 = model.getPMI(question, choice4);

			if (pmi4 > maxPMI){
				maxPMI = pmi4;
				answer = choice4;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		System.out.println(choice4 + " has a pmi value: " + pmi4);
		
		return answer;
	}
	
	
	public String computedPosAnswerByPPMICosine(PPMIModelByArrays model, String question, String choice1, String choice2, String choice3, String choice4){
		
		float maxSim = Float.NEGATIVE_INFINITY;
		String answer = "";
		
		float sim1 = 0;
		
		try {
			sim1 = model.getDistributionalSimilarity(question, choice1);
			maxSim = sim1;
			answer = choice1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		
		System.out.println(choice1 + " has a pmi value: " + sim1);
		
		
		float sim2 = 0;
		
		try {
			sim2 = model.getDistributionalSimilarity(question, choice2);

			if (sim2 > maxSim){
				maxSim = sim2;
				answer = choice2;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		System.out.println(choice2 + " has a pmi value: " + sim2);
		
		float sim3 = 0;
		
		try {
			sim3 = model.getDistributionalSimilarity(question, choice3);

			if (sim3 > maxSim){
				maxSim = sim3;
				answer = choice3;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		System.out.println(choice3 + " has a pmi value: " + sim3);
		
		float sim4 = 0;
		try {
			sim4 = model.getDistributionalSimilarity(question, choice4);

			if (sim4 > maxSim){
				maxSim = sim4;
				answer = choice4;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		System.out.println(choice4 + " has a pmi value: " + sim4);
		
		return answer;
	}

	public String computedAnswerByPMI(CoOccurModelByArrays model, String p_question, String p_choice1, String p_choice2, String p_choice3, String p_choice4){
		
		float maxPMI = Float.NEGATIVE_INFINITY;
		String answer = "";
		
		String question = morpha.stem(new Word(p_question)).value();
		String choice1 = morpha.stem(new Word(p_choice1)).value();
		String choice2 = morpha.stem(new Word(p_choice2)).value();
		String choice3 = morpha.stem(new Word(p_choice3)).value();
		String choice4 = morpha.stem(new Word(p_choice4)).value();
		
		float pmi1 = 0;
		
		try {
			pmi1 = model.getPMI(question, choice1);
			maxPMI = pmi1;
			answer = p_choice1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		
		System.out.println(choice1 + " has a pmi value: " + pmi1);
		
		
		float pmi2 = 0;
		
		try {
			pmi2 = model.getPMI(question, choice2);

			if (pmi2 > maxPMI){
				maxPMI = pmi2;
				answer = p_choice2;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		System.out.println(choice2 + " has a pmi value: " + pmi2);
		
		float pmi3 = 0;
		
		try {
			pmi3 = model.getPMI(question, choice3);

			if (pmi3 > maxPMI){
				maxPMI = pmi3;
				answer = p_choice3;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		System.out.println(choice3 + " has a pmi value: " + pmi3);
		
		float pmi4 = 0;
		try {
			pmi4 = model.getPMI(question, choice4);

			if (pmi4 > maxPMI){
				maxPMI = pmi4;
				answer = p_choice4;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		System.out.println(choice4 + " has a pmi value: " + pmi4);
		
		return answer;
	}

	public String computedAnswerByVectorSimilarity(CoOccurModelByArrays model, String p_question, String p_choice1, String p_choice2, String p_choice3, String p_choice4){
		
		float maxSIM = Float.NEGATIVE_INFINITY;
		String answer = "";
		
		String question = morpha.stem(new Word(p_question)).value();
		String choice1 = morpha.stem(new Word(p_choice1)).value();
		String choice2 = morpha.stem(new Word(p_choice2)).value();
		String choice3 = morpha.stem(new Word(p_choice3)).value();
		String choice4 = morpha.stem(new Word(p_choice4)).value();
		
		float sim1 = 0;
		
		try {
			sim1 = model.getVectorSimilarity(question, choice1, WINDOW_THRESHOLD);
			maxSIM = sim1;
			answer = p_choice1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		
		System.out.println(choice1 + " has a sim value: " + sim1);
		
		
		float sim2 = 0;
		
		try {
			sim2 = model.getVectorSimilarity(question, choice2, WINDOW_THRESHOLD);

			if (sim2 > maxSIM){
				maxSIM = sim2;
				answer = p_choice2;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		System.out.println(choice2 + " has a sim value: " + sim2);
		
		float sim3 = 0;
		
		try {
			sim3 = model.getVectorSimilarity(question, choice3, WINDOW_THRESHOLD);

			if (sim3 > maxSIM){
				maxSIM = sim3;
				answer = p_choice3;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		System.out.println(choice3 + " has a sim value: " + sim3);
		
		float sim4 = 0;
		try {
			sim4 = model.getVectorSimilarity(question, choice4, WINDOW_THRESHOLD);

			if (sim4 > maxSIM){
				maxSIM = sim4;
				answer = p_choice4;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		System.out.println(choice4 + " has a sim value: " + sim4);
		
		return answer;
	}
	
	
	public String computedAnswerByTwoPMIs(CoOccurModelByArrays model1, CoOccurModelByArrays model2, String p_question, String p_choice1, String p_choice2, String p_choice3, String p_choice4){
		
		float maxSim = 0;
		String answer = "";
		
		String question = morpha.stem(new Word(p_question)).value();
		String choice1 = morpha.stem(new Word(p_choice1)).value();
		String choice2 = morpha.stem(new Word(p_choice2)).value();
		String choice3 = morpha.stem(new Word(p_choice3)).value();
		String choice4 = morpha.stem(new Word(p_choice4)).value();
		
		float similarity;

		float pmi1a = 0;
		float pmi2a = 0;
		similarity = Float.NEGATIVE_INFINITY;
		
		try {
			pmi2a = model2.getPMI(question, choice1);
			//if (pmi2a < 2) pmi2a = 2;
		
		} catch (Exception e) {

			System.out.println(e.getMessage());				
			
			// if not exist in the smaller window
			try {
				pmi1a = model1.getPMI(question, choice1);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice1) < model1.FREQUENCY_LIMIT){ 
					similarity = 0; // exclude rarely appearing words
				}else{
					if (model1.getCoOccurrence(question, choice1) > 10) 
						similarity = combinedPMI(pmi1a, BASE_PMI);
					else
						similarity = pmi1a; // + CORPUS_PMI_DELTA;
					//similarity = combinedPMI(pmi1a, 0);
				}
				
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());				
				similarity = 0;
			}
			
		}
		
		if (similarity == Float.NEGATIVE_INFINITY){
			
			try {
				pmi1a = model1.getPMI(question, choice1);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice1) < model1.FREQUENCY_LIMIT) 
					similarity = 0; // exclude rarely appearing words
				else
					similarity = combinedPMI(pmi1a, pmi2a);
				
			} catch (Exception e) {
				similarity = 0;
				System.out.println("Error in program or corpus ! The two words " + question + " " + choice1 + " do not co-occur in the bigger window but co-occur in the smaller window");				
			}
			
		}

		if (similarity > maxSim){
			maxSim = similarity;
			answer = p_choice1;
		}

		System.out.println(choice1 + " has a combined pmi value: " + similarity);
		

		float pmi1b = 0;
		float pmi2b = 0;
		similarity = Float.NEGATIVE_INFINITY;
		
		try {
			pmi2b = model2.getPMI(question, choice2);
			//if (pmi2b < 2) pmi2b = 2;
		
		} catch (Exception e) {

			System.out.println(e.getMessage());				

			// if not exist in the smaller window
			try {
				pmi1b = model1.getPMI(question, choice2);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice1) < model1.FREQUENCY_LIMIT){ 
					similarity = 0; // exclude rarely appearing words
					//similarity = combinedPMI(pmi1b, 0);
				}else{
					if (model1.getCoOccurrence(question, choice2) > 10) 
						similarity = combinedPMI(pmi1b, BASE_PMI);
					else
						similarity = pmi1b; // + CORPUS_PMI_DELTA;
				}
				
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());				
				similarity = 0;
			}
		}
		
		if (similarity == Float.NEGATIVE_INFINITY){
			
			try {
				pmi1b = model1.getPMI(question, choice2);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice2) < model1.FREQUENCY_LIMIT) 
					similarity = 0; // exclude rarely appearing words
				else
					similarity = combinedPMI(pmi1b, pmi2b);
				
			} catch (Exception e) {
				similarity = 0;
				System.out.println("Error in program or corpus ! The two words " + question + " " + choice2 + " do not co-occur in the bigger window but co-occur in the smaller window");				
			}
			
		}

		if (similarity > maxSim){
			maxSim = similarity;
			answer = p_choice2;
		}
		
		System.out.println(choice2 + " has a combined pmi value: " + similarity);
		
		
		float pmi1c = 0;
		float pmi2c = 0;
		similarity = Float.NEGATIVE_INFINITY;
		
		try {
			pmi2c = model2.getPMI(question, choice3);
			//if (pmi2c < 2) pmi2c = 2;
		
		} catch (Exception e) {

			System.out.println(e.getMessage());				

			// if not exist in the smaller window
			try {
				pmi1c = model1.getPMI(question, choice3);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice3) < model1.FREQUENCY_LIMIT){ 
					similarity = 0; // exclude rarely appearing words
					//similarity = combinedPMI(pmi1c, 0);
				}else{
					if (model1.getCoOccurrence(question, choice3) > 10) 
						similarity = combinedPMI(pmi1c, BASE_PMI);
					else
						similarity = pmi1c; // + CORPUS_PMI_DELTA;
				}
				
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());				
				similarity = 0;
			}
		}
		
		if (similarity == Float.NEGATIVE_INFINITY){
			
			try {
				pmi1c = model1.getPMI(question, choice3);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice3) < model1.FREQUENCY_LIMIT) 
					similarity = 0; // exclude rarely appearing words
				else
					similarity = combinedPMI(pmi1c, pmi2c);
				
			} catch (Exception e) {
				similarity = 0;
				System.out.println("Error in program or corpus ! The two words " + question + " " + choice3 + " do not co-occur in the bigger window but co-occur in the smaller window");				
				
			}
			
		}

		if (similarity > maxSim){
			maxSim = similarity;
			answer = p_choice3;
		}
		
		System.out.println(choice3 + " has a combined pmi value: " + similarity);
		

		float pmi1d = 0;
		float pmi2d = 0;
		similarity = Float.NEGATIVE_INFINITY;
		
		try {
			pmi2d = model2.getPMI(question, choice4);
			//if (pmi2d < 2) pmi2d = 2;
		
		} catch (Exception e) {

			System.out.println(e.getMessage());	
			
			// if not exist in the smaller window
			try {
				pmi1d = model1.getPMI(question, choice4);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice4) < model1.FREQUENCY_LIMIT){ 
					similarity = 0; // exclude rarely appearing words
					//similarity = combinedPMI(pmi1d, 0);
				}else{
					if (model1.getCoOccurrence(question, choice4) > 10) 
						similarity = combinedPMI(pmi1d, BASE_PMI);
					else
						similarity = pmi1d; // + CORPUS_PMI_DELTA;
				}
				
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());				
				similarity = 0;
			}
		}
		
		if (similarity == Float.NEGATIVE_INFINITY){
			
			try {
				pmi1d = model1.getPMI(question, choice4);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice4) < model1.FREQUENCY_LIMIT) 
					similarity = 0; // exclude rarely appearing words
				else
					similarity = combinedPMI(pmi1d, pmi2d);
				
			} catch (Exception e) {
				similarity = 0;
				System.out.println("Error in program or corpus ! The two words " + question + " " + choice4 + " do not co-occur in the bigger window but co-occur in the smaller window");				
				
			}
			
		}

		if (similarity > maxSim){
			maxSim = similarity;
			answer = p_choice4;
		}

		System.out.println(choice4 + " has a combined pmi value: " + similarity);
		
		return answer;
	}

	public String computedAnswerByTwoPMIs2(CoOccurModelByArrays model1, CoOccurModelByArrays model2, String p_question, String p_choice1, String p_choice2, String p_choice3, String p_choice4){
		
		float maxSim = 0;
		String answer = "";
		
		String question = morpha.stem(new Word(p_question)).value();
		String choice1 = morpha.stem(new Word(p_choice1)).value();
		String choice2 = morpha.stem(new Word(p_choice2)).value();
		String choice3 = morpha.stem(new Word(p_choice3)).value();
		String choice4 = morpha.stem(new Word(p_choice4)).value();
		
		float similarity;

		float pmi1a = 0;
		float pmi2a = 0;
		similarity = Float.NEGATIVE_INFINITY;
		
		try {
			pmi2a = model2.getPMI(question, choice1);
			//if (pmi2a < 2) pmi2a = 2;
		
		} catch (Exception e) {

			System.out.println(e.getMessage());				
			
			// if not exist in the smaller window
			try {
				pmi1a = model1.getPMI(question, choice1);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice1) < model1.FREQUENCY_LIMIT){ 
					similarity = 0; // exclude rarely appearing words
				}else{
					similarity = combinedPMI(pmi1a, BASE_PMI);
				}
				
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());				
				similarity = 0;
			}
			
		}
		
		if (similarity == Float.NEGATIVE_INFINITY){
			
			try {
				pmi1a = model1.getPMI(question, choice1);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice1) < model1.FREQUENCY_LIMIT) 
					similarity = 0; // exclude rarely appearing words
				else
					similarity = combinedPMI(pmi1a, pmi2a);
				
			} catch (Exception e) {
				similarity = 0;
				System.out.println("Error in program or corpus ! The two words " + question + " " + choice1 + " do not co-occur in the bigger window but co-occur in the smaller window");				
			}
			
		}

		if (similarity > maxSim){
			maxSim = similarity;
			answer = p_choice1;
		}

		System.out.println(choice1 + " has a combined pmi value: " + similarity);
		

		float pmi1b = 0;
		float pmi2b = 0;
		similarity = Float.NEGATIVE_INFINITY;
		
		try {
			pmi2b = model2.getPMI(question, choice2);
			//if (pmi2b < 2) pmi2b = 2;
		
		} catch (Exception e) {

			System.out.println(e.getMessage());				

			// if not exist in the smaller window
			try {
				pmi1b = model1.getPMI(question, choice2);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice1) < model1.FREQUENCY_LIMIT){ 
					similarity = 0; // exclude rarely appearing words
					//similarity = combinedPMI(pmi1b, 0);
				}else{
					similarity = combinedPMI(pmi1b, BASE_PMI);
				}
				
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());				
				similarity = 0;
			}
		}
		
		if (similarity == Float.NEGATIVE_INFINITY){
			
			try {
				pmi1b = model1.getPMI(question, choice2);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice2) < model1.FREQUENCY_LIMIT) 
					similarity = 0; // exclude rarely appearing words
				else
					similarity = combinedPMI(pmi1b, pmi2b);
				
			} catch (Exception e) {
				similarity = 0;
				System.out.println("Error in program or corpus ! The two words " + question + " " + choice2 + " do not co-occur in the bigger window but co-occur in the smaller window");				
			}
			
		}

		if (similarity > maxSim){
			maxSim = similarity;
			answer = p_choice2;
		}
		
		System.out.println(choice2 + " has a combined pmi value: " + similarity);
		
		
		float pmi1c = 0;
		float pmi2c = 0;
		similarity = Float.NEGATIVE_INFINITY;
		
		try {
			pmi2c = model2.getPMI(question, choice3);
			//if (pmi2c < 2) pmi2c = 2;
		
		} catch (Exception e) {

			System.out.println(e.getMessage());				

			// if not exist in the smaller window
			try {
				pmi1c = model1.getPMI(question, choice3);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice3) < model1.FREQUENCY_LIMIT){ 
					similarity = 0; // exclude rarely appearing words
					//similarity = combinedPMI(pmi1c, 0);
				}else{
					similarity = combinedPMI(pmi1c, BASE_PMI);
				}
				
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());				
				similarity = 0;
			}
		}
		
		if (similarity == Float.NEGATIVE_INFINITY){
			
			try {
				pmi1c = model1.getPMI(question, choice3);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice3) < model1.FREQUENCY_LIMIT) 
					similarity = 0; // exclude rarely appearing words
				else
					similarity = combinedPMI(pmi1c, pmi2c);
				
			} catch (Exception e) {
				similarity = 0;
				System.out.println("Error in program or corpus ! The two words " + question + " " + choice3 + " do not co-occur in the bigger window but co-occur in the smaller window");				
				
			}
			
		}

		if (similarity > maxSim){
			maxSim = similarity;
			answer = p_choice3;
		}
		
		System.out.println(choice3 + " has a combined pmi value: " + similarity);
		

		float pmi1d = 0;
		float pmi2d = 0;
		similarity = Float.NEGATIVE_INFINITY;
		
		try {
			pmi2d = model2.getPMI(question, choice4);
			//if (pmi2d < 2) pmi2d = 2;
		
		} catch (Exception e) {

			System.out.println(e.getMessage());	
			
			// if not exist in the smaller window
			try {
				pmi1d = model1.getPMI(question, choice4);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice4) < model1.FREQUENCY_LIMIT){ 
					similarity = 0; // exclude rarely appearing words
					//similarity = combinedPMI(pmi1d, 0);
				}else{
					similarity = combinedPMI(pmi1d, BASE_PMI);
				}
				
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());				
				similarity = 0;
			}
		}
		
		if (similarity == Float.NEGATIVE_INFINITY){
			
			try {
				pmi1d = model1.getPMI(question, choice4);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice4) < model1.FREQUENCY_LIMIT) 
					similarity = 0; // exclude rarely appearing words
				else
					similarity = combinedPMI(pmi1d, pmi2d);
				
			} catch (Exception e) {
				similarity = 0;
				System.out.println("Error in program or corpus ! The two words " + question + " " + choice4 + " do not co-occur in the bigger window but co-occur in the smaller window");				
				
			}
			
		}

		if (similarity > maxSim){
			maxSim = similarity;
			answer = p_choice4;
		}

		System.out.println(choice4 + " has a combined pmi value: " + similarity);
		
		return answer;
	}

	public String computedAnswerByTwoPMIs3(CoOccurModelByArrays model1, CoOccurModelByArrays model2, String p_question, String p_choice1, String p_choice2, String p_choice3, String p_choice4){
		
		float maxSim = 0;
		String answer = "";
		
		String question = morpha.stem(new Word(p_question)).value();
		String choice1 = morpha.stem(new Word(p_choice1)).value();
		String choice2 = morpha.stem(new Word(p_choice2)).value();
		String choice3 = morpha.stem(new Word(p_choice3)).value();
		String choice4 = morpha.stem(new Word(p_choice4)).value();
		
		float similarity;

		float pmi1a = 0;
		float pmi2a = 0;
		float pmi1b = 0;
		float pmi2b = 0;
		float pmi1c = 0;
		float pmi2c = 0;
		float pmi1d = 0;
		float pmi2d = 0;
		
		
		try {
			pmi2a = model2.getPMI(question, choice1);
			pmi2b = model2.getPMI(question, choice2);
			pmi2c = model2.getPMI(question, choice3);
			pmi2d = model2.getPMI(question, choice4);
		
		} catch (Exception e) {

			System.out.println(e.getMessage());				
			
			//a
			try {
				pmi1a = model1.getPMI(question, choice1);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice1) < model1.FREQUENCY_LIMIT){ 
					similarity = 0; // exclude rarely appearing words
				}else{
					similarity = pmi1a;
				}
				
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());				
				similarity = 0;
			}
			
			if (similarity > maxSim){
				maxSim = similarity;
				answer = p_choice1;
			}
			
			System.out.println(choice1 + " has a pmi value: " + similarity);
			
			//b
			try {
				pmi1b = model1.getPMI(question, choice2);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice2) < model1.FREQUENCY_LIMIT){ 
					similarity = 0; // exclude rarely appearing words
				}else{
					similarity = pmi1b;
				}
				
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());				
				similarity = 0;
			}
			
			if (similarity > maxSim){
				maxSim = similarity;
				answer = p_choice2;
			}
			
			System.out.println(choice2 + " has a pmi value: " + similarity);
			
			//c
			try {
				pmi1c = model1.getPMI(question, choice3);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice3) < model1.FREQUENCY_LIMIT){ 
					similarity = 0; // exclude rarely appearing words
				}else{
					similarity = pmi1c;
				}
				
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());				
				similarity = 0;
			}
			
			if (similarity > maxSim){
				maxSim = similarity;
				answer = p_choice3;
			}
			
			System.out.println(choice3 + " has a pmi value: " + similarity);
			
			//d
			try {
				pmi1d = model1.getPMI(question, choice4);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice4) < model1.FREQUENCY_LIMIT){ 
					similarity = 0; // exclude rarely appearing words
				}else{
					similarity = pmi1d;
				}
				
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());				
				similarity = 0;
			}
			
			if (similarity > maxSim){
				maxSim = similarity;
				answer = p_choice4;
			}

			System.out.println(choice4 + " has a pmi value: " + similarity);
			
			return answer;
		}		
		

		//a
		try {
			pmi1a = model1.getPMI(question, choice1);
			
			if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice1) < model1.FREQUENCY_LIMIT) 
				similarity = 0; // exclude rarely appearing words
			else
				similarity = combinedPMI(pmi1a, pmi2a);
			
		} catch (Exception e) {
			similarity = 0;
			System.out.println("Error in program or corpus ! The two words " + question + " " + choice1 + " do not co-occur in the bigger window but co-occur in the smaller window");				
		}
			
		if (similarity > maxSim){
			maxSim = similarity;
			answer = p_choice1;
		}

		System.out.println(choice1 + " has a combined pmi value: " + similarity);


		//b
		try {
			pmi1b = model1.getPMI(question, choice2);
			
			if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice2) < model1.FREQUENCY_LIMIT) 
				similarity = 0; // exclude rarely appearing words
			else
				similarity = combinedPMI(pmi1b, pmi2b);
			
		} catch (Exception e) {
			similarity = 0;
			System.out.println("Error in program or corpus ! The two words " + question + " " + choice2 + " do not co-occur in the bigger window but co-occur in the smaller window");				
		}
			
		if (similarity > maxSim){
			maxSim = similarity;
			answer = p_choice2;
		}

		System.out.println(choice2 + " has a combined pmi value: " + similarity);
		
		
		//c
		try {
			pmi1c = model1.getPMI(question, choice3);
			
			if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice3) < model1.FREQUENCY_LIMIT) 
				similarity = 0; // exclude rarely appearing words
			else
				similarity = combinedPMI(pmi1c, pmi2c);
			
		} catch (Exception e) {
			similarity = 0;
			System.out.println("Error in program or corpus ! The two words " + question + " " + choice3 + " do not co-occur in the bigger window but co-occur in the smaller window");				
		}
			
		if (similarity > maxSim){
			maxSim = similarity;
			answer = p_choice3;
		}

		System.out.println(choice3 + " has a combined pmi value: " + similarity);
		

		//d
		try {
			pmi1d = model1.getPMI(question, choice4);
			
			if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice4) < model1.FREQUENCY_LIMIT) 
				similarity = 0; // exclude rarely appearing words
			else
				similarity = combinedPMI(pmi1d, pmi2d);
			
		} catch (Exception e) {
			similarity = 0;
			System.out.println("Error in program or corpus ! The two words " + question + " " + choice4 + " do not co-occur in the bigger window but co-occur in the smaller window");				
		}
			
		if (similarity > maxSim){
			maxSim = similarity;
			answer = p_choice4;
		}

		System.out.println(choice4 + " has a combined pmi value: " + similarity);
		
		numberOfCombinedPMI ++;
		return answer;
	}

	public String computedAnswerByDeltaPMI(CoOccurModelByArrays model1, CoOccurModelByArrays model2, String p_question, String p_choice1, String p_choice2, String p_choice3, String p_choice4){
		
		String answer = "";
		
		String question = morpha.stem(new Word(p_question)).value();
		String choice1 = morpha.stem(new Word(p_choice1)).value();
		String choice2 = morpha.stem(new Word(p_choice2)).value();
		String choice3 = morpha.stem(new Word(p_choice3)).value();
		String choice4 = morpha.stem(new Word(p_choice4)).value();
		
		float similarity;

		float pmi1a = 0;
		float pmi2a = 0;
		similarity = Float.NEGATIVE_INFINITY;
		float invalid = -10;
		float maxSim = invalid;
		
		try {
			pmi2a = model2.getPMI(question, choice1);
			//if (pmi2a < 2) pmi2a = 2;
		
		} catch (Exception e) {

			System.out.println(e.getMessage());				
			
			// if not exist in the smaller window
			try {
				pmi1a = model1.getPMI(question, choice1);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice1) < model1.FREQUENCY_LIMIT){ 
					similarity = invalid; // exclude rarely appearing words
				}else{
					if (model1.getCoOccurrence(question, choice1) > 10) 
						similarity = deltaPMI(pmi1a, 0);
					else
						similarity = invalid; // + CORPUS_PMI_DELTA;
					//similarity = deltaPMI(pmi1a, 0);
				}
				
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());				
				similarity = invalid;
			}
			
		}
		
		if (similarity == Float.NEGATIVE_INFINITY){
			
			try {
				pmi1a = model1.getPMI(question, choice1);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice1) < model1.FREQUENCY_LIMIT) 
					similarity = invalid; // exclude rarely appearing words
				else
					similarity = deltaPMI(pmi1a, pmi2a);
				
			} catch (Exception e) {
				similarity = invalid;
				System.out.println("Error in program or corpus ! The two words " + question + " " + choice1 + " do not co-occur in the bigger window but co-occur in the smaller window");				
			}
			
		}

		if (similarity > maxSim){
			maxSim = similarity;
			answer = p_choice1;
		}

		System.out.println(choice1 + " has a delta pmi value: " + similarity);
		

		float pmi1b = 0;
		float pmi2b = 0;
		similarity = Float.NEGATIVE_INFINITY;
		
		try {
			pmi2b = model2.getPMI(question, choice2);
			//if (pmi2b < 2) pmi2b = 2;
		
		} catch (Exception e) {

			System.out.println(e.getMessage());				

			// if not exist in the smaller window
			try {
				pmi1b = model1.getPMI(question, choice2);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice1) < model1.FREQUENCY_LIMIT){ 
					similarity = invalid; // exclude rarely appearing words
					//similarity = deltaPMI(pmi1b, 0);
				}else{
					if (model1.getCoOccurrence(question, choice2) > 10) 
						similarity = deltaPMI(pmi1b, 0);
					else
						similarity = invalid; // + CORPUS_PMI_DELTA;
				}
				
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());				
				similarity = invalid;
			}
		}
		
		if (similarity == Float.NEGATIVE_INFINITY){
			
			try {
				pmi1b = model1.getPMI(question, choice2);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice2) < model1.FREQUENCY_LIMIT) 
					similarity = invalid; // exclude rarely appearing words
				else
					similarity = deltaPMI(pmi1b, pmi2b);
				
			} catch (Exception e) {
				similarity = invalid;
				System.out.println("Error in program or corpus ! The two words " + question + " " + choice2 + " do not co-occur in the bigger window but co-occur in the smaller window");				
			}
			
		}

		if (similarity > maxSim){
			maxSim = similarity;
			answer = p_choice2;
		}
		
		System.out.println(choice2 + " has a delta pmi value: " + similarity);
		
		
		float pmi1c = 0;
		float pmi2c = 0;
		similarity = Float.NEGATIVE_INFINITY;
		
		try {
			pmi2c = model2.getPMI(question, choice3);
			//if (pmi2c < 2) pmi2c = 2;
		
		} catch (Exception e) {

			System.out.println(e.getMessage());				

			// if not exist in the smaller window
			try {
				pmi1c = model1.getPMI(question, choice3);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice3) < model1.FREQUENCY_LIMIT){ 
					similarity = invalid; // exclude rarely appearing words
					//similarity = deltaPMI(pmi1c, 0);
				}else{
					if (model1.getCoOccurrence(question, choice3) > 10) 
						similarity = deltaPMI(pmi1c, 0);
					else
						similarity = invalid; // + CORPUS_PMI_DELTA;
				}
				
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());				
				similarity = invalid;
			}
		}
		
		if (similarity == Float.NEGATIVE_INFINITY){
			
			try {
				pmi1c = model1.getPMI(question, choice3);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice3) < model1.FREQUENCY_LIMIT) 
					similarity = invalid; // exclude rarely appearing words
				else
					similarity = deltaPMI(pmi1c, pmi2c);
				
			} catch (Exception e) {
				similarity = invalid;
				System.out.println("Error in program or corpus ! The two words " + question + " " + choice3 + " do not co-occur in the bigger window but co-occur in the smaller window");				
				
			}
			
		}

		if (similarity > maxSim){
			maxSim = similarity;
			answer = p_choice3;
		}
		
		System.out.println(choice3 + " has a delta pmi value: " + similarity);
		

		float pmi1d = 0;
		float pmi2d = 0;
		similarity = Float.NEGATIVE_INFINITY;
		
		try {
			pmi2d = model2.getPMI(question, choice4);
			//if (pmi2d < 2) pmi2d = 2;
		
		} catch (Exception e) {

			System.out.println(e.getMessage());	
			
			// if not exist in the smaller window
			try {
				pmi1d = model1.getPMI(question, choice4);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice4) < model1.FREQUENCY_LIMIT){ 
					similarity = invalid; // exclude rarely appearing words
					//similarity = deltaPMI(pmi1d, 0);
				}else{
					if (model1.getCoOccurrence(question, choice4) > 10) 
						similarity = deltaPMI(pmi1d, 0);
					else
						similarity = invalid; // + CORPUS_PMI_DELTA;
				}
				
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());				
				similarity = invalid;
			}
		}
		
		if (similarity == Float.NEGATIVE_INFINITY){
			
			try {
				pmi1d = model1.getPMI(question, choice4);
				
				if (REMOVE_NON_FREQUENT_WORDS && model1.getFrequency(choice4) < model1.FREQUENCY_LIMIT) 
					similarity = invalid; // exclude rarely appearing words
				else
					similarity = deltaPMI(pmi1d, pmi2d);
				
			} catch (Exception e) {
				similarity = invalid;
				System.out.println("Error in program or corpus ! The two words " + question + " " + choice4 + " do not co-occur in the bigger window but co-occur in the smaller window");				
				
			}
			
		}

		if (similarity > maxSim){
			maxSim = similarity;
			answer = p_choice4;
		}

		System.out.println(choice4 + " has a delta pmi value: " + similarity);
		
		return answer;
	}

	public void DoToeflPosTestByPromotedPMI (CoOccurModelByArrays wholeWindowModel) throws IOException{

		int correctAnswers = 0;
		
		File testFile = new File(testFileName);
		BufferedReader testReader = new BufferedReader(new FileReader(testFile));
		
		String rdline;
		int LineNo = 0;
		String[] question = new String[6];
		
		while ((rdline = testReader.readLine()) != null){

			if (LineNo % 7 != 6)
				question[LineNo % 7] = morpha.stem(rdline.substring(0, rdline.length() - 3), rdline.substring(rdline.length() - 2, rdline.length())).word() 
						+ rdline.substring(rdline.length() - 3, rdline.length());
;
			
			if (LineNo % 7 == 5){
				
				HashSet<String> posSet = new HashSet<String>();
				for (String word : question){
					posSet.add(word.substring(word.length() - 2, word.length()));
				}
				
				String answer;
				
				/*
				if (posSet.contains("JJ"))
					answer = computedPosAnswerByPromotedPMI(wholeWindowModel, question[0], question[1], question[2], question[3], question[4]);
				else
					answer = computedPosAnswerByPromotedPMI(deltaWindowModel, question[0], question[1], question[2], question[3], question[4]);
				*/
				answer = computedPosAnswerByPromotedPMI(wholeWindowModel, question[0], question[1], question[2], question[3], question[4]);
				
				if (answer.equals(question[5])){
					correctAnswers ++;
					System.out.println("Successful question <" + question[0] + ">: Correct Answer=>" + question[5] + ", " + "Computed Answer=>" + answer);
				}else
					System.out.println("Failed question <" + question[0] + ">: Correct Answer=>" + question[5] + ", " + "Computed Answer=>" + answer);
			}
			
			LineNo ++;
		}
		
		System.out.println("total correct answers: " + correctAnswers);
		testReader.close();
	}

	public void DoToeflPosTestByPMI (CoOccurModelByArrays wholeWindowModel) throws IOException{
		//public void DoToeflPosTestByPMI (CoOccurModelByArrays wholeWindowModel, CoOccurModelByArrays deltaWindowModel) throws IOException{

		int correctAnswers = 0;
		
		File testFile = new File(testFileName);
		BufferedReader testReader = new BufferedReader(new FileReader(testFile));
		
		String rdline;
		int LineNo = 0;
		String[] question = new String[6];
		
		while ((rdline = testReader.readLine()) != null){

			if (LineNo % 7 != 6)
				question[LineNo % 7] = morpha.stem(rdline.substring(0, rdline.length() - 3), rdline.substring(rdline.length() - 2, rdline.length())).word() 
						+ rdline.substring(rdline.length() - 3, rdline.length());
;
			
			if (LineNo % 7 == 5){
				
				HashSet<String> posSet = new HashSet<String>();
				for (String word : question){
					posSet.add(word.substring(word.length() - 2, word.length()));
				}
				
				String answer;

				/*
				if (posSet.contains("JJ"))
					answer = computedPosAnswerByPMI(wholeWindowModel, question[0], question[1], question[2], question[3], question[4]);
				else
					answer = computedPosAnswerByPMI(deltaWindowModel, question[0], question[1], question[2], question[3], question[4]);
				*/
				answer = computedPosAnswerByPMI(wholeWindowModel, question[0], question[1], question[2], question[3], question[4]);
				
				
				if (answer.equals(question[5])){
					correctAnswers ++;
					System.out.println("Successful question <" + question[0] + ">: Correct Answer=>" + question[5] + ", " + "Computed Answer=>" + answer);
				}else
					System.out.println("Failed question <" + question[0] + ">: Correct Answer=>" + question[5] + ", " + "Computed Answer=>" + answer);
			}
			
			LineNo ++;
		}
		
		System.out.println("total correct answers: " + correctAnswers);
		testReader.close();
	}
	
	
	public void DoToeflPosTestByPPMICosine (PPMIModelByArrays model) throws IOException{
		//public void DoToeflPosTestByPMI (CoOccurModelByArrays wholeWindowModel, CoOccurModelByArrays deltaWindowModel) throws IOException{

		int correctAnswers = 0;
		
		File testFile = new File(testFileName);
		BufferedReader testReader = new BufferedReader(new FileReader(testFile));
		
		String rdline;
		int LineNo = 0;
		String[] question = new String[6];
		
		while ((rdline = testReader.readLine()) != null){

			if (LineNo % 7 != 6)
				question[LineNo % 7] = morpha.stem(rdline.substring(0, rdline.length() - 3), rdline.substring(rdline.length() - 2, rdline.length())).word() 
						+ rdline.substring(rdline.length() - 3, rdline.length());
;
			
			if (LineNo % 7 == 5){
				
				HashSet<String> posSet = new HashSet<String>();
				for (String word : question){
					posSet.add(word.substring(word.length() - 2, word.length()));
				}
				
				String answer;

				/*
				if (posSet.contains("JJ"))
					answer = computedPosAnswerByPMI(wholeWindowModel, question[0], question[1], question[2], question[3], question[4]);
				else
					answer = computedPosAnswerByPMI(deltaWindowModel, question[0], question[1], question[2], question[3], question[4]);
				*/
				answer = computedPosAnswerByPPMICosine(model, question[0], question[1], question[2], question[3], question[4]);
				
				
				if (answer.equals(question[5])){
					correctAnswers ++;
					System.out.println("Successful question <" + question[0] + ">: Correct Answer=>" + question[5] + ", " + "Computed Answer=>" + answer);
				}else
					System.out.println("Failed question <" + question[0] + ">: Correct Answer=>" + question[5] + ", " + "Computed Answer=>" + answer);
			}
			
			LineNo ++;
		}
		
		System.out.println("total correct answers: " + correctAnswers);
		testReader.close();
	}	

	
	public void DoToeflTestByPMI(CoOccurModelByArrays model) throws IOException{

		int correctAnswers = 0;
		
		File testFile = new File(testFileName);
		BufferedReader testReader = new BufferedReader(new FileReader(testFile));
		
		String rdline;
		int LineNo = 0;
		String[] question = new String[6];
		
		while ((rdline = testReader.readLine()) != null){

			if (LineNo % 7 != 6)
				question[LineNo % 7] = rdline;
			
			if (LineNo % 7 == 5){
				
				String answer = computedAnswerByPMI(model, question[0], question[1], question[2], question[3], question[4]);
				
				if (answer.equals(question[5])){
					correctAnswers ++;
					System.out.println("Successful question <" + question[0] + ">: Correct Answer=>" + question[5] + ", " + "Computed Answer=>" + answer);
				}else
					System.out.println("Failed question <" + question[0] + ">: Correct Answer=>" + question[5] + ", " + "Computed Answer=>" + answer);
			}
			
			LineNo ++;
		}
		
		System.out.println("total correct answers: " + correctAnswers);
		testReader.close();
	}


	public void DoToeflTestByVectorSimilarity(CoOccurModelByArrays model) throws IOException{

		int correctAnswers = 0;
		
		File testFile = new File(testFileName);
		BufferedReader testReader = new BufferedReader(new FileReader(testFile));
		
		String rdline;
		int LineNo = 0;
		String[] question = new String[6];
		
		while ((rdline = testReader.readLine()) != null){

			if (LineNo % 7 != 6)
				question[LineNo % 7] = rdline;
			
			if (LineNo % 7 == 5){
				
				String answer = computedAnswerByVectorSimilarity(model, question[0], question[1], question[2], question[3], question[4]);
				
				if (answer.equals(question[5])){
					correctAnswers ++;
					System.out.println("Successful question <" + question[0] + ">: Correct Answer=>" + question[5] + ", " + "Computed Answer=>" + answer);
				}else
					System.out.println("Failed question <" + question[0] + ">: Correct Answer=>" + question[5] + ", " + "Computed Answer=>" + answer);
			}
			
			LineNo ++;
		}
		
		System.out.println("total correct answers: " + correctAnswers);
		testReader.close();
	}
	
	
	public void ComputeBigWindowStatisticByToeflDataSet() throws IOException{

		int correctAnswers = 0;
		
		File testFile = new File(testFileName);
		BufferedReader testReader = new BufferedReader(new FileReader(testFile));
		
		String rdline;
		int LineNo = 0;
		String[] question = new String[6];
		
		while ((rdline = testReader.readLine()) != null){

			if (LineNo % 7 != 6)
				question[LineNo % 7] = rdline;
			
			if (LineNo % 7 == 5){
				
				float autoPMI;
				String questionWord = morpha.stem(new Word(question[0])).value();
				String synonym = morpha.stem(new Word(question[5])).value();
				
				try {
					autoPMI = model1.getPMI(questionWord, questionWord);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					autoPMI = 0;
				}
				
				FloatElement[] sortedPMIWords = model1.getSortedPMIWords(questionWord);
				
				float highestPMI = sortedPMIWords[0].value;
				
				int rank = -1;
				for (int i=0; i < sortedPMIWords.length; i++ ){
					if (sortedPMIWords[i].word.equals(questionWord)){
						rank = i;
						break;
					}
				}
				
				float synonymPMI;
				try {
					synonymPMI = model1.getPMI(questionWord, synonym);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					synonymPMI = 0;
				}

				if (questionWord.length() <= 6)
					System.out.println(questionWord + ",\t\t\t" + autoPMI + ",\t" + highestPMI + ",\t" + (highestPMI - autoPMI) + ",\t" + rank + ",\t" + synonymPMI + ",\t" + (autoPMI - synonymPMI));
				else if (questionWord.length() == 7)
					System.out.println(questionWord + ",\t\t" + autoPMI + ",\t" + highestPMI + ",\t" + (highestPMI - autoPMI) + ",\t" + rank + ",\t" + synonymPMI + ",\t" + (autoPMI - synonymPMI));
				else
					System.out.println(questionWord + ",\t\t" + autoPMI + ",\t" + highestPMI + ",\t" + (highestPMI - autoPMI) + ",\t" + rank + ",\t" + synonymPMI + ",\t" + (autoPMI - synonymPMI));
			}
			
			LineNo ++;
		}
		
		System.out.println("total correct answers: " + correctAnswers);
		testReader.close();
	}
	

	public void ComputeSmallWindowStatisticByToeflDataSet() throws IOException{

		int correctAnswers = 0;
		
		File testFile = new File(testFileName);
		BufferedReader testReader = new BufferedReader(new FileReader(testFile));
		
		String rdline;
		int LineNo = 0;
		String[] question = new String[6];
		
		while ((rdline = testReader.readLine()) != null){

			if (LineNo % 7 != 6)
				question[LineNo % 7] = rdline;
			
			if (LineNo % 7 == 5){
				
				float autoPMI;
				String questionWord = morpha.stem(new Word(question[0])).value();
				String synonym = morpha.stem(new Word(question[5])).value();
				
				try {
					autoPMI = model2.getPMI(questionWord, questionWord);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					autoPMI = 0;
				}
				
				FloatElement[] sortedPMIWords = model2.getSortedPMIWords(questionWord);
				
				float highestPMI = sortedPMIWords[0].value;
				
				int rank = -1;
				for (int i=0; i < sortedPMIWords.length; i++ ){
					if (sortedPMIWords[i].word.equals(questionWord)){
						rank = i;
						break;
					}
				}
				
				float synonymPMI;
				try {
					synonymPMI = model2.getPMI(questionWord, synonym);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					synonymPMI = 0;
				}

				if (questionWord.length() <= 6)
					System.out.println(questionWord + ",\t\t\t" + autoPMI + ",\t" + highestPMI + ",\t" + (highestPMI - autoPMI) + ",\t" + rank + ",\t" + synonymPMI + ",\t" + (autoPMI - synonymPMI));
				else if (questionWord.length() == 7)
					System.out.println(questionWord + ",\t\t" + autoPMI + ",\t" + highestPMI + ",\t" + (highestPMI - autoPMI) + ",\t" + rank + ",\t" + synonymPMI + ",\t" + (autoPMI - synonymPMI));
				else
					System.out.println(questionWord + ",\t\t" + autoPMI + ",\t" + highestPMI + ",\t" + (highestPMI - autoPMI) + ",\t" + rank + ",\t" + synonymPMI + ",\t" + (autoPMI - synonymPMI));
			}
			
			LineNo ++;
		}
		
		System.out.println("total correct answers: " + correctAnswers);
		testReader.close();
	}

	public void DoToeflTestByTwoPMIs(CoOccurModelByArrays model1, CoOccurModelByArrays model2) throws IOException{

		int correctAnswers = 0;
		
		File testFile = new File(testFileName);
		BufferedReader testReader = new BufferedReader(new FileReader(testFile));
		
		String rdline;
		int LineNo = 0;
		String[] question = new String[6];
		
		while ((rdline = testReader.readLine()) != null){

			if (LineNo % 7 != 6)
				question[LineNo % 7] = rdline;
			
			if (LineNo % 7 == 5){
				
				String answer = computedAnswerByTwoPMIs(model1, model2, question[0], question[1], question[2], question[3], question[4]);
				
				if (answer.equals(question[5])){
					correctAnswers ++;
					System.out.println("Successful question <" + question[0] + ">: Correct Answer=>" + question[5] + ", " + "Computed Answer=>" + answer);
				}else
					System.out.println("Failed question <" + question[0] + ">: Correct Answer=>" + question[5] + ", " + "Computed Answer=>" + answer);
			}
			
			LineNo ++;
		}
		
		System.out.println("total correct answers: " + correctAnswers);
		testReader.close();
	}
	
	public void DoToeflTestByTwoPMIs2(CoOccurModelByArrays model1, CoOccurModelByArrays model2) throws IOException{

		int correctAnswers = 0;
		
		File testFile = new File(testFileName);
		BufferedReader testReader = new BufferedReader(new FileReader(testFile));
		
		String rdline;
		int LineNo = 0;
		String[] question = new String[6];
		
		while ((rdline = testReader.readLine()) != null){

			if (LineNo % 7 != 6)
				question[LineNo % 7] = rdline;
			
			if (LineNo % 7 == 5){
				
				String answer = computedAnswerByTwoPMIs2(model1, model2, question[0], question[1], question[2], question[3], question[4]);
				
				if (answer.equals(question[5])){
					correctAnswers ++;
					System.out.println("Successful question <" + question[0] + ">: Correct Answer=>" + question[5] + ", " + "Computed Answer=>" + answer);
				}else
					System.out.println("Failed question <" + question[0] + ">: Correct Answer=>" + question[5] + ", " + "Computed Answer=>" + answer);
			}
			
			LineNo ++;
		}
		
		System.out.println("total correct answers: " + correctAnswers);
		testReader.close();
	}


	public void DoToeflTestByTwoPMIs3(CoOccurModelByArrays model1, CoOccurModelByArrays model2) throws IOException{

		int correctAnswers = 0;
		numberOfCombinedPMI = 0;
		
		File testFile = new File(testFileName);
		BufferedReader testReader = new BufferedReader(new FileReader(testFile));
		
		String rdline;
		int LineNo = 0;
		String[] question = new String[6];
		
		while ((rdline = testReader.readLine()) != null){

			if (LineNo % 7 != 6)
				question[LineNo % 7] = rdline;
			
			if (LineNo % 7 == 5){
				
				String answer = computedAnswerByTwoPMIs3(model1, model2, question[0], question[1], question[2], question[3], question[4]);
				
				if (answer.equals(question[5])){
					correctAnswers ++;
					System.out.println("Successful question <" + question[0] + ">: Correct Answer=>" + question[5] + ", " + "Computed Answer=>" + answer);
				}else
					System.out.println("Failed question <" + question[0] + ">: Correct Answer=>" + question[5] + ", " + "Computed Answer=>" + answer);
			}
			
			LineNo ++;
		}
		
		System.out.println("total correct answers: " + correctAnswers);
		System.out.println("total nubmer of answers from combined PMI: " + numberOfCombinedPMI);
		
		testReader.close();
	}

	
	public void DoToeflTestByDeltaPMI(CoOccurModelByArrays model1, CoOccurModelByArrays model2) throws IOException{

		int correctAnswers = 0;
		
		File testFile = new File(testFileName);
		BufferedReader testReader = new BufferedReader(new FileReader(testFile));
		
		String rdline;
		int LineNo = 0;
		String[] question = new String[6];
		
		while ((rdline = testReader.readLine()) != null){

			if (LineNo % 7 != 6)
				question[LineNo % 7] = rdline;
			
			if (LineNo % 7 == 5){
				
				String answer = computedAnswerByDeltaPMI(model1, model2, question[0], question[1], question[2], question[3], question[4]);
				
				if (answer.equals(question[5])){
					correctAnswers ++;
					System.out.println("Successful question <" + question[0] + ">: Correct Answer=>" + question[5] + ", " + "Computed Answer=>" + answer);
				}else
					System.out.println("Failed question <" + question[0] + ">: Correct Answer=>" + question[5] + ", " + "Computed Answer=>" + answer);
			}
			
			LineNo ++;
		}
		
		System.out.println("total correct answers: " + correctAnswers);
		testReader.close();
	}
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//SimilarityAnalysis test = new SimilarityAnalysis("/home/lushan1/nlp/model2/Gutenberg2006AllW16/3esl_toefl", "/home/lushan1/nlp/model/Gutenberg2006/1212/Gutenberg2006AllW4");
		//SimilarityAnalysis test = new SimilarityAnalysis("/home/lushan1/nlp/model/Gutenberg2006/Gutenberg2006AllW4");
		//SimilarityAnalysis test = new SimilarityAnalysis("/home/lushan1/nlp/model2/Gutenberg2006AllW16/3esl_toefl");
		//SimilarityAnalysis test = new SimilarityAnalysis("/home/lushan1/nlp/model/usenet/usenetAllW4");
		//SimilarityAnalysis test = new SimilarityAnalysis("/home/lushan1/nlp/model/Gutenberg2006/Gutenberg2006AllW4", "/home/lushan1/nlp/model/usenet/usenetAllW4");
		//SimilarityAnalysis test = new SimilarityAnalysis("/home/lushan1/nlp/model/Gutenberg2006/Gutenberg2006AllW4", "/home/lushan1/nlp/model/Gutenberg2006/1212/Gutenberg2006AllW4");
		//SimilarityAnalysis test = new SimilarityAnalysis("/home/lushan1/nlp/model/Gutenberg2006/Gutenberg2006AllW21", "/home/lushan1/nlp/model2/Gutenberg2006AllW16/3esl_toefl");
		//SimilarityAnalysis test = new SimilarityAnalysis("/home/lushan1/nlp/model/Gutenberg2006/Gutenberg2006AllW21", "/home/lushan1/nlp/model/Gutenberg2006/Gutenberg2006AllW4");
		//SimilarityAnalysis test = new SimilarityAnalysis("/home/lushan1/nlp/model/usenet/usenetAllW21", "/home/lushan1/nlp/model/usenet/usenetAllW4");
		//SimilarityAnalysis test = new SimilarityAnalysis("/home/lushan1/nlp/model/Gutenberg2006/1226/Gutenberg2006AllW21", "/home/lushan1/nlp/model/Gutenberg2006/1226/Gutenberg2006AllW4");
		//SimilarityAnalysis test = new SimilarityAnalysis("/home/lushan1/nlp/model/Gutenberg2006/0204/Gutenberg2006AllW21", "/home/lushan1/nlp/model/Gutenberg2006/0204/Gutenberg2006AllW4");
		//SimilarityAnalysis test = new SimilarityAnalysis("/home/lushan1/nlp/model/Gutenberg2010/Gutenberg2010AllW21", "/home/lushan1/nlp/model/Gutenberg2010/Gutenberg2010AllW4");
		//SimilarityAnalysis test = new SimilarityAnalysis("/home/lushan1/nlp/model/Gutenberg2010/0221/Gutenberg2010AllW21");
		//SimilarityAnalysisWithPOS test = new SimilarityAnalysisWithPOS("/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW21", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW21");
		//SimilarityAnalysisWithPOS test = new SimilarityAnalysisWithPOS("/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW31", "/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW21");


		
		//SimilarityAnalysisWithPOS test = new SimilarityAnalysisWithPOS("/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW41", false);
		SimilarityAnalysisWithPOS test = new SimilarityAnalysisWithPOS("/home/lushan1/nlp/model/Wikipedia2006sfd/Wikipedia2006AllW5", false);
		test.model1.FREQUENCY_LIMIT = 0;
		test.model1.CONDITIONAL_THRESHOLD = 0; 

		//SimilarityAnalysisWithPOS test = new SimilarityAnalysisWithPOS("/home/lushan1/nlp/model/PositivePMI/Gutenberg2010AllW2", true);
		
		//SimilarityAnalysisWithPOS test = new SimilarityAnalysisWithPOS("/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW41", "/home/lushan1/nlp/model/PositivePMI/Gutenberg2010AllW2", true);
		
		
		System.out.println("Press any key to continue ...");
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		String dummy = input.readLine(); 
		
		/*
		//File testFile = new File("MC29.csv");
		File testFile = new File("WS289.csv");
		BufferedReader testReader = new BufferedReader(new FileReader(testFile));

		String rdline;
		
		while ((rdline = testReader.readLine()) != null){
			
			StringTokenizer st = new StringTokenizer(rdline, ",");
			String word1 = st.nextToken();
			String word2 = st.nextToken();
			String word3 = st.nextToken();

			word1 = word1 + "_" + word3;
			word2 = word2 + "_" + word3;
			
			try{
				//if (test.model1.getFrequency(word1) >= 200 && test.model1.getFrequency(word2) >= 200)
					//System.out.println(test.getPromotedPMI(test.model1, word1, word2, word3));
				//else
				//	System.out.println(word1 + " " + word2);
				
					System.out.println(test.model1.getPMI(word1,word2));
				//System.out.println(test.pmi_model.getDistributionalSimilarity(word1, word2));
			} catch (Exception e) {
					e.printStackTrace();
			}
			//System.out.println(test.getPromotedPMI(test.model1, word1, word2,"NN"));
		}
		*/
		

		try {
			test.DoToeflPosTestByPMI(test.model1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		try {
			test.DoToeflPosTestByPromotedPMI(test.model1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		/*
		try {
			test.DoToeflPosTestByPPMICosine(test.pmi_model);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		
		try {
			test.DoToeflTestByVectorSimilarity(test.model1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		try {
			test.DoToeflTestByTwoPMIs(test.model1, test.model2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			test.DoToeflTestByTwoPMIs2(test.model1, test.model2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			test.DoToeflTestByTwoPMIs3(test.model1, test.model2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		/*
		FloatElement[] similarWords;
		
		try {
			similarWords = test.getSortedSimilarWords("physician");
			test.printPCW(similarWords, 500);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		

		
		/*
		FloatElement[] pmiWords1 = test.model1.getSortedPMIWords("physician");
		
		test.printPCW(pmiWords1, 500);

		System.out.println();

		FloatElement[] pmiWords2 = test.model2.getSortedPMIWords("physician");
		
		test.printPCW(pmiWords2, 500);
		
		System.out.println();
		*/
		
		
		/*
		CoElement[] coWords1 = test.model2.getSortedCoWords("lawyer");
		
		test.printPCW(coWords1, 200);

		System.out.println();

		CoElement[] coWords2 = test.model2.getSortedCoWords("attorney");
		
		test.printPCW(coWords2, 200);
		
		System.out.println();
		
		FloatElement[] compares = test.getSortedDivides("lawyer");
		
		test.printPCW(compares, 200);

		FloatElement[] compares2 = test.geInversetSortedDivides("lawyer");
		
		System.out.println();

		test.printPCW(compares2, 200);
		
		System.out.println();
		*/
		
		/*
		System.out.println("Max value is " + test.model1.getMaxValue());
		System.out.println("Min value is " + test.model1.getMinValue());
		*/
		
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		
	}

}
