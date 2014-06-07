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

import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.umbc.dbpedia.model.SimilarityArrayModel;
import edu.umbc.dbpedia.model.SimilarityBox;
import edu.umbc.similarity.dictionary.StanfordTermTokenizer;

public class NounCompound {

	public static double HeadSimThreshold = 0.075; // 0.1; // 0.15;
	static double ShortTermWeight = 0.5; // 0.6;
	public static double virtualProportion = 1.0; // 0.75; // 0.5; 
	public static double NON_SIMILAR_PENALTY = -1.0; // -0.5; 
	
	int adj_vb_discriminator_threshold = 500;
	public String ori_phrase;
	public String head;
	public int head_loc;
	public String[] componentWords;
	//public static final HashSet<String> stopwords = new HashSet<String>(Arrays.asList("of", "at", "on", "in", "from", "the", "has", "have", "by", "is", "was", "are", "were", "be", "been", "for", "with", "over", "through", "across", "up", "s"));
	public static final HashSet<String> stopwords = new HashSet<String>(Arrays.asList("of", "at", "on", "from", "the", "has", "have", "by", "is", "was", "are", "were", "be", "been", "for", "with", "over", "through", "across", "up", "s"));
	
	public int lengthInAllWords;
	public int lengthInContentWords;
	public boolean isVirtual;
	public boolean hasVerb;

	public static final HashMap<String, String> substitutions = new HashMap<String, String>();
	    static {
	    	//substitutions.put("known for", "notable for");
	    	//substitutions.put("key person", "manager");
	    	//substitutions.put("advisor", "adviser");
	    	//substitutions.put("alma mater", "college");
	    	substitutions.put("who", "person");
	    	substitutions.put("where", "place");
	    	substitutions.put("when", "time");
	    	//substitutions.put("runtime", "running time");
	    	//substitutions.put("homepage", "website");
	    	//substitutions.put("highschool", "high school");
	    	//substitutions.put("conference proceedings", "proceedings");

	    }

	/*
	public NounCompound(String phrase) {
		// TODO Auto-generated constructor stub
		String delimitedPharse = LexicalProcess.tokenLocalName(phrase);
		
		StringTokenizer st = new StringTokenizer(delimitedPharse, " ");
		Vector<String> components = new Vector<String>();
		
		ori_phrase = phrase;
		int of_location = 0;
		String word = null; 
		lengthInAllWords = 0;
		
		while (st.hasMoreTokens()){
			word = st.nextToken();
			lengthInAllWords ++;
			
			if (!LexicalProcess.isAcronym(word))
				word = word.toLowerCase();

			if (word.equals("of")){
				of_location = components.size();
			}

			if (!stopwords.contains(word))
				components.add(word);
			
		}

		if (of_location !=0){
			head = components.get(of_location - 1);
			head_loc = of_location - 1;
		}else{
			head = word;
			head_loc = components.size() - 1;
		}
		
		lengthInContentWords = components.size();
		
		componentWords = components.toArray(new String[components.size()]);
		
	}
	*/
	
	public NounCompound(String phrase, MaxentTagger tagger, Morphology morpha, SimilarityArrayModel model) {
		// TODO Auto-generated constructor stub
		
		hasVerb = false;
		
		if (phrase.startsWith("^"))
			isVirtual = true;
		else
			isVirtual = false;
		
		String delimitedPharse; 
		
		delimitedPharse = LexicalProcess.tokenLocalName(phrase);
		
		for (Entry<String, String> entry: substitutions.entrySet()){
			
			if (delimitedPharse.contains(entry.getKey()))
				delimitedPharse = delimitedPharse.replace(entry.getKey(), entry.getValue());
		}
		
		String taggedPhrase = tagger.tagString("the " + delimitedPharse).replace('/', '_').trim();
		
		int start = taggedPhrase.indexOf(' ');
		taggedPhrase = taggedPhrase.substring(start + 1, taggedPhrase.length());
		
		
		//StringTokenizer st = new StringTokenizer(taggedPhrase, " ");
		StanfordTermTokenizer st = new StanfordTermTokenizer(taggedPhrase, new String[0], morpha, model.vocabulary, model.frequency);

		Vector<String> components = new Vector<String>();
		
		ori_phrase = phrase;
		int location = 0;
		boolean metNoun = false;
		
		boolean setLocation = false;
		String taggedWord;
		
		while ((taggedWord = st.getNextValidWord()) != null){

			int index = taggedWord.lastIndexOf('_');
			String word = taggedWord.substring(0, index);
			String posTag = taggedWord.substring(index + 1, taggedWord.length());
			
			if (!metNoun && posTag.startsWith("NN"))
				metNoun = true;
				
			if (metNoun && !setLocation && !posTag.startsWith("NN")){
				location = components.size();
				setLocation = true;
			}

			if (posTag.startsWith("VB"))
				hasVerb = true;
			
			if (stopwords.contains(word)){
				continue;
			}

			
			if (posTag.equals("NN") || posTag.equals("VB") || posTag.startsWith("JJ") || posTag.equals("RB") || posTag.equals("OT")){
				
				components.add(taggedWord);
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
				int verbFrq = model.getFrequency(verb) / 2;
				int adjFrq = model.getFrequency(adj) / 20;
				
				if (nounFrq > verbFrq && nounFrq > adjFrq){
					components.set(0, noun);
				}
			}
			
		}

		
		if (location !=0) {
			head = components.get(location - 1);
			head_loc = location - 1;
		}else if (components.size() > 0) {
			head = components.lastElement();
			head_loc = components.size() - 1;
		}

		componentWords = components.toArray(new String[components.size()]);
		
		
		
		/*
		if (lengthInContentWords < 3)
			componentWords = components.toArray(new String[components.size()]);
		else{
			
			if (head_loc == 0)
				componentWords = components.toArray(new String[components.size()]);
			else{
				components.remove(0);
				componentWords = components.toArray(new String[components.size()]);
				lengthInContentWords --;
				head_loc --;
			}
		}
		*/
		
	}
	
	
	public boolean isValidNounCompound(){
		
		if (!hasVerb && head != null && head.contains("_NN"))
			return true;
		else
			return false;
		
	}

	public static double getSimilarity(NounCompound target, NounCompound compound, SimilarityArrayModel model){
		
		return getSimilarity(target, compound, model, model);
	}
	
	
	public static double getSimilarity(NounCompound target, NounCompound compound, SimilarityArrayModel conceptModel, SimilarityArrayModel relationModel){
		
		if (target.lengthInContentWords == 0 || compound.lengthInContentWords == 0)
			return Double.NEGATIVE_INFINITY;
		
		/*
		if ((target.ori_phrase.equalsIgnoreCase("In" + compound.ori_phrase) && compound.ori_phrase.length() >= 4) 
				|| (compound.ori_phrase.equalsIgnoreCase("In" + target.ori_phrase) && target.ori_phrase.length() >= 4))
			return 0.5;	
		*/
		
		double positiveHeadSimilarity = conceptModel.getSimilarity(target.head, compound.head);
		
		if (compound.isVirtual){
			if (positiveHeadSimilarity * virtualProportion < HeadSimThreshold)
				return 0;
		}else{
			if (positiveHeadSimilarity < HeadSimThreshold)
				return 0;
		}
		
		double maxSimTarget = 0;
		
		for (int i=0; i<target.lengthInContentWords; i++){
			
			if (i == target.head_loc){
				maxSimTarget += positiveHeadSimilarity;
				continue;
			}
			
			double localMaxSim = -1.0;
			for (int j=0; j<compound.lengthInContentWords; j++){
				
				double localSim = relationModel.getSimilarity(target.componentWords[i], compound.componentWords[j]);
			
				if (localSim > localMaxSim)
					localMaxSim = localSim;
			}
			
			if (localMaxSim == 0){ 
				
				if (i == 0 && target.componentWords[i].endsWith("_JJS") && !compound.componentWords[0].endsWith("_JJS"))
					localMaxSim = NON_SIMILAR_PENALTY;

			}
			
			maxSimTarget += localMaxSim;
			
		}

		double maxSimCompound = 0;
		
		for (int i=0; i<compound.lengthInContentWords; i++){
			
			if (i == compound.head_loc){
				maxSimCompound += positiveHeadSimilarity;
				continue;
			}
			
			double localMaxSim = -1.0;
			for (int j=0; j<target.lengthInContentWords; j++){
				
				double localSim = relationModel.getSimilarity(compound.componentWords[i], target.componentWords[j]);
			
				if (localSim > localMaxSim)
					localMaxSim = localSim;
			}
			
			if (localMaxSim == 0){ 
				
				if (i == 0 && compound.componentWords[i].endsWith("_JJS") && !target.componentWords[0].endsWith("_JJS"))
					localMaxSim = NON_SIMILAR_PENALTY;

			}
			
			maxSimCompound += localMaxSim;
			
		}

		double sim;
		
		//return normalizedSim;
		if (compound.isVirtual){
		
			if (target.lengthInContentWords >= compound.lengthInContentWords)
				sim = (maxSimTarget / target.lengthInContentWords * (1 - ShortTermWeight) + maxSimCompound / compound.lengthInContentWords * ShortTermWeight) * virtualProportion;
			else
				sim = (maxSimTarget / target.lengthInContentWords * ShortTermWeight + maxSimCompound / compound.lengthInContentWords * (1 - ShortTermWeight)) * virtualProportion;
		}else{
			if (target.lengthInContentWords >= compound.lengthInContentWords)
				sim = maxSimTarget / target.lengthInContentWords * (1 - ShortTermWeight) + maxSimCompound / compound.lengthInContentWords * ShortTermWeight;
			else
				sim = maxSimTarget / target.lengthInContentWords * ShortTermWeight + maxSimCompound / compound.lengthInContentWords * (1 - ShortTermWeight);
		}
		
		if (sim > 0)
			return sim;
		else
			return 0;

	}

	public String toString(){
		String result =
			    ori_phrase + "\n" + 
				"Head is (" + head + ")" + "\n" +
				"The number of words is " + lengthInAllWords + "\n" +
				"The number of content words is " + lengthInContentWords + "\n" +
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
		//String modelLocation = "/home/lushan1/nlp/model/stanford/english-left3words/english-left3words-distsim.tagger";
    	String modelLocation = "/home/lushan1/nlp/model/stanford/pos-tagger/wsj-left3words/wsj-0-18-left3words-distsim.tagger"; // "./edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
    	//String modelLocation = "/home/lushan1/nlp/model/stanford/left3words-wsj-0-18.tagger";
    	//String modelLocation = "/home/lushan1/nlp/model/stanford/bidirectional-distsim-wsj-0-18.tagger";
        System.out.println("Reading model from file=" + modelLocation);
        MaxentTagger tagger = new MaxentTagger(modelLocation);
		Morphology morph = new Morphology();

        System.out.println("Reading sim array model ... ");
        SimilarityArrayModel simModel = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/webbase2012AllW2_S2");
        //SimilarityArrayModel simModel = SimilarityArrayModel.readModel("/home/lushan1/nlp/model/BigArray/Gigawords2009AllW2_S2");
        
    	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    	String inline;

    	while(true){
        	System.out.println("Please input first compound");
        	inline = input.readLine();
        	NounCompound firstCompound = new NounCompound(inline, tagger, morph, simModel);
        	System.out.println(firstCompound.toString());
        	System.out.println(firstCompound.isValidNounCompound());
        	
        	System.out.println("Please input second compound");
        	inline = input.readLine();
        	NounCompound secondCompound = new NounCompound(inline, tagger, morph, simModel);
        	System.out.println(secondCompound.toString());

        	System.out.println("The similarity is: " + getSimilarity(firstCompound, secondCompound, simModel));
        }
	}

}
