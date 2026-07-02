package com.utp.myapp.auth.infraestructure.persistence.jpa.mappers;

import com.utp.myapp.auth.domain.model.aggregates.User;
import com.utp.myapp.auth.domain.model.valueobjects.HashedPassword;
import com.utp.myapp.auth.infraestructure.persistence.jpa.entities.UserEntity;
import com.utp.myapp.shared.domain.model.valueobjects.Email;
import com.utp.myapp.shared.domain.model.valueobjects.TenantId;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return new User.Builder()
                .id(entity.getId())
                .email(Email.of(entity.getEmail()))
                .passwordHash(HashedPassword.fromHash(entity.getPasswordHash()))
                .role(entity.getRole())
                .tenantId(TenantId.of(entity.getTenantId()))
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setEmail(user.getEmail().value());
        entity.setPasswordHash(user.getPasswordHash().hash());
        entity.setRole(user.getRole());
        entity.setTenantId(user.getTenantId().value());
        entity.setActive(user.isActive());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        return entity;
    }
}
