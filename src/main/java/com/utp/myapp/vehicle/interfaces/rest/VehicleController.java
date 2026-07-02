package com.utp.myapp.vehicle.interfaces.rest;

import com.utp.myapp.shared.domain.model.exceptions.EntityNotFoundException;
import com.utp.myapp.shared.infraestructure.web.ApiResponse;
import com.utp.myapp.vehicle.application.assembler.VehicleAssembler;
import com.utp.myapp.vehicle.application.command.RegisterVehicleCommand;
import com.utp.myapp.vehicle.application.dto.VehicleDto;
import com.utp.myapp.vehicle.application.dto.VehicleSummaryDto;
import com.utp.myapp.vehicle.application.handler.GetVehiclesByCustomerQueryHandler;
import com.utp.myapp.vehicle.application.handler.RegisterVehicleCommandHandler;
import com.utp.myapp.vehicle.domain.model.repository.IVehicleRepository;
import com.utp.myapp.vehicle.infraestructure.storage.LocalFileStorageAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final RegisterVehicleCommandHandler registerHandler;
    private final GetVehiclesByCustomerQueryHandler queryHandler;
    private final IVehicleRepository vehicleRepository;
    private final VehicleAssembler vehicleAssembler;
    private final LocalFileStorageAdapter fileStorage;

    @PostMapping
    public ResponseEntity<ApiResponse<VehicleDto>> register(@RequestBody RegisterVehicleCommand command) {
        VehicleDto result = registerHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleDto>> getById(@PathVariable Long id) {
        return vehicleRepository.findById(id)
                .map(v -> ResponseEntity.ok(ApiResponse.ok(vehicleAssembler.toDto(v))))
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleSummaryDto>>> search(
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<VehicleSummaryDto> results;
        if (customerId != null) {
            results = queryHandler.handle(customerId);
        } else {
            results = queryHandler.search(query, page, size);
        }
        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return vehicleRepository.findById(id)
                .map(v -> {
                    String url = fileStorage.store(file, "vehicles/" + id);
                    v.addImageUrl(url);
                    vehicleRepository.save(v);
                    return ResponseEntity.ok(ApiResponse.ok(url, "Image uploaded"));
                })
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        vehicleRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Vehicle deleted"));
    }
}
