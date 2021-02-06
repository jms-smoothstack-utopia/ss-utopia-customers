package com.ss.utopia.orchestrator.controller;

import com.ss.utopia.orchestrator.client.AirportClient;
import com.ss.utopia.orchestrator.dto.AirportDto;
import com.ss.utopia.orchestrator.model.Airport;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/airport")
public class AirportController {

  private final AirportClient client;

  public AirportController(AirportClient client) {
    this.client = client;
  }

  @GetMapping("/{id}")
  public ResponseEntity<Airport> getAirportById(@PathVariable Long id) {
    return client.getAirportById(id);
  }

  @GetMapping
  public ResponseEntity<Airport[]> getAllAirports() {
    return client.getAllAirports();
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Long> createAirport(@Valid @RequestBody AirportDto airportDto) {
    return client.createAirport(airportDto);
  }

  @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> updateAirport(@PathVariable Long id,
                                         @Valid @RequestBody AirportDto airportDto) {
    client.updateAirport(id, airportDto);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteAirport(@PathVariable Long id) {
    client.deleteAirport(id);
    return ResponseEntity.noContent().build();
  }
}
