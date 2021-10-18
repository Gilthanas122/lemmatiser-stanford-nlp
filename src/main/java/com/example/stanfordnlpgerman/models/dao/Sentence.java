package com.example.stanfordnlpgerman.models.dao;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sentences")
@Where(clause="deleted=0")
public class Sentence {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  private String text;
  private int textPosition;
  private boolean deleted;

  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private NewsArticle newsArticle;

  @ManyToMany(mappedBy = "sentences")
  private Set<Lemma> lemmas = new HashSet<>();

  public Sentence() {
  }

  public Sentence(Long id, String text, int textPosition, boolean deleted, NewsArticle newsArticle, Set<Lemma> lemmas) {
    this.id = id;
    this.text = text;
    this.textPosition = textPosition;
    this.deleted = deleted;
    this.newsArticle = newsArticle;
    this.lemmas = lemmas;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
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

  public Set<Lemma> getLemmas() {
    return lemmas;
  }

  public void setLemmas(Set<Lemma> lemmas) {
    this.lemmas = lemmas;
  }
}
