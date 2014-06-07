package edu.umbc.dblp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;


public class WebDomain implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public HashSet<String> topLevelDomain;
	public HashSet<String> allLevelDomain;

	public WebDomain() {
		// TODO Auto-generated constructor stub
		topLevelDomain = new HashSet<String>();
		allLevelDomain = new HashSet<String>();
	}
	
	public void getTopLevelDomainFromFiles(String filename) throws IOException{
		
		BufferedReader textReader = new BufferedReader(new FileReader(filename));
		
		String line;
		
		while ((line = textReader.readLine()) != null){
			
			topLevelDomain.add(line.toLowerCase());
		}
	}
	
	
	public void getAllLevelDomainFromFiles(String filename) throws IOException{
		
		BufferedReader textReader = new BufferedReader(new FileReader(filename));
		
		String line;
		
		while ((line = textReader.readLine()) != null){
			
			allLevelDomain.add(line.toLowerCase());
		}
	}

	boolean isValidHost(String host){
		
		host = host.toLowerCase();
		
		StringTokenizer st = new StringTokenizer(host, ".");
		Vector<String> list = new Vector<String>();
		
		while (st.hasMoreElements()){
			list.add(st.nextToken());
		}
		
		if (list.size() > 5 || list.size() < 2)
			return false;
		
		if (!topLevelDomain.contains(list.lastElement()))
			return false;
		
		if (list.elementAt(list.size() - 2).equals("edu") ||  list.elementAt(list.size() - 2).equals("ac"))
			return true;
		
		String twoLevelDomain = list.elementAt(list.size() - 2) + "." + list.lastElement();
		
		if (!allLevelDomain.contains(twoLevelDomain))
			return false;
		
		return true;
	}
	
	public String getRootFromHost(String host){
		
		host = host.toLowerCase();
		
		StringTokenizer st = new StringTokenizer(host, ".");
		Vector<String> list = new Vector<String>();
		
		while (st.hasMoreElements()){
			list.add(st.nextToken());
		}
		
		if (list.size() > 5 || list.size() < 2)
			return null;
		
		if (list.lastElement().length() > 3)
			return null;
		
		int i = list.size();
		
		while (i > 0){
			
			try{
				if (allLevelDomain.contains(host))
					return  list.elementAt(list.size() - 1 - i) + "." + host;
			}catch (Exception e){
				System.out.println(e.toString());
				System.out.println(host);
				return null;
				
			}
			
			if (i == 2 && topLevelDomain.contains(list.lastElement()) 
					&& (list.elementAt(list.size() - 2).equals("edu") ||  list.elementAt(list.size() - 2).equals("ac")))
				return list.elementAt(list.size() - 1 - i) + "." + host;
			
			host = host.substring(host.indexOf(".") + 1, host.length());
			i --;
		}
		
		return null;
		
	}
	
	public String getRootFromURL(String url){
		
		URL aURL;
		
		try {
			aURL = new URL(url);
		} catch (MalformedURLException e) {
			return null;
		}
		
		String host = aURL.getHost();
		
		if (host.endsWith("\\"))
			host = host.substring(0, host.length()-1);
		
		return getRootFromHost(host);
		
	}
	
	public String getRootFromEmail(String email){
		
		int index = email.lastIndexOf("@");
		
		if (index <= 0)
			return null;
		
		String host = email.substring(index + 1, email.length());
		
		return getRootFromHost(host);
		
	}

	static public WebDomain readModel(String modelname) throws IOException, ClassNotFoundException{
        
		FileInputStream fileIn = new FileInputStream(modelname);
        ObjectInputStream objIn = new ObjectInputStream(fileIn);
        return (WebDomain) objIn.readObject();
	}
	
	
	static public void writeModel(String modelname, WebDomain model) throws IOException{

		FileOutputStream fileOut;
		ObjectOutputStream objOut;
		fileOut = new FileOutputStream(modelname);
		objOut = new ObjectOutputStream(fileOut);
		objOut.writeObject(model);
		objOut.close();
		System.out.println("seriliazation done!");

	}


	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		
		WebDomain domain = new WebDomain();
		domain.getTopLevelDomainFromFiles("/home/lushan1/dblp/data/domain/top_level_domain");
		domain.getAllLevelDomainFromFiles("/home/lushan1/dblp/data/domain/all_level_domain");
		System.out.println(domain.getRootFromHost("cs.umbc.edu"));
		System.out.println(domain.getRootFromURL("http://www.cs.umbc.edu"));
		System.out.println(domain.getRootFromEmail("lushan1@umbc.edu.cn"));
		
		System.out.println(domain.getRootFromHost("cs.umbc.edu.cn"));
		System.out.println(domain.getRootFromURL("http://www.cs.umbc.edu"));
		System.out.println(domain.getRootFromEmail("1@google.com"));

		//System.out.println(domain.topLevelDomain.contains("jp"));
		//System.out.println(domain.topLevelDomain.contains("jt"));
		//System.out.println(domain.allLevelDomain.contains("on.ca"));
		//System.out.println(domain.allLevelDomain.contains("nn.ca"));
		WebDomain.writeModel("/home/lushan1/dblp/data/domain/domain.model", domain);
		
		
		/*
		WebDomain domain = WebDomain.readModel("/home/lushan1/dblp/data/domain/domain.model");
		System.out.println(domain.topLevelDomain.contains("jp"));
		System.out.println(domain.topLevelDomain.contains("aa"));
		System.out.println(domain.allLevelDomain.contains("on.ca"));
		System.out.println(domain.allLevelDomain.contains("ac.uk"));
		
		System.out.println(domain.getRootFromHost("mrt.nodak.edu"));
		System.out.println(domain.getRootFromURL("http://www2.seika.ac.jp/"));
		System.out.println(domain.getRootFromEmail("1@google.com"));
		*/
	}

}
