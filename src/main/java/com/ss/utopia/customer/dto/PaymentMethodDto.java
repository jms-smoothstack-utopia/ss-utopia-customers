package com.ss.utopia.customer.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentMethodDto {

  @NotBlank(message = "Account number is required.")
  private String accountNum;

  private String notes;
}
