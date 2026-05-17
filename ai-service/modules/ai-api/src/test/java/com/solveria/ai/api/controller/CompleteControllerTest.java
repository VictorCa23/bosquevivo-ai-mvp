package com.solveria.ai.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.solveria.ai.application.port.in.CompletePromptUseCase;
import com.solveria.ai.domain.model.Completion;
import com.solveria.ai.domain.model.Prompt;
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
class CompleteControllerTest {

    private MockMvc mvc;

    @Mock private CompletePromptUseCase completeUseCase;

    @BeforeEach
    void setUp() {
        var controller = new CompleteController(completeUseCase);
        mvc =
                MockMvcBuilders.standaloneSetup(controller)
                        .setMessageConverters(new MappingJackson2HttpMessageConverter())
                        .build();
    }

    @Test
    void complete_returnsOk() throws Exception {
        when(completeUseCase.complete(any(Prompt.class)))
                .thenReturn(new Completion("hi", "stub", 1));

        mvc.perform(
                        post("/api/v1/ai/complete")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"prompt\":\"hello\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("hi"));
    }
}
