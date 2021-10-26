package com.example.stanfordnlpgerman.services.sentenceservice;

import com.example.stanfordnlpgerman.models.dtos.sentence.SentenceTextAndNewsPaperIdDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SentenceService {

  List<SentenceTextAndNewsPaperIdDTO> getAllSentencesBelongingToLemmaType(long lemmaTypId);
}
