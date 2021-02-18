package com.ss.utopia.customer.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto {

  @NotBlank(message = "First name is mandatory")
  private String firstName;

  @NotBlank(message = "Last name is mandatory")
  private String lastName;

  @Min(0)
  private Integer loyaltyPoints;

  @NotBlank
  @Email(message = "Email is invalid")
  private String email;

  @NotBlank(message = "Address line1 is mandatory")
  private String addrLine1;

  private String addrLine2;

  @NotBlank(message = "City is mandatory")
  private String city;

  @NotBlank(message = "State is mandatory")
  @Size(min = 2, max = 2, message = "State must consist of only 2 characters.")
  private String state;

  @NotBlank(message = "Zipcode is mandatory")
  @Pattern(regexp = "^\\d{5}(?:[-\\s]\\d{4})?$",
      message = "Zipcode does not meet expected format: '#####-####' or '#####'")
  private String zipcode;
}

