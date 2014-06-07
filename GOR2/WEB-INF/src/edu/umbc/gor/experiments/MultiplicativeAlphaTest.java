package edu.umbc.gor.experiments;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MultiplicativeAlphaTest {

	String inFileName;
	private PrintWriter out;
	
	public MultiplicativeAlphaTest(String inFileName, String outFileName) throws IOException {
		// TODO Auto-generated constructor stub
		this.inFileName = inFileName;
		out = new PrintWriter(new FileWriter(outFileName, true));
	}
	
	
	public void run() throws Exception{
		
		ReciprocalRankEvaluation test = new ReciprocalRankEvaluation(inFileName, true);
		
		System.out.println("Start running test cases ...");
		test.gor.setAllParameters(10, 20, 40, 0.25, 0, 10, 2.0, 3, 0.20, 0.5, 1.0);
		out.println("The number of test cases is " + test.testcases.size());
		
		for (double alpha = 0; alpha <= 1.01; alpha += 0.05){
		
			test.setMultiplicativeAlpha(alpha);
			System.out.println("parameter is set to " + alpha);
			
			test.performTest(out, String.format("%1.2f", alpha));
			
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
		
		MultiplicativeAlphaTest test;
		
		if (args.length == 2){
			String inFile = args[0];
			String outFile = args[1];
			test = new MultiplicativeAlphaTest(inFile, outFile);
		
		}else if (args.length == 1){
			String inFile = args[0];
			String outFile = args[0] + ".out";
			test = new MultiplicativeAlphaTest(inFile, outFile);
			
		}else{
			test = new MultiplicativeAlphaTest("/home/lushan1/dblp/evaluation/testcases.txt", "/home/lushan1/dblp/evaluation/output/testcases_multiplicative_alpha.out");
		
		}
		
		test.run();

	}

}
