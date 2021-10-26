package com.example.stanfordnlpgerman.services.texttokenservice;

import com.example.stanfordnlpgerman.models.dao.TextToken;
import com.example.stanfordnlpgerman.repositories.TextTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class TextTokenServiceImpl implements TextTokenService {
  private final TextTokenRepository textTokenRepository;

  public TextTokenServiceImpl(TextTokenRepository textTokenRepository) {
    this.textTokenRepository = textTokenRepository;
  }


  @Override
  public void saveTextTokenWithoutLemmaType(TextToken textToken) {
    textTokenRepository.save(textToken);
  }
}
