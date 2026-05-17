# Guia de Desarrollo - BosqueVivo AI MVP 1.0

Workspace modular para el MVP de reporte y gestion de incidentes ambientales.

## Modulos

- `bosquevivo-web/`: frontend React + Vite + TypeScript + Leaflet.
- `bosquevivo-service/`: API principal Spring Boot para incidentes, brigadas, auditoria y workflow.
- `iam-service/`: autenticacion demo, emision de JWT y endpoint `/api/auth/me`.
- `ai-service/`: analisis ambiental deterministico para prioridad, SLA y recomendacion.
- `core-plataform/`: libreria Maven compartida para errores API y correlacion HTTP.

## Puertos Locales

- Frontend: `3000`
- BosqueVivo API: `8082`
- IAM Service: `8089`
- AI Service: `8092` en Docker, `8091` si se ejecuta directo con Spring Boot.
- Swagger UI Docker: `8090`
- PostgreSQL BosqueVivo: `5435`
- PostgreSQL IAM: `5436`

## Flujo De Integracion

1. El frontend inicia sesion contra `iam-service`.
2. IAM devuelve un JWT con rol `ADMIN` o `CITIZEN`.
3. El frontend envia ese token a `bosquevivo-service`.
4. BosqueVivo API valida el JWT y aplica permisos por rol.
5. Al priorizar, BosqueVivo API llama a `ai-service`.
6. `core-plataform` aporta componentes compartidos usados por los servicios Java.

## Comandos Principales

Instalar Core Platform:

```powershell
cd core-plataform
.\mvnw.cmd -q -DskipTests install
cd ..
```

Tests backend:

```powershell
cd bosquevivo-service
mvn test
cd ..
```

Tests IAM:

```powershell
cd iam-service
.\mvnw.cmd test
cd ..
```

Build frontend:

```powershell
cd bosquevivo-web
npm install
npm run lint
npm run build
cd ..
```

Stack completo con Docker:

```powershell
docker compose config
docker compose up --build
```

## Usuarios Demo

```text
admin / admin123               ADMIN
ciudadano / ciudadano123       CITIZEN
```

## Notas

- No guardar secretos reales en `.env` ni en `.env.example`.
- El login vive en `iam-service`; `bosquevivo-service` no debe volver a exponer login local.
- Si cambia `core-plataform`, instala la libreria antes de compilar los servicios que la consumen.
