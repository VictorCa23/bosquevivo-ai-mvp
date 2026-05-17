package com.solveria.core.iam.infrastructure.persistence.adapter;

import com.solveria.core.iam.application.port.FieldRepositoryPort;
import com.solveria.core.iam.domain.model.Field;
import com.solveria.core.iam.infrastructure.persistence.mapper.FieldJpaMapper;
import com.solveria.core.iam.infrastructure.persistence.repository.FieldJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class FieldRepositoryAdapter implements FieldRepositoryPort {

    private final FieldJpaRepository fieldJpaRepository;
    private final FieldJpaMapper mapper;

    public FieldRepositoryAdapter(FieldJpaRepository fieldJpaRepository, FieldJpaMapper mapper) {
        this.fieldJpaRepository = fieldJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Field> findById(Long id) {
        return fieldJpaRepository.findById(id).map(mapper::toDomain);
    }
}
