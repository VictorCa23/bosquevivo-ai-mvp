# ADR-001: Execution Plan - Remove JPA from Domain

**Status:** READY FOR EXECUTION
**Priority:** P0 - MUST DO BEFORE PRODUCTION
**Estimated Effort:** 2 days (1 developer)
**Risk:** HIGH (breaking changes)

---

## 🎯 OBJECTIVE

Remove ALL JPA annotations from domain layer and move to infrastructure, following Clean Architecture principles.

**Current State:** 8 domain entities with JPA annotations
**Target State:** Pure domain models + JPA entities in infrastructure

---

## 📊 AFFECTED ENTITIES (Priority Order)

1. **Action** (simplest, no relationships) ← START HERE
2. **Field** (simple, no complex relationships)
3. **Module** (simple, no complex relationships)
4. **Resource** (simple, no complex relationships)
5. **User** (medium, has roles relationship)
6. **Role** (medium, has users + permissions)
7. **Permission** (complex, has role + module + resource + action + field)
8. **AuditLog** (independent, audit bounded context)

**⚠️ ORDER MATTERS:** Do simple entities first to gain confidence, then tackle complex ones.

---

## 🔧 REFACTORING PATTERN (Per Entity)

### Step 1: Create Pure Domain Model

**Before (with JPA):**
```java
// domain/model/Action.java
@Entity
@Table(name = "iam_action")
public class Action extends BaseEntity {
    @Column(nullable = false)
    private String name;
    // ...
}
```

**After (pure domain):**
```java
// domain/model/Action.java
public class Action {
    private final ActionId id;  // Value Object
    private String name;
    private String description;

    public Action(ActionId id, String name, String description) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.description = description;
    }

    // Only business logic, no persistence concerns
}
```

### Step 2: Create JPA Entity in Infrastructure

```java
// infrastructure/persistence/entity/ActionJpaEntity.java
@Entity
@Table(name = "iam_action")
public class ActionJpaEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    // JPA constructors, getters, setters
}
```

### Step 3: Create Mapper

```java
// infrastructure/persistence/mapper/ActionJpaMapper.java
@Component
public class ActionJpaMapper {

    public Action toDomain(ActionJpaEntity entity) {
        return new Action(
            new ActionId(entity.getId()),
            entity.getName(),
            entity.getDescription()
        );
    }

    public ActionJpaEntity toEntity(Action domain) {
        ActionJpaEntity entity = new ActionJpaEntity();
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        return entity;
    }

    public void updateEntity(ActionJpaEntity entity, Action domain) {
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
    }
}
```

### Step 4: Update Repository Adapter

```java
// infrastructure/persistence/adapter/ActionRepositoryAdapter.java
@Repository
public class ActionRepositoryAdapter implements ActionRepository {

    private final ActionJpaRepository jpaRepo;
    private final ActionJpaMapper mapper;

    @Override
    public Optional<Action> findById(ActionId id) {
        return jpaRepo.findById(id.value())
            .map(mapper::toDomain);
    }

    @Override
    public Action save(Action action) {
        ActionJpaEntity entity = mapper.toEntity(action);
        ActionJpaEntity saved = jpaRepo.save(entity);
        return mapper.toDomain(saved);
    }
}
```

### Step 5: Update Use Cases

```java
// application/usecase/CreateRoleUseCase.java

// BEFORE:
private final RoleJpaRepository roleRepo;  // ❌ Direct JPA dependency

// AFTER:
private final RoleRepository roleRepo;  // ✅ Domain port (interface)
```

---

## 🧪 TESTING CHECKLIST (Per Entity)

For EACH entity refactored, verify:

- [ ] **Compilation:** `mvn compile` succeeds
- [ ] **Unit Tests:** Domain tests pass (no JPA needed)
- [ ] **Integration Tests:** Repository adapter tests pass
- [ ] **Use Case Tests:** Business logic tests pass
- [ ] **ArchUnit Tests:** No JPA in domain violations
- [ ] **Existing Functionality:** iam-service endpoints still work

---

## 📝 DETAILED EXECUTION STEPS

### ITERATION 1: Action (Simplest)

**1.1. Backup current code**
```bash
git checkout -b refactor/remove-jpa-action
git add .
git commit -m "Checkpoint before Action refactor"
```

**1.2. Create pure domain Action**
- File: `domain/model/Action.java`
- Remove: `@Entity`, `@Table`, `@Column`, `extends BaseEntity`
- Add: Value Object `ActionId`
- Keep: Business logic methods

**1.3. Create ActionJpaEntity**
- File: `infrastructure/persistence/entity/ActionJpaEntity.java`
- Copy JPA annotations from old Action
- Keep: `extends BaseEntity`

**1.4. Create ActionJpaMapper**
- File: `infrastructure/persistence/mapper/ActionJpaMapper.java`
- Implement: `toDomain()`, `toEntity()`, `updateEntity()`

**1.5. Update ActionJpaRepository**
- File: `infrastructure/persistence/repository/ActionJpaRepository.java`
- Change: `extends JpaRepository<Action, Long>`
- To: `extends JpaRepository<ActionJpaEntity, Long>`

**1.6. Update ActionRepositoryAdapter**
- File: `infrastructure/persistence/adapter/ActionRepositoryAdapter.java`
- Add: `ActionJpaMapper` injection
- Wrap: All JPA calls with mapper

**1.7. Update ActionRepositoryPort (if exists)**
- File: `application/port/ActionRepositoryPort.java`
- Ensure: Uses domain `Action`, not JPA entity

**1.8. Run tests**
```bash
cd core-plataform
mvn clean test -Dtest="*Action*"
```

**1.9. If all pass, commit**
```bash
git add .
git commit -m "refactor: Remove JPA from Action domain model"
```

**1.10. If tests fail, analyze and fix:**
- Check mapper logic
- Verify adapter wrapping
- Update use case imports

---

### ITERATION 2-7: Repeat for Other Entities

**Order:**
1. ✅ Action (done)
2. Field
3. Module
4. Resource
5. User
6. Role
7. Permission (most complex - has 5 relationships!)

**For each:**
- Follow same 10 steps as Action
- Pay special attention to relationships (ManyToMany, OneToMany)
- Test thoroughly before moving to next

---

### ITERATION 8: AuditLog (Separate Bounded Context)

Same process but isolated - audit doesn't affect IAM entities.

---

## ⚠️ KNOWN CHALLENGES & SOLUTIONS

### Challenge 1: Circular Dependencies

**Problem:** Role → Permission, Permission → Role

**Solution:**
- Use **aggregate root pattern**: Role is the aggregate, Permission is part of it
- Or: Break circular reference with `PermissionId` value object in Role

### Challenge 2: Lazy Loading

**Problem:** JPA lazy loading won't work in pure domain

**Solution:**
- Fetch relationships explicitly in adapter
- Use **repository methods** with join fetches:
  ```java
  @Query("SELECT r FROM RoleJpaEntity r LEFT JOIN FETCH r.permissions WHERE r.id = :id")
  Optional<RoleJpaEntity> findByIdWithPermissions(@Param("id") Long id);
  ```

### Challenge 3: BaseEntity Inheritance

**Problem:** Domain models should not extend BaseEntity (has JPA annotations)

**Solution:**
- Create pure `DomainEntity` interface with `getId()`, `getVersion()`
- BaseEntity only in JPA entities (infrastructure)

### Challenge 4: iam-service Dependency

**Problem:** iam-service uses core-platform entities directly

**Solution:**
- After each entity refactor, run: `cd iam-service && mvn clean verify`
- Fix imports: `import ...domain.model.Action` (should still work)
- If iam-service directly used JPA repos → ERROR, must go through ports

---

## 🚨 ROLLBACK PLAN

If refactor breaks something critical:

**Option A: Rollback one entity**
```bash
git revert <commit-hash-of-that-entity>
git push
```

**Option B: Rollback entire branch**
```bash
git reset --hard <before-refactor-commit>
git push --force
```

**Option C: Keep both (temporary)**
- Don't delete old domain model yet
- Rename to `RoleLegacy.java`
- Use feature flag to switch between old/new implementation

---

## 📈 PROGRESS TRACKING

Create a tracking doc:

| Entity | Status | Commit | Tests Passing | Notes |
|--------|--------|--------|---------------|-------|
| Action | ✅ DONE | abc123 | ✅ All pass | - |
| Field | 🔄 IN PROGRESS | - | - | - |
| Module | ⏳ TODO | - | - | - |
| Resource | ⏳ TODO | - | - | - |
| User | ⏳ TODO | - | - | Complex: has roles |
| Role | ⏳ TODO | - | - | Complex: has users + permissions |
| Permission | ⏳ TODO | - | - | Most complex: 5 relationships |
| AuditLog | ⏳ TODO | - | - | Independent |

---

## ✅ DEFINITION OF DONE

Refactor is complete when:

- [ ] All 8 entities are pure domain (no JPA)
- [ ] All JPA entities in infrastructure layer
- [ ] All mappers implemented and tested
- [ ] All adapters updated
- [ ] `mvn clean verify` passes in core-platform
- [ ] `mvn clean verify` passes in iam-service
- [ ] ArchUnit test `domainMustNotDependOnJPA` is **ENABLED** and **PASSES**
- [ ] No `jakarta.persistence.*` imports in `domain/model/**`
- [ ] Documentation updated (README, ADR-001)

---

## 🎓 LEARNING RESOURCES

- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [DDD Aggregates](https://martinfowler.com/bliki/DDD_Aggregate.html)
- [Clean Architecture (Uncle Bob)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- Example: `core-platform/...persistence/entity/RoleJpaEntityExample.java`

---

## 📞 SUPPORT

If stuck:
1. Check example: `RoleJpaEntityExample` + `RoleJpaMapperExample`
2. Review this execution plan
3. Ask team for pair programming
4. Rollback and try again

---

**REMEMBER:** This is a **critical refactor**. Take your time, test thoroughly, commit often.
