package com.example.stanfordnlpgerman.models.dtos.sentence;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LemmaOccurenceInSentencesDTO implements Comparable<LemmaOccurenceInSentencesDTO> {
  private String originalLemmaText;
  private String lemmaText;
  private Integer lemmaOccurence;

  @Override
  public int compareTo(LemmaOccurenceInSentencesDTO lemmaOccurenceInSentencesDTO) {
    return lemmaOccurenceInSentencesDTO.getLemmaOccurence().compareTo(this.lemmaOccurence);
  }
}
