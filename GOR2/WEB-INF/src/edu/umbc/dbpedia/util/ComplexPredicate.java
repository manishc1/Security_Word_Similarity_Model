package edu.umbc.dbpedia.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Map.Entry;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.umbc.dbpedia.model.SimilarityArrayModel;
import edu.umbc.dbpedia.model.SimilarityBox;
import edu.umbc.similarity.dictionary.StanfordTermTokenizer;

public class ComplexPredicate {

	int adj_vb_discriminator_threshold = 500;
	public static double ShortTermWeight = 0.5; // 0.6;
	public static int phrase_verb_threshold = 4;
	public static double noun_phrase_sim_threshold = 0.10;
	public static double NON_SIMILAR_PENALTY = -1.0; // -0.5; 
	public String ori_phrase;
	public String[] componentWords;
	public boolean isSimpleNounPhrase;
	public int head_loc;
	public static final HashSet<String> stopwords = new HashSet<String>(Arrays.asList("of", "at", "on", "in", "from", "the", "has", "have", "by", "is", "was", "are", "were", "be", "been", "for", "with", "over", "through", "across", "locate", "location", "belongs", "belong", "part", "hold", "up", "total"
															,"get" , "receive", "include", "contain", "involve", "do", "comprise", "consist", "s"));

	public static final HashSet<String> reference_stopwords = new HashSet<String>(Arrays.asList("reference_NN", "name_NN"));
	
	public int lengthInAllWords;
	public int lengthInContentWords;
	
	public static final HashMap<String, String> substitutions = new HashMap<String, String>();
	
    static {
    	substitutions.put("known for", "notable for");
    	substitutions.put("key person", "manager");
    	substitutions.put("advisor", "adviser");
    	substitutions.put("alma mater", "college");
    	//substitutions.put("located in area", "located in");
    	substitutions.put("who", "person");
    	substitutions.put("where", "place");
    	substitutions.put("when", "time");
    	//substitutions.put("where", "location");
    	substitutions.put("runtime", "running time");
    	//substitutions.put("homepage", "website");
    	substitutions.put("highschool", "high school");
    	//substitutions.put("inproceedings", "proceedings");
    	//substitutions.put("conference proceedings", "proceedings");
    }

    public ComplexPredicate(NounCompound compound){
    	
    	ori_phrase = compound.ori_phrase;
    	componentWords = compound.componentWords;
    	isSimpleNounPhrase = true;
    	head_loc = compound.head_loc;
    	lengthInAllWords = compound.lengthInAllWords;
    	lengthInContentWords = compound.lengthInContentWords;
    	
    }
    
	public ComplexPredicate(String phrase, MaxentTagger tagger, Morphology morpha, SimilarityArrayModel model) {
		// TODO Auto-generated constructor stub
		
		String delimitedPharse; 
		
		delimitedPharse = LexicalProcess.tokenLocalName(phrase);
		
		for (Entry<String, String> entry: substitutions.entrySet()){
			
			if (delimitedPharse.contains(entry.getKey()))
				delimitedPharse = delimitedPharse.replace(entry.getKey(), entry.getValue());
		}
		
		String taggedPhrase;
		
		if (delimitedPharse.contains(" ")){
		
			taggedPhrase = tagger.tagString(delimitedPharse).replace('/', '_').trim();
			
		}else{
			
			taggedPhrase = tagger.tagString("which " + delimitedPharse).replace('/', '_').trim();
			
			int start = taggedPhrase.indexOf(' ');
			taggedPhrase = taggedPhrase.substring(start + 1, taggedPhrase.length());
		}
			
		
		StanfordTermTokenizer st = new StanfordTermTokenizer(taggedPhrase, new String[0], morpha, model.vocabulary, model.frequency);
		
		
		Vector<String> components = new Vector<String>();
		
		isSimpleNounPhrase = true;
		ori_phrase = phrase;
		String taggedWord;
		
		while ((taggedWord = st.getNextValidWord()) != null){
			
			int index = taggedWord.lastIndexOf('_');
			String word = taggedWord.substring(0, index);
			String posTag = taggedWord.substring(index + 1, taggedWord.length());
			
			if (stopwords.contains(word)){
				isSimpleNounPhrase = false;
				continue;
			}
			
			if (!ori_phrase.startsWith("@") && components.size() > 0){
				if (reference_stopwords.contains(taggedWord)){
					continue;
				}
			}
			
			if (posTag.startsWith("NN")){
				
				components.add(taggedWord);
				
			}else if (posTag.startsWith("VB")){
				
				isSimpleNounPhrase = false;
				components.add(taggedWord);
				
			}else if (posTag.startsWith("JJ")){
				
				components.add(taggedWord);
			
			}else if (posTag.startsWith("RB")){
				
				components.add(taggedWord);
			
			}else if (posTag.equals("OT")){
				
				isSimpleNounPhrase = false;
				//components.add(taggedWord);
			}

		}

		lengthInAllWords = st.lengthInAllWords;
		
		lengthInContentWords = components.size();

		if (lengthInContentWords == 1 && !delimitedPharse.contains(" ")){
			
			taggedWord = components.get(0);
			
			int index = taggedWord.lastIndexOf('_');
			String word = taggedWord.substring(0, index);
			//String posTag = taggedWord.substring(index + 1, taggedWord.length());
			
			if (word.equals(delimitedPharse)){
				
				String noun = word + "_NN";
				String verb = word + "_VB";
				String adj = word + "_JJ";
				
				int nounFrq = model.getFrequency(noun);
				int verbFrq = model.getFrequency(verb); // / 2;
				int adjFrq = model.getFrequency(adj); // / 20;
				
					
				if (nounFrq >= verbFrq && nounFrq >= adjFrq / 10){
					components.set(0, noun);
					isSimpleNounPhrase = true;
				}else if (verbFrq > nounFrq && verbFrq >= adjFrq / 10){
					components.set(0, verb);
					isSimpleNounPhrase = false;
				}
			
			} else if (delimitedPharse.equalsIgnoreCase(word + "s") && taggedWord.endsWith("_NN")){
				
				String verb = word + "_VB";
				int verbFrq = model.getFrequency(verb);
				
				if (verbFrq > 20000){
					components.set(0, verb);
					isSimpleNounPhrase = false;
				}
			}
			
		}
		
		componentWords = components.toArray(new String[components.size()]);
		
		if (componentWords.length > 0 && !componentWords[componentWords.length - 1].endsWith("_NN"))
			isSimpleNounPhrase = false;
		
		if (isSimpleNounPhrase)
			head_loc = componentWords.length - 1;
		else
			head_loc = -1;

	}

	public static double getSimilarity(ComplexPredicate target, ComplexPredicate compound, SimilarityArrayModel model){
		
		return getSimilarity(target, compound, model, model);

	}
	
	public static double getSimilarity(ComplexPredicate target, ComplexPredicate compound, SimilarityArrayModel relationModel,  SimilarityArrayModel conceptModel){
		
		if (target.lengthInContentWords == 0 || compound.lengthInContentWords == 0)
			return Double.NEGATIVE_INFINITY;
		
		/*
		if ((target.ori_phrase.equalsIgnoreCase("In" + compound.ori_phrase) && compound.ori_phrase.length() >= 4) 
				|| (compound.ori_phrase.equalsIgnoreCase("In" + target.ori_phrase) && target.ori_phrase.length() >= 4))
			return 0.5;	
		*/
		
		/*
		if (target.isSimpleNounPhrase == true && compound.isSimpleNounPhrase == true){
			
			if (target.lengthInContentWords == 1 || compound.lengthInContentWords == 1){

				boolean strictTargetNoun = false;
				boolean strictCompoundNoun = false;
				
				if (target.ori_phrase.contains("name") || target.ori_phrase.contains("Name")){
					strictTargetNoun = false;
					
				}else if (target.lengthInContentWords > 1)
					strictTargetNoun = true;
				else{
					String targetNoun = target.componentWords[target.componentWords.length -1];
					String targetVerb = targetNoun.substring(0, targetNoun.length() - 3) + "_VB";
					if (conceptModel.getFrequency(targetNoun) > conceptModel.getFrequency(targetVerb) / 2)
						strictTargetNoun = true;
				}

				if (compound.ori_phrase.contains("name") || compound.ori_phrase.contains("Name")){
					strictCompoundNoun = false;
					
				}else if (compound.lengthInContentWords > 1)
					strictCompoundNoun = true;
				else{
					String compoundNoun = compound.componentWords[compound.componentWords.length -1];
					String compoundVerb = compoundNoun.substring(0, compoundNoun.length() - 3) + "_VB";
					if (conceptModel.getFrequency(compoundNoun) > conceptModel.getFrequency(compoundVerb) / 2)
						strictCompoundNoun = true;
				}

				if (strictTargetNoun && strictCompoundNoun){
					double temp = conceptModel.getSimilarity(target.componentWords[target.componentWords.length -1], compound.componentWords[compound.componentWords.length -1]);
					
					if (temp < 0)
						return 0;
					
					if (temp < noun_phrase_sim_threshold)
						return temp;
				}
				
			}else if (!target.ori_phrase.contains("name") && !target.ori_phrase.contains("Name") && !compound.ori_phrase.contains("name") && !compound.ori_phrase.contains("Name") ){
				double temp = conceptModel.getSimilarity(target.componentWords[target.componentWords.length -1], compound.componentWords[compound.componentWords.length -1]);
				
				if (temp < 0)
					return 0;
				
				if (temp < noun_phrase_sim_threshold)
					return temp;
			}
		}
		*/
			

		double maxSimTarget = 0;
		
		for (int i=0; i<target.lengthInContentWords; i++){
			
			double localMaxSim = -1.0;
			for (int j=0; j<compound.lengthInContentWords; j++){
				
				double localSim = relationModel.getSimilarity(target.componentWords[i], compound.componentWords[j]);
			
				if (localSim > localMaxSim)
					localMaxSim = localSim;
			}
			
			if (localMaxSim == 0){ 
				
				if (target.head_loc != -1 && i != target.head_loc && target.componentWords[i].endsWith("_NN") && relationModel.getFrequency(target.componentWords[i]) != 0)
					localMaxSim = NON_SIMILAR_PENALTY;
				
				if (i == 0 && target.componentWords[i].endsWith("_JJS") && !compound.componentWords[0].endsWith("_JJS"))
					localMaxSim = NON_SIMILAR_PENALTY;
			}
			
			maxSimTarget += localMaxSim;
			
		}

		double maxSimCompound = 0;
		
		for (int i=0; i<compound.lengthInContentWords; i++){
			
			double localMaxSim = -1.0;
			for (int j=0; j<target.lengthInContentWords; j++){
				
				double localSim = relationModel.getSimilarity(compound.componentWords[i], target.componentWords[j]);
			
				if (localSim > localMaxSim)
					localMaxSim = localSim;
			}
			
			
			if (localMaxSim == 0){
				
				if (compound.head_loc != -1 && i != compound.head_loc && compound.componentWords[i].endsWith("_NN")  && relationModel.getFrequency(compound.componentWords[i]) != 0)
					localMaxSim = NON_SIMILAR_PENALTY;
				
				if (i == 0 && compound.componentWords[i].endsWith("_JJS") && !target.componentWords[0].endsWith("_JJS"))
					localMaxSim = NON_SIMILAR_PENALTY;
			}
			
			maxSimCompound += localMaxSim;
			
		}

		double sim;
		
		//return normalizedSim;
		if (target.lengthInContentWords >= compound.lengthInContentWords)
			sim = maxSimTarget / target.lengthInContentWords * (1 - ShortTermWeight) + maxSimCompound / compound.lengthInContentWords * ShortTermWeight;
		else
			sim = maxSimTarget / target.lengthInContentWords * ShortTermWeight + maxSimCompound / compound.lengthInContentWords * (1 - ShortTermWeight);
		
		/*
		double compoundSim = maxSimCompound / compound.length;
		double targetSim = maxSimTarget / target.length;
		
		if (compoundSim > targetSim)
			sim = targetSim;
		else
			sim = compoundSim;
		*/

		if (sim > 0)
			return sim;
		else
			return 0;

	}

	
	public String toString(){
		String result =
			    ori_phrase + "\n" + 
				"The number of words is " + lengthInAllWords + "\n" +
				"The number of content words is " + lengthInContentWords + "\n" +
				"The head location is (" + head_loc + ")" + "\n" +
				"The list of content words includes: ";
		
		for (String word: componentWords){
			result += word + " ";
		}

		return result;
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
    	//String modelLocation = "/home/lushan1/nlp/model/stanford/left3words-distsim-wsj-0-18.tagger";
    	//String modelLocation = "/home/lushan1/nlp/model/stanford/20120612/left3words-distsim-wsj-0-18.tagger";
    	//String modelLocation = "/home/lushan1/nlp/model/stanford/pos-tagger/wsj-left3words/wsj-0-18-left3words-distsim.tagger";
		String modelLocation = "/home/lushan1/nlp/model/stanford/pos-tagger/english-left3words/english-left3words-distsim.tagger";
    	//String modelLocation = "/home/lushan1/nlp/model/stanford/left3words-wsj-0-18.tagger";
    	//String modelLocation = "/home/lushan1/nlp/model/stanford/bidirectional-distsim-wsj-0-18.tagger";
        System.out.println("Reading model from file=" + modelLocation);
        MaxentTagger tagger = new MaxentTagger(modelLocation);
		Morphology morph = new Morphology();

        System.out.println("Reading sim array model ... ");
        SimilarityArrayModel simModel = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/webbase2012AllW5");
        //SimilarityArrayModel simModel = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/Gigawords2009AllW5_S3");
        
    	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    	String inline;

    	while(true){
        	System.out.println("Please input first compound");
        	inline = input.readLine();
        	ComplexPredicate firstCompound = new ComplexPredicate(inline, tagger, morph, simModel);
        	System.out.println(firstCompound.toString());
        	
        	System.out.println("Please input second compound");
        	inline = input.readLine();
        	ComplexPredicate secondCompound = new ComplexPredicate(inline, tagger, morph, simModel);
        	System.out.println(secondCompound.toString());

        	System.out.println("The similarity is: " + getSimilarity(firstCompound, secondCompound, simModel));
        }
	}

}
