package com.utp.myapp.vehicle.application.handler;

import com.utp.myapp.shared.infraestructure.config.TenantContext;
import com.utp.myapp.vehicle.application.assembler.VehicleAssembler;
import com.utp.myapp.vehicle.application.dto.VehicleSummaryDto;
import com.utp.myapp.vehicle.domain.model.repository.IVehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetVehiclesByCustomerQueryHandler {

    private final IVehicleRepository vehicleRepository;
    private final VehicleAssembler vehicleAssembler;

    public List<VehicleSummaryDto> handle(Integer customerId) {
        return vehicleRepository.findByCustomerId(customerId).stream()
                .map(vehicleAssembler::toSummaryDto)
                .toList();
    }

    public List<VehicleSummaryDto> search(String query, int page, int size) {
        String tenantId = TenantContext.getTenantId();
        if (query != null && !query.isBlank()) {
            return vehicleRepository.searchByTenant(tenantId, query, page, size).stream()
                    .map(vehicleAssembler::toSummaryDto)
                    .toList();
        }
        return vehicleRepository.findByTenantId(tenantId, page, size).stream()
                .map(vehicleAssembler::toSummaryDto)
                .toList();
    }
}
