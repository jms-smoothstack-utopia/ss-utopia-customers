package com.ss.utopia.orchestrator.controller;

import com.ss.utopia.orchestrator.client.TicketsClient;
import com.ss.utopia.orchestrator.dto.TicketsDto;
import com.ss.utopia.orchestrator.model.Ticket;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickets")
public class TicketsController {

  private final TicketsClient client;

  public TicketsController(TicketsClient client) {
    this.client = client;
  }

  @GetMapping
  public ResponseEntity<Ticket[]> getAllTickets() {
    return client.getAllTickets();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
    return client.getTicketById(id);
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Ticket> createTicket(@Valid @RequestBody TicketsDto ticketsDto) {
    return client.createTicket(ticketsDto);
  }

  @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> checkIn(@PathVariable Long id,
                                   @Valid @RequestBody TicketsDto ticketsDto) {
    client.checkIn(id, ticketsDto);
    return ResponseEntity.ok().build();
  }
}
