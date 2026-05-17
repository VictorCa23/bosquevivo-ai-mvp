# Release Checklist - IAM Service

Checklist completo para preparar una release del microservicio IAM Service.

## 📋 Pre-Release Checklist

### 1. Código
- [ ] Todas las features completadas y probadas
- [ ] Todos los bugs críticos resueltos
- [ ] Código revisado (code review)
- [ ] No hay código comentado innecesario
- [ ] No hay TODOs pendientes

### 2. Tests
- [ ] Todos los tests pasan: `mvn test`
- [ ] Cobertura mínima: 80%
- [ ] Tests de integración pasan
- [ ] Contract tests pasan (MockMvc)
- [ ] Pact provider tests pasan (si aplica)
- [ ] Tests de performance (si aplica)

### 3. Documentación
- [ ] OpenAPI/Swagger actualizado y completo
- [ ] README actualizado
- [ ] CHANGELOG actualizado
- [ ] ADRs actualizados (si hay decisiones arquitectónicas nuevas)
- [ ] Documentación de API en `docs/api/` (si aplica)

### 4. Configuración
- [ ] `application.yml` configurado correctamente
- [ ] `application-dev.yml` configurado
- [ ] `application-prod.yml` configurado
- [ ] Variables de entorno documentadas
- [ ] Secrets no hardcodeados

### 5. Dependencias
- [ ] `pom.xml` con versiones correctas
- [ ] Dependencias actualizadas (sin vulnerabilidades conocidas)
- [ ] `core-platform` con versión correcta
- [ ] No hay dependencias conflictivas

### 6. Logging y Observabilidad
- [ ] Logs en formato estructurado: `event=... key=value`
- [ ] Logs configurados para producción (nivel, formato)
- [ ] Métricas de Actuator configuradas
- [ ] Health checks funcionando

### 7. i18n
- [ ] Mensajes en los 3 idiomas (es, en, pt)
- [ ] Todos los errorCodes tienen mensajes
- [ ] Mensajes probados con diferentes locales

## 🏷️ Versionado

### Verificar Versión
- [ ] Versión en `pom.xml` correcta
- [ ] Versión sigue semántica (MAJOR.MINOR.PATCH)
- [ ] CHANGELOG actualizado con versión

### Git Tags
```bash
# Crear tag
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

- [ ] Tag creado
- [ ] Tag pusheado al repositorio remoto

## 🧪 Testing de Release

### Tests Locales
```bash
# Compilar
mvn clean package

# Ejecutar tests
mvn test

# Ejecutar aplicación
java -jar target/iam-service-1.0.0.jar
```

- [ ] Aplicación compila
- [ ] Aplicación arranca
- [ ] Todos los endpoints funcionan
- [ ] Health check funciona

### Tests de Integración
- [ ] Endpoints probados con Postman/curl
- [ ] Swagger UI funciona
- [ ] Exception handling funciona
- [ ] i18n funciona (diferentes locales)

### Tests de Contrato
- [ ] Contract tests pasan
- [ ] Pact provider tests pasan
- [ ] Contratos validados con consumidores (si aplica)

## 📦 Build y Artefactos

### Build
```bash
# Build completo
mvn clean package

# Build sin tests (solo si es necesario)
mvn clean package -DskipTests
```

- [ ] Build exitoso
- [ ] JAR generado en `target/`
- [ ] JAR es ejecutable

### Artefactos
- [ ] JAR con nombre correcto: `iam-service-{version}.jar`
- [ ] JAR no incluye dependencias de desarrollo
- [ ] Manifest correcto

### Repositorio Maven (si aplica)
```bash
# Instalar en repositorio local
mvn clean install

# Deploy a repositorio remoto (si aplica)
mvn clean deploy
```

- [ ] Artefacto instalado/desplegado
- [ ] Versión correcta en repositorio

## 🚀 Deployment

### Pre-Deployment
- [ ] Variables de entorno configuradas
- [ ] Secrets configurados (no hardcodeados)
- [ ] Base de datos migrada (si aplica)
- [ ] Configuración de red/firewall (si aplica)

### Deployment
- [ ] Deployment en ambiente de staging
- [ ] Smoke tests en staging
- [ ] Deployment en producción
- [ ] Smoke tests en producción

### Post-Deployment
- [ ] Health check en producción: `/actuator/health`
- [ ] Endpoints funcionando
- [ ] Logs monitoreados
- [ ] Métricas monitoreadas

## 📊 Monitoreo Post-Release

### Primeras 24 Horas
- [ ] Monitorear logs de errores
- [ ] Monitorear métricas (latencia, throughput)
- [ ] Monitorear health checks
- [ ] Verificar que no hay errores críticos

### Semana 1
- [ ] Revisar métricas de uso
- [ ] Revisar feedback de usuarios
- [ ] Identificar mejoras necesarias

## 📝 Documentación Post-Release

### Actualizar Documentación
- [ ] CHANGELOG actualizado con fecha de release
- [ ] README actualizado (si hay cambios)
- [ ] Documentación de API actualizada
- [ ] Runbooks actualizados (si hay cambios en proceso)

### Comunicación
- [ ] Notificar a stakeholders de la release
- [ ] Documentar breaking changes (si aplica)
- [ ] Documentar nuevas features

## 🔄 Rollback Plan

### Preparación
- [ ] Plan de rollback documentado
- [ ] Versión anterior identificada
- [ ] Procedimiento de rollback probado

### Si es Necesario Rollback
1. Identificar versión anterior estable
2. Revertir deployment
3. Verificar que servicio funciona
4. Documentar razón del rollback
5. Planificar fix para próxima release

## ✅ Checklist Final

### Antes de Marcar Release como Completa
- [ ] Todos los items anteriores completados
- [ ] Release probada en staging
- [ ] Release probada en producción
- [ ] Documentación actualizada
- [ ] Monitoreo configurado

### Comandos Finales
```bash
# Verificar estado
git status
git log --oneline -10

# Verificar tag
git tag -l

# Verificar build
ls -lh target/iam-service-*.jar
```

## 📚 Referencias

- Master Runbook: `docs/runbooks/MASTER-RUNBOOK.md`
- Dev Checklist: `docs/runbooks/DEV-CHECKLIST.md`
- Convenciones: `docs/prompts/000-conventions.md`

## 🆘 Troubleshooting

### Build Falla
- Verificar versiones de Java y Maven
- Verificar dependencias
- Verificar configuración de `pom.xml`

### Tests Fallan
- Ejecutar tests individualmente para identificar problema
- Verificar configuración de tests
- Verificar mocks y datos de prueba

### Deployment Falla
- Verificar variables de entorno
- Verificar configuración de red
- Verificar logs de aplicación
- Verificar health checks
