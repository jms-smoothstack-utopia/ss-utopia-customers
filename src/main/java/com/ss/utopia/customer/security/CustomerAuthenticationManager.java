package com.ss.utopia.customer.security;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class CustomerAuthenticationManager {

  public boolean customerEmailMatches(Authentication authentication, String email) {
    try {
      var principal = (JwtPrincipal) authentication.getPrincipal();
      return principal.getEmail().equals(email);
    } catch (ClassCastException ex) {
      return false;
    }
  }

  public boolean customerIdMatches(Authentication authentication, UUID id) {
    try  {
      var principal = (JwtPrincipal) authentication.getPrincipal();
      return  principal.getUserId().equals(id);
    } catch (ClassCastException ex)  {
      return false;
    }
  }
}
