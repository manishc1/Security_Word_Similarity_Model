package edu.umbc.gor.experiments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

public class QA_TestCase {
	
	public String description;
	public String questionType;
	public String questionNo;
	public ArrayList<String> relations;
	public ArrayList<HashSet<String>> answerSets;

	public QA_TestCase(String paragraph, String qType, String qNo) throws Exception {
		
		description = "";
		relations = new ArrayList<String>();
		answerSets = new ArrayList<HashSet<String>>();
		questionType = qType;
		questionNo = qNo;
		
		BufferedReader reader = new BufferedReader(new StringReader(paragraph));
		
		String line;

		while ((line = reader.readLine()) != null){
			
			if (line.startsWith("#")){
				if (line.startsWith("# DS") || line.startsWith("# IS") || line.startsWith("# DM") || line.startsWith("# IM")){
					description += line.substring(2, line.length()) + "\n";
					int index = line.indexOf('.');
					questionType = line.substring(2, 4);
					questionNo = line.substring(5, index);
				}
				
			}else if (line.startsWith(">")){
				HashSet<String> answerSet = new HashSet<String>();
				
				String answers = line.substring(1, line.length());
				
				StringTokenizer st = new StringTokenizer(answers, " ");
				
				while (st.hasMoreElements()){
					
					answerSet.add(st.nextToken());
				}

				answerSets.add(answerSet);
				
			}else
				relations.add(line);
		}
		
		if (relations.size() < 1 || answerSets.size() < 1 || answerSets.get(0).size() < 1)
			throw new Exception("wrong format in test case: " + paragraph);
		
	}
	
	public String toString(){
		
		return questionType + questionNo + ": " + relations.toString();
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
