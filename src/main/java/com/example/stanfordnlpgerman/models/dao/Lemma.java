package com.example.stanfordnlpgerman.models.dao;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "lemmas")
@Where(clause="deleted=0")
public class Lemma {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String text;
  private String phraseType;
  private boolean deleted;

  @ManyToMany
  private Set<Sentence> sentences = new HashSet<>();

  @ManyToMany
  private Set<NewsArticle> newsArticles = new HashSet<>();

  public Lemma() {
  }

  public Lemma(long id, String text, String phraseType, boolean deleted, Set<Sentence> sentences, Set<NewsArticle> newsArticles) {
    this.id = id;
    this.text = text;
    this.phraseType = phraseType;
    this.deleted = deleted;
    this.sentences = sentences;
    this.newsArticles = newsArticles;
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

  public String getPhraseType() {
    return phraseType;
  }

  public void setPhraseType(String phraseType) {
    this.phraseType = phraseType;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public Set<Sentence> getSentences() {
    return sentences;
  }

  public void setSentences(Set<Sentence> sentences) {
    this.sentences = sentences;
  }

  public Set<NewsArticle> getNewsArticles() {
    return newsArticles;
  }

  public void setNewsArticles(Set<NewsArticle> newsArticles) {
    this.newsArticles = newsArticles;
  }
}
