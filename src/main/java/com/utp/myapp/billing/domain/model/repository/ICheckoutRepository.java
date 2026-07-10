package com.utp.myapp.billing.domain.model.repository;

import com.utp.myapp.billing.domain.model.aggregates.CheckoutSession;
import com.utp.myapp.billing.domain.model.valueobjects.CheckoutStatus;
import com.utp.myapp.shared.domain.model.valueobjects.TenantId;

import java.util.List;
import java.util.Optional;

public interface ICheckoutRepository extends ICRUD<CheckoutSession> {
    Optional<CheckoutSession> findByProviderIntentId(String intentId);
    List<CheckoutSession> findOpenByTenantId(TenantId tenantId);
    List<CheckoutSession> findByStatus(CheckoutStatus status);
}
