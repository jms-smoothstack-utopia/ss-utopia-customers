package com.ss.utopia.customer.exception;

import org.springframework.dao.DuplicateKeyException;

public class DuplicateEmailException extends DuplicateKeyException {

  private final String email;

  public DuplicateEmailException(String email) {
    super("A customer account with that email already exists.");
    this.email = email;
  }

  public String getEmail() {
    return email;
  }
}
