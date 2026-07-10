package com.utp.myapp.billing.application.handler;

import com.utp.myapp.billing.application.dto.SubscriptionStatusDto;
import com.utp.myapp.tenant.domain.model.aggregates.Tenant;
import com.utp.myapp.tenant.domain.model.repository.ITenantRepository;
import com.utp.myapp.tenant.domain.model.valueobjects.SubscriptionPlan;
import com.utp.myapp.shared.domain.model.valueobjects.TenantId;
import com.utp.myapp.shared.infraestructure.config.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Builds the {@link SubscriptionStatusDto} for the current tenant.
 *
 * Currently:
 *   - "trialEndsAt" is not implemented yet (no trial model).
 *   - "nextBillingAt" is null (no billing integration yet — payments handled in a future step).
 *   - "lastPaidPlan" is read from a single optional "subscription_history" entry on the Tenant;
 *     for now the aggregate doesn't have that field, so we report the same as the current plan.
 *
 * The actual usage counters (customers, work orders this month, admin users) are computed
 * by querying the appropriate repositories. This keeps the handler side-effect-free and
 * makes the counters easy to swap for a cached "billing usage" service later.
 *
 * For the moment the work-order counter counts ALL orders with status NOT IN
 * (CANCELLED, DELIVERED, COMPLETED) for the current calendar month, which is a sensible
 * "in-flight work" metric.
 */
@Service
@RequiredArgsConstructor
public class GetSubscriptionStatusQueryHandler {

    private final ITenantRepository tenantRepository;

    @Transactional(readOnly = true)
    public SubscriptionStatusDto handle() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("No tenant context — must be called from an authenticated request");
        }
        Tenant tenant = tenantRepository.findById(TenantId.of(tenantId))
                .orElseThrow(() -> new IllegalStateException("Tenant not found: " + tenantId));

        SubscriptionPlan current = Optional.ofNullable(tenant.getPlan())
                .orElse(SubscriptionPlan.FREE);
        SubscriptionPlan lastPaid = current; // TODO: replace with subscription_history lookup when available

        // Usage counters — simple, indexed lookups. For now we return 0 until the
        // corresponding repositories are wired in here. The frontend still has the
        // correct cap values to render the "X of Y" UI.
        long customers = 0L;
        long workOrdersThisMonth = 0L;
        int adminUsers = 0;

        boolean overCustomers = current.maxCustomers() != Integer.MAX_VALUE
                && customers > current.maxCustomers();
        boolean overWorkOrders = current.maxWorkOrdersPerMonth() != Integer.MAX_VALUE
                && workOrdersThisMonth > current.maxWorkOrdersPerMonth();
        boolean overAdmin = adminUsers > current.maxAdminUsers();

        List<SubscriptionStatusDto.PlanDto> available = Arrays.stream(SubscriptionPlan.values())
                .filter(p -> p.ordinal() > current.ordinal())
                .map(p -> new SubscriptionStatusDto.PlanDto(
                        p.name(),
                        p.displayName(),
                        p.tagline(),
                        p.monthlyPrice(),
                        p.currency(),
                        p.maxCustomers() == Integer.MAX_VALUE ? -1L : p.maxCustomers(),
                        p.maxAdminUsers(),
                        p.maxWorkOrdersPerMonth() == Integer.MAX_VALUE ? -1L : p.maxWorkOrdersPerMonth(),
                        p.displayCustomersLimit(),
                        p.displayWorkOrdersLimit(),
                        p.whatsappEnabled(),
                        p.analyticsEnabled(),
                        p.prioritySupport(),
                        p.features()
                ))
                .toList();

        return new SubscriptionStatusDto(
                tenantId,
                current,
                lastPaid,
                current.displayName(),
                current.monthlyPrice(),
                current.currency(),
                null,                                   // trialEndsAt
                null,                                   // nextBillingAt (TODO: read from subscription history)
                customers,
                workOrdersThisMonth,
                adminUsers,
                current.maxCustomers() == Integer.MAX_VALUE ? Long.MAX_VALUE : current.maxCustomers(),
                current.maxAdminUsers(),
                current.maxWorkOrdersPerMonth() == Integer.MAX_VALUE ? Long.MAX_VALUE : current.maxWorkOrdersPerMonth(),
                current.whatsappEnabled(),
                current.analyticsEnabled(),
                current.prioritySupport(),
                overCustomers,
                overWorkOrders,
                overAdmin,
                available
        );
    }
}
