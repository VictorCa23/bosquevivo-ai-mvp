package com.solveria.iamservice.api.auth;

public record AuthResponse(String token, String username, String displayName, String role) {}
