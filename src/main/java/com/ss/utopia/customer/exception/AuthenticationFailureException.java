package com.ss.utopia.customer.exception;

import java.util.Optional;
import org.springframework.http.ResponseEntity;

public class AuthenticationFailureException extends RuntimeException {

  private final transient ResponseEntity<String> response;

  public AuthenticationFailureException(String msg) {
    super(msg);
    this.response = null;
  }

  public AuthenticationFailureException(ResponseEntity<String> response) {
    super("Failed to authenticate with service account. Status code: "
              + response.getStatusCodeValue());
    this.response = response;
  }

  public Optional<ResponseEntity<String>> getResponse() {
    return Optional.ofNullable(response);
  }
}
