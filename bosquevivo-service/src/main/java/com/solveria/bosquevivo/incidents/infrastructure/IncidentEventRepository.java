package com.solveria.bosquevivo.incidents.infrastructure;

import com.solveria.bosquevivo.incidents.domain.IncidentEvent;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentEventRepository extends JpaRepository<IncidentEvent, UUID> {
    List<IncidentEvent> findByIncidentIdOrderByCreatedAtDesc(UUID incidentId);
}
