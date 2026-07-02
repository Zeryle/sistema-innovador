package com.utp.myapp.sales.infraestructure.persistence.jpa.mappers;

import com.utp.myapp.sales.domain.model.aggregates.Customer;
import com.utp.myapp.sales.domain.model.valueobjets.Address;
import com.utp.myapp.sales.infraestructure.persistence.jpa.entities.AddressEmbeddable;
import com.utp.myapp.sales.infraestructure.persistence.jpa.entities.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toDomain(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Customer.Builder()
                .id(entity.getId() != null ? entity.getId().intValue() : null)
                .name(entity.getName())
                .lastName(entity.getLastName())
                .dni(entity.getDni())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .tenantId(entity.getTenantId())
                .notes(entity.getNotes())
                .profileImageUrl(entity.getProfileImageUrl())
                .address(toAddressDomain(entity.getAddress()))
                .build();
    }

    public Address toAddressDomain(AddressEmbeddable embeddable) {
        if (embeddable == null) {
            return null;
        }
        return new Address(
                embeddable.getStreet(),
                embeddable.getNumber(),
                embeddable.getCity(),
                embeddable.getCountry()
        );
    }

    public CustomerEntity toEntity(Customer customer) {
        if (customer == null) {
            return null;
        }
        CustomerEntity entity = new CustomerEntity();
        if (customer.getId() != null) {
            entity.setId(customer.getId().longValue());
        }
        entity.setName(customer.getName());
        entity.setLastName(customer.getLastName());
        entity.setDni(customer.getDni());
        entity.setEmail(customer.getEmail());
        entity.setPhone(customer.getPhone());
        entity.setTenantId(customer.getTenantId());
        entity.setNotes(customer.getNotes());
        entity.setProfileImageUrl(customer.getProfileImageUrl());
        entity.setAddress(toEmbeddable(customer.getAddress()));
        entity.setCreatedAt(customer.getCreatedAt());
        entity.setUpdatedAt(customer.getUpdatedAt());
        return entity;
    }

    public AddressEmbeddable toEmbeddable(Address address) {
        if (address == null) {
            return null;
        }
        return AddressEmbeddable.builder()
                .street(address.street())
                .number(address.number())
                .city(address.city())
                .country(address.country())
                .build();
    }
}
