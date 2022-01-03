package com.example.stanfordnlpgerman.models.dao;

import lombok.Builder;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "newsarticles")
@Builder
@Where(clause = "deleted=0")
public class NewsArticle {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String newsPaperName;
  private String title;
  private String author;
  @Temporal(TemporalType.DATE)
  private Date publicationDate;
  private short pageNumber;
  private boolean deleted;
  private int relevance;

  @OneToMany(mappedBy = "newsArticle", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
  private List<Sentence> sentences = new ArrayList<>();

  @ManyToMany(mappedBy = "newsArticles", cascade = {CascadeType.MERGE, CascadeType.PERSIST},  fetch = FetchType.LAZY)
  private List<LemmaType> lemmaTypes = new ArrayList<>();

  public NewsArticle() {
  }

  public NewsArticle(long id, String newsPaperName, String title, String author, Date publicationDate, short pageNumber, boolean deleted, int relevance, List<Sentence> sentences, List<LemmaType> lemmaTypes) {
    this.id = id;
    this.newsPaperName = newsPaperName;
    this.title = title;
    this.author = author;
    this.publicationDate = publicationDate;
    this.pageNumber = pageNumber;
    this.deleted = deleted;
    this.relevance = relevance;
    this.sentences = sentences;
    this.lemmaTypes = lemmaTypes;
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

  public short getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(short pageNumber) {
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

  public List<LemmaType> getLemmaTypes() {
    return lemmaTypes;
  }

  public void setLemmaTypes(List<LemmaType> lemmaTypes) {
    this.lemmaTypes = lemmaTypes;
  }

  public int getRelevance() {
    return relevance;
  }

  public void setRelevance(int relevance) {
    this.relevance = relevance;
  }

  public void addOneLemmaType(LemmaType lemmaType) {
    this.lemmaTypes.add(lemmaType);
  }
}
