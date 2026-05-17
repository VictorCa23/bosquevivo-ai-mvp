package com.solveria.iamservice.contract;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solveria.core.shared.exceptions.EntityNotFoundException;
import com.solveria.iamservice.api.exception.ErrorCodes;
import com.solveria.iamservice.application.dto.AssignPermissionsToRoleResponse;
import com.solveria.iamservice.application.orchestration.AssignPermissionsToRoleOrchestrator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AssignPermissionsToRoleContractTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private AssignPermissionsToRoleOrchestrator assignPermissionsToRoleOrchestrator;

    @Test
    void shouldReturn200OkWhenPermissionsAssignedSuccessfully() throws Exception {
        Long roleId = 1L;
        List<Long> permissionIds = List.of(1L, 2L, 3L, 4L);

        AssignPermissionsToRoleResponse response =
                new AssignPermissionsToRoleResponse(
                        1L, "ADMIN", "Administrator role", permissionIds);

        when(assignPermissionsToRoleOrchestrator.execute(any())).thenReturn(response);

        String requestBody =
                objectMapper.writeValueAsString(
                        new com.solveria.iamservice.application.dto.PermissionIdsRequest(
                                permissionIds));

        mockMvc.perform(
                        put("/api/v1/iam/roles/{roleId}/permissions", roleId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("ADMIN"))
                .andExpect(jsonPath("$.description").value("Administrator role"))
                .andExpect(jsonPath("$.permissionIds").isArray())
                .andExpect(jsonPath("$.permissionIds[0]").value(1L))
                .andExpect(jsonPath("$.permissionIds[1]").value(2L))
                .andExpect(jsonPath("$.permissionIds[2]").value(3L))
                .andExpect(jsonPath("$.permissionIds[3]").value(4L));
    }

    @Test
    void shouldReturn400BadRequestWhenValidationFails() throws Exception {
        Long roleId = 1L;
        String requestBody =
                """
                {
                  "permissionIds": []
                }
                """;

        mockMvc.perform(
                        put("/api/v1/iam/roles/{roleId}/permissions", roleId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.VALIDATION_ERROR))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/iam/roles/1/permissions"))
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details.permissionIds").exists());
    }

    @Test
    void shouldReturn404NotFoundWhenRoleNotFound() throws Exception {
        Long roleId = 999L;
        List<Long> permissionIds = List.of(1L, 2L);

        EntityNotFoundException exception = new EntityNotFoundException("Role", roleId.toString());
        when(assignPermissionsToRoleOrchestrator.execute(any())).thenThrow(exception);

        String requestBody =
                objectMapper.writeValueAsString(
                        new com.solveria.iamservice.application.dto.PermissionIdsRequest(
                                permissionIds));

        mockMvc.perform(
                        put("/api/v1/iam/roles/{roleId}/permissions", roleId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("error.entity.not_found"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/iam/roles/999/permissions"));
    }

    @Test
    void shouldReturn500InternalServerErrorWhenUnexpectedErrorOccurs() throws Exception {
        Long roleId = 1L;
        List<Long> permissionIds = List.of(1L, 2L);

        when(assignPermissionsToRoleOrchestrator.execute(any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        String requestBody =
                objectMapper.writeValueAsString(
                        new com.solveria.iamservice.application.dto.PermissionIdsRequest(
                                permissionIds));

        mockMvc.perform(
                        put("/api/v1/iam/roles/{roleId}/permissions", roleId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.UNEXPECTED_ERROR))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/iam/roles/1/permissions"));
    }
}
