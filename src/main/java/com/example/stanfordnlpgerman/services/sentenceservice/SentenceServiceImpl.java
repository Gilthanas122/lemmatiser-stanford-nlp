package com.example.stanfordnlpgerman.services.sentenceservice;

import com.example.stanfordnlpgerman.repositories.SentenceRepository;
import org.springframework.stereotype.Service;

@Service
public class SentenceServiceImpl implements SentenceService{
  private final SentenceRepository sentenceRepository;

  public SentenceServiceImpl(SentenceRepository sentenceRepository) {
    this.sentenceRepository = sentenceRepository;
  }
}
