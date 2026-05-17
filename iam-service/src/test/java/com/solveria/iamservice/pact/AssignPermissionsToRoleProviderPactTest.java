package com.solveria.iamservice.pact;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.spring.spring6.PactVerificationSpring6Provider;
import au.com.dius.pact.provider.spring.spring6.Spring6MockMvcTestTarget;
import com.solveria.iamservice.application.dto.AssignPermissionsToRoleResponse;
import com.solveria.iamservice.application.orchestration.AssignPermissionsToRoleOrchestrator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Pact Provider Verification Test for the Assign Permissions to Role endpoint.
 *
 * <p>This test verifies that the iam-service provider implementation fulfills the contracts defined
 * by consumers. The contracts are loaded from the {@code pacts} folder in test resources.
 *
 * <p>The test uses MockMvc to simulate HTTP requests without starting a full application server,
 * making it suitable for CI/CD pipelines and Pact Broker integration.
 *
 * <p>Provider states are configured using {@code @State} annotations to set up the necessary
 * conditions before each contract verification.
 *
 * @see <a href="https://docs.pact.io/implementation_guides/jvm/provider/junit5">Pact JUnit 5
 *     Provider</a>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Provider("iam-service")
@PactFolder("pacts")
@Tag("pact")
class AssignPermissionsToRoleProviderPactTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private AssignPermissionsToRoleOrchestrator assignPermissionsToRoleOrchestrator;

    @TestTemplate
    @ExtendWith(PactVerificationSpring6Provider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    void setUp(PactVerificationContext context) {
        context.setTarget(new Spring6MockMvcTestTarget(mockMvc));
    }

    @State("role exists and permissions can be assigned")
    void roleExistsAndPermissionsCanBeAssigned() {
        AssignPermissionsToRoleResponse response =
                new AssignPermissionsToRoleResponse(
                        1L, "ADMIN", "Administrator role", List.of(1L, 2L, 3L));

        when(assignPermissionsToRoleOrchestrator.execute(any())).thenReturn(response);
    }

    @State("role does not exist")
    void roleDoesNotExist() {
        // Prepared for future contract scenarios where role does not exist
        // This state method can be implemented when consumer contracts require it
    }
}
