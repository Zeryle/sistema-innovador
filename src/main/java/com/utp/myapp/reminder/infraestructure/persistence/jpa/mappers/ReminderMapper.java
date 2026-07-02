package com.utp.myapp.reminder.infraestructure.persistence.jpa.mappers;

import com.utp.myapp.reminder.domain.model.aggregates.Reminder;
import com.utp.myapp.reminder.infraestructure.persistence.jpa.entities.ReminderEntity;
import org.springframework.stereotype.Component;

@Component
public class ReminderMapper {

    public Reminder toDomain(ReminderEntity entity) {
        if (entity == null) return null;
        return new Reminder.Builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .vehicleId(entity.getVehicleId())
                .tenantId(entity.getTenantId())
                .type(entity.getType())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .scheduledDate(entity.getScheduledDate())
                .channel(entity.getChannel())
                .intervalDays(entity.getIntervalDays())
                .build();
    }

    public ReminderEntity toEntity(Reminder domain) {
        if (domain == null) return null;
        ReminderEntity entity = new ReminderEntity();
        entity.setId(domain.getId());
        entity.setCustomerId(domain.getCustomerId());
        entity.setVehicleId(domain.getVehicleId());
        entity.setTenantId(domain.getTenantId());
        entity.setType(domain.getType());
        entity.setTitle(domain.getTitle());
        entity.setMessage(domain.getMessage());
        entity.setScheduledDate(domain.getScheduledDate());
        entity.setStatus(domain.getStatus());
        entity.setChannel(domain.getChannel());
        entity.setIntervalDays(domain.getIntervalDays());
        return entity;
    }
}
