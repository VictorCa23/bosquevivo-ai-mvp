# BosqueVivo AI MVP 1.0

Base minima funcional para reportar incidentes ambientales con backend Spring Boot,
frontend React/Vite y mapa Leaflet.

## Alcance MVP 1.0

Incluye el flujo operativo principal de incidentes:

- Crear un incidente ambiental.
- Editar y eliminar incidentes existentes.
- Seleccionar ubicacion en un mapa Leaflet.
- Guardar `latitude` y `longitude`.
- Registrar severidad: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`.
- Priorizar incidente usando `ai-service` con fallback local deterministico.
- Usar timeouts configurables al invocar `ai-service`.
- Esperar healthcheck real de `ai-service` antes de iniciar el backend en Docker.
- Asignar incidente a una brigada.
- Iniciar atencion en campo.
- Cerrar incidente con checklist minimo y notas.
- Reabrir incidente cerrado.
- Listar incidentes y filtrar por estado, tipo, severidad y busqueda.
- Ver detalle de un incidente.
- Ver incidentes filtrados como marcadores en un mapa general.
- Consultar eventos de auditoria del incidente.
- Ver KPIs operativos simples.
- Operar incidentes con flujo guiado por estado: solo se muestra la siguiente accion valida.
- Cerrar incidentes con checklist visible: area asegurada, riesgo controlado y notas.
- Reabrir incidentes con motivo escrito.
- Usar un panel operativo mas user-friendly con resumen, KPIs y tarjetas escaneables.
- Ver badges visuales de estado, severidad y siguiente accion recomendada.
- Recibir feedback claro de exito o error despues de cada operacion.
- Limpiar filtros rapidamente para volver a la vista general.
- Iniciar sesion contra `iam-service` con usuarios demo y token JWT.
- Aplicar roles basicos: `ADMIN`, `CITIZEN`.
- Proteger endpoints del backend segun rol.
- Ocultar o deshabilitar acciones del frontend segun permisos.
- Integrar `core-plataform/core-platform` como libreria compartida real.
- Usar `CorrelationIdFilter` de Core Platform para trazabilidad HTTP en backend y AI.
- Usar `ApiErrorResponse` y `ApiErrorFactory` de Core Platform para errores API compartidos.
- Exponer `X-Correlation-Id` en respuestas y errores API de los servicios.
- Exponer OpenAPI en backend y Swagger UI en contenedor.
- Exponer OpenAPI de `bosquevivo-service`, `iam-service` y `ai-service` en Swagger UI.
- Levantar API, frontend, `iam-service`, `ai-service` y PostgreSQL con Docker Compose.
- Usar una integracion limpia: IAM autentica, BosqueVivo API opera el dominio,
  AI Service analiza incidentes y Core Platform aporta componentes compartidos.

No incluye todavia: IA generativa real, usuarios persistidos en IAM, multi-tenant real, auditoria avanzada, app movil,
MongoDB, Redis, dashboards avanzados ni mapas operativos complejos.

## Stack

- Backend: Java 21, Spring Boot 3.4.0, Maven.
- IAM Service: Java 21, Spring Boot 3.4.0, Maven.
- AI Service: Java 21, Spring Boot 3.4.0, Maven multi-modulo.
- Core Platform: libreria Maven `com.solveria:core-platform`.
- Frontend: React, Vite, TypeScript, Leaflet.
- Base de datos: PostgreSQL 16.
- Infra local: Docker Compose.
- Documentacion API: `/v3/api-docs` + Swagger UI.

## Estructura

```text
bosquevivo-service/   Backend Spring Boot modular por paquetes
bosquevivo-web/       Frontend web React + Leaflet
ai-service/           Servicio de analisis ambiental MVP 1.0
iam-service/          Servicio de identidad y acceso del MVP 1.0
core-plataform/       Libreria compartida usada por backend y AI
docker-compose.yml    Stack MVP 1.0
.env.example          Variables locales sin secretos reales
```

El backend mantiene una separacion simple:

```text
incidents/
  api/
  application/
  domain/
  infrastructure/
shared/
  config/
```

## Prerrequisitos

- Java 21 o superior.
- Maven 3.9+.
- Node.js 22+ y npm.
- Docker Desktop.
- PowerShell en Windows.

## Variables de Entorno

Usa `.env.example` como plantilla:

```powershell
Copy-Item .env.example .env
```

Valores principales:

```env
BOSQUEVIVO_SERVICE_PORT=8082
BOSQUEVIVO_JWT_SECRET=dev-only-change-this-secret-32-bytes
BOSQUEVIVO_JWT_EXPIRATION_MINUTES=480
BOSQUEVIVO_AI_ENABLED=true
BOSQUEVIVO_AI_BASE_URL=http://localhost:8092
BOSQUEVIVO_AI_CONNECT_TIMEOUT_MS=1000
BOSQUEVIVO_AI_READ_TIMEOUT_MS=2000
AI_SERVICE_PORT=8092
IAM_SERVICE_PORT=8089
BOSQUEVIVO_WEB_PORT=3000
BOSQUEVIVO_DB_PORT=5435
IAM_DB_PORT=5436
BOSQUEVIVO_DB_NAME=bosquevivo
BOSQUEVIVO_DB_USER=postgres
BOSQUEVIVO_DB_PASSWORD=postgres
SWAGGER_UI_PORT=8090
VITE_API_URL=http://localhost:8082
VITE_IAM_URL=http://localhost:8089
```

## Desarrollo Local Sin Docker

Backend:

```powershell
cd core-plataform
.\mvnw.cmd -q -DskipTests install
cd ..
cd bosquevivo-service
mvn clean spring-boot:run
```

AI Service:

```powershell
cd ai-service
.\mvnw.cmd -pl modules/ai-bootstrap -am spring-boot:run
```

IAM Service:

```powershell
cd iam-service
.\mvnw.cmd spring-boot:run
```

Frontend:

```powershell
cd bosquevivo-web
npm install
npm run dev
```

URLs:

- Frontend: `http://localhost:3000`
- API: `http://localhost:8082`
- IAM Service: `http://localhost:8089`
- AI Service: `http://localhost:8091`
- Health: `http://localhost:8082/actuator/health`
- AI Health: `http://localhost:8091/actuator/health`
- OpenAPI JSON: `http://localhost:8082/v3/api-docs`

## Docker

Primero compila el backend para generar el jar que usa el Dockerfile:

```powershell
cd core-plataform
.\mvnw.cmd -q -DskipTests install
cd ..
cd bosquevivo-service
mvn clean package
cd ..
cd iam-service
.\mvnw.cmd -q -DskipTests package
cd ..
cd ai-service
.\mvnw.cmd -q -DskipTests package
cd ..
```

Luego levanta el stack:

```powershell
docker compose config
docker compose up --build
```

Servicios:

- Frontend: `http://localhost:3000`
- API: `http://localhost:8082`
- IAM Service: `http://localhost:8089`
- AI Service: `http://localhost:8092`
- Swagger UI: `http://localhost:8090`
- PostgreSQL: `127.0.0.1:5435`
- IAM PostgreSQL: `127.0.0.1:5436`

Detener:

```powershell
docker compose down
```

Detener y borrar volumen local de base de datos:

```powershell
docker compose down -v
```

## API MVP

Endpoints principales:

```text
POST  http://localhost:8089/api/auth/login
GET   http://localhost:8089/api/auth/me
GET   http://localhost:8082/api/auth/me
POST  /api/incidents
GET   /api/incidents?status=PRIORITIZED&type=SMOKE&severity=HIGH&search=norte
GET   /api/incidents/summary
GET   /api/incidents/{id}
GET   /api/incidents/{id}/events
PUT   /api/incidents/{id}
PATCH /api/incidents/{id}/status
POST  /api/incidents/{id}/prioritize
POST  /api/incidents/{id}/assign
POST  /api/incidents/{id}/start-attention
POST  /api/incidents/{id}/close
POST  /api/incidents/{id}/reopen
DELETE /api/incidents/{id}
GET   /api/brigades
```

Endpoint AI interno del MVP:

```text
POST  http://localhost:8092/api/v1/incidents/analyze
```

Usuarios demo del MVP:

```text
admin / admin123               ADMIN
ciudadano / ciudadano123       CITIZEN
```

`ADMIN` opera el flujo completo: prioriza, asigna brigadas, inicia atencion, cierra,
reabre y elimina incidentes. `CITIZEN` reporta incidentes y consulta el estado del
panel.

Ejemplo de creacion:

```json
{
  "title": "Humo cerca del parque",
  "description": "Se observa humo al norte del area verde.",
  "type": "SMOKE",
  "severity": "HIGH",
  "latitude": -17.7833,
  "longitude": -63.1821
}
```

Tipos permitidos:

```text
FIRE, SMOKE, ILLEGAL_LOGGING, POLLUTION, OTHER
```

Estados permitidos:

```text
CREATED, PRIORITIZED, ASSIGNED, IN_ATTENTION, CLOSED, REOPENED
```

Severidades permitidas:

```text
LOW, MEDIUM, HIGH, CRITICAL
```

Flujo sugerido de demo:

```text
crear -> priorizar -> asignar brigada -> iniciar atencion -> cerrar -> revisar KPI/eventos
```

## Swagger

Los servicios exponen OpenAPI en:

```text
http://localhost:8082/v3/api-docs
http://localhost:8089/v3/api-docs
http://localhost:8092/v3/api-docs
```

Swagger UI corre como contenedor separado para evitar conflictos del starter UI con
Spring Boot/Spring Security:

```text
http://localhost:8090
```

Swagger UI muestra tres definiciones: `BosqueVivo API`, `IAM Service` y `AI Service`.

## IAM Service

El MVP 1.0 deja a `iam-service` como unico punto de login. IAM emite JWT compatibles
con `bosquevivo-service` usando el mismo secreto de desarrollo.

Usuarios demo:

```text
admin / admin123               ADMIN
ciudadano / ciudadano123       CITIZEN
```

El backend ya no expone login local. El frontend inicia sesion contra:

```text
POST http://localhost:8089/api/auth/login
```

## AI Service

El MVP 1.0 mantiene `ai-service` como microservicio separado para analisis ambiental.
Por ahora usa reglas deterministicas, no llamadas a modelos externos. Esto permite
mantener el contrato y la arquitectura listos para conectar IA real mas adelante.

La logica esta separada en politicas:

```text
IncidentRiskPolicy
IncidentSlaPolicy
IncidentRecommendationPolicy
```

Cuando un administrador prioriza un incidente, `bosquevivo-service` llama a:

```text
POST /api/v1/incidents/analyze
```

La respuesta incluye:

```text
priorityScore, slaHours, priorityReason, recommendedAction, model
```

Si `ai-service` no esta disponible o `BOSQUEVIVO_AI_ENABLED=false`, el backend usa
el calculo local anterior como fallback para no bloquear la operacion.

El cliente HTTP del backend usa timeouts configurables:

```env
BOSQUEVIVO_AI_CONNECT_TIMEOUT_MS=1000
BOSQUEVIVO_AI_READ_TIMEOUT_MS=2000
```

## Core Platform

El MVP 1.0 mantiene `bosquevivo-service`, `bosquevivo-web`, `iam-service` y `ai-service` fuera de `core-plataform`.
Core Platform se consume como dependencia Maven para funcionalidades compartidas.

En esta version se usa para:

```text
X-Correlation-Id
ApiErrorResponse
ApiErrorFactory
```

Si el cliente envia ese header, el backend lo conserva. Si no lo envia, Core Platform
genera uno y lo devuelve en cada respuesta. Los errores de `bosquevivo-service` y
`ai-service` ahora usan el mismo formato base compartido.
