package com.ss.utopia.customer.service;

import com.ss.utopia.customer.dto.DeleteAccountDto;
import java.util.UUID;

public interface DeleteAccountService {

  void requestDeletion(DeleteAccountDto deleteAccountDto);

  void finalizeDeletion(UUID confirmationToken);
}
