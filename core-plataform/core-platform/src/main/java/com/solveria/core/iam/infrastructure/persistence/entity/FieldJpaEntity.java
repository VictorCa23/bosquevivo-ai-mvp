package com.solveria.core.iam.infrastructure.persistence.entity;

import com.solveria.core.shared.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * JPA entity for Field persistence.
 *
 * <p>This class handles all JPA/database concerns, keeping the domain model pure.
 */
@Entity
@Table(name = "iam_field")
public class FieldJpaEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    private ResourceJpaEntity resource;

    protected FieldJpaEntity() {
        // JPA required constructor
    }

    public FieldJpaEntity(String name, ResourceJpaEntity resource) {
        this.name = name;
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceJpaEntity getResource() {
        return resource;
    }

    public void setResource(ResourceJpaEntity resource) {
        this.resource = resource;
    }
}
