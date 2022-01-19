package com.example.stanfordnlpgerman.services.sentenceservice;

import com.example.stanfordnlpgerman.exceptions.lemmatypes.LemmaTokenNotFoundByIdException;
import com.example.stanfordnlpgerman.models.dtos.sentence.AdjacentSentencesToInvalidDTO;
import com.example.stanfordnlpgerman.models.dtos.sentence.InvalidSentencesDTO;
import com.example.stanfordnlpgerman.models.dtos.sentence.LemmaOccurenceInSentencesDTO;
import com.example.stanfordnlpgerman.models.dtos.sentence.SentenceTextAndNewsPaperIdDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SentenceService {

  List<SentenceTextAndNewsPaperIdDTO> getAllSentencesBelongingToLemmaType(long lemmaTypId);

  List<LemmaOccurenceInSentencesDTO> showWordsInContext(long lemmaTypeId, int distance) throws LemmaTokenNotFoundByIdException;

  List<InvalidSentencesDTO> getInvalidSentences();

  AdjacentSentencesToInvalidDTO getAdjacentSentences(long id, String text);
}
