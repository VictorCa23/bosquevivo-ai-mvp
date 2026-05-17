package com.solveria.core.iam.infrastructure.persistence.specification;

import com.solveria.core.iam.domain.model.Permission;
import org.springframework.data.jpa.domain.Specification;

public final class IamSpecifications {

    private IamSpecifications() {}

    public static Specification<Permission> byRoleId(Long roleId) {
        return (root, query, cb) -> cb.equal(root.get("role").get("id"), roleId);
    }
}
