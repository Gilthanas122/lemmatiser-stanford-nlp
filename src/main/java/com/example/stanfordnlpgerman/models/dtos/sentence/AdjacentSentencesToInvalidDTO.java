package com.example.stanfordnlpgerman.models.dtos.sentence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdjacentSentencesToInvalidDTO {
  private String sentencesText;
  private long originalSentenceId;
  private String originalSentenceText;
}
