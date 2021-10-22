package com.example.stanfordnlpgerman.exceptions.lemmatokens;

public class LemmaTokenNotFoundByTextException extends Exception {
  public LemmaTokenNotFoundByTextException() {
  }

  public LemmaTokenNotFoundByTextException(String message) {
    super(message);
  }
}
