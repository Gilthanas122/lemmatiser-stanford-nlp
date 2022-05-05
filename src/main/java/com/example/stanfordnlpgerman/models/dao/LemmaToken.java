package com.example.stanfordnlpgerman.models.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "lemmatokens")
@Where(clause = "deleted=0")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LemmaToken implements Comparable<LemmaToken> {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public long id;
  @Column(columnDefinition = "varchar(50)")
  private String text;
  @Column(columnDefinition = "varchar(8)")
  private String phraseType;
  private boolean deleted;

  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
  private LemmaType lemmaType;

  @Override
  public int compareTo(LemmaToken lemmaToken) {
    return this.text.compareTo(lemmaToken.text);
  }
}
