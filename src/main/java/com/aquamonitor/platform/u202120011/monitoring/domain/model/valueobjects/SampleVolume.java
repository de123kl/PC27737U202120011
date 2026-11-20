package com.aquamonitor.platform.u202120011.monitoring.domain.model.valueobjects;

public record SampleVolume(Integer quantity, String unit) {
    public SampleVolume {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("sampling.plan.error.sampleVolume.quantity.notNullOrPositive");
        }
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("sampling.plan.error.sampleVolume.unit.notNullOrBlank");
        }
    }

    public SampleVolume addVolume(Integer additionalVolume) {
        if (additionalVolume == null || additionalVolume <= 0) {
            throw new IllegalArgumentException("sampling.plan.error.sampleVolume.additionalVolume.notNullOrPositive");
        }
        if (additionalVolume > Integer.MAX_VALUE - quantity) {
            throw new IllegalArgumentException("sampling.plan.error.sampleVolume.resultingVolume.exceedsMaxValue");
        }
        return new SampleVolume(quantity + additionalVolume, unit);
    }
}
