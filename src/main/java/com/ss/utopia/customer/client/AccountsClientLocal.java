package com.ss.utopia.customer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile("local")
@FeignClient(name = "utopia-auth-service", url = "http://localhost:8089")
public interface AccountsClientLocal extends AccountsClient {

}
