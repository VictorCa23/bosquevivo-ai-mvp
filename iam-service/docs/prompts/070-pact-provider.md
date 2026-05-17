# 070 - Pact Provider Testing

## Objetivo

Implementar tests de contrato con Pact (Provider) para validar que el servicio cumple con los contratos definidos por los consumidores.

## Archivos Objetivo

- `src/test/java/com/solveria/iamservice/pact/AssignPermissionsToRoleProviderPactTest.java` (ya existe, verificar/completar)
- `src/test/resources/pacts/` (verificar que existan pacts)
- `pom.xml` (agregar dependencias de Pact si faltan)

## Prompt para Cursor

```
Actúa como un Staff Software Engineer experto en Contract Testing con Pact.

Contexto:
- Repo: iam-service (microservicio Spring Boot).
- Ya existe AssignPermissionsToRoleProviderPactTest (verificar que esté completo).
- Objetivo: asegurar que los tests de Pact Provider funcionan correctamente.

Reglas obligatorias:
1. Tests de Pact validan que el provider cumple con los contratos.
2. Usar @SpringBootTest para tests de integración.
3. Mockear solo servicios externos, no el propio servicio.

Tareas:

1. Dependencias (pom.xml):
   - Verificar que existan dependencias de Pact:
     * au.com.dius.pact.provider:junit5
     * au.com.dius.pact.provider:spring
   - Versión compatible con Spring Boot 3.4.0

2. AssignPermissionsToRoleProviderPactTest:
   - Verificar @SpringBootTest
   - Verificar @AutoConfigureMockMvc
   - Verificar @Provider("iam-service")
   - Verificar @PactFolder("src/test/resources/pacts")
   - Verificar @TestTemplate
   - Verificar @ExtendWith(PactVerificationInvocationContextProvider.class)
   - Verificar método @BeforeEach que configura MockMvcTestTarget
   - Verificar métodos @State para preparar datos de prueba
   - Verificar que los estados correspondan con los del pact file

3. Pact Files (src/test/resources/pacts/):
   - Verificar que existan pact files JSON
   - Verificar que los estados definidos en pacts coincidan con @State methods
   - Verificar que los requests/responses sean correctos

4. Configuración:
   - Verificar application-test.yml o @TestPropertySource
   - Configurar puerto para tests si es necesario

5. Validación:
   - Tests de Pact pasan: mvn test
   - Pacts se validan correctamente
   - Estados funcionan (preparan datos necesarios)

Entregables:
- Archivos de test verificados/completados.
- Tests de Pact pasando: mvn test
- Pacts validados correctamente.
```

## Checklist de Validación Post-Generación

### Dependencias
- [ ] Dependencias de Pact en pom.xml
- [ ] Versiones compatibles con Spring Boot 3.4.0
- [ ] No hay conflictos de versiones

### Tests
- [ ] `mvn test` pasa todos los tests de Pact
- [ ] Tests de integración funcionan (@SpringBootTest)
- [ ] MockMvc configurado correctamente

### Pact Files
- [ ] Pact files existen en src/test/resources/pacts/
- [ ] Estados en pact files coinciden con @State methods
- [ ] Requests/responses en pacts son correctos

### Estados
- [ ] @State methods preparan datos necesarios
- [ ] Estados son idempotentes (pueden ejecutarse múltiples veces)
- [ ] Estados limpian datos después de la prueba (si aplica)

### Validación
- [ ] Provider cumple con todos los contratos
- [ ] Errores de contrato se reportan claramente
- [ ] Tests son determinísticos (mismo resultado cada vez)

### Ejecución
```bash
# Ejecutar tests de Pact
mvn test -Dtest=*ProviderPactTest

# Verificar pacts
mvn pact:verify
```

## Pitfalls Comunes

1. **Estados no coinciden**: Los nombres de @State methods deben coincidir exactamente con los estados definidos en los pact files.
2. **Falta configurar MockMvcTestTarget**: En @BeforeEach, debe configurarse MockMvcTestTarget con el MockMvc para que Pact pueda hacer requests.
3. **Tests no son idempotentes**: Los @State methods deben poder ejecutarse múltiples veces sin efectos secundarios (usar IDs únicos, limpiar datos, etc.).
