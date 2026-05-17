package com.solveria.iamservice.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collection;

@Schema(description = "Response containing role information after permissions assignment")
public record AssignPermissionsToRoleResponse(
        @Schema(description = "Role identifier", example = "1") Long id,
        @Schema(description = "Role name", example = "ADMIN") String name,
        @Schema(description = "Role description", example = "Administrator role")
                String description,
        @Schema(
                        description = "List of permission IDs assigned to the role",
                        example = "[1, 2, 3, 4]")
                Collection<Long> permissionIds) {
    public AssignPermissionsToRoleResponse(Long id, String name, String description) {
        this(id, name, description, null);
    }
}
