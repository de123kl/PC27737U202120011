package com.aquamonitor.platform.u202120011.shared.shared.domain.events;

import java.time.Instant;

public interface DomainEvent {
    Instant occurredAt();
}
