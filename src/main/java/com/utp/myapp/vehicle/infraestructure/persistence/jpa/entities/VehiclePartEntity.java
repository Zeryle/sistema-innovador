package com.utp.myapp.vehicle.infraestructure.persistence.jpa.entities;

import com.utp.myapp.shared.infraestructure.persistence.jpa.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "vehicle_part")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehiclePartEntity extends BaseEntity {

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(name = "part_category_id")
    private Long partCategoryId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "condition_description", length = 50)
    private String conditionDescription;

    @Column(name = "last_inspection_date")
    private LocalDate lastInspectionDate;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "notes", length = 1000)
    private String notes;
}
