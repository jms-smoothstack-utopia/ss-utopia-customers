package com.ss.utopia.customer.exception;

import java.util.NoSuchElementException;

public class NoSuchPaymentMethod extends NoSuchElementException {

  public NoSuchPaymentMethod(Long customerId, Long paymentId) {
    super("Could not locate payment method with ID '"
              + paymentId + "' for customer ID '" + customerId + "'");
  }
}
