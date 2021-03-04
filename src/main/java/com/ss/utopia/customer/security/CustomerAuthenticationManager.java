package com.ss.utopia.customer.security;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomerAuthenticationManager {

  public boolean customerEmailMatches(Authentication authentication, String email) {
    var authedEmail = (String) authentication.getPrincipal();
    return authedEmail.equals(email);
  }

  public boolean customerIdMatches(Authentication authentication, UUID id) {
    var jwtOwnerId = UUID.fromString((String)authentication.getDetails());
    return jwtOwnerId.equals(id);
  }
}
