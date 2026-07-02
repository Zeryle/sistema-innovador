package com.utp.myapp.auth.domain.model.valueobjects;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

/**
 * Value object that encapsulates a BCrypt-hashed password.
 */
public record HashedPassword(String hash) {

    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    public HashedPassword {
        if (hash == null || hash.trim().isEmpty()) {
            throw new IllegalArgumentException("Password hash cannot be null or empty");
        }
    }

    /**
     * Creates a HashedPassword from a raw (plaintext) password.
     */
    public static HashedPassword fromRaw(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        return new HashedPassword(ENCODER.encode(rawPassword));
    }

    /**
     * Creates a HashedPassword from an already-hashed value (e.g., from DB).
     */
    public static HashedPassword fromHash(String hash) {
        return new HashedPassword(hash);
    }

    /**
     * Verifies a raw password against this hash.
     */
    public boolean matches(String rawPassword) {
        return ENCODER.matches(rawPassword, this.hash);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HashedPassword that)) return false;
        return Objects.equals(hash, that.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(hash);
    }

    @Override
    public String toString() {
        return hash;
    }
}
