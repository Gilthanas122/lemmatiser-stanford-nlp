package com.example.stanfordnlpgerman.models.dao;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "sentences")
@Where(clause = "deleted=0")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Sentence implements Comparable<Sentence> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private long id;
  private String text;
  @Column(columnDefinition = "smallint")
  private short textPosition;
  private boolean deleted;
  private boolean invalid;

  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
  private NewsArticle newsArticle;

  @ManyToMany(mappedBy = "sentences", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
  private Set<LemmaType> lemmaTypes = new HashSet<>();

  @OneToMany(mappedBy = "sentence", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
  private List<TextToken> textTokens;

  @Override
  public int compareTo(Sentence o) {
    return Short.compare(this.getTextPosition(), o.getTextPosition());
  }
}
