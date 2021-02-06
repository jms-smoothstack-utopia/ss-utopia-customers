package com.ss.utopia.orchestrator.model;

import java.util.Set;
import lombok.Data;

@Data
public class Customer {

  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private Set<Address> addresses;
  private Set<PaymentMethod> paymentMethods;
}

