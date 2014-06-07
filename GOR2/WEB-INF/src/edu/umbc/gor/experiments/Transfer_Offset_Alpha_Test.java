package edu.umbc.gor.experiments;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Transfer_Offset_Alpha_Test {

	String inFileName;
	private PrintWriter out;
	
	public Transfer_Offset_Alpha_Test(String inFileName, String outFileName) throws IOException {
		// TODO Auto-generated constructor stub
		this.inFileName = inFileName;
		out = new PrintWriter(new FileWriter(outFileName, true));
	}
	
	
	public void run() throws Exception{
		
		ReciprocalRankEvaluation test = new ReciprocalRankEvaluation(inFileName, true);
		
		System.out.println("Start running test cases ...");
		test.gor.setAllParameters(10, 20, 40, 0.25, 0, 10, 2.0, 5, 0.20, 0.5, 1.0);
		out.println("The number of test cases is " + test.testcases.size());
		
		
		for (double transferRate = 0; transferRate <= 1.01; transferRate += 0.1){
			
			test.setTransferRate(transferRate);
			System.out.println("transitRate is set to " + transferRate);
			
			for (double offset = 0; offset <= 0; offset += 1.0){
				
				test.setUniverseOffset(offset);
				System.out.println("universe offset is set to " + offset);

				//for (double alpha = 0; alpha <= 1.01; alpha += 0.05){
				for (double alpha = 0; alpha <= 1.01; alpha += 0.05){
				
					test.setMultiplicativeAlpha(alpha);
					System.out.println("multiplicative alpha is set to " + alpha);
					
					test.performTest(out, String.format("%1.2f", transferRate) + "\t" + String.valueOf(offset) + "\t" + String.format("%1.2f", alpha));
					
					test.clear();
				}
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
		
		Transfer_Offset_Alpha_Test test;
		
		if (args.length == 2){
			String inFile = args[0];
			String outFile = args[1];
			test = new Transfer_Offset_Alpha_Test(inFile, outFile);
		
		}else if (args.length == 1){
			String inFile = args[0];
			String outFile = args[0] + ".out";
			test = new Transfer_Offset_Alpha_Test(inFile, outFile);
			
		}else{
			test = new Transfer_Offset_Alpha_Test("/home/lushan1/dblp/evaluation/testcases.txt", "/home/lushan1/dblp/evaluation/output/testcases_transfer_offset_alpha.out");
		
		}
		
		test.run();

	}

}
