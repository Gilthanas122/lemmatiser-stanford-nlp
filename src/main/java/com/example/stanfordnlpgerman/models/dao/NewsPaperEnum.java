package com.example.stanfordnlpgerman.models.dao;

public enum NewsPaperEnum {
  NEUE_ZEITUNG("Neue Zeitung"),
  PESTHER_LLOYD("Pesther Lloyd");

  private final String name;

  NewsPaperEnum(String s) {
    name = s;
  }

  public boolean equalsName(String otherName) {
    // (otherName == null) check is not needed because name.equals(null) returns false
    return name.equals(otherName);
  }

  public String toString() {
    return this.name;
  }
}
