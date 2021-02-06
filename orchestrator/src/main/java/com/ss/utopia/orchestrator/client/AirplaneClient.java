package com.ss.utopia.orchestrator.client;

import com.ss.utopia.orchestrator.dto.AirplaneDto;
import com.ss.utopia.orchestrator.model.Airplane;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConfigurationProperties(prefix = "ss.utopia.airplane", ignoreUnknownFields = false)
public class AirplaneClient {

  private final RestTemplate restTemplate;
  private final String endpoint = "/airplane/";
  private String apiHost;

  public AirplaneClient(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  public void setApiHost(String apiHost) {
    this.apiHost = apiHost;
  }

  public ResponseEntity<Airplane> getAirplaneById(Long id) {
    var url = apiHost + endpoint + id;
    return restTemplate.getForEntity(url, Airplane.class);
  }

  public ResponseEntity<Airplane[]> getAllAirplanes() {
    var url = apiHost + endpoint;
    return restTemplate.getForEntity(url, Airplane[].class);
  }

  public ResponseEntity<Long> createAirplane(AirplaneDto airplaneDto) {
    var url = apiHost + endpoint;
    return restTemplate.postForEntity(url, airplaneDto, Long.class);
  }


  public void updateAirplane(Long id, AirplaneDto airplaneDto) {
    var url = apiHost + endpoint + id;
    restTemplate.put(url, airplaneDto);
  }

  public void deleteAirplane(Long id) {
    var url = apiHost + endpoint + id;
    restTemplate.delete(url);
  }
}
