package com.ss.utopia.customer.client.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.utopia.customer.client.AccountsClient;
import com.ss.utopia.customer.exception.AuthenticationFailureException;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

public class ServiceAuthenticationProviderImplTests {

  AccountsClient accountsClient = Mockito.mock(AccountsClient.class);
  ObjectMapper objectMapper = new ObjectMapper();
  ServiceAuthenticationConfiguration serviceAuthenticationConfiguration =
      Mockito.mock(ServiceAuthenticationConfiguration.class);

  ServiceAuthenticationProvider serviceAuthenticationProvider;

  String mockAuthHeader = "Bearer abc.def.xyz";
  ZonedDateTime mockExpiration = ZonedDateTime.now().plusHours(24);

  AuthenticationResponse mockAuthenticationResponse = AuthenticationResponse.builder()
      .token(mockAuthHeader)
      .expiresAt(mockExpiration.toInstant().toEpochMilli())
      .build();

  @BeforeEach
  void beforeEach() throws Exception {
    Mockito.reset(accountsClient);
    var res = objectMapper.writeValueAsString(mockAuthenticationResponse);
    when(accountsClient.login(any())).thenReturn(ResponseEntity.ok(res));

    when(serviceAuthenticationConfiguration.getEmail()).thenReturn("test@test.com");
    when(serviceAuthenticationConfiguration.getPassword()).thenReturn("password");

    serviceAuthenticationProvider =
        new ServiceAuthenticationProviderImpl(accountsClient,
                                              objectMapper,
                                              serviceAuthenticationConfiguration);
  }

  @Test
  void test_getEmail_ShouldReturnEmail () {
    assertEquals("test@test.com", serviceAuthenticationConfiguration.getEmail());
  }

  @Test
  void test_getPassword_ShouldReturnPassword () {
    assertEquals("password", serviceAuthenticationConfiguration.getPassword());
  }

  @Test
  void test_getAuthorizationHeader_ShouldNotReturnNullOnFreshInstance() {
    var result = serviceAuthenticationProvider.getAuthorizationHeader();
    assertNotNull(result);
  }

  @Test
  void test_refreshAuthorization_ShouldNotRefreshIfTokenExpirationGreaterThanOneHour() {
    var result = serviceAuthenticationProvider.getAuthorizationHeader();
    serviceAuthenticationProvider.refreshAuthorization();
    var newResult = serviceAuthenticationProvider.getAuthorizationHeader();

    assertEquals(result, newResult);
  }

  @Test
  void test_refreshAuthorization_ShouldReturnNewTokenIfExpired() throws Exception {
    when(accountsClient.login(any()))
        .thenReturn(ResponseEntity.ok(
            objectMapper.writeValueAsString(
                AuthenticationResponse.builder()
                    .token("Bearer originalToken")
                    .expiresAt(ZonedDateTime.now().plusMinutes(2).toInstant().toEpochMilli())
                    .build())));

    var originalToken = serviceAuthenticationProvider.getAuthorizationHeader();
    assertNotNull(originalToken);
    when(accountsClient.login(any()))
        .thenReturn(ResponseEntity.ok(
            objectMapper.writeValueAsString(
                AuthenticationResponse.builder()
                    .token("Bearer newToken")
                    .expiresAt(ZonedDateTime.now().plusHours(24).toInstant().toEpochMilli())
                    .build())));

    var newToken = serviceAuthenticationProvider.getAuthorizationHeader();

    assertNotEquals(originalToken, newToken);
  }

  @Test
  void test_refreshAuthorization_ShouldThrowExceptionIfCredentialsEmpty() {
    when(serviceAuthenticationConfiguration.getEmail()).thenReturn(null);
    assertThrows(AuthenticationFailureException.class, () -> serviceAuthenticationProvider.refreshAuthorization());
    when(serviceAuthenticationConfiguration.getEmail()).thenReturn("");
    assertThrows(AuthenticationFailureException.class, () -> serviceAuthenticationProvider.refreshAuthorization());
    when(serviceAuthenticationConfiguration.getEmail()).thenReturn("        ");
    assertThrows(AuthenticationFailureException.class, () -> serviceAuthenticationProvider.refreshAuthorization());
    when(serviceAuthenticationConfiguration.getEmail()).thenReturn("test@test.com");

    when(serviceAuthenticationConfiguration.getPassword()).thenReturn(null);
    assertThrows(AuthenticationFailureException.class, () -> serviceAuthenticationProvider.refreshAuthorization());
    when(serviceAuthenticationConfiguration.getPassword()).thenReturn("");
    assertThrows(AuthenticationFailureException.class, () -> serviceAuthenticationProvider.refreshAuthorization());
    when(serviceAuthenticationConfiguration.getPassword()).thenReturn("            ");
    assertThrows(AuthenticationFailureException.class, () -> serviceAuthenticationProvider.refreshAuthorization());
  }
}
