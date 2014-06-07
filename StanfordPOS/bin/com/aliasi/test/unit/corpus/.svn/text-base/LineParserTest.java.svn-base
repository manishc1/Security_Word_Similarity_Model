package com.aliasi.test.unit.corpus;

import com.aliasi.corpus.ObjectHandler;
import com.aliasi.corpus.LineParser;

import com.aliasi.util.Files;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;


import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xml.sax.InputSource;

public class LineParserTest  {

    static final String[] DOCS = new String[] {
        "",
        "\n",
        "\n\n",
        "abc",
        "abc\n",
        "abc\rdef",
        "\nabc\rdef\r\n\n",
    };

    static final String[][] EXPECTED_LINES = new String[][] {
        { },
        { "" },
        { "", "" },
        { "abc" },
        { "abc" },
        { "abc", "def" },
        { "", "abc", "def", "" }
    };

    @Test
    public void test1() throws IOException {
        IdParser parser = new IdParser();
        Collector<String> collector = new Collector<String>();
        parser.setHandler(collector);

        for (int i = 0; i < DOCS.length; ++i) {
            String input = DOCS[i];
            char[] cs = input.toCharArray();
            List<String> lineList = Arrays.<String>asList(EXPECTED_LINES[i]);

            collector.clear();
            CharArrayReader reader = new CharArrayReader(cs);
            InputSource in = new InputSource(reader);
            parser.parse(in);
            assertEquals(lineList, collector.result());

            collector.clear();
            String s = new String(cs);
            byte[] bytes = s.getBytes("UTF-16");
            ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
            in = new InputSource(bytesIn);
            in.setEncoding("UTF-16");
            parser.parse(in);
            assertEquals(lineList, collector.result());

            collector.clear();
            File tempFile = null;
            try {
                tempFile = File.createTempFile("lingpipe-LineParserTestData","txt");
                Files.writeBytesToFile(bytes,tempFile);
                in = new InputSource(tempFile.toURI().toString());
                in.setEncoding("UTF-16");
                parser.parse(in);
                assertEquals(lineList, collector.result());
            } finally {
                if (tempFile != null)
                    tempFile.delete();
            }

            collector.clear();
            parser.parseString(cs,0,cs.length);
            assertEquals(lineList, collector.result());
        }
    }

    public class IdParser extends LineParser<ObjectHandler<String>> {
        @Override
        public void parseLine(String line, int number) {
            getHandler().handle(line);
        }
    }


    public class Collector<E> implements ObjectHandler<E> {
        List<E> mCollector = new ArrayList<E>();
        public void handle(E e) {
            mCollector.add(e);
        }
        public List<E> result() {
            return mCollector;
        }
        public void clear() {
            mCollector.clear();
        }
    }

}
