package com.example.stanfordnlpgerman.models.dtos.lemmatype;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLemmaTypeRequest {
  private String lemmaTypeId;
  private String lemmaToken;
  private String phraseType;
}
