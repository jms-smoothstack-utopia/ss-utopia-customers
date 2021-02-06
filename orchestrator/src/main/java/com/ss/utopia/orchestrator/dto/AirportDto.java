package com.ss.utopia.orchestrator.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AirportDto {

  @NotBlank(message = "City is mandatory")
  private String city;

  @NotBlank(message = "Iata Id is mandatory")
  private String iataId;
}
