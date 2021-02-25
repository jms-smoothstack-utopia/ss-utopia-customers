package com.ss.utopia.customer.exception;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * To be thrown when a {@link com.ss.utopia.customer.entity.Customer} cannot be found.
 * <p>
 * Contains the offending ID that can be retrieved with {@link #getCustomerId()}.
 */
public class NoSuchCustomerException extends NoSuchElementException {

  private final UUID customerId;

  public NoSuchCustomerException(UUID id) {
    super("No customer record found for id=" + id);
    this.customerId = id;
  }

  public UUID getCustomerId() {
    return customerId;
  }
}
