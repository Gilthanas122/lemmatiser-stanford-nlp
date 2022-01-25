package com.example.stanfordnlpgerman.models.dtos;

public enum NewsPaperEnum {
  NEUE_ZEITUNG("Neue Zeitung"),
  PESTHER_LLOYD("Pesther Lloyd");

  private final String name;

  NewsPaperEnum(String s) {
    name = s;
  }

  public boolean equalsName(String otherName) {
    return name.equals(otherName);
  }

  public String toString() {
    return this.name;
  }
}
