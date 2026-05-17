package com.solveria.iamservice.application.orchestration;

import com.solveria.core.iam.application.usecase.CreateRoleUseCase;

/**
 * Orchestrator for CreateRole use case. Coordinates between API layer and core-platform use case.
 *
 * <p>TODO: Implement execute method when CreateRoleResponse is defined. For now, this orchestrator
 * is wired but not fully implemented.
 *
 * <p>Registered as bean in UseCaseConfig (explicit wiring).
 */
public class CreateRoleOrchestrator {

    private final CreateRoleUseCase createRoleUseCase;

    public CreateRoleOrchestrator(CreateRoleUseCase createRoleUseCase) {
        this.createRoleUseCase = createRoleUseCase;
    }
}
