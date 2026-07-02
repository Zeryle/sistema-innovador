package com.utp.myapp.vehicle.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleSummaryDto {
    private Long id;
    private String plate;
    private String make;
    private String model;
    private int year;
    private String customerName;
}
