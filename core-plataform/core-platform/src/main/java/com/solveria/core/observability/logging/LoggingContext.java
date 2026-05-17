package com.solveria.core.observability.logging;

import com.solveria.core.observability.context.CorrelationIdContext;
import org.slf4j.MDC;

public final class LoggingContext {

    private LoggingContext() {}

    public static void enrich() {
        MDC.put("correlationId", CorrelationIdContext.get());
    }

    public static void clear() {
        MDC.clear();
    }
}
