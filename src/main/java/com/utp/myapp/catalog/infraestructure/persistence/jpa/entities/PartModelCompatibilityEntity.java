package com.utp.myapp.catalog.infraestructure.persistence.jpa.entities;

import com.utp.myapp.shared.infraestructure.persistence.jpa.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "part_model_compatibility")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartModelCompatibilityEntity extends BaseEntity {

    @Column(name = "part_category_id", nullable = false)
    private Long partCategoryId;

    @Column(name = "vehicle_make", nullable = false, length = 50)
    private String vehicleMake;

    @Column(name = "vehicle_model", nullable = false, length = 50)
    private String vehicleModel;

    @Column(name = "year_from", nullable = false)
    private int yearFrom;

    @Column(name = "year_to", nullable = false)
    private int yearTo;
}
