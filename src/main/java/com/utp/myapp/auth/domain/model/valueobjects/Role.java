package com.utp.myapp.auth.domain.model.valueobjects;

/**
 * User roles.
 */
public enum Role {
    OWNER,        // Dueño del taller — full access
    MECHANIC,     // Mecánico — can manage work orders
    RECEPTIONIST  // Recepcionista — can manage customers and appointments
}
