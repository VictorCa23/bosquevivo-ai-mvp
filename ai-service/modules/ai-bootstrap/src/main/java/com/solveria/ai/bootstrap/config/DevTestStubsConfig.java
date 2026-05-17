package com.solveria.ai.bootstrap.config;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

/**
 * Stubs configuration for DEV and TEST profiles. Provides fallback beans to prevent context
 * failures when external dependencies are missing.
 */
@Configuration
public class DevTestStubsConfig {

    /**
     * Stub EmbeddingModel for dev and test profiles. Returns zero vectors to prevent PgVectorStore
     * autoconfig from failing when no real embedding provider exists. Compatible with Spring AI
     * 1.1.2: methods return float[] / List<float[]>.
     */
    @Bean
    @Profile({"dev", "test"})
    @ConditionalOnMissingBean(EmbeddingModel.class)
    public EmbeddingModel stubEmbeddingModel(Environment env) {
        int dimension = env.getProperty("ai.embedding.dim", Integer.class, 1536);

        return new EmbeddingModel() {
            @Override
            public EmbeddingResponse call(EmbeddingRequest request) {
                List<Embedding> embeddings = new java.util.ArrayList<>();
                int index = 0;
                for (String text : request.getInstructions()) {
                    float[] vector = createZeroVector(dimension);
                    embeddings.add(new Embedding(vector, index));
                    index++;
                }
                return new EmbeddingResponse(embeddings);
            }

            @Override
            public float[] embed(Document document) {
                return createZeroVector(dimension);
            }

            @Override
            public float[] embed(String text) {
                return createZeroVector(dimension);
            }

            @Override
            public List<float[]> embed(List<String> texts) {
                return texts.stream()
                        .map(text -> createZeroVector(dimension))
                        .collect(Collectors.toList());
            }

            private float[] createZeroVector(int dim) {
                float[] vector = new float[dim];
                // Array is already initialized to 0.0f by default
                return vector;
            }
        };
    }
}
