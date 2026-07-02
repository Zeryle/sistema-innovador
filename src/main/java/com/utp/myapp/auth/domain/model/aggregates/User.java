package com.utp.myapp.auth.domain.model.aggregates;

import com.utp.myapp.auth.domain.model.valueobjects.HashedPassword;
import com.utp.myapp.auth.domain.model.valueobjects.Role;
import com.utp.myapp.shared.domain.model.aggregates.AuditableAggregateRoot;
import com.utp.myapp.shared.domain.model.valueobjects.Email;
import com.utp.myapp.shared.domain.model.valueobjects.TenantId;

import java.time.LocalDateTime;

/**
 * User aggregate root — represents a user belonging to a tenant (auto shop).
 */
public class User extends AuditableAggregateRoot {

    private Long id;
    private Email email;
    private HashedPassword passwordHash;
    private Role role;
    private TenantId tenantId;
    private boolean active;

    private User() {
    }

    public static User register(Email email, String rawPassword, Role role, TenantId tenantId) {
        User user = new User();
        user.email = email;
        user.passwordHash = HashedPassword.fromRaw(rawPassword);
        user.role = role;
        user.tenantId = tenantId;
        user.active = true;
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();
        user.version = 0L;
        return user;
    }

    public boolean verifyPassword(String rawPassword) {
        return this.passwordHash.matches(rawPassword);
    }

    public void changePassword(String newRawPassword) {
        this.passwordHash = HashedPassword.fromRaw(newRawPassword);
        markUpdated();
    }

    public void updateRole(Role newRole) {
        this.role = newRole;
        markUpdated();
    }

    public void deactivate() {
        this.active = false;
        markUpdated();
    }

    public void activate() {
        this.active = true;
        markUpdated();
    }

    public Long getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public HashedPassword getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public boolean isActive() {
        return active;
    }

    // Manual Builder pattern (consistent with existing codebase style)
    public static class Builder {
        private final User user = new User();

        public Builder id(Long id) {
            user.id = id;
            return this;
        }

        public Builder email(Email email) {
            user.email = email;
            return this;
        }

        public Builder passwordHash(HashedPassword passwordHash) {
            user.passwordHash = passwordHash;
            return this;
        }

        public Builder role(Role role) {
            user.role = role;
            return this;
        }

        public Builder tenantId(TenantId tenantId) {
            user.tenantId = tenantId;
            return this;
        }

        public Builder active(boolean active) {
            user.active = active;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            user.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            user.updatedAt = updatedAt;
            return this;
        }

        public User build() {
            return user;
        }
    }
}
