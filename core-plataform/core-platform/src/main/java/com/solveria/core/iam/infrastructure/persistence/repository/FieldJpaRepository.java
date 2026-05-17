package com.solveria.core.iam.infrastructure.persistence.repository;

import com.solveria.core.iam.infrastructure.persistence.entity.FieldJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FieldJpaRepository
        extends JpaRepository<FieldJpaEntity, Long>, JpaSpecificationExecutor<FieldJpaEntity> {}
