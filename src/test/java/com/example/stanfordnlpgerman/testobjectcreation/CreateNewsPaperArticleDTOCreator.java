package com.example.stanfordnlpgerman.testobjectcreation;

import com.example.stanfordnlpgerman.models.dtos.newsarticle.CreateNewsPaperArticleDTO;

public class CreateNewsPaperArticleDTOCreator {
  public static final String NEWS_PAPER_NAME = "my news paper name";
  public static final Integer PUBLICATION_YEAR = 1999;
  public static final String TEXT = "my text";
  public static final String TITLE = "my title";

  public static CreateNewsPaperArticleDTO createCreateNewsPaperArticleDTO(){
    return CreateNewsPaperArticleDTO.builder()
            .newsPaperName(NEWS_PAPER_NAME)
            .publicationYear(PUBLICATION_YEAR)
            .text(TEXT)
            .title(TITLE)
            .build();
  }
}
