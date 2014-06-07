package edu.umbc.nlp.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


public class CoOccurModelByBing {

	String searchEngineURL = "http://www.bing.com/search?q=";
	String keyPhrase = "class=\"sb_count\" id=\"count\">1-1";
	
	public int CO_OCCUR_THRESHOLD = 1;
	public long totalPages = Long.parseLong("900000000000"); // 8700000000 * 50

	
	public CoOccurModelByBing() {
		// TODO Auto-generated constructor stub
		
	}

	public long getCoOccurrence(String word1, String word2) throws Exception{
		
		StringBuffer content = new StringBuffer();
		
		try {
			URL url = new URL(searchEngineURL + word1 + "+near%3A40+" + word2);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			 
			            String line;
			            while ((line = reader.readLine()) != null) {
			            	
			            	content.append(line);
			            }
			 
			            reader.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		
		String returnpage = content.toString();
		String frequencyLiteral;
		
		int location = returnpage.indexOf(keyPhrase);
		
		if (location > 0){
			
			int endLoc = returnpage.indexOf("results", location);
			frequencyLiteral = returnpage.substring(location + keyPhrase.length() + 4, endLoc).trim();
			
		}else
			throw new Exception("no frequency found in the returned page");
		
		frequencyLiteral = frequencyLiteral.replaceAll(",", "");
		
		long frequency = Long.parseLong(frequencyLiteral);

		Thread.sleep(200);
		
		return frequency;
			
	}
	
	public long getFrequency(String word) throws Exception{
		
		StringBuffer content = new StringBuffer();
		
		try {
			URL url = new URL(searchEngineURL + word);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			 
			            String line;
			            while ((line = reader.readLine()) != null) {
			            	
			            	content.append(line);
			            }
			 
			            reader.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		
		String returnpage = content.toString();
		String frequencyLiteral;
		
		int location = returnpage.indexOf(keyPhrase);
		
		if (location > 0){
			
			int endLoc = returnpage.indexOf("results", location);
			frequencyLiteral = returnpage.substring(location + keyPhrase.length() + 4, endLoc).trim();
			
		}else
			throw new Exception("no frequency found in the returned page");
		
		frequencyLiteral = frequencyLiteral.replaceAll(",", "");
		
		long frequency = Long.parseLong(frequencyLiteral);
		
		Thread.sleep(200);

		return frequency;
			
	}	
	
	
	public float getPMI(String word1, String word2) throws Exception{

		double frqWord1 = getFrequency(word1);
		
		double frqWord2 = getFrequency(word2);
		
		/*
		if (frqWord1 > totalWords / 5000) 
			frqWord1 = totalWords / 5000 + (frqWord1 - totalWords / 5000) * 0.5;
		
		if (frqWord2 > totalWords / 5000) 
			frqWord2 = totalWords / 5000 + (frqWord2 - totalWords / 5000) * 0.5;
		*/
		
		double frqWord1Word2 = getCoOccurrence(word1, word2);
		if (frqWord1Word2 < CO_OCCUR_THRESHOLD) 
			throw new Exception(word1 + " has no or rare co-occurrence with " + word2);

		if (frqWord1 == 0 || frqWord2 == 0) 
			throw new Exception(word1 + " or " + word2 + " has zero frequency");
		
		double pmi = Math.log(((double) frqWord1Word2 * totalPages) / ((double) frqWord1 * frqWord2));
		//double pmi = Math.log(((double) frqWord1Word2 * totalWords) / ( Math.pow(frqWord1, 1) * Math.pow(frqWord2, 1)));
		return (float) pmi; 
	}
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		CoOccurModelByBing test = new CoOccurModelByBing();
		
		long startTime = System.currentTimeMillis();

		System.out.println(test.getCoOccurrence("enormously", "tremendously"));
		System.out.println("enormously and tremendously have a pmi: " + test.getPMI("enormously", "tremendously"));
		System.out.println("provision and stipulation have a pmi: " + test.getPMI("provision", "stipulation"));
		System.out.println("prominent and conspicuous have a pmi: " + test.getPMI("prominent", "conspicuous"));
		System.out.println("zenith and pinnacle have a pmi: " + test.getPMI("zenith", "pinnacle"));
		System.out.println("flawed and imperfect have a pmi: " + test.getPMI("flawed", "imperfect"));
		System.out.println("desperately and urgently have a pmi: " + test.getPMI("desperately", "urgently"));
				
		long endTime = System.currentTimeMillis();
		long elipsedTime = endTime - startTime;
		System.out.println(elipsedTime + " milli seconds have been taken.");

		System.out.println(test.getFrequency("enormously"));
		
		
	}

	
	
}
