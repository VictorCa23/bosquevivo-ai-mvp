package com.solveria.iamservice.application.auth;

import com.solveria.iamservice.api.auth.AuthResponse;
import com.solveria.iamservice.api.auth.LoginRequest;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final JwtTokenService jwtTokenService;
    private final Map<String, DemoIdentity> identities;

    public AuthService(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
        this.identities =
                Map.of(
                        "admin",
                        new DemoIdentity("admin", "Administrador", "ADMIN", "admin123"),
                        "ciudadano",
                        new DemoIdentity("ciudadano", "Ciudadano", "CITIZEN", "ciudadano123"));
    }

    public AuthResponse login(LoginRequest request) {
        DemoIdentity identity = identities.get(request.username().toLowerCase());
        if (identity == null || !identity.password().equals(request.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return toResponse(identity);
    }

    public AuthResponse findByUsername(String username) {
        DemoIdentity identity = identities.get(username.toLowerCase());
        if (identity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Identity not found");
        }
        return toResponse(identity);
    }

    private AuthResponse toResponse(DemoIdentity identity) {
        return new AuthResponse(
                jwtTokenService.createToken(identity),
                identity.username(),
                identity.displayName(),
                identity.role());
    }
}
