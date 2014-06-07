package edu.umbc.dblp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Map.Entry;

public class InstitutionWebsite {
	
	TreeMap<String, String> coreInstitutionSites;
	
	TreeMap<String, String> otherInstitutionSites;
	

	public InstitutionWebsite() {
		// TODO Auto-generated constructor stub
		coreInstitutionSites = new TreeMap<String, String>();
		otherInstitutionSites = new TreeMap<String, String>();
		
	}
	
	public void getCountryCodeforHost(String inputFile, String outputFile) throws IOException, ClassNotFoundException  {
		
		
		BufferedReader textReader = new BufferedReader(new FileReader(inputFile));
		PrintWriter out = new PrintWriter(new FileWriter(outputFile));
		
		String line;
		String outline;
		
		while ((line = textReader.readLine()) != null){
			
			int index = line.lastIndexOf(".");
			String country_code = line.substring(index + 1, line.length()); 
			
			outline = line + "\t" + country_code;
				
			out.println(outline);
		}
		
		out.close();

	}


	public void getInstitutionNameforHost(String inputFile, String outputFile) throws IOException, ClassNotFoundException  {
		
		
		BufferedReader textReader = new BufferedReader(new FileReader(inputFile));
		PrintWriter out = new PrintWriter(new FileWriter(outputFile));
		
		String line;
		String outline;
		
		while ((line = textReader.readLine()) != null){
			
			int index = line.indexOf(".");
			int index2 = line.indexOf(".", index + 1);
			String domain = line.substring(index + 1, line.length());
			
			if (index2 < 0 && (domain.equals("edu") || domain.equals("org") || domain.equals("com") || domain.equals("gov") )){
				
				outline = line + "\t" + line.substring(0, index);
				
			}else{
				
				outline = line + "\t" + line;
			}
				
			out.println(outline);
		}
		
		out.close();

	}

	public void collectRootWebsiteKeysFromEmails(String inputFile, String outputFile) throws IOException, ClassNotFoundException  {
		
		
		WebDomain domain = WebDomain.readModel("/home/lushan1/dblp/data/domain/domain.model");
		
		TreeSet<String> institutionKeys = new TreeSet<String>();
		
		BufferedReader textReader = new BufferedReader(new FileReader(inputFile));
		
		String line;
		
		while ((line = textReader.readLine()) != null){
			
			String key = domain.getRootFromEmail(line);
			
			if (key != null)
				institutionKeys.add(key.trim());
		}
		
		
		PrintWriter out = new PrintWriter(new FileWriter(outputFile));
		
		for (String siteKey: institutionKeys){
			
			out.println(siteKey);
		}
		
		out.close();

	}

	
	public void collectRootWebsiteKeysFromURLs(String inputFile, String outputFile) throws IOException, ClassNotFoundException  {
		
		
		WebDomain domain = WebDomain.readModel("/home/lushan1/dblp/data/domain/domain.model");
		
		TreeSet<String> institutionKeys = new TreeSet<String>();
		
		BufferedReader textReader = new BufferedReader(new FileReader(inputFile));
		
		String line;
		
		while ((line = textReader.readLine()) != null){
			
			String key = domain.getRootFromURL(line);
			
			if (key != null)
				institutionKeys.add(key.trim());
		}
		
		
		PrintWriter out = new PrintWriter(new FileWriter(outputFile));
		
		for (String siteKey: institutionKeys){
			
			out.println(siteKey);
		}
		
		out.close();

	}
	
	
	public void getPersonInstitutionKeyFromHomepage(String inputFile, String outputFile) throws IOException, ClassNotFoundException  {
		
		
		WebDomain domain = WebDomain.readModel("/home/lushan1/dblp/data/domain/domain.model");
		
	
		BufferedReader textReader = new BufferedReader(new FileReader(inputFile));
		
		PrintWriter out = new PrintWriter(new FileWriter(outputFile));
		
		String line;
		
		while ((line = textReader.readLine()) != null){
			
			int index = line.indexOf("\t");
			
			String personKey = line.substring(0, index);
			String url = line.substring(index + 1, line.length());
			
			String institutionKey = "";
			
			if (!url.equals("") && !url.contains("googlepages") && !url.contains("scholar.google") && !url.contains("sites.google") && !url.contains("plus.google"))
				institutionKey = domain.getRootFromURL(url);

			out.println(personKey + "\t" + institutionKey); 
		}
		
		out.close();

	}

	
	public void getPersonInstitutionKeyFromEmails(String inputFile, String outputFile) throws IOException, ClassNotFoundException  {
		
		
		WebDomain domain = WebDomain.readModel("/home/lushan1/dblp/data/domain/domain.model");
		
	
		BufferedReader textReader = new BufferedReader(new FileReader(inputFile));
		
		PrintWriter out = new PrintWriter(new FileWriter(outputFile));
		
		String line;
		
		while ((line = textReader.readLine()) != null){
			
			int index = line.indexOf("\t");
			
			String personKey = line.substring(0, index);
			String email = line.substring(index + 1, line.length());
			
			String institutionKey = "";
			
			if (!email.equals("") && !email.contains("gmail") && !email.contains("hotmail") && !email.contains("163.com"))
				institutionKey = domain.getRootFromEmail(email);

			if (institutionKey != null)
				out.println(personKey + "\t" + institutionKey); 
		}
		
		out.close();

	}

	public void getPubAuthorInstitutionKeyFromEmails(String inputFile, String outputFile) throws IOException, ClassNotFoundException  {
		
		
		WebDomain domain = WebDomain.readModel("/home/lushan1/dblp/data/domain/domain.model");
		
	
		BufferedReader textReader = new BufferedReader(new FileReader(inputFile));
		
		PrintWriter out = new PrintWriter(new FileWriter(outputFile));
		
		String line;
		
		while ((line = textReader.readLine()) != null){
			
			int index = line.lastIndexOf("\t");
			
			String key = line.substring(0, index);
			String email = line.substring(index + 1, line.length());
			
			String institutionKey = "";
			
			if (!email.equals("") && !email.contains("gmail") && !email.contains("hotmail") && !email.contains("163.com"))
				institutionKey = domain.getRootFromEmail(email);

			if (institutionKey != null)
				out.println(key + "\t" + institutionKey); 
		}
		
		out.close();

	}

	
	public void collectRootWebsites(String filename) throws IOException, ClassNotFoundException  {
		
		
		WebDomain domain = WebDomain.readModel("/home/lushan1/dblp/data/domain/domain.model");
		
		BufferedReader textReader = new BufferedReader(new FileReader(filename));
		
		String line;
		
		while ((line = textReader.readLine()) != null){
			
			int index = line.indexOf("\t");
			
			String key = line.substring(0, index);
			String name = line.substring(index + 1, line.length());
			
			URL url;
			String host;
			
			try {
				url = new URL(key);
				
				String path = url.getPath();
				
				if (!path.equals("") && !path.equals("/") && !path.matches("/index\\.[a-z][a-z][a-z][a-z]*"))
					continue;
				
				String query = url.getQuery();
				
				if (query != null)
					continue;
				
				host = url.getHost();

			} catch (MalformedURLException e1) {
				continue;
			}
			
			String root = domain.getRootFromHost(host);
			
			if (root != null && root.equals(host)){
				
				if (!coreInstitutionSites.containsKey(root))
					coreInstitutionSites.put(root, name);
			}
			
		}

	}

	public void collectOtherWebsites(String filename) throws IOException, ClassNotFoundException  {
		
		
		WebDomain domain = WebDomain.readModel("/home/lushan1/dblp/data/domain/domain.model");
		
		BufferedReader textReader = new BufferedReader(new FileReader(filename));
		
		String line;
		
		while ((line = textReader.readLine()) != null){
			
			int index = line.indexOf("\t");
			
			String key = line.substring(0, index);
			String name = line.substring(index + 1, line.length());
			
			URL url;
			String host;
			
			try {
				url = new URL(key);
				
				String path = url.getPath();
				
				if (!path.equals("") && !path.equals("/") && !path.matches("/index\\.[a-z][a-z][a-z][a-z]*"))
					continue;
				
				String query = url.getQuery();
				
				if (query != null)
					continue;
				
				host = url.getHost();

			} catch (MalformedURLException e1) {
				continue;
			}
			
			String root = domain.getRootFromHost(host);
			
			
			if (root != null && !root.equals(host)){
				
				if (!coreInstitutionSites.containsKey(root)){
					
					if (otherInstitutionSites.containsKey(root)){
						String oldname = otherInstitutionSites.get(root);
						if (name.length() < oldname.length())
							otherInstitutionSites.put(root, name);
						
					}else	
						otherInstitutionSites.put(root, name);
				}
			}
			
		}

	}

	
	public void saveRootWebstes(String filename) throws IOException{
		
		PrintWriter out = new PrintWriter(new FileWriter(filename));
		
		for (Entry<String, String> site: coreInstitutionSites.entrySet()){
			
			out.println(site.getKey() + "\t" + site.getValue());
		}
		
		out.close();
	}
	
	public void saveOtherWebstes(String filename) throws IOException{
		
		PrintWriter out = new PrintWriter(new FileWriter(filename));
		
		for (Entry<String, String> site: otherInstitutionSites.entrySet()){
			
			out.println(site.getKey() + "\t" + site.getValue());
		}
		
		out.close();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		InstitutionWebsite test = new InstitutionWebsite();
		//test.collectRootWebsites("/home/lushan1/dblp/data/univ_websites_B.txt");
		//test.collectOtherWebsites("/home/lushan1/dblp/data/university_list");
		//test.saveRootWebstes("/home/lushan1/dblp/data/univ_websites_B.core");
		//test.saveOtherWebstes("/home/lushan1/dblp/data/university_list.other");
		
		//test.collectRootWebsiteKeysFromEmails("/home/lushan1/dblp/data/tmp_affiliation_key.txt", "/home/lushan1/dblp/data/tmp_affiliation_key.out");
		//test.collectRootWebsiteKeysFromEmails("/home/lushan1/dblp/data/tmp_email.csv", "/home/lushan1/dblp/data/tmp_email.out");
		//test.collectRootWebsiteKeysFromURLs("/home/lushan1/dblp/data/tmp_homepage.csv", "/home/lushan1/dblp/data/tmp_homepage.out");
		//test.getInstitutionNameforHost("/home/lushan1/dblp/data/affiliation_key_all", "/home/lushan1/dblp/data/affiliation_implied_name");
		//test.getCountryCodeforHost("/home/lushan1/dblp/data/tmp_inst_key", "/home/lushan1/dblp/data/tmp_inst_key_countrycode");
		//test.getPersonInstitutionKeyFromHomepage("/home/lushan1/dblp/data/person_homepage.csv", "/home/lushan1/dblp/data/person_instKey_from_homepage.csv");
		//test.getPersonInstitutionKeyFromEmails("/home/lushan1/dblp/data/person_email.csv", "/home/lushan1/dblp/data/person_instKey_from_email.csv");
		//test.getPubAuthorInstitutionKeyFromEmails("/home/lushan1/dblp/data/pub_author_email.csv", "/home/lushan1/dblp/data/pub_author_instKey_from_email.csv");
		//test.getPubAuthorInstitutionKeyFromEmails("/home/lushan1/dblp/data/pub_author_email2.csv", "/home/lushan1/dblp/data/pub_author_instKey_from_email2.csv");
		test.getPersonInstitutionKeyFromHomepage("/home/lushan1/dblp/data/school_homepage.txt", "/home/lushan1/dblp/data/school_instKey_from_homepage.txt");
	}

}
