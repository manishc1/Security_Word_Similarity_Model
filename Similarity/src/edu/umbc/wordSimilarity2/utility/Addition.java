package edu.umbc.wordSimilarity2.utility;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import edu.umbc.wordSimilarity2.CoOccurModelByArrays;


public class Addition {

	public CoOccurModelByArrays model1;
	public CoOccurModelByArrays model2;

	public Addition(String bigModelName, String smallModelName) {
		// TODO Auto-generated constructor stub
		model1 = new CoOccurModelByArrays(bigModelName, false);
		model2 = new CoOccurModelByArrays(smallModelName, false);
	}

	public int SaveDifferenceAsModel(String modelName, int ratio1, int ratio2){
		
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
					outOfCoOccurModel.print(model1.wordMatrix[i][j] / ratio1 + model2.wordMatrix[i][j] / ratio2);
					
					if (j != model1.sizeOfVocabulary -1)
						outOfCoOccurModel.print(",");
				}
				outOfCoOccurModel.println();
			}
			
			for (int i = 0; i < model1.sizeOfVocabulary; i++){
				outOfFrequency.print(model1.frequency[i] / ratio1 + model2.frequency[i] / ratio2);
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
		
		//Addition utility = new Addition("/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW5", "/home/lushan1/nlp/model/Wikipedia2006sfd/Wikipedia2006AllW5");
		//utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/combine/Gutenberg2010_Wikipedia2006_AllW5", 10, 1);

		Addition utility = new Addition("/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW5", "/home/lushan1/nlp/model/ukwac2012sfd/ukwac2012AllW5");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/combine/gutn_ukwac_AllW5", 1, 1);

		
		utility = null;
		System.gc();
		System.out.println("work is done.");

		System.out.println("Congratulation! Task Finished.");

		//System.out.println("Max value is " + utility.model1.getMaxValue());
		//System.out.println("Min value is " + utility.model1.getMinValue());

	}

}
