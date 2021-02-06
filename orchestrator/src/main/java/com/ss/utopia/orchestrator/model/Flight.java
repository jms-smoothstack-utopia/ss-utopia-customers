package com.ss.utopia.orchestrator.model;

import lombok.Data;

@Data
public class Flight {

  private Long id;
  private Airport origin;
  private Airport destination;
  private Airplane airplane;
  private Integer filledSeats;
}
