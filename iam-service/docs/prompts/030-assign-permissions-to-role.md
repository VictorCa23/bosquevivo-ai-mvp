# 030 - Assign Permissions to Role

## Objetivo

Implementar el caso de uso AssignPermissionsToRole completo: Controller REST, Orchestrator, DTOs, validaciones, manejo de errores y tests.

## Archivos Objetivo

- `src/main/java/com/solveria/iamservice/api/rest/AssignPermissionsToRoleController.java`
- `src/main/java/com/solveria/iamservice/application/orchestration/AssignPermissionsToRoleOrchestrator.java` (ya existe, verificar/completar)
- `src/main/java/com/solveria/iamservice/application/dto/AssignPermissionsToRoleRequest.java` (ya existe, verificar)
- `src/main/java/com/solveria/iamservice/application/dto/AssignPermissionsToRoleResponse.java` (ya existe, verificar)
- `src/main/java/com/solveria/iamservice/api/exception/ErrorCodes.java` (agregar códigos)
- `src/main/resources/i18n/messages_es.properties` (agregar mensajes)
- `src/main/resources/i18n/messages_en.properties` (agregar mensajes)
- `src/main/resources/i18n/messages_pt.properties` (agregar mensajes)
- `src/test/java/com/solveria/iamservice/api/rest/AssignPermissionsToRoleControllerTest.java` (nuevo)
- `src/test/java/com/solveria/iamservice/application/orchestration/AssignPermissionsToRoleOrchestratorTest.java` (nuevo)

## Prompt para Cursor

```
Actúa como un Staff Software Engineer experto en Spring Boot, DDD y Clean Architecture.

Contexto:
- Repo: iam-service (microservicio Spring Boot).
- Core-platform ya tiene: AssignPermissionsToRoleUseCase, AssignPermissionsToRoleCommand, RoleRepositoryPort, PermissionRepositoryPort.
- Ya existe AssignPermissionsToRoleOrchestrator (verificar que esté completo).
- Objetivo: implementar AssignPermissionsToRole completo siguiendo las convenciones del proyecto.

Reglas obligatorias:
1. NO hardcodear mensajes: usar errorCode (keys i18n).
2. Logs con formato estructurado: event=... key=value.
3. Separación: API/Orchestration en iam-service, negocio en core-platform.

Tareas:

1. Controller REST (AssignPermissionsToRoleController):
   - Endpoint: PUT /api/v1/roles/{roleId}/permissions
   - Validación JSR-303 en @RequestBody AssignPermissionsToRoleRequest
   - Validar que roleId del path coincida con roleId del body (si aplica)
   - Inyectar AssignPermissionsToRoleOrchestrator
   - Retornar ResponseEntity<AssignPermissionsToRoleResponse> con HttpStatus.OK
   - Manejar excepciones (delegar a GlobalExceptionHandler)

2. Orchestrator (AssignPermissionsToRoleOrchestrator):
   - Verificar que tenga @Component
   - Verificar que inyecte AssignPermissionsToRoleUseCase
   - Verificar logs estructurados:
     * event=IAM_ASSIGN_PERMISSIONS_REQUEST_RECEIVED roleId={} permissionIdsCount={}
     * event=IAM_ASSIGN_PERMISSIONS_SUCCESS roleId={}
     * event=IAM_ASSIGN_PERMISSIONS_ERROR errorCode={} roleId={}
   - Verificar mapeo Request -> Command
   - Verificar mapeo Role -> Response
   - Verificar manejo de SolverException (relanzar, no envolver)
   - Verificar manejo de Exception genérica (IamServiceException con errorCode)

3. DTOs:
   - AssignPermissionsToRoleRequest: roleId (Long, @NotNull), permissionIds (List<Long>, @NotEmpty)
   - AssignPermissionsToRoleResponse: id (Long), name (String), description (String), permissionIds (List<Long>)

4. ErrorCodes:
   - Agregar: IAM_ASSIGN_PERMISSIONS_FAILED = "IAM_ASSIGN_PERMISSIONS_FAILED"

5. i18n (messages_es.properties, messages_en.properties, messages_pt.properties):
   - IAM_ASSIGN_PERMISSIONS_FAILED={mensaje traducible}
   - validation.permission.ids.required={mensaje}
   - validation.role.id.required={mensaje}

6. Tests:
   - AssignPermissionsToRoleControllerTest: MockMvc, validar request/response, códigos HTTP, validaciones
   - AssignPermissionsToRoleOrchestratorTest: Mock del UseCase, validar mapeos y logs

Entregables:
- Archivos creados/modificados con rutas exactas.
- Tests pasando: mvn test
- Endpoint funcionando: curl PUT /api/v1/roles/{id}/permissions
```

## Checklist de Validación Post-Generación

### Compilación
- [ ] `mvn clean compile` sin errores
- [ ] Todos los imports correctos
- [ ] DTOs validados correctamente

### Tests
- [ ] `mvn test` pasa todos los tests
- [ ] Cobertura mínima: 80%
- [ ] Tests unitarios del Orchestrator
- [ ] Tests de integración del Controller

### Validaciones
- [ ] Request sin permissionIds → 400 Bad Request
- [ ] Request con permissionIds vacío → 400 Bad Request
- [ ] Request con roleId inexistente → 404 Not Found (si el UseCase lanza EntityNotFoundException)
- [ ] Request con permissionIds inexistentes → 400 Bad Request (si el UseCase lanza SolverException)
- [ ] Request válido → 200 OK con body

### Logs
- [ ] Logs en formato `event=IAM_ASSIGN_PERMISSIONS_...`
- [ ] Logs incluyen roleId y permissionIdsCount
- [ ] Logs de error incluyen errorCode

### i18n
- [ ] Mensajes en 3 idiomas (es, en, pt)
- [ ] ErrorCodes resueltos correctamente
- [ ] Mensajes de validación funcionan

### Endpoint
```bash
# Test exitoso
curl -X PUT http://localhost:8080/api/v1/roles/1/permissions \
  -H "Content-Type: application/json" \
  -d '{"roleId":1,"permissionIds":[1,2,3]}'

# Test validación
curl -X PUT http://localhost:8080/api/v1/roles/1/permissions \
  -H "Content-Type: application/json" \
  -d '{"roleId":1,"permissionIds":[]}'
```

## Pitfalls Comunes

1. **No validar que roleId del path coincida con body**: Si el body tiene roleId, debe coincidir con el path parameter.
2. **Falta validar lista vacía**: @NotEmpty en permissionIds es crítico para evitar asignaciones vacías.
3. **No manejar EntityNotFoundException del UseCase**: El UseCase puede lanzar EntityNotFoundException que debe mapearse a 404.
