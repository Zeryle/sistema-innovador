package com.utp.myapp.vehicle.application.dto;

import com.utp.myapp.vehicle.domain.model.valueobjects.FuelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleDto {
    private Long id;
    private Integer customerId;
    private String customerName;
    private String make;
    private String model;
    private int year;
    private String plate;
    private String color;
    private String vin;
    private int mileage;
    private String mileageUnit;
    private FuelType fuelType;
    private List<String> imageUrls;
    private String tenantId;
}
