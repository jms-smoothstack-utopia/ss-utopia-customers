package com.ss.utopia.customer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.ss.utopia.customer.entity.Customer;
import com.ss.utopia.customer.exception.CaughtStripeException;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

class StripeCustomerServiceImplUnitTest {

  //public test key from Stripe docs
  private final static String stripeKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";
  private final static String badStripeKey = "bad";
  private final static String testEmail = "test@test.com";
  private static CustomerCreateParams createParams;
  private static String stripeId;
  private static CustomerUpdateParams updateParams;
  private final StripeCustomerServiceImpl service = new StripeCustomerServiceImpl();

  @BeforeAll
  static void beforeAll() {
    createParams = CustomerCreateParams.builder()
            .setEmail(testEmail)
            .setName("Foo")
            .build();
    updateParams = CustomerUpdateParams.builder()
            .setName("Bar")
            .build();

    //create a customer so that we can later test updating and deleting it
    StripeCustomerServiceImpl serviceForBefore = new StripeCustomerServiceImpl();
    serviceForBefore.setStripeKey(stripeKey);
    stripeId = serviceForBefore.createStripeCustomer(createParams);
  }

  @Test
  void test_createStripeCustomer_createsCustomer() {
    service.setStripeKey(stripeKey);
    String testId = service.createStripeCustomer(createParams);
    assertEquals("cus_", testId.substring(0, 4));
  }

  @Test
  void test_createStripeCustomer_throwsExceptionOnStripeError() {
    service.setStripeKey(badStripeKey);
    assertThrows(CaughtStripeException.class, () -> service.createStripeCustomer(createParams));
  }

  @Test
  void test_retrieveStripeCustomer_getsCustomer() {
    service.setStripeKey(stripeKey);
    var customer = service.retrieveStripeCustomer(stripeId);
    assertEquals(testEmail, customer.getEmail());
  }

  @Test
  void test_retrieveStripeCustomer_throwsExceptionOnStripeError() {
    service.setStripeKey(badStripeKey);
    assertThrows(CaughtStripeException.class, () -> service.retrieveStripeCustomer(stripeId));
  }

  @Test
  void test_updateStripeCustomer_updatesExistingCustomer() {
    service.setStripeKey(stripeKey);
    service.updateStripeCustomer(stripeId, updateParams);
    var updatedCustomer = service.retrieveStripeCustomer(stripeId);
    assertEquals("Bar", updatedCustomer.getName());
  }

  @Test
  void test_updateStripeCustomer_throwsExceptionOnStripeError() {
    service.setStripeKey(badStripeKey);
    assertThrows(CaughtStripeException.class, () -> service.updateStripeCustomer(stripeId,
            updateParams));
  }

  @Test
  void test_deleteStripeCustomer_deletesCustomer() {
    service.setStripeKey(stripeKey);
    service.deleteStripeCustomer(stripeId);

    //deleted customers CAN still be retrieved through the API
    //see: https://stripe.com/docs/api/customers/delete
    var deletedCustomer = service.retrieveStripeCustomer(stripeId);
    assertTrue(deletedCustomer.getDeleted());
  }

  @Test
  void test_deleteStripeCustomer_throwsExceptionOnStripeError() {
    service.setStripeKey(badStripeKey);
    assertThrows(CaughtStripeException.class, () -> service.deleteStripeCustomer(stripeId));
  }
}
