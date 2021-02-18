package com.ss.utopia.customer.dto;

import java.util.UUID;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
public class CreateCustomerRecordDto {

  @NotNull
  private UUID id;

  @NotBlank(message = "First name is mandatory")
  private String firstName;

  @NotBlank(message = "Last name is mandatory")
  private String lastName;

  @NotNull
  @NotBlank(message = "Email cannot be blank.")
  @Email(message = "Email must be valid.")
  private String email;

  @Pattern(regexp = "^\\d{3}-\\d{3}-\\d{4}$", message = "Phone number must be in the form ###-###-####.")
  private String phoneNumber;

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