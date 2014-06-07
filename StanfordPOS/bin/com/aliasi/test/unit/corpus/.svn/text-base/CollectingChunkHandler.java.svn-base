package com.aliasi.test.unit.corpus;

import com.aliasi.chunk.Chunking;

import com.aliasi.corpus.ChunkHandler;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class CollectingChunkHandler
    implements ChunkHandler {

    List mChunkingList = new ArrayList();

    public void handle(Chunking chunking) {
        mChunkingList.add(chunking);
    }

    public void assertEquals(List chunkingList) {
        junit.framework.Assert.assertEquals(chunkingList,mChunkingList);
    }

    public void assertEquals(Chunking[] chunkings) {
        junit.framework.Assert.assertEquals(Arrays.asList(chunkings),mChunkingList);
    }

}