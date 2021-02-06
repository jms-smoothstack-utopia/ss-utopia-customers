package com.ss.utopia.orchestrator.model;

import lombok.Data;

@Data
public class Ticket {

  private Long id;
  private Long flightId;
  private Long customerId;
  private String passenger;
  private boolean isCheckedIn;
}
