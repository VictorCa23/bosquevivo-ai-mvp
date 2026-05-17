package com.solveria.core.iam.infrastructure.persistence.adapter;

import com.solveria.core.iam.application.port.ActionRepositoryPort;
import com.solveria.core.iam.domain.model.Action;
import com.solveria.core.iam.infrastructure.persistence.mapper.ActionJpaMapper;
import com.solveria.core.iam.infrastructure.persistence.repository.ActionJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ActionRepositoryAdapter implements ActionRepositoryPort {

    private final ActionJpaRepository actionJpaRepository;
    private final ActionJpaMapper mapper;

    public ActionRepositoryAdapter(
            ActionJpaRepository actionJpaRepository, ActionJpaMapper mapper) {
        this.actionJpaRepository = actionJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Action> findById(Long id) {
        return actionJpaRepository.findById(id).map(mapper::toDomain);
    }
}
