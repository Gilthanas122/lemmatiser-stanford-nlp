package com.example.stanfordnlpgerman.models.dao;

import lombok.Builder;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
  private List<Lemma> lemmas = new ArrayList<>();

  public Sentence() {
  }

  public Sentence(long id, String text, int textPosition, boolean deleted, NewsArticle newsArticle, List<Lemma> lemmas) {
    this.id = id;
    this.text = text;
    this.textPosition = textPosition;
    this.deleted = deleted;
    this.newsArticle = newsArticle;
    this.lemmas = lemmas;
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

  public List<Lemma> getLemmas() {
    return lemmas;
  }

  public void setLemmas(List<Lemma> lemmas) {
    this.lemmas = lemmas;
  }
}
