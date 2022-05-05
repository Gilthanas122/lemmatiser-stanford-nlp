package com.example.stanfordnlpgerman.models.dtos.lemmatype;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvalidLemmasDTO {
  private long textTokenId;
  private String textTokenText;
}
