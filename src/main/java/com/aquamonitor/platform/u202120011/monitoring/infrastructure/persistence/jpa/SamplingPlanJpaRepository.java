package com.aquamonitor.platform.u202120011.monitoring.infrastructure.persistence.jpa;

import com.aquamonitor.platform.u202120011.monitoring.infrastructure.persistence.jpa.entities.SamplingPlanJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SamplingPlanJpaRepository extends JpaRepository<SamplingPlanJpaEntity, Long> {
    boolean existsByPlanCode(String planCode);

    Optional<SamplingPlanJpaEntity> findByPlanCode(String planCode);
}
