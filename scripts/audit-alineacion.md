Validé yo con Maven Wrapper en los 3 repos:

- `core-plataform`: `.\mvnw.cmd -q clean verify` -> ✅ **OK**
- `iam-service`: `.\mvnw.cmd -q clean verify` -> ❌ **FAIL**
  - Error: Flyway migration `V2__seed_default_actions.sql` en tests (`NULL not allowed for column "ID"` en `iam_action`)
- `ai-service`: `.\mvnw.cmd -q clean verify` -> ❌ **FAIL**
  - Error: `spotless:check` con violaciones de formato en `ai-domain` (varios archivos Java)

Conclusión: **todavía no está todo OK para arrancar vertical con build verde completo**.  
Está alineada la gobernanza mínima, pero siguen dos bloqueos de verificación en `iam-service` y `ai-service`.
