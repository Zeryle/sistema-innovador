package com.utp.myapp.analytics.domain.model.valueobjects;

import java.time.LocalDate;

public record DateRange(LocalDate from, LocalDate to) {

    public DateRange {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from date must be before to date");
        }
    }

    public static DateRange of(LocalDate from, LocalDate to) {
        return new DateRange(from, to);
    }
}
