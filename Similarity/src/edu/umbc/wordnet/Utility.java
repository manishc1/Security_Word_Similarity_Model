package edu.umbc.wordnet;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Set;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.dictionary.Dictionary;
import net.didion.jwnl.test.generic.TestDefaults;

public class Utility {

	Dictionary wordnet;
	
	public Utility() throws Exception {
		// TODO Auto-generated constructor stub
		JWNL.initialize(new FileInputStream(TestDefaults.CONFIG_PATH + TestDefaults.FILE_CONFIG_NAME));
		wordnet = Dictionary.getInstance();
	}

	public Utility(String path) throws Exception {
		// TODO Auto-generated constructor stub
		JWNL.initialize(new FileInputStream(path + "config" + File.separator + TestDefaults.FILE_CONFIG_NAME));
		wordnet = Dictionary.getInstance();
	}
	
    public Set<String> getSynonyms(String lemma, String partOfSpeach) throws Exception
    {
       Set<String> synonyms = new HashSet<String>();
 
       POS pos;
       
       if (partOfSpeach.equalsIgnoreCase("noun"))
    	   pos = POS.NOUN;
       else if (partOfSpeach.equalsIgnoreCase("verb"))
    	   pos = POS.VERB;
       else if (partOfSpeach.equalsIgnoreCase("adj"))
    	   pos = POS.ADJECTIVE;
       else if (partOfSpeach.equalsIgnoreCase("adv"))
    	   pos = POS.ADVERB;
       else
    	   throw new Exception("unrecognized part of speach.");
       
   	   IndexWord indexWord = wordnet.getIndexWord(pos, lemma);
       
       if (indexWord == null)
          return synonyms;
       
       Synset[] synSets = indexWord.getSenses();
       
       for (Synset synset : synSets)
       {
          Word[] words = synset.getWords();
          
          for (Word word : words)
          {
             synonyms.add(word.getLemma().replace('_', ' '));
          }
       }
       return synonyms;
    }
	
    public Set<String> lookupAllSynonyms(String lemma, String partOfSpeach) throws Exception
    {
       Set<String> synonyms = new HashSet<String>();
 
       POS pos;
       
       if (partOfSpeach.equalsIgnoreCase("noun"))
    	   pos = POS.NOUN;
       else if (partOfSpeach.equalsIgnoreCase("verb"))
    	   pos = POS.VERB;
       else if (partOfSpeach.equalsIgnoreCase("adj"))
    	   pos = POS.ADJECTIVE;
       else if (partOfSpeach.equalsIgnoreCase("adv"))
    	   pos = POS.ADVERB;
       else
    	   throw new Exception("unrecognized part of speach.");
       
   	   IndexWord indexWord = wordnet.lookupIndexWord(pos, lemma);
       
       if (indexWord == null)
          return synonyms;
       
       Synset[] synSets = indexWord.getSenses();
       
       for (Synset synset : synSets)
       {
          Word[] words = synset.getWords();
          
          for (Word word : words)
          {
             synonyms.add(word.getLemma().replace('_', ' '));
          }
       }
       return synonyms;
    }

    public Set<String> lookupSynonymsByThreshold(String lemma, String partOfSpeach, int wordNumber, int senseNumber) throws Exception
    {
       Set<String> synonyms = new HashSet<String>();
 
       POS pos;
       
       if (partOfSpeach.equalsIgnoreCase("noun"))
    	   pos = POS.NOUN;
       else if (partOfSpeach.equalsIgnoreCase("verb"))
    	   pos = POS.VERB;
       else if (partOfSpeach.equalsIgnoreCase("adj"))
    	   pos = POS.ADJECTIVE;
       else if (partOfSpeach.equalsIgnoreCase("adv"))
    	   pos = POS.ADVERB;
       else
    	   throw new Exception("unrecognized part of speach.");
       
       if (lemma.equalsIgnoreCase("mailbox") || lemma.equalsIgnoreCase("email")){
    	   synonyms.add("mbox");
       }
       
       if (lemma.equalsIgnoreCase("latitude")){
    	   synonyms.add("lat");
       }

       if (lemma.equalsIgnoreCase("longitude")){
    	   synonyms.add("long");
       }
       

       IndexWord indexWord = wordnet.lookupIndexWord(pos, lemma);
       
       if (indexWord == null)
          return synonyms;
       
       Synset[] synSets = indexWord.getSenses();
       
       //for (Synset synset : synSets)
       for (int i=0; i < synSets.length && i < senseNumber; i++) {
          
    	  Word[] words = synSets[i].getWords();
          long offset = synSets[i].getOffset();
          
          for (Word word : words)
          {
        	 int wordSenseNumber = 0; 
        	 IndexWord indexWord2 = wordnet.lookupIndexWord(word.getPOS(), word.getLemma());
        	 Synset[] synSets2 = indexWord2.getSenses();
        	 for (int j=0; j < synSets2.length; j++){
        		 if (synSets2[j].getOffset() == offset){
        			 wordSenseNumber = j + 1;
        			 break;
        		 }
        	 }
        	  
        	 if (wordSenseNumber <= senseNumber && synonyms.size() < wordNumber){
        		 
        		 String wordOrPhrase = word.getLemma();
        		 boolean hasDigit = false;
        		 
        		 for (int j=0; j < wordOrPhrase.length(); j++){
        			 if (Character.isDigit(wordOrPhrase.charAt(j)))
        				 hasDigit = true;
        		 }
        		 
        		 if (!hasDigit)
        			 synonyms.add(word.getLemma().replace('_', ' ').toLowerCase());
        		 
        	 }else if (synonyms.size() >= wordNumber)
        		 return synonyms;
          }
       }
       
       return synonyms;
    }

    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			Utility util = new Utility();
			Set<String> synonyms = util.lookupSynonymsByThreshold("know", "verb", 10, 3);
			System.out.println(synonyms.toString());

			
			
        } catch(Exception e) {
            e.printStackTrace();
        } 
	}

}
