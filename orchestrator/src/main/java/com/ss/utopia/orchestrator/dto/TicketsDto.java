package com.ss.utopia.orchestrator.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TicketsDto {

  @NotBlank(message = "Flight Number is required")
  private Long flightId;

  @NotBlank(message = "Customer ID is required")
  private Long customerId;

  @NotBlank(message = "Passenger name is required")
  private String passenger;

  @NotBlank(message = "Is the passenger check-in True or False?")
  private boolean isCheckedIn;

}
