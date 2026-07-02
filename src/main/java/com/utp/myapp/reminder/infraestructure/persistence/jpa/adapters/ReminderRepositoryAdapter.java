package com.utp.myapp.reminder.infraestructure.persistence.jpa.adapters;

import com.utp.myapp.reminder.domain.model.aggregates.Reminder;
import com.utp.myapp.reminder.domain.model.repository.IReminderRepository;
import com.utp.myapp.reminder.infraestructure.persistence.jpa.entities.ReminderEntity;
import com.utp.myapp.reminder.infraestructure.persistence.jpa.mappers.ReminderMapper;
import com.utp.myapp.reminder.infraestructure.persistence.jpa.repositories.JPAReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReminderRepositoryAdapter implements IReminderRepository {

    private final JPAReminderRepository jpa;
    private final ReminderMapper mapper;

    @Override
    public Reminder save(Reminder reminder) {
        ReminderEntity entity = mapper.toEntity(reminder);
        ReminderEntity saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Reminder> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Reminder> findByCustomerId(Integer customerId) {
        return jpa.findByCustomerId(customerId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Reminder> findByVehicleId(Long vehicleId) {
        return jpa.findByVehicleId(vehicleId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Reminder> findPendingBefore(LocalDateTime dateTime) {
        return jpa.findPendingBefore(dateTime).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Reminder> findByTenantId(String tenantId) {
        return jpa.findByTenantId(tenantId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
