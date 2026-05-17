package com.solveria.ai.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

class AiArchitectureTest {

    private final JavaClasses classes =
            new ClassFileImporter()
                    .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                    .importPackages("com.solveria.ai");

    @Test
    void layeredArchitecture_shouldBeRespected() {
        layeredArchitecture()
                .consideringAllDependencies()
                .layer("Domain")
                .definedBy("..domain..")
                .layer("Application")
                .definedBy("..application..")
                .layer("Infrastructure")
                .definedBy("..infrastructure..")
                .layer("API")
                .definedBy("..api..")
                .layer("Bootstrap")
                .definedBy("..bootstrap..")
                .whereLayer("Domain")
                .mayOnlyBeAccessedByLayers("Application", "Infrastructure", "API", "Bootstrap")
                .whereLayer("Application")
                .mayOnlyBeAccessedByLayers("Infrastructure", "API", "Bootstrap")
                .whereLayer("Infrastructure")
                .mayOnlyBeAccessedByLayers("Bootstrap")
                .whereLayer("API")
                .mayOnlyBeAccessedByLayers("Bootstrap")
                .check(classes);
    }

    @Test
    void domain_mustNotDependOnOtherLayers() {
        ArchRule rule =
                noClasses()
                        .that()
                        .resideInAPackage("..domain..")
                        .should()
                        .dependOnClassesThat()
                        .resideInAnyPackage(
                                "..application..",
                                "..infrastructure..",
                                "..api..",
                                "..bootstrap..");

        rule.check(classes);
    }

    @Test
    void application_mustNotDependOnInfrastructure() {
        ArchRule rule =
                noClasses()
                        .that()
                        .resideInAPackage("..application..")
                        .should()
                        .dependOnClassesThat()
                        .resideInAPackage("..infrastructure..");

        rule.check(classes);
    }

    @Test
    void api_mustNotDependOnInfrastructure() {
        ArchRule rule =
                noClasses()
                        .that()
                        .resideInAPackage("..api..")
                        .should()
                        .dependOnClassesThat()
                        .resideInAPackage("..infrastructure..");

        rule.check(classes);
    }

    @Test
    void ports_shouldBeInterfaces() {
        ArchRule rule =
                classes().that().resideInAPackage("..application.port..").should().beInterfaces();

        rule.check(classes);
    }

    @Test
    void adapters_shouldLiveInInfrastructure() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("Adapter")
                        .should()
                        .resideInAPackage("..infrastructure..");

        rule.check(classes);
    }

    @Test
    void controllers_shouldBeAnnotatedWithRestController() {
        ArchRule rule =
                classes()
                        .that()
                        .resideInAPackage("..api..")
                        .and()
                        .haveSimpleNameEndingWith("Controller")
                        .should()
                        .beAnnotatedWith("org.springframework.web.bind.annotation.RestController");

        rule.check(classes);
    }
}
