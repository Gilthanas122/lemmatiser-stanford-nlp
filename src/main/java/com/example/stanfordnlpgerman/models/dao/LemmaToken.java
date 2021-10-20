package com.example.stanfordnlpgerman.models.dao;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "lemmatokens")
@Where(clause="deleted=0")
public class LemmaToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public long id;
  private String text;
  private String phraseType;
  private boolean deleted;

  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private LemmaType lemmaType;

  public LemmaToken() {
  }

  public LemmaToken(long id, String text, String phraseType, boolean deleted, LemmaType lemmaType) {
    this.id = id;
    this.text = text;
    this.phraseType = phraseType;
    this.deleted = deleted;
    this.lemmaType = lemmaType;
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

  public LemmaType getLemmaType() {
    return lemmaType;
  }

  public void setLemmaType(LemmaType lemmaType) {
    this.lemmaType = lemmaType;
  }
}
