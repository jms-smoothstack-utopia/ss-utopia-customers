package com.ss.utopia.orchestrator.client;

import com.ss.utopia.orchestrator.dto.CustomerDto;
import com.ss.utopia.orchestrator.model.Customer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConfigurationProperties(prefix = "ss.utopia.customer", ignoreUnknownFields = false)
public class CustomerClient {

  private final String PATH_V1 = "/customer/";
  private final RestTemplate restTemplate;
  private String apiHost;

  public CustomerClient(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  public ResponseEntity<Customer[]> getAllCustomers() {
    var url = apiHost + PATH_V1;
    return restTemplate.getForEntity(url, Customer[].class);
  }

  public ResponseEntity<Customer> getCustomerById(Long id) {
    var url = apiHost + PATH_V1 + id;
    return restTemplate.getForEntity(url, Customer.class);
  }

  public ResponseEntity<Long> createNewCustomer(CustomerDto customerDto) {
    var url = apiHost + PATH_V1;
    return restTemplate.postForEntity(url, customerDto, Long.class);
  }

  public void setApiHost(String apiHost) {
    this.apiHost = apiHost;
  }

  public void updateExisting(Long id, CustomerDto customerDto) {
    var url = apiHost + PATH_V1 + id;
    restTemplate.put(url, customerDto);
  }

  public void deleteCustomer(Long id) {
    var url = apiHost + PATH_V1 + id;
    restTemplate.delete(url);
  }
}
