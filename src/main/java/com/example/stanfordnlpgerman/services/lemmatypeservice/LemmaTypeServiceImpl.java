package com.example.stanfordnlpgerman.services.lemmatypeservice;

import com.example.stanfordnlpgerman.models.dao.LemmaType;
import com.example.stanfordnlpgerman.repositories.LemmaTypeRepository;
import com.example.stanfordnlpgerman.services.lemmatokenservice.LemmaTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    Set<LemmaType> lemmaTypes = lemmaTypeRepository.findAllByText(originalText);
    return lemmaTypes;
  }
}
