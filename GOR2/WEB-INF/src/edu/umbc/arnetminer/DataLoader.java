package edu.umbc.arnetminer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.csvreader.CsvWriter;

public class DataLoader {
	
	String data_dir = "/home/lushan1/dblp/arnetminer/";
	
	private int count = 0;
	
	BufferedReader textReader;
	
    CsvWriter publication_csv_out;
    CsvWriter author_csv_out;
    CsvWriter cites_csv_out;

    ArnetPaper record = null;
    ArrayList<String> authors; 	//= new ArrayList<String>();
    ArrayList<String> cites; 	// = new ArrayList<String>();

    
	public DataLoader(String filename) {
		// TODO Auto-generated constructor stub
		
        try{
        	
        	textReader = new BufferedReader(new FileReader(filename));
        	
        	publication_csv_out = new CsvWriter(new PrintWriter(
    				new OutputStreamWriter(new FileOutputStream(data_dir + "output/publications.csv"),
    						Charset.forName("UTF-8"))), ',');

        	author_csv_out = new CsvWriter(new PrintWriter(
    				new OutputStreamWriter(new FileOutputStream(data_dir + "output/authors.csv"),
    						Charset.forName("UTF-8"))), ',');


        	cites_csv_out = new CsvWriter(new PrintWriter(
    				new OutputStreamWriter(new FileOutputStream(data_dir + "output/cites.csv"),
    						Charset.forName("UTF-8"))), ',');

        	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}


	}
	
    private void initializeData() {
        record = new ArnetPaper();
        authors = new ArrayList<String>();
        cites = new ArrayList<String>();
    } //- initializeData

    
    private void processRecord() {
        record.setAuthors(authors);
        record.setCites(cites);
        
        // Send the record to the database
        sendRecord(record);
    } //- processRecord

    
	public void load() throws IOException{
		
		String curLine;
		
		initializeData();
		
		while ((curLine = textReader.readLine()) != null){
			
			curLine = curLine.trim();
			
			if (curLine.equals("")){
				processRecord();
				initializeData();
			
			}else if (curLine.startsWith("#*")){
				
				String title = curLine.substring(2, curLine.length());
				
				if (title.endsWith("."))
					title = title.substring(0, title.length() - 1);
				
				record.setTitle(title);
				
			}else if (curLine.startsWith("#@")){
				
				String authorsLine = curLine.substring(2, curLine.length());
				
				StringTokenizer st = new StringTokenizer(authorsLine, ",");
				
				while (st.hasMoreElements()){
					authors.add(st.nextToken());
				}
				
			}else if (curLine.startsWith("#year")){
				
				record.setYear(curLine.substring(5, curLine.length()));				
			
			}else if (curLine.startsWith("#conf")){
				
				record.setConference(curLine.substring(5, curLine.length()));				
			
			}else if (curLine.startsWith("#index")){
				
				record.setId(curLine.substring(6, curLine.length()));				
			
			}else if (curLine.startsWith("#%")){
				
				cites.add(curLine.substring(2, curLine.length()));
			
			}else if (curLine.startsWith("#!")){
				
				record.setAbstract(curLine.substring(2, curLine.length()));
				
			}
			
		}
		
		if (record.getId() != null){
			processRecord();
		}
		
        publication_csv_out.close();
        author_csv_out.close();
        cites_csv_out.close();

	}
	
    private void sendRecord(ArnetPaper record) {
    	
    	String[] publication_record = new String[5];
    	
    	publication_record[0] = record.getId();
    	publication_record[1] = record.getTitle();
    	publication_record[2] = record.getYear();
    	publication_record[3] = record.getConference();
    	publication_record[4] = record.getAbstract();
    	
    	
    	try {
			publication_csv_out.writeRecord(publication_record);
			
			// process authors
			String[] author_record = new String[3];

			for (int i=0; i < record.getAuthors().size(); i++){
				
				String author = record.getAuthors().get(i);
				
				author_record[0] = record.getId();
				author_record[1] = author;
				author_record[2] = String.valueOf(i + 1);
				
				author_csv_out.writeRecord(author_record);
				
			}
			
			
			// process citations
			String[] cite_record = new String[2];
			
			for (int i=0; i < record.getCites().size(); i++){
				
				cite_record[0] = record.getId();
				cite_record[1] = record.getCites().get(i);
				
				cites_csv_out.writeRecord(cite_record);
			}
			
			
			count ++;
			
			if (count % 10000 == 0)
				System.out.println(count);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	/*
        logger.info("Storing: " + record.getDkey());
        try {
            csxemetadata.addDBLPRecord(record);
        }catch (DataAccessException e) {
            logger.error("Storing: " + record.getDkey(), e);
        }
        */
    } //- sendRecord


	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		DataLoader loader = new DataLoader("/home/lushan1/dblp/arnetminer/DBLP-citation-Feb21.txt");
		loader.load();
		
	}

}
