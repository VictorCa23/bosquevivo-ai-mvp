package com.solveria.bosquevivo.incidents.application.ai;

import com.solveria.bosquevivo.incidents.domain.Incident;
import com.solveria.bosquevivo.incidents.domain.IncidentSeverity;
import com.solveria.bosquevivo.incidents.domain.IncidentType;
import java.time.Duration;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class IncidentAnalysisClient {

    private static final Logger log = LoggerFactory.getLogger(IncidentAnalysisClient.class);

    private final RestClient restClient;
    private final boolean enabled;

    public IncidentAnalysisClient(
            RestClient.Builder builder,
            @Value("${bosquevivo.ai-service.base-url:http://localhost:8091}") String baseUrl,
            @Value("${bosquevivo.ai-service.enabled:false}") boolean enabled,
            @Value("${bosquevivo.ai-service.connect-timeout-ms:1000}") long connectTimeoutMs,
            @Value("${bosquevivo.ai-service.read-timeout-ms:2000}") long readTimeoutMs) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));
        this.restClient = builder.baseUrl(baseUrl).requestFactory(requestFactory).build();
        this.enabled = enabled;
    }

    public IncidentAnalysisResult analyze(Incident incident) {
        if (!enabled) {
            return localAnalysis(incident, "local-rules");
        }

        try {
            AiIncidentAnalysisResponse response =
                    restClient
                            .post()
                            .uri("/api/v1/incidents/analyze")
                            .body(AiIncidentAnalysisRequest.from(incident))
                            .retrieve()
                            .body(AiIncidentAnalysisResponse.class);
            if (response == null) {
                return localAnalysis(incident, "local-rules-empty-ai-response");
            }
            return new IncidentAnalysisResult(
                    response.priorityScore(),
                    response.priorityReason(),
                    OffsetDateTime.now().plusHours(response.slaHours()),
                    response.recommendedAction(),
                    response.model());
        } catch (RuntimeException ex) {
            log.warn("event=AI_ANALYSIS_FALLBACK reason={}", ex.getMessage());
            return localAnalysis(incident, "local-rules-ai-fallback");
        }
    }

    private IncidentAnalysisResult localAnalysis(Incident incident, String model) {
        int score =
                Math.min(
                        typeScore(incident.getType()) + severityScore(incident.getSeverity()), 100);
        int slaHours = slaHours(score);
        String reason =
                "Prioridad "
                        + score
                        + "/100 por tipo "
                        + incident.getType()
                        + " y severidad "
                        + incident.getSeverity()
                        + ". SLA sugerido: "
                        + slaHours
                        + " horas.";
        return new IncidentAnalysisResult(
                score,
                reason,
                OffsetDateTime.now().plusHours(slaHours),
                recommendedAction(score),
                model);
    }

    private int typeScore(IncidentType type) {
        return switch (type) {
            case FIRE -> 55;
            case SMOKE -> 40;
            case ILLEGAL_LOGGING -> 35;
            case POLLUTION -> 30;
            case OTHER -> 20;
        };
    }

    private int severityScore(IncidentSeverity severity) {
        return switch (severity) {
            case CRITICAL -> 45;
            case HIGH -> 35;
            case MEDIUM -> 20;
            case LOW -> 10;
        };
    }

    private int slaHours(int score) {
        if (score >= 85) {
            return 2;
        }
        if (score >= 65) {
            return 6;
        }
        if (score >= 45) {
            return 12;
        }
        return 24;
    }

    private String recommendedAction(int score) {
        if (score >= 85) {
            return "Asignar brigada inmediatamente y monitorear evolucion del riesgo.";
        }
        if (score >= 65) {
            return "Priorizar atencion operativa y preparar asignacion de brigada.";
        }
        if (score >= 45) {
            return "Mantener seguimiento y validar informacion del reporte.";
        }
        return "Registrar y observar sin activar respuesta urgente.";
    }

    private record AiIncidentAnalysisRequest(
            String title,
            String description,
            String type,
            String severity,
            double latitude,
            double longitude) {
        private static AiIncidentAnalysisRequest from(Incident incident) {
            return new AiIncidentAnalysisRequest(
                    incident.getTitle(),
                    incident.getDescription(),
                    incident.getType().name(),
                    incident.getSeverity().name(),
                    incident.getLatitude(),
                    incident.getLongitude());
        }
    }

    private record AiIncidentAnalysisResponse(
            int priorityScore,
            int slaHours,
            String priorityReason,
            String recommendedAction,
            String model) {}
}
