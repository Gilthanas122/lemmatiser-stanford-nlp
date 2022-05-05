package com.example.stanfordnlpgerman.models.dtos.newsarticle;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateNewsPaperArticleDTO {
  private String title;
  private String newsPaperName;
  private String text;
  private int publicationYear;
}
