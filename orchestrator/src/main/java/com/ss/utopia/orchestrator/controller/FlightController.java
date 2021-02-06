package com.ss.utopia.orchestrator.controller;

import com.ss.utopia.orchestrator.client.FlightClient;
import com.ss.utopia.orchestrator.dto.FlightDto;
import com.ss.utopia.orchestrator.model.Flight;
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
@RequestMapping("/flight")
public class FlightController {

  private final FlightClient client;

  public FlightController(FlightClient client) {
    this.client = client;
  }

  @GetMapping("/{id}")
  public ResponseEntity<Flight> getFlightById(@PathVariable Long id) {
    return client.getFlightById(id);
  }

  @GetMapping
  public ResponseEntity<Flight[]> getAllFlights() {
    return client.getAllFlights();
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Long> createFlight(@Valid @RequestBody FlightDto flightDto) {
    return client.createFlight(flightDto);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateFlight(@PathVariable Long id,
                                        @Valid @RequestBody FlightDto flightDto) {
    client.updateFlight(id, flightDto);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteFlight(@PathVariable Long id) {
    client.deleteFlight(id);
    return ResponseEntity.noContent().build();
  }
}
