package com.solveria.core.iam.infrastructure.persistence.entity;

import com.solveria.core.shared.base.BaseEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * EXAMPLE: JPA Entity for Role (Clean Architecture Pattern).
 *
 * <p><strong>⚠️ This is a REFERENCE IMPLEMENTATION.</strong> Not currently used in production.
 *
 * <p>Shows how to properly separate domain models from JPA concerns:
 *
 * <ul>
 *   <li>Domain model ({@code Role.java}) = Pure business logic, no framework deps
 *   <li>JPA entity ({@code RoleJpaEntity.java}) = Persistence mapping, infrastructure layer
 *   <li>Mapper ({@code RoleJpaMapper.java}) = Converts between domain ↔ JPA entity
 * </ul>
 *
 * <h3>Migration Path:</h3>
 *
 * <ol>
 *   <li>Create pure domain {@code Role} (no JPA)
 *   <li>Rename current domain {@code Role} → {@code RoleJpaEntity}, move to this package
 *   <li>Create {@code RoleJpaMapper}
 *   <li>Update {@code RoleRepositoryAdapter} to use mapper
 *   <li>Update all use cases to work with pure domain {@code Role}
 * </ol>
 *
 * @see com.solveria.core.iam.domain.model.Role
 * @see <a href="../../../../../../adr/ADR-001-jpa-in-domain-technical-debt.md">ADR-001</a>
 */
public class RoleJpaEntityExample extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column private String description;

    // JPA relationships stay in infrastructure layer
    @ManyToMany(mappedBy = "roles")
    private Set<UserJpaEntityExample> users = new HashSet<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PermissionJpaEntityExample> permissions = new HashSet<>();

    protected RoleJpaEntityExample() {
        // JPA required constructor
    }

    public RoleJpaEntityExample(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters/Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<UserJpaEntityExample> getUsers() {
        return users;
    }

    public Set<PermissionJpaEntityExample> getPermissions() {
        return permissions;
    }

    public void addPermission(PermissionJpaEntityExample permission) {
        permissions.add(permission);
        permission.setRole(this);
    }

    public void removePermission(PermissionJpaEntityExample permission) {
        permissions.remove(permission);
        permission.setRole(null);
    }

    // Placeholder for other entities (not implementing full example)
    static class UserJpaEntityExample {}

    static class PermissionJpaEntityExample {
        private RoleJpaEntityExample role;

        public void setRole(RoleJpaEntityExample role) {
            this.role = role;
        }
    }
}
