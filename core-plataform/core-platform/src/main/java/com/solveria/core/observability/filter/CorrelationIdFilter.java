package com.solveria.core.observability.filter;

import com.solveria.core.observability.context.CorrelationIdContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

public class CorrelationIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String correlationId = request.getHeader("X-Correlation-Id");

            if (correlationId == null || correlationId.isBlank()) {
                correlationId = UUID.randomUUID().toString();
            }

            CorrelationIdContext.set(correlationId);
            response.setHeader("X-Correlation-Id", correlationId);

            filterChain.doFilter(request, response);

        } finally {
            CorrelationIdContext.clear();
        }
    }
}
