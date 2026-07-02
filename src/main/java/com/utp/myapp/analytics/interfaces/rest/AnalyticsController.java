package com.utp.myapp.analytics.interfaces.rest;

import com.utp.myapp.shared.infraestructure.config.TenantContext;
import com.utp.myapp.shared.infraestructure.web.ApiResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @PersistenceContext
    private EntityManager em;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        String tenantId = TenantContext.getTenantId();
        Map<String, Object> dashboard = new LinkedHashMap<>();

        try {
            // Active orders
            Query q = em.createNativeQuery(
                "SELECT COUNT(*) FROM work_order WHERE tenant_id = :tid AND status NOT IN ('COMPLETED','DELIVERED','CANCELLED')");
            q.setParameter("tid", tenantId);
            dashboard.put("activeOrders", ((Number) q.getSingleResult()).intValue());
        } catch (Exception e) { dashboard.put("activeOrders", 0); }

        try {
            // Total orders
            Query q = em.createNativeQuery("SELECT COUNT(*) FROM work_order WHERE tenant_id = :tid");
            q.setParameter("tid", tenantId);
            dashboard.put("totalOrders", ((Number) q.getSingleResult()).intValue());
        } catch (Exception e) { dashboard.put("totalOrders", 0); }

        try {
            // Total customers
            Query q = em.createNativeQuery("SELECT COUNT(*) FROM customer WHERE tenant_id = :tid");
            q.setParameter("tid", tenantId);
            dashboard.put("totalCustomers", ((Number) q.getSingleResult()).intValue());
        } catch (Exception e) { dashboard.put("totalCustomers", 0); }

        try {
            // Total vehicles
            Query q = em.createNativeQuery("SELECT COUNT(*) FROM vehicle WHERE tenant_id = :tid");
            q.setParameter("tid", tenantId);
            dashboard.put("totalVehicles", ((Number) q.getSingleResult()).intValue());
        } catch (Exception e) { dashboard.put("totalVehicles", 0); }

        try {
            // Pending reminders
            Query q = em.createNativeQuery("SELECT COUNT(*) FROM reminder WHERE tenant_id = :tid AND status = 'SCHEDULED'");
            q.setParameter("tid", tenantId);
            dashboard.put("pendingReminders", ((Number) q.getSingleResult()).intValue());
        } catch (Exception e) { dashboard.put("pendingReminders", 0); }

        try {
            // Orders by status
            Query q = em.createNativeQuery(
                "SELECT status, COUNT(*) FROM work_order WHERE tenant_id = :tid GROUP BY status ORDER BY COUNT(*) DESC");
            q.setParameter("tid", tenantId);
            List<Object[]> rows = q.getResultList();
            Map<String, Integer> statusCounts = new LinkedHashMap<>();
            for (Object[] row : rows) {
                statusCounts.put((String) row[0], ((Number) row[1]).intValue());
            }
            dashboard.put("ordersByStatus", statusCounts);
        } catch (Exception e) { dashboard.put("ordersByStatus", Map.of()); }

        return ResponseEntity.ok(ApiResponse.ok(dashboard));
    }

    @GetMapping("/status-summary")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getStatusSummary() {
        String tenantId = TenantContext.getTenantId();
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Query q = em.createNativeQuery(
                "SELECT status, COUNT(*) as cnt FROM work_order WHERE tenant_id = :tid GROUP BY status");
            q.setParameter("tid", tenantId);
            List<Object[]> rows = q.getResultList();
            for (Object[] row : rows) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("status", row[0]);
                m.put("count", ((Number) row[1]).intValue());
                result.add(m);
            }
        } catch (Exception e) { /* empty */ }
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
