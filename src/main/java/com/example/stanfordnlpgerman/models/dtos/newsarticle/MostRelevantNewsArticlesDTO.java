package com.example.stanfordnlpgerman.models.dtos.newsarticle;

import java.time.LocalDate;

public interface MostRelevantNewsArticlesDTO {
  long getId();

  String getNewsPaperName();

  String getText();

  LocalDate getPublicationDate();

}
