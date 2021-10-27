package com.example.stanfordnlpgerman.services.sentenceservice;

import com.example.stanfordnlpgerman.models.dtos.sentence.LemmaOccurenceInSentencesDTO;
import com.example.stanfordnlpgerman.models.dtos.sentence.SentenceTextAndNewsPaperIdDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface SentenceService {

  List<SentenceTextAndNewsPaperIdDTO> getAllSentencesBelongingToLemmaType(long lemmaTypId);

  List<LemmaOccurenceInSentencesDTO> showWordsInContext(long lemmaTypeId);

}
