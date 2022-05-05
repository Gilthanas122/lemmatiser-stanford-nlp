package com.example.stanfordnlpgerman.models.dtos.validations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessageDTO {
  private String status;
  private String message;
}