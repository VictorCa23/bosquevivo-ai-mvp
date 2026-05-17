package com.solveria.core.iam.infrastructure.persistence.repository;

import com.solveria.core.iam.infrastructure.persistence.entity.ModuleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ModuleJpaRepository
        extends JpaRepository<ModuleJpaEntity, Long>, JpaSpecificationExecutor<ModuleJpaEntity> {}
