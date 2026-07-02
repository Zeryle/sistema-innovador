package com.utp.myapp.shared.domain.model.exceptions;

/**
 * Thrown when an entity cannot be found by its identifier.
 */
public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String entityName, Object id) {
        super(entityName + " not found with id: " + id, "ENTITY_NOT_FOUND");
    }

    public EntityNotFoundException(String message) {
        super(message, "ENTITY_NOT_FOUND");
    }
}
