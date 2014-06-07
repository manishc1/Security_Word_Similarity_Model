package edu.umbc.tool;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupExample {

	public JsoupExample() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		/*
		String html = "<p>An <a href='http://example.com/'><b>example</b></a> link.</p>";
		Document doc = Jsoup.parse(html);
		*/
		
		Document doc = Jsoup.connect("http://www.nature.com").get();

		//System.out.println(doc.title());
		//System.out.println(doc.text());
		
		Elements links = doc.select("p");
		
		for (int i = 0; i < links.size(); i++){
			
			String text = links.get(i).text();
			
			System.out.println(text);

		}

		
		/*
		String text = doc.body().text(); // "An example link"
		String linkHref = link.attr("href"); // "http://example.com/"
		String linkText = link.text(); // "example""

		String linkOuterH = link.outerHtml(); 
		    // "<a href="http://example.com"><b>example</b></a>"
		String linkInnerH = link.html(); // "<b>example</b>"
		
		System.out.println(text);
		*/
		
	}

}
