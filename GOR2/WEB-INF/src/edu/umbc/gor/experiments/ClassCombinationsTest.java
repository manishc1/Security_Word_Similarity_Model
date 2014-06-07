package edu.umbc.gor.experiments;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.umbc.configure.Configure;

public class ClassCombinationsTest {

	String inFileName;
	private PrintWriter out;
	
	public ClassCombinationsTest(String inFileName, String outFileName) throws IOException {
		// TODO Auto-generated constructor stub
		this.inFileName = inFileName;
		out = new PrintWriter(new FileWriter(outFileName, true));
	}
	
	
	public void run() throws Exception{
		
		ReciprocalRankEvaluation test = new ReciprocalRankEvaluation(inFileName, true);
		
		System.out.println("Start running test cases ...");
		//test.gor.setAllParameters(10, 20, 40, 0.25, 2.25, 10, 2.0, 5, 0.20, 0.4, 1.0);  // w2s2_w5s3 (dblp)
		//test.gor.setAllParameters(10, 20, 40, 0.25, 2.0, 10, 2.0, 5, 0.20, 0.6, 1.5);  // w2_w5 (dblp)

		test.gor.setAllParameters(10, 20, 10, 0.25, 2.25, 10, 2.0, 5, 0.20, 0.4, 1.0);  // w2s2_w5s3 (dbpedia)
		//test.gor.setAllParameters(10, 20, 40, 0.25, 2.0, 10, 2.0, 5, 0.20, 0.6, 1.5);  // w2_w5 (dbpedia)
		
		out.println("The number of test cases is " + test.testcases.size());
		
		for (int numberOfBestCombinations = 10; numberOfBestCombinations <= 20; numberOfBestCombinations += 1){
		//for (int numberOfBestCombinations = 30; numberOfBestCombinations <= 100; numberOfBestCombinations += 10){
		
			if (numberOfBestCombinations == 0){
				test.setNumberOfBestCombinations(1);
				System.out.println("numberOfBestCombinations is set to " + 1);
				test.performTest(out, String.valueOf(1));
			}else{
				test.setNumberOfBestCombinations(numberOfBestCombinations);
				System.out.println("numberOfBestCombinations is set to " + numberOfBestCombinations);
				test.performTest(out, String.valueOf(numberOfBestCombinations));
			}
			
			test.clear();
		}
		
		/*
		test.setNumberOfBestCombinations(1);
		test.performTest(out, String.valueOf(1));
		test.clear();
		
		test.performTest(out, String.valueOf(1));
		test.clear();
		*/
		
		out.close();

	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		ClassCombinationsTest test;
		
		if (args.length == 2){
			String inFile = args[0];
			String outFile = args[1];
			test = new ClassCombinationsTest(inFile, outFile);
		
		}else if (args.length == 1){
			String inFile = args[0];
			String outFile = args[0] + ".out";
			test = new ClassCombinationsTest(inFile, outFile);
			
		}else{
			test = new ClassCombinationsTest(Configure.work_dir + "evaluation/testcases.txt", Configure.work_dir + "evaluation/output/class_combinations_n.out");
		
		}
		
		test.run();

	}

}
