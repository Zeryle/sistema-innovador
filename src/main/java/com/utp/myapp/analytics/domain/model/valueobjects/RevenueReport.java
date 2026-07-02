package com.utp.myapp.analytics.domain.model.valueobjects;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RevenueReport(
        LocalDate period,
        BigDecimal totalRevenue,
        long orderCount,
        BigDecimal avgTicket
) {}
