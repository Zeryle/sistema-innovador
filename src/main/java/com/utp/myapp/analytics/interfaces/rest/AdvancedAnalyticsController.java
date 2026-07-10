package com.utp.myapp.analytics.interfaces.rest;

import com.utp.myapp.analytics.application.analytics.AdvancedAnalyticsService;
import com.utp.myapp.shared.infraestructure.config.TenantContext;
import com.utp.myapp.shared.infraestructure.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics/advanced")
@RequiredArgsConstructor
public class AdvancedAnalyticsController {

    private final AdvancedAnalyticsService service;

    /**
     * Monthly trend of orders + revenue for the last N months. Default 6.
     */
    @GetMapping("/monthly-trend")
    public ResponseEntity<ApiResponse<Map<String, Object>>> monthlyTrend(
            @RequestParam(defaultValue = "6") int months) {
        String tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(ApiResponse.ok(service.getMonthlyTrend(tenantId, months)));
    }

    /**
     * Top N customers by total spend. Default 5.
     */
    @GetMapping("/top-customers")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> topCustomers(
            @RequestParam(defaultValue = "5") int limit) {
        String tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(ApiResponse.ok(service.getTopCustomers(tenantId, limit)));
    }

    /**
     * Period-over-period comparison: this month vs last month.
     */
    @GetMapping("/period-comparison")
    public ResponseEntity<ApiResponse<Map<String, Object>>> periodComparison() {
        String tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(ApiResponse.ok(service.getPeriodComparison(tenantId)));
    }

    /**
     * Distribution of work orders by service category.
     */
    @GetMapping("/service-breakdown")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> serviceBreakdown() {
        String tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(ApiResponse.ok(service.getServiceBreakdown(tenantId)));
    }
}
