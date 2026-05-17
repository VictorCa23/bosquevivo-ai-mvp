package com.solveria.bosquevivo.incidents.api;

import com.solveria.bosquevivo.incidents.application.BrigadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/brigades")
@Tag(name = "Brigades", description = "Simple brigade catalog for MVP operations")
public class BrigadeController {

    private final BrigadeService service;

    public BrigadeController(BrigadeService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List brigades")
    public List<BrigadeResponse> findAll() {
        return service.findAll();
    }
}
