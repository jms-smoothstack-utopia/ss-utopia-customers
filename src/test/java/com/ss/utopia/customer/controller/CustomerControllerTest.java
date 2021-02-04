package com.ss.utopia.customer.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ss.utopia.customer.model.Address;
import com.ss.utopia.customer.model.Customer;
import com.ss.utopia.customer.model.CustomerDto;
import com.ss.utopia.customer.service.CustomerService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CustomerControllerTest {

  private static final CustomerService service = Mockito.mock(CustomerService.class);
  private static final CustomerController controller = new CustomerController(service);
  private Customer validCustomerWithId;
  private CustomerDto validDtoWithId;
  private Customer noIdValidCustomer;
  private CustomerDto noIdValidDto;
  private Address validAddress;

  @BeforeEach
  void beforeEach() {
    Mockito.reset(service);

    // setup Customer objs
    validCustomerWithId = new Customer();
    noIdValidCustomer = new Customer();
    validCustomerWithId.setId(1L);
    validCustomerWithId.setFirstName("John");
    noIdValidCustomer.setFirstName(validCustomerWithId.getFirstName());
    validCustomerWithId.setLastName("Doe");
    noIdValidCustomer.setLastName(validCustomerWithId.getLastName());
    validCustomerWithId.setEmail("test@test.com");
    noIdValidCustomer.setEmail(validCustomerWithId.getEmail());

    // setup Address
    validAddress = new Address();
    validAddress.setCardinality(1);
    validAddress.setId(1L);
    validAddress.setLine1("123 Main St.");
    validAddress.setLine2("Apt #1");
    validAddress.setCity("Las Vegas");
    validAddress.setState("NV");
    validAddress.setZipcode("12345");

    // add addr and empty payment methods
    validCustomerWithId.setAddresses(Set.of(validAddress));
    noIdValidCustomer.setAddresses(Set.of(validAddress));
    validCustomerWithId.setPaymentMethods(Collections.emptySet());
    noIdValidCustomer.setPaymentMethods(Collections.emptySet());

    // setup DTOs
    validDtoWithId = new CustomerDto();
    noIdValidDto = new CustomerDto();
    validDtoWithId.setId(validCustomerWithId.getId());
    validDtoWithId.setFirstName(validCustomerWithId.getFirstName());
    noIdValidDto.setFirstName(validCustomerWithId.getFirstName());
    validDtoWithId.setLastName(validCustomerWithId.getLastName());
    noIdValidDto.setLastName(validCustomerWithId.getLastName());
    validDtoWithId.setEmail(validCustomerWithId.getEmail());
    noIdValidDto.setEmail(validCustomerWithId.getEmail());
    validDtoWithId.setAddrLine1(validAddress.getLine1());
    noIdValidDto.setAddrLine1(validAddress.getLine1());
    validDtoWithId.setAddrLine2(validAddress.getLine2());
    noIdValidDto.setAddrLine2(validAddress.getLine2());
    validDtoWithId.setCity(validAddress.getCity());
    noIdValidDto.setCity(validAddress.getCity());
    validDtoWithId.setState(validAddress.getState());
    noIdValidDto.setState(validAddress.getState());
    validDtoWithId.setZipcode(validAddress.getZipcode());
    noIdValidDto.setZipcode(validAddress.getZipcode());
  }

  @Test
  void test_getAll_ReturnsListWith200StatusCode() {
    when(service.getAll()).thenReturn(List.of(validCustomerWithId));

    var response = controller.getAll();

    assertEquals(200, response.getStatusCodeValue());

    var expectedList = List.of(validCustomerWithId);
    assertEquals(expectedList, response.getBody());
  }

  @Test
  void test_getAll_ReturnsEmptyListWith204StatusCodeIfNoCustomers() {
    var response = controller.getAll();

    assertEquals(204, response.getStatusCodeValue());

    assertNull(response.getBody());
  }

  @Test
  void test_getById_ReturnsValidCustomerWith200StatusCode() {
    when(service.getById(validCustomerWithId.getId())).thenReturn(Optional.of(validCustomerWithId));

    var response = controller.getById(validCustomerWithId.getId());

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(validCustomerWithId, response.getBody());
  }

  @Test
  void test_getById_Returns404StatusCodeOnInvalidId() {
    var response = controller.getById(-1L);

    assertEquals(404, response.getStatusCodeValue());
    assertNull(response.getBody());
  }

  @Test
  void test_createNew_ReturnsCreatedIdAnd201StatusCodeOnValidDto() {
    validDtoWithId.setId(null);

    when(service.create(any(Customer.class))).thenReturn(validCustomerWithId);

    var response = controller.createNew(noIdValidDto);

    assertEquals(201, response.getStatusCodeValue());
    assertEquals(validCustomerWithId.getId(), response.getBody());
  }

  //util
  boolean noValidationViolations(CustomerDto customerDto) {
    var validator = Validation.buildDefaultValidatorFactory().getValidator();

    var violations = validator.validate(customerDto);

    return violations.isEmpty();
  }

  @Test
  void test_createNew_DoesNotAllowInvalidFirstName() {
    validDtoWithId.setFirstName(null);
    assertFalse(noValidationViolations(validDtoWithId));


    validDtoWithId.setFirstName("");
    assertFalse(noValidationViolations(validDtoWithId));
  }

  @Test
  void test_createNew_DoesNotAllowInvalidLastName() {
    validDtoWithId.setLastName(null);
    assertFalse(noValidationViolations(validDtoWithId));

    validDtoWithId.setLastName("");
    assertFalse(noValidationViolations(validDtoWithId));
  }

  @Test
  void test_createNew_DoesNotAllowInvalidEmail() {
    validDtoWithId.setEmail(null);
    assertFalse(noValidationViolations(validDtoWithId));

    validDtoWithId.setEmail("");
    assertFalse(noValidationViolations(validDtoWithId));

    validDtoWithId.setEmail("asdfasdf");
    assertFalse(noValidationViolations(validDtoWithId));
  }

  @Test
  void test_createNew_DoesNotAllowInvalidAddrLine1() {
    validDtoWithId.setAddrLine1(null);
    assertFalse(noValidationViolations(validDtoWithId));

    validDtoWithId.setAddrLine1("");
    assertFalse(noValidationViolations(validDtoWithId));
  }

  @Test
  void test_createNew_DoesNotAllowInvalidCity() {
    validDtoWithId.setCity(null);
    assertFalse(noValidationViolations(validDtoWithId));

    validDtoWithId.setCity("");
    assertFalse(noValidationViolations(validDtoWithId));
  }

  @Test
  void test_createNew_DoesNotAllowInvalidState() {
    validDtoWithId.setState(null);
    assertFalse(noValidationViolations(validDtoWithId));

    validDtoWithId.setState("");
    assertFalse(noValidationViolations(validDtoWithId));

    validDtoWithId.setState("a");
    assertFalse(noValidationViolations(validDtoWithId));

    validDtoWithId.setState("aaa");
    assertFalse(noValidationViolations(validDtoWithId));
  }

  @Test
  void test_createNew_DoesNotAllowInvalidZipcode() {
    validDtoWithId.setZipcode(null);
    assertFalse(noValidationViolations(validDtoWithId));

    validDtoWithId.setZipcode("");
    assertFalse(noValidationViolations(validDtoWithId));

    validDtoWithId.setZipcode("asdfd-asdf");
    assertFalse(noValidationViolations(validDtoWithId));

    // test valid zipcodes as well
    validDtoWithId.setZipcode("12345-1234");
    assertTrue(noValidationViolations(validDtoWithId));

    validDtoWithId.setZipcode("12345");
    assertTrue(noValidationViolations(validDtoWithId));
  }

  @Test
  void test_updateExisting_ReturnsBadRequestOnMissingId() {
    var response = controller.updateExisting(noIdValidDto);

    assertEquals(400, response.getStatusCodeValue());
    assertNotNull(response.getBody());

    try {
      Map<String, String> body = (Map<String, String>) response.getBody();

      assertTrue(body.containsKey("message"));

      assertFalse(body.get("message").isBlank());
    } catch (ClassCastException ex) {
      fail(ex.getMessage());
    }
  }

  @Test
  void test_updateExisting_Returns200StatusCodeOnSuccess() {
    when(service.update(any(Customer.class))).thenReturn(validCustomerWithId);

    var response = controller.updateExisting(validDtoWithId);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(validCustomerWithId, response.getBody());
  }

  @Test
  void test_delete_Returns204StatusCode() {
    var response = controller.delete(1L);

    assertEquals(204, response.getStatusCodeValue());
    assertNull(response.getBody());
  }
}