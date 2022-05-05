package com.example.stanfordnlpgerman.services.lemmatypeservice;

public class LemmaTypeException extends RuntimeException{
  public LemmaTypeException(String message) {
    super(message);
  }

  public LemmaTypeException(String message, Throwable cause) {
    super(message, cause);
  }
}
