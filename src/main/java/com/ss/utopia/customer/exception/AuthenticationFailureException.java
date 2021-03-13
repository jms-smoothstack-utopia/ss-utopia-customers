package com.ss.utopia.customer.exception;

import java.util.Optional;
import org.springframework.http.ResponseEntity;

public class AuthenticationFailureException extends RuntimeException {

  private final ResponseEntity<?> response;

  public AuthenticationFailureException(String msg) {
    super(msg);
    this.response = null;
  }

  public AuthenticationFailureException(ResponseEntity<?> response) {
    super("Failed to authenticate with service account. Status code: "
              + response.getStatusCodeValue());
    this.response = response;
  }

  public Optional<ResponseEntity<?>> getResponse() {
    return Optional.ofNullable(response);
  }
}
