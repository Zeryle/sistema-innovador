package com.utp.myapp.reminder.infraestructure.persistence.jpa.repositories;

import com.utp.myapp.reminder.infraestructure.persistence.jpa.entities.ReminderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface JPAReminderRepository extends JpaRepository<ReminderEntity, Long> {

    List<ReminderEntity> findByCustomerId(Integer customerId);

    List<ReminderEntity> findByVehicleId(Long vehicleId);

    List<ReminderEntity> findByTenantId(String tenantId);

    @Query("SELECT r FROM ReminderEntity r WHERE r.status = 'SCHEDULED' AND r.scheduledDate <= :dateTime")
    List<ReminderEntity> findPendingBefore(@Param("dateTime") LocalDateTime dateTime);
}
