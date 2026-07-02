package com.utp.myapp.vehicle.infraestructure.persistence.jpa.entities;

import com.utp.myapp.shared.infraestructure.persistence.jpa.entities.BaseEntity;
import com.utp.myapp.vehicle.domain.model.valueobjects.FuelType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleEntity extends BaseEntity {

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "make", nullable = false, length = 50)
    private String make;

    @Column(name = "model", nullable = false, length = 50)
    private String model;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "plate", nullable = false, unique = true, length = 10)
    private String plate;

    @Column(name = "color", length = 30)
    private String color;

    @Column(name = "vin", unique = true, length = 17)
    private String vin;

    @Column(name = "mileage_value")
    private int mileageValue;

    @Column(name = "mileage_unit", length = 5)
    private String mileageUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", length = 15)
    private FuelType fuelType;

    @Column(name = "image_urls", length = 2000)
    private String imageUrls; // JSON array stored as string
}
