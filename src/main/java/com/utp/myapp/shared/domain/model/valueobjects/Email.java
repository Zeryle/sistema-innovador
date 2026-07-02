package com.utp.myapp.shared.domain.model.valueobjects;

import java.util.Objects;

public record Email(String value) {

    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,}$";

    public Email {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!value.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
    }

    public static Email of(String value) {
        return new Email(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return Objects.equals(value.toLowerCase(), email.value.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.toLowerCase());
    }

    @Override
    public String toString() {
        return value;
    }
}
