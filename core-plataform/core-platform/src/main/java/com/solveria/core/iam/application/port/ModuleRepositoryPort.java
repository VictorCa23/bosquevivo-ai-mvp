package com.solveria.core.iam.application.port;

import com.solveria.core.iam.domain.model.Module;
import java.util.Optional;

public interface ModuleRepositoryPort {
    Optional<Module> findById(Long id);
}
