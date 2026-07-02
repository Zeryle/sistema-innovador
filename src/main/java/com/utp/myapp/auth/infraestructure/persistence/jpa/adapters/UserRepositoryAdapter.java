package com.utp.myapp.auth.infraestructure.persistence.jpa.adapters;

import com.utp.myapp.auth.domain.model.aggregates.User;
import com.utp.myapp.auth.domain.model.repository.IUserRepository;
import com.utp.myapp.auth.infraestructure.persistence.jpa.entities.UserEntity;
import com.utp.myapp.auth.infraestructure.persistence.jpa.mappers.UserMapper;
import com.utp.myapp.auth.infraestructure.persistence.jpa.repositories.JPAUserRepository;
import com.utp.myapp.shared.domain.model.valueobjects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@SuppressWarnings("null")
public class UserRepositoryAdapter implements IUserRepository {

    private final JPAUserRepository jpa;
    private final UserMapper mapper;

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpa.findByEmail(email.value()).map(mapper::toDomain);
    }

    @Override
    public List<User> findByTenantId(String tenantId) {
        return jpa.findByTenantId(tenantId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpa.existsByEmail(email.value());
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
