package com.ss.utopia.customer;

import com.ss.utopia.customer.client.AccountsClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CustomerApplicationTests {

  @MockBean
  AccountsClient accountsClient;
  
  @Test
  void contextLoads() {
    assertTrue(true);
  }

}
