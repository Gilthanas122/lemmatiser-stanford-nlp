package com.example.stanfordnlpgerman.exceptions.lemmatypes;

public class LemmaTokenNotFoundByIdException extends Exception {
  public LemmaTokenNotFoundByIdException() {
  }

  public LemmaTokenNotFoundByIdException(String message) {
    super(message);
  }
}
