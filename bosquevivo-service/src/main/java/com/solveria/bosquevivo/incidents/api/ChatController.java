package com.solveria.bosquevivo.incidents.api;

import com.solveria.bosquevivo.incidents.application.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "Chat", description = "Asistente IA de consultas sobre incidentes")
public class ChatController {

    private final IncidentService incidentService;

    public ChatController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CITIZEN')")
    @Operation(summary = "Responde preguntas sobre incidentes en lenguaje natural")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String question = request.question().toLowerCase().trim();
        IncidentSummaryResponse summary = incidentService.summary();

        String answer = buildAnswer(question, summary);
        return new ChatResponse(answer);
    }

    private String buildAnswer(String question, IncidentSummaryResponse s) {
        // Preguntas sobre total
        if (contains(question, "cuántos", "cuantos", "total", "todos", "hay")) {
            if (contains(question, "cerrado", "cerrados", "cerrada", "resuelto", "resueltos", "solucionado")) {
                return String.format("Se han cerrado %d incidentes en total.", s.closed());
            }
            if (contains(question, "crítico", "critico", "críticos", "criticos")) {
                return String.format("Actualmente hay %d incidentes críticos.", s.critical());
            }
            if (contains(question, "abierto", "abiertos", "activo", "activos", "abierta")) {
                return String.format("Hay %d incidentes abiertos actualmente.", s.open());
            }
            if (contains(question, "asignado", "asignados")) {
                return String.format("Hay %d incidentes asignados a brigadas.", s.assigned());
            }
            if (contains(question, "atención", "atencion", "atendiendo")) {
                return String.format("Hay %d incidentes en atención activa.", s.inAttention());
            }
            if (contains(question, "priorizado", "priorizados")) {
                return String.format("Hay %d incidentes priorizados esperando asignación.", s.prioritized());
            }
            if (contains(question, "incidente", "incidentes", "reporte", "reportes", "caso", "casos")) {
                return String.format(
                    "En total hay %d incidentes registrados: %d abiertos, %d asignados, %d en atención, %d cerrados y %d críticos.",
                    s.total(), s.open(), s.assigned(), s.inAttention(), s.closed(), s.critical()
                );
            }
        }

        // Preguntas sobre resumen
        if (contains(question, "resumen", "estado", "situación", "situacion", "panorama")) {
            return String.format(
                "Resumen operativo: %d incidentes en total. %d abiertos, %d priorizados, %d asignados, %d en atención, %d cerrados. Hay %d críticos que requieren atención urgente.",
                s.total(), s.open(), s.prioritized(), s.assigned(), s.inAttention(), s.closed(), s.critical()
            );
        }

        // Preguntas sobre cerrados hoy (aproximación con el total cerrado)
        if (contains(question, "hoy", "día", "dia") && contains(question, "cerr", "resuel", "solucion")) {
            return String.format(
                "Hasta el momento se han cerrado %d incidentes en el sistema. Para ver los cierres de hoy específicamente, consulta el historial en el panel.",
                s.closed()
            );
        }

        // Preguntas de estado general
        if (contains(question, "cómo", "como", "bien", "mal", "funcionando")) {
            if (s.critical() > 0) {
                return String.format(
                    "Hay %d incidentes críticos activos que necesitan atención urgente, de un total de %d incidentes. Te recomiendo revisar los casos críticos primero.",
                    s.critical(), s.total()
                );
            }
            return String.format(
                "Todo bajo control. Hay %d incidentes activos de un total de %d. No hay críticos en este momento.",
                s.open(), s.total()
            );
        }

        // Respuesta por defecto
        return String.format(
            "Tengo estos datos disponibles: %d incidentes en total, %d abiertos, %d cerrados y %d críticos. " +
            "Puedes preguntarme sobre incidentes abiertos, cerrados, críticos, asignados o el resumen general.",
            s.total(), s.open(), s.closed(), s.critical()
        );
    }

    private boolean contains(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }

    public record ChatRequest(String question) {}
    public record ChatResponse(String answer) {}
}
