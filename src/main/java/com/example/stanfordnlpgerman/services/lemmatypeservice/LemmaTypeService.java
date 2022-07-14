package com.example.stanfordnlpgerman.services.lemmatypeservice;

import com.example.stanfordnlpgerman.exceptions.lemmatypes.LemmaTokenNotFoundByIdException;
import com.example.stanfordnlpgerman.exceptions.validations.NountFoundByIdException;
import com.example.stanfordnlpgerman.models.dao.LemmaType;
import com.example.stanfordnlpgerman.models.dtos.lemmatype.ShowMostCommonLemmasDTO;
import com.example.stanfordnlpgerman.models.dtos.lemmatype.UpdateLemmaTypeRequest;
import com.example.stanfordnlpgerman.models.dtos.sentence.LemmaOccurenceInSentencesDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public interface LemmaTypeService {
  Set<LemmaType> findAllByText(String originalText);

  Set<ShowMostCommonLemmasDTO> findMostCommonLemmas(short pageNumber, boolean keyword);

  List<LemmaOccurenceInSentencesDTO> findLemmasAndOccurencesInSentences(List<Long> sentenceIdsContainingLemma, long lemmaTypeId);

  void addTextTokenToLemmaType(long textTokenId, UpdateLemmaTypeRequest updateLemmaTypeRequest) throws NountFoundByIdException;

  LemmaType findById(long lemmaTypeId) throws LemmaTokenNotFoundByIdException;
}
