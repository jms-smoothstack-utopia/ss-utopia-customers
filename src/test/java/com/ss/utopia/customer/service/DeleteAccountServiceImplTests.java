package com.ss.utopia.customer.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.ss.utopia.customer.client.AccountsClient;
import com.ss.utopia.customer.client.authentication.ServiceAuthenticationProvider;
import com.ss.utopia.customer.dto.DeleteAccountDto;
import com.ss.utopia.customer.exception.DeleteAccountFailureException;
import feign.FeignException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

public class DeleteAccountServiceImplTests {

  AccountsClient accountsClient = Mockito.mock(AccountsClient.class);
  CustomerService customerService = Mockito.mock(CustomerService.class);
  ServiceAuthenticationProvider serviceAuthenticationProvider =
      Mockito.mock(ServiceAuthenticationProvider.class);

  DeleteAccountService deleteAccountService;

  DeleteAccountDto mockDeleteAccountDto = DeleteAccountDto.builder()
      .id(UUID.randomUUID())
      .email("test@test.com")
      .password("abCD1234!@")
      .build();

  @BeforeEach
  void beforeEach() {
    Mockito.reset(accountsClient);
    Mockito.reset(customerService);
    Mockito.reset(serviceAuthenticationProvider);

    when(serviceAuthenticationProvider.getAuthorizationHeader())
        .thenReturn("Bearer abc.def.xyz");

    deleteAccountService =
        new DeleteAccountServiceImpl(accountsClient,
                                     customerService,
                                     serviceAuthenticationProvider);
  }

  @Test
  void test_requestDeletion_ShouldRefreshAuthorizationAndRetryOnFirstForbiddenFailure() {
    doThrow(FeignException.Forbidden.class)
        .when(accountsClient).initiateCustomerDeletion(any(), any());

    try {
      deleteAccountService.requestDeletion(mockDeleteAccountDto);
      fail("Did not throw exception after refreshing.");
    } catch (FeignException.Forbidden ex) {
      //
    }

    Mockito.verify(serviceAuthenticationProvider, times(1)).refreshAuthorization();
  }

  @Test
  void test_finalizeDeletion_ShouldRefreshAuthorizationAndRetryOnFirstForbiddenFailure() {
    doThrow(FeignException.Forbidden.class)
        .when(accountsClient).completeCustomerDeletion(any(), any());

    try {
      deleteAccountService.finalizeDeletion(UUID.randomUUID());
      fail("Did not throw exception after refreshing.");
    } catch (FeignException.Forbidden ex) {
      //
    }

    Mockito.verify(serviceAuthenticationProvider, times(1)).refreshAuthorization();
  }

  @Test
  void test_finalizeDeletion_ShouldRemoveCustomerRecordOnSuccessfulResponse() {
    var uuidResponse = UUID.randomUUID();
    when(accountsClient.completeCustomerDeletion(any(), any()))
        .thenReturn(ResponseEntity.ok(uuidResponse));

    deleteAccountService.finalizeDeletion(UUID.randomUUID());

    Mockito.verify(customerService, times(1)).removeCustomerById(uuidResponse);
  }

  @Test
  void test_finalizeDeletion_ShouldThrowExceptionIfResponseBodyIsNullFromAccountClient() {
    when(accountsClient.completeCustomerDeletion(any(), any()))
        .thenReturn(ResponseEntity.ok().build());

    assertThrows(DeleteAccountFailureException.class,
                 () -> deleteAccountService.finalizeDeletion(UUID.randomUUID()));
  }
}
