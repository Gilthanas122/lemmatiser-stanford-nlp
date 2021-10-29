package com.example.stanfordnlpgerman.models.dtos.newsarticle;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

public class CreateNewsPaperArticleDTO {
  private String title;
  private String author;
  private short pageNumber;
  private String newsPaperName;
  private String text;

  @Temporal(TemporalType.DATE)
  private Date publicationDate;

  public CreateNewsPaperArticleDTO() {
  }

  public CreateNewsPaperArticleDTO(String title, String author, short pageNumber, String newsPaperName, String text, Date publicationDate) {
    this.title = title;
    this.author = author;
    this.pageNumber = pageNumber;
    this.newsPaperName = newsPaperName;
    this.text = text;
    this.publicationDate = publicationDate;
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

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Date getPublicationDate() {
    return publicationDate;
  }

  public void setPublicationDate(Date publicationDate) {
    this.publicationDate = publicationDate;
  }

  public String getNewsPaperName() {
    return newsPaperName;
  }

  public void setNewsPaperName(String newsPaperName) {
    this.newsPaperName = newsPaperName;
  }
}