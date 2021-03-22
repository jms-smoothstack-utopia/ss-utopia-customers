package com.ss.utopia.customer.exception;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Exception to be thrown when a {@link com.ss.utopia.customer.entity.PaymentMethod} cannot be
 * located or does not belong to the given {@link com.ss.utopia.customer.entity.Customer}.
 *
 * <p>Offending IDs can be retrieved with {@link #getCustomerId()} and {@link #getPaymentId()}.
 */
public class NoSuchPaymentMethod extends NoSuchElementException {

  private final UUID customerId;
  private final Long paymentId;

  public NoSuchPaymentMethod(UUID customerId, Long paymentId) {
    super("Could not locate payment method with ID '"
              + paymentId + "' for customer ID '" + customerId + "'");
    this.customerId = customerId;
    this.paymentId = paymentId;
  }

  public UUID getCustomerId() {
    return customerId;
  }

  public Long getPaymentId() {
    return paymentId;
  }
}
