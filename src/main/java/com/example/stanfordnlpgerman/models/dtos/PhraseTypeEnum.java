package com.example.stanfordnlpgerman.models.dtos;

public enum PhraseTypeEnum {

  NOUN("NOUN"),
  VERB("VERB"),
  PROPN("PROPN"),
  ADJ("ADJ"),
  DET("DET"),
  CCONJ("CCONJ"),
  ADP("ADP");

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
