# Master Runbook - IAM Service

Este runbook guía el desarrollo completo del microservicio IAM Service siguiendo un orden lógico y validando cada fase antes de continuar.

## 🖥️ Cursor Workspace

### Configurar Workspace Multi-Repositorio

1. **Abrir Cursor:**
   - Abrir Cursor IDE
   - File → Open Folder → Seleccionar `C:\Tito\Hibrido\proy\Cursor_Project`

2. **Agregar repositorios al workspace:**
   - File → Add Folder to Workspace
   - Agregar: `C:\SolverIA\core-plataform`
   - Agregar: `C:\SolverIA\iam-service`
   - Guardar workspace: File → Save Workspace As...

3. **Buscar archivos rápidamente:**
   - `Ctrl+Shift+F` (Find in Files): Buscar texto en todos los archivos
   - `Ctrl+P` (Quick Open): Buscar archivos por nombre
   - `Ctrl+Shift+E` (Explorer): Navegar estructura de archivos

4. **Navegación entre repos:**
   - Usar el Explorer sidebar para cambiar entre repositorios
   - Los archivos generados aparecerán en el árbol de archivos del workspace

---

## 🔐 Pruebas con JWT Habilitado

**Importante:** Si la seguridad JWT está habilitada, los endpoints `/api/**` requerirán autenticación.

### Obtener Token JWT

Antes de probar endpoints, necesitas obtener un token JWT válido. El proceso depende de tu configuración de seguridad:

1. **Endpoint de autenticación** (ejemplo típico):
   ```powershell
   # Ejemplo de login (ajustar según tu implementación)
   $response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/login" `
     -Method POST `
     -ContentType "application/json" `
     -Body '{"username":"user","password":"pass"}'
   $token = $response.token
   ```

2. **Guardar token en variable:**
   ```powershell
   $env:JWT_TOKEN = "<JWT_TOKEN>"
   ```

### Ejemplos de Requests con JWT

Todos los ejemplos de `curl` en este runbook deben incluir el header `Authorization` si JWT está habilitado:

```powershell
# Ejemplo con token (reemplazar <JWT_TOKEN> con token real)
curl.exe -X POST http://localhost:8080/api/v1/iam/roles `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer <JWT_TOKEN>" `
  -d '{\"name\":\"Admin\",\"description\":\"Administrator role\"}'
```

**Nota opcional:** Si existe un modo de desarrollo sin autenticación (configurable en `application-dev.yml`), puedes omitir el header `Authorization` en ese modo. Verificar configuración de seguridad antes de probar endpoints.

---

## 📋 Fases de Desarrollo

### Fase 0: Preparación
**Objetivo:** Leer convenciones y preparar el entorno.

1. **Leer convenciones:**
   - Abrir y leer completamente: `docs/prompts/000-conventions.md`

2. **Verificar entorno:**
   ```powershell
   # Verificar Java 21
   java -version

   # Verificar Maven (si está instalado globalmente)
   mvn -version

   # Verificar Maven Wrapper (recomendado)
   if (Test-Path ".\mvnw.cmd") {
       Write-Host "Maven Wrapper encontrado"
   } else {
       Write-Host "Maven Wrapper no encontrado. Agregar Maven Wrapper:"
       Write-Host "  mvn wrapper:wrapper"
   }
   ```

3. **Verificar dependencias:**
   ```powershell
   # Verificar que core-platform esté instalado en repositorio Maven local
   if (Test-Path ".\mvnw.cmd") {
       .\mvnw.cmd dependency:tree | Select-String "core-platform"
   } else {
       mvn dependency:tree | Select-String "core-platform"
   }
   ```

4. **Clonar/actualizar repositorio:**
   ```powershell
   git status
   git pull origin main
   ```

**✅ Checkpoint:** Entorno listo, convenciones leídas.

---

### Fase 1: Bootstrap (010)
**Objetivo:** Configurar estructura base del servicio.

1. **Usar prompt:**
   - Abrir: `docs/prompts/010-bootstrap-iam-service.md`
   - Copiar el bloque "Prompt para Cursor"
   - Pegar en Cursor AI
   - Revisar archivos generados/modificados

2. **Validar compilación:**
   ```powershell
   if (Test-Path ".\mvnw.cmd") {
       .\mvnw.cmd clean compile
   } else {
       mvn clean compile
   }
   ```
   - ✅ Debe compilar sin errores

3. **Validar arranque:**
   ```powershell
   if (Test-Path ".\mvnw.cmd") {
       .\mvnw.cmd spring-boot:run
   } else {
       mvn spring-boot:run
   }
   ```
   - ✅ Debe arrancar sin errores
   - ✅ Logs muestran "Started IamServiceApplication"
   - ✅ Actuator disponible: `Invoke-RestMethod -Uri "http://localhost:8080/actuator/health"`

4. **Validar OpenAPI:**
   ```powershell
   Invoke-RestMethod -Uri "http://localhost:8080/v3/api-docs" | ConvertTo-Json
   ```
   - ✅ Retorna JSON de OpenAPI

5. **Commit:**
   ```powershell
   git add .
   git commit -m "feat: bootstrap iam-service structure"
   ```

**✅ Checkpoint:** Servicio arranca, estructura base lista.

---

### Fase 2: Create Role (020)
**Objetivo:** Implementar caso de uso CreateRole completo.

1. **Usar prompt:**
   - Abrir: `docs/prompts/020-create-role.md`
   - Copiar el bloque "Prompt para Cursor"
   - Pegar en Cursor AI
   - Revisar archivos generados/modificados

2. **Validar compilación:**
   ```powershell
   if (Test-Path ".\mvnw.cmd") {
       .\mvnw.cmd clean compile
   } else {
       mvn clean compile
   }
   ```
   - ✅ Debe compilar sin errores

3. **Validar tests:**
   ```powershell
   if (Test-Path ".\mvnw.cmd") {
       .\mvnw.cmd test
   } else {
       mvn test
   }
   ```
   - ✅ Todos los tests pasan
   - 💡 **Recomendación:** Apuntar a cobertura mínima del 80% (verificar con herramientas de cobertura)

4. **Validar endpoint:**
   ```powershell
   # Test exitoso (ajustar si JWT está habilitado)
   $headers = @{
       "Content-Type" = "application/json"
   }
   # Si JWT está habilitado, agregar: "Authorization" = "Bearer $env:JWT_TOKEN"
   
   $body = @{
       name = "Admin"
       description = "Administrator role"
   } | ConvertTo-Json

   Invoke-RestMethod -Uri "http://localhost:8080/api/v1/iam/roles" `
     -Method POST `
     -Headers $headers `
     -Body $body

   # Test validación
   $invalidBody = @{
       name = ""
   } | ConvertTo-Json

   try {
       Invoke-RestMethod -Uri "http://localhost:8080/api/v1/iam/roles" `
         -Method POST `
         -Headers $headers `
         -Body $invalidBody
   } catch {
       Write-Host "Error esperado: $_"
   }
   ```
   - ✅ Request válido → 201 Created
   - ✅ Request inválido → 400 Bad Request con ErrorResponse

5. **Validar logs:**
   - ✅ Logs en formato `event=IAM_ROLE_CREATE_...`
   - ✅ Logs incluyen parámetros relevantes

6. **Validar i18n:**
   ```powershell
   # Test con Accept-Language
   $headers = @{
       "Content-Type" = "application/json"
       "Accept-Language" = "es"
   }
   # Si JWT está habilitado, agregar: "Authorization" = "Bearer $env:JWT_TOKEN"

   $body = @{
       name = ""
   } | ConvertTo-Json

   try {
       Invoke-RestMethod -Uri "http://localhost:8080/api/v1/iam/roles" `
         -Method POST `
         -Headers $headers `
         -Body $body
   } catch {
       Write-Host "Error esperado: $_"
   }
   ```
   - ✅ Mensajes en español
   - ✅ Mensajes en inglés (Accept-Language: en)
   - ✅ Mensajes en portugués (Accept-Language: pt)

7. **Commit:**
   ```powershell
   git add .
   git commit -m "feat: implement create role use case"
   ```

**✅ Checkpoint:** CreateRole funcionando, tests pasando, i18n funcionando.

---

### Fase 3: Assign Permissions to Role (030)
**Objetivo:** Implementar caso de uso AssignPermissionsToRole completo.

1. **Usar prompt:**
   - Abrir: `docs/prompts/030-assign-permissions-to-role.md`
   - Copiar el bloque "Prompt para Cursor"
   - Pegar en Cursor AI
   - Revisar archivos generados/modificados

2. **Validar compilación:**
   ```powershell
   if (Test-Path ".\mvnw.cmd") {
       .\mvnw.cmd clean compile
   } else {
       mvn clean compile
   }
   ```

3. **Validar tests:**
   ```powershell
   if (Test-Path ".\mvnw.cmd") {
       .\mvnw.cmd test
   } else {
       mvn test
   }
   ```
   - ✅ Todos los tests pasan

4. **Validar endpoint:**
   ```powershell
   # Test exitoso (asumiendo que existe roleId=1)
   $headers = @{
       "Content-Type" = "application/json"
   }
   # Si JWT está habilitado, agregar: "Authorization" = "Bearer $env:JWT_TOKEN"

   $body = @{
       permissionIds = @(1, 2, 3)
   } | ConvertTo-Json

   Invoke-RestMethod -Uri "http://localhost:8080/api/v1/iam/roles/1/permissions" `
     -Method PUT `
     -Headers $headers `
     -Body $body

   # Test validación
   $invalidBody = @{
       permissionIds = @()
   } | ConvertTo-Json

   try {
       Invoke-RestMethod -Uri "http://localhost:8080/api/v1/iam/roles/1/permissions" `
         -Method PUT `
         -Headers $headers `
         -Body $invalidBody
   } catch {
       Write-Host "Error esperado: $_"
   }
   ```
   - ✅ Request válido → 200 OK
   - ✅ Request inválido → 400 Bad Request

5. **Validar logs:**
   - ✅ Logs en formato `event=IAM_ASSIGN_PERMISSIONS_...`

6. **Commit:**
   ```powershell
   git add .
   git commit -m "feat: implement assign permissions to role use case"
   ```

**✅ Checkpoint:** AssignPermissionsToRole funcionando, tests pasando.

---

### Fase 4: Global Exception Handler (040)
**Objetivo:** Asegurar manejo global de excepciones con i18n.

1. **Usar prompt:**
   - Abrir: `docs/prompts/040-global-exception-handler.md`
   - Copiar el bloque "Prompt para Cursor"
   - Pegar en Cursor AI
   - Revisar archivos generados/modificados

2. **Validar compilación:**
   ```powershell
   if (Test-Path ".\mvnw.cmd") {
       .\mvnw.cmd clean compile
   } else {
       mvn clean compile
   }
   ```

3. **Validar tests:**
   ```powershell
   if (Test-Path ".\mvnw.cmd") {
       .\mvnw.cmd test
   } else {
       mvn test
   }
   ```
   - ✅ Tests de exception handlers pasan

4. **Validar manejo de excepciones:**
   ```powershell
   # Test diferentes tipos de error
   # EntityNotFoundException → 404
   # SolverException → 400
   # Exception genérica → 500
   ```
   - ✅ Todos los tipos de excepción se manejan correctamente
   - ✅ Respuestas tienen estructura consistente
   - ✅ Mensajes i18n funcionan

5. **Validar logs:**
   - ✅ Todos los errores se loguean con formato estructurado

6. **Commit:**
   ```powershell
   git add .
   git commit -m "feat: enhance global exception handler with i18n"
   ```

**✅ Checkpoint:** Exception handling completo, i18n funcionando.

---

### Fase 5: OpenAPI API Maturity (050)
**Objetivo:** Documentar API REST con OpenAPI.

1. **Usar prompt:**
   - Abrir: `docs/prompts/050-openapi-api-maturity.md`
   - Copiar el bloque "Prompt para Cursor"
   - Pegar en Cursor AI
   - Revisar archivos generados/modificados

2. **Validar compilación:**
   ```powershell
   if (Test-Path ".\mvnw.cmd") {
       .\mvnw.cmd clean compile
   } else {
       mvn clean compile
   }
   ```

3. **Validar Swagger UI:**
   ```powershell
   # Arrancar servicio
   if (Test-Path ".\mvnw.cmd") {
       .\mvnw.cmd spring-boot:run
   } else {
       mvn spring-boot:run
   }

   # Abrir en navegador
   # http://localhost:8080/swagger-ui.html
   ```
   - ✅ Swagger UI accesible
   - ✅ Todos los endpoints documentados
   - ✅ Ejemplos funcionan (Try it out)
   - ✅ Códigos de error documentados

4. **Validar OpenAPI JSON:**
   ```powershell
   $apiDocs = Invoke-RestMethod -Uri "http://localhost:8080/v3/api-docs"
   $apiDocs | ConvertTo-Json -Depth 10
   ```
   - ✅ JSON válido
   - ✅ Todos los endpoints presentes
   - ✅ Esquemas completos

5. **Commit:**
   ```powershell
   git add .
   git commit -m "feat: add OpenAPI documentation"
   ```

**✅ Checkpoint:** API documentada, Swagger UI funcionando.

---

### Fase 6: Contract Testing MockMvc (060)
**Objetivo:** Implementar tests de contrato con MockMvc.

1. **Usar prompt:**
   - Abrir: `docs/prompts/060-contract-testing-mockmvc.md`
   - Copiar el bloque "Prompt para Cursor"
   - Pegar en Cursor AI
   - Revisar archivos generados/modificados

2. **Validar compilación:**
   ```powershell
   if (Test-Path ".\mvnw.cmd") {
       .\mvnw.cmd clean compile test-compile
   } else {
       mvn clean compile test-compile
   }
   ```

3. **Validar tests:**
   ```powershell
   if (Test-Path ".\mvnw.cmd") {
       .\mvnw.cmd test
   } else {
       mvn test
   }
   ```
   - ✅ Todos los tests pasan
   - ✅ Cobertura de endpoints: 100%

4. **Validar tests aislados:**
   - ✅ Tests no dependen de BD
   - ✅ Tests son rápidos (< 1 segundo cada uno)

5. **Commit:**
   ```powershell
   git add .
   git commit -m "feat: add contract tests with MockMvc"
   ```

**✅ Checkpoint:** Contract tests funcionando, cobertura completa.

---

### Fase 7: Pact Provider (070)
**Objetivo:** Implementar tests de contrato con Pact (Provider).

1. **Usar prompt:**
   - Abrir: `docs/prompts/070-pact-provider.md`
   - Copiar el bloque "Prompt para Cursor"
   - Pegar en Cursor AI
   - Revisar archivos generados/modificados

2. **Validar dependencias:**
   ```powershell
   if (Test-Path ".\mvnw.cmd") {
       .\mvnw.cmd dependency:tree | Select-String "pact"
   } else {
       mvn dependency:tree | Select-String "pact"
   }
   ```
   - ✅ Dependencias de Pact presentes

3. **Validar tests:**
   ```powershell
   if (Test-Path ".\mvnw.cmd") {
       .\mvnw.cmd test -Dtest=*ProviderPactTest
   } else {
       mvn test -Dtest=*ProviderPactTest
   }
   ```
   - ✅ Tests de Pact pasan
   - ✅ Pacts se validan correctamente

4. **Validar pact files:**
   ```powershell
   Get-ChildItem -Path "src\test\resources\pacts\" -File
   ```
   - ✅ Pact files existen
   - ✅ Estados coinciden con @State methods

5. **Commit:**
   ```powershell
   git add .
   git commit -m "feat: add Pact provider tests"
   ```

**✅ Checkpoint:** Pact tests funcionando, contratos validados.

---

## 🔄 Cómo Pedir a Cursor que Cambie Solo lo Mínimo

Cuando necesites modificar código existente, usa este patrón:

```
Actúa como un Staff Software Engineer experto en Spring Boot.

Contexto:
- Repo: iam-service (microservicio Spring Boot).
- Ya existe {ARCHIVO} con funcionalidad {FUNCIONALIDAD}.

Objetivo:
- {OBJETIVO_ESPECÍFICO}

Restricciones:
- NO modificar código que no esté relacionado con {OBJETIVO_ESPECÍFICO}
- NO reestructurar archivos existentes
- Solo cambiar lo mínimo necesario para cumplir {OBJETIVO_ESPECÍFICO}

Tareas:
1. {TAREA_1}
2. {TAREA_2}

Entregables:
- Archivos modificados con rutas exactas.
- Explicación de cambios realizados.
```

**Ejemplo:**
```
Actúa como un Staff Software Engineer experto en Spring Boot.

Contexto:
- Repo: iam-service (microservicio Spring Boot).
- Ya existe RoleController con endpoint POST /api/v1/iam/roles.

Objetivo:
- Agregar endpoint GET /api/v1/iam/roles/{id} para obtener un rol por ID.

Restricciones:
- NO modificar el endpoint POST existente
- NO modificar otros archivos
- Solo agregar el nuevo endpoint GET

Tareas:
1. Agregar método GET en RoleController
2. Agregar método en CreateRoleOrchestrator para obtener rol
3. Agregar test para el nuevo endpoint

Entregables:
- RoleController con nuevo método GET
- Test del nuevo endpoint
```

---

## ✅ Validación Final

Antes de considerar el desarrollo completo, ejecutar:

```powershell
# Compilación
if (Test-Path ".\mvnw.cmd") {
    .\mvnw.cmd clean compile
} else {
    mvn clean compile
}

# Tests
if (Test-Path ".\mvnw.cmd") {
    .\mvnw.cmd test
} else {
    mvn test
}

# Arranque
if (Test-Path ".\mvnw.cmd") {
    .\mvnw.cmd spring-boot:run
} else {
    mvn spring-boot:run
}

# Verificar endpoints
Invoke-RestMethod -Uri "http://localhost:8080/actuator/health"
Invoke-RestMethod -Uri "http://localhost:8080/v3/api-docs" | ConvertTo-Json

# Verificar Swagger UI
# Abrir en navegador: http://localhost:8080/swagger-ui.html
```

**Checklist final:**
- [ ] Servicio compila sin errores
- [ ] Todos los tests pasan
- [ ] Servicio arranca correctamente
- [ ] Endpoints funcionan (CreateRole, AssignPermissions)
- [ ] Exception handling funciona con i18n
- [ ] Swagger UI documentado
- [ ] Contract tests pasan
- [ ] Pact tests pasan
- [ ] Logs en formato estructurado
- [ ] Mensajes i18n funcionan

---

## 📚 Referencias

- Convenciones: `docs/prompts/000-conventions.md`
- Prompts individuales: `docs/prompts/010-*.md` a `docs/prompts/070-*.md`
- Checklist de desarrollo: `docs/runbooks/DEV-CHECKLIST.md`
- Checklist de release: `docs/runbooks/RELEASE-CHECKLIST.md`
