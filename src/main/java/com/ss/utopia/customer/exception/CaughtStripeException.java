package com.ss.utopia.customer.exception;

import com.stripe.exception.StripeException;

public class CaughtStripeException extends RuntimeException {

  private final String stripeCode;
  private final String stripeErrorType;

  public CaughtStripeException(StripeException ex) {
    super("Caught a Stripe exception: " + ex.getMessage());
    var error = ex.getStripeError();
    this.stripeCode = error.getCode();
    this.stripeErrorType = error.getType();
  }

  public String getStripeCode() {
    return stripeCode;
  }

  public String getStripeErrorType() {
    return stripeErrorType;
  }
}