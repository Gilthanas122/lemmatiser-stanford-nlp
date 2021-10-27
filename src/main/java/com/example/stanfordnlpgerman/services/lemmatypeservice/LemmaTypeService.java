package com.example.stanfordnlpgerman.services.lemmatypeservice;

import com.example.stanfordnlpgerman.models.dao.LemmaType;
import com.example.stanfordnlpgerman.models.dtos.lemmatype.ShowMostCommonLemmasDTO;
import com.example.stanfordnlpgerman.models.dtos.sentence.LemmaOccurenceInSentencesDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public interface LemmaTypeService {
  Set<LemmaType> findByText(String originalText);

  List<ShowMostCommonLemmasDTO> findMostCommonLemmas(short pageNumber);

  List<LemmaOccurenceInSentencesDTO> findLemmasAndOccurencesInSentences(List<Long> sentenceIdsContainingLemma);
}
