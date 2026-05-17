package com.solveria.core.iam.infrastructure.persistence.entity;

import com.solveria.core.shared.base.BaseEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity for User persistence.
 *
 * <p>This class handles all JPA/database concerns, keeping the domain model pure.
 */
@Entity
@Table(name = "iam_user")
public class UserJpaEntity extends BaseEntity {

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean active;

    @ManyToMany
    @JoinTable(
            name = "iam_user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleJpaEntity> roles = new HashSet<>();

    protected UserJpaEntity() {
        // JPA required constructor
    }

    public UserJpaEntity(String username, String email, boolean active) {
        this.username = username;
        this.email = email;
        this.active = active;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<RoleJpaEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleJpaEntity> roles) {
        this.roles = roles;
    }

    public void assignRoles(Set<RoleJpaEntity> roles) {
        this.roles.clear();
        if (roles != null) {
            this.roles.addAll(roles);
        }
    }
}
