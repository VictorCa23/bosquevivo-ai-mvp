# BosqueVivo AI MVP 1.0 - Instrucciones rapidas

## Requisitos

- Docker Desktop instalado y corriendo.
- PowerShell o terminal de VS Code.

No hace falta instalar Java, Maven, Node.js ni PostgreSQL si se corre con Docker.

## Correr el proyecto

Desde esta carpeta:

```powershell
docker compose up --build -d
```

Abrir:

```text
http://localhost:3000
```

## Usuarios demo

```text
admin / admin123
ciudadano / ciudadano123
```

## URLs utiles

```text
Frontend:     http://localhost:3000
Backend API:  http://localhost:8082
IAM Service:  http://localhost:8089
AI Service:   http://localhost:8092
Swagger UI:   http://localhost:8090
```

## Detener

```powershell
docker compose down
```

Para borrar tambien las bases de datos locales creadas por Docker:

```powershell
docker compose down -v
```
