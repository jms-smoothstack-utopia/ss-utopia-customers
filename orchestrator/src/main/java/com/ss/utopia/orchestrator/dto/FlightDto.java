package com.ss.utopia.orchestrator.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FlightDto {

  @NotNull(message = "Origin airport number is required")
  private Long originId;

  @NotNull(message = "Destination airport number is required")
  private Long destinationId;

  @NotNull(message = "Airplane number is required")
  private Long airplaneId;

  @NotNull(message = "Number of filled seats is required")
  private Integer filledSeats;
}
