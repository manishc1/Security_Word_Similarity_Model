package edu.umbc.wordSimilarity2.utility;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import edu.umbc.wordSimilarity2.CoOccurModelByArrays;


public class CoOccurrenceToPositivePMI {

	public CoOccurModelByArrays model;

	public CoOccurrenceToPositivePMI(String originalModelName) {
		// TODO Auto-generated constructor stub
		model = new CoOccurModelByArrays(originalModelName, false);
	}

	public int SaveTranformedModel(String transformedModelName){
		
		try{
			FileWriter fileOfCoOccurModel = new FileWriter(transformedModelName + ".ppmi_mdl");
			PrintWriter outOfCoOccurModel = new PrintWriter(new BufferedWriter(fileOfCoOccurModel, 4000000));
			
			for (int i = 0; i < model.sizeOfVocabulary; i++){
				
				for (int j = 0; j < model.sizeOfVocabulary; j++){
					
					float pmi;
					
					try {
						
						pmi = model.getPMI(i, j);
						
						if (pmi < 0)
							pmi = 0;

						//}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//pmi = Float.NEGATIVE_INFINITY;
						pmi = 0;
					}
					
					outOfCoOccurModel.print(pmi);
					
					if (j != model.sizeOfVocabulary -1)
						outOfCoOccurModel.print(",");
				}
				outOfCoOccurModel.println();
			}
			
			
			outOfCoOccurModel.close();
		
		} catch (Exception e){
			System.out.println(e.getMessage());
			return -1;
		}
		
		return 0;		
	}


	/**
	 * 
	 * @param transformedModelName
	 * Not completed.
	 * 
	 * @return
	 */
	public int SaveTranformedModel_PMI_max(String transformedModelName){
		
		try{
			FileWriter fileOfCoOccurModel = new FileWriter(transformedModelName + ".ppmi_max_mdl");
			PrintWriter outOfCoOccurModel = new PrintWriter(new BufferedWriter(fileOfCoOccurModel, 4000000));
			
			for (int i = 0; i < model.sizeOfVocabulary; i++){
				
				for (int j = 0; j < model.sizeOfVocabulary; j++){
					
					float pmi;
					
					try {
						
						pmi = model.getPMI(i, j);
						
						if (pmi < 0)
							pmi = 0;

						//}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//pmi = Float.NEGATIVE_INFINITY;
						pmi = 0;
					}
					
					outOfCoOccurModel.print(pmi);
					
					if (j != model.sizeOfVocabulary -1)
						outOfCoOccurModel.print(",");
				}
				outOfCoOccurModel.println();
			}
			
			
			outOfCoOccurModel.close();
		
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
		
		CoOccurrenceToPositivePMI utility = new CoOccurrenceToPositivePMI("/home/lushan1/nlp/model/Gutenberg2010sfd/Gutenberg2010AllW2");
		utility.SaveTranformedModel("/home/lushan1/nlp/model/PositivePMI/Gutenberg2010AllW2");

		System.out.println("Congratulation! Task Finished.");

		//System.out.println("Max value is " + utility.model1.getMaxValue());
		//System.out.println("Min value is " + utility.model1.getMinValue());

	}

}
