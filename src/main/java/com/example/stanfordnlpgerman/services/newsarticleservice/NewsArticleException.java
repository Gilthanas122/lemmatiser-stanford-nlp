package com.example.stanfordnlpgerman.services.newsarticleservice;

public class NewsArticleException extends RuntimeException{
  public NewsArticleException(String message) {
    super(message);
  }

  public NewsArticleException(String message, Throwable cause) {
    super(message, cause);
  }
}
