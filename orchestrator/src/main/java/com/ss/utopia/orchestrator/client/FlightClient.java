package com.ss.utopia.orchestrator.client;

import com.ss.utopia.orchestrator.dto.FlightDto;
import com.ss.utopia.orchestrator.model.Flight;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConfigurationProperties(prefix = "ss.utopia.flight", ignoreUnknownFields = false)
public class FlightClient {

  private final RestTemplate restTemplate;
  private final String endpoint = "/flight/";
  private String apiHost;

  public FlightClient(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  public void setApiHost(String apiHost) {
    this.apiHost = apiHost;
  }

  public ResponseEntity<Flight> getFlightById(Long id) {
    var url = apiHost + endpoint + id;
    return restTemplate.getForEntity(url, Flight.class);
  }

  public ResponseEntity<Flight[]> getAllFlights() {
    var url = apiHost + endpoint;
    return restTemplate.getForEntity(url, Flight[].class);
  }

  public ResponseEntity<Long> createFlight(FlightDto flightDto) {
    var url = apiHost + endpoint;
    return restTemplate.postForEntity(url, flightDto, Long.class);
  }

  public void updateFlight(Long id, FlightDto flightDto) {
    var url = apiHost + endpoint + id;
    restTemplate.put(url, flightDto);
  }

  public void deleteFlight(Long id) {
    var url = apiHost + endpoint + id;
    restTemplate.delete(url);
  }
}
