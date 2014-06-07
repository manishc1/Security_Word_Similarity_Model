package edu.umbc.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.HashSet;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class SparqlRunner {

	private static String server_url = "http://ebserv2.cs.umbc.edu:8890/sparql?default-graph-uri=&";

	public SparqlRunner() {
		// TODO Auto-generated constructor stub
	}

	
	public static HashSet<String> run(String query) throws Exception  {

		HashSet<String> result = new HashSet<String>();

		try {
			
			String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");
			String url = server_url + "query=" + encodedQuery + "&format=text%2Fcsv&timeout=0&debug=on";

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);

			StatusLine status = response.getStatusLine();
			
			if (status.getStatusCode() == 500)
				return result;
			
			// Get the response
			BufferedReader reader = new BufferedReader
			  (new InputStreamReader(response.getEntity().getContent()));
			
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
            	
            	count ++;
            	
            	if (count == 1)
            		continue;
            	
            	if (line.startsWith("\"") && line.endsWith("\""))
            		result.add(line.substring(1, line.length() - 1));
            	else
            		result.add(line);
            }
			
            reader.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}

		return result;
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
