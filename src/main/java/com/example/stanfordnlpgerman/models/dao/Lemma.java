package com.example.stanfordnlpgerman.models.dao;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

  @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private List<Sentence> sentences = new ArrayList<>();

  @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private List<NewsArticle> newsArticles = new ArrayList<>();

  public Lemma() {
  }

  public Lemma(long id, String text, String phraseType, boolean deleted, List<Sentence> sentences, List<NewsArticle> newsArticles) {
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

  public List<Sentence> getSentences() {
    return sentences;
  }

  public void setSentences(List<Sentence> sentences) {
    this.sentences = sentences;
  }

  public List<NewsArticle> getNewsArticles() {
    return newsArticles;
  }

  public void setNewsArticles(List<NewsArticle> newsArticles) {
    this.newsArticles = newsArticles;
  }

  public void addOneSentence(Sentence sentence) {
    this.sentences.add(sentence);
  }

  public void addOneNewsArticle(NewsArticle newsArticle) {
    this.newsArticles.add(newsArticle);
  }
}
