package com.ss.utopia.customer.exception;

import org.springframework.dao.DuplicateKeyException;

/**
 * Exception to be thrown if creating or updating a {@link com.ss.utopia.customer.model.Customer}
 * and the email given already exists for another record.
 * <p>
 * Contains the offending email that can be retrieved with {@link #getEmail()}.
 */
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
