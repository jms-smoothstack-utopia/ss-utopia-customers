package com.ss.utopia.customer.service;

import com.ss.utopia.customer.client.AccountsClient;
import com.ss.utopia.customer.dto.DeleteAccountDto;
import com.ss.utopia.customer.exception.DeleteAccountFailureException;
import com.ss.utopia.customer.client.authentication.ServiceAuthenticationProvider;
import feign.FeignException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteAccountServiceImpl implements DeleteAccountService {

  private final AccountsClient accountsClient;
  private final CustomerService customerService;
  private final ServiceAuthenticationProvider serviceAuthenticationProvider;

  @Override
  public void requestDeletion(DeleteAccountDto deleteAccountDto) {
    Runnable fn = () -> accountsClient.initiateCustomerDeletion(serviceAuthenticationProvider
                                                                    .getAuthorizationHeader(),
                                                                deleteAccountDto);
    runWithRetry(fn);
  }

  @Override
  public void finalizeDeletion(UUID confirmationToken) {
    Runnable fn = () -> {
      var resp = accountsClient.completeCustomerDeletion(serviceAuthenticationProvider
                                                             .getAuthorizationHeader(),
                                                         confirmationToken);
      if (resp.getBody() != null) {
        customerService.removeCustomerById(resp.getBody());
      } else {
        log.error("Deletion failed on call to authentication service. Response contained no body.");
        throw new DeleteAccountFailureException(resp, confirmationToken);
      }
    };

    runWithRetry(fn);
  }

  private void runWithRetry(Runnable runnable) {
    try {
      runnable.run();
    } catch (FeignException.Forbidden ex) {
      serviceAuthenticationProvider.refreshAuthorization();
      runnable.run();
    }
  }
}
