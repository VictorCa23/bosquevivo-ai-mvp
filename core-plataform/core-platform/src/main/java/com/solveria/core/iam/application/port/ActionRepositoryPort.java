package com.solveria.core.iam.application.port;

import com.solveria.core.iam.domain.model.Action;
import java.util.Optional;

public interface ActionRepositoryPort {
    Optional<Action> findById(Long id);
}
