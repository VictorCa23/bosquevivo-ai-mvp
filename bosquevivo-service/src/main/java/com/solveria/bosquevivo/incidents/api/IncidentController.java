package com.solveria.bosquevivo.incidents.api;

import com.solveria.bosquevivo.incidents.application.IncidentService;
import com.solveria.bosquevivo.incidents.domain.IncidentSeverity;
import com.solveria.bosquevivo.incidents.domain.IncidentStatus;
import com.solveria.bosquevivo.incidents.domain.IncidentType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incidents")
@Tag(name = "Incidents", description = "Environmental incident reporting MVP")
public class IncidentController {

    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CITIZEN')")
    @Operation(summary = "Create an environmental incident")
    public ResponseEntity<IncidentResponse> create(
            @Valid @RequestBody CreateIncidentRequest request) {
        IncidentResponse response = service.create(request);
        return ResponseEntity.created(URI.create("/api/incidents/" + response.id())).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CITIZEN')")
    @Operation(summary = "List incidents with optional filters")
    public List<IncidentResponse> findAll(
            @RequestParam(required = false) IncidentStatus status,
            @RequestParam(required = false) IncidentType type,
            @RequestParam(required = false) IncidentSeverity severity,
            @RequestParam(required = false) String search) {
        return service.findAll(status, type, severity, search);
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'CITIZEN')")
    @Operation(summary = "Get incident operational summary")
    public IncidentSummaryResponse summary() {
        return service.summary();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CITIZEN')")
    @Operation(summary = "Get incident detail")
    public IncidentResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping("/{id}/events")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List incident audit events")
    public List<IncidentEventResponse> findEvents(@PathVariable UUID id) {
        return service.findEvents(id);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Change incident status")
    public IncidentResponse changeStatus(
            @PathVariable UUID id, @Valid @RequestBody ChangeIncidentStatusRequest request) {
        return service.changeStatus(id, request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update incident data")
    public IncidentResponse update(
            @PathVariable UUID id, @Valid @RequestBody UpdateIncidentRequest request) {
        return service.update(id, request);
    }

    @PostMapping("/{id}/prioritize")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Prioritize incident using deterministic MVP rules")
    public IncidentResponse prioritize(@PathVariable UUID id) {
        return service.prioritize(id);
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign incident to a brigade")
    public IncidentResponse assign(
            @PathVariable UUID id, @Valid @RequestBody AssignIncidentRequest request) {
        return service.assign(id, request);
    }

    @PostMapping("/{id}/start-attention")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Start field attention")
    public IncidentResponse startAttention(@PathVariable UUID id) {
        return service.startAttention(id);
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Close incident with checklist")
    public IncidentResponse close(
            @PathVariable UUID id, @Valid @RequestBody CloseIncidentRequest request) {
        return service.close(id, request);
    }

    @PostMapping("/{id}/reopen")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reopen a closed incident")
    public IncidentResponse reopen(
            @PathVariable UUID id, @Valid @RequestBody ReopenIncidentRequest request) {
        return service.reopen(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete incident")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
