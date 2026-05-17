package com.solveria.bosquevivo.incidents.application;

import com.solveria.bosquevivo.incidents.api.AssignIncidentRequest;
import com.solveria.bosquevivo.incidents.api.ChangeIncidentStatusRequest;
import com.solveria.bosquevivo.incidents.api.CloseIncidentRequest;
import com.solveria.bosquevivo.incidents.api.CreateIncidentRequest;
import com.solveria.bosquevivo.incidents.api.IncidentEventResponse;
import com.solveria.bosquevivo.incidents.api.IncidentResponse;
import com.solveria.bosquevivo.incidents.api.IncidentSummaryResponse;
import com.solveria.bosquevivo.incidents.api.ReopenIncidentRequest;
import com.solveria.bosquevivo.incidents.api.UpdateIncidentRequest;
import com.solveria.bosquevivo.incidents.application.ai.IncidentAnalysisClient;
import com.solveria.bosquevivo.incidents.application.ai.IncidentAnalysisResult;
import com.solveria.bosquevivo.incidents.domain.Brigade;
import com.solveria.bosquevivo.incidents.domain.Incident;
import com.solveria.bosquevivo.incidents.domain.IncidentEvent;
import com.solveria.bosquevivo.incidents.domain.IncidentEventType;
import com.solveria.bosquevivo.incidents.domain.IncidentSeverity;
import com.solveria.bosquevivo.incidents.domain.IncidentStatus;
import com.solveria.bosquevivo.incidents.domain.IncidentType;
import com.solveria.bosquevivo.incidents.infrastructure.BrigadeRepository;
import com.solveria.bosquevivo.incidents.infrastructure.IncidentEventRepository;
import com.solveria.bosquevivo.incidents.infrastructure.IncidentRepository;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class IncidentService {

    private final IncidentRepository repository;
    private final BrigadeRepository brigadeRepository;
    private final IncidentEventRepository eventRepository;
    private final IncidentAnalysisClient analysisClient;

    public IncidentService(
            IncidentRepository repository,
            BrigadeRepository brigadeRepository,
            IncidentEventRepository eventRepository,
            IncidentAnalysisClient analysisClient) {
        this.repository = repository;
        this.brigadeRepository = brigadeRepository;
        this.eventRepository = eventRepository;
        this.analysisClient = analysisClient;
    }

    @Transactional
    public IncidentResponse create(CreateIncidentRequest request) {
        Incident incident =
                new Incident(
                        request.title(),
                        request.description(),
                        request.type(),
                        request.severity(),
                        request.latitude(),
                        request.longitude());
        Incident saved = repository.save(incident);
        record(saved, IncidentEventType.INCIDENT_CREATED, "Incidente reportado por canal web.");
        return IncidentResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<IncidentResponse> findAll(
            IncidentStatus status, IncidentType type, IncidentSeverity severity, String search) {
        return repository.findAll().stream()
                .filter(incident -> status == null || incident.getStatus() == status)
                .filter(incident -> type == null || incident.getType() == type)
                .filter(incident -> severity == null || incident.getSeverity() == severity)
                .filter(incident -> matchesSearch(incident, search))
                .sorted(Comparator.comparing(Incident::getCreatedAt).reversed())
                .map(IncidentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public IncidentResponse findById(UUID id) {
        return IncidentResponse.from(getIncident(id));
    }

    @Transactional(readOnly = true)
    public List<IncidentEventResponse> findEvents(UUID id) {
        getIncident(id);
        return eventRepository.findByIncidentIdOrderByCreatedAtDesc(id).stream()
                .map(IncidentEventResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public IncidentSummaryResponse summary() {
        List<Incident> incidents = repository.findAll();
        return new IncidentSummaryResponse(
                incidents.size(),
                incidents.stream()
                        .filter(incident -> incident.getStatus() != IncidentStatus.CLOSED)
                        .count(),
                countStatus(incidents, IncidentStatus.PRIORITIZED),
                countStatus(incidents, IncidentStatus.ASSIGNED),
                countStatus(incidents, IncidentStatus.IN_ATTENTION),
                countStatus(incidents, IncidentStatus.CLOSED),
                incidents.stream()
                        .filter(incident -> incident.getSeverity() == IncidentSeverity.CRITICAL)
                        .count());
    }

    @Transactional
    public IncidentResponse changeStatus(UUID id, ChangeIncidentStatusRequest request) {
        Incident incident = getIncident(id);
        incident.changeStatus(request.status());
        record(
                incident,
                IncidentEventType.INCIDENT_UPDATED,
                "Estado ajustado manualmente a " + request.status());
        return IncidentResponse.from(incident);
    }

    @Transactional
    public IncidentResponse update(UUID id, UpdateIncidentRequest request) {
        Incident incident = getIncident(id);
        ensureEditable(incident);
        incident.update(
                request.title(),
                request.description(),
                request.type(),
                request.severity(),
                request.latitude(),
                request.longitude());
        record(incident, IncidentEventType.INCIDENT_UPDATED, "Datos del incidente actualizados.");
        return IncidentResponse.from(incident);
    }

    @Transactional
    public IncidentResponse prioritize(UUID id) {
        Incident incident = getIncident(id);
        ensureNotClosed(incident);
        IncidentAnalysisResult result = analysisClient.analyze(incident);
        String reason =
                result.reason()
                        + " Accion sugerida: "
                        + result.recommendedAction()
                        + " Modelo: "
                        + result.model()
                        + ".";
        incident.prioritize(result.score(), reason, result.slaDueAt());
        record(incident, IncidentEventType.INCIDENT_PRIORITIZED, reason);
        return IncidentResponse.from(incident);
    }

    @Transactional
    public IncidentResponse assign(UUID id, AssignIncidentRequest request) {
        Incident incident = getIncident(id);
        ensureStatus(incident, IncidentStatus.PRIORITIZED, IncidentStatus.REOPENED);
        Brigade brigade =
                brigadeRepository
                        .findById(request.brigadeId())
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Brigade not found"));
        if (!brigade.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brigade is not available");
        }
        incident.assign(brigade.getId());
        record(incident, IncidentEventType.BRIGADE_ASSIGNED, "Asignado a " + brigade.getName());
        return IncidentResponse.from(incident);
    }

    @Transactional
    public IncidentResponse startAttention(UUID id) {
        Incident incident = getIncident(id);
        ensureStatus(incident, IncidentStatus.ASSIGNED);
        incident.startAttention();
        record(incident, IncidentEventType.ATTENTION_STARTED, "Brigada inicio atencion en campo.");
        return IncidentResponse.from(incident);
    }

    @Transactional
    public IncidentResponse close(UUID id, CloseIncidentRequest request) {
        Incident incident = getIncident(id);
        ensureStatus(incident, IncidentStatus.IN_ATTENTION);
        incident.close(request.notes());
        record(incident, IncidentEventType.INCIDENT_CLOSED, request.notes());
        return IncidentResponse.from(incident);
    }

    @Transactional
    public IncidentResponse reopen(UUID id, ReopenIncidentRequest request) {
        Incident incident = getIncident(id);
        ensureStatus(incident, IncidentStatus.CLOSED);
        incident.reopen(request.reason());
        record(incident, IncidentEventType.INCIDENT_REOPENED, request.reason());
        return IncidentResponse.from(incident);
    }

    @Transactional
    public void delete(UUID id) {
        Incident incident = getIncident(id);
        repository.delete(incident);
    }

    private boolean matchesSearch(Incident incident, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }
        String normalized = search.toLowerCase();
        return incident.getTitle().toLowerCase().contains(normalized)
                || (incident.getDescription() != null
                        && incident.getDescription().toLowerCase().contains(normalized));
    }

    private void ensureEditable(Incident incident) {
        if (incident.getStatus() == IncidentStatus.CLOSED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Closed incidents cannot be edited; reopen first");
        }
    }

    private void ensureNotClosed(Incident incident) {
        if (incident.getStatus() == IncidentStatus.CLOSED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Closed incidents cannot be changed");
        }
    }

    private void ensureStatus(Incident incident, IncidentStatus... allowedStatuses) {
        for (IncidentStatus allowedStatus : allowedStatuses) {
            if (incident.getStatus() == allowedStatus) {
                return;
            }
        }
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Invalid transition from " + incident.getStatus());
    }

    private long countStatus(List<Incident> incidents, IncidentStatus status) {
        return incidents.stream().filter(incident -> incident.getStatus() == status).count();
    }

    private void record(Incident incident, IncidentEventType type, String detail) {
        eventRepository.save(new IncidentEvent(incident.getId(), type, detail));
    }

    private Incident getIncident(UUID id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Incident not found"));
    }
}
