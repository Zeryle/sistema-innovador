package com.utp.myapp.tenant.domain.model.repository;

import com.utp.myapp.shared.domain.model.valueobjects.TenantId;
import com.utp.myapp.tenant.domain.model.aggregates.Tenant;

import java.util.List;
import java.util.Optional;

/**
 * Domain port for Tenant persistence operations.
 */
public interface ITenantRepository {

    Tenant save(Tenant tenant);

    Optional<Tenant> findById(TenantId id);

    boolean existsById(TenantId id);

    List<Tenant> findAll();
}