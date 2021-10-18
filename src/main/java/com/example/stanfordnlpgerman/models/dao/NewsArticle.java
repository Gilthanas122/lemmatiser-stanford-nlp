package com.example.stanfordnlpgerman.models.dao;

import lombok.Builder;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "newsarticles")
@Builder
@Where(clause="deleted=0")
public class NewsArticle {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @Enumerated(EnumType.STRING)
  private NewsPaperEnum newsPaperEnum;
  private String title;
  private String author;
  @Temporal(TemporalType.DATE)
  private Date publicationDate;
  private int pageNumber;
  private boolean deleted;

  @OneToMany(mappedBy = "newsArticle", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private Set<Sentence> sentences = new HashSet<>();

  @ManyToMany(mappedBy = "newsArticles")
  private Set<Lemma> lemmas = new HashSet<>();

  public NewsArticle() {
  }

  public NewsArticle(Long id, NewsPaperEnum newsPaperEnum, String title, String author, Date publicationDate, int pageNumber, boolean deleted, Set<Sentence> sentences, Set<Lemma> lemmas) {
    this.id = id;
    this.newsPaperEnum = newsPaperEnum;
    this.title = title;
    this.author = author;
    this.publicationDate = publicationDate;
    this.pageNumber = pageNumber;
    this.deleted = deleted;
    this.sentences = sentences;
    this.lemmas = lemmas;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public NewsPaperEnum getNewsPaperEnum() {
    return newsPaperEnum;
  }

  public void setNewsPaperEnum(NewsPaperEnum newsPaperEnum) {
    this.newsPaperEnum = newsPaperEnum;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public Date getPublicationDate() {
    return publicationDate;
  }

  public void setPublicationDate(Date publicationDate) {
    this.publicationDate = publicationDate;
  }

  public int getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(int pageNumber) {
    this.pageNumber = pageNumber;
  }

  public Set<Sentence> getSentences() {
    return sentences;
  }

  public void setSentences(Set<Sentence> sentences) {
    this.sentences = sentences;
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
