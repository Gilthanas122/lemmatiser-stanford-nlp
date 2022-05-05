package com.example.stanfordnlpgerman.models.dao;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sentences")
@Where(clause = "deleted=0")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
  private List<LemmaType> lemmaTypes = new ArrayList<>();

  @OneToMany(mappedBy = "sentence", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
  private List<TextToken> textTokens;

  @Override
  public int compareTo(Sentence o) {
    return Short.compare(this.getTextPosition(), o.getTextPosition());
  }
}
