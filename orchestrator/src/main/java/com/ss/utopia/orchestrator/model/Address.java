package com.ss.utopia.orchestrator.model;

import lombok.Data;

@Data
public class Address {

  private Long id;
  private Integer cardinality;
  private String line1;
  private String line2;
  private String city;
  private String state;
  private String zipcode;
}
