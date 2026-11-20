package com.aquamonitor.platform.u202120011.monitoring.domain.model.valueobjects;

import java.time.LocalDate;

public record SamplingWindow(LocalDate startDate, LocalDate endDate) {
    public SamplingWindow {
        if (startDate == null) {
            throw new IllegalArgumentException("sampling.plan.error.samplingWindow.startDate.notNull");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("sampling.plan.error.samplingWindow.endDate.notNull");
        }
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("sampling.plan.error.samplingWindow.endDate.before.startDate");
        }
    }

    public boolean contains(LocalDate checkDate) {
        if (checkDate == null) {
            throw new IllegalArgumentException("sampling.plan.error.samplingWindow.checkDate.notNull");
        }
        return !checkDate.isBefore(startDate) && !checkDate.isAfter(endDate);
    }
}
