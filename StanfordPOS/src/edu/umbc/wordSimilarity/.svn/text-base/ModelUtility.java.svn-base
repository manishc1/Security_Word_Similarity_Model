package edu.umbc.wordSimilarity;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;


public class ModelUtility {

	public CoOccurModelByArrays model1;
	public CoOccurModelByArrays model2;

	public ModelUtility(String bigModelName, String smallModelName) {
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
		ModelUtility utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW3", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW2");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010/distribution/Gutenberg2010AllAt2");
		utility = null;
		System.gc();
		System.out.println("2 is done.");
		*/

		/*
		utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW4", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW3");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010/distribution/Gutenberg2010AllAt3");
		utility = null;
		System.gc();
		System.out.println("3 is done.");
		
		utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW5", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW4");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010/distribution/Gutenberg2010AllAt4");
		utility = null;
		System.gc();
		System.out.println("4 is done.");

		utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW6", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW5");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010/distribution/Gutenberg2010AllAt5");
		utility = null;
		System.gc();
		System.out.println("5 is done.");

		utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW7", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW6");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010/distribution/Gutenberg2010AllAt6");
		utility = null;
		System.gc();
		System.out.println("6 is done.");

		utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW8", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW7");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010/distribution/Gutenberg2010AllAt7");
		utility = null;
		System.gc();
		System.out.println("7 is done.");

		utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW9", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW8");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010/distribution/Gutenberg2010AllAt8");
		utility = null;
		System.gc();
		System.out.println("8 is done.");

		utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW10", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW9");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010/distribution/Gutenberg2010AllAt9");
		utility = null;
		System.gc();
		System.out.println("9 is done.");

		utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW11", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW10");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010/distribution/Gutenberg2010AllAt10");
		utility = null;
		System.gc();
		System.out.println("10 is done.");

		utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW12", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW11");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010/distribution/Gutenberg2010AllAt11");
		utility = null;
		System.gc();
		System.out.println("11 is done.");

		utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW13", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW12");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010/distribution/Gutenberg2010AllAt12");
		utility = null;
		System.gc();
		System.out.println("12 is done.");

		utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW14", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW13");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010/distribution/Gutenberg2010AllAt13");
		utility = null;
		System.gc();
		System.out.println("13 is done.");

		utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW15", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW14");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010/distribution/Gutenberg2010AllAt14");
		utility = null;
		System.gc();
		System.out.println("14 is done.");

		utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW16", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW15");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010/distribution/Gutenberg2010AllAt15");
		utility = null;
		System.gc();
		System.out.println("15 is done.");

		utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW17", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW16");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010/distribution/Gutenberg2010AllAt16");
		utility = null;
		System.gc();
		System.out.println("16 is done.");

		utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW18", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW17");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010/distribution/Gutenberg2010AllAt17");
		utility = null;
		System.gc();
		System.out.println("17 is done.");

		utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW19", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW18");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010/distribution/Gutenberg2010AllAt18");
		utility = null;
		System.gc();
		System.out.println("18 is done.");
		*/

		ModelUtility utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW20", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW19");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010/distribution/Gutenberg2010AllAt19");
		utility = null;
		System.gc();
		System.out.println("19 is done.");

		utility = new ModelUtility("/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW21", "/home/lushan1/nlp/model/Gutenberg2010/collection2/Gutenberg2010AllW20");
		utility.SaveDifferenceAsModel("/home/lushan1/nlp/model/Gutenberg2010/distribution/Gutenberg2010AllAt20");
		utility = null;
		System.gc();
		System.out.println("20 is done.");
		

		System.out.println("Congratulation! Task Finished.");

		//System.out.println("Max value is " + utility.model1.getMaxValue());
		//System.out.println("Min value is " + utility.model1.getMinValue());

	}

}
