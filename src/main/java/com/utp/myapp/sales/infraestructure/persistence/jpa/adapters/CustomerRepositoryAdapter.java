package com.utp.myapp.sales.infraestructure.persistence.jpa.adapters;

import com.utp.myapp.sales.domain.model.aggregates.Customer;
import com.utp.myapp.sales.domain.model.repository.ICustomerRepository;
import com.utp.myapp.sales.infraestructure.persistence.jpa.entities.CustomerEntity;
import com.utp.myapp.sales.infraestructure.persistence.jpa.mappers.CustomerMapper;
import com.utp.myapp.sales.infraestructure.persistence.jpa.repositories.JPACustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CustomerRepositoryAdapter implements ICustomerRepository {

    private final JPACustomerRepository jpa;
    private final CustomerMapper mapper;

    @Override
    public Customer insert(Customer customer) {
        CustomerEntity entity = mapper.toEntity(customer);
        CustomerEntity saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Customer update(Customer customer) {
        CustomerEntity entity = mapper.toEntity(customer);
        CustomerEntity saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(Integer id) {
        jpa.deleteById(id);
    }

    @Override
    public Customer listById(Integer id) {
        return jpa.findById(id).map(mapper::toDomain).orElse(null);
    }

    @Override
    public List<Customer> listAll() {
        return jpa.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Customer> findByDni(String dni) {
        return jpa.findByDni(dni).map(mapper::toDomain);
    }

    @Override
    public Optional<Customer> findByPhone(String phone) {
        return jpa.findByPhone(phone).map(mapper::toDomain);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return jpa.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public List<Customer> searchByName(String tenantId, String query, int page, int size) {
        return jpa.searchByTenant(tenantId, query, PageRequest.of(page, size))
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Customer> findByTenantId(String tenantId, int page, int size) {
        return jpa.findByTenantId(tenantId, PageRequest.of(page, size))
                .stream().map(mapper::toDomain).toList();
    }
}
