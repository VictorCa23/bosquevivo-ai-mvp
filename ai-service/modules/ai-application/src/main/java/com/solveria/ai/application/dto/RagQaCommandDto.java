package com.solveria.ai.application.dto;

/** DTO: RAG QA use case input (question + namespace). */
public record RagQaCommandDto(String question, String namespace) {}
