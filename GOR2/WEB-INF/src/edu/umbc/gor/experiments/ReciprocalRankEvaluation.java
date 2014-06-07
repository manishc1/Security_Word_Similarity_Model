package edu.umbc.gor.experiments;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import edu.umbc.configure.Configure;
import edu.umbc.dblp.model.CorrelationModel;
import edu.umbc.dblp.model.GraphMapping;
import edu.umbc.dblp.model.OutputGraphModel;

public class ReciprocalRankEvaluation {
	
	public GraphMapping gor;
	public ArrayList<TestCase> testcases;
	public int numberOfTestCases;
	public double reciprocalRankSum;
	boolean batchMode = true;

	public ReciprocalRankEvaluation(String testFile, boolean batchMode) throws Exception {
		// TODO Auto-generated constructor stub
		gor = new GraphMapping("");
		this.batchMode = batchMode;
		gor.batchMode = batchMode;
		testcases = new ArrayList<TestCase>();
		
		TestCaseGenerator generator = new TestCaseGenerator(testFile);
		
		TestCase testcase;
		while ((testcase = generator.getNextTestCase()) != null){
			testcases.add(testcase);
		}
		
		reciprocalRankSum = 0;
		numberOfTestCases = testcases.size();
	}
	
	
	public void setMultiplicativeAlpha(double multiplicative_alpha){
		
		gor.multiplicative_alpha = multiplicative_alpha;
	}
	
	public void setTransferRate(double transfer_rate_lamda) throws Exception{
		
		String transfer_lamda = String.format("%1.1f", transfer_rate_lamda);
		
		gor.correlation_model = CorrelationModel.readModel(Configure.work_dir + "latest_triples_NoConstraint_lambda" + transfer_lamda);
	}
	
	public void setUniverseOffset(double universe_offset){
		
		gor.universe_offset = universe_offset;
	}

	public void setUnevenMatchTheta(double theta){
		
		gor.uneven_match_parameter = theta;
	}

	public void setPathFilter_MaxChoices(int maxChoices){
		
		gor.PathFilter_MaxChoices = maxChoices;
	}

	public void setPathFilter_Threshold(double threshold){
		
		gor.PathFilter_Threshold = threshold;
	}
	
	public void setNumberOfBestCombinations(int numberOfBestCombinations){
		
		gor.NumberOfBestCombinations = numberOfBestCombinations;
	}
	
	public void setNumberOfPropertyCandidates(int numberOfPropertyCandidates){
		
		gor.NumberOfPropertyCandidates = numberOfPropertyCandidates;
	}

	public void setNumberOfClassCandidates(int numberOfClassCandidates){
		
		gor.NumberOfClassCandidates = numberOfClassCandidates;
	}

	
	public void performTest(PrintWriter out, String parameters) throws Exception{
		
		long startTime = System.currentTimeMillis();

		for (TestCase testcase: testcases){
			
			if (!testcase.description.equals("") && !batchMode)
				System.out.println(testcase.description);
			
			for (String relation: testcase.relations){
				if (relation.contains(","))
					gor.getGraph().addOneRelation(relation);
				else
					gor.getGraph().addOneNode(relation);
			}
			
			gor.setupInput();
			gor.optimize();
			
			gor.resolvePath();
			
			OutputGraphModel[] outputGraphs = gor.getOutputGraphs();
			double reciprocalRank = 0;
			
			for (int i = 0; i < outputGraphs.length; i++){
				
				if (!batchMode)
					System.out.println(outputGraphs[i].signature() + " " + outputGraphs[i].fitness);
				
				if (testcase.correct_mappings.contains(outputGraphs[i].signature())){
					reciprocalRank = 1.0 / (i + 1);
					break;
				}
			}
			
			reciprocalRankSum += reciprocalRank;
			
			if (!batchMode){
				
				System.out.println(testcase);
				
				if (reciprocalRank != 1.0)
					System.out.println(" warning: has a reciprocal rank " + String.format("%1.2f", reciprocalRank));
				else
					System.out.println(" perfect: has a reciprocal rank " + String.format("%1.2f", reciprocalRank));
					
				System.out.println();
			}
			
			gor.clear();
		}
		
		System.out.println("The mean of reciprocal rank is " + String.format("%1.5f", reciprocalRankSum / numberOfTestCases));
		
		long endTime = System.currentTimeMillis();
		long elipsedTime = endTime - startTime;
		
		if (!batchMode){
			System.out.println(elipsedTime + " milli seconds have been taken totally.");
			System.out.println(numberOfTestCases + " test cases in total.");
			System.out.println(elipsedTime / numberOfTestCases + " milli seconds have been taken for each test case on average.");
		}

		if (out != null)
			out.println(parameters + "\t" + String.format("%1.5f", reciprocalRankSum / numberOfTestCases) + "\t" + elipsedTime / numberOfTestCases);

	}
	
	
	public void performDetailedTest(String outFileName) throws Exception{
		
		PrintWriter out = null;
		batchMode = true;
		gor.batchMode = true;
		long total_time = 0;
		
		if (outFileName != null)
			out = new PrintWriter(new FileWriter(outFileName, true));
		
		for (TestCase testcase: testcases){
			
			long startTime = System.currentTimeMillis();

			if (!testcase.description.equals("") && !batchMode)
				System.out.println(testcase.description);
			
			for (String relation: testcase.relations){
				if (relation.contains(","))
					gor.getGraph().addOneRelation(relation);
				else
					gor.getGraph().addOneNode(relation);
			}
			
			gor.setupInput();
			gor.optimize();
			gor.resolvePath();
			
			OutputGraphModel[] outputGraphs = gor.getOutputGraphs();
			double reciprocalRank = 0;
			
			for (int i = 0; i < outputGraphs.length; i++){
				
				if (!batchMode)
					System.out.println(outputGraphs[i].signature() + " " + outputGraphs[i].fitness);
				
				if (testcase.correct_mappings.contains(outputGraphs[i].signature())){
					reciprocalRank = 1.0 / (i + 1);
					break;
				}
			}
			
			reciprocalRankSum += reciprocalRank;
					
			if (!batchMode){
				
				System.out.println(testcase);
				
				if (reciprocalRank != 1.0)
					System.out.println(" warning: has a reciprocal rank " + String.format("%1.2f", reciprocalRank));
				else
					System.out.println(" perfect: has a reciprocal rank " + String.format("%1.2f", reciprocalRank));
					
				System.out.println();
			}
			
			
			gor.clear();
			
			long endTime = System.currentTimeMillis();
			long elipsedTime = endTime - startTime;
			total_time += elipsedTime;

			if (out != null)
				out.println(testcase.questionType + "\t" + testcase.questionType + testcase.questionNo + "\t" + elipsedTime + "\t" + String.format("%1.3f", reciprocalRank));
			
		}
		
		System.out.println(numberOfTestCases + " test cases in total.");
		out.println(numberOfTestCases + " test cases in total.");
		out.println(total_time + " milli seconds have been taken totally.");
		out.println(total_time / numberOfTestCases + " milli seconds have been taken for each test case on average.");
		out.println("The mean of reciprocal rank is " + String.format("%1.5f", reciprocalRankSum / numberOfTestCases));

		if (out != null)
			out.close();

	}

	
	public void clear(){
		
		reciprocalRankSum = 0;
		
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ReciprocalRankEvaluation test = new ReciprocalRankEvaluation(Configure.work_dir + "evaluation/testcases.txt", false);
		//ReciprocalRankEvaluation test = new ReciprocalRankEvaluation(Configure.work_dir + "evaluation/testcases_questions.txt", false);
		//ReciprocalRankEvaluation test = new ReciprocalRankEvaluation(Configure.work_dir + "evaluation/trial.txt", false);
		
		System.out.println("Start running test cases ...");
		//test.setAllParameters(10, 20, 40, 0.2, -1, 10, 2.0, 3, 0.20, 0.75);
		//test.gor.setAllParameters(10, 20, 40, 0.27, 0.75, 10, 2.0, 3, 0.20, 0.6125);
		//test.gor.setAllParameters(10, 20, 10, 0.27, 0.75, 10, 2.0, 3, 0.20, 0.6125);
		//test.setAllParameters(10, 20, 40, 0.35, 1, 10, 2.0, 3, 0.20, 0.5);
		//test.setAllParameters(10, 20, 40, 0.25, 0, 10, 2.0, 3, 0.20, 0.5);
		//test.setAllParameters(10, 20, 5, 0.25, 0, 10, 2.0, 3, 0.20, 0.5);

		if (Configure.work_dir.contains("/home/lushan1/dbpedia/"))
			//test.gor.setAllParameters(10, 20, 10, 0.27, 0.75, 10, 2.0, 3, 0.20, 0.6125);
			//test.gor.setAllParameters(10, 20, 10,  0.27, 0.75, 10, 2.0, 5, 0.20, 0.6125, 1.5);
			test.gor.setAllParameters(10, 20, 10, 0.25, 2.25, 10, 2.0, 5, 0.20, 0.4, 1.0);  // w2s2_w5s3
			//test.gor.setAllParameters(10, 20, 10, 0.25, 1.5, 10, 2.0, 5, 0.20, 0.4, 1.75);  // w2s2_w5s3 (optimal)
			//test.gor.setAllParameters(10, 20, 10, 0.25, 2.0, 10, 2.0, 5, 0.20, 0.6, 1.5);  // w2_w5
			//test.gor.setAllParameters(10, 20, 10, 0.25, -0.25, 10, 2.0, 5, 0.20, 0.6, 3.75);  // w2_w5 (optimal)
			//test.gor.setAllParameters(10, 20, 10, 1.0, 2.0, 10, 2.0, 5, 0.20, 1.0, 1.0); // string_sim
		else{
			//test.gor.setAllParameters(10, 20, 40, 0.27, 0.75, 10, 2.0, 5, 0.20, 0.6125, 1.0);
			//test.gor.setAllParameters(10, 20, 40, 0.25, 2.25, 10, 2.0, 5, 0.20, 0.4, 1.0);  // w2s2_w5s3
			test.gor.setAllParameters(10, 20, 40, 0.25, 2.0, 10, 2.0, 5, 0.20, 0.6, 1.5);  // w2_w5
		}
		
		//test.gor.batchMode = true;
		//test.performTest(null, "");
		//test.clear();
		
		test.performTest(null, "");
		
		test.clear();
		test.performDetailedTest(Configure.work_dir + "evaluation/output/testcases_detailed.out");

	}

}
