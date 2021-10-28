package com.example.stanfordnlpgerman.services.texttokenservice;

import com.example.stanfordnlpgerman.models.dao.TextToken;
import com.example.stanfordnlpgerman.models.dtos.lemmatype.InvalidLemmasDTO;
import com.example.stanfordnlpgerman.repositories.TextTokenRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

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

  @Override
  public Set<InvalidLemmasDTO> getInvalidLemmas() {
    Set<TextToken> textTokens = textTokenRepository.getTextTokensInvalid();
    Set<InvalidLemmasDTO> invalidLemmasDTOS = new HashSet<>();
    for (TextToken tt : textTokens) {
      invalidLemmasDTOS.add(InvalidLemmasDTO
              .builder()
              .textTokenId(tt.getId())
              .textTokenText(tt.getText())
              .build());
    }
    return invalidLemmasDTOS;
  }
}
