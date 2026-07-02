package com.utp.myapp.sales.domain.model.repository;

import com.utp.myapp.sales.domain.model.aggregates.Customer;

import java.util.List;
import java.util.Optional;

public interface ICustomerRepository extends ICRUD<Customer> {

    Optional<Customer> findByDni(String dni);

    Optional<Customer> findByPhone(String phone);

    Optional<Customer> findByEmail(String email);

    List<Customer> searchByName(String tenantId, String query, int page, int size);

    List<Customer> findByTenantId(String tenantId, int page, int size);
}
