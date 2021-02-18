package com.ss.utopia.customer.controller;

import com.ss.utopia.customer.entity.Customer;
import com.ss.utopia.customer.entity.PaymentMethod;
import com.ss.utopia.customer.service.CustomerService;
import com.ss.utopia.customer.dto.CustomerDto;
import com.ss.utopia.customer.dto.PaymentMethodDto;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerController {

  private static final String MAPPING = "/customers";

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);
  private final CustomerService service;

  public CustomerController(CustomerService customerService) {
    this.service = customerService;
  }

  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<List<Customer>> getAllCustomers() {
    LOGGER.info("GET Customer all");
    var customers = service.getAllCustomers();
    if (customers.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(customers);
  }

  @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
    LOGGER.info("GET Customer id=" + id);
    return ResponseEntity.of(Optional.ofNullable(service.getCustomerById(id)));
  }

  @GetMapping(value = "/loyalty/{id}", produces = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Integer> getCustomerLoyaltyPoints(@PathVariable Long id) {
    LOGGER.info("GET Customer Loyalty Points when Customer id=" + id);
    return ResponseEntity.of(Optional.ofNullable(service.getCustomerLoyaltyPoints(id)));
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Customer> createNewCustomer(@Valid @RequestBody CustomerDto customerDto) {
    LOGGER.info("POST Customer");
    var createdCustomer = service.createNewCustomer(customerDto);
    var uri = URI.create(MAPPING + "/" + createdCustomer.getId());
    return ResponseEntity.created(uri).body(createdCustomer);
  }

  //todo DTO should be updated to allow multiple addresses (or a new one created).
  // Additionally, any field not present should not cause an error and should instead just not be modified.
  @PutMapping(value = "/{id}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> updateExistingCustomer(@PathVariable Long id,
                                                  @Valid @RequestBody CustomerDto customerDto) {
    LOGGER.info("PUT Customer id=" + id);
    service.updateCustomer(id, customerDto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
    LOGGER.info("DELETE id=" + id);
    service.removeCustomerById(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping(value = "/{customerId}/payment-method/{paymentId}",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<PaymentMethod> getPaymentMethod(@PathVariable Long customerId,
                                                        @PathVariable Long paymentId) {
    LOGGER.info("GET PaymentMethod customerId=" + customerId + ", paymentId=" + paymentId);
    return ResponseEntity.of(Optional.of(service.getPaymentMethod(customerId, paymentId)));
  }

  @PostMapping(value = "/{id}/payment-method",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> addPaymentMethod(@PathVariable Long id,
                                              @Valid @RequestBody PaymentMethodDto paymentMethodDto) {
    LOGGER.info("POST PaymentMethod id=" + id);
    var paymentId = service.addPaymentMethod(id, paymentMethodDto);
    var uri = URI.create(MAPPING + "/" + id + "/payment-method/" + paymentId);
    return ResponseEntity.created(uri).build();
  }

  @PutMapping(value = "/{customerId}/payment-method/{paymentId}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> updatePaymentMethod(@PathVariable Long customerId,
                                               @PathVariable Long paymentId,
                                               @Valid @RequestBody PaymentMethodDto paymentMethodDto) {
    LOGGER.info("PUT PaymentMethod customerId=" + customerId + ", paymentId=" + paymentId);
    service.updatePaymentMethod(customerId, paymentId, paymentMethodDto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{customerId}/payment-method/{paymentId}")
  public ResponseEntity<?> removePaymentMethod(@PathVariable Long customerId,
                                               @PathVariable Long paymentId) {
    LOGGER.info("DELETE PaymentMethod customerId=" + customerId + ", paymentId=" + paymentId);
    service.removePaymentMethod(customerId, paymentId);
    return ResponseEntity.noContent().build();
  }
}
