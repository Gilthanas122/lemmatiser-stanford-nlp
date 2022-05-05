package com.example.stanfordnlpgerman.models.dtos.texttoken;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddLemmaTypeToTextTokenDTO {
  private long textTokenId;
  private String textTokenText;
  private String sentenceText;
  private Map<Long, String> lemmaTypeTextsAndIds = new HashMap();
}