package com.ss.utopia.customer.client;

import com.ss.utopia.customer.dto.CreateUserAccountDto;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("utopia-auth-service")
public interface AccountsClient {

  @PostMapping("/api/v0.1/accounts")
  ResponseEntity<UUID> createNewAccount(@RequestBody CreateUserAccountDto dto);
}
