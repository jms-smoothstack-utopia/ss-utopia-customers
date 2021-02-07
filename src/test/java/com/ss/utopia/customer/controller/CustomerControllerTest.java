package com.ss.utopia.customer.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.utopia.customer.dto.CustomerDto;
import com.ss.utopia.customer.exception.ControllerAdvisor;
import com.ss.utopia.customer.exception.NoSuchCustomerException;
import com.ss.utopia.customer.model.Address;
import com.ss.utopia.customer.model.Customer;
import com.ss.utopia.customer.service.CustomerService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@Profile("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CustomerControllerTest {

  private final CustomerService service = Mockito.mock(CustomerService.class);
  private final CustomerController controller = new CustomerController(service);
  private final ObjectMapper jsonMapper = new ObjectMapper();
  private final MockMvc mvc = MockMvcBuilders.standaloneSetup(controller)
      .setControllerAdvice(new ControllerAdvisor())
      .build();

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
  void test_getAll_ReturnsListWith200StatusCode() throws Exception {
    when(service.getAll()).thenReturn(List.of(validCustomer));

    var result = mvc
        .perform(get("/customer"))
        .andExpect(status().is(200))
        .andReturn();

    var response = Arrays
        .stream(jsonMapper.readValue(result.getResponse().getContentAsString(), Customer[].class))
        .collect(Collectors.toList());

    assertEquals(List.of(validCustomer), response);
  }

  @Test
  void test_getAll_ReturnsEmptyListWith204StatusCodeIfNoCustomers() throws Exception {
    when(service.getAll()).thenReturn(Collections.emptyList());

    mvc
        .perform(
            get("/customer"))
        .andExpect(status().is(204));
  }

  @Test
  void test_getById_ReturnsValidCustomerWith200StatusCode() throws Exception {
    when(service.getById(validCustomer.getId())).thenReturn(validCustomer);

    var result = mvc
        .perform(
            get("/customer/" + validCustomer.getId()))
        .andExpect(status().is(200))
        .andReturn();

    var response = jsonMapper
        .readValue(result.getResponse().getContentAsString(), Customer.class);

    assertEquals(validCustomer, response);
  }

  @Test
  void test_getById_Returns404OnInvalidId() throws Exception {
    when(service.getById(-1L)).thenThrow(new NoSuchCustomerException(-1L));

    mvc
        .perform(
            get("/customer/-1"))
        .andExpect(status().is(404));
  }

  @Test
  void test_createNew_ReturnsCreatedIdAnd201StatusCodeOnValidDto() throws Exception {
    when(service.create(validDto)).thenReturn(validCustomer);

    var headerName = "Location";
    var headerVal = "/customer/" + validCustomer.getId();

    mvc
        .perform(
            post("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(validDto)))
        .andExpect(status().is(201))
        .andExpect(header().string(headerName, headerVal));
  }

  //util
  boolean noValidationViolations(CustomerDto customerDto) {
    return Validation.buildDefaultValidatorFactory()
        .getValidator()
        .validate(customerDto)
        .isEmpty();
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
  void test_updateExisting_Returns405OnMissingId() throws Exception {
    mvc
        .perform(
            put("/customer/"))
        .andExpect(status().is(405));
  }

  @Test
  void test_updateExisting_Returns404OnNonExistentCustomer() throws Exception {
    when(service.update(anyLong(), any(CustomerDto.class)))
        .thenThrow(new NoSuchCustomerException(-1L));

    mvc
        .perform(
            put("/customer/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(validDto)))
        .andExpect(status().is(404));
  }

  @Test
  void test_updateExisting_Returns200StatusCodeOnSuccess() throws Exception {
    when(service.update(anyLong(), any(CustomerDto.class))).thenReturn(validCustomer);

    mvc
        .perform(
            put("/customer/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(validDto)))
        .andExpect(status().is(204));
  }

  @Test
  void test_delete_Returns204StatusCode() throws Exception {
    mvc
        .perform(
            delete("/customer/1"))
        .andExpect(status().is(204));
  }
}