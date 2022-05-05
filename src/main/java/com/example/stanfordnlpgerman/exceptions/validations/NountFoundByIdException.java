package com.example.stanfordnlpgerman.exceptions.validations;

public class NountFoundByIdException extends RuntimeException {
  public NountFoundByIdException() {
  }

  public NountFoundByIdException(String message) {
    super(message);
  }

}
