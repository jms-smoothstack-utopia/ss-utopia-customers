package com.ss.utopia.customer.bootstrap;

import com.ss.utopia.customer.entity.Address;
import com.ss.utopia.customer.entity.Customer;
import com.ss.utopia.customer.repository.CustomerRepository;
import java.util.Collections;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local-h2")
@RequiredArgsConstructor
public class H2DataBootstrap implements CommandLineRunner {

  private final CustomerRepository customerRepository;

  @Override
  public void run(String... args) throws Exception {
    if (customerRepository.count() == 0) {
      loadAllCustomerAccounts();
    }
  }

  private void loadAllCustomerAccounts() {
    loadCustomer1();
  }

  private void loadCustomer1() {
    var customer = Customer.builder()
        .email("john_sample@example.com")
        .firstName("John")
        .lastName("Sample")
        .loyaltyPoints(0)
        .phoneNumber("999-999-9999")
        .addresses(Set.of(Address.builder()
                              .cardinality(1)
                              .line1("2 Electric Ave.")
                              .line2("Suite HI-R")
                              .city("Las Vegas")
                              .state("NV")
                              .zipcode("69420")
                              .build()))
        .paymentMethods(Collections.emptySet())
        .build();
    customerRepository.save(customer);
  }
}
