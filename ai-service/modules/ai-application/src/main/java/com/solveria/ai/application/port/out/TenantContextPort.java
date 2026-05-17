package com.solveria.ai.application.port.out;

import java.util.List;

/**
 * Port out: tenant context (tenantId, principal, scopes). Infrastructure provides implementation
 * (e.g. from JWT in future).
 */
public interface TenantContextPort {

    String currentTenantId();

    String principal();

    List<String> scopes();
}
