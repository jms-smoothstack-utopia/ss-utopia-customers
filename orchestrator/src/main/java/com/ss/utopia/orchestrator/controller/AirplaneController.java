package com.ss.utopia.orchestrator.controller;

import com.ss.utopia.orchestrator.client.AirplaneClient;
import com.ss.utopia.orchestrator.dto.AirplaneDto;
import com.ss.utopia.orchestrator.model.Airplane;
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
@RequestMapping("/airplane")
public class AirplaneController {

  private final AirplaneClient client;

  public AirplaneController(AirplaneClient client) {
    this.client = client;
  }

  @GetMapping("/{id}")
  public ResponseEntity<Airplane> getAirplaneById(@PathVariable Long id) {
    return client.getAirplaneById(id);
  }

  @GetMapping
  public ResponseEntity<Airplane[]> getAllAirplanes() {
    return client.getAllAirplanes();
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Long> createAirplane(@Valid @RequestBody AirplaneDto airplaneDto) {
    return client.createAirplane(airplaneDto);
  }

  @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> updateAirplane(@PathVariable Long id,
                                          @Valid @RequestBody AirplaneDto airplaneDto) {
    client.updateAirplane(id, airplaneDto);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteAirplane(@PathVariable Long id) {
    client.deleteAirplane(id);
    return ResponseEntity.noContent().build();
  }
}
