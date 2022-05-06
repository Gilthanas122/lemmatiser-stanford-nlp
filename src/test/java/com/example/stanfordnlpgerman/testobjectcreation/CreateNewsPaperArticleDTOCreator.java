package com.example.stanfordnlpgerman.testobjectcreation;

import com.example.stanfordnlpgerman.models.dtos.newsarticle.CreateNewsPaperArticleDTO;

import java.util.ArrayList;
import java.util.List;

public class CreateNewsPaperArticleDTOCreator {
  public static final String NEWS_PAPER_NAME = "my news paper name";
  public static final Integer PUBLICATION_YEAR = 1999;
  public static final String TEXT = "my text sentence 1. Sentence 2?";
  public static final String TITLE = "my title";


  public static List<CreateNewsPaperArticleDTO> createNewsPaperArticleDTOs(int amount){
    List<CreateNewsPaperArticleDTO> createNewsPaperArticleDTOs = new ArrayList<>();
    for (int index = 1; index <= 2; index++) {
      createNewsPaperArticleDTOs.add(createCreateNewsPaperArticleDTO(index));
    }
    return createNewsPaperArticleDTOs;
  }

  public static CreateNewsPaperArticleDTO createCreateNewsPaperArticleDTO(int index){
    return CreateNewsPaperArticleDTO.builder()
            .newsPaperName(NEWS_PAPER_NAME)
            .publicationYear(PUBLICATION_YEAR)
            .text(TEXT + index)
            .title(TITLE + index)
            .build();
  }
}
