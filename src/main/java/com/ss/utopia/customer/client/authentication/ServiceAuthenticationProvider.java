package com.ss.utopia.customer.client.authentication;

public interface ServiceAuthenticationProvider {

  String getAuthorizationHeader();

  void refreshAuthorization();
}
