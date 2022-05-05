package com.example.stanfordnlpgerman.models.dao;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "lemmatypes")
@Where(clause = "deleted=0")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LemmaType implements Comparable<LemmaType> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String text;
  private boolean deleted;

  @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
  private List<Sentence> sentences = new ArrayList<>();

  @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
  private List<NewsArticle> newsArticles = new ArrayList<>();

  @OneToMany(mappedBy = "lemmaType", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
  private List<LemmaToken> lemmaTokens = new ArrayList<>();

  @OneToMany(mappedBy = "lemmaType", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
  private List<TextToken> textTokens = new ArrayList<>();

  public LemmaType(String text) {
    this.text = text;
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
