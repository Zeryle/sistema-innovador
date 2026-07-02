package com.utp.myapp.vehicle.application.handler;

import com.utp.myapp.shared.infraestructure.config.TenantContext;
import com.utp.myapp.vehicle.application.assembler.VehicleAssembler;
import com.utp.myapp.vehicle.application.command.RegisterVehicleCommand;
import com.utp.myapp.vehicle.application.dto.VehicleDto;
import com.utp.myapp.vehicle.domain.model.aggregates.Vehicle;
import com.utp.myapp.vehicle.domain.model.exceptions.DuplicatePlateException;
import com.utp.myapp.vehicle.domain.model.repository.IVehicleRepository;
import com.utp.myapp.vehicle.domain.model.valueobjects.FuelType;
import com.utp.myapp.vehicle.domain.model.valueobjects.LicensePlate;
import com.utp.myapp.vehicle.domain.model.valueobjects.Mileage;
import com.utp.myapp.vehicle.domain.model.valueobjects.VIN;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterVehicleCommandHandler {

    private final IVehicleRepository vehicleRepository;
    private final VehicleAssembler vehicleAssembler;

    @Transactional
    public VehicleDto handle(RegisterVehicleCommand command) {
        // Check duplicate plate
        vehicleRepository.findByPlate(command.getPlate()).ifPresent(v -> {
            throw new DuplicatePlateException(command.getPlate());
        });

        Vehicle vehicle = new Vehicle.Builder()
                .customerId(command.getCustomerId())
                .make(command.getMake())
                .model(command.getModel())
                .year(command.getYear())
                .plate(LicensePlate.of(command.getPlate()))
                .color(command.getColor())
                .vin(command.getVin() != null ? VIN.of(command.getVin()) : null)
                .mileage(Mileage.ofKm(command.getMileage()))
                .fuelType(command.getFuelType() != null ? command.getFuelType() : FuelType.GASOLINE)
                .tenantId(TenantContext.getTenantId())
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleAssembler.toDto(saved);
    }
}
