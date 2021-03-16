package com.ss.utopia.customer.controller;

import com.ss.utopia.customer.dto.CreateCustomerDto;
import com.ss.utopia.customer.dto.DeleteAccountDto;
import com.ss.utopia.customer.dto.PaymentMethodDto;
import com.ss.utopia.customer.dto.UpdateCustomerDto;
import com.ss.utopia.customer.dto.UpdateCustomerLoyaltyDto;
import com.ss.utopia.customer.entity.Customer;
import com.ss.utopia.customer.entity.PaymentMethod;
import com.ss.utopia.customer.security.permissions.AdminOnlyPermission;
import com.ss.utopia.customer.security.permissions.CreateCustomerPermission;
import com.ss.utopia.customer.security.permissions.DeleteCustomerByIdPermission;
import com.ss.utopia.customer.security.permissions.GetCustomerByEmailPermission;
import com.ss.utopia.customer.security.permissions.GetCustomerByIdPermission;
import com.ss.utopia.customer.service.CustomerService;
import com.ss.utopia.customer.service.DeleteAccountService;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
  private final CustomerService customerService;
  private final DeleteAccountService deleteAccountService;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<List<Customer>> getAllCustomers() {
    log.info("GET Customer all");
    var customers = customerService.getAllCustomers();
    if (customers.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(customers);
  }

  @GetCustomerByIdPermission
  @GetMapping(value = "/{customerId}",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Customer> getCustomerById(@PathVariable UUID customerId) {
    log.info("GET Customer id=" + customerId);
    return ResponseEntity.of(Optional.ofNullable(customerService.getCustomerById(customerId)));
  }

  @GetCustomerByEmailPermission
  @GetMapping(value = "/email/{email}",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Customer> getCustomerByEmail(@PathVariable String email) {
    log.info("GET Customer email=" + email);
    return ResponseEntity.of(Optional.ofNullable(customerService.getCustomerByEmail(email)));
  }

  @GetCustomerByIdPermission
  @GetMapping(value = "/loyalty/{customerId}",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Integer> getCustomerLoyaltyPoints(@PathVariable UUID customerId) {
    log.info("GET Customer Loyalty Points when Customer id=" + customerId);
    return ResponseEntity.of(Optional.ofNullable(customerService
                                                  .getCustomerLoyaltyPoints(customerId)));
  }

  @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','TRAVEL_AGENT')")
  @PutMapping(value = "/loyalty/{customerId}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> updateCustomerLoyaltyPoints(@PathVariable UUID customerId,
                                                       @Valid @RequestBody
                                                       UpdateCustomerLoyaltyDto
                                                               customerLoyaltyDto) {
    log.info("PUT Update Customer loyalty points when Customer ID=" + customerId);
    customerService.updateCustomerLoyaltyPoints(customerId, customerLoyaltyDto);
    return ResponseEntity.ok().build();
  }

  @CreateCustomerPermission
  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Customer> createNewCustomer(@Valid @RequestBody
                                                              CreateCustomerDto customerDto) {
    log.info("POST Customer");
    var createdCustomer = customerService.createNewCustomer(customerDto);
    var uri = URI.create(MAPPING + "/" + createdCustomer.getId());
    return ResponseEntity.created(uri).body(createdCustomer);
  }

  @GetCustomerByIdPermission
  @PutMapping(value = "/{customerId}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> updateExistingCustomer(@PathVariable UUID customerId,
                                                  @Valid @RequestBody
                                                          UpdateCustomerDto updateCustomerDto) {
    log.info("PUT Customer id=" + customerId);
    customerService.updateCustomer(customerId, updateCustomerDto);
    return ResponseEntity.noContent().build();
  }

  @AdminOnlyPermission
  @DeleteMapping("/{customerId}")
  public ResponseEntity<String> deleteCustomer(@PathVariable UUID customerId) {
    log.info("DELETE id=" + customerId);
    customerService.removeCustomerById(customerId);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('ADMIN')"
      + " OR @customerAuthenticationManager.customerIdMatches(authentication, #deleteAccountDto)")
  @DeleteMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> initiateDeleteCustomer(@Valid @RequestBody
                                                            DeleteAccountDto deleteAccountDto) {
    log.info("Initiate delete id=" + deleteAccountDto.getId());
    deleteAccountService.requestDeletion(deleteAccountDto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(value = "/confirm/{confirmationToken}")
  public ResponseEntity<?> confirmDeleteCustomer(@PathVariable UUID confirmationToken) {
    log.info("Confirm delete token=" + confirmationToken);
    deleteAccountService.finalizeDeletion(confirmationToken);
    return ResponseEntity.noContent().build();
  }

  @GetCustomerByIdPermission
  @GetMapping(value = "/{customerId}/payment-method/{paymentId}",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<PaymentMethod> getPaymentMethod(@PathVariable UUID customerId,
                                                        @PathVariable Long paymentId) {
    log.info("GET PaymentMethod customerId=" + customerId + ", paymentId=" + paymentId);
    return ResponseEntity.of(Optional.of(customerService.getPaymentMethod(customerId, paymentId)));
  }

  @GetCustomerByIdPermission
  @PostMapping(value = "/{customerId}/payment-method",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> addPaymentMethod(@PathVariable UUID customerId,
                                            @Valid @RequestBody PaymentMethodDto paymentMethodDto) {
    log.info("POST PaymentMethod id=" + customerId);
    var paymentId = customerService.addPaymentMethod(customerId, paymentMethodDto);
    var uri = URI.create(MAPPING + "/" + customerId + "/payment-method/" + paymentId);
    return ResponseEntity.created(uri).build();
  }

  @GetCustomerByIdPermission
  @PutMapping(value = "/{customerId}/payment-method/{paymentId}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> updatePaymentMethod(@PathVariable UUID customerId,
                                               @PathVariable Long paymentId,
                                               @Valid @RequestBody
                                                         PaymentMethodDto paymentMethodDto) {
    log.info("PUT PaymentMethod customerId=" + customerId + ", paymentId=" + paymentId);
    customerService.updatePaymentMethod(customerId, paymentId, paymentMethodDto);
    return ResponseEntity.noContent().build();
  }

  @DeleteCustomerByIdPermission
  @DeleteMapping("/{customerId}/payment-method/{paymentId}")
  public ResponseEntity<?> removePaymentMethod(@PathVariable UUID customerId,
                                               @PathVariable Long paymentId) {
    log.info("DELETE PaymentMethod customerId=" + customerId + ", paymentId=" + paymentId);
    customerService.removePaymentMethod(customerId, paymentId);
    return ResponseEntity.noContent().build();
  }
}
