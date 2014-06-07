package edu.umbc.similarity.dictionary;

import java.util.HashSet;
import java.util.StringTokenizer;

public class WordDefinition {

	int numberOfSenses;
	HashSet<String> derivativeSet;            		//1
	HashSet<String> synonymSet;						//2
	HashSet<String> synonymOfDerivativeSet;			//3
	HashSet<String> parentSet;					    //4
	HashSet<String> grandParentSet;			 		//5
	HashSet<String> grandGrandParentSet;			//6
	HashSet<String> synonymOfHypernymSet;			//7
	HashSet<String> childrenSet;					//8
	HashSet<String> setOfDefiningWords;				//9
	HashSet<String> setOfFrequentWords;				//10
	
	
	public WordDefinition(int p_numberOfSenses, HashSet<String> p_derivativeSet, HashSet<String> p_synonymSet, HashSet<String> p_synonymOfDerivativeSet, HashSet<String> p_hypernymSet,
			HashSet<String> p_hypernymOfHypernymSet, HashSet<String> p_hypernymOfHypernymOfHypernymSet, HashSet<String> p_synonymOfHypernymSet, HashSet<String> p_childrenSet, HashSet<String> p_setOfDefiningWords, HashSet<String> p_setOfFrequentWords) {
		// TODO Auto-generated constructor stub
		numberOfSenses = p_numberOfSenses;
		derivativeSet = p_derivativeSet;
		synonymSet = p_synonymSet;
		synonymOfDerivativeSet = p_synonymOfDerivativeSet;
		parentSet = p_hypernymSet;
		grandParentSet = p_hypernymOfHypernymSet;
		grandGrandParentSet = p_hypernymOfHypernymOfHypernymSet;
		synonymOfHypernymSet = p_synonymOfHypernymSet;
		childrenSet = p_childrenSet;
		setOfDefiningWords = p_setOfDefiningWords;
		setOfFrequentWords = p_setOfFrequentWords;
	}

	public String serialize(){
		
		StringBuffer result = new StringBuffer();

		result.append("0. ");
		result.append(numberOfSenses);
		result.append("\t");

		result.append("1. ");
		for (String derivative: derivativeSet){
			result.append(derivative + ", ");
		}
		result.append("\t");
		
		result.append("2. ");
		for (String synonym: synonymSet){
			result.append(synonym + ", ");
		}
		result.append("\t");

		result.append("3. ");
		for (String synonymOfDerivative: synonymOfDerivativeSet){
			result.append(synonymOfDerivative + ", ");
		}
		result.append("\t");
		
		result.append("4. ");
		for (String hypernym: parentSet){
			result.append(hypernym + ", ");
		}
		result.append("\t");
		
		result.append("5. ");
		for (String hypernymOfHypernym: grandParentSet){
			result.append(hypernymOfHypernym + ", ");
		}
		result.append("\t");

		result.append("6. ");
		for (String derivativeOfHypernym: grandGrandParentSet){
			result.append(derivativeOfHypernym + ", ");
		}
		result.append("\t");

		result.append("7. ");
		for (String synonymOfHypernym: synonymOfHypernymSet){
			result.append(synonymOfHypernym + ", ");
		}
		result.append("\t");

		result.append("8. ");
		for (String child: childrenSet){
			result.append(child + ", ");
		}
		result.append("\t");
		
		result.append("9. ");
		for (String definingWord: setOfDefiningWords){
			result.append(definingWord + ", ");
		}
		result.append("\t");
		
		result.append("10. ");
		for (String frequentWord: setOfFrequentWords){
			result.append(frequentWord + ", ");
		}
		
		return result.toString();
		
	}
	
	public WordDefinition(String serialization) throws Exception{
		
		int preindex = 0;
		int index = 0;
		String item;
		int itemNo;

		index = serialization.indexOf('\t', preindex);
		item = serialization.substring(preindex, index);
		itemNo = Integer.valueOf(item.substring(0, 1));
		if (itemNo != 0)
			throw new Exception("wrong format at " + itemNo + " in " + serialization);
		numberOfSenses = Integer.valueOf(item.substring(2, item.length()).trim());
		preindex = index + 1;

		index = serialization.indexOf('\t', preindex);
		item = serialization.substring(preindex, index);
		itemNo = Integer.valueOf(item.substring(0, 1));
		if (itemNo != 1)
			throw new Exception("wrong format at " + itemNo + " in " + serialization);
		String serializedDerivativeSet = item.substring(2, item.length()).trim();
		StringTokenizer st = new StringTokenizer(serializedDerivativeSet, ",");
		derivativeSet = new HashSet<String>();
		while (st.hasMoreTokens()){
			derivativeSet.add(st.nextToken().trim());
		}
		preindex = index + 1;
		
		index = serialization.indexOf('\t', preindex);
		item = serialization.substring(preindex, index);
		itemNo = Integer.valueOf(item.substring(0, 1));
		if (itemNo != 2)
			throw new Exception("wrong format at " + itemNo + " in " + serialization);
		String serializedSynonymSet = item.substring(2, item.length()).trim();
		st = new StringTokenizer(serializedSynonymSet, ",");
		synonymSet = new HashSet<String>();
		while (st.hasMoreTokens()){
			synonymSet.add(st.nextToken().trim());
		}
		preindex = index + 1;
		
		index = serialization.indexOf('\t', preindex);
		item = serialization.substring(preindex, index);
		itemNo = Integer.valueOf(item.substring(0, 1));
		if (itemNo != 3)
			throw new Exception("wrong format at " + itemNo + " in " + serialization);
		String serializedSynonymOfDerivativeSet = item.substring(2, item.length()).trim();
		st = new StringTokenizer(serializedSynonymOfDerivativeSet, ",");
		synonymOfDerivativeSet = new HashSet<String>();
		while (st.hasMoreTokens()){
			synonymOfDerivativeSet.add(st.nextToken().trim());
		}
		preindex = index + 1;
		
		index = serialization.indexOf('\t', preindex);
		item = serialization.substring(preindex, index);
		itemNo = Integer.valueOf(item.substring(0, 1));
		if (itemNo != 4)
			throw new Exception("wrong format at " + itemNo + " in " + serialization);
		String serializedHypernymSet = item.substring(2, item.length()).trim();
		st = new StringTokenizer(serializedHypernymSet, ",");
		parentSet = new HashSet<String>();
		while (st.hasMoreTokens()){
			parentSet.add(st.nextToken().trim());
		}
		preindex = index + 1;
			
		index = serialization.indexOf('\t', preindex);
		item = serialization.substring(preindex, index);
		itemNo = Integer.valueOf(item.substring(0, 1));
		if (itemNo != 5)
			throw new Exception("wrong format at " + itemNo + " in " + serialization);
		String serializedHypernymOfHypernymSet = item.substring(2, item.length()).trim();
		st = new StringTokenizer(serializedHypernymOfHypernymSet, ",");
		grandParentSet = new HashSet<String>();
		while (st.hasMoreTokens()){
			grandParentSet.add(st.nextToken().trim());
		}
		preindex = index + 1;
		
		index = serialization.indexOf('\t', preindex);
		item = serialization.substring(preindex, index);
		itemNo = Integer.valueOf(item.substring(0, 1));
		if (itemNo != 6)
			throw new Exception("wrong format at " + itemNo + " in " + serialization);
		String serializedDerivativeOfHypernymSet = item.substring(2, item.length()).trim();
		st = new StringTokenizer(serializedDerivativeOfHypernymSet, ",");
		grandGrandParentSet = new HashSet<String>();
		while (st.hasMoreTokens()){
			grandGrandParentSet.add(st.nextToken().trim());
		}
		preindex = index + 1;

		index = serialization.indexOf('\t', preindex);
		item = serialization.substring(preindex, index);
		itemNo = Integer.valueOf(item.substring(0, 1));
		if (itemNo != 7)
			throw new Exception("wrong format at " + itemNo + " in " + serialization);
		String serializedSynonymOfHypernymSet = item.substring(2, item.length()).trim();
		st = new StringTokenizer(serializedSynonymOfHypernymSet, ",");
		synonymOfHypernymSet = new HashSet<String>();
		while (st.hasMoreTokens()){
			synonymOfHypernymSet.add(st.nextToken().trim());
		}
		preindex = index + 1;

		index = serialization.indexOf('\t', preindex);
		item = serialization.substring(preindex, index);
		itemNo = Integer.valueOf(item.substring(0, 1));
		if (itemNo != 8)
			throw new Exception("wrong format at " + itemNo + " in " + serialization);
		String serializedChildrenSet = item.substring(2, item.length()).trim();
		st = new StringTokenizer(serializedChildrenSet, ",");
		childrenSet = new HashSet<String>();
		while (st.hasMoreTokens()){
			childrenSet.add(st.nextToken().trim());
		}
		preindex = index + 1;

		index = serialization.indexOf('\t', preindex);
		item = serialization.substring(preindex, index);
		itemNo = Integer.valueOf(item.substring(0, 1));
		if (itemNo != 9)
			throw new Exception("wrong format at " + itemNo + " in " + serialization);
		String serializedSetOfDefiningWords = item.substring(2, item.length()).trim();
		st = new StringTokenizer(serializedSetOfDefiningWords, ",");
		setOfDefiningWords = new HashSet<String>();
		while (st.hasMoreTokens()){
			setOfDefiningWords.add(st.nextToken().trim());
		}
		preindex = index + 1;

		/*
		index = serialization.indexOf('\t', preindex);
		item = serialization.substring(preindex, index);
		itemNo = Integer.valueOf(item.substring(0, 2));
		if (itemNo != 10)
			throw new Exception("wrong format at " + itemNo + " in " + serialization);
		String serializedSetOfFrequentWords = item.substring(2, item.length()).trim();
		st = new StringTokenizer(serializedSetOfFrequentWords, ",");
		setOfFrequentWords = new HashSet<String>();
		while (st.hasMoreTokens()){
			setOfFrequentWords.add(st.nextToken().trim());
		}
		preindex = index + 1;
		*/

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
