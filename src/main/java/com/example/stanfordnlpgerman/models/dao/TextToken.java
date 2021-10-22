package com.example.stanfordnlpgerman.models.dao;

import lombok.Builder;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "texttokens")
@Where(clause="deleted=0")
@Builder
public class TextToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String text;
  private String phraseType;
  @Column(columnDefinition = "integer default 0")
  private int sentencePosition = 1;
  private boolean deleted;
  private boolean invalid;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private LemmaType lemmaType;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private Sentence sentence;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private NewsArticle newsArticle;

  public TextToken() {
    sentencePosition = 1;
  }

  public TextToken(long id, String text, String phraseType, int sentencePosition, boolean deleted, boolean invalid, LemmaType lemmaType, Sentence sentence, NewsArticle newsArticle) {
    this.id = id;
    this.text = text;
    this.phraseType = phraseType;
    this.sentencePosition = sentencePosition;
    this.deleted = deleted;
    this.invalid = invalid;
    this.lemmaType = lemmaType;
    this.sentence = sentence;
    this.newsArticle = newsArticle;
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

  public int getSentencePosition() {
    return sentencePosition;
  }

  public void setSentencePosition(int sentencePosition) {
    this.sentencePosition = sentencePosition;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public LemmaType getLemmaType() {
    return lemmaType;
  }

  public void setLemmaType(LemmaType lemmaType) {
    this.lemmaType = lemmaType;
  }

  public Sentence getSentence() {
    return sentence;
  }

  public void setSentence(Sentence sentence) {
    this.sentence = sentence;
  }

  public NewsArticle getNewsArticle() {
    return newsArticle;
  }

  public void setNewsArticle(NewsArticle newsArticle) {
    this.newsArticle = newsArticle;
  }

  public boolean isInvalid() {
    return invalid;
  }

  public void setInvalid(boolean invalid) {
    this.invalid = invalid;
  }
}
