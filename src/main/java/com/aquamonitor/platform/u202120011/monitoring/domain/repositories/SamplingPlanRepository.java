package com.aquamonitor.platform.u202120011.monitoring.domain.repositories;

import com.aquamonitor.platform.u202120011.monitoring.domain.model.aggregates.SamplingPlan;
import com.aquamonitor.platform.u202120011.monitoring.domain.model.valueobjects.PlanCode;

import java.util.List;
import java.util.Optional;
public interface SamplingPlanRepository {
    SamplingPlan save(SamplingPlan samplingPlan);

    boolean existsByPlanCode(PlanCode planCode);

    Optional<SamplingPlan> findById(Long id);

    Optional<SamplingPlan> findByPlanCode(PlanCode planCode);

    List<SamplingPlan> findAll();
}
