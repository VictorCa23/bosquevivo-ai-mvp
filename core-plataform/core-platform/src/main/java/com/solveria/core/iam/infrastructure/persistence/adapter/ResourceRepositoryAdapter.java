package com.solveria.core.iam.infrastructure.persistence.adapter;

import com.solveria.core.iam.application.port.ResourceRepositoryPort;
import com.solveria.core.iam.domain.model.Resource;
import com.solveria.core.iam.infrastructure.persistence.mapper.ResourceJpaMapper;
import com.solveria.core.iam.infrastructure.persistence.repository.ResourceJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ResourceRepositoryAdapter implements ResourceRepositoryPort {

    private final ResourceJpaRepository resourceJpaRepository;
    private final ResourceJpaMapper mapper;

    public ResourceRepositoryAdapter(
            ResourceJpaRepository resourceJpaRepository, ResourceJpaMapper mapper) {
        this.resourceJpaRepository = resourceJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Resource> findById(Long id) {
        return resourceJpaRepository.findById(id).map(mapper::toDomain);
    }
}
