package com.utp.myapp.billing.infraestructure.persistence.jpa.repositories;

import com.utp.myapp.billing.domain.model.valueobjects.CheckoutStatus;
import com.utp.myapp.billing.infraestructure.persistence.jpa.entities.CheckoutSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JPACheckoutSessionRepository extends JpaRepository<CheckoutSessionEntity, String> {
    Optional<CheckoutSessionEntity> findByProviderIntentId(String providerIntentId);
    List<CheckoutSessionEntity> findByTenantIdOrderByCreatedAtDesc(String tenantId);
    List<CheckoutSessionEntity> findByStatus(CheckoutStatus status);
}
