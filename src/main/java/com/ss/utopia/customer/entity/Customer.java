package com.ss.utopia.customer.entity;

import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

  @Id
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  private String firstName;

  private String lastName;

  @Builder.Default
  private Integer loyaltyPoints = 0;

  private String phoneNumber;

  @Column(unique = true)
  private String email;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private Set<Address> addresses;
  
  @Builder.Default
  private Boolean ticketEmails = true;
  
  @Builder.Default
  private Boolean flightEmails = true;

  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @OneToMany(cascade = CascadeType.ALL)
  private Set<PaymentMethod> paymentMethods;
}
