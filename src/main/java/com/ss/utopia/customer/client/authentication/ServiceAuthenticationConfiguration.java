package com.ss.utopia.customer.client.authentication;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "com.ss.utopia.customer.client.authentication")
public class ServiceAuthenticationConfiguration {

  @Getter @Setter
  private String email;
  @Getter @Setter
  private String password;
}
