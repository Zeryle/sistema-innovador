package com.utp.myapp.sales.application.handler;

import com.utp.myapp.sales.application.assembler.CustomerAssembler;
import com.utp.myapp.sales.application.dto.CustomerSummaryDto;
import com.utp.myapp.sales.application.query.SearchCustomersQuery;
import com.utp.myapp.sales.domain.model.aggregates.Customer;
import com.utp.myapp.sales.domain.model.repository.ICustomerRepository;
import com.utp.myapp.shared.infraestructure.config.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchCustomersQueryHandler {

    private final ICustomerRepository repository;
    private final CustomerAssembler assembler;

    public List<CustomerSummaryDto> handle(SearchCustomersQuery query) {
        String tenantId = TenantContext.getTenantId();
        List<Customer> customers;

        if (query.getQuery() != null && !query.getQuery().isBlank()) {
            customers = repository.searchByName(tenantId, query.getQuery(), query.getPage(), query.getSize());
        } else {
            customers = repository.findByTenantId(tenantId, query.getPage(), query.getSize());
        }

        return customers.stream()
                .map(assembler::toSummaryDto)
                .toList();
    }
}
