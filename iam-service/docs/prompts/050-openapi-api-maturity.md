# 050 - OpenAPI API Maturity

## Objetivo

Documentar la API REST con OpenAPI/Swagger siguiendo estándares de madurez enterprise: descripciones, ejemplos, códigos de respuesta, esquemas de error.

## Archivos Objetivo

- `src/main/java/com/solveria/iamservice/config/OpenApiConfig.java` (ya existe, verificar/completar)
- `src/main/java/com/solveria/iamservice/api/rest/RoleController.java` (agregar anotaciones OpenAPI)
- `src/main/java/com/solveria/iamservice/api/rest/AssignPermissionsToRoleController.java` (agregar anotaciones OpenAPI)
- `src/main/java/com/solveria/iamservice/application/dto/CreateRoleRequest.java` (agregar anotaciones OpenAPI)
- `src/main/java/com/solveria/iamservice/application/dto/CreateRoleResponse.java` (agregar anotaciones OpenAPI)
- `src/main/java/com/solveria/iamservice/application/dto/AssignPermissionsToRoleRequest.java` (agregar anotaciones OpenAPI)
- `src/main/java/com/solveria/iamservice/application/dto/AssignPermissionsToRoleResponse.java` (agregar anotaciones OpenAPI)
- `src/main/java/com/solveria/iamservice/api/rest/dto/ErrorResponse.java` (agregar anotaciones OpenAPI)

## Prompt para Cursor

```
Actúa como un Staff Software Engineer experto en Spring Boot, OpenAPI y API Design.

Contexto:
- Repo: iam-service (microservicio Spring Boot).
- Ya existe OpenApiConfig (verificar que esté completo).
- Objetivo: documentar API REST con OpenAPI siguiendo estándares enterprise.

Reglas obligatorias:
1. Documentación completa: descripciones, ejemplos, códigos de respuesta.
2. Esquemas de error documentados.
3. Validaciones documentadas.

Tareas:

1. OpenApiConfig:
   - Verificar @Configuration
   - Configurar OpenAPI con:
     * Info: title, version, description, contact
     * Servers: dev, prod (si aplica)
     * Tags: agrupar endpoints por recurso
   - Configurar path para docs: /v3/api-docs
   - Configurar UI: /swagger-ui.html

2. Controllers (RoleController, AssignPermissionsToRoleController):
   - Agregar @Tag(name = "Roles") para agrupar endpoints
   - Agregar @Operation en cada método:
     * summary: descripción breve
     * description: descripción detallada
     * responses: códigos de respuesta con @ApiResponse
   - Documentar cada código de respuesta:
     * 200/201: éxito
     * 400: validación/error de negocio
     * 404: recurso no encontrado
     * 500: error interno
   - Agregar @Parameter para path variables y query params
   - Agregar @RequestBody con ejemplos

3. DTOs (Request/Response):
   - Agregar @Schema en cada campo:
     * description: descripción del campo
     * example: ejemplo de valor
     * required: si es obligatorio
     * minLength/maxLength: para strings
     * minimum/maximum: para números
   - Agregar @Schema en la clase con description

4. ErrorResponse:
   - Documentar estructura de error
   - Ejemplos de respuestas de error para cada código

5. Validación:
   - Swagger UI accesible: http://localhost:8080/swagger-ui.html
   - Todos los endpoints documentados
   - Ejemplos funcionan en Swagger UI
   - Códigos de respuesta correctos

Entregables:
- Archivos modificados con anotaciones OpenAPI.
- Swagger UI funcionando y completo.
- Ejemplos de request/response en documentación.
```

## Checklist de Validación Post-Generación

### Configuración
- [ ] OpenApiConfig configurado correctamente
- [ ] Info completa (title, version, description)
- [ ] Servers configurados (dev, prod)
- [ ] Tags definidos para agrupar endpoints

### Documentación de Endpoints
- [ ] Todos los endpoints tienen @Operation
- [ ] Descripciones claras y útiles
- [ ] Códigos de respuesta documentados (200, 400, 404, 500)
- [ ] Ejemplos de request/response

### Documentación de DTOs
- [ ] Todos los campos tienen @Schema
- [ ] Descripciones en cada campo
- [ ] Ejemplos de valores
- [ ] Validaciones documentadas (required, minLength, etc.)

### Swagger UI
- [ ] Accesible en http://localhost:8080/swagger-ui.html
- [ ] Todos los endpoints visibles
- [ ] Ejemplos funcionan (Try it out)
- [ ] Respuestas de error documentadas

### Validación Manual
```bash
# Verificar OpenAPI JSON
curl http://localhost:8080/v3/api-docs

# Abrir Swagger UI en navegador
# http://localhost:8080/swagger-ui.html
```

## Pitfalls Comunes

1. **Falta documentar códigos de error**: No solo documentar 200/201, también 400, 404, 500 con ejemplos de ErrorResponse.
2. **Ejemplos no realistas**: Los ejemplos deben ser datos realistas que ayuden a entender el uso de la API.
3. **Falta agrupar endpoints con Tags**: Usar @Tag para agrupar endpoints relacionados (Roles, Permissions, etc.).
