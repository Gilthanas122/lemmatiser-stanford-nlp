package com.example.stanfordnlpgerman.models.dtos.lemmatype;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvalidLemmasDTO {
  private long textTokenId;
  private String textTokenText;
}
