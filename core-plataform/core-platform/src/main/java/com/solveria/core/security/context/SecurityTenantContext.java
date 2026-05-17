package com.solveria.core.security.context;

/** Contexto de tenant actual. */
public final class SecurityTenantContext {

    private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();

    private SecurityTenantContext() {}

    public static void setTenantId(String tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static String getTenantId() {
        return TENANT_ID.get();
    }

    public static void clear() {
        TENANT_ID.remove();
    }
}
