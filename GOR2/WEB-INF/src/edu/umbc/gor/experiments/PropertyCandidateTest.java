package edu.umbc.gor.experiments;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.umbc.configure.Configure;

public class PropertyCandidateTest {

	String inFileName;
	private PrintWriter out;
	
	public PropertyCandidateTest(String inFileName, String outFileName) throws IOException {
		// TODO Auto-generated constructor stub
		this.inFileName = inFileName;
		out = new PrintWriter(new FileWriter(outFileName, true));
	}
	
	
	public void run() throws Exception{
		
		ReciprocalRankEvaluation test = new ReciprocalRankEvaluation(inFileName, true);
		
		System.out.println("Start running test cases ...");
		test.gor.setAllParameters(10, 20, 10, 0.25, 2.25, 10, 2.0, 5, 0.20, 0.4, 1.0);  // w2s2_w5s3 (dbpedia)
		//test.gor.setAllParameters(10, 20, 1, 0.25, 2.25, 10, 2.0, 5, 0.20, 0.4, 1.0);  // w2s2_w5s3 (dbpedia)
		out.println("The number of test cases is " + test.testcases.size());
		
		test.setNumberOfPropertyCandidates(2);
		test.performTest(out, String.valueOf(2));
		test.clear();

		for (int NumberOfPropertyCandidates = 2; NumberOfPropertyCandidates <= 32; NumberOfPropertyCandidates *= 2){
		
			test.setNumberOfPropertyCandidates(NumberOfPropertyCandidates);
			
			System.out.println("numberOfPropertyCandidates is set to " + NumberOfPropertyCandidates);
			
			test.performTest(out, String.valueOf(NumberOfPropertyCandidates));
			
			test.clear();
		}
		

		out.close();

	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		PropertyCandidateTest test;
		
		if (args.length == 2){
			String inFile = args[0];
			String outFile = args[1];
			test = new PropertyCandidateTest(inFile, outFile);
		
		}else if (args.length == 1){
			String inFile = args[0];
			String outFile = args[0] + ".out";
			test = new PropertyCandidateTest(inFile, outFile);
			
		}else{
			test = new PropertyCandidateTest(Configure.work_dir + "evaluation/testcases.txt", Configure.work_dir + "evaluation/output/property_candidates.out");
		
		}
		
		test.run();

	}

}
