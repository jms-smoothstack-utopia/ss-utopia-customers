package com.ss.utopia.customer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.ss.utopia.customer.client.AccountsClient;
import com.ss.utopia.customer.client.authentication.ServiceAuthenticationProvider;
import com.ss.utopia.customer.dto.CreateCustomerDto;
import com.ss.utopia.customer.dto.PaymentMethodDto;
import com.ss.utopia.customer.dto.UpdateCustomerDto;
import com.ss.utopia.customer.dto.UpdateCustomerLoyaltyDto;
import com.ss.utopia.customer.entity.Address;
import com.ss.utopia.customer.entity.Customer;
import com.ss.utopia.customer.entity.PaymentMethod;
import com.ss.utopia.customer.exception.DuplicateEmailException;
import com.ss.utopia.customer.exception.NoSuchCustomerException;
import com.ss.utopia.customer.repository.CustomerRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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
  private static CreateCustomerDto dtoFirstCustomer;
  private static UpdateCustomerDto dtoSecondCustomer;

  private final CustomerRepository repository = Mockito.mock(CustomerRepository.class);
  private final AccountsClient accountsClient = Mockito.mock(AccountsClient.class);
  private final ServiceAuthenticationProvider serviceAuthenticationProvider = Mockito.mock(
      ServiceAuthenticationProvider.class);
  private final CustomerService service = new CustomerServiceImpl(repository,
                                                                  accountsClient,
                                                                  serviceAuthenticationProvider);

  @BeforeAll
  static void beforeAll() {
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
        .paymentMethods(Set.of(
            PaymentMethod.builder()
                .id(1L)
                .ownerId(firstCustomerId)
                .accountNum("123456789")
                .notes("primary method")
                .build()))
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
                .accountNum("98765431")
                .notes(null)
                .build()))
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

  /*  bad/possibly unnecessary, might refactor or delete addPaymentMethod
  @Test
  void test_addPaymentMethod_AddsPaymentMethod() {
    when(repository.findById(firstCustomer.getId())).thenReturn(Optional.of(firstCustomer));
    //might need to mock repository.save here

    var dtoPaymentMethod = PaymentMethodDto.builder()
            .accountNum("555")
            .notes("DTO for test")
            .build();

    service.addPaymentMethod(firstCustomer.getId(), dtoPaymentMethod);
    assertEquals(firstCustomer.getPaymentMethods().size(), 2);

  }
  */
}