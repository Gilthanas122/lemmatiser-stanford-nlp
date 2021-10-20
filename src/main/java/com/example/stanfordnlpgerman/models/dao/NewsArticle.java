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
  private String newsPaperName;
  private String title;
  private String author;
  @Temporal(TemporalType.DATE)
  private Date publicationDate;
  private int pageNumber;
  private boolean deleted;

  @OneToMany(mappedBy = "newsArticle", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private List<Sentence> sentences = new ArrayList<>();

  @ManyToMany(mappedBy = "newsArticles", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private List<Lemma> lemmas = new ArrayList<>();

  public NewsArticle() {
  }

  public NewsArticle(long id, String newsPaperName, String title, String author, Date publicationDate, int pageNumber, boolean deleted, List<Sentence> sentences, List<Lemma> lemmas) {
    this.id = id;
    this.newsPaperName = newsPaperName;
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

  public String getNewsPaperName() {
    return newsPaperName;
  }

  public void setNewsPaperName(String newsPaperName) {
    this.newsPaperName = newsPaperName;
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

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public void setId(long id) {
    this.id = id;
  }

  public List<Sentence> getSentences() {
    return sentences;
  }

  public void setSentences(List<Sentence> sentences) {
    this.sentences = sentences;
  }

  public List<Lemma> getLemmas() {
    return lemmas;
  }

  public void setLemmas(List<Lemma> lemmas) {
    this.lemmas = lemmas;
  }
}