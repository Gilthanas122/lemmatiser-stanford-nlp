package com.example.stanfordnlpgerman.services.lemmatokenservice;

import com.example.stanfordnlpgerman.models.dao.LemmaType;
import com.example.stanfordnlpgerman.repositories.LemmaTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LemmaTokenServiceImpl implements LemmaTokenService {
  private final LemmaTokenRepository lemmaTokenRepository;

  public LemmaTokenServiceImpl(LemmaTokenRepository lemmaTokenRepository) {
    this.lemmaTokenRepository = lemmaTokenRepository;
  }

}
