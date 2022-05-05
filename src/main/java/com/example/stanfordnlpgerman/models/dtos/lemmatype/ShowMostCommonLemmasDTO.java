package com.example.stanfordnlpgerman.models.dtos.lemmatype;

import lombok.Builder;

public interface ShowMostCommonLemmasDTO {

  long getLemmaTypeId();

  String getText();

  int getCount();

  long getNewsArticleId();
}
