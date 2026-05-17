package com.solveria.iamservice.application.orchestration;

import com.solveria.core.iam.application.command.AssignPermissionsToRoleCommand;
import com.solveria.core.iam.application.usecase.AssignPermissionsToRoleUseCase;
import com.solveria.core.iam.domain.model.Role;
import com.solveria.core.shared.exceptions.SolverException;
import com.solveria.iamservice.application.dto.AssignPermissionsToRoleRequest;
import com.solveria.iamservice.application.dto.AssignPermissionsToRoleResponse;
import com.solveria.iamservice.application.exception.IamServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssignPermissionsToRoleOrchestrator {

    private static final Logger log =
            LoggerFactory.getLogger(AssignPermissionsToRoleOrchestrator.class);

    private final AssignPermissionsToRoleUseCase assignPermissionsToRoleUseCase;

    public AssignPermissionsToRoleOrchestrator(
            AssignPermissionsToRoleUseCase assignPermissionsToRoleUseCase) {
        this.assignPermissionsToRoleUseCase = assignPermissionsToRoleUseCase;
    }

    public AssignPermissionsToRoleResponse execute(AssignPermissionsToRoleRequest request) {
        log.info(
                "event=IAM_ASSIGN_PERMISSIONS_REQUEST_RECEIVED roleId={} permissionIdsCount={}",
                request.roleId(),
                request.permissionIds() != null ? request.permissionIds().size() : 0);

        try {
            AssignPermissionsToRoleCommand command =
                    new AssignPermissionsToRoleCommand(request.roleId(), request.permissionIds());

            Role role = assignPermissionsToRoleUseCase.execute(command);

            log.info("event=IAM_ASSIGN_PERMISSIONS_SUCCESS roleId={}", role.getId());

            return mapToResponse(role);
        } catch (SolverException e) {
            log.error(
                    "event=IAM_ASSIGN_PERMISSIONS_ERROR errorCode={} roleId={}",
                    e.getCode(),
                    request.roleId(),
                    e);
            throw e;
        } catch (Exception e) {
            log.error("event=IAM_ASSIGN_PERMISSIONS_ERROR roleId={}", request.roleId(), e);
            throw new IamServiceException("IAM_ASSIGN_PERMISSIONS_FAILED", null, e);
        }
    }

    private AssignPermissionsToRoleResponse mapToResponse(Role role) {
        return new AssignPermissionsToRoleResponse(
                role.getId(), role.getName(), role.getDescription(), role.getPermissionIds());
    }
}
