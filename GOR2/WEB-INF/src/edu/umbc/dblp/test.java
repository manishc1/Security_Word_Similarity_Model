package edu.umbc.dblp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class test {

	public test() {
		// TODO Auto-generated constructor stub
	}

	public static int getDepth(String path){
		
		int depth = 0;
		int place;
		String rest = path;
		
		do{
			place = rest.indexOf(", ");
			
			if (place > 0){
				rest = rest.substring(place + 2);
				depth ++;
			}
			
		}while (place > 0);
		
		return depth - 1;
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("This looks good, and I like it. I hope I can do it well");
		
		//URL url = new URL("http://lacitec.on.ca");
		URL url = new URL("http://pku.edu.cn/index.jsp");
		
		String path = url.getPath();
		
		System.out.println(path.matches("/index\\.[a-z][a-z][a-z][a-z]*"));
		//System.out.println(url.getContent());
		
		System.out.println(test.getDepth("Publication, >Conference_Proceedings, >^ISBN"));
		
		System.out.println(Math.pow(0.5, 1));
		
		String a = "UMBC";
		System.out.print(a.toLowerCase());
		System.out.print(a);
	}

}
