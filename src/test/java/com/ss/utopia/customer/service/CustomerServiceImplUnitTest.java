package com.ss.utopia.customer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.ss.utopia.customer.client.AccountsClient;
import com.ss.utopia.customer.client.authentication.ServiceAuthenticationProvider;
import com.ss.utopia.customer.dto.*;
import com.ss.utopia.customer.entity.Address;
import com.ss.utopia.customer.entity.Customer;
import com.ss.utopia.customer.entity.PaymentMethod;
import com.ss.utopia.customer.exception.AccountsClientException;
import com.ss.utopia.customer.exception.DuplicateEmailException;
import com.ss.utopia.customer.exception.NoSuchCustomerException;
import com.ss.utopia.customer.exception.NoSuchPaymentMethod;
import com.ss.utopia.customer.repository.CustomerRepository;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.stripe.exception.StripeException;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentMethodCreateParams;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

class CustomerServiceImplUnitTest {

  private final static UUID firstCustomerId = UUID.randomUUID();
  private final static UUID secondCustomerId = UUID.randomUUID();
  private static Customer firstCustomer;
  private static Customer secondCustomer;
  private static PaymentMethod paymentMethodFirstCustomer;
  private static PaymentMethodDto paymentMethodFirstCustomerDto;
  private static PaymentMethodCreateParams mockPaymentMethodCreateParams;
  private static com.stripe.model.PaymentMethod mockPaymentMethod;
  private static CreateCustomerDto dtoFirstCustomer;
  private static UpdateCustomerDto dtoSecondCustomer;

  private final CustomerRepository repository = Mockito.mock(CustomerRepository.class);
  private final AccountsClient accountsClient = Mockito.mock(AccountsClient.class);
  private final ServiceAuthenticationProvider serviceAuthenticationProvider = Mockito.mock(
      ServiceAuthenticationProvider.class);
  private final StripeCustomerServiceImpl stripeCustomerService = Mockito.mock(
          StripeCustomerServiceImpl.class);

  private final CustomerService service = new CustomerServiceImpl(repository,
                                                                  accountsClient,
                                                                  serviceAuthenticationProvider,
                                                                  stripeCustomerService);

  @BeforeAll
  static void beforeAll() {

    paymentMethodFirstCustomerDto = PaymentMethodDto.builder()
        .cardNumber("4242424242424242")   //card number that always succeeds
        .expMonth(12L)                    //https://stripe.com/docs/testing#cards
        .expYear(2029L)
        .cvc("000")
        .notes("primary method")
        .build();

    mockPaymentMethodCreateParams = PaymentMethodCreateParams.builder()
        .setType(PaymentMethodCreateParams.Type.CARD)
        .setCard(PaymentMethodCreateParams.CardDetails.builder()
            .setNumber(paymentMethodFirstCustomerDto.getCardNumber())
            .setExpMonth(paymentMethodFirstCustomerDto.getExpMonth())
            .setExpYear(paymentMethodFirstCustomerDto.getExpYear())
            .setCvc(paymentMethodFirstCustomerDto.getCvc())
            .build())
        .build();

    try {
      mockPaymentMethod = com.stripe.model.PaymentMethod.create(mockPaymentMethodCreateParams,
              RequestOptions.builder()
                      .setApiKey("sk_test_4eC39HqLyjWDarjtT1zdp7dc")  //public test key
                      .build());
    } catch (StripeException e) {
      e.printStackTrace();
    }

    paymentMethodFirstCustomer = PaymentMethod.builder()
            .id(1L)
            .ownerId(firstCustomerId)
            .stripeId(mockPaymentMethod.getId())
            .notes(paymentMethodFirstCustomerDto.getNotes())
            .build();

    var paymentMethodSet = new HashSet<PaymentMethod>();
    paymentMethodSet.add(paymentMethodFirstCustomer);

    firstCustomer = Customer.builder()
        .id(firstCustomerId)
        .firstName("John")
        .lastName("Smith")
        .email("john_smith@test.com")
        .loyaltyPoints(7)
        .addresses(Set.of(
            Address.builder()
                .id(1L)
                .cardinality(1)
                .line1("123 Main St")
                .line2("Apt #5")
                .city("Atlanta")
                .state("GA")
                .zipcode("12345-6789").build()))
        .paymentMethods(paymentMethodSet)
        .stripeId("cus_TestStripeId01")
        .build();

    dtoFirstCustomer = CreateCustomerDto.builder()
        .firstName(firstCustomer.getFirstName())
        .lastName(firstCustomer.getLastName())
        .email(firstCustomer.getEmail())
        .addrLine1("123 Main St")
        .addrLine2("Apt #5")
        .city("Atlanta")
        .state("GA")
        .zipcode("12345-6789")
        .build();

    UUID secondTestId = UUID.randomUUID();
    secondCustomer = Customer.builder()
        .id(secondCustomerId)
        .firstName("Jane")
        .lastName("Doe")
        .email("jane_doe@test.com")
        .loyaltyPoints(4)
        .addresses(Set.of(
            Address.builder()
                .id(2L)
                .cardinality(1)
                .line1("456 Strawberry Ln")
                .line2(null)
                .city("Las Vegas")
                .state("NV")
                .zipcode("98765").build()))
        .paymentMethods(Set.of(
            PaymentMethod.builder()
                .id(2L)
                .ownerId(secondCustomerId)
                .stripeId("pm_FooBarFooBarFooBarFooBar")
                .notes(null)
                .build()))
            .stripeId("cus_TestStripeId02")
        .build();

    dtoSecondCustomer = UpdateCustomerDto.builder()
        .firstName(secondCustomer.getFirstName())
        .lastName(secondCustomer.getLastName())
        .email(secondCustomer.getEmail())
        .addrLine1("456 Strawberry Ln")
        .addrLine2(null)
        .city("Las Vegas")
        .state("NV")
        .zipcode("98765")
        .build();
  }

  @BeforeEach
  void beforeEach() {
    Mockito.reset(repository);
    Mockito.reset(accountsClient);
    Mockito.reset(serviceAuthenticationProvider);

    when(serviceAuthenticationProvider.getAuthorizationHeader())
        .thenReturn("Bearer abc.def.xyz");
  }

  @Test
  void test_getAllCustomers_ReturnsListOfTestItems() {
    when(repository.findAll()).thenReturn(List.of(firstCustomer, secondCustomer));

    var expected = List.of(firstCustomer, secondCustomer);
    var actual = service.getAllCustomers();

    assertEquals(expected, actual);
  }

  @Test
  void test_getCustomerById_ReturnsExpectedResult() {
    when(repository.findById(firstCustomer.getId())).thenReturn(Optional.of(firstCustomer));
    var result = service.getCustomerById(firstCustomer.getId());
    assertEquals(firstCustomer, result);

    when(repository.findById(secondCustomer.getId())).thenReturn(Optional.of(secondCustomer));
    result = service.getCustomerById(secondCustomer.getId());
    assertEquals(secondCustomer, result);
  }

  @Test
  void test_getCustomerById_ThrowsIllegalArgumentExceptionOnInvalidId() {
    assertThrows(IllegalArgumentException.class,
                 () -> service.getCustomerById(null));
    assertThrows(IllegalArgumentException.class,
                 () -> service.getCustomerById(UUID.fromString("")));
    assertThrows(IllegalArgumentException.class,
                 () -> service.getCustomerById(UUID.fromString("asdfasdfasdfasdf")));
    assertThrows(IllegalArgumentException.class,
                 () -> service.getCustomerById(UUID.fromString("1")));
    assertThrows(IllegalArgumentException.class,
                 () -> service.getCustomerById(UUID.fromString("-1")));
    assertThrows(IllegalArgumentException.class,
                 () -> service.getCustomerById(UUID.fromString("0")));
  }

  @Test
  void test_getCustomerById_ThrowsIllegalArgumentExceptionOnNullId() {
    assertThrows(IllegalArgumentException.class, () -> service.getCustomerById(null));
  }

  @Test
  void test_getCustomerByEmail_ReturnsExpectedResult() {
    when(repository.findByEmail(firstCustomer.getEmail())).thenReturn(Optional.of(firstCustomer));

    var customer = service.getCustomerByEmail(firstCustomer.getEmail());
    assertEquals(firstCustomer, customer);
  }

  @Test
  void test_getCustomerByEmail_ThrowsIllegalArgumentExceptionOnNullId() {
    assertThrows(IllegalArgumentException.class, () -> service.getCustomerByEmail(null));
  }

  @Test
  void test_getCustomerLoyaltyPointsById_ReturnsCustomerWithExpectedValuesOnSuccess() {
    when(repository.findById(firstCustomer.getId())).thenReturn(Optional.of(firstCustomer));
    var result = service.getCustomerLoyaltyPoints(firstCustomer.getId());
    assertEquals(firstCustomer.getLoyaltyPoints(), result);

    when(repository.findById(secondCustomer.getId())).thenReturn(Optional.of(secondCustomer));
    result = service.getCustomerLoyaltyPoints(secondCustomer.getId());
    assertEquals(secondCustomer.getLoyaltyPoints(), result);
  }

  @Test
  void test_getCustomerLoyaltyPointsById_ThrowsIllegalArgumentExceptionOnInvalidId() {
    assertThrows(IllegalArgumentException.class,
                 () -> service.getCustomerLoyaltyPoints(null));
    assertThrows(IllegalArgumentException.class,
                 () -> service.getCustomerLoyaltyPoints(UUID.fromString("")));
    assertThrows(IllegalArgumentException.class,
                 () -> service.getCustomerLoyaltyPoints(UUID.fromString("asdfasdfasdfasdf")));
    assertThrows(IllegalArgumentException.class,
                 () -> service.getCustomerLoyaltyPoints(UUID.fromString("1")));
    assertThrows(IllegalArgumentException.class,
                 () -> service.getCustomerLoyaltyPoints(UUID.fromString("-1")));
    assertThrows(IllegalArgumentException.class,
                 () -> service.getCustomerLoyaltyPoints(UUID.fromString("0")));
  }

  @Test
  void test_updateCustomerLoyaltyPoints_ThrowsExceptionOnNegativeValue() {
    var mockUpdateDto = UpdateCustomerLoyaltyDto.builder()
        .increment(false)
        .pointsToChange(1000)
        .build();
    when(repository.findById(firstCustomerId)).thenReturn(Optional.of(firstCustomer));

    assertThrows(IllegalStateException.class,
                 () -> service.updateCustomerLoyaltyPoints(firstCustomerId, mockUpdateDto));
  }

  @Test
  void test_createNewCustomer_ReturnsCustomerWithExpectedValuesOnSuccess() {
    when(repository.save(any(Customer.class))).thenReturn(firstCustomer);
    when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(accountsClient.createNewAccount(any())).thenReturn(ResponseEntity.ok(UUID.randomUUID()));

    var result = service.createNewCustomer(dtoFirstCustomer);
    assertEquals(firstCustomer, result);
  }

  @Test
  void test_createNewCustomer_ThrowsDuplicateEmailExceptionOnDuplicateEmailRecord() {
    when(repository.findByEmail(firstCustomer.getEmail())).thenReturn(Optional.of(firstCustomer));

    assertThrows(DuplicateEmailException.class,
                 () -> service.createNewCustomer(CreateCustomerDto.builder()
                                                     .email(firstCustomer.getEmail())
                                                     .build()));
  }

  @Test
  void test_updateCustomer_ThrowsDuplicateEmailExceptionOnDuplicateEmailRecord() {
    when(repository.findByEmail(firstCustomer.getEmail())).thenReturn(Optional.of(firstCustomer));
    when(repository.findByEmail(secondCustomer.getEmail())).thenReturn(Optional.of(secondCustomer));

    var dtoForUpdate = UpdateCustomerDto.builder()
        .firstName(secondCustomer.getFirstName())
        .lastName(secondCustomer.getLastName())
        .email(firstCustomer.getEmail())    //trying to use firstCustomer's email!
        .addrLine1("456 Strawberry Ln")
        .addrLine2(null)
        .city("Las Vegas")
        .state("NV")
        .zipcode("98765")
        .build();

    assertThrows(DuplicateEmailException.class,
                 () -> service.updateCustomer(secondCustomer.getId(), dtoForUpdate));
  }

  @Test
  void test_updateCustomer_CallsAccountsClientUpdate() {
    when(repository.findById(firstCustomerId))
        .thenReturn(Optional.of(firstCustomer));

    var oldEmail = firstCustomer.getEmail();
    var newEmail = "some_new_email@test.com";

    dtoFirstCustomer.setEmail("some_new_email@test.com");

    var result = service.updateCustomer(firstCustomer.getId(),
                                        UpdateCustomerDto.builder().email(newEmail).build());

    Mockito.verify(accountsClient, times(1))
        .updateCustomerEmail(serviceAuthenticationProvider.getAuthorizationHeader(),
                             firstCustomer.getId(),
                             newEmail);

    firstCustomer.setEmail(oldEmail);
  }

  @Test
  void test_createNewCustomer_ThrowsAccountsClientExceptionOnNullUUID() {
    when(accountsClient.createNewAccount(any()))
        .thenReturn(ResponseEntity.of(Optional.empty()));

    assertThrows(AccountsClientException.class,
                 () -> service.createNewCustomer(dtoFirstCustomer));
  }

  @Test
  void test_removeCustomerById_ThrowsIllegalArgumentExceptionIfNullId() {
    assertThrows(IllegalArgumentException.class, () -> service.removeCustomerById(null));
  }

  @Test
  void test_removeCustomerById_CallsRepositoryDeleteMethodWhenResultFound() {
    when(repository.findById(any())).thenReturn(Optional.of(firstCustomer));

    service.removeCustomerById(firstCustomerId);

    Mockito.verify(repository).findById(firstCustomerId);

    Mockito.verify(repository).delete(firstCustomer);
  }

  @Test
  void test_getPaymentMethod_ThrowsIllegalArgumentExceptionIfEitherIdIsNull() {
    assertThrows(IllegalArgumentException.class,
                 () -> service.getPaymentMethod(null, null));
    assertThrows(IllegalArgumentException.class,
                 () -> service.getPaymentMethod(null, 1L));
    assertThrows(IllegalArgumentException.class,
                 () -> service.getPaymentMethod(firstCustomerId, null));
  }

  @Test
  void test_getPaymentMethod_ReturnsExpectedPaymentMethod() {
    when(repository.findById(firstCustomerId)).thenReturn(Optional.of(firstCustomer));

    var result = service.getPaymentMethod(firstCustomerId, paymentMethodFirstCustomer.getId());

    assertEquals(paymentMethodFirstCustomer, result);
  }

  @Test
  void test_getPaymentMethod_ThrowNoSuchPaymentMethodIfNotFound() {
    when(repository.findById(firstCustomerId)).thenReturn(Optional.of(firstCustomer));

    assertThrows(NoSuchPaymentMethod.class,
                 () -> service.getPaymentMethod(firstCustomerId, -1L));
  }

  @Test
  void test_getPaymentMethod_ThrowsNoSuchCustomerExceptionIfNotFound() {
    when(repository.findById(any())).thenReturn(Optional.empty());

    assertThrows(NoSuchCustomerException.class,
                 () -> service.getPaymentMethod(firstCustomerId,
                                                paymentMethodFirstCustomer.getId()));
  }

  @Test
  void test_getAllPaymentMethodsFor_returnsPaymentMethods() {
    when(repository.findById(firstCustomerId)).thenReturn(Optional.of(firstCustomer));

    var result = service.getAllPaymentMethodsFor(firstCustomerId);

    assertTrue(result.contains(paymentMethodFirstCustomer));
  }

  @Test
  void test_getAllPaymentMethodsFor_ThrowsNoSuchCustomerExceptionIfNotFound() {
    when(repository.findById(any())).thenReturn(Optional.empty());

    assertThrows(NoSuchCustomerException.class,
            () -> service.getAllPaymentMethodsFor(firstCustomerId));
  }

  @Test
  void test_addPaymentMethod_ReturnsCreatedPaymentId() {
    firstCustomer.getPaymentMethods().clear();

    var paymentMethodSet = new HashSet<PaymentMethod>();
    paymentMethodSet.add(paymentMethodFirstCustomer);

    when(repository.findById(firstCustomerId))
        .thenReturn(Optional.of(firstCustomer));


    when(repository.save(any())).thenReturn(Customer.builder()
                                                .id(firstCustomerId)
                                                .firstName("John")
                                                .lastName("Smith")
                                                .email("john_smith@test.com")
                                                .loyaltyPoints(7)
                                                .addresses(Set.of(
                                                    Address.builder()
                                                        .id(1L)
                                                        .cardinality(1)
                                                        .line1("123 Main St")
                                                        .line2("Apt #5")
                                                        .city("Atlanta")
                                                        .state("GA")
                                                        .zipcode("12345-6789").build()))
                                                .paymentMethods(paymentMethodSet)
                                                .stripeId("cus_TestStripeId01")
                                                .build());


    when(stripeCustomerService.createPaymentMethod(any())).thenReturn(mockPaymentMethod.getId());
    when(stripeCustomerService.retrieveStripePaymentMethod(anyString()))
            .thenReturn(mockPaymentMethod);

    doNothing().when(stripeCustomerService).attachStripePaymentMethod(anyString(), anyString());

    var result = service.addPaymentMethod(firstCustomerId, paymentMethodFirstCustomerDto);

    assertEquals(paymentMethodFirstCustomer.getId(), result);

    firstCustomer.setPaymentMethods(paymentMethodSet);
  }

  @Test
  void test_addPaymentMethod_ThrowsNoSuchElementIfProblemSavingPaymentMethod() {
    firstCustomer.getPaymentMethods().clear();

    var paymentMethodSet = new HashSet<PaymentMethod>();
    paymentMethodSet.add(paymentMethodFirstCustomer);

    when(repository.findById(firstCustomerId))
        .thenReturn(Optional.of(firstCustomer));
    when(stripeCustomerService.createPaymentMethod(any())).thenReturn(mockPaymentMethod.getId());
    when(stripeCustomerService.retrieveStripePaymentMethod(anyString()))
            .thenReturn(mockPaymentMethod);
    doNothing().when(stripeCustomerService).attachStripePaymentMethod(anyString(), anyString());

    when(repository.save(any())).thenReturn(Customer.builder()
                                                .id(firstCustomerId)
                                                .firstName("John")
                                                .lastName("Smith")
                                                .email("john_smith@test.com")
                                                .loyaltyPoints(7)
                                                .addresses(Set.of(
                                                    Address.builder()
                                                        .id(1L)
                                                        .cardinality(1)
                                                        .line1("123 Main St")
                                                        .line2("Apt #5")
                                                        .city("Atlanta")
                                                        .state("GA")
                                                        .zipcode("12345-6789").build()))
                                                .paymentMethods(Collections.emptySet())
                                                .stripeId("cus_TestStripeId01")
                                                .build());

    assertThrows(NoSuchElementException.class,
                 () -> service.addPaymentMethod(firstCustomerId, paymentMethodFirstCustomerDto));

    firstCustomer.setPaymentMethods(paymentMethodSet);
  }

  @Test
  void test_updatePaymentMethod_ThrowsIllegalArgumentExceptionOnNullIds() {
    var method = UpdatePaymentMethodDto.builder().build();

    assertThrows(IllegalArgumentException.class,
                 () -> service.updatePaymentMethod(null, null, null));
    assertThrows(IllegalArgumentException.class,
                 () -> service.updatePaymentMethod(null, 1L, method));
    assertThrows(IllegalArgumentException.class,
                 () -> service.updatePaymentMethod(firstCustomerId, null, method));
    assertThrows(IllegalArgumentException.class,
                 () -> service.updatePaymentMethod(firstCustomerId, 1L, null));
  }

  @Test
  void test_updatePaymentMethod_ThrowsNoSuchPaymentMethodOnNotFound() {
    var reset = firstCustomer.getPaymentMethods();
    firstCustomer.setPaymentMethods(Collections.emptySet());

    when(repository.findById(any()))
        .thenReturn(Optional.of(firstCustomer));

    assertThrows(NoSuchPaymentMethod.class,
                 () -> service.updatePaymentMethod(firstCustomerId,
                                                   paymentMethodFirstCustomer.getId(),
                                                   UpdatePaymentMethodDto.builder().build()));

    firstCustomer.setPaymentMethods(reset);
  }


  @Test
  void test_updatePaymentMethod_PerformsUpdate() {
    when(repository.findById(any()))
        .thenReturn(Optional.of(firstCustomer));

    var oldSet = new HashSet<>(firstCustomer.getPaymentMethods());

    assertTrue(oldSet.contains(paymentMethodFirstCustomer));

    service.updatePaymentMethod(firstCustomerId,
                                paymentMethodFirstCustomer.getId(),
                                UpdatePaymentMethodDto.builder()
                                    .notes("something new")
                                    .build());

    Mockito.verify(repository).save(any());

    var expected = PaymentMethod.builder()
        .id(paymentMethodFirstCustomer.getId())
        .ownerId(firstCustomerId)
        .stripeId(paymentMethodFirstCustomer.getStripeId())
        .notes("something new")
        .build();

    assertNotEquals(oldSet, firstCustomer.getPaymentMethods());

    assertFalse(oldSet.contains(expected));

    var result = firstCustomer.getPaymentMethods()
        .stream()
        .filter(p -> p.getId().equals(paymentMethodFirstCustomer.getId()))
        .findFirst()
        .orElseThrow();

    assertEquals(expected, result);


    var replace = new HashSet<PaymentMethod>();
    replace.add(paymentMethodFirstCustomer);
    firstCustomer.setPaymentMethods(replace);
  }

  @Test
  void test_removePaymentMethods_ThrowsIllegalArgumentExceptionOnNullIds() {
    assertThrows(IllegalArgumentException.class,
                 () -> service.removePaymentMethod(firstCustomerId, null));
    assertThrows(IllegalArgumentException.class,
                 () -> service.removePaymentMethod(null, paymentMethodFirstCustomer.getId()));
    assertThrows(IllegalArgumentException.class,
                 () -> service.removePaymentMethod(null, null));
  }

  @Test
  void test_removePaymentMethod_RemovesPaymentMethod() {
    when(repository.findById(any()))
        .thenReturn(Optional.ofNullable(firstCustomer));

    var methodSet = firstCustomer.getPaymentMethods();

    assertTrue(methodSet.contains(paymentMethodFirstCustomer));

    service.removePaymentMethod(firstCustomerId, paymentMethodFirstCustomer.getId());

    Mockito.verify(repository).save(firstCustomer);

    assertFalse(methodSet.contains(paymentMethodFirstCustomer));

    var replaceSet = new HashSet<PaymentMethod>();
    replaceSet.add(paymentMethodFirstCustomer);
    firstCustomer.setPaymentMethods(replaceSet);
  }

  @Test
  void test_removePaymentMethod_DoesNotRemoveIfNotFound() {
    when(repository.findById(any()))
        .thenReturn(Optional.ofNullable(firstCustomer));

    var expected = firstCustomer.getPaymentMethods().size();

    assertTrue(expected > 0);

    assertThrows(NoSuchPaymentMethod.class, () ->
            service.removePaymentMethod(firstCustomerId, -1L));

    var result = firstCustomer.getPaymentMethods().size();

    assertEquals(expected, result);

    var replaceSet = new HashSet<PaymentMethod>();
    replaceSet.add(paymentMethodFirstCustomer);
    firstCustomer.setPaymentMethods(replaceSet);
  }
}