/*
 * Copyright 2009 Author: Lushan Han at Ebiquity Lab, UMBC
 *  
 */
package edu.umbc.wordSimilarity2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

//Positive PMI Model 
public class PPMIModelByArrays {

	public float[][] wordMatrix;
	public int sizeOfVocabulary;
	public String[] vocabulary;
	public int frequency[];
	public String modelName;
	public long totalWords;
	public int FREQUENCY_LIMIT = 700; 
	public boolean REMOVE_NON_FREQUENT_WORDS = true;
	//public int CO_OCCUR_THRESHOLD = 1;
	//public double CONDITIONAL_THRESHOLD = (double) 0; // 1 / 4000;  1 / 1000;
	
	
	public PPMIModelByArrays(String[] sortedTermList, String model_name){
		vocabulary = sortedTermList;
		sizeOfVocabulary = vocabulary.length;
		wordMatrix = new float[sizeOfVocabulary][sizeOfVocabulary];
		frequency = new int[sizeOfVocabulary];
		totalWords = 0;
		modelName = model_name;
	}
	
	
	public PPMIModelByArrays(String filename, boolean existAlert){
		this(filename, existAlert, 700);
	}
	
	
	public PPMIModelByArrays(String filename, boolean existAlert, int frequencyThreshold){
		
		try{
			FREQUENCY_LIMIT = frequencyThreshold;
			//CONDITIONAL_THRESHOLD = conditionalThreshold;
			
			modelName = filename;
			/* Read vocabulary */
			File vocabularyFile = new File(filename + ".voc");
			BufferedReader vocabularyReader = new BufferedReader(new FileReader(vocabularyFile), 1000000);
			
			String rdline;
			rdline = vocabularyReader.readLine();
			sizeOfVocabulary = Integer.valueOf(rdline);
			vocabulary = new String[sizeOfVocabulary];
			wordMatrix = new float[sizeOfVocabulary][sizeOfVocabulary];
			frequency = new int[sizeOfVocabulary];
			totalWords = 0;
			
			for (int i = 0; i < sizeOfVocabulary; i++){
				vocabulary[i] = vocabularyReader.readLine();
			}
		
			vocabularyReader.close();
			Arrays.sort(vocabulary);
			
			/* Read Co-Occurrence Matrix */
			File ppmiModelFile = new File(filename + ".ppmi_mdl");
			if (ppmiModelFile.exists()){
				
				if (existAlert)
					throw new Exception("The model already exists! Thus the program quit.");
				
				BufferedReader coOccurModelReader = new BufferedReader(new FileReader(ppmiModelFile), 4000000);
				
				for (int i = 0; i < sizeOfVocabulary; i++){
					rdline = coOccurModelReader.readLine();
					StringTokenizer st = new StringTokenizer(rdline, ",");
					
					int j = 0;
					while (st.hasMoreTokens()){
						wordMatrix[i][j] = Float.valueOf(st.nextToken());
						j++;
					}
				}
			
				coOccurModelReader.close();
				
			
				/* Read Frequency array */
				File frequencyFile = new File(filename + ".frq");
				BufferedReader frequencyReader = new BufferedReader(new FileReader(frequencyFile), 1000000);
				
				for (int i = 0; i < sizeOfVocabulary; i++){
					frequency[i] = Integer.valueOf(frequencyReader.readLine());
					totalWords += frequency[i];
				}
			
				//hack for gutenberg corpus
				totalWords = 2000000000;
				
				frequencyReader.close();
			
			/* in the case that model file not exist */	
			}else{
				System.out.println("The model doesn't exist. Create a new one!");
			}
			
			

		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
			System.exit(-1);
		}

	}
	
	
	public String printPCW(Object[] sortedWords, int size){
		
		StringBuffer temp = new StringBuffer();
		
		for (int i=0; i<size; i++){
			temp.append(sortedWords[i] + ", ");
		}
		
		System.out.println(temp);
		
		return temp.toString();

	}
	


	public FloatElement[] getSortedPMIWords(String word){
		
		int index1 = index(word);
		FloatElement[] correlateWords = new FloatElement[sizeOfVocabulary];
		
		for (int i=0; i<sizeOfVocabulary; i++){
			
			float pmi = 0;
			
			try {
				pmi = getPMI(index1, i);
				
				if (REMOVE_NON_FREQUENT_WORDS && (getFrequency(i) < FREQUENCY_LIMIT)) 
					//pmi = Float.NEGATIVE_INFINITY;
					continue;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//pmi = Float.NEGATIVE_INFINITY;
				continue;
			}
			
			FloatElement element = new FloatElement(vocabulary[i], pmi);
			correlateWords[i] = element;
		}
		
		Arrays.sort(correlateWords);
		
		return correlateWords;
		
	}

	public float[] getPMIVector(String word){
		
		int index1 = index(word);
		return wordMatrix[index1];
		
	}
	
	public float[] getPMIVector(int index1){
		
		return wordMatrix[index1];
	}
	
	public FloatElement[] getSortedPMIWordsWithPOS(String word, String pos){
		
		int index1 = index(word);
		ArrayList<FloatElement> correlateWords = new ArrayList<FloatElement>();
		
		for (int i=0; i<sizeOfVocabulary; i++){
			
			if (!vocabulary[i].endsWith(pos))
				continue;
				
			float pmi = 0;
			double condProb = 0;
			int frequency = 0;
			
			try {
				pmi = getPMI(index1, i);
				
				frequency =  getFrequency(i);
				
				if (REMOVE_NON_FREQUENT_WORDS && (frequency < FREQUENCY_LIMIT)) 
					//pmi = Float.NEGATIVE_INFINITY;
					continue;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//pmi = Float.NEGATIVE_INFINITY;
				continue;
			}
			
			FloatElement element = new FloatElement(vocabulary[i], pmi, (float) condProb, frequency);
			correlateWords.add(element);
		}
		
		FloatElement[] result = correlateWords.toArray(new FloatElement[correlateWords.size()]);
		Arrays.sort(result);
		
		return result;
		
	}

	
	public FloatElement[] getSortedPMIWordsWithSamePOS(String word){
		
		int index1 = index(word);
		ArrayList<FloatElement> correlateWords = new ArrayList<FloatElement>();
		String pos = word.substring(word.length() - 2, word.length());
		
		for (int i=0; i<sizeOfVocabulary; i++){
			
			if (!vocabulary[i].endsWith(pos))
				continue;
			
			if (i == index1)
				continue;
			
			float pmi = 0;
			double condProb = 0;
			int frequency = 0;
			
			try {
				pmi = getPMI(index1, i);
				
				frequency =  getFrequency(i);
				
				if (REMOVE_NON_FREQUENT_WORDS && (frequency < FREQUENCY_LIMIT)) 
					//pmi = Float.NEGATIVE_INFINITY;
					continue;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// pmi = Float.NEGATIVE_INFINITY;
				continue;
			}
			
			FloatElement element = new FloatElement(vocabulary[i], pmi, (float) condProb, frequency);
			correlateWords.add(element);
		}
		
		FloatElement[] result = correlateWords.toArray(new FloatElement[correlateWords.size()]);
		Arrays.sort(result);
		
		return result;
		
	}
	

	public int getPmiOrder(String word1, String word2) throws Exception{
		
		FloatElement[] pmiSortedWords = getSortedPMIWords(word1);
		
		for (int i=0; i < pmiSortedWords.length; i++){
			
			if (pmiSortedWords[i].word.equals(word2))
				return i;
				
		}
		
		throw new Exception("The word " + word2 + " is not in the vocabulary");
	}
	
	
	public float getDistributionalSimilarity(String question, String choice) throws Exception{
		
		float[] PMIvector1 = getPMIVector(question);
		float[] PMIvector2 = getPMIVector(choice);
		
		double square_sum_1 = 0;
		double square_sum_2 = 0;
		
		for (int i=0; i< PMIvector1.length; i++){
			square_sum_1 += PMIvector1[i] * PMIvector1[i];
			square_sum_2 += PMIvector2[i] * PMIvector2[i];
		}
		
		double norm1 = Math.sqrt(square_sum_1);
		double norm2 = Math.sqrt(square_sum_2);
		
		double dot_product = 0;
		
		for (int i=0; i< PMIvector1.length; i++){
			
			dot_product += PMIvector1[i] * PMIvector2[i];
		}
		
		return (float) (dot_product / (norm1 * norm2));
	}
	
	public float getDistributionalSimilarity(int question, int choice) throws Exception{
		
		float[] PMIvector1 = getPMIVector(question);
		float[] PMIvector2 = getPMIVector(choice);
		
		double square_sum_1 = 0;
		double square_sum_2 = 0;
		
		for (int i=0; i< PMIvector1.length; i++){
			square_sum_1 += PMIvector1[i] * PMIvector1[i];
			square_sum_2 += PMIvector2[i] * PMIvector2[i];
		}
		
		double norm1 = Math.sqrt(square_sum_1);
		double norm2 = Math.sqrt(square_sum_2);
		
		double dot_product = 0;
		
		for (int i=0; i< PMIvector1.length; i++){
			
			if (PMIvector1[i] < 0 || PMIvector2[i] < 0)
				System.out.println(i + " " + PMIvector1[i] + " " + PMIvector2[i]);
			
			dot_product += PMIvector1[i] * PMIvector2[i];
		}
		
		//System.out.println();
		return (float) ( dot_product / (norm1 * norm2) );
	}
	
	
	public FloatElement[] getDistributionalSimilarWordsWithSamePOS(String word){
		
		int index1 = index(word);
		ArrayList<FloatElement> similarWords = new ArrayList<FloatElement>();
		String pos = word.substring(word.length() - 2, word.length());
		
		for (int i=0; i<sizeOfVocabulary; i++){
			
			if (!vocabulary[i].endsWith(pos))
				continue;
				
			if (i == index1)
				continue;
			
			float sim = 0;
			double condProb = 0;
			int frequency = 0;
			
			try {
				frequency =  getFrequency(i);

				if (REMOVE_NON_FREQUENT_WORDS && (frequency < FREQUENCY_LIMIT)) 
					//pmi = Float.NEGATIVE_INFINITY;
					continue;

				sim = getDistributionalSimilarity(index1, i);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//pmi = Float.NEGATIVE_INFINITY;
				continue;
			}
			
			FloatElement element = new FloatElement(vocabulary[i], sim, (float) condProb, frequency);
			similarWords.add(element);
		}
		
		FloatElement[] result = similarWords.toArray(new FloatElement[similarWords.size()]);
		Arrays.sort(result);
		
		return result;
		
	}
	
	
	public float getMaxValue(){

		float maxValue = 0;
		
		for (int i=0; i < sizeOfVocabulary; i++){
			for (int j=0; j < sizeOfVocabulary; j++){
				
				if (wordMatrix[i][j] > maxValue)
					maxValue = wordMatrix[i][j];
			}
			
		}
		
		return maxValue;
	}

	public float getMinValue(){

		float minValue = Float.MAX_VALUE;
		
		for (int i=0; i < sizeOfVocabulary; i++){
			for (int j=0; j < sizeOfVocabulary; j++){
				
				if (wordMatrix[i][j] < minValue)
					minValue = wordMatrix[i][j];
			}
			
		}
		
		return minValue;
	}
	
	
	public int index(String word){
		return Arrays.binarySearch(vocabulary, word);
	}
	

	public float getPMI(int index1, int index2){
		
		if (index1 >=0 && index2 >= 0)
			return wordMatrix[index1][index2];
		else
			return 0;
	}


	public float getPMI(String word1, String word2){
		
		int index1 = index(word1);
		int index2 = index(word2);

		if (index1 >=0 && index2 >= 0)
			return wordMatrix[index1][index2];
		else
			return 0;
	}
	
	public int getFrequency(String word){
		int index = index(word);
		if (index >= 0)
			return frequency[index];
		else
			return 0;
	}

	public int getFrequency(int index){

		if (index >= 0)
			return frequency[index];
		else
			return 0;
	}
	
	public String[] getVocabulary(){
		return vocabulary;
	}
	
	public int saveModel(){
		
		try{
			FileWriter fileOfVocabulary = new FileWriter(modelName + ".voc");
			PrintWriter outOfVocabulary = new PrintWriter(new BufferedWriter(fileOfVocabulary, 1000000));
			
			FileWriter fileOfCoOccurModel = new FileWriter(modelName + ".ppmi_mdl");
			PrintWriter outOfCoOccurModel = new PrintWriter(new BufferedWriter(fileOfCoOccurModel, 4000000));

			FileWriter fileOfFrequency = new FileWriter(modelName + ".frq");
			PrintWriter outOfFrequency = new PrintWriter(new BufferedWriter(fileOfFrequency, 1000000));
			
			outOfVocabulary.println(vocabulary.length);
			
			for (String word:vocabulary){
				outOfVocabulary.print(word);
				outOfVocabulary.println();
			}
			
			for (int i = 0; i < sizeOfVocabulary; i++){
				for (int j = 0; j < sizeOfVocabulary; j++){
					outOfCoOccurModel.print(wordMatrix[i][j]);
					
					if (j != sizeOfVocabulary -1)
						outOfCoOccurModel.print(",");
				}
				outOfCoOccurModel.println();
			}
			
			for (int i = 0; i < sizeOfVocabulary; i++){
				outOfFrequency.print(frequency[i]);
				outOfFrequency.println();
			}
			
			outOfVocabulary.close();
			outOfCoOccurModel.close();
			outOfFrequency.close();
		
		} catch (Exception e){
			System.out.println(e.getMessage());
			return -1;
		}
		
		return 0;
		
	}
	
	public void close(){
		
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		/*
		CoOccurModelByArrays test = new CoOccurModelByArrays(new String[]{"ant", "bat", "cat", "dog"}, "test");
		test.addCoOccurrence("cat", "dog");
		test.addCoOccurrence("ant", "dog");
		
		test.saveModel();
		*/
		PPMIModelByArrays test = new PPMIModelByArrays("/home/lushan1/nlp/model/PositivePMI/Gutenberg2010AllW2", false);
		//test.saveModel();

		//CoOccurModelByArrays test2 = new CoOccurModelByArrays("3esl");
		
		/*
		CoOccurModelByArrays test = new CoOccurModelByArrays("simple");
		System.out.println(test.getCoOccurrence("ant", "cat"));
		test.addCoOccurrence("ant", "cat");
		test.addCoOccurrence("ANT", "cat");
		test.addCoOccurrence("DOG", "CAT");
		test.addFrequency("ant");
		System.out.println(test.getCoOccurrence("dog", "cat"));
		System.out.println("frequency is " + test.getFrequency("ant"));
		test.saveModel();
		*/
		
		System.out.println("Press any key to continue ...");
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		String dummy = input.readLine(); 

		test.printPCW(test.getDistributionalSimilarWordsWithSamePOS("twist_VB"), 400);
		
		System.out.println("done!");
		System.out.println("done!");
		System.out.println("done!");
		System.out.println("done!");
		System.out.println("done!");
		System.out.println("done!");
		System.out.println("done!");
		System.out.println("done!");
	}

}
