package com.ss.utopia.customer.controller;

import com.ss.utopia.customer.dto.CreateCustomerDto;
import com.ss.utopia.customer.dto.PaymentMethodDto;
import com.ss.utopia.customer.dto.UpdateCustomerDto;
import com.ss.utopia.customer.entity.Customer;
import com.ss.utopia.customer.entity.PaymentMethod;
import com.ss.utopia.customer.service.CustomerService;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping(EndpointConstants.API_V_0_1_CUSTOMERS)
@RequiredArgsConstructor
public class CustomerController {

  private static final String MAPPING = EndpointConstants.API_V_0_1_CUSTOMERS;
  private final CustomerService service;

  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<List<Customer>> getAllCustomers() {
    log.info("GET Customer all");
    var customers = service.getAllCustomers();
    if (customers.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(customers);
  }

  @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Customer> getCustomerById(@PathVariable UUID id) {
    log.info("GET Customer id=" + id);
    return ResponseEntity.of(Optional.ofNullable(service.getCustomerById(id)));
  }

  @GetMapping(value = "/loyalty/{id}", produces = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Integer> getCustomerLoyaltyPoints(@PathVariable UUID id) {
    log.info("GET Customer Loyalty Points when Customer id=" + id);
    return ResponseEntity.of(Optional.ofNullable(service.getCustomerLoyaltyPoints(id)));
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Customer> createNewCustomer(@Valid @RequestBody CreateCustomerDto customerDto) {
    log.info("POST Customer");
    var createdCustomer = service.createNewCustomer(customerDto);
    var uri = URI.create(MAPPING + "/" + createdCustomer.getId());
    return ResponseEntity.created(uri).body(createdCustomer);
  }

  //todo DTO should be updated to allow multiple addresses (or a new one created).
  // Additionally, any field not present should not cause an error and should instead just not be modified.
  @PutMapping(value = "/{id}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> updateExistingCustomer(@PathVariable UUID id,
                                                  @Valid @RequestBody UpdateCustomerDto updateCustomerDto) {
    log.info("PUT Customer id=" + id);
    service.updateCustomer(id, updateCustomerDto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteCustomer(@PathVariable UUID id) {
    log.info("DELETE id=" + id);
    service.removeCustomerById(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping(value = "/{customerId}/payment-method/{paymentId}",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<PaymentMethod> getPaymentMethod(@PathVariable UUID customerId,
                                                        @PathVariable Long paymentId) {
    log.info("GET PaymentMethod customerId=" + customerId + ", paymentId=" + paymentId);
    return ResponseEntity.of(Optional.of(service.getPaymentMethod(customerId, paymentId)));
  }

  @PostMapping(value = "/{id}/payment-method",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> addPaymentMethod(@PathVariable UUID id,
                                            @Valid @RequestBody PaymentMethodDto paymentMethodDto) {
    log.info("POST PaymentMethod id=" + id);
    var paymentId = service.addPaymentMethod(id, paymentMethodDto);
    var uri = URI.create(MAPPING + "/" + id + "/payment-method/" + paymentId);
    return ResponseEntity.created(uri).build();
  }

  @PutMapping(value = "/{customerId}/payment-method/{paymentId}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> updatePaymentMethod(@PathVariable UUID customerId,
                                               @PathVariable Long paymentId,
                                               @Valid @RequestBody PaymentMethodDto paymentMethodDto) {
    log.info("PUT PaymentMethod customerId=" + customerId + ", paymentId=" + paymentId);
    service.updatePaymentMethod(customerId, paymentId, paymentMethodDto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{customerId}/payment-method/{paymentId}")
  public ResponseEntity<?> removePaymentMethod(@PathVariable UUID customerId,
                                               @PathVariable Long paymentId) {
    log.info("DELETE PaymentMethod customerId=" + customerId + ", paymentId=" + paymentId);
    service.removePaymentMethod(customerId, paymentId);
    return ResponseEntity.noContent().build();
  }
}
