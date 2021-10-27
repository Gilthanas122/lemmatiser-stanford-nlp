package com.example.stanfordnlpgerman.models.dtos.lemmatype;

import java.util.List;

public interface ShowMostCommonLemmasDTO {

  long getLemmaTypeId();

  String getText();

  int getCount();

  long getNewsArticleId();
}
