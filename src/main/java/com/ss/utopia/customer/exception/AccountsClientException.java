package com.ss.utopia.customer.exception;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public class AccountsClientException extends RuntimeException {

  private final transient ResponseEntity<UUID> response;

  public AccountsClientException(ResponseEntity<UUID> response) {
    super("UUID returned from AccountsClient was null. Response status="
              + response.getStatusCode().value());
    this.response = response;
  }

  public AccountsClientException(IOException ex) {
    super(ex);
    this.response = null;
  }

  public Optional<ResponseEntity<UUID>> getResponse() {
    return Optional.ofNullable(response);
  }
}
