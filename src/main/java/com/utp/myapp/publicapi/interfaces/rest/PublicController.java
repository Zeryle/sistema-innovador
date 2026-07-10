package com.utp.myapp.publicapi.interfaces.rest;

import com.utp.myapp.catalog.domain.model.aggregates.PartCategory;
import com.utp.myapp.catalog.domain.model.repository.IPartCategoryRepository;
import com.utp.myapp.shared.infraestructure.web.ApiResponse;
import com.utp.myapp.tenant.domain.model.aggregates.Tenant;
import com.utp.myapp.tenant.domain.model.repository.ITenantRepository;
import com.utp.myapp.tenant.domain.model.valueobjects.SubscriptionPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final ITenantRepository tenantRepository;
    private final IPartCategoryRepository partCategoryRepository;

    @GetMapping("/tenant")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTenant() {
        List<Tenant> all = tenantRepository.findAll();
        if (all.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok(Map.of()));
        }
        Tenant t = all.get(0);
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("id", t.getId().value());
        info.put("businessName", t.getBusinessName());
        info.put("phone", t.getPhone());
        info.put("ruc", t.getRuc());
        info.put("logoUrl", t.getLogoUrl());
        info.put("plan", t.getPlan() != null ? t.getPlan().name() : null);
        return ResponseEntity.ok(ApiResponse.ok(info));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCategories() {
        List<PartCategory> all = partCategoryRepository.findAll();
        List<Map<String, Object>> roots = all.stream()
                .filter(c -> c.getParentCategoryId() == null)
                .map(root -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", root.getId());
                    m.put("name", root.getName());
                    m.put("description", root.getDescription());
                    m.put("imageUrl", root.getImageUrl());
                    List<String> subs = all.stream()
                            .filter(c -> root.getId().equals(c.getParentCategoryId()))
                            .map(PartCategory::getName)
                            .toList();
                    m.put("subcategories", subs);
                    return m;
                })
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(roots));
    }

    /**
     * Returns the full catalog of subscription tiers (FREE, BASIC, PREMIUM) with
     * pricing, customer caps, and feature flags. Unauthenticated — used by the
     * public landing pricing page.
     */
    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPlans() {
        List<Map<String, Object>> plans = Arrays.stream(SubscriptionPlan.values())
                .map(plan -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("code", plan.name());
                    m.put("name", plan.displayName());
                    m.put("tagline", plan.tagline());
                    m.put("monthlyPrice", plan.monthlyPrice());
                    m.put("currency", plan.currency());
                    m.put("maxCustomers", plan.maxCustomers() == Integer.MAX_VALUE
                            ? null : plan.maxCustomers());
                    m.put("maxCustomersDisplay", plan.displayCustomersLimit());
                    m.put("maxWorkOrdersPerMonth", plan.maxWorkOrdersPerMonth() == Integer.MAX_VALUE
                            ? null : plan.maxWorkOrdersPerMonth());
                    m.put("maxWorkOrdersDisplay", plan.displayWorkOrdersLimit());
                    m.put("maxAdminUsers", plan.maxAdminUsers());
                    m.put("whatsappEnabled", plan.whatsappEnabled());
                    m.put("analyticsEnabled", plan.analyticsEnabled());
                    m.put("prioritySupport", plan.prioritySupport());
                    m.put("features", plan.features());
                    return m;
                })
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(plans));
    }
}