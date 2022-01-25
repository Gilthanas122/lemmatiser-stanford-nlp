package com.example.stanfordnlpgerman.models.dtos.newsarticle;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

public class CreateNewsPaperArticleDTO {
  private String title;
  private String newsPaperName;
  private String text;
  private int publicationYear;

  public CreateNewsPaperArticleDTO() {
  }

  public CreateNewsPaperArticleDTO(String title, String newsPaperName, String text, int publicationYear) {
    this.title = title;
    this.newsPaperName = newsPaperName;
    this.text = text;
    this.publicationYear = publicationYear;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getNewsPaperName() {
    return newsPaperName;
  }

  public void setNewsPaperName(String newsPaperName) {
    this.newsPaperName = newsPaperName;
  }

  public int getPublicationYear() {
    return publicationYear;
  }

  public void setPublicationYear(int publicationYear) {
    this.publicationYear = publicationYear;
  }
}
