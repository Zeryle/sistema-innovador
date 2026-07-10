package com.utp.myapp.analytics.application.analytics;

import com.utp.myapp.shared.infraestructure.config.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Read-only analytics queries. Lives in the application layer of the
 * analytics bounded context.
 *
 * Premium features live here:
 *  - monthly trend (last 6 months) for orders and revenue
 *  - top customers by orders / spend
 *  - period-over-period comparison (this month vs last month)
 *  - service-type breakdown (what repairs are most common)
 *  - average ticket size
 */
@Service
@Transactional(readOnly = true)
public class AdvancedAnalyticsService {

    @PersistenceContext
    private EntityManager em;

    public Map<String, Object> getMonthlyTrend(String tenantId, int months) {
        Map<String, Object> result = new LinkedHashMap<>();
        // Build a list of N last months with order count and revenue per month
        List<Map<String, Object>> series = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = months - 1; i >= 0; i--) {
            LocalDate from = today.minusMonths(i).withDayOfMonth(1);
            LocalDate to = today.minusMonths(i).withDayOfMonth(today.minusMonths(i).lengthOfMonth()).plusDays(1);
            try {
                Query q = em.createNativeQuery(
                        "SELECT COUNT(*), COALESCE(SUM(estimated_cost), 0) " +
                        "FROM work_order WHERE tenant_id = :tid AND created_at >= :from AND created_at < :to");
                q.setParameter("tid", tenantId);
                q.setParameter("from", from.atStartOfDay());
                q.setParameter("to", to.atStartOfDay());
                Object[] row = (Object[]) q.getSingleResult();
                long count = ((Number) row[0]).longValue();
                double revenue = row[1] == null ? 0.0 : ((Number) row[1]).doubleValue();
                Map<String, Object> point = new LinkedHashMap<>();
                point.put("month", from.toString().substring(0, 7));
                point.put("orders", count);
                point.put("revenue", revenue);
                series.add(point);
            } catch (Exception e) {
                Map<String, Object> point = new LinkedHashMap<>();
                point.put("month", from.toString().substring(0, 7));
                point.put("orders", 0);
                point.put("revenue", 0.0);
                series.add(point);
            }
        }
        result.put("series", series);
        return result;
    }

    public List<Map<String, Object>> getTopCustomers(String tenantId, int limit) {
        // Top customers by total estimated_cost
        String sql = "SELECT c.id, (c.name || ' ' || c.last_name) AS full_name, COUNT(wo.id) AS total_orders, " +
                     "COALESCE(SUM(wo.estimated_cost), 0) AS total_spend " +
                     "FROM customer c LEFT JOIN work_order wo ON wo.customer_id = c.id AND wo.tenant_id = :tid " +
                     "WHERE c.tenant_id = :tid " +
                     "GROUP BY c.id, (c.name || ' ' || c.last_name) " +
                     "ORDER BY total_spend DESC, total_orders DESC " +
                     "LIMIT " + limit;
        try {
            Query q = em.createNativeQuery(sql);
            q.setParameter("tid", tenantId);
            @SuppressWarnings("unchecked")
            List<Object[]> rows = q.getResultList();
            List<Map<String, Object>> out = new ArrayList<>();
            for (Object[] r : rows) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("customerId", r[0]);
                m.put("fullName", r[1]);
                m.put("totalOrders", ((Number) r[2]).intValue());
                m.put("totalSpend", ((Number) r[3]).doubleValue());
                out.add(m);
            }
            return out;
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Period-over-period comparison: this month vs last month, on the same KPIs.
     */
    public Map<String, Object> getPeriodComparison(String tenantId) {
        Map<String, Object> result = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate thisMonthStart = today.withDayOfMonth(1);
        LocalDate lastMonthStart = thisMonthStart.minusMonths(1);
        LocalDate lastMonthEnd = thisMonthStart.minusDays(1);

        int thisOrders = countOrders(tenantId, thisMonthStart, today.plusDays(1));
        int lastOrders = countOrders(tenantId, lastMonthStart, lastMonthEnd.plusDays(1));
        double thisRevenue = sumRevenue(tenantId, thisMonthStart, today.plusDays(1));
        double lastRevenue = sumRevenue(tenantId, lastMonthStart, lastMonthEnd.plusDays(1));
        int thisNewCustomers = countNewCustomers(tenantId, thisMonthStart, today.plusDays(1));
        int lastNewCustomers = countNewCustomers(tenantId, lastMonthStart, lastMonthEnd.plusDays(1));

        result.put("thisMonth", Map.of(
                "month", thisMonthStart.toString().substring(0, 7),
                "orders", thisOrders,
                "revenue", thisRevenue,
                "newCustomers", thisNewCustomers
        ));
        result.put("lastMonth", Map.of(
                "month", lastMonthStart.toString().substring(0, 7),
                "orders", lastOrders,
                "revenue", lastRevenue,
                "newCustomers", lastNewCustomers
        ));
        // deltas: +12 means 12% growth
        result.put("ordersDelta", pct(thisOrders, lastOrders));
        result.put("revenueDelta", pct((int) thisRevenue, (int) lastRevenue));
        result.put("customersDelta", pct(thisNewCustomers, lastNewCustomers));
        return result;
    }

    /**
     * Distribution of work orders by diagnostic / type. Premium exposes
     * the diagnostic_notes substring after the first ':' or by a fixed set
     * of common service types.
     */
    public List<Map<String, Object>> getServiceBreakdown(String tenantId) {
        // We classify orders into coarse categories based on description text.
        // This is a demo, so a simple LIKE-based count is good enough.
        String[] patterns = {"aceite", "freno", "suspension", "electrico", "motor", "transmision", "alineacion"};
        String[] labels = {"Cambio de aceite", "Frenos", "Suspension", "Electrico", "Motor", "Transmision", "Alineacion"};
        List<Map<String, Object>> out = new ArrayList<>();
        try {
            for (int i = 0; i < patterns.length; i++) {
                Query q = em.createNativeQuery(
                        "SELECT COUNT(*) FROM work_order WHERE tenant_id = :tid " +
                        "AND LOWER(COALESCE(description, '')) LIKE :pat");
                q.setParameter("tid", tenantId);
                q.setParameter("pat", "%" + patterns[i] + "%");
                long count = ((Number) q.getSingleResult()).longValue();
                if (count > 0) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("category", labels[i]);
                    m.put("count", count);
                    out.add(m);
                }
            }
        } catch (Exception ignored) {}
        return out;
    }

    private int countOrders(String tenantId, LocalDate from, LocalDate to) {
        try {
            Query q = em.createNativeQuery(
                    "SELECT COUNT(*) FROM work_order WHERE tenant_id = :tid " +
                    "AND created_at >= :from AND created_at < :to");
            q.setParameter("tid", tenantId);
            q.setParameter("from", from.atStartOfDay());
            q.setParameter("to", to.atStartOfDay());
            return ((Number) q.getSingleResult()).intValue();
        } catch (Exception e) { return 0; }
    }

    private double sumRevenue(String tenantId, LocalDate from, LocalDate to) {
        try {
            Query q = em.createNativeQuery(
                    "SELECT COALESCE(SUM(estimated_cost), 0) FROM work_order " +
                    "WHERE tenant_id = :tid AND created_at >= :from AND created_at < :to");
            q.setParameter("tid", tenantId);
            q.setParameter("from", from.atStartOfDay());
            q.setParameter("to", to.atStartOfDay());
            return ((Number) q.getSingleResult()).doubleValue();
        } catch (Exception e) { return 0.0; }
    }

    private int countNewCustomers(String tenantId, LocalDate from, LocalDate to) {
        try {
            Query q = em.createNativeQuery(
                    "SELECT COUNT(*) FROM customer WHERE tenant_id = :tid " +
                    "AND created_at >= :from AND created_at < :to");
            q.setParameter("tid", tenantId);
            q.setParameter("from", from.atStartOfDay());
            q.setParameter("to", to.atStartOfDay());
            return ((Number) q.getSingleResult()).intValue();
        } catch (Exception e) { return 0; }
    }

    /**
     * Percentage change. Returns +12 for 12% growth, -50 for halving.
     * If previous is 0 and current > 0, returns +100. If both 0, returns 0.
     */
    private int pct(int current, int previous) {
        if (previous == 0) return current > 0 ? 100 : 0;
        return (int) Math.round(((double)(current - previous) / previous) * 100);
    }
}
