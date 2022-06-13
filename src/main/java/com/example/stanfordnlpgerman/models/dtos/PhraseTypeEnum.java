package com.example.stanfordnlpgerman.models.dtos;

public enum PhraseTypeEnum {
  ADJ("ADJ"),
  ADV("ADV"),
  NOUN("NOUN"),
  VERB("VERB"),
  PROPN("PROPN"),
  INTJ("INTJ"),
  ADP("ADP"),
  AUX("AUX"),
  CCONJ("CCONJ"),
  DET("DET"),
  NUM("NUM"),
  PART("PART"),
  PRON("PRON"),
  SCONJ("SCONJ"),
  PUNCT("PUNCT"),
  SYM("SYM"),
  X("OTHER");

  private final String name;

  PhraseTypeEnum(String s) {
    name = s;
  }

  public boolean equalsName(String otherName) {
    return name.equals(otherName);
  }

  public String toString() {
    return this.name;
  }
}
