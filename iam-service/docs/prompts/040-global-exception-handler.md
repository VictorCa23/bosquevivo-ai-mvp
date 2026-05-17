# 040 - Global Exception Handler

## Objetivo

Implementar manejo global de excepciones con i18n, códigos de error estructurados y respuestas consistentes.

## Archivos Objetivo

- `src/main/java/com/solveria/iamservice/api/rest/GlobalRestExceptionHandler.java` (ya existe, verificar/completar)
- `src/main/java/com/solveria/iamservice/api/exception/GlobalExceptionHandler.java` (ya existe, verificar si se usa)
- `src/main/java/com/solveria/iamservice/api/rest/dto/ErrorResponse.java` (ya existe, verificar)
- `src/main/java/com/solveria/iamservice/api/exception/dto/ApiErrorResponse.java` (ya existe, verificar)
- `src/main/java/com/solveria/iamservice/api/exception/ErrorCodes.java` (completar)
- `src/main/java/com/solveria/iamservice/config/I18nConfig.java` (ya existe, verificar)
- `src/main/resources/i18n/messages_es.properties` (completar)
- `src/main/resources/i18n/messages_en.properties` (completar)
- `src/main/resources/i18n/messages_pt.properties` (completar)

## Prompt para Cursor

```
Actúa como un Staff Software Engineer experto en Spring Boot, DDD y Clean Architecture.

Contexto:
- Repo: iam-service (microservicio Spring Boot).
- Ya existe GlobalRestExceptionHandler (verificar que esté completo).
- Objetivo: asegurar manejo global de excepciones con i18n y respuestas consistentes.

Reglas obligatorias:
1. NO hardcodear mensajes: usar errorCode (keys i18n) y resolver con MessageSource.
2. Logs con formato estructurado: event=... errorCode={} status={} path={}.
3. Respuestas consistentes: siempre incluir errorCode, timestamp, path.

Tareas:

1. GlobalRestExceptionHandler:
   - Verificar @RestControllerAdvice
   - Verificar inyección de MessageSource
   - Manejar excepciones:
     * EntityNotFoundException → 404 NOT_FOUND
     * PermissionDeniedException → 403 FORBIDDEN
     * BusinessRuleViolationException → 409 CONFLICT
     * DomainException → 400 BAD_REQUEST
     * ApplicationException → 400 BAD_REQUEST
     * SolverException (genérico) → 400 BAD_REQUEST
     * IamServiceException → 500 INTERNAL_SERVER_ERROR
     * MethodArgumentNotValidException → 400 BAD_REQUEST (validaciones)
     * Exception (genérico) → 500 INTERNAL_SERVER_ERROR
   - Para cada excepción:
     * Log estructurado: event=IAM_ERROR errorCode={} status={} path={}
     * Resolver mensaje con MessageSource usando errorCode
     * Retornar ErrorResponse con errorCode, message (i18n), timestamp, path

2. ErrorResponse DTO:
   - Verificar campos: errorCode (String), message (String, i18n), timestamp (Instant), path (String)
   - Verificar que sea inmutable (record o clase final)

3. ErrorCodes:
   - Completar con todos los códigos usados:
     * VALIDATION_ERROR
     * UNEXPECTED_ERROR
     * IAM_ROLE_CREATE_FAILED
     * IAM_ASSIGN_PERMISSIONS_FAILED
     * (otros según necesidad)

4. I18nConfig:
   - Verificar @Configuration
   - Verificar MessageSource bean configurado
   - Basename: "i18n/messages"
   - Encoding: UTF-8
   - Fallback: false (no usar system locale)

5. i18n (messages_es.properties, messages_en.properties, messages_pt.properties):
   - Completar todos los errorCodes usados
   - Formato: {ERROR_CODE}={Mensaje traducible}
   - Con parámetros: {ERROR_CODE}={Mensaje con {parametro}}

6. Validación:
   - Todos los errorCodes tienen mensajes en los 3 idiomas
   - Mensajes se resuelven correctamente según Accept-Language header
   - Fallback a inglés si no hay mensaje en el idioma solicitado

Entregables:
- Archivos verificados/completados con rutas exactas.
- Tests pasando: mvn test
- Endpoint con error retorna respuesta i18n correcta.
```

## Checklist de Validación Post-Generación

### Compilación
- [ ] `mvn clean compile` sin errores
- [ ] Todos los @ExceptionHandler funcionan
- [ ] MessageSource se inyecta correctamente

### Tests
- [ ] `mvn test` pasa todos los tests
- [ ] Tests de exception handlers
- [ ] Tests de i18n (diferentes locales)

### Manejo de Excepciones
- [ ] EntityNotFoundException → 404 con errorCode correcto
- [ ] SolverException → 400 con errorCode y mensaje i18n
- [ ] MethodArgumentNotValidException → 400 con detalles de validación
- [ ] Exception genérica → 500 con UNEXPECTED_ERROR

### i18n
- [ ] Mensajes en 3 idiomas (es, en, pt)
- [ ] Resolución según Accept-Language header
- [ ] Fallback a inglés si no hay mensaje
- [ ] Parámetros en mensajes funcionan ({id}, {entity}, etc.)

### Logs
- [ ] Logs en formato `event=IAM_ERROR errorCode={} status={} path={}`
- [ ] Todos los errores se loguean antes de retornar respuesta
- [ ] Logs incluyen stack trace para debugging

### Respuestas
- [ ] Todas las respuestas tienen estructura consistente
- [ ] errorCode siempre presente
- [ ] message siempre presente (i18n)
- [ ] timestamp siempre presente
- [ ] path siempre presente

### Validación Manual
```bash
# Test con error (debe retornar mensaje en español)
curl -X POST http://localhost:8080/api/v1/roles \
  -H "Content-Type: application/json" \
  -H "Accept-Language: es" \
  -d '{"name":""}'

# Test con error (debe retornar mensaje en inglés)
curl -X POST http://localhost:8080/api/v1/roles \
  -H "Content-Type: application/json" \
  -H "Accept-Language: en" \
  -d '{"name":""}'
```

## Pitfalls Comunes

1. **Hardcodear mensajes en exception handler**: ❌ `"Error: " + ex.getMessage()` → ✅ `messageSource.getMessage(ex.getCode(), args, locale)`
2. **Falta fallback de idioma**: Si no hay mensaje en el idioma solicitado, debe fallback a inglés, no retornar el errorCode.
3. **No loguear antes de retornar**: Siempre loguear el error con formato estructurado antes de construir la respuesta.
