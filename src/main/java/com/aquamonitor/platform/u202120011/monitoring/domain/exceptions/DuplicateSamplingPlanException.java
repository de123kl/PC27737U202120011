package com.aquamonitor.platform.u202120011.monitoring.domain.exceptions;

public class DuplicateSamplingPlanException extends RuntimeException {
    public DuplicateSamplingPlanException() {
        super("sampling.plan.error.planCode.duplicate");
    }

    public DuplicateSamplingPlanException(String message) {
        super(message);
    }

    public DuplicateSamplingPlanException(Throwable cause) {
        super("sampling.plan.error.planCode.duplicate", cause);
    }

    public DuplicateSamplingPlanException(String message, Throwable cause) {
        super(message, cause);
    }
}
