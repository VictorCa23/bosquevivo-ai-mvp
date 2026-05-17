package com.solveria.core.iam.infrastructure.persistence.entity;

import com.solveria.core.shared.base.BaseEntity;
import jakarta.persistence.*;

/**
 * JPA entity for Permission persistence.
 *
 * <p>This class handles all JPA/database concerns, keeping the domain model pure.
 */
@Entity
@Table(name = "iam_permission")
public class PermissionJpaEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleJpaEntity role;

    @ManyToOne(optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private ModuleJpaEntity module;

    @ManyToOne(optional = false)
    @JoinColumn(name = "resource_id", nullable = false)
    private ResourceJpaEntity resource;

    @ManyToOne(optional = false)
    @JoinColumn(name = "action_id", nullable = false)
    private ActionJpaEntity action;

    @ManyToOne
    @JoinColumn(name = "field_id")
    private FieldJpaEntity field;

    protected PermissionJpaEntity() {
        // JPA required constructor
    }

    public PermissionJpaEntity(
            RoleJpaEntity role,
            ModuleJpaEntity module,
            ResourceJpaEntity resource,
            ActionJpaEntity action,
            FieldJpaEntity field) {
        this.role = role;
        this.module = module;
        this.resource = resource;
        this.action = action;
        this.field = field;
    }

    public RoleJpaEntity getRole() {
        return role;
    }

    public void setRole(RoleJpaEntity role) {
        this.role = role;
    }

    public ModuleJpaEntity getModule() {
        return module;
    }

    public ResourceJpaEntity getResource() {
        return resource;
    }

    public ActionJpaEntity getAction() {
        return action;
    }

    public FieldJpaEntity getField() {
        return field;
    }
}
