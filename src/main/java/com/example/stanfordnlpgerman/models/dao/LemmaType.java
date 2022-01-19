package com.example.stanfordnlpgerman.models.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

@Entity
@Table(name = "lemmatypes")
@Where(clause = "deleted=0")
@Builder
public class LemmaType implements Comparable<LemmaType> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String text;
  private boolean deleted;

  @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
  private Set<Sentence> sentences = new HashSet<>();

  @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST},  fetch = FetchType.LAZY)
  private Set<NewsArticle> newsArticles = new HashSet<>();

  @OneToMany(mappedBy = "lemmaType", cascade = {CascadeType.MERGE, CascadeType.PERSIST},  fetch = FetchType.LAZY)
  private Set<LemmaToken> lemmaTokens = new HashSet<>();

  @OneToMany(mappedBy = "lemmaType", cascade = {CascadeType.MERGE, CascadeType.PERSIST},  fetch = FetchType.LAZY)
  private Set<TextToken> textTokens = new HashSet<>();

  public LemmaType() {
  }

  public LemmaType(long id, String text, boolean deleted, Set<Sentence> sentences, Set<NewsArticle> newsArticles, Set<LemmaToken> lemmaTokens, Set<TextToken> textTokens) {
    this.id = id;
    this.text = text;
    this.deleted = deleted;
    this.sentences = sentences;
    this.newsArticles = newsArticles;
    this.lemmaTokens = lemmaTokens;
    this.textTokens = textTokens;
  }

  public LemmaType(String text) {
    this.text = text;
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

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public Set<LemmaToken> getLemmaTokens() {
    return lemmaTokens;
  }

  public void setLemmaTokens(Set<LemmaToken> lemmaTokens) {
    this.lemmaTokens = lemmaTokens;
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

  public Set<TextToken> getTextTokens() {
    return textTokens;
  }

  public void setTextTokens(Set<TextToken> textTokens) {
    this.textTokens = textTokens;
  }

  @Override
  public int compareTo(LemmaType lemmaType) {
    return this.text.compareTo(lemmaType.text);
  }

  public void addOneTextToken(TextToken textToken) {
    this.textTokens.add(textToken);
  }

  public void addOneSentence(Sentence sentence) {
    this.sentences.add(sentence);
  }

  public void addOneNewsArticle(NewsArticle newsArticle) {
    this.newsArticles.add(newsArticle);
  }

  public void addOneLemmaToken(LemmaToken lemmaToken) {
    this.lemmaTokens.add(lemmaToken);
  }

  public void removeOneSentence(Sentence sentenceToDelete) {
    this.sentences.removeIf(s -> s.getId() == sentenceToDelete.getId());
  }
}
