package com.utp.myapp.auth.infraestructure.persistence.jpa.entities;

import com.utp.myapp.auth.domain.model.valueobjects.Role;
import com.utp.myapp.shared.infraestructure.persistence.jpa.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_entity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity extends BaseEntity {

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "active", nullable = false)
    private boolean active;
}
