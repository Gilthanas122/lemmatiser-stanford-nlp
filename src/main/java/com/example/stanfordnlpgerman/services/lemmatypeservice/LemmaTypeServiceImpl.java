package com.example.stanfordnlpgerman.services.lemmatypeservice;

import com.example.stanfordnlpgerman.models.dao.LemmaType;
import com.example.stanfordnlpgerman.models.dtos.lemmatype.ShowMostCommonLemmasDTO;
import com.example.stanfordnlpgerman.models.dtos.sentence.LemmaOccurenceInSentencesDTO;
import com.example.stanfordnlpgerman.repositories.LemmaTypeRepository;
import com.example.stanfordnlpgerman.services.lemmatokenservice.LemmaTokenService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class LemmaTypeServiceImpl implements LemmaTypeService {
  private final LemmaTypeRepository lemmaTypeRepository;
  private final LemmaTokenService lemmaTokenService;

  public LemmaTypeServiceImpl(LemmaTypeRepository lemmaTypeRepository, LemmaTokenService lemmaTokenService) {
    this.lemmaTypeRepository = lemmaTypeRepository;
    this.lemmaTokenService = lemmaTokenService;
  }

  @Override
  @Transactional
  public Set<LemmaType> findByText(String originalText) {
    return lemmaTypeRepository.findAllByText(originalText);
  }

  @Override
  public List<ShowMostCommonLemmasDTO> findMostCommonLemmas(short pageNumber) {
    return lemmaTypeRepository.findMostCommonLemmasInNewsArticles(PageRequest.of(pageNumber, 25, Sort.by("textTokens.size")));
  }

  @Override
  public List<LemmaOccurenceInSentencesDTO> findLemmasAndOccurencesInSentences(List<Long> sentenceIdsContainingLemma) {
    return lemmaTypeRepository.findLemmaTypeOccurencesInSentences(sentenceIdsContainingLemma);
  }


}
