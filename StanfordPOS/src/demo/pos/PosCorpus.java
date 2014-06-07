package demo.pos;
import com.aliasi.corpus.Parser;
import com.aliasi.corpus.TagHandler;

import java.io.IOException;

import java.util.Iterator;

import org.xml.sax.InputSource;


public interface PosCorpus {

    public Parser<TagHandler> parser();

    public Iterator<InputSource> sourceIterator() throws IOException;

}
