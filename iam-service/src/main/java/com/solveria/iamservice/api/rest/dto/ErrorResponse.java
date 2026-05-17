package com.solveria.iamservice.api.rest.dto;

import java.time.Instant;

public record ErrorResponse(String errorCode, String message, Instant timestamp, String path) {}
