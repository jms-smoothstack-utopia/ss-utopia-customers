package com.ss.utopia.customer.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCustomerLoyaltyDto {

  @NotNull
  @Min(1)
  private Integer pointsToChange;

  @NotNull
  private Boolean increment;
}
