package com.utp.myapp.reminder.domain.model.aggregates;

import com.utp.myapp.shared.domain.model.aggregates.AuditableAggregateRoot;
import com.utp.myapp.reminder.domain.model.valueobjects.NotificationChannel;
import com.utp.myapp.reminder.domain.model.valueobjects.ReminderStatus;
import com.utp.myapp.reminder.domain.model.valueobjects.ReminderType;

import java.time.LocalDateTime;

public class Reminder extends AuditableAggregateRoot {

    private Long id;
    private Integer customerId;
    private Long vehicleId;
    private String tenantId;
    private ReminderType type;
    private String title;
    private String message;
    private LocalDateTime scheduledDate;
    private LocalDateTime sentDate;
    private ReminderStatus status;
    private NotificationChannel channel;
    private int intervalDays;
    private int maxOccurrences;

    private Reminder() {}

    public Long getId() { return id; }
    public Integer getCustomerId() { return customerId; }
    public Long getVehicleId() { return vehicleId; }
    public String getTenantId() { return tenantId; }
    public ReminderType getType() { return type; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public ReminderStatus getStatus() { return status; }
    public NotificationChannel getChannel() { return channel; }
    public LocalDateTime getSentDate() { return sentDate; }
    public int getIntervalDays() { return intervalDays; }
    public int getMaxOccurrences() { return maxOccurrences; }

    public void markSent() {
        this.status = ReminderStatus.SENT;
        this.sentDate = LocalDateTime.now();
        markUpdated();
    }

    public void cancel() {
        this.status = ReminderStatus.CANCELLED;
        markUpdated();
    }

    public static class Builder {
        private final Reminder r = new Reminder();
        public Builder id(Long id) { r.id = id; return this; }
        public Builder customerId(Integer id) { r.customerId = id; return this; }
        public Builder vehicleId(Long id) { r.vehicleId = id; return this; }
        public Builder tenantId(String id) { r.tenantId = id; return this; }
        public Builder type(ReminderType t) { r.type = t; return this; }
        public Builder title(String t) { r.title = t; return this; }
        public Builder message(String m) { r.message = m; return this; }
        public Builder scheduledDate(LocalDateTime d) { r.scheduledDate = d; return this; }
        public Builder channel(NotificationChannel c) { r.channel = c; return this; }
        public Builder intervalDays(int d) { r.intervalDays = d; return this; }
        public Reminder build() {
            if (r.status == null) {
                r.status = ReminderStatus.SCHEDULED;
            }
            return r;
        }
    }
}
