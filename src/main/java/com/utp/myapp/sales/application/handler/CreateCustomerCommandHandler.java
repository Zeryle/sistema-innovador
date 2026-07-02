package com.utp.myapp.sales.application.handler;

import com.utp.myapp.sales.application.assembler.CustomerAssembler;
import com.utp.myapp.sales.application.command.CreateCustomerCommand;
import com.utp.myapp.sales.application.dto.CustomerDto;
import com.utp.myapp.sales.domain.model.aggregates.Customer;
import com.utp.myapp.sales.domain.model.repository.ICustomerRepository;
import com.utp.myapp.shared.infraestructure.config.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCustomerCommandHandler {

    private final ICustomerRepository repository;
    private final CustomerAssembler assembler;

    public CustomerDto handle(CreateCustomerCommand command) {
        CustomerDto customerDto = CustomerDto.builder()
                .name(command.getName())
                .lastName(command.getLastName())
                .dni(command.getDni())
                .email(command.getEmail())
                .phone(command.getPhone())
                .notes(command.getNotes())
                .tenantId(TenantContext.getTenantId())
                .address(command.getAddress())
                .build();

        Customer customer = assembler.toDomainCreate(customerDto);
        Customer customerSaved = repository.insert(customer);
        return assembler.toDto(customerSaved);
    }
}
