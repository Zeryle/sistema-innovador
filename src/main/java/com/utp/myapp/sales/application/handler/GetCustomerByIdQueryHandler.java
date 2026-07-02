package com.utp.myapp.sales.application.handler;

import com.utp.myapp.sales.application.assembler.CustomerAssembler;
import com.utp.myapp.sales.application.dto.CustomerDto;
import com.utp.myapp.sales.application.query.GetCustomerByIdQuery;
import com.utp.myapp.sales.domain.model.aggregates.Customer;
import com.utp.myapp.sales.domain.model.repository.ICustomerRepository;
import com.utp.myapp.shared.domain.model.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetCustomerByIdQueryHandler {

    private final ICustomerRepository repository;
    private final CustomerAssembler assembler;

    public CustomerDto handle(GetCustomerByIdQuery query) {
        Customer customer = repository.listById(query.getId());
        if (customer == null) {
            throw new EntityNotFoundException("Customer", query.getId());
        }
        return assembler.toDto(customer);
    }
}
