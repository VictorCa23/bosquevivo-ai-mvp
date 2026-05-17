package com.solveria.ai.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.solveria.ai.application.dto.RagQaResultDto;
import com.solveria.ai.application.port.in.RagQaUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class RagQaControllerTest {

    private MockMvc mvc;

    @Mock private RagQaUseCase ragQaUseCase;

    @BeforeEach
    void setUp() {
        var controller = new RagQaController(ragQaUseCase);
        mvc =
                MockMvcBuilders.standaloneSetup(controller)
                        .setMessageConverters(new MappingJackson2HttpMessageConverter())
                        .build();
    }

    @Test
    void qa_returnsOk() throws Exception {
        when(ragQaUseCase.ask(any())).thenReturn(new RagQaResultDto("answer", 10, 20));

        mvc.perform(
                        post("/api/v1/ai/rag/qa")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"question\":\"q?\",\"namespace\":\"ns1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("answer"))
                .andExpect(jsonPath("$.promptTokens").value(10))
                .andExpect(jsonPath("$.completionTokens").value(20));
    }
}
