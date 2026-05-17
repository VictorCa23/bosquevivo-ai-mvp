package com.solveria.ai.application.port.out;

import com.solveria.ai.application.dto.RagChunkDto;
import java.util.List;

/** Port out: vector store (similarity search with mandatory tenantId + namespace filter). */
public interface VectorStorePort {

    /**
     * Similarity search for RAG. Filter by tenantId and namespace is mandatory.
     *
     * @param query user question (embedded internally by adapter)
     * @param topK max results
     * @param tenantId tenant filter
     * @param namespace namespace filter
     * @return retrieved chunks (content)
     */
    List<RagChunkDto> similaritySearch(String query, int topK, String tenantId, String namespace);
}
