package com.ss.utopia.customer.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

  @NotNull(message = "Expiration month is required.")
  @Digits(integer = 2, fraction = 0, message = "Expiration month must be a number.")
  private Long expMonth;

  @NotNull(message = "Expiration year is required.")
  @Digits(integer = 4, fraction = 0, message = "Expiration year must be a number.")
  private Long expYear;

  @NotBlank(message = "CVC is required.")
  private String cvc;

  private String notes;
}
