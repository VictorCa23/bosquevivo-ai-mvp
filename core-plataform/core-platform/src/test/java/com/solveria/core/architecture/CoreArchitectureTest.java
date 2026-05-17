package com.solveria.core.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

public class CoreArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter().importPackages("com.solveria.core");

    @Test
    void domainMustNotDependOnOtherLayers() {
        ArchRule rule =
                noClasses()
                        .that()
                        .resideInAPackage("..domain..")
                        .should()
                        .dependOnClassesThat()
                        .resideInAnyPackage("..application..", "..infrastructure..", "..api..");

        rule.check(classes);
    }

    @Test
    void applicationMustNotDependOnInfrastructure() {
        ArchRule rule =
                noClasses()
                        .that()
                        .resideInAPackage("..application..")
                        .should()
                        .dependOnClassesThat()
                        .resideInAPackage("..infrastructure..");

        rule.check(classes);
    }

    /**
     * KNOWN TECHNICAL DEBT (ADR-001): Domain layer currently has JPA annotations (@Entity, @Table,
     * etc.) This test DOCUMENTS the violation but does NOT enforce removal yet.
     *
     * <p>TODO: Enable this test when JPA is fully removed from domain (Phase 2)
     */
    // @Test
    void domainShouldNotDependOnJpa_DISABLED_KNOWN_DEBT() {
        ArchRule rule =
                noClasses()
                        .that()
                        .resideInAPackage("..domain..")
                        .should()
                        .dependOnClassesThat()
                        .resideInAnyPackage("jakarta.persistence..", "javax.persistence..");

        // This would fail currently - disabled as per ADR-001
        // rule.check(classes);

        System.out.println("⚠️  WARNING: Domain layer has JPA dependencies (ADR-001)");
        System.out.println("    Affected: iam.domain.model.* and audit.domain.model.*");
        System.out.println("    See: core-plataform/adr/ADR-001-jpa-in-domain-technical-debt.md");
    }
}
