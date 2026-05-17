package com.solveria.core.shared.specifications;

import com.solveria.core.shared.base.BaseEntity;
import org.springframework.data.jpa.domain.Specification;

public final class TenantSpecifications {

    private TenantSpecifications() {}

    public static <T extends BaseEntity> Specification<T> hasTenant(String tenantId) {
        return (root, query, cb) -> cb.equal(root.get("tenantId"), tenantId);
    }
}
