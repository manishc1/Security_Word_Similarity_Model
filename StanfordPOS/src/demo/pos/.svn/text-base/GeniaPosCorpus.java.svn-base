package demo.pos;
import com.aliasi.corpus.Parser;
import com.aliasi.corpus.TagHandler;

import com.aliasi.corpus.parsers.GeniaPosParser;

import com.aliasi.util.Iterators;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Iterator;

import org.xml.sax.InputSource;

public class GeniaPosCorpus implements PosCorpus {

    private final File mGeniaGZipFile;

    public GeniaPosCorpus(File geniaZipFile) {
        mGeniaGZipFile = geniaZipFile;
    }

    public Iterator<InputSource> sourceIterator() throws IOException {
        FileInputStream fileIn = new FileInputStream(mGeniaGZipFile);
        InputSource in = new InputSource(fileIn);
        return Iterators.singleton(in);
    }

    public Parser<TagHandler> parser() {
        return new GeniaPosParser();
    }

}

