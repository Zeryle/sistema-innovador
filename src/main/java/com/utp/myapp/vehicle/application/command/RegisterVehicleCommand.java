package com.utp.myapp.vehicle.application.command;

import com.utp.myapp.vehicle.domain.model.valueobjects.FuelType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class RegisterVehicleCommand {
    private final Integer customerId;
    private final String make;
    private final String model;
    private final int year;
    private final String plate;
    private final String color;
    private final String vin;
    private final int mileage;
    private final FuelType fuelType;
    private final Integer cylinderCapacity;
    private final Integer numDoors;
    private final String transmissionType;
}
