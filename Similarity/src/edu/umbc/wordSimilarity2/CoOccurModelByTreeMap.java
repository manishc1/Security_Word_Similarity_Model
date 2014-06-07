/*
 * Copyright 2009 Author: Lushan Han at Ebiquity Lab, UMBC
 *  
 */
package edu.umbc.wordSimilarity2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;

public class CoOccurModelByTreeMap implements Serializable {

	public TreeMap<Integer, Integer>[] wordMatrix;
	public int sizeOfVocabulary;
	public String[] vocabulary;
	public int frequency[];
	public String modelName;
	public long totalWords;
	//public static int FrequencyLimit = 2000000;
	public int FrequencyLimit = 2000000;
	public boolean REMOVE_NON_FREQUENT_WORDS = true;
	public int CO_OCCUR_THRESHOLD = 1;
	public double CONDITIONAL_THRESHOLD = (double) 1 / 1000; // 1 / 1000;
	
	public CoOccurModelByTreeMap(String[] sortedTermList, String model_name){
		vocabulary = sortedTermList;
		sizeOfVocabulary = vocabulary.length;
		
		wordMatrix = (TreeMap<Integer, Integer>[]) new TreeMap[sizeOfVocabulary];
		
		for (int i=0; i<sizeOfVocabulary; i++){
			wordMatrix[i] = new TreeMap<Integer, Integer>();
		}
		
		frequency = new int[sizeOfVocabulary];
		totalWords = 0;
		modelName = model_name;
	}
	
	public CoOccurModelByTreeMap(String filename, boolean existAlert){
		
		try{
		
			modelName = filename;
			/* Read vocabulary */
			File vocabularyFile = new File(filename + ".voc");
			BufferedReader vocabularyReader = new BufferedReader(new FileReader(vocabularyFile), 1000000);
			
			String rdline;
			rdline = vocabularyReader.readLine();
			sizeOfVocabulary = Integer.valueOf(rdline);
			vocabulary = new String[sizeOfVocabulary];
			
			wordMatrix = (TreeMap<Integer, Integer>[]) new TreeMap[sizeOfVocabulary];

			for (int i=0; i<sizeOfVocabulary; i++){
				wordMatrix[i] = new TreeMap<Integer, Integer>();
			}
			
			frequency = new int[sizeOfVocabulary];
			totalWords = 0;
			
			for (int i = 0; i < sizeOfVocabulary; i++){
				vocabulary[i] = vocabularyReader.readLine();
			}
		
			vocabularyReader.close();
			Arrays.sort(vocabulary);
			
            File coOccurModelFile = new File(filename + ".mdl2");
            if (coOccurModelFile.exists()){
                    if (existAlert)
                    	throw new Exception("The model already exists! Thus the program quit.");
                    else
                    	System.out.println("Overwriting an existing model!");
            }else
            	System.out.println("The model doesn't exist. Create a new one!");
			

		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
			System.exit(-1);
		}
	}
	
	
	public CoElement[] getSortedCoWords(String word){
		
		int index1 = index(word);
		CoElement[] coWords = new CoElement[sizeOfVocabulary];
		
		for (int i=0; i<sizeOfVocabulary; i++){
			
			CoElement element = new CoElement(vocabulary[i], getCoOccurrence(index1, i));
			coWords[i] = element;
		}
		
		Arrays.sort(coWords);
		
		return coWords;
		
	}

	public FloatElement[] getSortedPMIWords(String word){
		
		int index1 = index(word);
		FloatElement[] correlateWords = new FloatElement[sizeOfVocabulary];
		
		for (int i=0; i<sizeOfVocabulary; i++){
			
			float pmi = 0;
			
			try {
				pmi = getPMI(index1, i);
				
				if ((double) getCoOccurrence(index1, i) / getFrequency(index1) < CONDITIONAL_THRESHOLD)
					pmi = Float.NEGATIVE_INFINITY;
				
				if (REMOVE_NON_FREQUENT_WORDS && (getFrequency(i) < totalWords / FrequencyLimit)) 
					pmi = Float.NEGATIVE_INFINITY;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				pmi = Float.NEGATIVE_INFINITY;
			}
			
			FloatElement element = new FloatElement(vocabulary[i], pmi);
			correlateWords[i] = element;
		}
		
		Arrays.sort(correlateWords);
		
		return correlateWords;
		
	}
	
	public float getPMI(String word1, String word2) throws Exception{

		double frqWord1 = getFrequency(word1);
		
		double frqWord2 = getFrequency(word2);
		
		/*
		if (frqWord1 > totalWords / 5000) 
			frqWord1 = totalWords / 5000 + (frqWord1 - totalWords / 5000) * 0.5;
		
		if (frqWord2 > totalWords / 5000) 
			frqWord2 = totalWords / 5000 + (frqWord2 - totalWords / 5000) * 0.5;
		*/
		
		double frqWord1Word2 = getCoOccurrence(word1, word2);
		if (frqWord1Word2 < CO_OCCUR_THRESHOLD) 
			throw new Exception(word1 + " has no or rare co-occurrence with " + word2);

		if (frqWord1 == 0 || frqWord2 == 0) 
			throw new Exception(word1 + " or " + word2 + " has zero frequency");
		
		double pmi = Math.log(((double) frqWord1Word2 * totalWords) / ((double) frqWord1 * frqWord2));
		//double pmi = Math.log(((double) frqWord1Word2 * totalWords) / ( Math.pow(frqWord1, 1) * Math.pow(frqWord2, 1)));
		return (float) pmi; 
	}

	
	public float getPMI(int index1, int index2) throws Exception{

		double frqWord1 = getFrequency(index1);
		
		double frqWord2 = getFrequency(index2);
		
		/*
		if (frqWord1 > totalWords / 5000) 
			frqWord1 = totalWords / 5000 + (frqWord1 - totalWords / 5000) * 0.5;
		
		if (frqWord2 > totalWords / 5000) 
			frqWord2 = totalWords / 5000 + (frqWord2 - totalWords / 5000) * 0.5;
		*/

		double frqWord1Word2 = getCoOccurrence(index1, index2);
		
		if (frqWord1Word2 < CO_OCCUR_THRESHOLD) 
			throw new Exception(vocabulary[index1] + " has no or rare co-occurrence with " + vocabulary[index2]);

		if (frqWord1 == 0 || frqWord2 == 0) 
			throw new Exception(vocabulary[index1] + " or " + vocabulary[index2] + " has zero frequency");
		
		double pmi = Math.log(((double) frqWord1Word2 * totalWords) / ((double) frqWord1 * frqWord2));
		//double pmi = Math.log(((double) frqWord1Word2 * totalWords) / ( Math.pow(frqWord1, 1) * Math.pow(frqWord2, 1)));
		return (float) pmi; 
	}
	
		
	public int getPmiOrder(String word1, String word2) throws Exception{
		
		FloatElement[] pmiSortedWords = getSortedPMIWords(word1);
		
		for (int i=0; i < pmiSortedWords.length; i++){
			
			if (pmiSortedWords[i].word.equals(word2))
				return i;
				
		}
		
		throw new Exception("The word " + word2 + " is not in the vocabulary");
	}
	
	
	public float getVectorSimilarity(String question, String choice, int threshold) throws Exception{
		
		FloatElement[] correlatedVectorForWord1 = getSortedPMIWords(question);
		FloatElement[] correlatedVectorForWord2 = getSortedPMIWords(choice);
		
		double square_sum_1 = 0;
		double square_sum_2 = 0;
		HashMap<String, Double> choiceMap = new HashMap<String, Double>(threshold);
		DoubleElement[] questionVector = new DoubleElement[threshold];
		
		for (int i=0; i<threshold; i++){
			square_sum_1 += Math.pow(Math.E, 2 * correlatedVectorForWord1[i].value);
			square_sum_2 += Math.pow(Math.E, 2 * correlatedVectorForWord2[i].value);
			//square_sum_1 += Math.pow(correlatedVectorForWord1[i].value, 2);
			//square_sum_2 += Math.pow(correlatedVectorForWord2[i].value, 2);
		}
		
		double norm1 = Math.sqrt(square_sum_1);
		double norm2 = Math.sqrt(square_sum_2);
		
		for (int i=0; i<threshold; i++){
			double newvalue1 = Math.pow(Math.E, correlatedVectorForWord1[i].value);
			//double newvalue1 = correlatedVectorForWord1[i].value;
			questionVector[i] = new DoubleElement(correlatedVectorForWord1[i].word, newvalue1 / norm1);

			double newvalue2 = Math.pow(Math.E, correlatedVectorForWord2[i].value);
			//double newvalue2 = correlatedVectorForWord2[i].value;
			choiceMap.put(correlatedVectorForWord2[i].word, newvalue2 / norm2);
		}
		
		double dot_product = 0;
		
		for (DoubleElement questionCoWord : questionVector){
			
			Double value = choiceMap.get(questionCoWord.word);
			if (value != null){
				dot_product += value * questionCoWord.value;
				System.out.print(questionCoWord.word + " ");
			}
			
		}
		
		System.out.println();
		return (float) dot_product;
	}
	
	
	/*
	public int[] getVector(String word){
		int index1 = index(word);
		return wordMatrix[index1]; 
	}
	*/
	
	
	public int getMaxValue(){

		int maxValue = 0;
		
		for (int i=0; i < sizeOfVocabulary; i++){
			
			for (Entry<Integer, Integer> entry :wordMatrix[i].entrySet()){
				if (entry.getValue() > maxValue)
					maxValue = entry.getValue();
			}
		}
		
		return maxValue;
	}
	
	public int getComputedTotalwords(){
		
		int computedTotalwords = 0;
		for (int i = 0; i < sizeOfVocabulary; i++){
			computedTotalwords += frequency[i];
		}
		return computedTotalwords;
	}

	public int getMinValue(){

		int minValue = Integer.MAX_VALUE;
		
		for (int i=0; i < sizeOfVocabulary; i++){
			
			for (Entry<Integer, Integer> entry :wordMatrix[i].entrySet()){
				if (entry.getValue() < minValue)
					minValue = entry.getValue();
			}
		}
		
		return minValue;
	}
	
	
	public int index(String word){
		return Arrays.binarySearch(vocabulary, word);
	}
	
	public void addCoOccurrence(String word1, String word2){
		
		int index1 = index(word1);
		int index2 = index(word2);
		
		if (index1 >=0 && index2 >= 0){
			
			if (index1 <= index2){
				
				Integer coOccurs = wordMatrix[index1].get(index2);
				
				if (coOccurs == null){
					wordMatrix[index1].put(index2, 1);
				}else{
					wordMatrix[index1].put(index2, ++ coOccurs);
				}

			}else {
				
				Integer coOccurs = wordMatrix[index2].get(index1);
				
				if (coOccurs == null){
					wordMatrix[index2].put(index1, 1);
				}else{
					wordMatrix[index2].put(index1, ++ coOccurs);
				}
			}
		}
	}


	public void addCoOccurrence(int index1, int index2){
		
		if (index1 >=0 && index2 >= 0){
			
			if (index1 <= index2){
				
				Integer coOccurs = wordMatrix[index1].get(index2);
				
				if (coOccurs == null){
					wordMatrix[index1].put(index2, 1);
				}else{
					wordMatrix[index1].put(index2, ++ coOccurs);
				}

			}else {
				
				Integer coOccurs = wordMatrix[index2].get(index1);
				
				if (coOccurs == null){
					wordMatrix[index2].put(index1, 1);
				}else{
					wordMatrix[index2].put(index1, ++ coOccurs);
				}
			}
			
		}
	}
	
	public int addFrequency(String word){
		int index = index(word);
		
		if (index >= 0){
			frequency[index] ++;
		}
		return index; 
	}
	
	public int getCoOccurrence(String word1, String word2){
		
		int index1 = index(word1);
		int index2 = index(word2);
		
		if (index1 >=0 && index2 >= 0){
		
			if (index1 <= index2){
				
				Integer coOccurs = wordMatrix[index1].get(index2);
				
				if (coOccurs == null){
					return 0;
				}else{
					return coOccurs;
				}

			}else {
				
				Integer coOccurs = wordMatrix[index2].get(index1);
				
				if (coOccurs == null){
					return 0;
				}else{
					return coOccurs;
				}
			}
		
		}else
			return 0;
		
	}

	public int getCoOccurrence(int index1, int index2){
		
		if (index1 >=0 && index2 >= 0){
			
			if (index1 <= index2){
				
				Integer coOccurs = wordMatrix[index1].get(index2);
				
				if (coOccurs == null){
					return 0;
				}else{
					return coOccurs;
				}

			}else {
				
				Integer coOccurs = wordMatrix[index2].get(index1);
				
				if (coOccurs == null){
					return 0;
				}else{
					return coOccurs;
				}
			}
		
		}else
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
	
	public int saveSortedVocabularyl(){
		
		try{
			FileWriter fileOfVocabulary = new FileWriter(modelName + ".voc");
			PrintWriter outOfVocabulary = new PrintWriter(new BufferedWriter(fileOfVocabulary, 1000000));
			
			outOfVocabulary.println(vocabulary.length);
			
			for (String word:vocabulary){
				outOfVocabulary.print(word);
				outOfVocabulary.println();
			}
		
			outOfVocabulary.close();
			
		} catch (Exception e){
			System.out.println(e.getMessage());
			return -1;
		}
		
		return 0;
		
	}
	
	public void close(){
		
	}
	
	/**
	 * @throws ClassNotFoundException 
	 * @param args
	 * @throws IOException 
	 * @throws  
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		
		/*
		CoOccurModelByArrays test = new CoOccurModelByArrays(new String[]{"ant", "bat", "cat", "dog"}, "test");
		test.addCoOccurrence("cat", "dog");
		test.addCoOccurrence("ant", "dog");
		
		test.saveModel();
		*/
		/*
		CoOccurModelByTreeMap test = new CoOccurModelByTreeMap("gutenberg2", false);

		//CoOccurModelByArrays test2 = new CoOccurModelByArrays("3esl");
		
		//CoOccurModelByArrays test = new CoOccurModelByArrays("simple");
		test.addCoOccurrence("ant_NN", "dog_NN");
		test.addCoOccurrence("dog_NN", "cat_NN");
		test.addCoOccurrence("cat_NN", "dog_NN");
		test.addCoOccurrence("dog_NN", "dog_NN");
		test.addFrequency("dog_NN");
		test.addFrequency("dog_NN");
		System.out.println(test.getCoOccurrence("ant_NN", "cat_NN"));
		System.out.println(test.getCoOccurrence("dog_NN", "cat_NN"));
		System.out.println(test.getCoOccurrence("dog_NN", "dog_NN"));
		System.out.println(test.getCoOccurrence("cat_NN", "cat_NN"));
		System.out.println("frequency is " + test.getFrequency("dog_NN"));
		//test.saveModel();
		
		System.out.println("done!");
		
		
		FileOutputStream fileOut;
		ObjectOutputStream objOut;
		fileOut = new FileOutputStream("gutenberg.mdl2");
		objOut = new ObjectOutputStream(fileOut);
		objOut.writeObject(test);
		objOut.close();
		
		System.out.println("seriliazation done!");

        FileInputStream fileIn = new FileInputStream("gutenberg.mdl2");
        ObjectInputStream objIn = new ObjectInputStream(fileIn);
        CoOccurModelByTreeMap test2 = (CoOccurModelByTreeMap) objIn.readObject();

		System.out.println("recreation done!");

		System.out.println(test.getCoOccurrence("ant_NN", "cat_NN"));
		System.out.println(test.getCoOccurrence("dog_NN", "cat_NN"));
		System.out.println(test.getCoOccurrence("dog_NN", "dog_NN"));
		System.out.println(test.getCoOccurrence("cat_NN", "cat_NN"));
		System.out.println("frequency is " + test.getFrequency("dog_NN"));
		*/
        FileInputStream fileIn = new FileInputStream("/home/lushan1/nlp/test/model/test.mdl2");
        ObjectInputStream objIn = new ObjectInputStream(fileIn);
        CoOccurModelByTreeMap test2 = (CoOccurModelByTreeMap) objIn.readObject();

		System.out.println("recreation done!");
		System.out.println("Totoal words: " + test2.totalWords);
		System.out.println("computed totoal words: " + test2.getComputedTotalwords());
		
		System.out.println(test2.getCoOccurrence("ant_NN", "cat_NN"));
		System.out.println(test2.getCoOccurrence("dog_NN", "cat_NN"));
		System.out.println(test2.getCoOccurrence("dog_NN", "dog_NN"));
		System.out.println(test2.getCoOccurrence("cat_NN", "cat_NN"));
		

	}

}
