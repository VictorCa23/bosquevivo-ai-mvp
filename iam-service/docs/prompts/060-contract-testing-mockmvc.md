# 060 - Contract Testing con MockMvc

## Objetivo

Implementar tests de contrato usando MockMvc para validar que los endpoints REST cumplen con el contrato esperado (request/response, códigos HTTP, validaciones).

## Archivos Objetivo

- `src/test/java/com/solveria/iamservice/api/rest/RoleControllerTest.java` (nuevo o completar)
- `src/test/java/com/solveria/iamservice/api/rest/AssignPermissionsToRoleControllerTest.java` (nuevo o completar)
- `src/test/resources/application-test.yml` (nuevo, configuración para tests)

## Prompt para Cursor

```
Actúa como un Staff Software Engineer experto en Spring Boot Testing y Contract Testing.

Contexto:
- Repo: iam-service (microservicio Spring Boot).
- Objetivo: implementar tests de contrato con MockMvc para validar endpoints REST.

Reglas obligatorias:
1. Tests deben validar contrato completo: request, response, códigos HTTP.
2. Usar @WebMvcTest para aislar capa web.
3. Mockear Orchestrators, no UseCases directamente.

Tareas:

1. RoleControllerTest:
   - @WebMvcTest(RoleController.class)
   - @AutoConfigureMockMvc
   - Mockear CreateRoleOrchestrator con @MockBean
   - Tests:
     * testCreateRole_Success: POST /api/v1/roles → 201 Created, validar response body
     * testCreateRole_ValidationError: POST con datos inválidos → 400 Bad Request, validar ErrorResponse
     * testCreateRole_ServiceError: Orchestrator lanza excepción → validar código HTTP y ErrorResponse
   - Usar MockMvc para hacer requests
   - Validar JSON response con jsonPath()
   - Validar códigos HTTP con status()

2. AssignPermissionsToRoleControllerTest:
   - @WebMvcTest(AssignPermissionsToRoleController.class)
   - @AutoConfigureMockMvc
   - Mockear AssignPermissionsToRoleOrchestrator con @MockBean
   - Tests:
     * testAssignPermissions_Success: PUT /api/v1/roles/{id}/permissions → 200 OK, validar response
     * testAssignPermissions_ValidationError: PUT con datos inválidos → 400 Bad Request
     * testAssignPermissions_RoleNotFound: Orchestrator lanza EntityNotFoundException → 404 Not Found
     * testAssignPermissions_ServiceError: Orchestrator lanza excepción → validar código HTTP
   - Validar path variables
   - Validar request body
   - Validar response body

3. application-test.yml:
   - Configuración mínima para tests
   - Deshabilitar features no necesarios (actuator, etc.)
   - Configurar logging para tests

4. Validación:
   - Todos los tests pasan: mvn test
   - Cobertura de endpoints: 100%
   - Tests aislados (no dependen de base de datos)

Entregables:
- Archivos de test creados/completados.
- Tests pasando: mvn test
- Cobertura de endpoints validada.
```

## Checklist de Validación Post-Generación

### Compilación
- [ ] `mvn clean compile test-compile` sin errores
- [ ] Todos los imports correctos
- [ ] Dependencias de test correctas

### Tests
- [ ] `mvn test` pasa todos los tests
- [ ] Tests aislados (no dependen de BD)
- [ ] Cobertura de endpoints: 100%

### Casos de Prueba
- [ ] Test éxito: 201/200 con response body válido
- [ ] Test validación: 400 con ErrorResponse
- [ ] Test not found: 404 con ErrorResponse
- [ ] Test error interno: 500 con ErrorResponse

### Validación de Contrato
- [ ] Request body validado (JSON correcto)
- [ ] Response body validado (JSON correcto, campos presentes)
- [ ] Códigos HTTP correctos
- [ ] Headers validados (Content-Type, etc.)

### Ejecución
```bash
# Ejecutar tests
mvn test

# Ejecutar tests específicos
mvn test -Dtest=RoleControllerTest

# Ver cobertura
mvn test jacoco:report
```

## Pitfalls Comunes

1. **Mockear UseCases en lugar de Orchestrators**: En @WebMvcTest, mockear Orchestrators (capa de aplicación), no UseCases (core-platform).
2. **Falta validar estructura completa del response**: No solo validar código HTTP, también validar que el JSON response tenga todos los campos esperados.
3. **Tests dependen de BD**: @WebMvcTest debe aislar la capa web, no debe necesitar base de datos.
