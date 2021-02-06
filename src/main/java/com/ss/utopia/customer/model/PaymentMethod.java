package com.ss.utopia.customer.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;

/**
 * TODO: Verify payment method storage information needed.
 * TODO: Figure out relational mapping.
 */
@Data
@Entity
public class PaymentMethod {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String accountNum;

  private String notes;
}
