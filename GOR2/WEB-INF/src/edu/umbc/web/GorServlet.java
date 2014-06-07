package edu.umbc.web;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.umbc.configure.Configure;
import edu.umbc.dblp.model.GraphMapping;
import edu.umbc.dblp.model.OutputGraphModel;


public class GorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static GraphMapping workflow = null;
	private static String LOCAL_PATH;

	/*
	private static String OUTPUT_TEMPLATE1 = "singlewordfixeddomain.template";
    private static String NAMESPACE_VAR = "$namespaces";
    private static String WORD_VAR = "$word";
    private static String TERM_VAR = "$terms";
    private static String CLASS_VAR = "$class";
    private static String PROPERTY_VAR = "$property";
	*/
	
	private static String OUTPUT_TEMPLATE_MainPage = "index.template";
	private static String OUTPUT_TEMPLATE_Translations = "translations.template";
	private static String QUERY_VAR = "$query";
    private static String MESSAGE_VAR = "$message";	
    //private static String server_url = "http://greententacle.techfak.uni-bielefeld.de:5171/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&";  
    private static String server_url = "http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&";
    //private static String server_url = "http://lod.openlinksw.com/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&";
    //private static String server_url = "http://lod.openlinksw.com/sparql?default-graph-uri=&";
    private static int MAX_ROWS = 4;
        
	
	public GorServlet() {
		super();
	}
	
    public void init() throws ServletException
    {
        super.init() ;
        return ;
    }

    /** Initializes the servlet.
    */
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
		LOCAL_PATH = this.getServletContext().getRealPath("" + File.separatorChar);
		try{
			workflow = new GraphMapping(LOCAL_PATH);
			
			if (Configure.work_dir.contains("/home/lushan1/dbpedia/"))
				//workflow.setAllParameters(10, 20, 20, 0.27, 0.75, 10, 2.0, 5, 0.20, 0.6125, 1.0);
				//workflow.setAllParameters(10, 20, 10, 0.27, 0.75, 10, 2.0, 5, 0.20, 0.6125, 1.5);
				workflow.setAllParameters(  10, 20, 10, 0.25, 2.25, 10, 2.0, 5, 0.20, 0.4, 1.0);
			else
				//workflow.setAllParameters(10, 20, 40, 0.27, 0.75, 10, 2.0, 3, 0.20, 0.6125, 1.0);
				workflow.setAllParameters(10, 20, 40, 0.25, 2.25, 10, 2.0, 5, 0.20, 0.4, 1.0);


		}catch (Exception e){
			throw new ServletException(e.getCause());
		}
		
        OUTPUT_TEMPLATE_MainPage = this.getServletContext().getRealPath(File.separatorChar + OUTPUT_TEMPLATE_MainPage);
        OUTPUT_TEMPLATE_Translations = this.getServletContext().getRealPath(File.separatorChar + OUTPUT_TEMPLATE_Translations);
        
        
    }
    
    
    
	protected void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse) 
	throws IOException{
		doPost(httpRequest, httpResponse);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	throws IOException{

		System.out.println("Current thread ID is: " + Thread.currentThread().getId());
		String formType = request.getParameter("formType");
		response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
		
        if (formType.equalsIgnoreCase("mainpage")){
			
			String input_graph = request.getParameter("relations");
			String choice = request.getParameter("query");

			FileWriter fileOfQueryLog;
			PrintWriter query_log;
			try {
				fileOfQueryLog = new FileWriter("query.log", true);
				//query_log = new PrintWriter(new BufferedWriter(fileOfQueryLog));
				query_log = new PrintWriter(fileOfQueryLog);
				query_log.println(input_graph);
				query_log.println();
				query_log.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	        Vector<String> relations = new Vector<String>();
	        StringTokenizer st = new StringTokenizer(input_graph, "\n\r");
	        while (st.hasMoreTokens()){
	        	String relation = st.nextToken();
	        	if (!relation.trim().equals(""))
	        		relations.add(relation.trim());
	        }

		
			File f = new File(OUTPUT_TEMPLATE_MainPage);
			System.out.println("template file: "+f.getAbsolutePath());
			BufferedReader fileReader = new BufferedReader(new FileReader(f));

			try{

				if (relations.size() == 0){
					throw new IOException("must need at least a single relation!");
				}
		
				if (relations.size() > MAX_ROWS)
					throw new IOException("The maximum number of relations has been set to 4.");

				
				for (String relation: relations){
					if (relation.contains(","))
						workflow.getGraph().addOneRelation(relation);
					else
						workflow.getGraph().addOneNode(relation);
				}
				
				workflow.setupInput();
				System.out.println("Starting translating ...");
				workflow.optimize();
				workflow.resolvePath();

				try{
					//workflow.printOptimizedResult();
					
					OutputGraphModel[] outputGraphs = workflow.getOutputGraphs();
					
					for (int i = 0; i < outputGraphs.length; i++){
							System.out.println(outputGraphs[i].signature() + " " + outputGraphs[i].fitness);
					}

				}catch(Exception e){
					e.printStackTrace();
				}

				//workflow.improveRecallByCollectingSimilarProperties();
				
				/* ---------------------------------- */
				/* Begin Debug Area                   */
				/* ---------------------------------- */
				
				for (int i = 0; i < workflow.NumberOfBestCombinations; i++){
					//System.out.println(workflow.getInterpretationFromOptimalResult(workflow.getCombinationResults()[i]));
					//System.out.println();
				}

				/*
				for (int i = 0; i < workflow.NumberOfBestCombinations; i++){
					System.out.println("The " + i + "th best result after clustering similar properties is as follows: ");
					System.out.println(workflow.getInterpretationFromImprovedResult(workflow.getImprovedResults()[i]));
					System.out.println();
				}
				*/

				
				/* ---------------------------------- */
				/* End Debug Area                     */
				/* ---------------------------------- */
				
				
				
				if (choice.equals("See Translations")){
					File file = new File(OUTPUT_TEMPLATE_Translations);
					System.out.println("template file: "+file.getAbsolutePath());
					BufferedReader fileReader_translations = new BufferedReader(new FileReader(file));

					StringBuffer buffer = new StringBuffer();
			        String rdline;
			        int count = 0;
			        rdline= fileReader_translations.readLine();
			        
			        while (rdline!=null){
			        	if (!rdline.startsWith("$")){
			        		buffer.append(rdline);
			        	}else{
			        		int interpretationNo = count / 6;
			        		int position = count % 6;

			        		if (interpretationNo < workflow.getCombinationResults().length){
				        		if (position == 0)
				        			buffer.append(workflow.getInterpretationFromOptimalResult(workflow.getCombinationResults()[interpretationNo]));
				        		else if (position == 1)
				        			buffer.append(workflow.makeSparqlQueryFromOptimalResult(workflow.getCombinationResults()[interpretationNo]));
				        		else if (position == 2)
				        			buffer.append(workflow.makeConciseSparqlQueryFromOptimalResult(workflow.getCombinationResults()[interpretationNo]));
				        		//else if (position == 3)
				        		//	buffer.append(workflow.getInterpretationFromImprovedResult(workflow.getImprovedResults()[interpretationNo]));
				        		//else if (position == 4)
				        		//	buffer.append(workflow.makeSparqlQueryFromImprovedResult(workflow.getImprovedResults()[interpretationNo]));
				        		//else if (position == 5)
				        		//	buffer.append(workflow.makeConciseSparqlQueryFromImprovedResult(workflow.getImprovedResults()[interpretationNo]));
			        		}	
				        		
			        		count ++;
			        	}
				        	
			        	rdline = fileReader_translations.readLine();
			        }
			        
			        writer.println(buffer.toString());

				}else if (choice.equals("Query")){
					String query = workflow.makeConciseSparqlQueryFromImprovedResult(workflow.getImprovedResults()[0]);
					String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");
					String location = server_url + "query=" + encodedQuery + "&format=text%2Fhtml&timeout=0&debug=on";
					response.sendRedirect(location);
					
				}else if (choice.equals("Relaxed Query")){
					String query = workflow.makeReducedSparqlQueryFromImprovedResult(workflow.getImprovedResults()[0]);
					String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");
					String location = server_url + "query=" + encodedQuery + "&format=text%2Fhtml&timeout=0&debug=on";
					response.sendRedirect(location);
					
				}
				
			}catch (Exception e){
		        //generate output
		        String rdline;
		        
		        rdline= fileReader.readLine();
		        
		        while (rdline!=null){
		        	if (!rdline.startsWith("$")){
			        	writer.println(rdline);
			        	
		        	}else if (rdline.equals(QUERY_VAR)){
		        		writer.print(input_graph);
		        	}else if (rdline.equals(MESSAGE_VAR)){
	
		        		writer.print("You have an error in your query. " + e.getMessage());
		        		
		        		
		        	}
		        	rdline = fileReader.readLine();
		        }

	    		//response.flushBuffer();
	    		//response.reset();

	        }

			workflow.clear();

		} else if (formType.equalsIgnoreCase("SparqlQuery")){
			String query = request.getParameter("sparql");
			String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");
			String location = server_url + "query=" + encodedQuery + "&format=text%2Fhtml&timeout=0&debug=on";
			response.sendRedirect(location);
		}
		
		writer.flush();
		writer.close();
        
	}

}
