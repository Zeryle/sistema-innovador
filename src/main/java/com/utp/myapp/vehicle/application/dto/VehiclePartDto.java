package com.utp.myapp.vehicle.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehiclePartDto {
    private Long id;
    private Long vehicleId;
    private Long partCategoryId;
    private String name;
    private String condition;
    private LocalDate lastInspectionDate;
    private String imageUrl;
    private String notes;
}
