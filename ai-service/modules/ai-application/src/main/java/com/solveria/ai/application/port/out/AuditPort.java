package com.solveria.ai.application.port.out;

import java.util.Map;

/** Port out: audit logging. Stub for now; infrastructure provides implementation. */
public interface AuditPort {

    void audit(String event, Map<String, Object> details);
}
