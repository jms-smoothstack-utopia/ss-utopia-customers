package com.ss.utopia.customer.exception;

import java.util.UUID;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

public class DeleteAccountFailureException extends RuntimeException {

  @Getter
  private final transient ResponseEntity<UUID> responseEntity;
  @Getter
  private final UUID confirmationToken;

  public DeleteAccountFailureException(ResponseEntity<UUID> resp,
                                       UUID confirmationToken) {
    super("STATUS: " + resp.getStatusCodeValue()
              + " - Unable to confirm deletion with token=" + confirmationToken);
    this.responseEntity = resp;
    this.confirmationToken = confirmationToken;
  }
}
