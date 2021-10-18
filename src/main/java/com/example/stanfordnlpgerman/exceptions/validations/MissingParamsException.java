package com.example.stanfordnlpgerman.exceptions.validations;

public class MissingParamsException extends Exception{
  public MissingParamsException() {
  }

  public MissingParamsException(String message) {
    super(message);
  }
}
