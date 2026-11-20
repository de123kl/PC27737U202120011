package com.aquamonitor.platform.u202120011.monitoring.infrastructure.persistence.jpa.entities;

import com.aquamonitor.platform.u202120011.monitoring.domain.model.valueobjects.SamplingPlanStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "sampling_plans",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_sampling_plan_plan_code",
                columnNames = "plan_code"
        ))
public class SamplingPlanJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_code", nullable = false)
    private String planCode;

    @Column(name = "station_id", nullable = false)
    private UUID stationId;

    @Column(name = "sampling_window_start_date", nullable = false)
    private LocalDate samplingWindowStartDate;

    @Column(name = "sampling_window_end_date", nullable = false)
    private LocalDate samplingWindowEndDate;

    @Column(name = "sample_volume_quantity", nullable = false)
    private Integer sampleVolumeQuantity;

    @Column(name = "sample_volume_unit", nullable = false)
    private String sampleVolumeUnit;

    @Column(name = "priority_level", nullable = false)
    private Integer priorityLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SamplingPlanStatus status;

    @Column(name = "sampling_notes")
    private String samplingNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected SamplingPlanJpaEntity() {
    }

    public SamplingPlanJpaEntity(Long id, String planCode, UUID stationId,
                                 LocalDate samplingWindowStartDate,
                                 LocalDate samplingWindowEndDate,
                                 Integer sampleVolumeQuantity, String sampleVolumeUnit,
                                 Integer priorityLevel, SamplingPlanStatus status,
                                 String samplingNotes, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.planCode = planCode;
        this.stationId = stationId;
        this.samplingWindowStartDate = samplingWindowStartDate;
        this.samplingWindowEndDate = samplingWindowEndDate;
        this.sampleVolumeQuantity = sampleVolumeQuantity;
        this.sampleVolumeUnit = sampleVolumeUnit;
        this.priorityLevel = priorityLevel;
        this.status = status;
        this.samplingNotes = samplingNotes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getPlanCode() {
        return planCode;
    }

    public UUID getStationId() {
        return stationId;
    }

    public LocalDate getSamplingWindowStartDate() {
        return samplingWindowStartDate;
    }

    public LocalDate getSamplingWindowEndDate() {
        return samplingWindowEndDate;
    }

    public Integer getSampleVolumeQuantity() {
        return sampleVolumeQuantity;
    }

    public String getSampleVolumeUnit() {
        return sampleVolumeUnit;
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
