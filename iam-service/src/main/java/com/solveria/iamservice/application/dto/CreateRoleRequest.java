package com.solveria.iamservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoleRequest(
        @NotBlank(message = "{validation.role.name.required}")
                @Size(min = 3, max = 50, message = "{validation.role.name.size}")
                String name,
        @Size(max = 255, message = "{validation.role.description.size}") String description) {}
