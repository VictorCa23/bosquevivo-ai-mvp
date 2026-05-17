# AI Service

Multi-módulo Maven (Java 21, Spring Boot 3.5.x) con **Clean Architecture** y **Hexagonal** estricta. Spring AI 1.1.2. Mongo y Redis son **opcionales** en ai-infrastructure; la app arranca sin ellos.

## Build y tests

```bash
.\mvnw.cmd -q -B clean test
```

Si `clean` falla por bloqueos de archivos (p. ej. IDE o procesos que usan `target/`), usa `.\mvnw.cmd -q -B test` para validar build y tests; o cierra IDE/procesos y repite `clean test`. Desde **iam-service**: `.\mvnw.cmd -f "ruta\ai-service\pom.xml" -q -B clean test`.

### Si Git trackeaba `target/`

Si en el pasado se versionaron directorios `target/`, quítalos del índice y deja de trackearlos:

```bash
git rm -r --cached modules/**/target
git commit -m "chore: remove build targets from repo"
```

Luego asegúrate de que `.gitignore` incluye `**/target/` y `**/modules/**/target/`.

---

## Diagrama de dependencias entre módulos

```
                    ┌─────────────┐
                    │ ai-domain   │  (sin deps internas)
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ai-application│  (solo ai-domain)
                    └──────┬──────┘
                           │
              ┌────────────┼────────────┐
              │            │            │
       ┌──────▼──────┐     │     ┌──────▼──────┐
       │ ai-api      │     │     │ai-infrastructure│  (ai-domain + ai-application)
       │(solo app)   │     │     └──────┬──────┘
       └──────┬──────┘     │            │
              │            │            │
              └────────────┼────────────┘
                           │
                    ┌──────▼──────┐
                    │ai-bootstrap │  (ai-api + ai-infrastructure)
                    └─────────────┘
```

**Reglas:**
- **ai-domain**: no depende de otros módulos internos.
- **ai-application**: solo **ai-domain**.
- **ai-api**: solo **ai-application** (no infrastructure).
- **ai-infrastructure**: **ai-domain** + **ai-application**.
- **ai-bootstrap**: **ai-api** + **ai-infrastructure**.

---

## Prohibido Spring en ai-domain y ai-application

En **ai-domain** y **ai-application** está **prohibido**:

- Cualquier `import org.springframework.*`
- Anotaciones `@Service`, `@Component`, `@Configuration`, `@Autowired`
- Dependencias `spring-boot-starter-*`

El wiring (beans, use cases, adapters) se hace en **ai-bootstrap** (p. ej. `UseCaseConfig` con `@Bean`).

---

## Spring AI (Paso 2)

**Spring AI se incorpora únicamente en ai-infrastructure y ai-bootstrap en el Paso 2.**  
Ni ai-domain ni ai-application deben depender de Spring AI ni de ningún framework.

---

## Paquetes

- `com.solveria.ai.domain.*` — modelos y políticas.
- `com.solveria.ai.application.port.in` / `port.out` / `service` / `dto` — casos de uso y puertos.
- `com.solveria.ai.infrastructure.*` — adapters (LLM, vector store, cache, etc.).
- `com.solveria.ai.api.*` — controllers, request/response, error, OpenAPI.
- `com.solveria.ai.bootstrap.*` — `@SpringBootApplication`, config, seguridad.

---

## Seguridad

- No hay `permitAll()` global.
- Públicos: `/actuator/health`, `/actuator/info`.
- Swagger (`/v3/api-docs`, `/swagger-ui`) solo en perfil **dev**.
- El resto exige autenticación (HTTP Basic por defecto). JWT Resource Server está preparado (comentado/TODOs) para el Paso de seguridad.

---

## Perfiles

- **test**: Redis/Mongo y SpringDoc desactivados; usado en `mvn test`.
- **dev**: SpringDoc activo, Swagger permitido. Arrancar con `-Dspring-boot.run.profiles=dev`.
