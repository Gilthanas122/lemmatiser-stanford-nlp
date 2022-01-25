package com.example.stanfordnlpgerman.models.dtos.newsarticle;

import lombok.Builder;

import java.util.Date;

@Builder
public class NewsArticleDataDTO {
  private String title;
  private String author;
  private short pageNumber;
  private String newsPaperName;
  private String text;
  private int publicationYear;

  public NewsArticleDataDTO() {
  }

  public NewsArticleDataDTO(String title, String author, short pageNumber, String newsPaperName, String text, int publicationYear) {
    this.title = title;
    this.author = author;
    this.pageNumber = pageNumber;
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

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public short getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(short pageNumber) {
    this.pageNumber = pageNumber;
  }

  public String getNewsPaperName() {
    return newsPaperName;
  }

  public void setNewsPaperName(String newsPaperName) {
    this.newsPaperName = newsPaperName;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public int getPublicationYear() {
    return publicationYear;
  }

  public void setPublicationYear(int publicationYear) {
    this.publicationYear = publicationYear;
  }
}
