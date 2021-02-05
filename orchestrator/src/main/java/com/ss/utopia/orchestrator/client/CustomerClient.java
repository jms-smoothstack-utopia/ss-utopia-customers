package com.ss.utopia.orchestrator.client;

import com.ss.utopia.orchestrator.model.Customer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConfigurationProperties(prefix = "ss.utopia.customer", ignoreUnknownFields = false)
public class CustomerClient {

  private final String PATH_V1 = "/customer/";
  private String apiHost;

  private final RestTemplate restTemplate;

  public CustomerClient(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  public Customer getCustomerById(Long id) {
    var uri = apiHost + PATH_V1 + id;
    return restTemplate.getForObject(uri, Customer.class);
  }

  public void setApiHost(String apiHost) {
    this.apiHost = apiHost;
  }
}
