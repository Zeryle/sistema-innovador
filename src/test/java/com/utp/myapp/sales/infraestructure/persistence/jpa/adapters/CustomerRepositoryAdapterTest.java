package com.utp.myapp.sales.infraestructure.persistence.jpa.adapters;

import com.utp.myapp.sales.domain.model.aggregates.Customer;
import com.utp.myapp.sales.domain.model.valueobjets.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test of customer")
@SpringBootTest
class CustomerRepositoryAdapterTest {

    @Autowired
    private CustomerRepositoryAdapter customerRepositoryAdapter;

    @DisplayName("Create client valid should return client")
    @Test
    public void createCustomerValid(){
        Address address = new Address("street3", "3", "state3", "zip3");
        Customer customer = new Customer.Builder()
                .name("name3")
                .email("email3")
                .lastName("lastname3")
                .dni("dni3")
                .address(address)
                .build();
      Customer customerSave = customerRepositoryAdapter.insert(customer);
      //assertNotNull(customerSave.getId());
      assertEquals( "name3",customerSave.getName());
      assertEquals("email3",customerSave.getEmail());
      assertEquals("lastname3",customerSave.getLastName());
    }

}