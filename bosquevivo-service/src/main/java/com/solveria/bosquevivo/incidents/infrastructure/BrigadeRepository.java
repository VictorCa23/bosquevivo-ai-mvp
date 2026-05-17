package com.solveria.bosquevivo.incidents.infrastructure;

import com.solveria.bosquevivo.incidents.domain.Brigade;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrigadeRepository extends JpaRepository<Brigade, UUID> {}
