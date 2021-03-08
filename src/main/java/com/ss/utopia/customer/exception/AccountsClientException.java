package com.ss.utopia.customer.exception;

import java.util.UUID;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

public class AccountsClientException extends RuntimeException {

  @Getter
  private final ResponseEntity<UUID> response;

  public AccountsClientException(ResponseEntity<UUID> response) {
    super("UUID returned from AccountsClient was null. Response status="
              + response.getStatusCode().value());
    this.response = response;
  }
}
