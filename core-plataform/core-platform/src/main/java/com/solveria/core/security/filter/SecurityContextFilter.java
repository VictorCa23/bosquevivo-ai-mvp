package com.solveria.core.security.filter;

import com.solveria.core.security.context.SecurityTenantContext;
import com.solveria.core.security.context.SecurityUserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

public class SecurityContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String tenantId = request.getHeader("X-Tenant-Id");
            String userId = request.getHeader("X-User-Id");

            if (tenantId != null && !tenantId.isBlank()) {
                SecurityTenantContext.setTenantId(tenantId);
            }

            if (userId != null && !userId.isBlank()) {
                SecurityUserContext.setUserId(Long.parseLong(userId));
            }

            filterChain.doFilter(request, response);

        } finally {
            SecurityTenantContext.clear();
            SecurityUserContext.clear();
        }
    }
}
