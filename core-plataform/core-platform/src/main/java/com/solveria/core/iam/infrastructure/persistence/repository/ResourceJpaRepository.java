package com.solveria.core.iam.infrastructure.persistence.repository;

import com.solveria.core.iam.infrastructure.persistence.entity.ResourceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ResourceJpaRepository
        extends JpaRepository<ResourceJpaEntity, Long>,
                JpaSpecificationExecutor<ResourceJpaEntity> {}
