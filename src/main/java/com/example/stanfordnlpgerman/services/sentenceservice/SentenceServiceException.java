package com.example.stanfordnlpgerman.services.sentenceservice;

public class SentenceServiceException extends RuntimeException{
    public SentenceServiceException(String message) {
        super(message);
    }

    public SentenceServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
