package com.ss.utopia.orchestrator.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CustomerClientTest {

  @Autowired
  CustomerClient client;

  @Test
  void test_getCustomerById_IsNotNull() {
    var customer = client.getCustomerById(1L);

    assertNotNull(customer);
  }
}