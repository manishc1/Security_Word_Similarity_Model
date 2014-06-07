package com.aliasi.test.unit.tokenizer;

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.LengthStopFilterTokenizer;
import com.aliasi.tokenizer.Tokenizer;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;


public class LengthStopFilterTokenizerTest  {

    @Test
    public void testFilter() {
        testTokenizer(0,"foo bar",new String[] { });
        testTokenizer(3,"foo bar",new String[] { "foo", "bar" });
        testTokenizer(2,"foo ba", new String[] { "ba" });
        testTokenizer(2,"ba foo", new String[] { "ba" });
        testTokenizer(2,"a bc def a gh", new String[] { "a", "bc", "a", "gh" });
    }

    void testTokenizer(int max, String in, String[] expectedTokens) {
        Tokenizer base = IndoEuropeanTokenizerFactory.FACTORY.tokenizer(in.toCharArray(),0,in.length());
        Tokenizer filtered = new LengthStopFilterTokenizer(base,max);
        String[] tokens = filtered.tokenize();
        assertArrayEquals(expectedTokens,tokens);
    }

}
