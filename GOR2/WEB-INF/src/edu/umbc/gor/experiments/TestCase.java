package edu.umbc.gor.experiments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;

public class TestCase {
	
	public String description;
	public String questionType;
	public String questionNo;
	public ArrayList<String> relations;
	public HashSet<String> correct_mappings;

	public TestCase(String paragraph, String qType, String qNo) throws Exception {
		
		description = "";
		relations = new ArrayList<String>();
		correct_mappings = new HashSet<String>();
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
				
			}else if (line.startsWith(">"))
				correct_mappings.add(line.substring(1, line.length()));
			else
				relations.add(line);
		}
		
		if (relations.size() < 1 || correct_mappings.size() < 1)
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
