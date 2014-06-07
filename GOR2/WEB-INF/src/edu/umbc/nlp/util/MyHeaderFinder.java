package edu.umbc.nlp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.SemanticHeadFinder;
import edu.stanford.nlp.trees.Tree;
import edu.umbc.dbpedia.model.LSA_Model;


public class MyHeaderFinder {

	LexicalizedParser lp;
	String[] vocabulary;
	Morphology morpha;

	public MyHeaderFinder(LexicalizedParser parser, String[] voc, Morphology mor) {
		// TODO Auto-generated constructor stub
		lp = parser;
		vocabulary = voc;
		morpha = mor;
	}

	public int index(String word){
		return Arrays.binarySearch(vocabulary, word);
	}

	public String matcherInVocabulary(String word){
		
		int position = (index(word + "_") + 1) * -1;
		
		if (position >= vocabulary.length)
			return null;

		if (vocabulary[position].startsWith(word + "_"))
			return vocabulary[position];
		else
			return null;
	}
	
	public String findHead(String sentence){
		
		Tree tree = lp.apply(sentence);
		HeadFinder headFinder = new SemanticHeadFinder();
		Tree ht = tree.headTerminal(headFinder);

		if (ht == null) return "";

		List<Tree> leaves = tree.getLeaves();
		int location = leaves.indexOf(ht);
		String headString = ht.nodeString();
		
		Tree leftLeaf;
		if (location > 0){
			leftLeaf = leaves.get(location - 1);
			String leftLeafString = leftLeaf.nodeString();
			
			String twoWords = leftLeafString + " " +  headString;
			if (Character.isLowerCase(leftLeafString.charAt(0)) || Character.isLowerCase(headString.charAt(0)))
				twoWords = twoWords.toLowerCase();

			String matcher = matcherInVocabulary(twoWords);
			if (matcher != null){
				return matcher;
			}else{
				String pos_tag = ht.parent(tree).label().value().substring(0, 2);
				String result = morpha.lemma(headString, pos_tag) + "_" + pos_tag;
				return result;
			}
		}else{
			String pos_tag = ht.parent(tree).label().value().substring(0, 2);
			String result = morpha.lemma(headString, pos_tag) + "_" + pos_tag;
			return result;
		}
	}
	
	public String findNounPhraseHead(String nounPhrase){
		
		Tree tree = lp.apply("I see " + nounPhrase);
		Tree sentenceTree = tree.firstChild();
		if (!sentenceTree.label().value().equals("S"))
			return "";
		
		if (sentenceTree.children().length < 2)
			return "";
			
		Tree seeTree = sentenceTree.getChild(1);
		if (!seeTree.label().value().equals("VP"))
			return "";
		
		if (seeTree.children().length < 2 || !seeTree.firstChild().firstChild().value().equals("see"))
			return "";
		
		Tree targetTree = seeTree.getChild(1);
		if (!targetTree.label().value().equals("NP"))
			return "";
		
		HeadFinder headFinder = new SemanticHeadFinder();
		Tree ht = targetTree.headTerminal(headFinder);
		
		if (ht == null) return "";
		
		List<Tree> leaves = targetTree.getLeaves();
		int location = leaves.indexOf(ht);
		String headString = ht.nodeString();
		
		Tree leftLeaf;
		Tree rightLeaf;

		if (location < leaves.size() - 1){
			rightLeaf = leaves.get(location + 1);
			String rightLeafString = rightLeaf.nodeString();
			if (rightLeafString.equals("or") || rightLeafString.equals("and"))
				return "";
		}
		
		if (location > 0){
			
			leftLeaf = leaves.get(location - 1);
			String leftLeafString = leftLeaf.nodeString();
			
			String twoWords = leftLeafString + " " +  headString;
			if (Character.isLowerCase(leftLeafString.charAt(0)) || Character.isLowerCase(headString.charAt(0)))
				twoWords = twoWords.toLowerCase();

			String matcher = matcherInVocabulary(twoWords);
			if (matcher != null){
				return matcher;
			}else{
				String pos_tag = ht.parent(targetTree).label().value().substring(0, 2);
				String result = morpha.lemma(headString, pos_tag) + "_" + pos_tag;
				return result;
			}
		}else{
			String pos_tag = ht.parent(targetTree).label().value().substring(0, 2);
			String result = morpha.lemma(headString, pos_tag) + "_" + pos_tag;
			return result;
		}
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		Morphology morph = new Morphology();
		LSA_Model lsa_model = new LSA_Model("/home/lushan1/nlp/model/SVD/webbase2012AllW5");
		MyHeaderFinder test = new MyHeaderFinder(LexicalizedParser.loadModel("/home/lushan1/nlp/model/stanford/lexparser/englishPCFG.ser.gz", "-maxLength", "500"), lsa_model.vocabulary, morph);
		//System.out.println(test.findHead("Who is the daughter of the U.S. President Barrack Obama married to?"));
		//System.out.println(test.findHead("What area code is Berlin?"));
		//System.out.println(test.findNounPhraseHead("the United States"));
		//System.out.println(test.findHead("perform a marriage ceremony"));
		//System.out.println(test.findNounPhraseHead("(computer science) the code that identifies where a piece of information is stored"));
		//System.out.println(test.findNounPhraseHead("Alma mater"));
		//System.out.println(test.findNounPhraseHead("A celestial body of hot gases that radiates energy derived from thermonuclear reactions in the interior"));
		//System.out.println(test.findNounPhraseHead("hot gases that radiates energy derived from thermonuclear reactions in the interior"));
		
		System.out.println("Input the phrase.");
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		String phrase = input.readLine();
		
		while (!phrase.equals("quit")){
			
			//System.out.println(test.findNounPhraseHead(phrase));
			System.out.println(test.findHead(phrase));
			
			System.out.println("Input the phrase.");
			phrase = input.readLine();
		}

	}

}
