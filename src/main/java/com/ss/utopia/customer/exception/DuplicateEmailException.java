package com.ss.utopia.customer.exception;

import org.springframework.dao.DuplicateKeyException;

public class DuplicateEmailException extends DuplicateKeyException {

  private final String email;

  public DuplicateEmailException(String email) {
    super("Duplicate Email: '" + email + "'");
    this.email = email;
  }

  public String getEmail() {
    return email;
  }
}
