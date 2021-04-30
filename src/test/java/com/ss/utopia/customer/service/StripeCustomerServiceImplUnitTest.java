package com.ss.utopia.customer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ss.utopia.customer.exception.CaughtStripeException;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentMethodCreateParams;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class StripeCustomerServiceImplUnitTest {

  //public test key from Stripe docs
  private final static String stripeKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";
  private final static String badStripeKey = "bad";
  private final static String testEmail = "test@test.com";
  private static CustomerCreateParams customerCreateParams;
  private static String customerStripeId;
  private static CustomerUpdateParams customerUpdateParams;
  private static PaymentMethodCreateParams paymentMethodCreateParams;
  private final StripeCustomerServiceImpl service = new StripeCustomerServiceImpl();

  @BeforeAll
  static void beforeAll() {
    customerCreateParams = CustomerCreateParams.builder()
            .setEmail(testEmail)
            .setName("Foo")
            .build();
    customerUpdateParams = CustomerUpdateParams.builder()
            .setName("Bar")
            .build();
    paymentMethodCreateParams = PaymentMethodCreateParams.builder()
            .setType(PaymentMethodCreateParams.Type.CARD)
            .setCard(PaymentMethodCreateParams.CardDetails.builder()
                  .setNumber("4242424242424242")
                  .setCvc("123")
                  .setExpYear(2029L)
                  .setExpMonth(12L)
                  .build())
            .build();

    //create a customer so that we can later test updating and deleting it
    StripeCustomerServiceImpl serviceForBefore = new StripeCustomerServiceImpl();
    serviceForBefore.setStripeKey(stripeKey);
    customerStripeId = serviceForBefore.createStripeCustomer(customerCreateParams);
  }

  @Test
  void test_createStripeCustomer_createsCustomer() {
    service.setStripeKey(stripeKey);
    String testId = service.createStripeCustomer(customerCreateParams);
    assertEquals("cus_", testId.substring(0, 4));
  }

  @Test
  void test_createStripeCustomer_throwsExceptionOnStripeError() {
    service.setStripeKey(badStripeKey);
    assertThrows(CaughtStripeException.class, () -> service.createStripeCustomer(customerCreateParams));
  }

  @Test
  void test_retrieveStripeCustomer_getsCustomer() {
    service.setStripeKey(stripeKey);
    var customer = service.retrieveStripeCustomer(customerStripeId);
    assertEquals(testEmail, customer.getEmail());
  }

  @Test
  void test_retrieveStripeCustomer_throwsExceptionOnStripeError() {
    service.setStripeKey(badStripeKey);
    assertThrows(CaughtStripeException.class, () -> service.retrieveStripeCustomer(customerStripeId));
  }

  @Test
  void test_updateStripeCustomer_updatesExistingCustomer() {
    service.setStripeKey(stripeKey);
    service.updateStripeCustomer(customerStripeId, customerUpdateParams);
    var updatedCustomer = service.retrieveStripeCustomer(customerStripeId);
    assertEquals("Bar", updatedCustomer.getName());
  }

  @Test
  void test_updateStripeCustomer_throwsExceptionOnStripeError() {
    service.setStripeKey(badStripeKey);
    assertThrows(CaughtStripeException.class, () -> service.updateStripeCustomer(customerStripeId,
            customerUpdateParams));
  }

  @Test
  void test_deleteStripeCustomer_deletesCustomer() {
    service.setStripeKey(stripeKey);
    service.deleteStripeCustomer(customerStripeId);

    //deleted customers CAN still be retrieved through the API
    //see: https://stripe.com/docs/api/customers/delete
    var deletedCustomer = service.retrieveStripeCustomer(customerStripeId);
    assertTrue(deletedCustomer.getDeleted());
  }

  @Test
  void test_deleteStripeCustomer_throwsExceptionOnStripeError() {
    service.setStripeKey(badStripeKey);
    assertThrows(CaughtStripeException.class, () -> service.deleteStripeCustomer(customerStripeId));
  }

  @Test
  void test_createPaymentMethod_createsPaymentMethod() {
    service.setStripeKey(stripeKey);
    var id = service.createPaymentMethod(paymentMethodCreateParams);
    assertEquals("pm_", id.substring(0, 3));
  }

  @Test
  void test_createPaymentMethod_throwsExceptionOnStripeError() {
    service.setStripeKey(badStripeKey);
    assertThrows(CaughtStripeException.class, () ->
            service.createPaymentMethod(paymentMethodCreateParams));
  }

  @Test
  void test_retrieveStripePaymentMethod_getsPaymentMethod() {
    service.setStripeKey(stripeKey);
    var id = service.createPaymentMethod(paymentMethodCreateParams);
    assertEquals(id, service.retrieveStripePaymentMethod(id).getId());
  }

  @Test
  void test_retrieveStripePaymentMethod_throwsExceptionOnStripeError() {
    service.setStripeKey(badStripeKey);
    assertThrows(CaughtStripeException.class, () -> service.retrieveStripePaymentMethod("foo"));
  }

  @Test
  void test_attachStripePaymentMethod_attachesPaymentMethodToCustomer() {
    service.setStripeKey(stripeKey);
    String customerId = service.createStripeCustomer(customerCreateParams);
    String paymentMethodId = service.createPaymentMethod(paymentMethodCreateParams);

    service.attachStripePaymentMethod(paymentMethodId, customerId);

    var retrievedCustomer = service.retrieveStripeCustomer(customerId);
    var retrievedPaymentMethod = service.retrieveStripePaymentMethod(paymentMethodId);
    assertEquals(retrievedPaymentMethod.getCustomer(), retrievedCustomer.getId());
  }

  @Test
  void test_attachStripePaymentMethod_throwsExceptionOnStripeError() {
    service.setStripeKey(badStripeKey);
    assertThrows(CaughtStripeException.class, () ->
            service.attachStripePaymentMethod("foo", "bar"));
  }

  @Test
  void test_detachStripePaymentMethod_detachesPaymentMethodFromCustomer() {
    service.setStripeKey(stripeKey);
    String customerId = service.createStripeCustomer(customerCreateParams);
    String paymentMethodId = service.createPaymentMethod(paymentMethodCreateParams);

    //test attaching here because we have to attach properly before detaching
    service.attachStripePaymentMethod(paymentMethodId, customerId);

    var retrievedCustomer = service.retrieveStripeCustomer(customerId);
    var retrievedPaymentMethod = service.retrieveStripePaymentMethod(paymentMethodId);
    assertEquals(retrievedPaymentMethod.getCustomer(), retrievedCustomer.getId());

    //now test detaching
    service.detachStripePaymentMethod(paymentMethodId);
    retrievedPaymentMethod = service.retrieveStripePaymentMethod(paymentMethodId);
    assertNull(retrievedPaymentMethod.getCustomer());
  }

  @Test
  void test_detachStripePaymentMethod_throwsExceptionOnStripeError() {
    service.setStripeKey(badStripeKey);
    assertThrows(CaughtStripeException.class, () ->
            service.detachStripePaymentMethod("foo"));
  }
}
