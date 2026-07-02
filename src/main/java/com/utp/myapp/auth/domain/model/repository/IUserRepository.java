package com.utp.myapp.auth.domain.model.repository;

import com.utp.myapp.auth.domain.model.aggregates.User;
import com.utp.myapp.shared.domain.model.valueobjects.Email;

import java.util.List;
import java.util.Optional;

/**
 * Domain port for User persistence operations.
 */
public interface IUserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(Email email);

    List<User> findByTenantId(String tenantId);

    boolean existsByEmail(Email email);

    void deleteById(Long id);
}
