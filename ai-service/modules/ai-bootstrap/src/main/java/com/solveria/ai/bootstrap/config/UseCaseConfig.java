package com.solveria.ai.bootstrap.config;

import com.solveria.ai.application.port.in.AnalyzeIncidentUseCase;
import com.solveria.ai.application.port.in.CompletePromptUseCase;
import com.solveria.ai.application.port.in.RagQaUseCase;
import com.solveria.ai.application.port.out.AuditPort;
import com.solveria.ai.application.port.out.LlmChatPort;
import com.solveria.ai.application.port.out.LlmPort;
import com.solveria.ai.application.port.out.TenantContextPort;
import com.solveria.ai.application.port.out.VectorStorePort;
import com.solveria.ai.application.service.AnalyzeIncidentService;
import com.solveria.ai.application.service.CompletePromptService;
import com.solveria.ai.application.service.RagQaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CompletePromptUseCase completePromptUseCase(LlmPort llmPort) {
        return new CompletePromptService(llmPort);
    }

    @Bean
    public AnalyzeIncidentUseCase analyzeIncidentUseCase() {
        return new AnalyzeIncidentService();
    }

    @Bean
    public RagQaUseCase ragQaUseCase(
            TenantContextPort tenantContext,
            VectorStorePort vectorStore,
            LlmChatPort llmChat,
            AuditPort audit) {
        return new RagQaService(tenantContext, vectorStore, llmChat, audit);
    }
}
