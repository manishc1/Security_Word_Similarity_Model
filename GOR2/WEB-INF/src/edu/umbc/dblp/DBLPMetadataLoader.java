package edu.umbc.dblp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import org.springframework.dao.DataAccessException;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.csvreader.CsvWriter;

//import edu.psu.citeseerx.dao2.logic.CSXExternalMetadataFacade;
//import edu.psu.citeseerx.domain.DBLP;

/**
 * Stores all metadata from a new dblp.xml file into the external metadata
 * storage. This data is used by other components in different ways. For example,
 * to obtain information to generate links from summary pages or correct
 * metadata in CiteSeerX corpus.
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 1083 $ $Date: 2009-04-15 15:24:12 -0400 (Wed, 15 Apr 2009) $
 */
public class DBLPMetadataLoader {
    
    protected final Log logger = LogFactory.getLog(getClass());
    
    private static final String[] dblPElements = {
        "article", "inproceedings", "proceedings", "book", "incollection",
        "phdthesis", "mastersthesis", "www"};
    
    // Fields that will appear just once per record
    private static final String[] dblpSingleFields = {"title", "booktitle", 
        "pages", "year", "address", "journal", "volume", "number", "month",
        "url", "ee", "cdrom", "publisher", "note", "crossref", "isbn", "series",
        "school", "chapter"
    };
    
    private static final String[] dblpMultiFields = {
        "author", "editor", "cite"
    };
    
    private Set<String> elements;
    private Set<String> singleFields;
    private Set<String> multiFields;
    
    /*
    private CSXExternalMetadataFacade csxemetadata;

    public void setCSXEMETADATA(CSXExternalMetadataFacade csxemetadata) {
        this.csxemetadata = csxemetadata;
    } //- setCSXEMETADATA
    */
    
    private int count = 0;
    
    String data_dir = "/home/lushan1/dblp/";
    
    private String DBLPDataFile;
    
    /**
     * @param DBLPDataFile DBLP XML file location (full path)
     */
    public void setDBLPDataFile(String DBLPDataFile) {
        this.DBLPDataFile = DBLPDataFile;
    } //- setDBLPDataFile

    private String DBLPDTDFile;
    
    /**
     * @param file DBLP DTD file location (full path)
     */
    public void setDBLPDTDFile(String file) {
        DBLPDTDFile = file;
    } //- setDBLPDTDFile
    
    // ContentHandlers.
    private DBLPHandler dblpHandler;
    
    CsvWriter publication_csv_out;
    CsvWriter author_csv_out;
    CsvWriter editor_csv_out;
    CsvWriter cites_csv_out;

    
    public DBLPMetadataLoader() {
        dblpHandler = new DBLPHandler();

        elements = new HashSet<String>();
        for (int i = 0; i < dblPElements.length; ++i) {
            elements.add(dblPElements[i]);
        }
        singleFields = new HashSet<String>();
        for (int i = 0; i < dblpSingleFields.length; ++i) {
            singleFields.add(dblpSingleFields[i]);
        }
        multiFields = new HashSet<String>();
        for (int i = 0; i < dblpMultiFields.length; ++i) {
            multiFields.add(dblpMultiFields[i]);
        }
        
        try{
        	
        	publication_csv_out = new CsvWriter(new PrintWriter(
    				new OutputStreamWriter(new FileOutputStream(data_dir + "output/publications.csv"),
    						Charset.forName("UTF-8"))), ',');

        	author_csv_out = new CsvWriter(new PrintWriter(
    				new OutputStreamWriter(new FileOutputStream(data_dir + "output/authors.csv"),
    						Charset.forName("UTF-8"))), ',');

        	editor_csv_out = new CsvWriter(new PrintWriter(
    				new OutputStreamWriter(new FileOutputStream(data_dir + "output/editors.csv"),
    						Charset.forName("UTF-8"))), ',');

        	cites_csv_out = new CsvWriter(new PrintWriter(
    				new OutputStreamWriter(new FileOutputStream(data_dir + "output/cites.csv"),
    						Charset.forName("UTF-8"))), ',');

        	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
        
        
    } //- DBLPMetadataUpdater

    // This one will use a SAX parser.
    public void updateDBLP() {
        try {
            // Get the SAX factory.
            SAXParserFactory factory = SAXParserFactory.newInstance();
            
            // Neither we want validation nor namespaces.
            factory.setNamespaceAware(false);
            factory.setValidating(true);

            SAXParser parser = factory.newSAXParser();
            parser.getXMLReader().setEntityResolver(
                    new DBLPEntityResolver(DBLPDTDFile));

            /*xmlReader.setFeature(
                    "http://apache.org/xml/features/nonvalidating/load-external-dtd", 
                    false);*/
            
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(DBLPDataFile);
              }
            
            catch( Exception e ) {
                System.err.println( "Exception e = " + e );
                e.printStackTrace();
                return;
            }; 

            parser.parse(inputStream, dblpHandler);
            
            publication_csv_out.close();
            author_csv_out.close();
            editor_csv_out.close();
            cites_csv_out.close();
            
            
        }catch (ParserConfigurationException e) {
            logger.error("The underlaying parser doesn't support the " +
            		"requested feature", e);
            System.out.println(e);
        }catch(SAXException e) {
            logger.error("Error", e);
            System.out.println(e);

        }catch(IOException e) {
            logger.error("A parsing error has occurred: " + DBLPDataFile, e);
            System.out.println(e);

        }
        
    } //- updateDBLP
    

    private void sendRecord(DBLP record) {
    	
    	String[] publication_record = new String[21];
    	
    	publication_record[0] = record.getDkey();
    	publication_record[1] = record.getType();
    	publication_record[2] = record.getTitle();
    	publication_record[3] = record.getBookTitle();
    	publication_record[4] = record.getPages();
    	publication_record[5] = record.getYear();
    	publication_record[6] = record.getAddress();
    	publication_record[7] = record.getJournal();
    	publication_record[8] = record.getVolume();
    	publication_record[9] = record.getNumber();
    	publication_record[10] = record.getMonth();
    	publication_record[11] = record.getUrl();
    	publication_record[12] = record.getEe();
    	publication_record[13] = record.getCdrom();
    	publication_record[14] = record.getPublisher();
    	publication_record[15] = record.getNote();
    	publication_record[16] = record.getCrossref();
    	publication_record[17] = record.getIsbn();
    	publication_record[18] = record.getSeries();
    	publication_record[19] = record.getSchool();
    	publication_record[20] = record.getChapter();
    	
    	
    	try {
			publication_csv_out.writeRecord(publication_record);
			
			// process authors
			String[] author_record = new String[4];

			for (int i=0; i < record.getAuthors().size(); i++){
				
				String author = record.getAuthors().get(i);
				
				author_record[0] = record.getDkey();
				author_record[1] = author;
				
				String authorName = author;
				int index = author.lastIndexOf(" ");
				
				if (index > 0){
					String lastTerm = author.substring(index + 1, author.length());
					
					if (Character.isDigit(lastTerm.charAt(0)))
						authorName = author.substring(0, index);
				}

				author_record[2] = authorName;
				
				author_record[3] = String.valueOf(i + 1);
				
				author_csv_out.writeRecord(author_record);
				
			}
			
			// process editors
			String[] editor_record = new String[4];

			for (int i=0; i < record.getEditors().size(); i++){
				
				String editor = record.getEditors().get(i);
				
				editor_record[0] = record.getDkey();
				editor_record[1] = editor;
				
				String editorName = editor;
				int index = editor.lastIndexOf(" ");
				
				if (index > 0){
					String lastTerm = editor.substring(index + 1, editor.length());
					
					if (Character.isDigit(lastTerm.charAt(0)))
						editorName = editor.substring(0, index);
				}

				editor_record[2] = editorName;
				
				editor_record[3] = String.valueOf(i + 1);
				
				editor_csv_out.writeRecord(editor_record);
				
			}
			
			// process citations
			String[] cite_record = new String[2];
			
			for (int i=0; i < record.getCites().size(); i++){
				
				cite_record[0] = record.getDkey();
				cite_record[1] = record.getCites().get(i);
				
				if (cite_record[1].startsWith("."))
					continue;
				
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
    
    /*
     * This class handles the dblp element
     */
    private class DBLPHandler extends DefaultHandler{
        
        DBLP record = null;
        ArrayList<String> authors; 	//= new ArrayList<String>();
        ArrayList<String> cites; 	// = new ArrayList<String>();
        ArrayList<String> editors; 	// = new ArrayList<String>();
        StringBuffer elementValue = new StringBuffer();
        boolean inRecord = false;
        
        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        @Override
        public void startElement(String uri, String localName, String name,
                Attributes attributes) throws SAXException {
            if ("dblp".equals(name)) {
                // Do nothing this is the root element
                
            }else if (elements.contains(name)) {
                // We are going to process a new record
                initializeData();
                record.setType(name);
                record.setDkey(attributes.getValue("", "key"));
                inRecord = true;
                
            }else if (singleFields.contains(name) || multiFields.contains(name)){
            	
             	elementValue = new StringBuffer();
            }
            
        } //- startElement

        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
         */
        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (inRecord) {
                elementValue.append(new String(ch, start, length));
            }
        } //- characters

        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public void endElement(String uri, String localName, String name)
                throws SAXException {
            
            if ("dblp".equals(name)) {
                // Do nothing this is the root element
            	
                
            }else if (inRecord && elements.contains(name)) {
                // We finished the record
                processRecord();
                inRecord = false;
                
            }else if (inRecord && singleFields.contains(name)) {
                    processSingleField(name);
                    
            }else if (inRecord && multiFields.contains(name)) {
                    processMultiField(name);
            }
            
        } //- endElement
        
        private void initializeData() {
            record = new DBLP();
            authors = new ArrayList<String>();
            cites = new ArrayList<String>();
            editors = new ArrayList<String>();
        } //- initializeData
        
        /*
         * Adds the field value to the record
         */
        private void processSingleField(String field) {
            String value;
            try {
                byte[] utf8 = elementValue.toString().getBytes("UTF-8");
                value = new String(utf8, "UTF-8");
            }catch (UnsupportedEncodingException e) {
                value = elementValue.toString();
            }
            if (field.equals("title")) {
                /*
                 * DBLP adds a "." and the end of some titles. Since we do an
                 * exact title match in order to build the links and our titles
                 * don't have that dot, I'm deleting it.
                 * it.
                 */
                int lastDotIndex = value.lastIndexOf('.');
                if (lastDotIndex != -1 && lastDotIndex == value.length()-1) {
                    value = value.substring(0, lastDotIndex);
                }
                record.setTitle(value);
            }
            else if (field.equals("booktitle")) {record.setBookTitle(value);}
            else if (field.equals("pages")) {record.setPages(value);}
            else if (field.equals("address")) {record.setAddress(value);}
            else if (field.equals("journal")) {record.setJournal(value);}
            else if (field.equals("month")) {record.setMonth(value);}
            else if (field.equals("url")) {record.setUrl(value);}
            else if (field.equals("ee")) {record.setEe(value);}
            else if (field.equals("cdrom")) {record.setCdrom(value);}
            else if (field.equals("publisher")) {record.setPublisher(value);}
            else if (field.equals("note")) {record.setNote(value);}
            else if (field.equals("crossref")) {record.setCrossref(value);}
            else if (field.equals("isbn")) {record.setIsbn(value);}
            else if (field.equals("series")) {record.setSeries(value);}
            else if (field.equals("school")) {record.setSchool(value);}
            else if (field.equals("chapter")) {record.setChapter(value);}
            else if (field.equals("year")) {record.setYear(value);}
            else if (field.equals("volume")) {record.setVolume(value);}
            else if (field.equals("number")) {record.setNumber(value);}
            
            
            /*
            else if (field.equals("year") || field.equals("volume") ||
                    field.equals("number")) {
                try {
                    int numValue = Integer.parseInt(value);
                    if (field.equals("year")) {record.setYear(numValue);}
                    else if (field.equals("volume")) {
                        record.setVolume(numValue);
                    }
                    else if (field.equals("number")) {
                        record.setNumber(numValue);
                    }
                }catch (NumberFormatException e) {
                    // Nothing the field is not set
                }
            }
            */
            
            
            
        } //- processSingleField
        
        /*
         * Add the value to the adequate 
         */
        private void processMultiField(String field) {
            String value;
            try {
                byte[] utf8 = elementValue.toString().getBytes("UTF-8");
                value = new String(utf8, "UTF-8");
            }catch (UnsupportedEncodingException e) {
                value = elementValue.toString();
            }

            if (field.equals("author")) {
            	authors.add(value);

            }else if (field.equals("cite")) {
            	cites.add(value);

            }else if (field.equals("editor")) {
            	editors.add(value);
            }
        } //- processMultiField
        
        private void processRecord() {
            record.setAuthors(authors);
            record.setCites(cites);
            record.setEditors(editors);
            
            // Send the record to the database
            sendRecord(record);
        } //- processRecord
    } //- class DBLPDocHandler

    public class DBLPEntityResolver implements EntityResolver {

        private String dtdLocation;
        
        /* (non-Javadoc)
         * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
         */
        public DBLPEntityResolver(String dtdLocation) {
            this.dtdLocation = dtdLocation;
        }

        public InputSource resolveEntity(String publicId, String systemId)
                throws SAXException, IOException {
            if (dtdLocation != null && dtdLocation.length() > 0) {
                return new InputSource(new StringReader(dtdLocation));
            }else{
                return null;
            }
        }
        
    } //- class DBLPEntityResolver
    
    
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		DBLPMetadataLoader loader = new DBLPMetadataLoader();
		loader.setDBLPDTDFile("dblp.dtd");
		loader.setDBLPDataFile(loader.data_dir + "data/dblp.xml");
		loader.updateDBLP();
		
	}


} //- class DBLPMetadataUpdater
