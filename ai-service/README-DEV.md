# Guía de Desarrollo - AI Service

Esta guía explica cómo arrancar el servicio en modo desarrollo local.

## Requisitos Previos

- Java 21
- Maven (o usar `mvnw.cmd` incluido)
- Docker Desktop (para Postgres)
- PowerShell (Windows)

## Pasos para Arrancar en DEV

### Paso 1: Levantar Postgres (Docker)

```powershell
.\scripts\dev-up.ps1
```

Esto levanta Postgres en el puerto **5433** del host (mapeado desde 5432 del contenedor).

**Verificar que Postgres está corriendo:**
```powershell
docker ps | findstr "ai-service-pg"
```

### Paso 2: Compilar el Proyecto

```powershell
.\scripts\dev-build.ps1
```

Compila todos los módulos sin ejecutar tests.

### Paso 3: Ejecutar el Servicio

```powershell
.\scripts\dev-run.ps1
```

El servicio arranca en el puerto **8081** por defecto.

**Ejecutar en puerto alternativo:**
```powershell
.\scripts\dev-run.ps1 -Port 8082
```

## Verificación

Una vez arrancado, verifica el health endpoint:

```powershell
curl.exe "http://localhost:8081/actuator/health"
```

Deberías ver un JSON con `"status":"UP"` y `"db":{"status":"UP"}`.

## Detener Servicios

**Detener Spring Boot:** `Ctrl+C` en la terminal donde está corriendo.

**Detener Postgres:**
```powershell
.\scripts\dev-down.ps1
```

## Configuración

- **Base de datos:** Postgres en `localhost:5433` (usuario: `postgres`, password: `postgres`)
- **Puerto del servicio:** `8081` (configurable con `-Port` en `dev-run.ps1`)
- **Perfil Spring:** `dev` (usa stubs, no requiere OpenAI API key)

## Troubleshooting

**Error: "No qualifying bean of type LlmChatPort"**
- Verifica que estás usando el perfil `dev`
- El stub `DevLlmChatPortStub` debería estar activo automáticamente

**Error: "CannotGetJdbcConnectionException"**
- Verifica que Postgres está corriendo: `docker ps`
- Verifica el puerto: debe ser `5433` en el host
- Verifica las credenciales en `application-dev.yml`

**Error: "Unable to find a suitable main class"**
- Asegúrate de usar `-pl modules/ai-bootstrap` en el comando Maven
- O usa directamente `.\scripts\dev-run.ps1`
