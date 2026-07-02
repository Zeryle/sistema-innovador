package com.utp.myapp.catalog.domain.model.aggregates;

import com.utp.myapp.shared.domain.model.aggregates.AuditableAggregateRoot;

public class PartCategory extends AuditableAggregateRoot {

    private Long id;
    private String name;
    private String description;
    private Long parentCategoryId;
    private String imageUrl;
    private String tenantId;

    private PartCategory() {}

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Long getParentCategoryId() { return parentCategoryId; }
    public String getImageUrl() { return imageUrl; }
    public String getTenantId() { return tenantId; }

    public static class Builder {
        private final PartCategory category = new PartCategory();
        public Builder id(Long id) { category.id = id; return this; }
        public Builder name(String name) { category.name = name; return this; }
        public Builder description(String description) { category.description = description; return this; }
        public Builder parentCategoryId(Long parent) { category.parentCategoryId = parent; return this; }
        public Builder imageUrl(String url) { category.imageUrl = url; return this; }
        public Builder tenantId(String tenantId) { category.tenantId = tenantId; return this; }
        public PartCategory build() { return category; }
    }
}
