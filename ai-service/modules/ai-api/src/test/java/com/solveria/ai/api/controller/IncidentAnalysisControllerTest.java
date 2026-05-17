package com.solveria.ai.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.solveria.ai.application.dto.IncidentAnalysisResultDto;
import com.solveria.ai.application.port.in.AnalyzeIncidentUseCase;
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
class IncidentAnalysisControllerTest {

    private MockMvc mvc;

    @Mock private AnalyzeIncidentUseCase useCase;

    @BeforeEach
    void setUp() {
        var controller = new IncidentAnalysisController(useCase);
        mvc =
                MockMvcBuilders.standaloneSetup(controller)
                        .setMessageConverters(new MappingJackson2HttpMessageConverter())
                        .build();
    }

    @Test
    void analyzeReturnsIncidentRiskRecommendation() throws Exception {
        when(useCase.analyze(any()))
                .thenReturn(
                        new IncidentAnalysisResultDto(
                                100,
                                2,
                                "Analisis AI MVP 0.7.1",
                                "Asignar brigada inmediatamente.",
                                "bosquevivo-rules-mvp-0.7.1"));

        mvc.perform(
                        post("/api/v1/incidents/analyze")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "title":"Fuego en reserva",
                                          "description":"Fuego visible",
                                          "type":"FIRE",
                                          "severity":"CRITICAL",
                                          "latitude":-17.78,
                                          "longitude":-63.18
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priorityScore").value(100))
                .andExpect(jsonPath("$.slaHours").value(2))
                .andExpect(jsonPath("$.model").value("bosquevivo-rules-mvp-0.7.1"));
    }

    @Test
    void analyzeRejectsInvalidCoordinates() throws Exception {
        mvc.perform(
                        post("/api/v1/incidents/analyze")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "title":"Coordenada invalida",
                                          "description":"Fuera de rango",
                                          "type":"OTHER",
                                          "severity":"LOW",
                                          "latitude":-100,
                                          "longitude":-63.18
                                        }
                                        """))
                .andExpect(status().isBadRequest());
    }
}
