package com.utp.myapp.sales.infraestructure.persistence.jpa.entities;

import com.utp.myapp.shared.infraestructure.persistence.jpa.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEntity extends BaseEntity {

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "last_name", nullable = false, length = 30)
    private String lastName;

    @Column(name = "dni", nullable = false, length = 30)
    private String dni;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Embedded
    private AddressEmbeddable address;
}
