package com.aquamonitor.platform.u202120011.monitoring.domain.model.aggregates;

import com.aquamonitor.platform.u202120011.monitoring.domain.model.events.SamplingPlanCreatedEvent;
import com.aquamonitor.platform.u202120011.monitoring.domain.model.valueobjects.PlanCode;
import com.aquamonitor.platform.u202120011.monitoring.domain.model.valueobjects.SampleVolume;
import com.aquamonitor.platform.u202120011.monitoring.domain.model.valueobjects.SamplingPlanStatus;
import com.aquamonitor.platform.u202120011.monitoring.domain.model.valueobjects.SamplingWindow;
import com.aquamonitor.platform.u202120011.monitoring.domain.model.valueobjects.StationId;
import com.aquamonitor.platform.u202120011.shared.shared.domain.aggregates.AbstractDomainAggregateRoot;

import java.time.Instant;
import java.util.Objects;

public final class SamplingPlan extends AbstractDomainAggregateRoot {
    private Long id;
    private final PlanCode planCode;
    private final StationId stationId;
    private final SamplingWindow samplingWindow;
    private final SampleVolume sampleVolume;
    private final Integer priorityLevel;
    private final SamplingPlanStatus status;
    private final String samplingNotes;
    private final Instant createdAt;
    private final Instant updatedAt;

    private SamplingPlan(Long id, PlanCode planCode, StationId stationId,
                         SamplingWindow samplingWindow, SampleVolume sampleVolume,
                         Integer priorityLevel, SamplingPlanStatus status,
                         String samplingNotes, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.planCode = Objects.requireNonNull(planCode);
        this.stationId = Objects.requireNonNull(stationId);
        this.samplingWindow = Objects.requireNonNull(samplingWindow);
        this.sampleVolume = Objects.requireNonNull(sampleVolume);
        this.priorityLevel = validatePriorityLevel(priorityLevel);
        this.status = status == null ? SamplingPlanStatus.PENDING : status;
        this.samplingNotes = samplingNotes;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static SamplingPlan create(PlanCode planCode, StationId stationId,
                                      SamplingWindow samplingWindow, SampleVolume sampleVolume,
                                      Integer priorityLevel, SamplingPlanStatus status,
                                      String samplingNotes, Instant now) {
        SamplingPlan plan = new SamplingPlan(
                null,
                planCode,
                stationId,
                samplingWindow,
                sampleVolume,
                priorityLevel,
                status,
                samplingNotes,
                Objects.requireNonNull(now),
                Objects.requireNonNull(now)
        );
        plan.registerEvent(new SamplingPlanCreatedEvent(stationId.value(), now));
        return plan;
    }

    public static SamplingPlan rehydrate(Long id, PlanCode planCode, StationId stationId,
                                         SamplingWindow samplingWindow, SampleVolume sampleVolume,
                                         Integer priorityLevel, SamplingPlanStatus status,
                                         String samplingNotes, Instant createdAt, Instant updatedAt) {
        if (id == null) {
            throw new IllegalArgumentException("sampling.plan.error.id.notNull");
        }
        return new SamplingPlan(id, planCode, stationId, samplingWindow, sampleVolume,
                priorityLevel, status, samplingNotes, createdAt, updatedAt);
    }

    private static Integer validatePriorityLevel(Integer priorityLevel) {
        if (priorityLevel == null || priorityLevel < 1 || priorityLevel > 5) {
            throw new IllegalArgumentException("sampling.plan.error.priorityLevel.range");
        }
        return priorityLevel;
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("sampling.plan.error.id.alreadyAssigned");
        }
        if (id == null) {
            throw new IllegalArgumentException("sampling.plan.error.id.notNull");
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public PlanCode getPlanCode() {
        return planCode;
    }

    public StationId getStationId() {
        return stationId;
    }

    public SamplingWindow getSamplingWindow() {
        return samplingWindow;
    }

    public SampleVolume getSampleVolume() {
        return sampleVolume;
    }

    public Integer getPriorityLevel() {
        return priorityLevel;
    }

    public SamplingPlanStatus getStatus() {
        return status;
    }

    public String getSamplingNotes() {
        return samplingNotes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
