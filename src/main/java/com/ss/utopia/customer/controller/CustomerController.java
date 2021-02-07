package com.ss.utopia.customer.controller;

import com.ss.utopia.customer.dto.CustomerDto;
import com.ss.utopia.customer.dto.PaymentMethodDto;
import com.ss.utopia.customer.model.Customer;
import com.ss.utopia.customer.model.PaymentMethod;
import com.ss.utopia.customer.service.CustomerService;
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
@RequestMapping("/customer")
public class CustomerController {

  //todo add logging
  private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
  private final CustomerService service;

  public CustomerController(CustomerService customerService) {
    this.service = customerService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Customer>> getAll() {
    var customers = service.getAll();
    if (customers.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(customers);
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Customer> getById(@PathVariable Long id) {
    return ResponseEntity.of(Optional.ofNullable(service.getById(id)));
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<URI> createNew(@Valid @RequestBody CustomerDto customerDto) {
    var createdCustomer = service.create(customerDto);
    var uri = URI.create("/customer/" + createdCustomer.getId());
    return ResponseEntity.created(uri).build();
  }

  //todo DTO should be updated to allow multiple addresses (or a new one created).
  // Additionally, any field not present should not cause an error and should instead just not be modified.
  @PutMapping(value = "/{id}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> updateExisting(@PathVariable Long id,
                                          @Valid @RequestBody CustomerDto customerDto) {
    service.update(id, customerDto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> delete(@PathVariable Long id) {
    service.removeById(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping(value = "/{customerId}/payment-method/{paymentId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PaymentMethod> getPaymentMethod(@PathVariable Long customerId,
                                                        @PathVariable Long paymentId) {
    return ResponseEntity.of(Optional.of(service.getPaymentMethod(customerId, paymentId)));
  }

  @PostMapping(value = "/{id}/payment-method",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<URI> addPaymentMethod(@PathVariable Long id,
                                              @Valid @RequestBody PaymentMethodDto paymentMethodDto) {
    var paymentId = service.addPaymentMethod(id, paymentMethodDto);
    var uri = URI.create("/" + id + "/payment-method/" + paymentId);
    return ResponseEntity.created(uri).build();
  }

  @PutMapping(value = "/{customerId}/payment-method/{paymentId}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> updatePaymentMethod(@PathVariable Long customerId,
                                               @PathVariable Long paymentId,
                                               @Valid @RequestBody PaymentMethodDto paymentMethodDto) {
    service.updatePaymentMethod(customerId, paymentId, paymentMethodDto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{customerId}/payment-method/{paymentId}")
  public ResponseEntity<?> removePaymentMethod(@PathVariable Long customerId,
                                               @PathVariable Long paymentId) {
    service.removePaymentMethod(customerId, paymentId);
    return ResponseEntity.noContent().build();
  }
}
