package com.ss.utopia.customer.client.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequest {

  private String email;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private String password;

}
