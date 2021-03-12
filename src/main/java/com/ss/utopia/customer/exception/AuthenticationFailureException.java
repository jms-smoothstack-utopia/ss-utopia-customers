package com.ss.utopia.customer.exception;

import lombok.Getter;
import org.springframework.http.ResponseEntity;

public class AuthenticationFailureException extends RuntimeException {

  @Getter
  private final ResponseEntity<?> response;

  public AuthenticationFailureException(ResponseEntity<?> response) {
    super("Failed to authenticate with service account. Status code: "
              + response.getStatusCodeValue());
    this.response = response;
  }
}
