package com.utp.myapp.reminder.infraestructure.persistence.jpa.entities;

import com.utp.myapp.shared.infraestructure.persistence.jpa.entities.BaseEntity;
import com.utp.myapp.reminder.domain.model.valueobjects.NotificationChannel;
import com.utp.myapp.reminder.domain.model.valueobjects.ReminderStatus;
import com.utp.myapp.reminder.domain.model.valueobjects.ReminderType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reminder")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderEntity extends BaseEntity {

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private ReminderType type;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", length = 1000)
    private String message;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDateTime scheduledDate;

    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReminderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", length = 10)
    private NotificationChannel channel;

    @Column(name = "interval_days")
    private int intervalDays;

    @Column(name = "max_occurrences")
    private int maxOccurrences;
}
