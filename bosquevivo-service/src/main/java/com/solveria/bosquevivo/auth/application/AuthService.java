package com.solveria.bosquevivo.auth.application;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final Map<String, DemoUser> users;

    public AuthService() {
        this.users =
                Map.of(
                        "admin",
                        user("admin", "Administrador", "ADMIN"),
                        "ciudadano",
                        user("ciudadano", "Ciudadano", "CITIZEN"));
    }

    public CurrentUser findByUsername(String username) {
        DemoUser user = users.get(username.toLowerCase());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token user");
        }
        return new CurrentUser(user.username(), user.displayName(), user.role());
    }

    private DemoUser user(String username, String displayName, String role) {
        return new DemoUser(username, displayName, role);
    }
}
