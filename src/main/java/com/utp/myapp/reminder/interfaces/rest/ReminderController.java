package com.utp.myapp.reminder.interfaces.rest;

import com.utp.myapp.sales.domain.model.aggregates.Customer;
import com.utp.myapp.sales.domain.model.repository.ICustomerRepository;
import com.utp.myapp.shared.domain.model.exceptions.EntityNotFoundException;
import com.utp.myapp.shared.infraestructure.config.TenantContext;
import com.utp.myapp.shared.infraestructure.web.ApiResponse;
import com.utp.myapp.vehicle.domain.model.aggregates.Vehicle;
import com.utp.myapp.vehicle.domain.model.repository.IVehicleRepository;
import com.utp.myapp.reminder.domain.model.aggregates.Reminder;
import com.utp.myapp.reminder.domain.model.repository.IReminderRepository;
import com.utp.myapp.reminder.domain.model.valueobjects.NotificationChannel;
import com.utp.myapp.reminder.domain.model.valueobjects.ReminderType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final IReminderRepository repository;
    private final ICustomerRepository customerRepository;
    private final IVehicleRepository vehicleRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll() {
        String tenantId = TenantContext.getTenantId();
        List<Map<String, Object>> result = repository.findByTenantId(tenantId).stream()
                .map(this::toMap)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(r -> ResponseEntity.ok(ApiResponse.ok(toMap(r))))
                .orElseThrow(() -> new EntityNotFoundException("Reminder", id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(@RequestBody Map<String, Object> body) {
        String tenantId = TenantContext.getTenantId();
        Reminder r = new Reminder.Builder()
                .customerId(body.get("customerId") != null ? ((Number) body.get("customerId")).intValue() : null)
                .vehicleId(body.get("vehicleId") != null ? ((Number) body.get("vehicleId")).longValue() : null)
                .tenantId(tenantId)
                .type(ReminderType.valueOf((String) body.get("type")))
                .title((String) body.get("title"))
                .message((String) body.getOrDefault("message", ""))
                .scheduledDate(LocalDateTime.parse((String) body.get("scheduledDate")))
                .channel(body.get("channel") != null ? NotificationChannel.valueOf((String) body.get("channel")) : NotificationChannel.WHATSAPP)
                .build();
        Reminder saved = repository.save(r);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toMap(saved)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable Long id) {
        repository.findById(id).ifPresent(r -> {
            r.cancel();
            repository.save(r);
        });
        return ResponseEntity.ok(ApiResponse.ok(null, "Reminder cancelled"));
    }

    private Map<String, Object> toMap(Reminder r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", r.getId());
        m.put("customerId", r.getCustomerId());
        m.put("vehicleId", r.getVehicleId());
        m.put("tenantId", r.getTenantId());
        m.put("type", r.getType() != null ? r.getType().name() : null);
        m.put("title", r.getTitle());
        m.put("message", r.getMessage());
        m.put("scheduledDate", r.getScheduledDate() != null ? r.getScheduledDate().toString() : null);
        m.put("status", r.getStatus() != null ? r.getStatus().name() : null);
        m.put("channel", r.getChannel() != null ? r.getChannel().name() : null);

        // Enrich with names
        if (r.getCustomerId() != null) {
            Customer c = customerRepository.listById(r.getCustomerId());
            if (c != null) m.put("customerName", c.getFullName());
        }
        if (r.getVehicleId() != null) {
            vehicleRepository.findById(r.getVehicleId()).ifPresent(v ->
                m.put("vehiclePlate", v.getPlate() != null ? v.getPlate().value() : "")
            );
        }
        return m;
    }
}
