package edu.umbc.gor.experiments;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import edu.umbc.configure.Configure;
import edu.umbc.dblp.model.CorrelationModel;
import edu.umbc.dblp.model.GraphMapping;
import edu.umbc.dblp.model.OutputGraphModel;

public class TestcasesSplitor {
	
	public ArrayList<TestCase> testcases;
	public int numberOfTestCases;
	public String testfile;
	private PrintWriter out1;
	private PrintWriter out2;
	private PrintWriter out;
	private Random random;


	public TestcasesSplitor(String testFile, String outFile1, String outFile2, int randSeed) throws Exception {
		// TODO Auto-generated constructor stub
		testfile = testFile;
		out1 = new PrintWriter(new FileWriter(outFile1, true));
		out2 = new PrintWriter(new FileWriter(outFile2, true));
		random = new Random(randSeed);


	}
	
	
	public void Split() throws Exception {
		// TODO Auto-generated constructor stub
		
		TestCaseGenerator generator = new TestCaseGenerator(testfile);
		out = out1;
		int r = 0;
		boolean secondRound = false;
		
		TestCase testcase;
		while ((testcase = generator.getNextTestCase()) != null){
			
			if (!testcase.description.equals("")){
				
				if (!secondRound){
					r = random.nextInt(2);
					
					if (r == 0) 
						out = out1;
					else
						out = out2;
					
					secondRound = true;
				
				}else{
					if (r == 0)
						out = out2;
					else
						out = out1;
					
					secondRound = false;
				}
			}
			
			if (!testcase.description.equals(""))
				out.print("#" + testcase.description);
			
			for (String relation: testcase.relations){
				out.println(relation);
			}

			for (String mapping: testcase.correct_mappings){
				out.println(">" + mapping);
			}
			
			out.println();

		}
		
		
		out1.close();
		out2.close();
		
	}


	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		int randSeed = 2;
		TestcasesSplitor splitor = new TestcasesSplitor("/home/lushan1/dblp/evaluation/testcases.txt", "/home/lushan1/dblp/evaluation/testcases_" + randSeed + "A.txt", "/home/lushan1/dblp/evaluation/testcases_" + randSeed + "B.txt", randSeed);
		splitor.Split();
	}

}
