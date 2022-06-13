package com.example.stanfordnlpgerman.models.dtos.texttoken;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddLemmaTypeToTextTokenDTO {
  private long textTokenId;
  private String textTokenText;
  private String sentenceText;
  private Map<Long, String> lemmaTypeTextsAndIds = new HashMap();
  private String phraseType;
}