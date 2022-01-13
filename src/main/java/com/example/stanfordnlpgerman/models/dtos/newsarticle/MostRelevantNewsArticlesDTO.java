package com.example.stanfordnlpgerman.models.dtos.newsarticle;

import java.util.Date;

public interface MostRelevantNewsArticlesDTO {
  long getId();

  String getNewsPaperName();

  String getText();

  Date getPublicationDate();

}
