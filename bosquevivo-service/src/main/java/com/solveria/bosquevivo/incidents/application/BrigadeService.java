package com.solveria.bosquevivo.incidents.application;

import com.solveria.bosquevivo.incidents.api.BrigadeResponse;
import com.solveria.bosquevivo.incidents.infrastructure.BrigadeRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BrigadeService {

    private final BrigadeRepository repository;

    public BrigadeService(BrigadeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<BrigadeResponse> findAll() {
        return repository.findAll().stream().map(BrigadeResponse::from).toList();
    }
}
