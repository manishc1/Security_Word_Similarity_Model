package edu.umbc.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.umbc.wordSimilarity2.CoOccurModelByArrays;

public class GenerateSynonymPairs {

	public BufferedReader synonymReader;
	String POS;
	public CoOccurModelByArrays model;
	private PrintWriter synonymPairsOut;

	public GenerateSynonymPairs(String InfileName, String pos, String modelname) {
		// TODO Auto-generated constructor stub
		try{
			File synonymCollection = new File(InfileName);
			synonymReader = new BufferedReader(new FileReader(synonymCollection));
			POS = pos;
			
			synonymPairsOut = new PrintWriter(new FileWriter("/home/lushan1/nlp/evaluation/synonymPairs_all_" + POS + ".txt"));
			model = new CoOccurModelByArrays(modelname, false);
			
		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
			System.exit(-1);
		}

	}
	
	
	public void process() throws IOException{
		
		String textLine;
		
		while ((textLine = synonymReader.readLine()) != null){

			if (textLine.trim().equals(""))
				continue;
			
			TestEntryWrapper entry = new TestEntryWrapper(textLine);
			
			// if (entry.numberOfSenses > 1) continue;

			for (String synonym: entry.allSynonyms){
				
				if (model.getCoOccurrence(entry.targetWord + "_" + POS, synonym + "_" + POS) > 200){
					
					synonymPairsOut.println(entry.targetWord + "_" + POS + " " + synonym + "_" + POS);
				}
			}

			/*
			if (model.getCoOccurrence(entry.targetWord + "_" + POS, entry.firstSynonym + "_" + POS) > 200){
				
				synonymPairsOut.println(entry.targetWord + "_" + POS + " " + entry.firstSynonym + "_" + POS);
			}
			*/

		
		}
		
		synonymReader.close();
		synonymPairsOut.close();

	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		GenerateSynonymPairs run = new GenerateSynonymPairs("/home/lushan1/nlp/evaluation/advSynonyms700.txt", "RB", 
				"/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW41");
		
		run.process();
		
		System.out.println("Work completed!");
	}

}
