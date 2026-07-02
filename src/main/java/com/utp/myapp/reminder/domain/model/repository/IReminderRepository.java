package com.utp.myapp.reminder.domain.model.repository;

import com.utp.myapp.reminder.domain.model.aggregates.Reminder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IReminderRepository {
    Reminder save(Reminder reminder);
    Optional<Reminder> findById(Long id);
    List<Reminder> findByCustomerId(Integer customerId);
    List<Reminder> findByVehicleId(Long vehicleId);
    List<Reminder> findPendingBefore(LocalDateTime dateTime);
    List<Reminder> findByTenantId(String tenantId);
    void deleteById(Long id);
}
