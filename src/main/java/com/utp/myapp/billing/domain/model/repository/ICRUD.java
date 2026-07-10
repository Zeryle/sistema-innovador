package com.utp.myapp.billing.domain.model.repository;

import java.util.List;
import java.util.Optional;

/**
 * Minimal CRUD contract. Used by every billing-specific repository;
 * idempotent so multiple adapters can compose it without surprises.
 */
public interface ICRUD<T> {
    T insert(T entity);
    T update(T entity);
    void delete(String id);
    Optional<T> listById(String id);
    List<T> listAll();
}
