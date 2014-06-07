package demo.sentence;
import com.aliasi.medline.MedlineCitationSet;

import com.aliasi.sentences.MedlineSentenceModel;
import com.aliasi.sentences.SentenceAnnotateFilter;
import com.aliasi.sentences.SentenceModel;

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;

import com.aliasi.util.Files;
import com.aliasi.util.Strings;

import com.aliasi.xml.GroupCharactersFilter;
import com.aliasi.xml.SAXWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/** Use SentenceModel to add sentence boundary annotations to selected XML elements */
public class SAXSentenceFilterDemo {

    public static void main(String[] args) throws SAXException, IOException {
	TokenizerFactory tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;
	SentenceModel sentenceModel  = new MedlineSentenceModel();
	String[] elts = new String[] {
	    MedlineCitationSet.ARTICLE_TITLE_ELT,
	    MedlineCitationSet.ABSTRACT_TEXT_ELT };
	SentenceAnnotateFilter sentenceFilter = 
	    new SentenceAnnotateFilter(sentenceModel,tokenizerFactory,elts);

	FileOutputStream fileOut = new FileOutputStream("SentenceFilterDemoOutput.xml");
	SAXWriter writer = new SAXWriter(fileOut,Strings.UTF8);
	sentenceFilter.setHandler(writer);
	GroupCharactersFilter handler = new GroupCharactersFilter(sentenceFilter);

	XMLReader xmlReader = XMLReaderFactory.createXMLReader();
	xmlReader.setContentHandler(handler);
	xmlReader.setDTDHandler(handler);
	xmlReader.setErrorHandler(handler);
	xmlReader.setEntityResolver(handler);

	File file = new File(args[0]);
        String url = file.toURI().toURL().toString();
        InputSource inSource = new InputSource(url);
	xmlReader.parse(inSource);
    }
}


