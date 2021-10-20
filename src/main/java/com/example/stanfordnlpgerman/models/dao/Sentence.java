package com.example.stanfordnlpgerman.models.dao;

import lombok.Builder;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sentences")
@Where(clause="deleted=0")
@Builder
public class Sentence {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private long id;
  private String text;
  private int textPosition;
  private boolean deleted;

  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private NewsArticle newsArticle;

  @ManyToMany(mappedBy = "sentences", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private List<LemmaType> lemmaTypes = new ArrayList<>();

  @OneToMany(mappedBy = "sentence", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private List<TextToken> textTokens;

  public Sentence() {
  }

  public Sentence(long id, String text, int textPosition, boolean deleted, NewsArticle newsArticle, List<LemmaType> lemmaTypes, List<TextToken> textTokens) {
    this.id = id;
    this.text = text;
    this.textPosition = textPosition;
    this.deleted = deleted;
    this.newsArticle = newsArticle;
    this.lemmaTypes = lemmaTypes;
    this.textTokens = textTokens;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public int getTextPosition() {
    return textPosition;
  }

  public void setTextPosition(int textPosition) {
    this.textPosition = textPosition;
  }

  public NewsArticle getNewsArticle() {
    return newsArticle;
  }

  public void setNewsArticle(NewsArticle newsArticle) {
    this.newsArticle = newsArticle;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public List<LemmaType> getLemmaTypes() {
    return lemmaTypes;
  }

  public void setLemmaTypes(List<LemmaType> lemmaTypes) {
    this.lemmaTypes = lemmaTypes;
  }

  public List<TextToken> getTextTokens() {
    return textTokens;
  }

  public void setTextTokens(List<TextToken> textTokens) {
    this.textTokens = textTokens;
  }
}
