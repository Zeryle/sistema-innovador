package com.utp.myapp.sales.application.handler;

import com.utp.myapp.sales.application.assembler.CustomerAssembler;
import com.utp.myapp.sales.application.command.UpdateCustomerCommand;
import com.utp.myapp.sales.application.dto.CustomerDto;
import com.utp.myapp.sales.domain.model.aggregates.Customer;
import com.utp.myapp.sales.domain.model.repository.ICustomerRepository;
import com.utp.myapp.shared.domain.model.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateCustomerCommandHandler {

    private final ICustomerRepository repository;
    private final CustomerAssembler assembler;

    public CustomerDto handle(UpdateCustomerCommand command) {
        Customer existing = repository.listById(command.getId());
        if (existing == null) {
            throw new EntityNotFoundException("Customer", command.getId());
        }

        CustomerDto customerDto = CustomerDto.builder()
                .id(command.getId())
                .name(command.getName() != null ? command.getName() : existing.getName())
                .lastName(command.getLastName() != null ? command.getLastName() : existing.getLastName())
                .dni(command.getDni() != null ? command.getDni() : existing.getDni())
                .email(command.getEmail() != null ? command.getEmail() : existing.getEmail())
                .phone(command.getPhone() != null ? command.getPhone() : existing.getPhone())
                .notes(command.getNotes() != null ? command.getNotes() : existing.getNotes())
                .tenantId(existing.getTenantId())
                .address(command.getAddress() != null ? command.getAddress() : null)
                .build();

        Customer customer = assembler.toDomainCreate(customerDto);
        Customer updated = repository.update(customer);
        return assembler.toDto(updated);
    }
}
