package com.example.stanfordnlpgerman.models.dtos.newsarticle;

import lombok.*;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MostRelevantNewsArticleDTOAGG {
  private long id;
  private String newsPaperName;
  private String text;
  private LocalDate publicationDate;

  public MostRelevantNewsArticleDTOAGG(MostRelevantNewsArticlesDTO mostRelevantNewsArticlesDTO){
    this.id = mostRelevantNewsArticlesDTO.getId();
    this.newsPaperName = mostRelevantNewsArticlesDTO.getNewsPaperName();
    this.text = mostRelevantNewsArticlesDTO.getText();
    this.publicationDate = mostRelevantNewsArticlesDTO.getPublicationDate();
  }
}
