package com.aquamonitor.platform.u202120011.monitoring.infrastructure.persistence.jpa.mappers;

import com.aquamonitor.platform.u202120011.monitoring.domain.model.aggregates.SamplingPlan;
import com.aquamonitor.platform.u202120011.monitoring.domain.model.valueobjects.PlanCode;
import com.aquamonitor.platform.u202120011.monitoring.domain.model.valueobjects.SampleVolume;
import com.aquamonitor.platform.u202120011.monitoring.domain.model.valueobjects.SamplingWindow;
import com.aquamonitor.platform.u202120011.monitoring.domain.model.valueobjects.StationId;
import com.aquamonitor.platform.u202120011.monitoring.infrastructure.persistence.jpa.entities.SamplingPlanJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SamplingPlanPersistenceMapper {
    public SamplingPlanJpaEntity toEntity(SamplingPlan plan) {
        return new SamplingPlanJpaEntity(
                plan.getId(),
                plan.getPlanCode().value(),
                plan.getStationId().value(),
                plan.getSamplingWindow().startDate(),
                plan.getSamplingWindow().endDate(),
                plan.getSampleVolume().quantity(),
                plan.getSampleVolume().unit(),
                plan.getPriorityLevel(),
                plan.getStatus(),
                plan.getSamplingNotes(),
                plan.getCreatedAt(),
                plan.getUpdatedAt()
        );
    }

    public SamplingPlan toDomain(SamplingPlanJpaEntity entity) {
        return SamplingPlan.rehydrate(
                entity.getId(),
                new PlanCode(entity.getPlanCode()),
                new StationId(entity.getStationId()),
                new SamplingWindow(entity.getSamplingWindowStartDate(),
                        entity.getSamplingWindowEndDate()),
                new SampleVolume(entity.getSampleVolumeQuantity(),
                        entity.getSampleVolumeUnit()),
                entity.getPriorityLevel(),
                entity.getStatus(),
                entity.getSamplingNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
