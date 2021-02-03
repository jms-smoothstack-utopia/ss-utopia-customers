package com.ss.utopia.customer.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodDto {

  @NotBlank(message = "Card number is required.")
  private String cardNumber;

  @NotBlank(message = "Expiration month is required.")
  private Long expMonth;

  @NotBlank(message = "Expiration year is required.")
  private Long expYear;

  @NotBlank(message = "CVC is required.")
  private String cvc;

  private String notes;
}
