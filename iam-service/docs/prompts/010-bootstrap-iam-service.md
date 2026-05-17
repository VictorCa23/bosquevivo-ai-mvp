# 010 - Bootstrap IAM Service

## Objetivo

Configurar la estructura base del microservicio IAM Service para que compile, arranque y tenga el wiring correcto entre capas.

## Archivos Objetivo

- `pom.xml` (raíz del proyecto)
- `src/main/java/com/solveria/iamservice/IamServiceApplication.java`
- `src/main/java/com/solveria/iamservice/config/UseCaseConfig.java`
- `src/main/resources/application.yml`
- `src/main/resources/application-dev.yml`
- `src/main/resources/application-prod.yml`

## Prompt para Cursor

```
Actúa como un Staff Software Engineer experto en Spring Boot, DDD y Clean Architecture.

Contexto:
- Repo: iam-service (microservicio Spring Boot standalone).
- Existe core-platform (librería separada) que contiene dominio IAM + usecases + ports + adapters JPA.
- Objetivo: dejar iam-service listo para desarrollo enterprise sin reestructurar todo.

Tareas:

1. Verifica que pom.xml tenga:
   - Parent: org.springframework.boot:spring-boot-starter-parent (versión 3.4.0)
   - Dependencias: spring-boot-starter-web, spring-boot-starter-validation, spring-boot-starter-actuator, springdoc-openapi-starter-webmvc-ui, spring-boot-starter-test
   - Dependencia a core-platform con versión configurable (property core.platform.version)
   - Plugin: spring-boot-maven-plugin

2. Verifica que IamServiceApplication tenga:
   - @SpringBootApplication con scanBasePackages que incluya "com.solveria.iamservice" y "com.solveria.core"
   - @EntityScan para escanear entidades JPA de "com.solveria.core.iam.domain.model"
   - @EnableJpaRepositories para repositorios de "com.solveria.core.iam.infrastructure.persistence.repository"

3. Verifica que UseCaseConfig tenga:
   - @Configuration
   - Beans para CreateRoleUseCase y AssignPermissionsToRoleUseCase
   - Inyección correcta de los Ports requeridos (RoleRepositoryPort, PermissionRepositoryPort)

4. Verifica que application.yml tenga configuración básica:
   - server.port
   - spring.application.name
   - spring.jpa (si aplica)
   - springdoc.api-docs.path
   - management.endpoints.web.exposure.include (para actuator)

5. NO modifiques código existente salvo para compilar/arrancar o corregir wiring.

Entregables:
- Archivos verificados/corregidos con rutas exactas.
- Explicación breve del porqué de cada cambio.
```

## Checklist de Validación Post-Generación

### Compilación
- [ ] `mvn clean compile` ejecuta sin errores
- [ ] No hay dependencias faltantes
- [ ] Versiones de Spring Boot y Java son compatibles

### Arranque
- [ ] `mvn spring-boot:run` inicia correctamente
- [ ] No hay errores de wiring de beans
- [ ] Logs muestran "Started IamServiceApplication"

### Configuración
- [ ] `application.yml` tiene todas las propiedades necesarias
- [ ] Actuator endpoints disponibles: `/actuator/health`
- [ ] OpenAPI docs disponibles: `/swagger-ui.html` o `/v3/api-docs`

### Beans
- [ ] `CreateRoleUseCase` bean disponible (verificar con `@Autowired` en test)
- [ ] `AssignPermissionsToRoleUseCase` bean disponible
- [ ] Ports (RoleRepositoryPort, PermissionRepositoryPort) se inyectan correctamente

### Verificación Manual
```bash
# Compilar
mvn clean compile

# Arrancar
mvn spring-boot:run

# Verificar health
curl http://localhost:8080/actuator/health

# Verificar OpenAPI
curl http://localhost:8080/v3/api-docs
```

## Pitfalls Comunes

1. **Falta @EntityScan**: Si las entidades JPA no se escanean, Spring no las reconoce y falla al arrancar.
2. **Falta @EnableJpaRepositories**: Los repositorios JPA no se crean como beans y falla la inyección.
3. **Versión incorrecta de core-platform**: Verificar que la versión en `pom.xml` coincida con la versión instalada en el repositorio Maven local/remoto.
