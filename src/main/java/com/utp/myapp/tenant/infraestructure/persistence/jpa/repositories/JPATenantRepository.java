package com.utp.myapp.tenant.infraestructure.persistence.jpa.repositories;

import com.utp.myapp.tenant.infraestructure.persistence.jpa.entities.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JPATenantRepository extends JpaRepository<TenantEntity, Long> {
}
