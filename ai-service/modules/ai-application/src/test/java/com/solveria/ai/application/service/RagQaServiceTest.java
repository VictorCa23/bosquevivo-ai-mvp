package com.solveria.ai.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.solveria.ai.application.dto.ChatResultDto;
import com.solveria.ai.application.dto.RagChunkDto;
import com.solveria.ai.application.dto.RagQaCommandDto;
import com.solveria.ai.application.dto.RagQaResultDto;
import com.solveria.ai.application.port.out.AuditPort;
import com.solveria.ai.application.port.out.LlmChatPort;
import com.solveria.ai.application.port.out.TenantContextPort;
import com.solveria.ai.application.port.out.VectorStorePort;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RagQaServiceTest {

    @Mock private TenantContextPort tenantContext;
    @Mock private VectorStorePort vectorStore;
    @Mock private LlmChatPort llmChat;
    @Mock private AuditPort audit;

    private RagQaService service;

    @BeforeEach
    void setUp() {
        service = new RagQaService(tenantContext, vectorStore, llmChat, audit);
    }

    @Test
    void ask_flowsThroughPortsAndReturnsResult() {
        when(tenantContext.currentTenantId()).thenReturn("t1");
        when(vectorStore.similaritySearch(anyString(), eq(5), eq("t1"), eq("ns1")))
                .thenReturn(List.of(new RagChunkDto("ctx")));
        when(llmChat.chat(anyString())).thenReturn(new ChatResultDto("answer", 10, 20));

        RagQaResultDto result = service.ask(new RagQaCommandDto("q?", "ns1"));

        assertEquals("answer", result.answer());
        assertEquals(10, result.promptTokens());
        assertEquals(20, result.completionTokens());
        verify(tenantContext).currentTenantId();
        verify(vectorStore).similaritySearch("q?", 5, "t1", "ns1");
        verify(llmChat).chat(anyString());
        verify(audit)
                .audit(
                        eq("rag.qa"),
                        argThat(
                                (Map<String, Object> m) ->
                                        "t1".equals(m.get("tenantId"))
                                                && "ns1".equals(m.get("namespace"))));
    }
}
