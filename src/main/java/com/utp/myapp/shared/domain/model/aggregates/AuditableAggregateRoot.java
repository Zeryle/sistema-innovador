package com.utp.myapp.shared.domain.model.aggregates;

import java.time.LocalDateTime;

/**
 * Base class for all aggregate roots.
 * Provides audit metadata that is managed at the domain level.
 */
public abstract class AuditableAggregateRoot {

    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    protected Long version;

    protected AuditableAggregateRoot() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version = 0L;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    protected void markUpdated() {
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
}
