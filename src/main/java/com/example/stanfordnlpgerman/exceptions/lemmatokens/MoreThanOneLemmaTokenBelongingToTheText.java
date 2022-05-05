package com.example.stanfordnlpgerman.exceptions.lemmatokens;

public class MoreThanOneLemmaTokenBelongingToTheText extends RuntimeException {
  public MoreThanOneLemmaTokenBelongingToTheText() {
  }

  public MoreThanOneLemmaTokenBelongingToTheText(String message) {
    super(message);
  }
}
