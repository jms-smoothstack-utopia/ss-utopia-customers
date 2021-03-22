package com.ss.utopia.customer.exception;

import java.util.UUID;
import lombok.Getter;

public class IllegalPointChangeException extends IllegalStateException {

  @Getter
  private final UUID customerId;
  @Getter
  private final Integer currentPoints;
  @Getter
  private final Integer attemptedDelta;

  public IllegalPointChangeException(UUID customerId,
                                     Integer currentPoints,
                                     Integer attemptedDelta) {
    super("Couldn't change points of customer " + customerId + " by " + attemptedDelta
              + "! Customer has " + currentPoints + ".");
    this.customerId = customerId;
    this.currentPoints = currentPoints;
    this.attemptedDelta = attemptedDelta;
  }
}
