package com.example.stanfordnlpgerman.models.dtos.newsarticle;

import lombok.*;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewsArticleDataDTO {
  private String title;
  private String newsPaperName;
  private String text;
  private int publicationYear;

}
