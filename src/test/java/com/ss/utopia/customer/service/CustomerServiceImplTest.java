package com.ss.utopia.customer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ss.utopia.customer.model.Address;
import com.ss.utopia.customer.model.Customer;
import com.ss.utopia.customer.model.PaymentMethod;
import com.ss.utopia.customer.repository.CustomerRepository;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CustomerServiceImplTest {

  @Autowired
  private CustomerRepository repository;

  @Test
  void test_verifyUsingH2Repository() {
    new Address();
    var customer = Customer.builder()
        .firstName("fname")
        .lastName("lname")
        .email("email@email.email")
        .addresses(Set.of(Address.builder()
                              .line1("line1")
                              .line2("line2")
                              .city("city")
                              .state("st")
                              .zipcode("12345")
                              .build()))
        .paymentMethods(Set.of(PaymentMethod.builder()
                                   .ownerId(1L)
                                   .accountNum("123456789")
                                   .notes("notes")
                                   .build()))
        .build();

    assertEquals(repository.findAll().size(), 0);
    repository.save(customer);
    assertEquals(repository.findAll().size(), 1);
  }
}