package com.solveria.ai.infrastructure.vector.mongodb_atlas;

import com.solveria.ai.application.dto.RagChunkDto;
import com.solveria.ai.application.port.out.VectorStorePort;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Vector store adapter via MongoDB Atlas Vector Search. Stub for future implementation. */
@Component
@Profile("atlas-vector")
public class MongoAtlasVectorStoreAdapter implements VectorStorePort {

    @Override
    public List<RagChunkDto> similaritySearch(
            String query, int topK, String tenantId, String namespace) {
        return List.of();
    }
}
