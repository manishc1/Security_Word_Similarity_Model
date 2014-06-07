package edu.umbc.gor.experiments;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class pathElement_maxChoices_threshold_Test {

	String inFileName;
	private PrintWriter out;
	
	public pathElement_maxChoices_threshold_Test(String inFileName, String outFileName) throws IOException {
		// TODO Auto-generated constructor stub
		this.inFileName = inFileName;
		out = new PrintWriter(new FileWriter(outFileName, true));
	}
	
	
	public void run() throws Exception{
		
		ReciprocalRankEvaluation test = new ReciprocalRankEvaluation(inFileName, true);
		
		System.out.println("Start running test cases ...");
		test.gor.setAllParameters(10, 20, 40, 0.25, 0, 10, 2.0, 3, 0.20, 0.5, 1.0);
		out.println("The number of test cases is " + test.testcases.size());
		
		
		for (int maxChoices = 1; maxChoices <= 5; maxChoices++){
			
			test.setPathFilter_MaxChoices(maxChoices);
			System.out.println("maxChoices is set to " + maxChoices);
			
			for (double threshold = 0.05; threshold <= 0.501; threshold += 0.05){
				
				test.setPathFilter_Threshold(threshold);
				System.out.println("threshold is set to " + threshold);

					
				test.performTest(out, String.valueOf(maxChoices) + "\t" + String.format("%1.2f", threshold));
				
				test.clear();
			}
		}
		
		out.close();

	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		pathElement_maxChoices_threshold_Test test;
		
		if (args.length == 2){
			String inFile = args[0];
			String outFile = args[1];
			test = new pathElement_maxChoices_threshold_Test(inFile, outFile);
		
		}else if (args.length == 1){
			String inFile = args[0];
			String outFile = args[0] + ".out";
			test = new pathElement_maxChoices_threshold_Test(inFile, outFile);
			
		}else{
			test = new pathElement_maxChoices_threshold_Test("/home/lushan1/dblp/evaluation/runtime/trial.txt", "/home/lushan1/dblp/evaluation/output/trial.out");
		
		}
		
		test.run();

	}

}
