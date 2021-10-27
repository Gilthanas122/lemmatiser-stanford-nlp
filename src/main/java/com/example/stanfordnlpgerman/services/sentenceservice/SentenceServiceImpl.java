package com.example.stanfordnlpgerman.services.sentenceservice;

import com.example.stanfordnlpgerman.models.dtos.sentence.LemmaOccurenceInSentencesDTO;
import com.example.stanfordnlpgerman.models.dtos.sentence.SentenceTextAndNewsPaperIdDTO;
import com.example.stanfordnlpgerman.repositories.SentenceRepository;
import com.example.stanfordnlpgerman.services.lemmatypeservice.LemmaTypeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SentenceServiceImpl implements SentenceService{
  private final SentenceRepository sentenceRepository;
  private final LemmaTypeService lemmaTypeService;

  public SentenceServiceImpl(SentenceRepository sentenceRepository, LemmaTypeService lemmaTypeService) {
    this.sentenceRepository = sentenceRepository;
    this.lemmaTypeService = lemmaTypeService;
  }

  @Override
  public List<SentenceTextAndNewsPaperIdDTO> getAllSentencesBelongingToLemmaType(long lemmaTypId) {
    return sentenceRepository.findByLemmaTypeId(lemmaTypId);
  }

  @Override
  public List<LemmaOccurenceInSentencesDTO> showWordsInContext(long lemmaTypeId) {
    List<Long> sentenceIdsContainingLemma = sentenceRepository.sentencesContainingLemma(lemmaTypeId);
    List<LemmaOccurenceInSentencesDTO> lemmasAndOccurences = lemmaTypeService.findLemmasAndOccurencesInSentences(sentenceIdsContainingLemma);
    return lemmasAndOccurences;
  }
}
