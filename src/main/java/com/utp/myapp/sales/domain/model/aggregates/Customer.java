package com.utp.myapp.sales.domain.model.aggregates;

import com.utp.myapp.sales.domain.model.valueobjets.Address;
import com.utp.myapp.shared.domain.model.aggregates.AuditableAggregateRoot;

public class Customer extends AuditableAggregateRoot {

    private Integer id;
    private String name;
    private String lastName;
    private String dni;
    private String email;
    private String phone;
    private String tenantId;
    private String notes;
    private String profileImageUrl;
    private Address address;

    private Customer() {
    }

    public static class Builder {
        private final Customer customer = new Customer();

        public Builder id(Integer id) {
            customer.id = id;
            return this;
        }

        public Builder name(String name) {
            customer.name = name;
            return this;
        }

        public Builder lastName(String lastName) {
            customer.lastName = lastName;
            return this;
        }

        public Builder dni(String dni) {
            customer.dni = dni;
            return this;
        }

        public Builder email(String email) {
            customer.email = email;
            return this;
        }

        public Builder phone(String phone) {
            customer.phone = phone;
            return this;
        }

        public Builder tenantId(String tenantId) {
            customer.tenantId = tenantId;
            return this;
        }

        public Builder notes(String notes) {
            customer.notes = notes;
            return this;
        }

        public Builder profileImageUrl(String profileImageUrl) {
            customer.profileImageUrl = profileImageUrl;
            return this;
        }

        public Builder address(Address address) {
            customer.address = address;
            return this;
        }

        public Customer build() {
            return customer;
        }
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDni() {
        return dni;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getNotes() {
        return notes;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public Address getAddress() {
        return address;
    }

    public String getFullName() {
        return (name != null ? name : "") + " " + (lastName != null ? lastName : "");
    }
}
