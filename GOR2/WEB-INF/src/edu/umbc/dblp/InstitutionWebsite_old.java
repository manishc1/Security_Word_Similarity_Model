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
import java.util.TreeSet;
import java.util.Vector;

public class InstitutionWebsite_old {
	
	TreeSet<String> institutionSites;
	
	
	

	public InstitutionWebsite_old() {
		// TODO Auto-generated constructor stub
		institutionSites = new TreeSet<String>();
		
	}

	
	public void parseSitesFromURLs(String filename) throws IOException  {
		
		BufferedReader textReader = new BufferedReader(new FileReader(filename));
		
		String line;
		
		while ((line = textReader.readLine()) != null){
			
			StringTokenizer st = new StringTokenizer(line, ".");
			Vector<String> list = new Vector<String>();
			
			while (st.hasMoreElements()){
				list.add(st.nextToken());
			}
			
			if (list.size() > 5 || list.size() < 2)
				continue;
			
			if (list.lastElement().length() > 3)
				continue;
			
			String domain = list.lastElement();
			boolean found = false;
			
			for (int i = list.size() - 2; i >= 0; i--){
				
				domain = list.elementAt(i) + "." + domain;
				
				if (institutionSites.contains(domain))
					break;
				
				URL url;
				
				try {
					url = new URL("http://" + domain);
					url.getContent();
				} catch (MalformedURLException e1) {
					break;
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					continue;
				}
				
				found = true;
				break;
			}
			
			if (found == true){
				institutionSites.add(domain);
			}
			
		}

	}
	
	
	public void parseRootsFromURL(String filename) throws IOException, ClassNotFoundException  {
		
		
		WebDomain domain = WebDomain.readModel("/home/lushan1/dblp/data/domain/domain.model");
		
		BufferedReader textReader = new BufferedReader(new FileReader(filename));
		
		String line;
		
		while ((line = textReader.readLine()) != null){
			
			String root = domain.getRootFromHost(line);
			
			if (root != null){
				institutionSites.add(root);
			}
			
		}

	}

	
	public void saveInstitutionWebstes(String filename) throws IOException{
		
		PrintWriter out = new PrintWriter(new FileWriter(filename));
		
		for (String site: institutionSites){
			
			out.println(site);
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
		InstitutionWebsite_old test = new InstitutionWebsite_old();
		//test.parseSitesFromURLs("/home/lushan1/dblp/data/university_2006_test.txt");
		test.parseRootsFromURL("/home/lushan1/dblp/data/university_2006_test.txt");
		test.saveInstitutionWebstes("/home/lushan1/dblp/data/university_2006_test_out2.txt");
	}

}
