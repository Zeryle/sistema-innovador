package com.utp.myapp.vehicle.application.assembler;

import com.utp.myapp.sales.domain.model.aggregates.Customer;
import com.utp.myapp.sales.domain.model.repository.ICustomerRepository;
import com.utp.myapp.vehicle.application.dto.VehicleDto;
import com.utp.myapp.vehicle.application.dto.VehicleSummaryDto;
import com.utp.myapp.vehicle.domain.model.aggregates.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehicleAssembler {

    private final ICustomerRepository customerRepository;

    public VehicleDto toDto(Vehicle vehicle) {
        if (vehicle == null) return null;
        String customerName = "";
        if (vehicle.getCustomerId() != null) {
            Customer c = customerRepository.listById(vehicle.getCustomerId());
            if (c != null) customerName = c.getFullName();
        }
        return VehicleDto.builder()
                .id(vehicle.getId())
                .customerId(vehicle.getCustomerId())
                .customerName(customerName)
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .plate(vehicle.getPlate() != null ? vehicle.getPlate().value() : null)
                .color(vehicle.getColor())
                .vin(vehicle.getVin() != null ? vehicle.getVin().value() : null)
                .mileage(vehicle.getMileage() != null ? vehicle.getMileage().value() : 0)
                .mileageUnit(vehicle.getMileage() != null ? vehicle.getMileage().unit().name() : "KM")
                .fuelType(vehicle.getFuelType())
                .imageUrls(vehicle.getImageUrls())
                .tenantId(vehicle.getTenantId())
                .build();
    }

    public VehicleSummaryDto toSummaryDto(Vehicle vehicle) {
        if (vehicle == null) return null;
        String customerName = "";
        if (vehicle.getCustomerId() != null) {
            Customer c = customerRepository.listById(vehicle.getCustomerId());
            if (c != null) customerName = c.getFullName();
        }
        return VehicleSummaryDto.builder()
                .id(vehicle.getId())
                .plate(vehicle.getPlate() != null ? vehicle.getPlate().value() : null)
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .customerName(customerName)
                .build();
    }
}
