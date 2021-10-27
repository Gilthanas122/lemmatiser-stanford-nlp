package com.example.stanfordnlpgerman.services.sentenceservice;

import com.example.stanfordnlpgerman.models.dtos.sentence.SentenceTextAndNewsPaperIdDTO;
import com.example.stanfordnlpgerman.repositories.SentenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SentenceServiceImpl implements SentenceService{
  private final SentenceRepository sentenceRepository;

  public SentenceServiceImpl(SentenceRepository sentenceRepository) {
    this.sentenceRepository = sentenceRepository;
  }

  @Override
  public List<SentenceTextAndNewsPaperIdDTO> getAllSentencesBelongingToLemmaType(long lemmaTypId) {
    return sentenceRepository.findByLemmaTypeId(lemmaTypId);
  }
}
