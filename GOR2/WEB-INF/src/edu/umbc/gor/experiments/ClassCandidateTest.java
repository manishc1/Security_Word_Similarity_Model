package edu.umbc.gor.experiments;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.umbc.configure.Configure;

public class ClassCandidateTest {

	String inFileName;
	private PrintWriter out;
	
	public ClassCandidateTest(String inFileName, String outFileName) throws IOException {
		// TODO Auto-generated constructor stub
		this.inFileName = inFileName;
		out = new PrintWriter(new FileWriter(outFileName, true));
	}
	
	
	public void run() throws Exception{
		
		ReciprocalRankEvaluation test = new ReciprocalRankEvaluation(inFileName, true);
		
		System.out.println("Start running test cases ...");
		test.gor.setAllParameters(10, 20, 10, 0.25, 2.25, 10, 2.0, 5, 0.20, 0.4, 1.0);  // w2s2_w5s3 (dbpedia)
		
		out.println("The number of test cases is " + test.testcases.size());

		test.setNumberOfClassCandidates(1);
		test.performTest(out, String.valueOf(1));
		test.clear();

		for (int NumberOfClassCandidates = 1; NumberOfClassCandidates <= 32; NumberOfClassCandidates *= 2){
		
			test.setNumberOfClassCandidates(NumberOfClassCandidates);
			
			System.out.println("numberOfClassCandidates is set to " + NumberOfClassCandidates);
			
			test.performTest(out, String.valueOf(NumberOfClassCandidates));
			
			test.clear();
		}
		
		/*
		test.setNumberOfClassCandidates(1);
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
		
		ClassCandidateTest test;
		
		if (args.length == 2){
			String inFile = args[0];
			String outFile = args[1];
			test = new ClassCandidateTest(inFile, outFile);
		
		}else if (args.length == 1){
			String inFile = args[0];
			String outFile = args[0] + ".out";
			test = new ClassCandidateTest(inFile, outFile);
			
		}else{
			test = new ClassCandidateTest(Configure.work_dir + "evaluation/testcases.txt", Configure.work_dir + "evaluation/output/class_candidates.out");
		
		}
		
		test.run();

	}

}
