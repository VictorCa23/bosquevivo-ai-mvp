package com.solveria.core.iam.application.port;

import com.solveria.core.iam.domain.model.Resource;
import java.util.Optional;

public interface ResourceRepositoryPort {
    Optional<Resource> findById(Long id);
}
