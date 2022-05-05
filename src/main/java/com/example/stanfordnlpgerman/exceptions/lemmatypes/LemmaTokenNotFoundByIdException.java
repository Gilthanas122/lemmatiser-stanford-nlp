package com.example.stanfordnlpgerman.exceptions.lemmatypes;

public class LemmaTokenNotFoundByIdException extends RuntimeException {
  public LemmaTokenNotFoundByIdException() {
  }

  public LemmaTokenNotFoundByIdException(String message) {
    super(message);
  }
}
