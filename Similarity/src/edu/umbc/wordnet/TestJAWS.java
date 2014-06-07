package edu.umbc.wordnet;

import edu.smu.tspell.wordnet.*;

/**
 * Displays word forms and definitions for synsets containing the word form
 * specified on the command line. To use this application, specify the word
 * form that you wish to view synsets for, as in the following example which
 * displays all synsets containing the word form "airplane":
 * <br>
 * java TestJAWS airplane
 */
public class TestJAWS
{

	/**
	 * Main entry point. The command-line arguments are concatenated together
	 * (separated by spaces) and used as the word form to look up.
	 */
	public static void main(String[] args)
	{
		//String[] pos = new String[]{"noun", "verb", "adj", "adv"};
		String[] input = new String[]{"persistent"}; 
		
		System.setProperty("wordnet.database.dir", "/opt/WordNet-3.0/dict/");
		
			//  Concatenate the command-line arguments
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < input.length; i++)
			{
				buffer.append((i > 0 ? " " : "") + input[i]);
			}
			String wordForm = buffer.toString();
			//  Get the synsets containing the wrod form
			WordNetDatabase database = WordNetDatabase.getFileInstance();
			Synset[] synsets = database.getSynsets(wordForm);
			//  Display the word forms and definitions for synsets retrieved
			if (synsets.length > 0)
			{
				System.out.println("The following synsets contain '" +
						wordForm + "' or a possible base form " +
						"of that text:");
				
				for (int i = 0; i < synsets.length; i++)
				{
					System.out.println("");
					System.out.print("(" + synsets[i].getType() + ")");
					System.out.print("(" + synsets[i].getTagCount(wordForm) + ")");
					String[] wordForms = synsets[i].getWordForms();
					for (int j = 0; j < wordForms.length; j++)
					{
						System.out.print((j > 0 ? ", " : "") +
								wordForms[j]);
					}
					System.out.println(": " + synsets[i].getDefinition());
					
					for (WordSense sense : synsets[i].getDerivationallyRelatedForms(wordForm)){
						System.out.print(sense.getWordForm() + " ");
					}	
					
					System.out.println();
					
				}
			}
			else
			{
				System.err.println("No synsets exist that contain " +
						"the word form '" + wordForm + "'");
			}
	}

}
