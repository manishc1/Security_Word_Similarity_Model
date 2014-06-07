package edu.umbc.wordSimilarity2.utility;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import edu.umbc.wordSimilarity2.CoOccurModelByArrays;


public class Subtract {

	public CoOccurModelByArrays model1;
	public CoOccurModelByArrays model2;

	public Subtract(String bigModelName, String smallModelName) {
		// TODO Auto-generated constructor stub
		model1 = new CoOccurModelByArrays(bigModelName, false);
		model2 = new CoOccurModelByArrays(smallModelName, false);
	}

	public int SaveDifferenceAsModel(String modelName){
		
		try{
			FileWriter fileOfVocabulary = new FileWriter(modelName + ".voc");
			PrintWriter outOfVocabulary = new PrintWriter(new BufferedWriter(fileOfVocabulary, 1000000));
			
			FileWriter fileOfCoOccurModel = new FileWriter(modelName + ".mdl");
			PrintWriter outOfCoOccurModel = new PrintWriter(new BufferedWriter(fileOfCoOccurModel, 4000000));

			FileWriter fileOfFrequency = new FileWriter(modelName + ".frq");
			PrintWriter outOfFrequency = new PrintWriter(new BufferedWriter(fileOfFrequency, 1000000));
			
			outOfVocabulary.println(model1.vocabulary.length);
			
			for (String word:model1.vocabulary){
				outOfVocabulary.print(word);
				outOfVocabulary.println();
			}
			
			for (int i = 0; i < model1.sizeOfVocabulary; i++){
				for (int j = 0; j < model1.sizeOfVocabulary; j++){
					outOfCoOccurModel.print(model1.wordMatrix[i][j] - model2.wordMatrix[i][j]);
					
					if (j != model1.sizeOfVocabulary -1)
						outOfCoOccurModel.print(",");
				}
				outOfCoOccurModel.println();
			}
			
			for (int i = 0; i < model1.sizeOfVocabulary; i++){
				outOfFrequency.print(model1.frequency[i]);
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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/*
		ModelUtility utility;
		
		for (int i=16; i<18; i++){
			
			utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW" + (i+1), "/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW" + i);
			utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010sfd/distribution/Gutenberg2010AllAt" + i);
			utility = null;
			System.gc();
			System.out.println(i + " is done.");
		}
		*/
		
		Subtract utility = new Subtract("/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW41", "/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW6");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010W6to41");
		utility = null;
		System.gc();
		System.out.println("work is done.");

		System.out.println("Congratulation! Task Finished.");

		//System.out.println("Max value is " + utility.model1.getMaxValue());
		//System.out.println("Min value is " + utility.model1.getMinValue());

	}

}
