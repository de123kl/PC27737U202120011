package com.aquamonitor.platform.u202120011.monitoring.infrastructure.persistence.jpa.adapters;

import com.aquamonitor.platform.u202120011.monitoring.domain.exceptions.DuplicateSamplingPlanException;
import com.aquamonitor.platform.u202120011.monitoring.domain.model.aggregates.SamplingPlan;
import com.aquamonitor.platform.u202120011.monitoring.domain.model.valueobjects.PlanCode;
import com.aquamonitor.platform.u202120011.monitoring.domain.repositories.SamplingPlanRepository;
import com.aquamonitor.platform.u202120011.monitoring.infrastructure.persistence.jpa.SamplingPlanJpaRepository;
import com.aquamonitor.platform.u202120011.monitoring.infrastructure.persistence.jpa.mappers.SamplingPlanPersistenceMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SamplingPlanRepositoryAdapter implements SamplingPlanRepository {
    private final SamplingPlanJpaRepository jpaRepository;
    private final SamplingPlanPersistenceMapper mapper;

    public SamplingPlanRepositoryAdapter(SamplingPlanJpaRepository jpaRepository,
                                         SamplingPlanPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public SamplingPlan save(SamplingPlan samplingPlan) {
        try {
            var saved = jpaRepository.saveAndFlush(mapper.toEntity(samplingPlan));
            var persisted = mapper.toDomain(saved);
            persisted.pullDomainEvents();
            return persisted;
        } catch (DataIntegrityViolationException exception) {
            if (isPlanCodeUniqueViolation(exception)) {
                throw new DuplicateSamplingPlanException(exception);
            }
            throw exception;
        }
    }

    @Override
    public boolean existsByPlanCode(PlanCode planCode) {
        return jpaRepository.existsByPlanCode(planCode.value());
    }

    @Override
    public Optional<SamplingPlan> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<SamplingPlan> findByPlanCode(PlanCode planCode) {
        return jpaRepository.findByPlanCode(planCode.value()).map(mapper::toDomain);
    }

    @Override
    public List<SamplingPlan> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    private boolean isPlanCodeUniqueViolation(Throwable exception) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof ConstraintViolationException violation) {
                String constraintName = violation.getConstraintName();
                if ("uk_sampling_plan_plan_code".equalsIgnoreCase(constraintName)) {
                    return true;
                }
            }
            String message = current.getMessage();
            if (message != null
                    && (message.toLowerCase().contains("uk_sampling_plan_plan_code")
                    || message.toLowerCase().contains("plan_code"))) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
