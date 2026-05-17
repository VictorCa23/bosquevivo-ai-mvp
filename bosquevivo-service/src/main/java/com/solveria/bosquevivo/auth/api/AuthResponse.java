package com.solveria.bosquevivo.auth.api;

public record AuthResponse(String token, String username, String displayName, String role) {}
