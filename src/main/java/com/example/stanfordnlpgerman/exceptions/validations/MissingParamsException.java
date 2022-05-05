package com.example.stanfordnlpgerman.exceptions.validations;

public class MissingParamsException extends RuntimeException {
  public MissingParamsException() {
  }

  public MissingParamsException(String message) {
    super(message);
  }
}
