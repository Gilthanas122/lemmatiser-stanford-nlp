package com.example.stanfordnlpgerman.exceptions.lemmatokens;

public class MoreThanOneLemmaTokenBelongingToTheText extends Exception {
  public MoreThanOneLemmaTokenBelongingToTheText() {
  }

  public MoreThanOneLemmaTokenBelongingToTheText(String message) {
    super(message);
  }
}
