# ADR-001: JPA Annotations in Domain Layer (Technical Debt)

**Status:** ⛔ BLOCKED FOR PRODUCTION (must remediate before go-live)
**Date:** 2026-02-23
**Updated:** 2026-02-23 (Phase B - Execution Plan Created)
**Decision Makers:** Tech Lead
**Affected:** core-platform IAM domain (8 entities)
**Severity:** **P0 - CRITICAL ARCHITECTURE VIOLATION**

## Context

During architectural audit (Phase A), we discovered that domain models in `core-platform` have direct JPA dependencies:
- `@Entity`, `@Table`, `@ManyToMany`, `@OneToMany` annotations present in domain layer
- Violates Clean Architecture principle: **Domain should not depend on infrastructure concerns**

**Affected entities:**
- `com.solveria.core.iam.domain.model.Role`
- `com.solveria.core.iam.domain.model.User`
- `com.solveria.core.iam.domain.model.Permission`
- `com.solveria.core.iam.domain.model.Module`
- `com.solveria.core.iam.domain.model.Resource`
- `com.solveria.core.iam.domain.model.Action`
- `com.solveria.core.iam.domain.model.Field`
- `com.solveria.core.audit.domain.model.AuditLog`

## Decision

**We ACCEPT this technical debt temporarily** with the following guardrails:

1. ✅ **Document as known debt** (this ADR)
2. ✅ **Add ArchUnit test** that allows JPA in domain BUT warns about it
3. ✅ **Create refactoring example** for one entity (Role) as template
4. ✅ **Plan incremental remediation** before next major release

**Why not fix immediately?**
- High risk: 8 entities with complex relationships
- iam-service already uses these entities via core-platform dependency
- Would require rewriting all adapters, mappers, and tests
- Better done incrementally with full test coverage per entity

## Consequences

### Positive
- No disruption to existing services
- Time to plan proper refactor with team
- Example pattern created for future work

### Negative
- Domain is NOT portable (tied to JPA)
- Cannot easily swap persistence mechanism
- Violates hexagonal architecture purity

## Remediation Plan

### ✅ Phase 1 (COMPLETED - Phase B)
- [x] Document debt in this ADR
- [x] Add ArchUnit test with warning (currently disabled)
- [x] Create example refactor for Role entity (RoleJpaEntityExample + Mapper)
- [x] **Create detailed execution plan** → See `ADR-001-EXECUTION-PLAN.md`

### 🔄 Phase 2 (IN PROGRESS - Sprint N)
**⚠️ BLOCKLIST PARA PRODUCCIÓN - DEBE COMPLETARSE**

**Estimated Effort:** 2 days (1 developer)
**Execution Order:**
1. [ ] Action (simplest) ← START HERE
2. [ ] Field
3. [ ] Module
4. [ ] Resource
5. [ ] User (critical for security)
6. [ ] Role (critical for authorization)
7. [ ] Permission (most complex)
8. [ ] AuditLog (independent)

**Detailed Steps:** See `ADR-001-EXECUTION-PLAN.md`

### ✅ Phase 3 (Definition of Done)
- [ ] All 8 entities refactored (pure domain + JPA in infrastructure)
- [ ] All tests passing (core-platform + iam-service)
- [ ] ArchUnit test `domainMustNotDependOnJPA` **ENABLED** and **PASSING**
- [ ] No `jakarta.persistence.*` in domain layer
- [ ] Documentation updated

## Pattern for Refactoring

### Current (with JPA in domain)
```java
// domain/model/Role.java
@Entity
@Table(name = "iam_role")
public class Role extends BaseEntity {
    @Column(nullable = false)
    private String name;
}
```

### Target (Clean Architecture)
```java
// domain/model/Role.java (PURE - no JPA)
public class Role {
    private final RoleId id;
    private String name;
    // Pure business logic only
}

// infrastructure/persistence/entity/RoleJpaEntity.java
@Entity
@Table(name = "iam_role")
public class RoleJpaEntity {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String name;
}

// infrastructure/persistence/mapper/RoleJpaMapper.java
public class RoleJpaMapper {
    public Role toDomain(RoleJpaEntity entity) { ... }
    public RoleJpaEntity toEntity(Role domain) { ... }
}
```

## ⚠️ PRODUCTION READINESS

**Can we go to production with this debt?**

**NO.** This is a **P0 ARCHITECTURE VIOLATION**.

**Why it blocks production:**
1. **Maintenance Hell:** Domain tied to JPA = cannot change persistence easily
2. **Testing Nightmare:** Domain tests require JPA infrastructure (slow, brittle)
3. **Portability Zero:** Locked to Spring Data JPA forever
4. **Team Confusion:** Newcomers will replicate the anti-pattern
5. **Technical Debt Interest:** Every new entity will add JPA to domain

**Timeline:**
- **Sprint N:** Complete Phase 2 refactor (2 days)
- **Sprint N+1:** Production deployment allowed

## References
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [DDD Aggregates](https://martinfowler.com/bliki/DDD_Aggregate.html)
- **Execution Plan:** `ADR-001-EXECUTION-PLAN.md` (MUST READ before refactoring)
- Phase A Audit Report (2026-02-23)
- Phase B Remediation (2026-02-23)
