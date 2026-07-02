package com.utp.myapp.shared.domain.model.valueobjects;

import java.util.Objects;

public record PhoneNumber(String value, String countryCode) {

    private static final String PHONE_REGEX = "^\\+?[1-9]\\d{6,14}$";

    public PhoneNumber {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        String sanitized = value.replaceAll("[\\s\\-\\(\\)]", "");
        if (!sanitized.matches(PHONE_REGEX)) {
            throw new IllegalArgumentException("Invalid phone number format: " + value);
        }
        if (countryCode == null || countryCode.trim().isEmpty()) {
            countryCode = "51"; // Default Peru
        }
    }

    /**
     * Returns the phone number in WhatsApp-compatible format (country code + number, no + sign).
     */
    public String toWhatsAppFormat() {
        String sanitized = value.replaceAll("[\\s\\-\\(\\)\\+]", "");
        if (sanitized.startsWith(countryCode)) {
            return sanitized;
        }
        return countryCode + sanitized;
    }

    public static PhoneNumber of(String value) {
        return new PhoneNumber(value, "51");
    }

    public static PhoneNumber of(String value, String countryCode) {
        return new PhoneNumber(value, countryCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhoneNumber that)) return false;
        return Objects.equals(toWhatsAppFormat(), that.toWhatsAppFormat());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toWhatsAppFormat());
    }

    @Override
    public String toString() {
        return "+" + toWhatsAppFormat();
    }
}
