package edu.umbc.gor.experiments;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

import edu.umbc.configure.Configure;
import edu.umbc.dblp.model.CorrelationModel;
import edu.umbc.dblp.model.GraphMapping;
import edu.umbc.dblp.model.OutputGraphModel;
import edu.umbc.web.SparqlRunner;

public class QA_Evaluation {
	
	public GraphMapping gor;
	public ArrayList<QA_TestCase> testcases;
	public int numberOfTestCases;
	public double precisionSum;
	public double recallSum;
	boolean batchMode = true;

	public QA_Evaluation(String testFile, boolean batchMode) throws Exception {
		// TODO Auto-generated constructor stub
		gor = new GraphMapping("");
		this.batchMode = batchMode;
		gor.batchMode = batchMode;
		testcases = new ArrayList<QA_TestCase>();
		
		QA_TestCaseGenerator generator = new QA_TestCaseGenerator(testFile);
		
		QA_TestCase testcase;
		while ((testcase = generator.getNextTestCase()) != null){
			testcases.add(testcase);
		}
		
		precisionSum = 0;
		recallSum = 0;
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

		for (QA_TestCase testcase: testcases){
			
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
			double precision = 0;
			double recall = 0;
			String sparlQuery;
			HashSet<String> queryResults;
			
			//for (int i = 0; i < outputGraphs.length && i < 5; i++){
			for (int i = 0; i < outputGraphs.length; i++){
				
				if (!batchMode)
					System.out.println(outputGraphs[i].signature() + " " + outputGraphs[i].fitness);
				
				// exact match entity name
				sparlQuery = outputGraphs[i].toSparqlQuery(0);
				
				queryResults = SparqlRunner.run(sparlQuery);
				
				if (queryResults.size() > 0){
					
					for (int j = 0; j < testcase.answerSets.size(); j++){
						
						precision = getPrecision(queryResults, testcase.answerSets.get(j));
						recall = getRecall(queryResults, testcase.answerSets.get(j));
					
						if (recall > 0)
							break;
					}
					
					break;
				}
				
				// exact match entity name appended with class name
				sparlQuery = outputGraphs[i].toSparqlQuery(1);
				
				queryResults = SparqlRunner.run(sparlQuery);
				
				if (queryResults.size() > 0){
					
					for (int j = 0; j < testcase.answerSets.size(); j++){
						
						precision = getPrecision(queryResults, testcase.answerSets.get(j));
						recall = getRecall(queryResults, testcase.answerSets.get(j));
					
						if (recall > 0)
							break;
					}
					
					break;
				}
				
				// match using bif:contains
				sparlQuery = outputGraphs[i].toSparqlQuery(2);
				
				queryResults = SparqlRunner.run(sparlQuery);
				
				if (queryResults.size() > 0){
					
					for (int j = 0; j < testcase.answerSets.size(); j++){
						
						precision = getPrecision(queryResults, testcase.answerSets.get(j));
						recall = getRecall(queryResults, testcase.answerSets.get(j));
					
						if (recall > 0)
							break;
					}
					
					break;
				}

				
				// remove non-essential relation
				boolean changed = outputGraphs[i].reduceNonEssentialRelation();
				
				if (changed){
					sparlQuery = outputGraphs[i].toSparqlQuery(2);
					
					queryResults = SparqlRunner.run(sparlQuery);
					
					if (queryResults.size() > 0){
						
						for (int j = 0; j < testcase.answerSets.size(); j++){
							
							precision = getPrecision(queryResults, testcase.answerSets.get(j));
							recall = getRecall(queryResults, testcase.answerSets.get(j));
						
							if (recall > 0)
								break;
						}
						
						break;
					}
				}
				
				// remove type constraints
				boolean hasAnswer = false;
				
				while (outputGraphs[i].hasTypeConstraints()){

					outputGraphs[i].removeMostGeneralTypeConstraint(gor.correlation_model);
					
					sparlQuery = outputGraphs[i].toSparqlQuery(2);
					
					queryResults = SparqlRunner.run(sparlQuery);
					
					if (queryResults.size() > 0){
						
						hasAnswer = true;
						
						for (int j = 0; j < testcase.answerSets.size(); j++){
							
							precision = getPrecision(queryResults, testcase.answerSets.get(j));
							recall = getRecall(queryResults, testcase.answerSets.get(j));
						
							if (recall > 0){
								break;
							}
						}
						
						break;
					}
				}
				
				if (hasAnswer)
					break;

			}
			
			precisionSum += precision;
			recallSum += recall;
			
			if (!batchMode){
				
				System.out.println(testcase);
				
				if (precision != 1.0 || recall != 1.0)
					System.out.println(" warning: has a precision " + String.format("%1.3f", precision) + ", recall " + String.format("%1.3f", recall));
				else
					System.out.println(" perfect: has a precision " + String.format("%1.3f", precision) + ", recall " + String.format("%1.3f", recall));
					
				System.out.println();
			}
			
			gor.clear();
		}
		
		System.out.println("The mean of precision is " + String.format("%1.5f", precisionSum / numberOfTestCases));
		System.out.println("The mean of recall is " + String.format("%1.5f", recallSum / numberOfTestCases));
		
		long endTime = System.currentTimeMillis();
		long elipsedTime = endTime - startTime;
		
		if (!batchMode){
			System.out.println(elipsedTime + " milli seconds have been taken totally.");
			System.out.println(numberOfTestCases + " test cases in total.");
			System.out.println(elipsedTime / numberOfTestCases + " milli seconds have been taken for each test case on average.");
		}

		if (out != null)
			out.println(parameters + "\t" + String.format("%1.5f", precisionSum / numberOfTestCases) + "\t" + String.format("%1.5f", recallSum / numberOfTestCases) + "\t" + elipsedTime / numberOfTestCases);

	}
	
	

	public void performDetailedTest(String outFileName) throws Exception{
		
		PrintWriter out = null;
		batchMode = true;
		gor.batchMode = true;
		long total_time = 0; 
		
		if (outFileName != null)
			out = new PrintWriter(new FileWriter(outFileName, true));

		for (QA_TestCase testcase: testcases){
			
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
			double precision = 0;
			double recall = 0;
			int numberOfSparqlQueries = 0;
			String sparlQuery;
			HashSet<String> queryResults;
			
			//for (int i = 0; i < outputGraphs.length && i < 5; i++){
			for (int i = 0; i < outputGraphs.length; i++){
				
				if (!batchMode)
					System.out.println(outputGraphs[i].signature() + " " + outputGraphs[i].fitness);
				
				// exact match entity name
				sparlQuery = outputGraphs[i].toSparqlQuery(0);
				
				queryResults = SparqlRunner.run(sparlQuery);
				numberOfSparqlQueries ++;
				
				if (queryResults.size() > 0){
					
					for (int j = 0; j < testcase.answerSets.size(); j++){
						
						precision = getPrecision(queryResults, testcase.answerSets.get(j));
						recall = getRecall(queryResults, testcase.answerSets.get(j));
					
						if (recall > 0)
							break;
					}
					
					break;
				}
				
				// exact match entity name appended with class name
				sparlQuery = outputGraphs[i].toSparqlQuery(1);
				
				queryResults = SparqlRunner.run(sparlQuery);
				numberOfSparqlQueries ++;
				
				if (queryResults.size() > 0){
					
					for (int j = 0; j < testcase.answerSets.size(); j++){
						
						precision = getPrecision(queryResults, testcase.answerSets.get(j));
						recall = getRecall(queryResults, testcase.answerSets.get(j));
					
						if (recall > 0)
							break;
					}
					
					break;
				}
				
				// match using bif:contains
				sparlQuery = outputGraphs[i].toSparqlQuery(2);
				
				queryResults = SparqlRunner.run(sparlQuery);
				numberOfSparqlQueries ++;
				
				if (queryResults.size() > 0){
					
					for (int j = 0; j < testcase.answerSets.size(); j++){
						
						precision = getPrecision(queryResults, testcase.answerSets.get(j));
						recall = getRecall(queryResults, testcase.answerSets.get(j));
					
						if (recall > 0)
							break;
					}
					
					break;
				}

				
				// remove non-essential relation
				boolean changed = outputGraphs[i].reduceNonEssentialRelation();
				
				if (changed){
					sparlQuery = outputGraphs[i].toSparqlQuery(2);
					
					queryResults = SparqlRunner.run(sparlQuery);
					numberOfSparqlQueries ++;
					
					if (queryResults.size() > 0){
						
						for (int j = 0; j < testcase.answerSets.size(); j++){
							
							precision = getPrecision(queryResults, testcase.answerSets.get(j));
							recall = getRecall(queryResults, testcase.answerSets.get(j));
						
							if (recall > 0)
								break;
						}
						
						break;
					}
				}
				
				// remove type constraints
				boolean hasAnswer = false;
				
				while (outputGraphs[i].hasTypeConstraints()){

					outputGraphs[i].removeMostGeneralTypeConstraint(gor.correlation_model);
					
					sparlQuery = outputGraphs[i].toSparqlQuery(2);
					
					queryResults = SparqlRunner.run(sparlQuery);
					numberOfSparqlQueries ++;
					
					if (queryResults.size() > 0){
						
						hasAnswer = true;
						
						for (int j = 0; j < testcase.answerSets.size(); j++){
							
							precision = getPrecision(queryResults, testcase.answerSets.get(j));
							recall = getRecall(queryResults, testcase.answerSets.get(j));
						
							if (recall > 0){
								break;
							}
						}
						
						break;
					}
				}
				
				if (hasAnswer)
					break;

			}
			
			precisionSum += precision;
			recallSum += recall;

			if (!batchMode){
				
				System.out.println(testcase);
				
				if (precision != 1.0 || recall != 1.0)
					System.out.println(" warning: has a precision " + String.format("%1.3f", precision) + ", recall " + String.format("%1.3f", recall));
				else
					System.out.println(" perfect: has a precision " + String.format("%1.3f", precision) + ", recall " + String.format("%1.3f", recall));
					
				System.out.println();
			}
			
			gor.clear();
			
			long endTime = System.currentTimeMillis();
			long elipsedTime = endTime - startTime;
			total_time += elipsedTime;

			if (out != null)
				out.println(testcase.questionType + "\t" + testcase.questionType + testcase.questionNo + "\t" + elipsedTime + "\t" + String.format("%1.3f", precision) + "\t" + String.format("%1.3f", recall) 
						+ "\t" + numberOfSparqlQueries);

		}
		
		System.out.println(numberOfTestCases + " test cases in total.");
		out.println(numberOfTestCases + " test cases in total.");
		out.println(total_time + " milli seconds have been taken totally.");
		out.println(total_time / numberOfTestCases + " milli seconds have been taken for each test case on average.");
		out.println("The mean of precision is " + String.format("%1.5f", precisionSum / numberOfTestCases));
		out.println("The mean of recall is " + String.format("%1.5f", recallSum / numberOfTestCases));

		
		if (out != null)
			out.close();

	}

	
	
	private double getRecall(HashSet<String> queryResults, HashSet<String> goldStandard) {
		
		if (queryResults.size() == 0)
			return 0;
		
		int count = 0;
		
		for (String entry: goldStandard){
			
			if (queryResults.contains(entry))
				count ++;
		}
		
		return (double) count / goldStandard.size();
	}


	private double getPrecision(HashSet<String> queryResults, HashSet<String> goldStandard) {

		if (queryResults.size() == 0)
			return 0;
		
		int count = 0;
		
		for (String result: queryResults){
			
			if (goldStandard.contains(result))
				count ++;
		}

		return (double) count / queryResults.size();
	}




	public void clear(){
		
		precisionSum = 0;
		recallSum = 0;
		
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		QA_Evaluation test = new QA_Evaluation(Configure.work_dir + "evaluation/QA_testcases.txt", false);
		//QA_Evaluation test = new QA_Evaluation(Configure.work_dir + "evaluation/QA_testcases.txt", false);
		//QA_Evaluation test = new QA_Evaluation(Configure.work_dir + "evaluation/QA_trial.txt", false);
		
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
			//test.gor.setAllParameters(10, 20, 10, 0.25, 2.25, 10, 2.0, 5, 0.20, 0.4, 1.0);  // w2s2_w5s3
			//test.gor.setAllParameters(10, 20, 10, 0.25, 1.5, 10, 2.0, 5, 0.20, 0.4, 1.75);  // w2s2_w5s3 (optimal)
			//test.gor.setAllParameters(10, 20, 10, 0.25, 2.0, 10, 2.0, 5, 0.20, 0.6, 1.5);  // w2_w5
			//test.gor.setAllParameters(10, 20, 10, 0.25, -0.25, 10, 2.0, 5, 0.20, 0.6, 3.75);  // w2_w5 (optimal)
			test.gor.setAllParameters(10, 20, 10, 1.0, 2.0, 10, 2.0, 5, 0.20, 1.0, 1.0); // string_sim
		else{
			//test.gor.setAllParameters(10, 20, 40, 0.27, 0.75, 10, 2.0, 5, 0.20, 0.6125, 1.0);
			//test.gor.setAllParameters(10, 20, 40, 0.25, 2.25, 10, 2.0, 5, 0.20, 0.4, 1.0);  // w2s2_w5s3
			test.gor.setAllParameters(10, 20, 40, 0.25, 2.0, 10, 2.0, 5, 0.20, 0.6, 1.5);  // w2_w5
		}
		
		//test.gor.batchMode = true;
		//test.performTest(null, "");
		//test.clear();
		
		//test.performTest(null, "");
		
		//test.clear();
		test.performDetailedTest(Configure.work_dir + "evaluation/output/QA_testcases_detailed.out");

	}

}
