package com.aquamonitor.platform.u202120011.monitoring.domain.model.valueobjects;

import java.util.UUID;
import java.util.regex.Pattern;

public record StationId(UUID value) {
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    private static final String NOT_NULL_MESSAGE_KEY = "sampling.plan.error.stationId.notNull";
    private static final String INVALID_UUID_MESSAGE_KEY = "sampling.plan.error.stationId.invalidUuid";

    public StationId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
        if (!UUID_PATTERN.matcher(value.toString()).matches()) {
            throw new IllegalArgumentException(INVALID_UUID_MESSAGE_KEY);
        }
    }
}
