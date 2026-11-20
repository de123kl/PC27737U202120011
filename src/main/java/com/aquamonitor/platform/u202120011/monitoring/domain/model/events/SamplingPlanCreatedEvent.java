package com.aquamonitor.platform.u202120011.monitoring.domain.model.events;

import com.aquamonitor.platform.u202120011.shared.shared.domain.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SamplingPlanCreatedEvent(UUID stationId, Instant occurredAt)
        implements DomainEvent {
}
