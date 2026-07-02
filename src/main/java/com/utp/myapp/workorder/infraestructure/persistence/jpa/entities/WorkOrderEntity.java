package com.utp.myapp.workorder.infraestructure.persistence.jpa.entities;

import com.utp.myapp.shared.infraestructure.persistence.jpa.entities.BaseEntity;
import com.utp.myapp.workorder.domain.model.valueobjects.Priority;
import com.utp.myapp.workorder.domain.model.valueobjects.WorkOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrderEntity extends BaseEntity {

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "mechanic_id")
    private Long mechanicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private WorkOrderStatus status;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "diagnostic_notes", length = 2000)
    private String diagnosticNotes;

    @Column(name = "estimated_cost", precision = 10, scale = 2)
    private BigDecimal estimatedCost;

    @Column(name = "final_cost", precision = 10, scale = 2)
    private BigDecimal finalCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 10)
    private Priority priority;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "estimated_end_date")
    private LocalDateTime estimatedEndDate;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;
}
