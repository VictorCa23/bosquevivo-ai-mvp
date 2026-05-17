package com.solveria.iamservice.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;

@Schema(description = "Request body containing list of permission IDs to assign to a role")
public record PermissionIdsRequest(
        @NotEmpty(message = "{validation.permission.ids.required}")
                @Schema(
                        description = "List of permission identifiers to assign",
                        example = "[1, 2, 3, 4]",
                        required = true)
                Collection<@NotNull Long> permissionIds) {}
