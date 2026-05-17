/**
 * JPA Entity Layer (Infrastructure Concern).
 *
 * <p><strong>⚠️ TECHNICAL DEBT (ADR-001):</strong> Currently, JPA annotations are in domain models.
 * This package will contain the <strong>correct implementation</strong> once we migrate.
 *
 * <h3>Target Pattern (Clean Architecture):</h3>
 *
 * <pre>
 * domain/model/Role.java         → Pure domain (no JPA)
 * infrastructure/entity/RoleJpaEntity.java  → JPA mappings
 * infrastructure/mapper/RoleJpaMapper.java  → Bidirectional conversion
 * infrastructure/adapter/RoleRepositoryAdapter.java → Implements domain port
 * </pre>
 *
 * <h3>Example (Role entity):</h3>
 *
 * <p>See {@code RoleJpaEntityExample} for reference implementation.
 *
 * @see com.solveria.core.iam.domain.model.Role
 * @see <a href="../../../../../../adr/ADR-001-jpa-in-domain-technical-debt.md">ADR-001</a>
 */
package com.solveria.core.iam.infrastructure.persistence.entity;
