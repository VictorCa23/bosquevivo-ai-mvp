# ADR-001 – Uso de JPA en el Dominio del Core Platform

## Estado
Aceptado

## Contexto
El core-platform de Solveria está diseñado como una librería reusable, basada en:
- DDD
- Hexagonal Architecture
- Clean Architecture

Durante la evaluación arquitectónica se identificó que:
- Las entidades de dominio utilizan anotaciones JPA
- Existe una clase BaseEntity con dependencias a JPA
- Se utilizan Specifications JPA en capas compartidas

Esto introduce una dependencia tecnológica en el dominio.

## Decisión
Se decide **aceptar conscientemente el uso de JPA dentro del dominio** del core-platform como una decisión pragmática.

El core-platform:
- Está orientado a entornos enterprise
- Utiliza Spring Boot + JPA como stack base
- Prioriza velocidad de desarrollo, consistencia y reducción de duplicación

No se separan entidades de dominio y entidades de persistencia en esta etapa.

## Justificación
- Separar dominio y persistencia introduce complejidad adicional (mappers, duplicación)
- El dominio no se reutilizará fuera del ecosistema Spring/JPA en el corto plazo
- La pureza teórica no justifica el costo operativo actual

Esta decisión **no invalida DDD ni Clean Architecture**, sino que adopta un enfoque enterprise-pragmático.

## Consecuencias
- El dominio no es 100 % agnóstico a la persistencia
- Cursor y herramientas de análisis deben considerar esta decisión
- La separación completa podrá evaluarse en una iteración futura si el contexto lo exige

## Revisión futura
Esta decisión podrá revisarse si:
- El core-platform necesita ser reutilizado fuera de JPA
- Se requiere soporte multi-persistencia
- Se decide una refactorización mayor

