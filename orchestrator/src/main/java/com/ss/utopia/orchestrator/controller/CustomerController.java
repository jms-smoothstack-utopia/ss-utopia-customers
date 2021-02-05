package com.ss.utopia.orchestrator.controller;

import com.ss.utopia.orchestrator.client.CustomerClient;
import com.ss.utopia.orchestrator.dto.CustomerDto;
import com.ss.utopia.orchestrator.model.Customer;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/customer")
public class CustomerController {


  private final CustomerClient client;

  public CustomerController(CustomerClient client) {
    this.client = client;
  }

  @GetMapping
  public ResponseEntity<Customer[]> getAll() {
    return client.getAllCustomers();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
    return client.getCustomerById(id);
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Long> createNewCustomer(@Valid @RequestBody CustomerDto customerDto) {
    return client.createNewCustomer(customerDto);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateExisting(@PathVariable Long id,
                                          @Valid @RequestBody CustomerDto customerDto) {
    client.updateExisting(id, customerDto);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    client.deleteCustomer(id);
    return ResponseEntity.noContent().build();
  }

  @ExceptionHandler(value = HttpClientErrorException.class)
  private ResponseEntity<?> exception(HttpClientErrorException ex) {
    return ResponseEntity.status(ex.getStatusCode())
        .headers(ex.getResponseHeaders())
        .body(ex.getResponseBodyAsByteArray());
  }
}
