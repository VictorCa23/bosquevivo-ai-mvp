package com.solveria.core.iam.infrastructure.persistence.adapter;

import com.solveria.core.iam.application.port.ModuleRepositoryPort;
import com.solveria.core.iam.domain.model.Module;
import com.solveria.core.iam.infrastructure.persistence.mapper.ModuleJpaMapper;
import com.solveria.core.iam.infrastructure.persistence.repository.ModuleJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ModuleRepositoryAdapter implements ModuleRepositoryPort {

    private final ModuleJpaRepository moduleJpaRepository;
    private final ModuleJpaMapper mapper;

    public ModuleRepositoryAdapter(
            ModuleJpaRepository moduleJpaRepository, ModuleJpaMapper mapper) {
        this.moduleJpaRepository = moduleJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Module> findById(Long id) {
        return moduleJpaRepository.findById(id).map(mapper::toDomain);
    }
}
