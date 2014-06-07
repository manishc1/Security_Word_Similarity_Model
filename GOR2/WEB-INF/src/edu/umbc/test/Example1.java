package edu.umbc.test;

import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.*;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Random;

class Example1 {
	public static void main(String []args) throws Exception {
		//String text = "A quick brown fox jumped over the lazy dog.";
		//String text = "Bills on ports and immigration were submitted by Senator Brownback, Republican of Kansas.";
		//String text = "Who is the daughter of Richard Nixon married to?";
		//String text = "Who is the daughter of the U.S. President Barrack Obama married to?";
		//String text = "Which is the longest river that flows through the states neighboring Mississippi?";
		//String text = "I see a piece of land cleared of trees and usually enclosed.";
		//String text = "I see A celestial body of hot gases that radiates energy derived from thermonuclear reactions in the interior";
		//String text = "See The subject matter of a conversation or discussion";
		//String text = "I see the U.S. President Barrack Obama";
		//String text = "I see the United States";
		
		/*
		String text = "a piece of cake";
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		// LexicalizedParser lp = new LexicalizedParser("grammar/englishPCFG.ser.gz");
		//lp.setOptionFlags(new String[]{"-maxLength", "500", "-retainTmpSubcategories"});
		//LexicalizedParser lp = LexicalizedParser.loadModel("/home/lushan1/nlp/model/stanford/lexparser/englishPCFG.ser.gz", "-maxLength", "500", "-retainTmpSubcategories");
		LexicalizedParser lp = LexicalizedParser.loadModel("/home/lushan1/nlp/model/stanford/lexparser/englishPCFG.ser.gz", "-maxLength", "500");
		Tree tree = lp.apply(text);
		TreePrint treePrint = lp.getTreePrint();
		treePrint.printTree(tree, "1", new PrintWriter(System.out));
		
		HeadFinder headFinder = new SemanticHeadFinder();
		//HeadFinder headFinder = new ModCollinsHeadFinder();
		Tree ht = tree.headTerminal(headFinder);
		System.out.println("Its head tree is:");
		treePrint.printTree(ht, "2", new PrintWriter(System.out));
		*/
		
		String rdline = "journals/siamdm/G.\\ abstract";
		System.out.println(rdline.replace("\\ ", ""));
		
		double d = 0.25;
		System.out.println("abc" + d);
		
		
		for (double t = 0; t <= 10; t = t + 1.0){
			System.out.println(t);
		}
		
		int zeroCount = 0;
		int oneCount = 0;
		
		Random generator = new Random(50);
		
		for (int i=0; i < 220; i++){
			int r = generator.nextInt(2);
			System.out.println(r);
			
			if (r == 0) 
				zeroCount ++;
			else
				oneCount ++;
		}

		System.out.println(zeroCount);
		System.out.println(oneCount);
		
	}
}