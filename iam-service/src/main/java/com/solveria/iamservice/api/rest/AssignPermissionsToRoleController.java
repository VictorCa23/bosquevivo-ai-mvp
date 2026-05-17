package com.solveria.iamservice.api.rest;

import com.solveria.iamservice.api.exception.dto.ApiErrorResponse;
import com.solveria.iamservice.application.dto.AssignPermissionsToRoleRequest;
import com.solveria.iamservice.application.dto.AssignPermissionsToRoleResponse;
import com.solveria.iamservice.application.dto.PermissionIdsRequest;
import com.solveria.iamservice.application.orchestration.AssignPermissionsToRoleOrchestrator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/iam/roles")
@Validated
@Tag(name = "Roles", description = "Role management operations")
public class AssignPermissionsToRoleController {

    private static final Logger log =
            LoggerFactory.getLogger(AssignPermissionsToRoleController.class);

    private final AssignPermissionsToRoleOrchestrator assignPermissionsToRoleOrchestrator;

    public AssignPermissionsToRoleController(
            AssignPermissionsToRoleOrchestrator assignPermissionsToRoleOrchestrator) {
        this.assignPermissionsToRoleOrchestrator = assignPermissionsToRoleOrchestrator;
    }

    @PutMapping("/{roleId}/permissions")
    @Operation(
            operationId = "assignPermissionsToRole",
            summary = "Assign permissions to a role",
            description = "Assigns a list of permissions to a specific role by role ID",
            requestBody =
                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "List of permission IDs to assign to the role",
                            required = true,
                            content =
                                    @Content(
                                            mediaType = "application/json",
                                            schema =
                                                    @Schema(
                                                            implementation =
                                                                    PermissionIdsRequest.class),
                                            examples =
                                                    @ExampleObject(
                                                            name = "Request Example",
                                                            value =
                                                                    """
                                            {
                                              "permissionIds": [1, 2, 3, 4]
                                            }
                                            """))))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Permissions assigned successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                AssignPermissionsToRoleResponse
                                                                        .class),
                                        examples =
                                                @ExampleObject(
                                                        name = "Success Response",
                                                        value =
                                                                """
                                            {
                                              "id": 1,
                                              "name": "ADMIN",
                                              "description": "Administrator role",
                                              "permissionIds": [1, 2, 3, 4]
                                            }
                                            """))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad request - Validation error or business rule violation",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class),
                                        examples = {
                                            @ExampleObject(
                                                    name = "Validation Error",
                                                    value =
                                                            """
                                                    {
                                                      "errorCode": "VALIDATION_ERROR",
                                                      "timestamp": "2024-01-15T10:30:00Z",
                                                      "path": "/api/v1/iam/roles/1/permissions",
                                                      "details": {
                                                        "permissionIds": "must not be empty"
                                                      }
                                                    }
                                                    """),
                                            @ExampleObject(
                                                    name = "Business Rule Violation",
                                                    value =
                                                            """
                                                    {
                                                      "errorCode": "IAM_PERMISSION_NOT_FOUND",
                                                      "timestamp": "2024-01-15T10:30:00Z",
                                                      "path": "/api/v1/iam/roles/1/permissions"
                                                    }
                                                    """)
                                        })),
                @ApiResponse(
                        responseCode = "404",
                        description = "Role not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "Role Not Found",
                                                        value =
                                                                """
                                            {
                                              "errorCode": "IAM_ROLE_NOT_FOUND",
                                              "timestamp": "2024-01-15T10:30:00Z",
                                              "path": "/api/v1/iam/roles/999/permissions"
                                            }
                                            """))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "Internal Error",
                                                        value =
                                                                """
                                            {
                                              "errorCode": "UNEXPECTED_ERROR",
                                              "timestamp": "2024-01-15T10:30:00Z",
                                              "path": "/api/v1/iam/roles/1/permissions"
                                            }
                                            """)))
            })
    public ResponseEntity<AssignPermissionsToRoleResponse> assignPermissionsToRole(
            @PathVariable
                    @Positive
                    @Schema(description = "Role identifier", example = "1", required = true)
                    Long roleId,
            @RequestBody
                    @Valid
                    @Schema(
                            description = "List of permission IDs to assign to the role",
                            required = true)
                    PermissionIdsRequest request) {
        log.debug(
                "event=IAM_ASSIGN_PERMISSIONS_HTTP_REQUEST_RECEIVED roleId={} permissionIdsCount={}",
                roleId,
                request.permissionIds() != null ? request.permissionIds().size() : 0);

        AssignPermissionsToRoleRequest orchestratorRequest =
                new AssignPermissionsToRoleRequest(roleId, request.permissionIds());

        AssignPermissionsToRoleResponse response =
                assignPermissionsToRoleOrchestrator.execute(orchestratorRequest);

        return ResponseEntity.ok(response);
    }
}
