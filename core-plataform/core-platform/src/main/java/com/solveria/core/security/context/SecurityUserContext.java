package com.solveria.core.security.context;

/**
 * Contexto de seguridad para identificar al usuario actual.
 *
 * <p>- Thread-safe (ThreadLocal) - No depende de infraestructura - Enterprise ready
 */
public final class SecurityUserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    private SecurityUserContext() {}

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    /** Identificador semántico del actor. Usado para auditoría y trazabilidad. */
    public static String getUserIdentifier() {
        Long id = USER_ID.get();
        return id != null ? id.toString() : "system";
    }

    public static void clear() {
        USER_ID.remove();
    }
}
