package edu.umbc.web;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import edu.umbc.dblp.WebDomain;


public class BingSearch {

	String searchEngineURL = "http://www.bing.com/search?q=";
	String keyPhrase1 = "<div class=\"sb_tlst\">";
	String keyPhrase2 = "<h3><a href="; //"class=\"sb_count\" id=\"count\">1-1";
	

	
	public BingSearch() {
		// TODO Auto-generated constructor stub
		
	}


	
	public String getTopURL(String name) throws Exception{
		
		StringBuffer content = new StringBuffer();
		
		String url_name = searchEngineURL + URLEncoder.encode(name, "utf-8");
		
		try {
			
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url_name);
			HttpResponse response = client.execute(request);

			// Get the response
			BufferedReader reader = new BufferedReader
			  (new InputStreamReader(response.getEntity().getContent()));
			
            String line;
            while ((line = reader.readLine()) != null) {
            	
            	content.append(line);
            }
			
			    
            /*
			URL url = new URL(url_name);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			
			int BUFFER_SIZE=512;
			char[] buffer = new char[BUFFER_SIZE]; // or some other size, 
			int charsRead = 0;
			
			Thread.sleep(100);
			
			while ( (charsRead  = reader.read(buffer,0,BUFFER_SIZE)) != -1) {
				
			  content.append(buffer,0, charsRead);
			  Thread.sleep(10);

			}
			*/
			
 
            reader.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		
		String returnpage = content.toString();
		String urlLiteral;
		
		int location = returnpage.indexOf(keyPhrase1);
		
		if (location > 0){
			
			location = returnpage.indexOf(keyPhrase2, location);
			
			int endLoc = returnpage.indexOf("\"", location + keyPhrase2.length() + 1);
			urlLiteral = returnpage.substring(location + keyPhrase2.length() + 1, endLoc).trim();
			
			if (urlLiteral.contains("wikipedia")){
				location = returnpage.indexOf(keyPhrase2, location + 1);
				endLoc = returnpage.indexOf("\"", location + keyPhrase2.length() + 1);
				urlLiteral = returnpage.substring(location + keyPhrase2.length() + 1, endLoc).trim();
				if (urlLiteral.contains("wikipedia"))
					urlLiteral = "";
			}
			
		}else
			throw new Exception("no url found in the returned page");
		
				
		Thread.sleep(200);

		return urlLiteral;
			
	}	
	
	
	
	public void getURLsFromInstitutionName(String inputFile, String outputFile) throws IOException   {
		
		
		BufferedReader textReader = new BufferedReader(new FileReader(inputFile));
		
		PrintWriter out = new PrintWriter(new FileWriter(outputFile));
		
		String line;
		
		while ((line = textReader.readLine()) != null){
			
			
			String url;
			try {
				url = getTopURL(line);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				url = "";
			}
			
			out.println(line + "\t" + url); 
			System.out.println(line + "\t" + url);
		}
		
		out.close();

	}


	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		BingSearch test = new BingSearch();
		
		long startTime = System.currentTimeMillis();

		test.getURLsFromInstitutionName("/tmp/school", "/tmp/school.out");
		//test.getTopURL("University of Braunschweig - Institute of Technology");
				
		long endTime = System.currentTimeMillis();
		long elipsedTime = endTime - startTime;
		System.out.println(elipsedTime + " milli seconds have been taken.");

		
		
	}

	
	
}
