package com.solveria.core.iam.infrastructure.persistence.entity;

import com.solveria.core.shared.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * JPA entity for Module persistence.
 *
 * <p>This class handles all JPA/database concerns, keeping the domain model pure.
 */
@Entity
@Table(name = "iam_module")
public class ModuleJpaEntity extends BaseEntity {

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    protected ModuleJpaEntity() {
        // JPA required constructor
    }

    public ModuleJpaEntity(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
