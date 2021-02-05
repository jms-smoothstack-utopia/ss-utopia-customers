package com.ss.utopia.customer.model;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String firstName;

  private String lastName;

  private String email;

  @OneToMany(cascade = CascadeType.ALL)
  private Set<Address> addresses;

  @OneToMany(cascade = CascadeType.ALL)
  private Set<PaymentMethod> paymentMethods;
}
