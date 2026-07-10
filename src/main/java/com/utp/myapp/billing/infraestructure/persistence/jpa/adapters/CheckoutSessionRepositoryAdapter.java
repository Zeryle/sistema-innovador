package com.utp.myapp.billing.infraestructure.persistence.jpa.adapters;

import com.utp.myapp.billing.domain.model.aggregates.CheckoutSession;
import com.utp.myapp.billing.domain.model.repository.ICheckoutRepository;
import com.utp.myapp.billing.domain.model.valueobjects.CheckoutStatus;
import com.utp.myapp.billing.infraestructure.persistence.jpa.mappers.CheckoutSessionMapper;
import com.utp.myapp.billing.infraestructure.persistence.jpa.repositories.JPACheckoutSessionRepository;
import com.utp.myapp.shared.domain.model.valueobjects.TenantId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adapter for {@link CheckoutSession} persistence. Implements the domain port
 * {@link ICheckoutRepository}.
 *
 * Important detail: in JPA, {@code save()} returns {@code merge} semantics — it
 * inserts if the entity is new and updates otherwise. To avoid duplicate
 * objects in the persistence context (which would throw NonUniqueObjectException
 * on a second save within the same session), we always load the existing entity
 * first and apply the aggregate's state on top of it. This keeps the aggregate
 * the source of truth and lets JPA handle the persistence correctly.
 */
@Component
@RequiredArgsConstructor
@Transactional
public class CheckoutSessionRepositoryAdapter implements ICheckoutRepository {

    private final JPACheckoutSessionRepository jpa;
    private final CheckoutSessionMapper mapper;

    @Override
    public CheckoutSession insert(CheckoutSession s) {
        // For a fresh insert there is no existing row yet — we save and rely on
        // the JPA layer to set @Version, createdAt, updatedAt.
        var entity = mapper.toEntity(s);
        if (jpa.existsById(s.id())) {
            // Race: another thread inserted with the same id. Reload + apply.
            var existing = jpa.findById(s.id()).orElseThrow();
            mapper.applyStatus(existing, s);
            existing.setSucceededAt(s.succeededAt());
            existing.setCancelledAt(s.cancelledAt());
            return mapper.toDomain(jpa.save(existing));
        }
        return mapper.toDomain(jpa.save(entity));
    }

    @Override
    public CheckoutSession update(CheckoutSession s) {
        var existing = jpa.findById(s.id())
                .orElseThrow(() -> new IllegalStateException("CheckoutSession not found: " + s.id()));
        mapper.applyStatus(existing, s);
        existing.setSucceededAt(s.succeededAt());
        existing.setCancelledAt(s.cancelledAt());
        return mapper.toDomain(jpa.save(existing));
    }

    @Override
    public void delete(String id) {
        jpa.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CheckoutSession> listById(String id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckoutSession> listAll() {
        return jpa.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CheckoutSession> findByProviderIntentId(String intentId) {
        return jpa.findByProviderIntentId(intentId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckoutSession> findOpenByTenantId(TenantId tenantId) {
        return jpa.findByTenantIdOrderByCreatedAtDesc(tenantId.value()).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckoutSession> findByStatus(CheckoutStatus status) {
        return jpa.findByStatus(status).stream().map(mapper::toDomain).toList();
    }
}