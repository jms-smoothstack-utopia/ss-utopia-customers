package com.ss.utopia.orchestrator.controller;

import com.ss.utopia.orchestrator.client.CustomerClient;
import com.ss.utopia.orchestrator.dto.CustomerDto;
import javax.validation.Valid;
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
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/customer")
public class CustomerController {


  private final CustomerClient client;

  public CustomerController(CustomerClient client) {
    this.client = client;
  }

  @GetMapping
  public ResponseEntity<?> getAll() {
    return client.getAllCustomers();
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
    try {
      return client.getCustomerById(id);
    } catch (HttpClientErrorException ex) {
      return handleException(ex);
    }
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> createNewCustomer(@Valid @RequestBody CustomerDto customerDto) {
    try {

      return client.createNewCustomer(customerDto);
    } catch (HttpClientErrorException ex) {
      return handleException(ex);
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateExisting(@PathVariable Long id,
                                          @Valid @RequestBody CustomerDto customerDto) {
    try {
      client.updateExisting(id, customerDto);
      return ResponseEntity.ok().build();
    } catch (HttpClientErrorException ex) {
      return handleException(ex);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    try {
      client.deleteCustomer(id);
      return ResponseEntity.noContent().build();
    } catch (HttpClientErrorException ex) {
      return handleException(ex);
    }
  }

  private ResponseEntity<?> handleException(HttpClientErrorException ex) {
    //todo this likely can be replaced with an ExceptionHandler somehow.
    return ResponseEntity.status(ex.getStatusCode())
        .headers(ex.getResponseHeaders())
        .body(ex.getResponseBodyAsByteArray());
  }
}
