package com.utp.myapp.sales.application.assembler;

import com.utp.myapp.sales.application.dto.AddressDto;
import com.utp.myapp.sales.application.dto.CustomerDto;
import com.utp.myapp.sales.application.dto.CustomerSummaryDto;
import com.utp.myapp.sales.domain.model.aggregates.Customer;
import com.utp.myapp.sales.domain.model.valueobjets.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerAssembler {

    private final AddressAssembler addressAssembler;

    public CustomerDto toDto(Customer customer) {
        if (customer == null) {
            return null;
        }
        AddressDto addressDto = addressAssembler.toDto(customer.getAddress());

        return CustomerDto.builder()
                .id(customer.getId())
                .name(customer.getName())
                .lastName(customer.getLastName())
                .dni(customer.getDni())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .tenantId(customer.getTenantId())
                .notes(customer.getNotes())
                .profileImageUrl(customer.getProfileImageUrl())
                .address(addressDto)
                .build();
    }

    public CustomerSummaryDto toSummaryDto(Customer customer) {
        if (customer == null) {
            return null;
        }
        return CustomerSummaryDto.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .dni(customer.getDni())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .build();
    }

    public Customer toDomainCreate(CustomerDto customerDto) {
        Address address = addressAssembler.toDomain(customerDto.getAddress());
        return new Customer.Builder()
                .name(customerDto.getName())
                .lastName(customerDto.getLastName())
                .dni(customerDto.getDni())
                .email(customerDto.getEmail())
                .phone(customerDto.getPhone())
                .tenantId(customerDto.getTenantId())
                .notes(customerDto.getNotes())
                .profileImageUrl(customerDto.getProfileImageUrl())
                .address(address)
                .build();
    }
}
