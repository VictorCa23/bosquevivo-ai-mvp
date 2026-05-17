package com.solveria.bosquevivo.auth.api;

import com.solveria.bosquevivo.auth.application.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authenticated user from IAM JWT")
public class AuthController {

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user")
    public AuthResponse me(Authentication authentication) {
        CurrentUser user = (CurrentUser) authentication.getPrincipal();
        return new AuthResponse(null, user.username(), user.displayName(), user.role());
    }
}
