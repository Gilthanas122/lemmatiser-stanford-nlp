package com.example.stanfordnlpgerman.models.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "texttokens")
@Where(clause = "deleted=0")
@Builder
public class TextToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @Column(columnDefinition = "varchar(50)")
  private String text;
  @Column(columnDefinition = "varchar(8)")
  private String phraseType;
  @Column(columnDefinition = "smallint default 0")
  private short sentencePosition = 1;
  private boolean deleted;
  private boolean invalid;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JsonIgnore
  private LemmaType lemmaType;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JsonIgnore
  private Sentence sentence;

  public TextToken() {
    sentencePosition = 1;
  }

  public TextToken(long id, String text, String phraseType, short sentencePosition, boolean deleted, boolean invalid, LemmaType lemmaType, Sentence sentence) {
    this.id = id;
    this.text = text;
    this.phraseType = phraseType;
    this.sentencePosition = sentencePosition;
    this.deleted = deleted;
    this.invalid = invalid;
    this.lemmaType = lemmaType;
    this.sentence = sentence;
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

  public short getSentencePosition() {
    return sentencePosition;
  }

  public void setSentencePosition(short sentencePosition) {
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

  public boolean isInvalid() {
    return invalid;
  }

  public void setInvalid(boolean invalid) {
    this.invalid = invalid;
  }
}
