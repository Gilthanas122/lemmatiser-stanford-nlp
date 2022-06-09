package com.example.stanfordnlpgerman.models.dtos.lemmatype;

public interface ShowMostCommonLemmasDTO {

  long getLemmaTypeId();

  String getText();

  int getCount();

  long getNewsArticleId();
}
