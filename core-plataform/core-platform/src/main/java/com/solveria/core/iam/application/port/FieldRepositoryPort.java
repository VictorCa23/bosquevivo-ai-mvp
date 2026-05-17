package com.solveria.core.iam.application.port;

import com.solveria.core.iam.domain.model.Field;
import java.util.Optional;

public interface FieldRepositoryPort {
    Optional<Field> findById(Long id);
}
