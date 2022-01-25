package com.example.stanfordnlpgerman.models.dtos.newsarticle;

import java.util.Date;

public class MostRelevantNewsArticleDTOAGG {
  private long id;
  private String newsPaperName;
  private String text;
  private Date publicationDate;

  public MostRelevantNewsArticleDTOAGG() {
  }

  public MostRelevantNewsArticleDTOAGG(long id, String newsPaperName, String text, Date publicationDate) {
    this.id = id;
    this.newsPaperName = newsPaperName;
    this.text = text;
    this.publicationDate = publicationDate;
  }


  public MostRelevantNewsArticleDTOAGG(MostRelevantNewsArticlesDTO mostRelevantNewsArticlesDTO){
    this.id = mostRelevantNewsArticlesDTO.getId();
    this.newsPaperName = mostRelevantNewsArticlesDTO.getNewsPaperName();
    this.text = mostRelevantNewsArticlesDTO.getText();
    this.publicationDate = mostRelevantNewsArticlesDTO.getPublicationDate();
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
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

  public Date getPublicationDate() {
    return publicationDate;
  }

  public void setPublicationDate(Date publicationDate) {
    this.publicationDate = publicationDate;
  }
}
