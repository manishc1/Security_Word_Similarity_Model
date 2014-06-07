package edu.umbc.dblp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;

import com.csvreader.CsvReader;

import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.umbc.dbpedia.util.LexicalProcess;

public class SubjectExtractor {
	
	HashSet<String> keywordSet;
	MaxentTagger tagger;
	Morphology morpha;
	
	public static final HashMap<String, String> morpha_corrections = new HashMap<String, String>();
    static {
    	morpha_corrections.put("papers_NN", "paper");
    	morpha_corrections.put("data_NN", "data");
    }


	public SubjectExtractor() throws Exception {
		// TODO Auto-generated constructor stub
		morpha = new Morphology();

		String modelLocation = "/home/lushan1/nlp/model/stanford/pos-tagger/wsj-left3words/wsj-0-18-left3words-distsim.tagger";
        System.out.println("Reading model from file=" + modelLocation);
        tagger = new MaxentTagger(modelLocation);
        System.out.println();
        
        keywordSet = new HashSet<String>();

		
	}
	
	public String lemma(String word, String tag){
		
		if (LexicalProcess.isAcronym(word))
			return word;
		
		String correction = morpha_corrections.get(word.toLowerCase() + "_" + tag);
		
		if (correction != null) return correction;
		
		return morpha.lemma(word, tag);
		
	}
	
	
	public void loadKeywordSet(String inputFile) throws IOException{
		
		BufferedReader textReader = new BufferedReader(new FileReader(inputFile));
		
		String line;
		
		while ((line = textReader.readLine()) != null){
			
			StringTokenizer st = new StringTokenizer(line, " ");
			StringBuffer key = new StringBuffer();
			
			while (st.hasMoreElements()){
				
				String word = st.nextToken();
				
				if (LexicalProcess.isAcronym(word))
					key.append(word + " ");
				else
					key.append(word.toLowerCase() + " ");
			}
			
			keywordSet.add(key.toString().trim());
			
		}
		
		textReader.close();
		
	}


	public HashSet<String> getSubjectsFromText(String text) throws Exception{
		
		HashSet<String>[] subjects = new HashSet[4];
		
		subjects[0] = new HashSet<String>();
		subjects[1] = new HashSet<String>();
		subjects[2] = new HashSet<String>();
		subjects[3] = new HashSet<String>();
		

		String taggedText = tagger.tagString(text).replace('/', '_').trim();
		
		//StringTokenizer st = new StringTokenizer(text, " .,*()<>!_?:;'\"");
		StringTokenizer st = new StringTokenizer(taggedText, " ");
		
		int position = 0;
		
		Vector<String> backlook = new Vector<String>();
		
		while (st.hasMoreElements()){
			
			String taggedWord = st.nextToken();
			position ++;
			
			int index = taggedWord.lastIndexOf('_');
			if (index <= 0){
				System.out.println(taggedWord);
				System.out.println(text);
				System.out.println(taggedText);
				continue;
			}
			String word = taggedWord.substring(0, index);
			String posTag = taggedWord.substring(index + 1, taggedWord.length());

			
			if (!LexicalProcess.isAcronym(word)){
				word = word.toLowerCase();
			}
			
			if (backlook.size() < 4)
				backlook.add(0, word);
			else{
				backlook.add(0, word);
				backlook.remove(4);
			}
			
			if (LexicalProcess.isAcronym(word)){
				
				if (keywordSet.contains(word))
					subjects[0].add(word);
				
				continue;
			}

			if (!posTag.startsWith("NN"))
				continue;
			
			String head = word;
			String head_lemma = lemma(head, "NN");
			
			boolean found = false;

			for (int i = backlook.size() - 1; i >= 0; i--){
				
				StringBuffer prefix = new StringBuffer();
				
				for (int j = i; j > 0; j--){
					prefix.append(backlook.elementAt(j) + " ");
				}
				
				String candidate = prefix.toString() + head_lemma;
				
				if (keywordSet.contains(candidate)){
					subjects[i].add(candidate);
					found = true;
				}else{
					candidate = prefix.toString() + head;
					
					if (keywordSet.contains(candidate)){
						subjects[i].add(candidate);
						found = true;
					}
				}
				
				if (found) break;
			}
			
		}
		
		
		ArrayList<String> result = new ArrayList<String>();
		
		result.addAll(subjects[3]);
		
		for (int k = 2 ; k >=0; k--){
			
			for (String subject : subjects[k]){
				
				boolean isSuffixOrPrefix = false;
				
				for (int i = 0; i < result.size(); i++){
					
					if (result.get(i).endsWith(subject)){
						isSuffixOrPrefix = true;
						break;
					}
					
					if (result.get(i).startsWith(subject)){
						isSuffixOrPrefix = true;
						break;
					}

				}
				
				if (!isSuffixOrPrefix){
					result.add(subject);
				}
			}
		}

		return new HashSet<String>(result);
		
		//return result.toArray(new String[result.size()]);
	}
	
	
	public void getSubjectsFromTitleAndAbstract(String inputFile, String outputFile) throws Exception  {
		
		
		BufferedReader textReader = new BufferedReader(new FileReader(inputFile));
		
		PrintWriter out = new PrintWriter(new FileWriter(outputFile));
		
		String line;
		
		int lineNo = 0;
		
		while ((line = textReader.readLine()) != null){
			
			line = line.replace("\\\"", "");
			lineNo ++;
			
			CsvReader reader = CsvReader.parse(line);
			
			if (reader.readRecord()){
				
				int columnCount = reader.getColumnCount();
				
				if (columnCount != 3){
					System.out.println(line);
					continue;
				}
				
				String dblp_key = reader.get(0);
				String paperTitle = reader.get(1);
				String paperAbstract = reader.get(2);
				
				HashSet<String> subjectsFromTitle = getSubjectsFromText(paperTitle);
				HashSet<String> subjects = getSubjectsFromText(paperAbstract);
				
				subjects.addAll(subjectsFromTitle);
				
				for (String subject: subjects){
					
					out.println("\"" + dblp_key + "\"" + "\t" + "\"" + subject + "\""); 
					
				}
				
			}
			
			if (lineNo % 1000 == 0)
				System.out.println(lineNo);
			
		}
		
		out.close();

	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		SubjectExtractor extractor = new SubjectExtractor();
		
		/*
		extractor.keywordSet.add("semantic web");
		extractor.keywordSet.add("ontologies");
		extractor.keywordSet.add("SPARQL queries");
		extractor.keywordSet.add("web");
		*/
		
		extractor.loadKeywordSet("/home/lushan1/dblp/data/subject.csv");
		extractor.getSubjectsFromTitleAndAbstract("/home/lushan1/dblp/data/title_abstract.csv", "/home/lushan1/dblp/data/subject_results.csv");
		
		
		
		/*
    	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    	while(true){
        	System.out.println("Please input text");
        	String text = input.readLine();
		
			HashSet<String> subjects = extractor.getSubjectsFromText(text);
	
			for (String subject : subjects){
				System.out.println(subject);
			}
			
    	}
		*/
	}

}
