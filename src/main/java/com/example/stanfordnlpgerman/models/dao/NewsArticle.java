package com.example.stanfordnlpgerman.models.dao;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "newsarticles")
@Builder
@Where(clause = "deleted=0")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewsArticle {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String newsPaperName;
  private String title;
  private int publicationYear;
  private boolean deleted;
  private int relevance;

  @OneToMany(mappedBy = "newsArticle", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
  private List<Sentence> sentences = new ArrayList<>();

  @ManyToMany(mappedBy = "newsArticles", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
  private Set<LemmaType> lemmaTypes = new HashSet<>();
}
