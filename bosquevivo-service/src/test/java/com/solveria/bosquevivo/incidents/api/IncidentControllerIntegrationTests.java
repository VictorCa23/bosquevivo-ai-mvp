package com.solveria.bosquevivo.incidents.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solveria.bosquevivo.auth.application.CurrentUser;
import com.solveria.bosquevivo.auth.security.JwtService;
import com.solveria.bosquevivo.incidents.domain.IncidentSeverity;
import com.solveria.bosquevivo.incidents.domain.IncidentType;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class IncidentControllerIntegrationTests {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private JwtService jwtService;

    private String adminToken;
    private String citizenToken;

    @BeforeEach
    void createIamCompatibleTokens() {
        adminToken = jwtService.createToken(new CurrentUser("admin", "Administrador", "ADMIN"));
        citizenToken =
                jwtService.createToken(new CurrentUser("ciudadano", "Ciudadano", "CITIZEN"));
    }

    @Test
    void managesOperationalIncidentLifecycle() throws Exception {
        UUID id = createIncident("Humo en la reserva", IncidentType.SMOKE, IncidentSeverity.HIGH);
        UUID brigadeId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        mockMvc.perform(
                        get("/api/incidents/{id}", id)
                                .header("Authorization", bearer(citizenToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Humo en la reserva"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.severity").value("HIGH"));

        mockMvc.perform(
                        post("/api/incidents/{id}/prioritize", id)
                                .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PRIORITIZED"))
                .andExpect(jsonPath("$.priorityScore").value(75));

        mockMvc.perform(
                        post("/api/incidents/{id}/assign", id)
                                .header("Authorization", bearer(adminToken))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(Map.of("brigadeId", brigadeId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ASSIGNED"))
                .andExpect(jsonPath("$.assignedBrigadeId").value(brigadeId.toString()));

        mockMvc.perform(
                        post("/api/incidents/{id}/start-attention", id)
                                .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_ATTENTION"));

        mockMvc.perform(
                        post("/api/incidents/{id}/close", id)
                                .header("Authorization", bearer(adminToken))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        json(
                                                Map.of(
                                                        "areaSecured",
                                                        true,
                                                        "riskControlled",
                                                        true,
                                                        "notes",
                                                        "Zona verificada."))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"))
                .andExpect(jsonPath("$.closureNotes").value("Zona verificada."));

        mockMvc.perform(
                        get("/api/incidents/{id}/events", id)
                                .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("INCIDENT_CLOSED"));
    }

    @Test
    void filtersIncidentsByStatusTypeSeverityAndSearch() throws Exception {
        createIncident("Tala cerca del rio", IncidentType.ILLEGAL_LOGGING, IncidentSeverity.MEDIUM);
        UUID smokeId =
                createIncident("Humo sector norte", IncidentType.SMOKE, IncidentSeverity.CRITICAL);

        mockMvc.perform(
                        post("/api/incidents/{id}/prioritize", smokeId)
                                .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk());

        String response =
                mockMvc.perform(
                                get("/api/incidents")
                                        .header("Authorization", bearer(citizenToken))
                                        .param("status", "PRIORITIZED")
                                        .param("type", "SMOKE")
                                        .param("severity", "CRITICAL")
                                        .param("search", "norte"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].id").value(smokeId.toString()))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertThat(response).contains("Humo sector norte").doesNotContain("Tala cerca del rio");
    }

    @Test
    void stillAllowsBasicUpdateAndDeleteBeforeClosure() throws Exception {
        UUID id = createIncident("Reporte editable", IncidentType.OTHER, IncidentSeverity.LOW);

        mockMvc.perform(
                        put("/api/incidents/{id}", id)
                                .header("Authorization", bearer(adminToken))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        json(
                                                Map.of(
                                                        "title",
                                                        "Reporte editado",
                                                        "description",
                                                        "Actualizado",
                                                        "type",
                                                        IncidentType.POLLUTION,
                                                        "severity",
                                                        IncidentSeverity.MEDIUM,
                                                        "latitude",
                                                        -17.79,
                                                        "longitude",
                                                        -63.19))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Reporte editado"))
                .andExpect(jsonPath("$.type").value("POLLUTION"));

        mockMvc.perform(
                        delete("/api/incidents/{id}", id)
                                .header("Authorization", bearer(adminToken)))
                .andExpect(status().isNoContent());
        mockMvc.perform(
                        get("/api/incidents/{id}", id)
                                .header("Authorization", bearer(citizenToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidCoordinates() throws Exception {
        mockMvc.perform(
                        post("/api/incidents")
                                .header("X-Correlation-Id", "test-correlation-id")
                                .header("Authorization", bearer(citizenToken))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        json(
                                                Map.of(
                                                        "title",
                                                        "Coordenada invalida",
                                                        "description",
                                                        "Fuera de rango.",
                                                        "type",
                                                        IncidentType.OTHER,
                                                        "severity",
                                                        IncidentSeverity.LOW,
                                                        "latitude",
                                                        -100,
                                                        "longitude",
                                                        -63.18))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.correlationId").value("test-correlation-id"));
    }

    @Test
    void listsBrigadesAndSummary() throws Exception {
        createIncident("Fuego critico", IncidentType.FIRE, IncidentSeverity.CRITICAL);

        mockMvc.perform(get("/api/brigades").header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Brigada Norte"));

        mockMvc.perform(get("/api/incidents/summary").header("Authorization", bearer(citizenToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.critical").value(1));
    }

    @Test
    void keepsOperationalActionsForAdministratorsOnly() throws Exception {
        UUID id = createIncident("Reporte ciudadano", IncidentType.SMOKE, IncidentSeverity.MEDIUM);

        mockMvc.perform(
                        post("/api/incidents/{id}/prioritize", id)
                                .header("Authorization", bearer(citizenToken)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/brigades").header("Authorization", bearer(citizenToken)))
                .andExpect(status().isForbidden());
    }

    private UUID createIncident(String title, IncidentType type, IncidentSeverity severity)
            throws Exception {
        String response =
                mockMvc.perform(
                                post("/api/incidents")
                                        .header("Authorization", bearer(citizenToken))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                json(
                                                        Map.of(
                                                                "title",
                                                                title,
                                                                "description",
                                                                "Reporte de prueba",
                                                                "type",
                                                                type,
                                                                "severity",
                                                                severity,
                                                                "latitude",
                                                                -17.7833,
                                                                "longitude",
                                                                -63.1821))))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        return UUID.fromString(objectMapper.readTree(response).get("id").asText());
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
