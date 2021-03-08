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
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCustomerDto {

  public static final String REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*-_=+,.?])[A-Za-z\\d!@#$%^&*-_=+,.?]{10,128}$";
  public static final String REGEX_MSG = "Password must be between 10 and 128 characters,"
      + " contain at least one lowercase letter,"
      + " at least one uppercase letter,"
      + " at least one number,"
      + " and at least one special character from the following: !@#$%^&*-_=+,.?";

  @NotNull
  @NotBlank(message = "First name is mandatory")
  private String firstName;

  @NotNull
  @NotBlank(message = "Last name is mandatory")
  private String lastName;

  @NotNull
  @NotBlank(message = "Email cannot be blank.")
  @Email(message = "Email must be valid.")
  private String email;

  @ToString.Exclude
  @NotNull
  @NotBlank(message = "Password cannot be blank.")
  @Size(min = 10, max = 128, message = "Length must be between 10 and 128 characters.")
  @Pattern(regexp = REGEX, message = REGEX_MSG)
  private String password;

  @NotNull
  @NotBlank
  @Pattern(regexp = "^\\d{3}-\\d{3}-\\d{4}$", message = "Phone number must be in the form ###-###-####.")
  private String phoneNumber;

  @NotNull
  @NotBlank(message = "Address line1 is mandatory")
  private String addrLine1;

  private String addrLine2;

  @NotNull
  @NotBlank(message = "City is mandatory")
  private String city;

  @NotNull
  @NotBlank(message = "State is mandatory")
  @Size(min = 2, max = 2, message = "State must consist of only 2 characters.")
  private String state;

  @NotNull
  @NotBlank(message = "Zipcode is mandatory")
  @Pattern(regexp = "^\\d{5}(?:[-\\s]\\d{4})?$",
      message = "Zipcode does not meet expected format: '#####-####' or '#####'")
  private String zipcode;
  
  private Boolean ticketEmails;
  
  private Boolean flightEmails;
}