package com.solveria.core.iam.infrastructure.persistence.entity;

import com.solveria.core.shared.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * JPA entity for Resource persistence.
 *
 * <p>This class handles all JPA/database concerns, keeping the domain model pure.
 */
@Entity
@Table(name = "iam_resource")
public class ResourceJpaEntity extends BaseEntity {

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    private ModuleJpaEntity module;

    protected ResourceJpaEntity() {
        // JPA required constructor
    }

    public ResourceJpaEntity(String code, String name, ModuleJpaEntity module) {
        this.code = code;
        this.name = name;
        this.module = module;
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

    public ModuleJpaEntity getModule() {
        return module;
    }

    public void setModule(ModuleJpaEntity module) {
        this.module = module;
    }
}
