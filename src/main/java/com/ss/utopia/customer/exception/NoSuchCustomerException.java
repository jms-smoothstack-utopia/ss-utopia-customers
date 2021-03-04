package com.ss.utopia.customer.exception;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

/**
 * To be thrown when a {@link com.ss.utopia.customer.entity.Customer} cannot be found.
 * <p>
 * Contains the offending ID that can be retrieved with {@link #getCustomerId()}.
 */
public class NoSuchCustomerException extends NoSuchElementException {

  private final UUID customerId;
  private final String customerEmail;

  public NoSuchCustomerException(UUID id) {
    super("No customer record found for id=" + id);
    this.customerId = id;
    this.customerEmail = null;
  }

  public NoSuchCustomerException(String email) {
    super("No customer record found for email=" + email);
    this.customerEmail = email;
    this.customerId = null;
  }

  public Optional<UUID> getCustomerId() {
    return Optional.ofNullable(customerId);
  }

  public Optional<String> getCustomerEmail() {
    return Optional.ofNullable(customerEmail);
  }
}
