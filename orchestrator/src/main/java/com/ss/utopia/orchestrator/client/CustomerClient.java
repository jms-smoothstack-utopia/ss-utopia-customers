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

  private final RestTemplate restTemplate;
  private String apiHost;
  private final String endpoint = "/customer/";

  public void setApiHost(String apiHost) {
    this.apiHost = apiHost;
  }

  public CustomerClient(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  public ResponseEntity<Customer[]> getAllCustomers() {
    var url = apiHost + endpoint;
    return restTemplate.getForEntity(url, Customer[].class);
  }

  public ResponseEntity<Customer> getCustomerById(Long id) {
    var url = apiHost + endpoint + id;
    return restTemplate.getForEntity(url, Customer.class);
  }

  public ResponseEntity<Long> createNewCustomer(CustomerDto customerDto) {
    var url = apiHost + endpoint;
    return restTemplate.postForEntity(url, customerDto, Long.class);
  }

  public void updateExisting(Long id, CustomerDto customerDto) {
    var url = apiHost + endpoint + id;
    restTemplate.put(url, customerDto);
  }

  public void deleteCustomer(Long id) {
    var url = apiHost + endpoint + id;
    restTemplate.delete(url);
  }
}
