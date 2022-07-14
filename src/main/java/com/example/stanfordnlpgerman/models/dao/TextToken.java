package com.example.stanfordnlpgerman.models.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "texttokens")
@Where(clause = "deleted=0")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TextToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String text;
  @Column(columnDefinition = "varchar(8)")
  private String phraseType;
  @Column(columnDefinition = "smallint default 0")
  private short sentencePosition = 0;
  private boolean deleted;
  private boolean invalid;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
  @JsonIgnore
  private LemmaType lemmaType;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
  @JsonIgnore
  private Sentence sentence;
}
