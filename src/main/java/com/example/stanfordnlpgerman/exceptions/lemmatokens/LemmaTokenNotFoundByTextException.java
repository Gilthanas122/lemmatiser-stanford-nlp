package com.example.stanfordnlpgerman.exceptions.lemmatokens;

public class LemmaTokenNotFoundByTextException extends RuntimeException {
  public LemmaTokenNotFoundByTextException() {
  }

  public LemmaTokenNotFoundByTextException(String message) {
    super(message);
  }
}
