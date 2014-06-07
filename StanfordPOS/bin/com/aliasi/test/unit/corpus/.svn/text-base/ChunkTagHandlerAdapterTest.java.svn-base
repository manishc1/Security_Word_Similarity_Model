package com.aliasi.test.unit.corpus;

import com.aliasi.chunk.ChunkFactory;
import com.aliasi.chunk.Chunking;
import com.aliasi.chunk.ChunkingImpl;

import com.aliasi.corpus.ChunkTagHandlerAdapter;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;
import static com.aliasi.test.unit.Asserts.assertFullEquals;


import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;

public class ChunkTagHandlerAdapterTest  {

    static TokenizerFactory TOK_FACTORY = new IndoEuropeanTokenizerFactory();

    @Test
    public void testPredicates() {
    assertTrue(ChunkTagHandlerAdapter.isBeginTag("B-FOO"));
    assertTrue(ChunkTagHandlerAdapter
           .isBeginTag(ChunkTagHandlerAdapter.BEGIN_TAG_PREFIX
                   + "FOO"));
    assertFalse(ChunkTagHandlerAdapter.isBeginTag("I-FOO"));
    assertFalse(ChunkTagHandlerAdapter.isBeginTag("a"));

    assertTrue(ChunkTagHandlerAdapter.isInTag("I-BAR"));
    assertTrue(ChunkTagHandlerAdapter
           .isInTag(ChunkTagHandlerAdapter.IN_TAG_PREFIX+"BAR"));
    assertFalse(ChunkTagHandlerAdapter.isInTag("B-BAR"));
    assertFalse(ChunkTagHandlerAdapter.isInTag("b"));

    assertTrue(ChunkTagHandlerAdapter.isOutTag("O"));
    assertTrue(ChunkTagHandlerAdapter
           .isOutTag(ChunkTagHandlerAdapter.OUT_TAG));
    assertFalse(ChunkTagHandlerAdapter.isOutTag("I-FOO"));
    assertFalse(ChunkTagHandlerAdapter.isOutTag("c"));
    }

    @Test
    public void testToChunkingBIO() {
    String[] tokens = new String[0];
    String[] whitespaces = new String[] { " " };
    String[] tags = new String[0];
    Chunking chunkingExpected = new ChunkingImpl(" ");
    assertFullEquals(chunkingExpected,
             ChunkTagHandlerAdapter
             .toChunkingBIO(tokens,whitespaces,tags));

    String[] tokens2 = new String[] { "John", "J", ".", "Smith",
                     "lives", "in", "Washington", "." };
    String[] whitespaces2 = new String[] { "", " ", "", " ", " ",
                          " ", " ", "", "" };
    String[] tags2 = { "B-PER", "I-PER", "I-PER", "I-PER",
              "O", "O", "B-LOC", "O" };
    ChunkingImpl chunkingExpected2 
        = new ChunkingImpl("John J. Smith lives in Washington.");
    chunkingExpected2.add(ChunkFactory.createChunk(0,13,"PER"));
    chunkingExpected2.add(ChunkFactory.createChunk(23,33,"LOC"));
    assertFullEquals(chunkingExpected2,
             ChunkTagHandlerAdapter
             .toChunkingBIO(tokens2,whitespaces2,tags2));
    }

    @Test
    public void testAdapter() {
    CollectingChunkHandler chunkCollector = new CollectingChunkHandler();
    ChunkTagHandlerAdapter adapter 
        = new ChunkTagHandlerAdapter(chunkCollector);
    

    String[] tokens2 = new String[] { "John", "J", ".", "Smith",
                     "lives", "in", "Washington", "." };
    String[] whitespaces2 = new String[] { "", " ", "", " ", " ",
                          " ", " ", "", "" };
    String[] tags2 = { "B-PER", "I-PER", "I-PER", "I-PER",
              "O", "O", "B-LOC", "O" };
    ChunkingImpl chunkingExpected2 
        = new ChunkingImpl("John J. Smith lives in Washington.");
    chunkingExpected2.add(ChunkFactory.createChunk(0,13,"PER"));
    chunkingExpected2.add(ChunkFactory.createChunk(23,33,"LOC"));

    adapter.handle(tokens2,whitespaces2,tags2);
    chunkCollector.assertEquals(new Chunking[] { chunkingExpected2 });
    }
}
