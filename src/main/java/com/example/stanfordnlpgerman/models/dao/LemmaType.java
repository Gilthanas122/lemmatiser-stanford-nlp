package com.example.stanfordnlpgerman.models.dao;

import lombok.Builder;
import org.hibernate.annotations.Where;
import org.springframework.boot.CommandLineRunner;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "lemmatypes")
@Where(clause="deleted=0")
@Builder
public class LemmaType implements Comparable<LemmaType> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @Column(columnDefinition = "varchar(50)")
  private String text;
  private boolean deleted;

  @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private List<Sentence> sentences = new ArrayList<>();

  @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private List<NewsArticle> newsArticles = new ArrayList<>();

  @OneToMany(mappedBy = "lemmaType", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private Set<LemmaToken> lemmaTokens = new HashSet<>();

  @OneToMany(mappedBy = "lemmaType", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private List<TextToken> textTokens = new ArrayList<>();

  public LemmaType() {
  }

  public LemmaType(long id, String text, boolean deleted, List<Sentence> sentences, List<NewsArticle> newsArticles, Set<LemmaToken> lemmaTokens, List<TextToken> textTokens) {
    this.id = id;
    this.text = text;
    this.deleted = deleted;
    this.sentences = sentences;
    this.newsArticles = newsArticles;
    this.lemmaTokens = lemmaTokens;
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

  public Set<LemmaToken> getLemmaTokens() {
    return lemmaTokens;
  }

  public void setLemmaTokens(Set<LemmaToken> lemmaTokens) {
    this.lemmaTokens = lemmaTokens;
  }

  public List<TextToken> getTextTokens() {
    return textTokens;
  }

  public void setTextTokens(List<TextToken> textTokens) {
    this.textTokens = textTokens;
  }

  @Override
  public int compareTo(LemmaType lemmaType) {
    return this.text.compareTo(lemmaType.text);
  }

  public void addOneTextToken(TextToken textToken) {
    this.textTokens.add(textToken);
  }
}
