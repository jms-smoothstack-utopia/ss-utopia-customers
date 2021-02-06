package com.ss.utopia.customer.exception;

import java.util.NoSuchElementException;

public class NoSuchCustomerException extends NoSuchElementException {

  public NoSuchCustomerException(Long id) {
    super("Customer with ID '" + id + "' could not be found.");
  }
}
