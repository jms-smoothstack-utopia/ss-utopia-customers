package com.ss.utopia.customer.controller;

import com.ss.utopia.customer.dto.CustomerDto;
import com.ss.utopia.customer.mapper.CustomerDtoMapper;
import com.ss.utopia.customer.model.Customer;
import com.ss.utopia.customer.service.CustomerService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
public class CustomerController {

  private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
  private final CustomerService service;

  public CustomerController(CustomerService customerService) {
    this.service = customerService;
  }

  @GetMapping
  public ResponseEntity<List<Customer>> getAll() {
    var customers = service.getAll();
    if (customers.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(customers);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Customer> getById(@PathVariable Long id) {
    return ResponseEntity.of(service.getById(id));
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Long> createNew(@Valid @RequestBody CustomerDto customerDto) {
    var customer = CustomerDtoMapper.map(customerDto);
    var createdCustomer = service.create(customer);
    return ResponseEntity.status(201).body(createdCustomer.getId());
  }

  //todo DTO should be updated to allow multiple addresses (or a new one created).
  // Additionally, any field not present should not cause an error and should instead just not be modified.
  @PutMapping("/{id}")
  public ResponseEntity<?> updateExisting(@PathVariable Long id,
                                          @Valid @RequestBody CustomerDto customerDto) {
    if (id == null) {
      return ResponseEntity.badRequest()
          .body(Map.of("message", "Customer ID is required for update."));
    }
    var customerTouUpdate = CustomerDtoMapper.map(customerDto);
    customerTouUpdate.setId(id);

    try {
      var updatedCustomer = service.update(customerTouUpdate);
      return ResponseEntity.ok(updatedCustomer);
    } catch (NoSuchElementException ex) {
      log.error(ex.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> delete(@PathVariable Long id) {
    service.removeById(id);
    return ResponseEntity.noContent().build();
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {

    Map<String, Object> response = new HashMap<>();
    response.put("error", "Invalid field(s) in request.");
    response.put("status", 400);

    var errors = ex.getBindingResult()
        .getAllErrors()
        .stream()
        .collect(
            Collectors.toMap(error -> ((FieldError) error).getField(),
                             error -> getErrorMessageOrDefault((FieldError) error)));

    response.put("message", errors);
    return response;
  }

  private String getErrorMessageOrDefault(FieldError error) {

    var logMsg = "Validation exception - Message: '";

    String errorMsg;
    if (error.getDefaultMessage() == null) {
      errorMsg = "Unknown validation failure";
    } else {
      errorMsg = error.getDefaultMessage();
    }

    logMsg += errorMsg + "' Field: " + error.getField()
        + " Rejected Value: " + error.getRejectedValue();

    log.debug(logMsg);

    return errorMsg;
  }
}
