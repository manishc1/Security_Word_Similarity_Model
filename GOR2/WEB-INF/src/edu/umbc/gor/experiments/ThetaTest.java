package edu.umbc.gor.experiments;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.umbc.configure.Configure;

public class ThetaTest {

	String inFileName;
	private PrintWriter out;
	
	public ThetaTest(String inFileName, String outFileName) throws IOException {
		// TODO Auto-generated constructor stub
		this.inFileName = inFileName;
		out = new PrintWriter(new FileWriter(outFileName, true));
	}
	
	
	public void run() throws Exception{
		
		ReciprocalRankEvaluation test = new ReciprocalRankEvaluation(inFileName, true);
		
		System.out.println("Start running test cases ...");

		// ---------------------- dblp -------------------------------------
		//w2s2_w5s3
		//test.gor.setAllParameters(10, 20, 40, 0.25, 2.25, 10, 2.0, 5, 0.20, 0.4, 1.0);

		//w2s2 w5s3_0A
		//test.gor.setAllParameters(10, 20, 40, 0.25, -1.0, 10, 2.0, 5, 0.20, 0.4, 1.0);

		//w2s2 w5s3_0B
		//test.gor.setAllParameters(10, 20, 40, 0.25, 0, 10, 2.0, 5, 0.20, 0.6, 1.0);

		//w2_w5
		//test.gor.setAllParameters(10, 20, 40, 0.25, 2.0, 10, 2.0, 5, 0.20, 0.6, 1.0);
		
		//w2_w5 (0.75)
		//test.gor.setAllParameters(10, 20, 40, 0.75, 0, 10, 2.0, 5, 0.20, 0.6, 1.0);
		
		//string_sim
		//test.gor.setAllParameters(10, 20, 40, 1.0, 2.0, 10, 2.0, 5, 0.20, 1.0, 1.0);

		//string_sim0.6
		//test.gor.setAllParameters(10, 20, 40, 1.0, 2.0, 10, 2.0, 5, 0.20, 1.0, 1.0);

		
		// ---------------------- dbpedia virtual class -------------------------------------
		// w2s2_w5s3
		//test.gor.setAllParameters(10, 20, 10, 0.25, 2.25, 10, 2.0, 5, 0.20, 0.4, 1.0);
		
		//w2_w5
		test.gor.setAllParameters(10, 20, 10, 0.25, 2.0, 10, 2.0, 5, 0.20, 0.6, 1.5);
		

		out.println("The number of test cases is " + test.testcases.size());
		
		//for (double theta = 0; theta <= 2.01; theta += 0.25){
		for (double theta = 4; theta <= 6.01; theta += 0.25){
		
			test.setUnevenMatchTheta(theta);
			System.out.println("parameter is set to " + theta);
			
			test.performTest(out, String.format("%1.2f", theta));
			
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
		
		ThetaTest test;
		
		if (args.length == 2){
			String inFile = args[0];
			String outFile = args[1];
			test = new ThetaTest(inFile, outFile);
		
		}else if (args.length == 1){
			String inFile = args[0];
			String outFile = args[0] + ".out";
			test = new ThetaTest(inFile, outFile);
			
		}else{
			test = new ThetaTest(Configure.work_dir + "evaluation/testcases.txt", Configure.work_dir + "evaluation/output/testcases_uneven_match_theta.out");

		}
		
		test.run();

	}

}
