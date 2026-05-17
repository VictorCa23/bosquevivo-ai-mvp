# 020 - Create Role

## Objetivo

Implementar el caso de uso CreateRole completo: Controller REST, Orchestrator, DTOs, validaciones, manejo de errores y tests.

## Archivos Objetivo

- `src/main/java/com/solveria/iamservice/api/rest/RoleController.java`
- `src/main/java/com/solveria/iamservice/application/orchestration/CreateRoleOrchestrator.java`
- `src/main/java/com/solveria/iamservice/application/dto/CreateRoleRequest.java`
- `src/main/java/com/solveria/iamservice/application/dto/CreateRoleResponse.java`
- `src/main/java/com/solveria/iamservice/api/exception/ErrorCodes.java` (agregar códigos)
- `src/main/resources/i18n/messages_es.properties` (agregar mensajes)
- `src/main/resources/i18n/messages_en.properties` (agregar mensajes)
- `src/main/resources/i18n/messages_pt.properties` (agregar mensajes)
- `src/test/java/com/solveria/iamservice/api/rest/RoleControllerTest.java` (nuevo)
- `src/test/java/com/solveria/iamservice/application/orchestration/CreateRoleOrchestratorTest.java` (nuevo)

## Prompt para Cursor

```
Actúa como un Staff Software Engineer experto en Spring Boot, DDD y Clean Architecture.

Contexto:
- Repo: iam-service (microservicio Spring Boot).
- Core-platform ya tiene: CreateRoleUseCase, CreateRoleCommand, RoleRepositoryPort.
- Objetivo: implementar CreateRole completo siguiendo las convenciones del proyecto.

Reglas obligatorias:
1. NO hardcodear mensajes: usar errorCode (keys i18n).
2. Logs con formato estructurado: event=... key=value.
3. Separación: API/Orchestration en iam-service, negocio en core-platform.

Tareas:

1. Controller REST (RoleController):
   - Endpoint: POST /api/v1/roles
   - Validación JSR-303 en @RequestBody CreateRoleRequest
   - Inyectar CreateRoleOrchestrator
   - Retornar ResponseEntity<CreateRoleResponse> con HttpStatus.CREATED
   - Manejar excepciones (delegar a GlobalExceptionHandler)

2. Orchestrator (CreateRoleOrchestrator):
   - @Component
   - Inyectar CreateRoleUseCase
   - Método execute(CreateRoleRequest) -> CreateRoleResponse
   - Logs estructurados:
     * event=IAM_ROLE_CREATE_REQUEST_RECEIVED name={} description={}
     * event=IAM_ROLE_CREATE_SUCCESS roleId={} name={}
     * event=IAM_ROLE_CREATE_ERROR errorCode={} name={}
   - Mapear CreateRoleRequest -> CreateRoleCommand
   - Mapear Role (del use case) -> CreateRoleResponse
   - Capturar SolverException y relanzar (no envolver)
   - Capturar Exception genérica y lanzar IamServiceException con errorCode

3. DTOs:
   - CreateRoleRequest: name (String, @NotBlank, @Size), description (String, @Size)
   - CreateRoleResponse: id (Long), name (String), description (String), createdAt (Instant)

4. ErrorCodes:
   - Agregar: IAM_ROLE_CREATE_FAILED = "IAM_ROLE_CREATE_FAILED"

5. i18n (messages_es.properties, messages_en.properties, messages_pt.properties):
   - IAM_ROLE_CREATE_FAILED={mensaje traducible}
   - validation.role.name.required={mensaje}
   - validation.role.name.size={mensaje}
   - validation.role.description.size={mensaje}

6. Tests:
   - RoleControllerTest: MockMvc, validar request/response, códigos HTTP
   - CreateRoleOrchestratorTest: Mock del UseCase, validar mapeos y logs

Entregables:
- Archivos creados/modificados con rutas exactas.
- Tests pasando: mvn test
- Endpoint funcionando: curl POST /api/v1/roles
```

## Checklist de Validación Post-Generación

### Compilación
- [ ] `mvn clean compile` sin errores
- [ ] Todos los imports correctos
- [ ] DTOs son records o clases inmutables

### Tests
- [ ] `mvn test` pasa todos los tests
- [ ] Cobertura mínima: 80%
- [ ] Tests unitarios del Orchestrator
- [ ] Tests de integración del Controller

### Validaciones
- [ ] Request sin name → 400 Bad Request
- [ ] Request con name muy corto/largo → 400 Bad Request
- [ ] Request válido → 201 Created con body

### Logs
- [ ] Logs en formato `event=IAM_ROLE_CREATE_...`
- [ ] Logs incluyen parámetros relevantes (name, roleId)
- [ ] Logs de error incluyen errorCode

### i18n
- [ ] Mensajes en 3 idiomas (es, en, pt)
- [ ] ErrorCodes resueltos correctamente
- [ ] Mensajes de validación funcionan

### Endpoint
```bash
# Test exitoso
curl -X POST http://localhost:8080/api/v1/roles \
  -H "Content-Type: application/json" \
  -d '{"name":"Admin","description":"Administrator role"}'

# Test validación
curl -X POST http://localhost:8080/api/v1/roles \
  -H "Content-Type: application/json" \
  -d '{"name":""}'
```

## Pitfalls Comunes

1. **Hardcodear mensajes en excepciones**: ❌ `throw new Exception("Error al crear rol")` → ✅ `throw new SolverException("IAM_ROLE_CREATE_FAILED")`
2. **Falta mapeo Request->Command**: El Orchestrator debe convertir CreateRoleRequest a CreateRoleCommand antes de llamar al UseCase.
3. **Logs sin estructura**: ❌ `log.info("Creating role")` → ✅ `log.info("event=IAM_ROLE_CREATE_REQUEST_RECEIVED name={}", name)`
