package com.ss.utopia.customer.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteAccountDto {

  private UUID id;

  private String email;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private String password;
}

