package com.solveria.ai.infrastructure.vector.pgvector;

import com.solveria.ai.application.dto.RagChunkDto;
import com.solveria.ai.application.port.out.VectorStorePort;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/** Vector store adapter via Spring AI PgVector. Mandatory metadata filter: tenantId + namespace. */
@Component
@ConditionalOnBean(VectorStore.class)
public class PgVectorStoreAdapter implements VectorStorePort {

    private final VectorStore vectorStore;

    public PgVectorStoreAdapter(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Escapes single quotes and backslashes for filter expression values to avoid errors and
     * injection. Keeps mandatory tenantId + namespace filter safe.
     */
    private static String escapeFilterValue(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("'", "''");
    }

    @Override
    public List<RagChunkDto> similaritySearch(
            String query, int topK, String tenantId, String namespace) {
        String filterExpr =
                "tenantId == '"
                        + escapeFilterValue(tenantId)
                        + "' && namespace == '"
                        + escapeFilterValue(namespace)
                        + "'";
        SearchRequest request =
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .similarityThresholdAll()
                        .filterExpression(filterExpr)
                        .build();
        List<Document> docs = vectorStore.similaritySearch(request);
        return docs.stream().map(d -> new RagChunkDto(d.getText())).collect(Collectors.toList());
    }
}
