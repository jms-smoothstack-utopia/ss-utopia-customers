package com.ss.utopia.orchestrator.model;

import lombok.Data;

@Data
public class PaymentMethod {

  private Long id;
  private String accountNum;
  private String notes;
}
