package com.solveria.ai.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

class DomainArchitectureTest {

    private final JavaClasses domainClasses =
            new ClassFileImporter()
                    .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                    .importPackages("com.solveria.ai.domain");

    @Test
    void domain_mustNotDependOnFrameworks() {
        ArchRule rule =
                noClasses()
                        .that()
                        .resideInAPackage("..domain..")
                        .should()
                        .dependOnClassesThat()
                        .resideInAnyPackage(
                                "org.springframework..",
                                "jakarta.persistence..",
                                "jakarta.validation..");

        rule.check(domainClasses);
    }

    @Test
    void domainModels_shouldBeRecords() {
        ArchRule rule = classes().that().resideInAPackage("..domain.model..").should().beRecords();

        rule.check(domainClasses);
    }

    @Test
    void domainPolicies_shouldBeInterfaces() {
        ArchRule rule =
                classes().that().resideInAPackage("..domain.policy..").should().beInterfaces();

        rule.check(domainClasses);
    }
}
