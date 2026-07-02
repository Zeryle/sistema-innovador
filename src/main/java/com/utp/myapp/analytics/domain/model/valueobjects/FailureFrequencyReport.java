package com.utp.myapp.analytics.domain.model.valueobjects;

public record FailureFrequencyReport(
        String partName,
        String vehicleMake,
        String vehicleModel,
        long failureCount,
        double percentage,
        double avgMileageAtFailure
) {}
