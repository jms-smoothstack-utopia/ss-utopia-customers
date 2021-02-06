package com.ss.utopia.customer.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ss.utopia.customer.dto.CustomerDto;
import com.ss.utopia.customer.model.Address;
import com.ss.utopia.customer.model.Customer;
import com.ss.utopia.customer.service.CustomerService;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
  private Customer validCustomer;
  private CustomerDto validDto;

  @BeforeEach
  void beforeEach() {
    Mockito.reset(service);

    // setup Customer objs
    validCustomer = new Customer();

    validCustomer.setId(1L);
    validCustomer.setFirstName("John");

    validCustomer.setLastName("Doe");
    validCustomer.setEmail("test@test.com");

    // setup Address
    Address validAddress = new Address();
    validAddress.setCardinality(1);
    validAddress.setId(1L);
    validAddress.setLine1("123 Main St.");
    validAddress.setLine2("Apt #1");
    validAddress.setCity("Las Vegas");
    validAddress.setState("NV");
    validAddress.setZipcode("12345");

    // add addr and empty payment methods
    validCustomer.setAddresses(Set.of(validAddress));
    validCustomer.setPaymentMethods(Collections.emptySet());

    // setup DTOs
    validDto = new CustomerDto();

    validDto.setFirstName(validCustomer.getFirstName());
    validDto.setLastName(validCustomer.getLastName());
    validDto.setEmail(validCustomer.getEmail());
    validDto.setAddrLine1(validAddress.getLine1());
    validDto.setAddrLine2(validAddress.getLine2());
    validDto.setCity(validAddress.getCity());
    validDto.setState(validAddress.getState());
    validDto.setZipcode(validAddress.getZipcode());
  }

  @Test
  void test_getAll_ReturnsListWith200StatusCode() {
    when(service.getAll()).thenReturn(List.of(validCustomer));

    var response = controller.getAll();

    assertEquals(200, response.getStatusCodeValue());

    var expectedList = List.of(validCustomer);
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
    when(service.getById(validCustomer.getId())).thenReturn(validCustomer);

    var response = controller.getById(validCustomer.getId());

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(validCustomer, response.getBody());
  }

  @Test
  void test_getById_Returns404StatusCodeOnInvalidId() {
    var response = controller.getById(-1L);

    assertEquals(404, response.getStatusCodeValue());
    assertNull(response.getBody());
  }

  @Test
  void test_createNew_ReturnsCreatedIdAnd201StatusCodeOnValidDto() {

    when(service.create(any(Customer.class))).thenReturn(validCustomer);

    var response = controller.createNew(validDto);

    assertEquals(201, response.getStatusCodeValue());
    var expected = URI.create("/customer/" + validCustomer.getId());
    assertEquals(expected, response.getBody());
  }

  //util
  boolean noValidationViolations(CustomerDto customerDto) {
    var validator = Validation.buildDefaultValidatorFactory().getValidator();

    var violations = validator.validate(customerDto);

    return violations.isEmpty();
  }

  @Test
  void test_createNew_DoesNotAllowInvalidFirstName() {
    validDto.setFirstName(null);
    assertFalse(noValidationViolations(validDto));

    validDto.setFirstName("");
    assertFalse(noValidationViolations(validDto));
  }

  @Test
  void test_createNew_DoesNotAllowInvalidLastName() {
    validDto.setLastName(null);
    assertFalse(noValidationViolations(validDto));

    validDto.setLastName("");
    assertFalse(noValidationViolations(validDto));
  }

  @Test
  void test_createNew_DoesNotAllowInvalidEmail() {
    validDto.setEmail(null);
    assertFalse(noValidationViolations(validDto));

    validDto.setEmail("");
    assertFalse(noValidationViolations(validDto));

    validDto.setEmail("asdfasdf");
    assertFalse(noValidationViolations(validDto));
  }

  @Test
  void test_createNew_DoesNotAllowInvalidAddrLine1() {
    validDto.setAddrLine1(null);
    assertFalse(noValidationViolations(validDto));

    validDto.setAddrLine1("");
    assertFalse(noValidationViolations(validDto));
  }

  @Test
  void test_createNew_DoesNotAllowInvalidCity() {
    validDto.setCity(null);
    assertFalse(noValidationViolations(validDto));

    validDto.setCity("");
    assertFalse(noValidationViolations(validDto));
  }

  @Test
  void test_createNew_DoesNotAllowInvalidState() {
    validDto.setState(null);
    assertFalse(noValidationViolations(validDto));

    validDto.setState("");
    assertFalse(noValidationViolations(validDto));

    validDto.setState("a");
    assertFalse(noValidationViolations(validDto));

    validDto.setState("aaa");
    assertFalse(noValidationViolations(validDto));
  }

  @Test
  void test_createNew_DoesNotAllowInvalidZipcode() {
    validDto.setZipcode(null);
    assertFalse(noValidationViolations(validDto));

    validDto.setZipcode("");
    assertFalse(noValidationViolations(validDto));

    validDto.setZipcode("asdfd-asdf");
    assertFalse(noValidationViolations(validDto));

    // test valid zipcodes as well
    validDto.setZipcode("12345-1234");
    assertTrue(noValidationViolations(validDto));

    validDto.setZipcode("12345");
    assertTrue(noValidationViolations(validDto));
  }

  @Test
  void test_updateExisting_ReturnsBadRequestOnMissingId() {
    var response = controller.updateExisting(null, validDto);

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
  void test_updateExisting_Returns404StatusCodeOnNonExistentCustomer() {
    when(service.update(any(Customer.class))).thenThrow(NoSuchElementException.class);

    var response = controller.updateExisting(validCustomer.getId(), validDto);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  void test_updateExisting_Returns200StatusCodeOnSuccess() {
    when(service.update(any(Customer.class))).thenReturn(validCustomer);

    var response = controller.updateExisting(validCustomer.getId(), validDto);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(validCustomer, response.getBody());
  }

  @Test
  void test_delete_Returns204StatusCode() {
    var response = controller.delete(1L);

    assertEquals(204, response.getStatusCodeValue());
    assertNull(response.getBody());
  }
}