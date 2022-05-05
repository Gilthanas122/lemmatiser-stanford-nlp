package com.example.stanfordnlpgerman.testobjectcreation;

import com.example.stanfordnlpgerman.models.dao.NewsArticle;

import java.util.ArrayList;
import java.util.List;

public class NewsArticleCreator {


  public static List<NewsArticle> createNewsArticles(int amount){
    List<NewsArticle> newsArticles = new ArrayList<>();
    for (int index = 1; index <= amount; index++) {
      newsArticles.add(createNewsArticle(index));
    }
    return newsArticles;
  }

  public static NewsArticle createNewsArticle(int index) {
    return NewsArticle.builder()
            .id(index)
            .newsPaperName("my news paper name" + index)
            .title("my news paper title " + index)
            .publicationYear(index + 1999)
            .relevance(index)
            .sentences(new ArrayList<>())
            .lemmaTypes(new ArrayList<>())
            .build();
  }
}
